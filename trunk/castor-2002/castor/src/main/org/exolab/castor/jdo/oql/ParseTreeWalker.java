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
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.jdo.oql;

import java.util.Hashtable;
import java.util.Enumeration;
import org.exolab.castor.persist.PersistenceEngine;
import org.exolab.castor.persist.spi.QueryExpression;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.engine.SQLEngine;
import org.exolab.castor.jdo.engine.JDOClassDescriptor;
import org.exolab.castor.jdo.engine.JDOFieldDescriptor;

/**
 * A class which walks the parse tree created by the parser to check for errors
 * and translate to SQL.
 *
 * @author <a href="mailto:nissim@nksystems.com">Nissim Karpenstein</a>
 * @version $Revision$ $Date$
 */
public class ParseTreeWalker implements TokenTypes
{

  private PersistenceEngine _dbEngine;
  
  private ParseTreeNode _parseTree;

  private String _projectionName;
  private String _projectionAlias;

  private String _fromClassName;
  private String _fromClassAlias;

  private Class _objClass;
  private QueryExpression _queryExpr;
  
  private Hashtable _paramInfo;
  private Hashtable _fieldInfo;
  
  private SQLEngine _engine;
  private JDOClassDescriptor _clsDesc;
    
  /**
   * Creates a new parse tree walker.  Which checks the tree for errors, and 
   * generates a QueryExpression containing the SQL translation.
   *
   * @param dbEngine The Persistence Engine
   * @param parseTree The parse tree to walk
   * @throws QueryException Thrown by checkErrors.
   */
  public ParseTreeWalker(PersistenceEngine dbEngine, ParseTreeNode parseTree) 
      throws QueryException 
  {
    _dbEngine = dbEngine;
    _parseTree = parseTree;

    _paramInfo = new Hashtable();
    _fieldInfo = new Hashtable();

    if ( ! _parseTree.isRoot() )
      throw (new QueryException( "ParseTreeWalker must be created with the root node of the parse tree."));

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
   * Traverses the tree checking for errors.  Records name and alias in 
   * SELECT part of query, records name and alias in FROM part.  Checks that
   * name in SELECT part matches name or alias in FROM part.
   *
   * @throws QueryException if there is an error.
   */
  private void checkErrors() throws QueryException {
    
    //get the projection Name and alias
    ParseTreeNode selectPart = _parseTree.getChild(0);
    if (selectPart.getToken().getTokenType() ==  KEYWORD_AS) {
      _projectionName = selectPart.getChild(0).getToken().getTokenValue();
      _projectionAlias = selectPart.getChild(1).getToken().getTokenValue();
    }
    else {
      _projectionName = selectPart.getToken().getTokenValue();
      _projectionAlias = _projectionName;
    }
           
    //get the class name from the from clause
    ParseTreeNode fromPart = _parseTree.getChild(1).getChild(0);
    if (fromPart.getToken().getTokenType() ==  KEYWORD_AS) {
      ParseTreeNode classNameNode = fromPart.getChild(0);
      if ( classNameNode.getToken().getTokenType() == DOT ) {
        StringBuffer sb = new StringBuffer();
        
        for (Enumeration e = classNameNode.children(); e.hasMoreElements(); ) {
          ParseTreeNode theChild = (ParseTreeNode) e.nextElement();
          sb.append( theChild.getToken().getTokenValue() ).append(".");
        }
          
        _fromClassName = sb.deleteCharAt( sb.length() - 1 ).toString();
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
    
    if ( ! _projectionName.equals(_fromClassAlias) )
      throw new QueryException( "Object name not the same in SELECT and FROM" );
      
    try {
      _objClass = Class.forName( _fromClassName );
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

    for ( int curChild = 2; curChild <= _parseTree.getChildCount() - 1; curChild++ ) {
      int tokenType = _parseTree.getChild(curChild).getToken().getTokenType();
      switch ( tokenType ) {
        case KEYWORD_WHERE:
          checkWhereClause( _parseTree.getChild(curChild) );
          break;
        case KEYWORD_ORDER:
          checkOrderClause( _parseTree.getChild(curChild) );
          break;
      }
    }
  }

  /**
   * Traverses the where clause sub-tree and checks for errors.  Creates a
   * Hashtables with FieldInfo for fields of selected objects which are 
   * mentioned in the where clause (i.e. nodes with tokenType of IDENTIFIER
   * or (DOT, IDENTIFIER, IDENTIFIER)).  Als creates a Hashtable of paramInfo
   * with type information for query parameters (i.e. $(Class)1 or $1).
   *
   * @throws QueryException if an error is detected.
   */
  private void checkWhereClause(ParseTreeNode whereClause) 
        throws QueryException {
    
    int tokenType = whereClause.getToken().getTokenType();
    switch (tokenType) {
      case DOT: case IDENTIFIER:
        checkField(whereClause);
        break;
      case DOLLAR:
        checkParameter(whereClause);
        break;
      case KEYWORD_IN:
        checkField(whereClause.getChild(0));
        checkInClauseRightSide(whereClause.getChild(1));
        
      default:
        for (Enumeration e = whereClause.children(); e.hasMoreElements(); ) {
          checkWhereClause( (ParseTreeNode) e.nextElement() );
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
  
    String fieldPrefix = _fromClassName;
    String fieldSuffix = fieldTree.getToken().getTokenValue();
    
    if ( fieldTree.getToken().getTokenType() == DOT ) {
      fieldPrefix = fieldTree.getChild(0).getToken().getTokenValue();
      fieldSuffix = fieldTree.getChild(1).getToken().getTokenValue();
    }
        
    if ( ( ! fieldPrefix.equals(_projectionName) ) &&
         ( ! fieldPrefix.equals(_projectionAlias) ) &&
         ( ! fieldPrefix.equals(_fromClassName) ) )
      throw new QueryException( "Invalid prefix identifier used in where clause: " + fieldPrefix);

    field = _clsDesc.getField(fieldSuffix);

    if (field == null)
      throw new QueryException( "The field " + fieldSuffix + " was not found." );
      
    _fieldInfo.put(fieldTree, field);

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
    int operation = paramTree.getParent().getToken().getTokenType();
    switch (operation) {
      case PLUS: case MINUS: case TIMES:
      case DIVIDE: case KEYWORD_MOD: case KEYWORD_ABS:
        systemType = "java.lang.Number";
        break;
      case KEYWORD_LIKE:  case CONCAT:
        systemType = "java.lang.String";
        break;
      case KEYWORD_AND: case KEYWORD_OR: case KEYWORD_NOT:
        systemType = "java.lang.Boolean";
        break;
      case EQUAL: case NOT_EQUAL: case GT:
      case GTE: case LT: case LTE:
        systemType = getTypeForComparison(paramTree.getParent());
    }
    
    //get the param info for this numbered param
    ParamInfo paramInfo = (ParamInfo) _paramInfo.get(paramNumber);
    if ( paramInfo == null ) {
      paramInfo = new ParamInfo(userDefinedType, systemType);
      _paramInfo.put(paramNumber, paramInfo);
    }
    else
      paramInfo.check(userDefinedType, systemType);

  }

  private String getTypeForComparison(ParseTreeNode comparisonTree) 
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
   * Checks the right side of an IN clause.  it must be a LIST, and
   * the children must all be literals.
   *
   * @param theList the ParseTreeNode containing the list which is the 
   *    right side argument to IN.
   * @throws QueryException if theList is not a list, or the list 
   *    contains non literals.
   */
  private void checkInClauseRightSide(ParseTreeNode theList) 
        throws QueryException {
    
    if ( theList.getToken().getTokenType() != KEYWORD_LIST )
      throw new QueryException( "The right side of the IN operator must be a LIST." );

    for ( Enumeration e = theList.children(); e.hasMoreElements(); ) {
      switch (( (ParseTreeNode) e.nextElement() ).getToken().getTokenType()) {
        case KEYWORD_NIL: case KEYWORD_UNDEFINED:
        case BOOLEAN_LITERAL: case LONG_LITERAL:
        case DOUBLE_LITERAL: case CHAR_LITERAL:
        case STRING_LITERAL: case DATE_LITERAL:
        case TIME_LITERAL: case TIMESTAMP_LITERAL:
          break;
        default:
          throw new QueryException( "The LIST can only contain literals and Keywords nil and undefined." );
      }
    }
    
  }

  /**
   * Traverses the order by clause sub-tree and checks for errors.  
   *
   * @throws QueryException if an error is detected.
   */
  private void checkOrderClause(ParseTreeNode orderClause) 
        throws QueryException {
    
    if ( orderClause.getToken().getTokenType() != KEYWORD_ORDER )
      throw new QueryException( "checkOrderClause was called on a subtree which is not an order clause.");
      
    for (Enumeration e = orderClause.children(); e.hasMoreElements(); ) {
      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();

      int tokenType = curChild.getToken().getTokenType();
      switch (tokenType) {
        case KEYWORD_ASC:
        case KEYWORD_DESC:
          checkField(curChild.getChild(0));
          break;
        case DOT: case IDENTIFIER:
          checkField(curChild);
          break;
        default:
          throw new QueryException( "Only identifiers, path expressions, and the keywords ASC and DESC are allowed in the ORDER BY clause." );
      }
    }
  }

  /**
   * Generates the QueryExpression which is an SQL representation or the OQL
   * parse tree.
   *
   */
  private void createQueryExpression() {

    _queryExpr = _engine.getFinder();

    //check for DISTINCT
    if ( _parseTree.getChild(0).getToken().getTokenType() == KEYWORD_DISTINCT )
      _queryExpr.setDistinct(true);
      
    //process where clause and order clause
    for ( Enumeration e = _parseTree.children(); e.hasMoreElements(); ) {
      ParseTreeNode curChild = (ParseTreeNode) e.nextElement();
      int tokenType = curChild.getToken().getTokenType();
      switch ( tokenType ) {
        case KEYWORD_WHERE:
          _queryExpr.addWhereClause( getWhereClause( curChild ) );    
          break;
        case KEYWORD_ORDER:
          _queryExpr.addOrderClause( getOrderClause( curChild ) );
      }
    }
  }
  
  /**
   * Returns a SQL version of an OQL where clause.
   *
   * @param whereClause the parse tree node with the where clause
   * @return The SQL translation of the where clause.
   */
  private String getWhereClause(ParseTreeNode whereClause) {
    String sqlExpr = getSQLExpr(whereClause.getChild(0));

    //Map numbered parameters
    StringBuffer sb = new StringBuffer();
    int pos = sqlExpr.indexOf("?");
    int SQLParamIndex = 1;
    while ( pos != -1 ) {
      int endPos = sqlExpr.indexOf(" ", pos);
      Integer paramNumber = null;
      if ( endPos != -1 )
        paramNumber = new Integer(sqlExpr.substring(pos + 1, endPos - 1));
      else
        paramNumber = new Integer(sqlExpr.substring(pos + 1));
      ParamInfo paramInfo = (ParamInfo) _paramInfo.get(paramNumber);
      paramInfo.mapToSQLParam( SQLParamIndex++ );
      sb.append(sqlExpr.substring(0, pos + 1)).append(" ");
      if (endPos != -1 )
        sqlExpr = sqlExpr.substring(endPos);
      else
        sqlExpr = "";
      pos = sqlExpr.indexOf("?");
    }
    sb.append(sqlExpr);
    
    
    return sb.toString();
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

      //List creation
      case KEYWORD_LIST:
        sb = new StringBuffer("( ");
        
        for (Enumeration e = exprTree.children(); e.hasMoreElements(); ) 
          sb.append( getSQLExpr( (ParseTreeNode) e.nextElement() ) )
            .append(" , ");
            
        //replace final comma space with close paren.
        sb.replace(sb.length() - 2, sb.length() - 1, " )");
        return sb.toString();

      //fields
      case IDENTIFIER: case DOT:
        JDOFieldDescriptor field = (JDOFieldDescriptor) _fieldInfo.get(exprTree);
        return _engine.quoteName( _clsDesc.getTableName() + "." + field.getSQLName() );

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
        sb = new StringBuffer();
        String copy = new String(exprTree.getToken().getTokenValue());
        
        int pos = copy.indexOf("\\\"");
        while ( pos != -1 ) {
          sb.append(copy.substring(0, pos)).append("\"\"");
          copy = copy.substring(pos + 2);
          pos = copy.indexOf("\\\"");
        }
        
        sb.append(copy);

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
    }

    return "";

    
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
