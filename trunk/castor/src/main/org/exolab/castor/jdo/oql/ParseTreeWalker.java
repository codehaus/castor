/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.jdo.oql;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.exolab.castor.jdo.DbMetaInfo;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.engine.JDOClassDescriptor;
import org.exolab.castor.jdo.engine.JDOFieldDescriptor;
import org.exolab.castor.jdo.engine.SQLEngine;
import org.exolab.castor.mapping.loader.Types;
import org.exolab.castor.persist.LockEngine;
import org.exolab.castor.persist.spi.QueryExpression;

/**
 * A class which walks the parse tree created by the parser to check for errors
 * and translate to SQL.
 *
 * @author <a href="mailto:nissim@nksystems.com">Nissim Karpenstein</a>
 * @version $Revision$ $Date$
 */
public class ParseTreeWalker implements TokenTypes
{

  private LockEngine _dbEngine;

  private ParseTreeNode _parseTree;

  private String _projectionName;
  private String _projectionAlias;
  private int _projectionType;

  private String _fromClassName;
  private String _fromClassAlias;

  private ClassLoader _classLoader;
  private Class _objClass;
  private QueryExpression _queryExpr;
  private DbMetaInfo _dbInfo;

  private Hashtable _paramInfo;
  private Hashtable _fieldInfo;
  private Hashtable _pathInfo;

  private Hashtable _allPaths;

  private SQLEngine _engine;
  private JDOClassDescriptor _clsDesc;

  //projection types
  public static final int AGGREGATE = 1;
  public static final int FUNCTION = 2;
  public static final int PARENT_OBJECT = 3;
  public static final int DEPENDANT_OBJECT = 4;
  public static final int DEPENDANT_OBJECT_VALUE = 5;
  public static final int DEPENDANT_VALUE = 6;

  public static final int MAX_TABLE_LENGTH = 30;

  /**
   * Creates a new parse tree walker.  Which checks the tree for errors, and
   * generates a QueryExpression containing the SQL translation.
   *
   * @param dbEngine The Persistence Engine
   * @param parseTree The parse tree to walk
   * @param classLoader A ClassLoader instance to load classes.
   * @throws QueryException Thrown by checkErrors.
   */
  public ParseTreeWalker(LockEngine dbEngine, ParseTreeNode parseTree, ClassLoader classLoader, DbMetaInfo dbInfo)
      throws QueryException
  {
    _dbEngine = dbEngine;
    _parseTree = parseTree;
    _classLoader = classLoader;
    _dbInfo = dbInfo;

    _paramInfo = new Hashtable();
    _fieldInfo = new Hashtable();
    _pathInfo = new Hashtable();

    _allPaths = new Hashtable();

    if ( ! _parseTree.isRoot() )
      throw (new QueryException( "ParseTreeWalker must be created with the root node of the parse tree."));

    //_log.debug (ParseTest.treeToString(_parseTree, ParseTest.NODE_TYPES));
    //_log.debug (ParseTest.treeToString(_parseTree, ParseTest.NODE_VALUES));


    checkErrors();
    createQueryExpression();
  }

  /**
   * Accessor method for _objClass.
   *
   * @return The _objClass member.
   */
  public Class getObjClass() {
    return _objClass;
  }

  /**
   * Accessor method for _projectionType.
   *
   * @return The _projectionType member.
   */
  public int getProjectionType() {
    return _projectionType;
  }

   /**
   * Accessor method for private _queryExpr member.
   *
   * @return private _queryExpr member
   */
  public QueryExpression getQueryExpression() {
    return _queryExpr;
  }

  /**
   * Accessor method for _paramInfo.
   *
   * @return The _paramInfo member.
   */
  public Hashtable getParamInfo() {
    return _paramInfo;
  }

  /**
   * Accessor method for _clsDesc.
   *
   * @return The _clsDesc member.
   */
  public JDOClassDescriptor getClassDescriptor() {
    return _clsDesc;
  }

  /**
   * Method to get path info for the selected object.  This is the path which
   * will be used by the QueryResults to follow the path if the object
   * selected is a DEPENDANT_OBJECT or DEPENDANT_OBJECT_VALUE.  Any other
   * projectionTypes do not need this, so null will be returned.
   * @return Path info for the selected element, null otherwise. 
   */
  public Vector getPathInfo() {
    switch ( _projectionType ) {
      case DEPENDANT_OBJECT:
      case DEPENDANT_OBJECT_VALUE:
        ParseTreeNode projectionNode;
        if ( _parseTree.getChild(0).getToken().getTokenType() == KEYWORD_DISTINCT )
          projectionNode = _parseTree.getChild(1);
        else
          projectionNode = _parseTree.getChild(0);

        if ( projectionNode.getToken().getTokenType() == KEYWORD_AS )
          projectionNode = projectionNode.getChild(0);

        return ( (Vector) _pathInfo.get( projectionNode ) );

      default:
        return null;
    }
  }

  /**
   * Traverses the tree checking for errors.
   *
   * @throws QueryException if there is an error.
   */
  private void checkErrors() throws QueryException {

    for (Enumeration e = _parseTree.children(); e.hasMoreElements(); ) {
      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();
      if ( curChild.getToken().getTokenType() == KEYWORD_FROM ) {
        checkFromPart( curChild.getChild(0) );
        break;
      }
    }

    if ( _parseTree.getChild(0).getToken().getTokenType() == KEYWORD_DISTINCT )
      checkSelectPart( _parseTree.getChild(1) );
    else
      checkSelectPart( _parseTree.getChild(0) );

    for ( int curChild = 2; curChild <= _parseTree.getChildCount() - 1; curChild++ ) {
      int tokenType = _parseTree.getChild(curChild).getToken().getTokenType();
      switch ( tokenType ) {
        case KEYWORD_WHERE:
          checkWhereClause( _parseTree.getChild(curChild) );
          break;
        case KEYWORD_ORDER:
          checkOrderClause( _parseTree.getChild(curChild) );
          break;
        case KEYWORD_LIMIT:
          checkLimitClause( _parseTree.getChild(curChild) );
          break;
        case KEYWORD_OFFSET:
            checkOffsetClause( _parseTree.getChild(curChild) );
            break;
      }
    }
  }

  /**
   * Checks the from parts of the query.
   *
   * @param fromPart is the ParseTreeNode containing the from part of the
   *      queryTree.
   * @throws QueryException if there is an error.
   */
  private void checkFromPart( ParseTreeNode fromPart )
        throws QueryException {

    //get the class name from the from clause
    if (fromPart.getToken().getTokenType() ==  KEYWORD_AS) {
      ParseTreeNode classNameNode = fromPart.getChild(0);
      if ( classNameNode.getToken().getTokenType() == DOT ) {
        StringBuffer sb = new StringBuffer();

        for (Enumeration e = classNameNode.children(); e.hasMoreElements(); ) {
          ParseTreeNode theChild = (ParseTreeNode) e.nextElement();
          sb.append( theChild.getToken().getTokenValue() ).append(".");
        }

        sb.setLength( sb.length() - 1 );
        _fromClassName = sb.toString();
      }
      else
        _fromClassName = classNameNode.getToken().getTokenValue();

      _fromClassAlias = fromPart.getChild(1).getToken().getTokenValue();
    }
    else {
      if ( fromPart.getToken().getTokenType() == DOT ) {
        StringBuffer sb = new StringBuffer();

        for (Enumeration e = fromPart.children(); e.hasMoreElements(); ) {
          ParseTreeNode theChild = (ParseTreeNode) e.nextElement();
          sb.append( theChild.getToken().getTokenValue() ).append(".");
        }

        _fromClassName = sb.deleteCharAt( sb.length() - 1 ).toString();
      }
      else
        _fromClassName = fromPart.getToken().getTokenValue();

      _fromClassAlias = _fromClassName;
    }

    try {
      if ( _classLoader == null )
        _objClass = Class.forName( _fromClassName );
      else
        _objClass = _classLoader.loadClass( _fromClassName );
    }
    catch ( ClassNotFoundException except ) {
      throw new QueryException( "Could not find class " + _fromClassName );
    }
    _engine = (SQLEngine) _dbEngine.getPersistence( _objClass );
    if ( _engine == null )
      throw new QueryException( "Could not find mapping for class " + _fromClassName );
    _clsDesc = _engine.getDescriptor();
    // This should never happen
    if ( _clsDesc == null )
      throw new QueryException( "Could not get a descriptor for class " + _fromClassName );

  }

  /**
   * Checks the select part of the query.
   *
   * @param selectPart is the ParseTreeNode containing the select part of the
   *      queryTree.
   * @throws QueryException if there is an error.
   */
  private void checkSelectPart( ParseTreeNode selectPart )
        throws QueryException {

    if (selectPart.getToken().getTokenType() ==  KEYWORD_AS) {
      checkProjection( selectPart.getChild(0), true, false );
      _projectionAlias = selectPart.getChild(1).getToken().getTokenValue();
    }
    else {
      checkProjection( selectPart, true, false );
      _projectionAlias = "";
    }

    //if ( ! _projectionName.equals(_fromClassAlias) )
    //  throw new QueryException( "Object name not the same in SELECT and FROM - select: " + _projectionName + ", from: " + _fromClassAlias );
  }

  /**
   * Search for the field in the given class descriptor and descriptors of the superclasses,
   * return null if not found.
   * @param fieldName The field name.
   * @param clsDesc A JDO class descriptor.
   * @return JDOFieldDescriptor for the specified field, null if not found.
   */
  private JDOFieldDescriptor getFieldDesc( String fieldName, JDOClassDescriptor clsDesc ) {
    JDOFieldDescriptor field;

    for ( JDOClassDescriptor cd = clsDesc; cd != null; cd = (JDOClassDescriptor) cd.getExtends() ) {
        field = cd.getField(fieldName);
        if ( field != null )
            return field;
    }
    return null;
  }

  /**
   * Search for the field in the given class descriptor and descriptors of the superclasses,
   * return null if not found.
   * Returns new Object[2] {fieldDesc, classDesc}, where classDesc is a descriptor of the class where
   * the field was found.
   * Also adds inner joins to the QueryExpression.
   * @param path The path info vector to build the alias with
   * @param tableIndex Field index in the path info
   */
  private Object[] getFieldAndClassDesc(String fieldName, JDOClassDescriptor clsDesc, QueryExpression expr,
                                        Vector path, int tableIndex) {
    JDOFieldDescriptor field = null;
    JDOClassDescriptor cd = clsDesc;
    JDOFieldDescriptor tempField = null;
    JDOClassDescriptor tempCd = clsDesc;
    Object[] retVal;

    while (tempCd != null) {
        tempField = tempCd.getField(fieldName);
        if (tempField != null) {
            field = tempField;
            cd = tempCd;
        }
        tempCd = (JDOClassDescriptor) tempCd.getExtends();
    }
 
    if (field == null) {
        return null;
    }

    // prepare the return value
    retVal = new Object[] {field, cd};
    if (cd != clsDesc) {
        // now add inner join for "extends"
        String tableAlias1 = clsDesc.getTableName();
        String tableAlias2 = cd.getTableName();
        if (tableIndex > 0) {
            tableAlias1 = buildTableAlias(tableAlias1, path, tableIndex);
            tableAlias2 = buildTableAlias(tableAlias2, path, tableIndex);
        }
        expr.addTable(cd.getTableName(), tableAlias2);
        expr.addInnerJoin(clsDesc.getTableName(), clsDesc.getIdentityColumnNames(), tableAlias1,
                          cd.getTableName(), cd.getIdentityColumnNames(), tableAlias2);
    }
    return retVal;
  }

  /**
   * Checks a projection (one of the items selected).  Determines the
   * _projectionType.
   *
   * @param projection The ParseTreeNode containing the projection.
   * @param topLevel pass true when calling on the top level of projection
   *    in the parse tree.  False when recursing
   * @param onlySimple pass true if you want to throw errors if the
   *    object passed as the ParseTreeNode is not a simple type (use this
   *    when recursing on the arguments passed to Aggregate and SQL functions).
   * @throws QueryException if there is an error.
   * @return a JDOFieldDescriptor representation of the field, if it represents a field.
   */
  private JDOFieldDescriptor checkProjection( ParseTreeNode projection,
                                boolean topLevel,
                                boolean onlySimple )
          throws QueryException {
    JDOFieldDescriptor field = null;

    if ( projection.getChildCount() == 0 ) {
      if ( topLevel ) {
        _projectionType = PARENT_OBJECT;
        _projectionName = projection.getToken().getTokenValue();
       if ( ! _projectionName.equals(_fromClassAlias) )
          throw new QueryException( "Object name not the same in SELECT and FROM - select: " + _projectionName + ", from: " + _fromClassAlias );
      }
      else
        if ( onlySimple )
          throw new QueryException( "Only primitive values are allowed to be passed as parameters to Aggregate and SQL functions." );
        else
          return null;
    }
    else {
      int tokenType = projection.getToken().getTokenType();
      switch( tokenType ) {
        case IDENTIFIER:
          //a SQL function call -- check the arguments
          _projectionType = FUNCTION;
          for (Enumeration e = projection.getChild(0).children();
               e.hasMoreElements(); )
            checkProjection( (ParseTreeNode) e.nextElement(), false, true );
          break;

        case DOT:
          //a path expression -- check if it is valid, and create a paramInfo
          Enumeration e = projection.children();
          ParseTreeNode curNode = null;
          String curName = null;
          StringBuffer projectionName = new StringBuffer();
          Vector projectionInfo = new Vector();

          //check that the first word before the dot is our class
          if ( e.hasMoreElements() ) {
            curNode = (ParseTreeNode) e.nextElement();
            curName = curNode.getToken().getTokenValue();
            if ( ( ! curName.equals(_projectionName) ) &&
                 ( ! curName.equals(_projectionAlias) ) &&
                 ( ! curName.equals(_fromClassName) ) &&
                 ( ! curName.equals(_fromClassAlias) ) ) {
              // reset the enumeration
              e = projection.children();
              curName = _fromClassAlias;
            }
            projectionName.append(curName);
            projectionInfo.addElement(curName);
          }

          //use the ClassDescriptor to check that the rest of the path is valid.
          JDOClassDescriptor curClassDesc = _clsDesc;
          JDOFieldDescriptor curField = null;
          int count = 0;
          String curToken;
          while ( e.hasMoreElements() ) {
            // there may be nested attribute name
            curField = null;
            curName = null;
            while ( curField == null && e.hasMoreElements() ) {
              curNode = (ParseTreeNode) e.nextElement();
              curToken = curNode.getToken().getTokenValue();
              if ( curName == null ) {
                curName = curToken;
              } else {
                curName = curName + "." + curToken;
              }
              curField = getFieldDesc(curName, curClassDesc);
            }
            if ( curField == null )
              throw new QueryException( "An unknown field was requested: " + curName + " (" + curClassDesc + ")" );
            projectionName.append(".").append(curName);
            projectionInfo.addElement(curName);
            curClassDesc = (JDOClassDescriptor) curField.getClassDescriptor();
            if ( curClassDesc == null && e.hasMoreElements() )
                throw new QueryException( "An non-reference field was requested: " + curName + " (" + curClassDesc + ")" );
            count++;
          }
          field = curField;

          _pathInfo.put( projection, projectionInfo );
          _fieldInfo.put( projection, curField );

          Class theClass = curField.getFieldType();
          // is it actually a Java primitive, or String,
          // or a subclass of Number
          boolean isSimple = Types.isSimpleType(theClass);

          if ( topLevel ) {
            _projectionName = projectionName.toString();
            if ( isSimple )
              if ( count > 1 || field.getContainingClassDescriptor() != _clsDesc)
                _projectionType = DEPENDANT_OBJECT_VALUE;
              else
                _projectionType = DEPENDANT_VALUE;
            else
              _projectionType = DEPENDANT_OBJECT;
          }
          else
            if ( ! isSimple && onlySimple )
              throw new QueryException( "Only primitive values are allowed to be passed as parameters to Aggregate and SQL functions." );
          break;

        case KEYWORD_COUNT:
          //count a special case
          _projectionType = AGGREGATE;
          if ( projection.getChild(0).getToken().getTokenType() == TIMES )
            //count(*)
            break;
          else
            //can call count on object types -- recurse over child
            checkProjection( projection.getChild(0), false, false );
          break;

        case KEYWORD_SUM:
        case KEYWORD_MIN:
        case KEYWORD_MAX:
        case KEYWORD_AVG:
          //an aggregate -- recurse over the child
          _projectionType = AGGREGATE;
          checkProjection( projection.getChild(0), false, true );
          break;

        default:
          //we use function to describe sql expressions also, like
          // SELECT s.age - 5 FROM Student s
          _projectionType = FUNCTION;
          for (e = projection.children(); e.hasMoreElements(); )
            checkProjection( (ParseTreeNode) e.nextElement(), false, false );
      }
    }
    return field;
  }

  /**
   * Traverses the where clause sub-tree and checks for errors.  Creates a
   * Hashtables with FieldInfo for fields of selected objects which are
   * mentioned in the where clause (i.e. nodes with tokenType of IDENTIFIER
   * or (DOT, IDENTIFIER, IDENTIFIER)).  Als creates a Hashtable of paramInfo
   * with type information for query parameters (i.e. $(Class)1 or $1).
   *
   * @param whereClause WHERE-clause to traverse.
   * @throws QueryException if an error is detected.
   */
  private void checkWhereClause(ParseTreeNode whereClause)
        throws QueryException {

    Enumeration e;
    int tokenType = whereClause.getToken().getTokenType();

    switch (tokenType) {
      case DOT:
        checkProjection( whereClause, false, false );
        break;
      case IDENTIFIER:
        e = whereClause.children();
        if ( e.hasMoreElements() ) {
            if ( whereClause.getChild(0).getToken().getTokenType() == LPAREN ) {
                // A function.
                while ( e.hasMoreElements() ) {
                    checkWhereClause( (ParseTreeNode) e.nextElement() );
                }
            }
        } else {
            checkField(whereClause);
        }
        break;
      case DOLLAR:
        checkParameter(whereClause);
        break;
      case KEYWORD_IN:
        checkField(whereClause.getChild(0));
        checkInClauseRightSide(whereClause.getChild(1));

      default:
        for (e = whereClause.children(); e.hasMoreElements(); ) {
          checkWhereClause( (ParseTreeNode) e.nextElement() );
        }
    }
  }


  /**
   * Traverses the limit clause sub-tree and checks for errors. Creates
   * a Hashtable of paramInfo with type information for query parameters
   * (i.e. $1).
   * @param limitClause LIMIT-clause to traverse.
   * @throws QueryException if an error is detected.
   */
  private void checkLimitClause(ParseTreeNode limitClause)
        throws QueryException {

    int tokenType = limitClause.getToken().getTokenType();
    switch (tokenType) {
      case DOLLAR:
        checkParameter(limitClause);
        break;

      default:
        for (Enumeration e = limitClause.children(); e.hasMoreElements(); ) {
          checkLimitClause( (ParseTreeNode) e.nextElement() );
        }
    }
  }

  /**
   * Traverses the offset clause sub-tree and checks for errors. Creates
   * a Hashtable of paramInfo with type information for query parameters
   * (i.e. $1).
   * @param offsetClause OFFSET-clause to traverse.
   * @throws QueryException if an error is detected.
   */
  private void checkOffsetClause(ParseTreeNode offsetClause)
        throws QueryException {

    int tokenType = offsetClause.getToken().getTokenType();
    switch (tokenType) {
      case DOLLAR:
        checkParameter(offsetClause);
        break;

      default:
        for (Enumeration e = offsetClause.children(); e.hasMoreElements(); ) {
          checkLimitClause( (ParseTreeNode) e.nextElement() );
        }
    }
  }


  /**
   * Checks whether the field passed in is valid within this object.  Also
   * adds this field to a Hashtable.
   *
   * @param fieldTree A leaf node containing an identifier, or a tree with DOT
   *    root, and two IDENTIFIER children (for fields that look like
   *    Person.name or Person-&gt;name)
   * @return a JDOFieldDescriptor representation of this field.
   * @throws QueryException if the field does not exist.
   */
  private JDOFieldDescriptor checkField(ParseTreeNode fieldTree)
        throws QueryException {

    //see if we've checked this field before.
    JDOFieldDescriptor field = (JDOFieldDescriptor)_fieldInfo.get(fieldTree);
    if ( field != null )
      return field;

    if ( fieldTree.getToken().getTokenType() == DOT ) {
        field = checkProjection( fieldTree, false, false );
    } else {
        field = getFieldDesc( fieldTree.getToken().getTokenValue(), _clsDesc );
        if (field != null)
            _fieldInfo.put(fieldTree, field);
    }
    if (field == null)
        throw new QueryException( "The field " + fieldTree.getToken().getTokenValue() + " was not found." );
    return field;

  }

  /**
   * Checks a numbered parameter from an OQL Parse Tree.  Creates a {@link ParamInfo}
   * object which stores the user or system defined class for this parameter.
   * If there's a user defined type for this parameter it is compared to see if
   * it is castable from the system defined class.  If not, an exception is
   * thrown.  If a user defined type is specified for a numbered parameter which
   * has already been examined, and the user defined types don't match an
   * exception is thrown.
   *
   * @param paramTree the Tree node containing DOLLAR, with children user
   *    defined class (if available) and parameter number.
   * @throws QueryException if an invalid class is specified by the user.
   */
  private void checkParameter(ParseTreeNode paramTree) throws QueryException {
    //get the parameter number and user defined type
    Integer paramNumber;
    String userDefinedType = "";
    if (paramTree.getChildCount() == 1)
      paramNumber = Integer.decode(paramTree.getChild(0)
                                   .getToken().getTokenValue());
    else {
      paramNumber = Integer.decode(paramTree.getChild(1)
                                   .getToken().getTokenValue());
      userDefinedType = paramTree.getChild(0).getToken().getTokenValue();
    }

    //Get the system defined type
    String systemType = "";
    // Get the SQL type if known
    JDOFieldDescriptor desc = null;
    int operation = paramTree.getParent().getToken().getTokenType();
    switch (operation) {
      case PLUS: case MINUS: case TIMES:
      case DIVIDE: case KEYWORD_MOD: case KEYWORD_ABS:
      case KEYWORD_LIMIT: //Alex
        systemType = "java.lang.Number";
        break;
      case KEYWORD_OFFSET:
        systemType = "java.lang.Number";
        break;
      case KEYWORD_LIKE:  case CONCAT:
        systemType = "java.lang.String";
        break;
      case KEYWORD_AND: case KEYWORD_OR: case KEYWORD_NOT:
        systemType = "java.lang.Boolean";
        break;
      case EQUAL: case NOT_EQUAL: case GT:
      case GTE: case LT: case LTE: case KEYWORD_BETWEEN:
        systemType = getParamTypeForComparison(paramTree.getParent());
        desc = getJDOFieldDescriptor(paramTree.getParent());
        break;
      case KEYWORD_LIST:
        systemType = getParamTypeForList(paramTree.getParent());
        break;
    }

    //get the param info for this numbered param
    ParamInfo paramInfo = (ParamInfo) _paramInfo.get(paramNumber);
    if ( paramInfo == null ) {
        paramInfo = new ParamInfo(userDefinedType, systemType, desc, _classLoader);
        _paramInfo.put(paramNumber, paramInfo);
    }
    else
        paramInfo.check(userDefinedType, systemType);

  }

  private String getParamTypeForComparison(ParseTreeNode comparisonTree)
      throws QueryException
  {

    for (Enumeration e = comparisonTree.children(); e.hasMoreElements(); ) {

      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();
      int tokenType = curChild.getToken().getTokenType();

      if ((tokenType == DOT) || (tokenType == IDENTIFIER)) {
        JDOFieldDescriptor field = checkField(curChild);
        return field.getFieldType().getName();
      }
    }

    throw new QueryException( "Could not get type for comparison." );
  }

  /**
   * Determine type of the list expression from the first contained
   * constant. If there are only bind variables without user defined
   * type present in the list, default to String as type.
   *  
   * @param listTree non-empty parser tree of list expression
   * @return class name of the list type
   * @throws QueryException
   */
  private String getParamTypeForList(ParseTreeNode listTree) throws QueryException
  {
    for (Enumeration e = listTree.children(); e.hasMoreElements(); ) {
      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();
      int tokenType = curChild.getToken().getTokenType();

      switch(tokenType) {
        case STRING_LITERAL:	return "java.lang.String";
        case DOUBLE_LITERAL:	return "java.lang.Double";
        case LONG_LITERAL:		return "java.lang.Long";
        case BOOLEAN_LITERAL:	return "java.lang.Boolean";
        case CHAR_LITERAL:		return "java.lang.Character";
        case DATE_LITERAL:		return "java.util.Date";
        case TIME_LITERAL:
        case TIMESTAMP_LITERAL:	return "java.util.Time";

        case DOLLAR:	// look for bind parameters with user defined type
          if (curChild.getChildCount() == 2) {
            String userDefinedType = curChild.getChild(0).getToken().getTokenValue();
            try {
                return Types.typeFromName(_classLoader, userDefinedType).getName();
            } catch(ClassNotFoundException e1) {
                throw new QueryException("Could not find class " + userDefinedType);
            }
          }
      }
    }

    return "java.lang.String";	// default to String, if only simple bind variables are present in the list 
  }

  private JDOFieldDescriptor getJDOFieldDescriptor(ParseTreeNode comparisonTree)
      throws QueryException
  {

    for (Enumeration e = comparisonTree.children(); e.hasMoreElements(); ) {

      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();
      int tokenType = curChild.getToken().getTokenType();

      if ((tokenType == DOT) || (tokenType == IDENTIFIER)) {
        return checkField(curChild);
      }
    }

    return null;
  }

  /**
   * Checks the right side of an IN clause.  it must be a LIST, and
   * the children must all be literals.
   *
   * @param theList the ParseTreeNode containing the list which is the
   *    right side argument to IN.
   * @throws QueryException if theList is not a list, or the list
   *    contains non literals.
   */
  private void checkInClauseRightSide(ParseTreeNode theList)
  		throws QueryException
  {
    if ( theList.getToken().getTokenType() != KEYWORD_LIST )
      throw new QueryException( "The right side of the IN operator must be a LIST." );

    for ( Enumeration e = theList.children(); e.hasMoreElements(); ) {
      ParseTreeNode node = (ParseTreeNode) e.nextElement();
	  int tokenType = node.getToken().getTokenType();

      switch(tokenType) {
        case KEYWORD_NIL: case KEYWORD_UNDEFINED:
        case BOOLEAN_LITERAL: case LONG_LITERAL:
        case DOUBLE_LITERAL: case CHAR_LITERAL:
        case STRING_LITERAL: case DATE_LITERAL:
        case TIME_LITERAL: case TIMESTAMP_LITERAL:
	      break;

        case DOLLAR:	// bind variable parameter
          checkParameter(node);
          break;

        default:
          throw new QueryException( "The LIST can only contain literals, bind variables and the keywords 'nil' and 'undefined'." );
      }
    }
  }

  /**
   * Traverses the order by clause sub-tree and checks for errors.
   *
   * @param orderClause ORDER-BY clause to traverse.
   * @throws QueryException if an error is detected.
   */
  private void checkOrderClause(ParseTreeNode orderClause)
        throws QueryException {

    if ( orderClause.getToken().getTokenType() != KEYWORD_ORDER )
      throw new QueryException( "checkOrderClause was called on a subtree which is not an order clause.");

    ParseTreeNode prevChild = null;
    for (Enumeration e = orderClause.children(); e.hasMoreElements(); ) {
      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();

      int tokenType = curChild.getToken().getTokenType();
      switch (tokenType) {
      case KEYWORD_ASC:
      case KEYWORD_DESC:
          // iterate on child
          curChild = curChild.getChild(0);
          tokenType = curChild.getToken().getTokenType();
      }
      switch (tokenType) {
      case DOT:
          checkProjection( curChild, false, false );
          break;
      case IDENTIFIER:
          if ( curChild.children().hasMoreElements() ) {
              if ( curChild.getChild(0).getToken().getTokenType() == LPAREN ) {
                  // A function, skip to next element
                  Enumeration arguments = curChild.getChild(0).children();
                  while(arguments.hasMoreElements()) {
                      ParseTreeNode nn = (ParseTreeNode)arguments.nextElement();
                      checkWhereClause(nn);
                  }
              }
          } else {
                checkField(curChild);
          }
          break;
      case LPAREN:
          if ( ( prevChild == null ) || ( prevChild.getToken().getTokenType() != IDENTIFIER ) )
              throw new QueryException("Illegal use of left parenthesis in ORDER BY clause.");
          break;
      default:
          throw new QueryException( "Only identifiers, path expressions, and the keywords ASC and DESC are allowed in the ORDER BY clause." );
      }
      prevChild = curChild;
    }
  }


  /**
   * Generates the QueryExpression which is an SQL representation or the OQL
   * parse tree.
   * @throws SyntaxNotSupportedException If a specific OQL feature is not supported by a RDBMS.
   */
  private void createQueryExpression() 
  	throws SyntaxNotSupportedException 
  {

    //We use the SQLEngine buildfinder for any queries which require
    //us to load the entire object from the database.  Otherwise
    //we use the local addSelectFromJoins method.

    //System.out.println( _projectionType );

    switch ( _projectionType ) {
      case PARENT_OBJECT:
      case DEPENDANT_OBJECT:
      case DEPENDANT_OBJECT_VALUE:
        _queryExpr = _engine.getFinder();
        break;
      default:
        _queryExpr = _engine.getQueryExpression();
        addSelectFromJoins();
    }

    _queryExpr.setDbMetaInfo(_dbInfo);

    //check for DISTINCT
    if ( _parseTree.getChild(0).getToken().getTokenType() == KEYWORD_DISTINCT )
      _queryExpr.setDistinct(true);

    //process where clause and order clause
    for ( Enumeration e = _parseTree.children(); e.hasMoreElements(); ) {
      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();
      int tokenType = curChild.getToken().getTokenType();
      switch ( tokenType ) {
        case KEYWORD_WHERE:
          addWhereClause(curChild);
          break;
        case KEYWORD_ORDER:
          _queryExpr.addOrderClause( getOrderClause( curChild ) );
          break;
        case KEYWORD_LIMIT:
          addLimitClause(curChild);
          break;
        case KEYWORD_OFFSET:
            addOffsetClause(curChild);
            break;
      }
    }
  }

  /**
   * Adds the selected fields, and the necessary joins to the QueryExpression.
   * This method is used when the query is for dependant values, aggregates,
   * or SQL functions, where we're just gonna query the item directly.
   *
   */
  private void addSelectFromJoins() {

    ParseTreeNode selectPart = null;
    if ( _parseTree.getChild(0).getToken().getTokenType() == KEYWORD_DISTINCT )
      selectPart = _parseTree.getChild(1);
    else
      selectPart = _parseTree.getChild(0);

    _queryExpr.addTable( _clsDesc.getTableName() );
    _queryExpr.addSelect( getSQLExpr( selectPart ) );

  }


  /**
   * Builds the alias name for a table from the path info.
   *
   * @param tableName The name of the table to add to the select clause
   * @param path The path info vector to build the alias with
   * @param tableIndex Field index in the path info
   * @return Alias name for a given table.
   */
  public String buildTableAlias( String tableName, Vector path, int tableIndex )
  {
      /* FIXME This aliasing will cause problems if a (different) table
      with the same name as our alias already exists (or will be added
      at a later time) in the query. Catching this might become a bit
      tricky ... */

      String tableAlias = tableName;
      int index;
      Integer i;

      /* We don't just alias all tables here, but only those where some
       attribute is queried (not those where we query a whole object).
       The reasons for this are impossible for me to explain right now,
       as I am rather tired; I'll write some better explanation later -
       I promise! :-) */
      if( path != null && path.size() > 2 ) {
          Vector tablePath = new Vector( path.subList( 0, tableIndex + 1 ) );
          i = (Integer) _allPaths.get( tablePath );
          if ( i == null ) {
              index = _allPaths.size();
              _allPaths.put( tablePath, new Integer( index ) );
          } else {
              index = i.intValue();
          }
          // fix for oracle support. If we have a 30 character table name, adding
          // anything to it (_#) will cause it to be too long and will make oracle
          // barf. the length we are trying to evaluate is:
          // length of the table name + the length of the index number we are using
          // + the _ character.
          String stringIndex = String.valueOf(index);
          if(tableAlias.length() + stringIndex.length() + 1 > MAX_TABLE_LENGTH) {
              // use a substring of the original table name, rather than the table alias
              // because if we truncate the table alias it could become "ununique"
              // (by truncating the _# we add) by truncating the table name
              // beforehand we are guaranteed uniqueness as long as the index is
              // not reused.
              tableAlias = tableAlias.substring(MAX_TABLE_LENGTH - (stringIndex.length() + 1));
          }
          // If table name contains '.', it should be replaced, since such names aren't allowed for aliases
          tableAlias = tableAlias.replace('.', '_') + "_" + index;
      }
      //System.out.println( "TableAlias: " + tableAlias );
      return tableAlias;
  }

  /**
   * Adds joins to the queryExpr for path expressions in the OQL.
   *
   * @param path Path expression to be added to JOIN clause.
   */
  private void addJoinsForPathExpression( Vector path ) {
    if ( path == null )
      throw new IllegalStateException( "path = null !" );

    // the class for the join is even this class
    // or one of the base classes
    JDOClassDescriptor sourceClass = _clsDesc;

    for ( int i = 1; i < path.size() - 1; i++ ) {
        JDOFieldDescriptor fieldDesc = null;

        // Find the sourceclass and the fielsddescriptor
        // in the class hierachie
        Object[] fieldAndClass = getFieldAndClassDesc((String) path.elementAt(i), sourceClass,
                                                      _queryExpr, path, i - 1);
        if (fieldAndClass == null) {
            throw new IllegalStateException( "Field not found:" + path.elementAt(i));
        }
        fieldDesc = (JDOFieldDescriptor) fieldAndClass[0];
        sourceClass = (JDOClassDescriptor) fieldAndClass[1];

        JDOClassDescriptor clsDesc = (JDOClassDescriptor) fieldDesc.getClassDescriptor();
        if ( clsDesc != null /*&& clsDesc != sourceClass*/ ) {
            //we must add this table as a join
            if ( fieldDesc.getManyKey() == null ) {
                //a many -> one relationship
                JDOFieldDescriptor foreignKey = (JDOFieldDescriptor) clsDesc.getIdentity();
                String sourceTableAlias = sourceClass.getTableName();
                if ( i > 1 )
                    sourceTableAlias = buildTableAlias( sourceTableAlias, path, i - 1 );

                _queryExpr.addInnerJoin( sourceClass.getTableName(),
                                         fieldDesc.getSQLName(),
                                         sourceTableAlias,
                                         clsDesc.getTableName(),
                                         foreignKey.getSQLName(),
                                         buildTableAlias( clsDesc.getTableName(), path, i ) );
            } else if ( fieldDesc.getManyTable() == null ) {
                //a one -> many relationship
                JDOFieldDescriptor identity = (JDOFieldDescriptor) sourceClass.getIdentity();
                String sourceTableAlias = sourceClass.getTableName();
                if ( i > 1 )
                    sourceTableAlias = buildTableAlias( sourceTableAlias, path, i - 1 );

                _queryExpr.addInnerJoin( sourceClass.getTableName(),
                                         identity.getSQLName(),
                                         sourceTableAlias,
                                         clsDesc.getTableName(),
                                         fieldDesc.getManyKey(),
                                         buildTableAlias( clsDesc.getTableName(), path, i ) );
            } else {
                //a many -> many relationship
                JDOFieldDescriptor identity = (JDOFieldDescriptor) sourceClass.getIdentity();
                JDOFieldDescriptor foreignKey = (JDOFieldDescriptor) clsDesc.getIdentity();
                String manyTableAlias = fieldDesc.getManyTable();
                String sourceTableAlias = sourceClass.getTableName();
                if ( i > 1 ) {
                    manyTableAlias = buildTableAlias( manyTableAlias, path, i - 1 );
                    sourceTableAlias = buildTableAlias( sourceTableAlias, path, i - 1 );
      }

                _queryExpr.addInnerJoin( sourceClass.getTableName(),
                                         identity.getSQLName(),
                                         sourceTableAlias,
                                         fieldDesc.getManyTable(),
                                         fieldDesc.getManyKey(),
                                         manyTableAlias);

                _queryExpr.addInnerJoin( fieldDesc.getManyTable(),
                                         fieldDesc.getSQLName(),
                                         manyTableAlias,
                                         clsDesc.getTableName(),
                                         foreignKey.getSQLName(),
                                         buildTableAlias( clsDesc.getTableName(), path, i ) );
            }
            sourceClass = clsDesc;
        }
    }
  }


  /**
   * Adds a SQL version of an OQL where clause.
   *
   * @param whereClause the parse tree node with the where clause
   */
  private void addWhereClause(ParseTreeNode whereClause)
  {
    String sqlExpr = getSQLExpr(whereClause.getChild(0));

	_queryExpr.addWhereClause(sqlExpr);
  }

  /**
   * Adds a SQL version of an OQL limit clause.
   *
   * @param limitClause the parse tree node with the limit clause
   * @throws SyntaxNotSupportedException If the LIMIT clause is not supported by a RDBMS.
   */
  private void addLimitClause(ParseTreeNode limitClause) 
  	throws SyntaxNotSupportedException 
  {
    String sqlExpr = getSQLExpr(limitClause/*.getChild(0)*/);

	_queryExpr.addLimitClause(sqlExpr);
  }

  /**
   * Adds a SQL version of an OQL offset clause.
   * @param offsetClause The parse tree node with the offset clause
   * @throws SyntaxNotSupportedException If the LIMIT clause is not supported by a RDBMS.
   */
  private void addOffsetClause(ParseTreeNode offsetClause)
  	throws SyntaxNotSupportedException
  {
    String sqlExpr = getSQLExpr(offsetClause/*.getChild(0)*/);

	_queryExpr.addOffsetClause(sqlExpr);
  }

  /**
   * Returns a SQL version of an OQL expr.
   *
   * @param exprTree the parse tree node with the expr
   * @return The SQL translation of the expr.
   */
  private String getSQLExpr(ParseTreeNode exprTree) {

    StringBuffer sb = null;
    int tokenType =  exprTree.getToken().getTokenType();

    switch (tokenType) {

      //Parens passed through from where clause in SQL
      case LPAREN:
        return "( " + getSQLExpr( exprTree.getChild(0) ) + " )";

      //(possible) unary operators
      case PLUS: case MINUS: case KEYWORD_ABS: case KEYWORD_NOT:
        if ( exprTree.getChildCount() == 1 )
          return exprTree.getToken().getTokenValue() + " "
                 + getSQLExpr( exprTree.getChild(0) );
        else
          //this was binary PLUS or MINUS
          return getSQLExpr( exprTree.getChild(0) ) + " "
               + exprTree.getToken().getTokenValue() + " "
               + getSQLExpr( exprTree.getChild(1) );

      //binary operators
      case KEYWORD_AND: case KEYWORD_OR:
      case EQUAL: case NOT_EQUAL: case CONCAT:
      case GT: case GTE: case LT: case LTE:
      case TIMES: case DIVIDE: case KEYWORD_MOD:
      case KEYWORD_LIKE: case KEYWORD_IN:
        return getSQLExpr( exprTree.getChild(0) ) + " "
               + exprTree.getToken().getTokenValue() + " "
               + getSQLExpr( exprTree.getChild(1) );

      //tertiary BETWEEN operator
      case KEYWORD_BETWEEN:
        return getSQLExpr( exprTree.getChild(0) ) + " "
               + exprTree.getToken().getTokenValue() + " "
               + getSQLExpr( exprTree.getChild(1) ) + " AND "
               + getSQLExpr( exprTree.getChild(2) );

      //built in functions
      case KEYWORD_IS_DEFINED:
        return getSQLExpr( exprTree.getChild(0) ) + " IS NOT NULL ";
      case KEYWORD_IS_UNDEFINED:
        return getSQLExpr( exprTree.getChild(0) ) + " IS NULL ";
      case KEYWORD_COUNT:
        if ( exprTree.getChild(0).getToken().getTokenType() == TIMES )
          return " COUNT(*) ";
        else
          return " COUNT(" + getSQLExpr(exprTree.getChild(0)) +") ";
      case KEYWORD_SUM:
      case KEYWORD_MIN:
      case KEYWORD_MAX:
      case KEYWORD_AVG:
        return " " + exprTree.getToken().getTokenValue() + "(" +
               getSQLExpr(exprTree.getChild(0)) + ") ";


      //List creation
      case KEYWORD_LIST:
        sb = new StringBuffer("( ");

        for (Enumeration e = exprTree.children(); e.hasMoreElements(); )
          sb.append( getSQLExpr( (ParseTreeNode) e.nextElement() ) )
            .append(" , ");

        //replace final comma space with close paren.
        sb.replace(sb.length() - 2, sb.length() - 1, " )").append(" ");
        return sb.toString();

      //fields or SQL functions
      case IDENTIFIER: case DOT:
        if ( exprTree.getChildCount() > 0 &&
             exprTree.getChild(0/* was '1' thth */).getToken().getTokenType() == LPAREN ) {
            //An SQL function
            sb = new StringBuffer(exprTree.getToken().getTokenValue()).append("(");
            int paramCount = 0;
            Enumeration e = exprTree.children();
            e = ((ParseTreeNode) e.nextElement()).children(); // LPAREN's children
            for (; e.hasMoreElements(); ) {
                sb.append( getSQLExpr( (ParseTreeNode) e.nextElement() ) )
                    .append(" , ");
                paramCount++;
            }

            if ( paramCount > 0 ) {
                //replace final comma space with close paren.
                sb.replace(sb.length() - 2, sb.length() - 1, " )");//.append(" ");
            } else {
                //there were no parameters, so no commas.
                sb.append(") ");
            }

            //System.out.println( "Function found. '" + sb + "'" );
            return sb.toString();
        } else {

            //a field
            Vector path = (Vector) _pathInfo.get(exprTree);
            if ( tokenType == DOT ) {
                if ( path == null ) {
                    System.err.println( "exprTree=" + exprTree.toStringEx() + "\npathInfo = {" );
                    Enumeration enumeration = _pathInfo.keys();
                    ParseTreeNode n;
                    while ( enumeration.hasMoreElements() ) {
                        n = (ParseTreeNode)enumeration.nextElement();
                        System.err.println( "\t" + n.toStringEx() );
                    }
                    // Exception follows in addJoinsForPathExpression()
                }
                addJoinsForPathExpression( path );
            }

            JDOFieldDescriptor field = (JDOFieldDescriptor) _fieldInfo.get(exprTree);
            if ( field == null ) {
                throw new IllegalStateException( "fieldInfo for " + exprTree.toStringEx() + " not found" );
            }

            JDOClassDescriptor clsDesc = (JDOClassDescriptor) field.getContainingClassDescriptor();
            if ( clsDesc == null ) {
                throw new IllegalStateException( "ContainingClass of "+ field.toString()+" is null !" );
            }

            String clsTableAlias;
            if ( tokenType == DOT && path != null && path.size() > 2 ) {
                clsTableAlias = buildTableAlias( clsDesc.getTableName(), path, path.size() - 2 );
                JDOClassDescriptor srcDesc = _clsDesc;
                for ( int i = 1; i < path.size(); i++ ) {
                    Object[] fieldAndClass = getFieldAndClassDesc((String) path.elementAt(i), srcDesc,
                                                                  _queryExpr, path, i - 1);
                    if (fieldAndClass == null) {
                        throw new IllegalStateException( "Field not found: " + path.elementAt(i) + " class " + srcDesc.getJavaClass());
                    }
                    JDOFieldDescriptor fieldDesc = (JDOFieldDescriptor) fieldAndClass[0];
                    srcDesc = (JDOClassDescriptor) fieldDesc.getClassDescriptor();
                }
            } else {
                clsTableAlias = buildTableAlias( clsDesc.getTableName(), path, 9999 );
            }

            return _queryExpr.encodeColumn(clsTableAlias, field.getSQLName()[0]);
        }

      //parameters
      case DOLLAR:
        //return a question mark with the parameter number.  The calling function
        //will do a mapping
        return "?" + exprTree.getChild(exprTree.getChildCount() - 1)
                              .getToken().getTokenValue();

      //literals which need no modification
      case BOOLEAN_LITERAL: case LONG_LITERAL: case DOUBLE_LITERAL:
      case CHAR_LITERAL:
        return exprTree.getToken().getTokenValue();

      //String literals: change \" to ""
      case STRING_LITERAL:
        //char replace function should really be somewhere else
        //first change \" to "
        sb = new StringBuffer();
        String copy = new String(exprTree.getToken().getTokenValue());

        int pos = copy.indexOf("\\\"", 1);
        while ( pos != -1 ) {
          sb.append(copy.substring(0, pos)).append("\"\"");
          copy = copy.substring(pos + 2);
          pos = copy.indexOf("\\\"");
        }

        sb.append(copy);

        //Then change surrounding double quotes to single quotes, and change
        //' to ''

        copy = sb.deleteCharAt(0).toString();

        sb.setLength(0);
        sb.append("'");
        pos = copy.indexOf("'", 1);
        while ( pos != -1 ) {
          sb.append(copy.substring(0, pos)).append("''");
          copy = copy.substring(pos + 1);
          pos = copy.indexOf("'");
        }

        sb.append(copy);
        //replace final double quote with single quote
        sb.replace(sb.length() - 1, sb.length(), "'");


        return sb.toString();

      //Date, time and timestamp literals...strip off keyword (?is that all?)
      case DATE_LITERAL: case TIME_LITERAL:
        //date and time both 4 chars long.
        return exprTree.getToken().getTokenValue().substring(5);
      case TIMESTAMP_LITERAL:
        return exprTree.getToken().getTokenValue().substring(10);

      case KEYWORD_NIL:
      case KEYWORD_UNDEFINED:
        return " NULL ";
      case KEYWORD_LIMIT: //Proceed it with it's own getSQLExpr
      case KEYWORD_OFFSET:
        return getSQLExprForLimit(exprTree);
    }

    return "";
  }


  private String getSQLExprForLimit(ParseTreeNode limitClause) {

    StringBuffer sb = new StringBuffer();
    for (Enumeration e = limitClause.children(); e.hasMoreElements(); ) {
        ParseTreeNode exprTree = (ParseTreeNode) e.nextElement();
      int tokenType =  exprTree.getToken().getTokenType();
      switch( tokenType ) {
      //parameters
        case DOLLAR:
        //return a question mark with the parameter number.  The calling function
        //will do a mapping
          sb.append( "?" + exprTree.getChild(exprTree.getChildCount() - 1)
                                .getToken().getTokenValue());
          break;
        case COMMA:
          sb.append(" , ");
          break;
        case BOOLEAN_LITERAL:
        case LONG_LITERAL:
        case DOUBLE_LITERAL:
        case CHAR_LITERAL:
          return exprTree.getToken().getTokenValue();
      }
    }
    return sb.toString();
  }


  /**
   * Returns a SQL version of an OQL order by clause.
   *
   * @param orderClause the parse tree node with the order by clause
   * @return The SQL translation of the order by clause.
   */
  private String getOrderClause(ParseTreeNode orderClause) {

    StringBuffer sb = new StringBuffer();

    for (Enumeration e = orderClause.children(); e.hasMoreElements(); ) {
      sb.append(", ");
      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();
      int tokenType = curChild.getToken().getTokenType();
      switch ( tokenType ) {
        case KEYWORD_ASC:
          sb.append( getSQLExpr( curChild.getChild(0) ) ).append( " ASC " );
          break;
        case KEYWORD_DESC:
          sb.append( getSQLExpr( curChild.getChild(0) ) ).append( " DESC " );
          break;
        case DOT: case IDENTIFIER:
          sb.append( getSQLExpr( curChild ) ).append( " " );
          break;
      }
    }

    //remove the additional comma space at the beginning
    sb.deleteCharAt(0).deleteCharAt(0);

    return sb.toString();
  }


}
