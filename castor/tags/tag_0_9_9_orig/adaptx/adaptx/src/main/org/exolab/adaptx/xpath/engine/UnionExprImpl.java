/*
 * (C) Copyright Keith Visco 1999  All rights reserved.
 *
 * The contents of this file are released under an Open Source 
 * Definition (OSD) compliant license; you may not use this file 
 * execpt in compliance with the license. Please see license.txt, 
 * distributed with this file. You may also obtain a copy of the
 * license at http://www.kvisco.com/xslp/license.txt
 *
 * The program is provided "as is" without any warranty express or
 * implied, including the warranty of non-infringement and the implied
 * warranties of merchantibility and fitness for a particular purpose.
 * The Copyright owner will not be liable for any damages suffered by
 * you as a result of using the Program. In no event will the Copyright
 * owner be liable for any special, indirect or consequential damages or
 * lost profits even if the Copyright owner has been advised of the
 * possibility of their occurrence.
 *
 * $Id$
 */


package org.exolab.adaptx.xpath.engine;


import org.exolab.adaptx.xpath.XPathNode;
import org.exolab.adaptx.xpath.XPathResult;
import org.exolab.adaptx.xpath.XPathContext;
import org.exolab.adaptx.xpath.XPathExpression;
import org.exolab.adaptx.xpath.XPathException;
import org.exolab.adaptx.xpath.NodeSet;
import org.exolab.adaptx.xpath.expressions.MatchExpression;
import org.exolab.adaptx.xpath.expressions.PathExpr;
import org.exolab.adaptx.xpath.expressions.UnionExpr;

/**
 * This class represents a UnionExpr
 * <PRE>
 * UnionExpr ::= PathExpr | (PathExpr '|' UnionExpr)
 * </PRE>
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
 */
class UnionExprImpl
    extends UnionExpr
{


    /**
     * A UnionExpr can be a wrapper for an ErrorExpr
    **/
    private ErrorExpr error = null;
    
    
    private PathExprImpl  _pathExpr = null;
    private UnionExprImpl _unionExpr = null;
     
      //----------------/
     //- Constructors -/
    //----------------/
    
    /**
     * Creates an empty UnionExprImpl with will always yeild 
     * an empty NodeSet
     */
    public UnionExprImpl() {
        super();
    } //-- UnionExprImpl
    
    /**
     * Creates a UnionExprImpl wrapper for an ErrorExpr.
     * This allows deferred errors until run-time.
     *
     * @param error the ErrorExpr to create the UnionExpr wrapper for.
     */
    public UnionExprImpl(ErrorExpr error) {
        super();
        this.error = error;
    } //-- UnionExprImpl
    
    /**
     * Creates a UnionExpr with the given PathExpr
     * @param expr the PathExpr
    **/
    public UnionExprImpl(PathExprImpl expr) {
        super();
        _pathExpr = expr;
    } //-- UnionExprImpl(PathExpr)
    
    /**
     * Creates a UnionExor with the given PathExpr and UnionExpr
     */
    public UnionExprImpl(PathExprImpl pathExpr, UnionExprImpl unionExpr) {
        super();
        _pathExpr = pathExpr;
        _unionExpr = unionExpr;
    } //-- UnionExprImpl(PathExpr, UnionExpr)
    
      //------------------/
     //- Public Methods -/
    //------------------/

    /**
     * Returns the PathExpr of this UnionExpr
     *
     * @return the PathExpr of this UnionExpr
     */
    public PathExpr getPathExpr() {
        return _pathExpr;
    } //-- PathExpr getPathExpr()
    
    /**
     * Returns the UnionExpr that this UnionExpr is in union with
     *
     * @return the UnionExpr that this UnionExpr is in union with
     */
    public UnionExpr getUnionExpr() {
        return _unionExpr;
    } //-- UnionExpr getUnionExpr
    
    
    /**
     * Sets the PathExpr of this UnionExpr
     *
     * @param expr the PathExpr of this UnionExpr
     */
    public void setPathExpr(PathExprImpl expr) {
        _pathExpr = expr;
    } //-- setPathExpr(PathExpr)
    
    /**
     * Sets the UnionExpr that this UnionExpr is in union with
     *
     * @param expr the UnionExpr which this UnionExpr is in union with
     */
    public void setUnionExpr(UnionExprImpl expr) {
        _unionExpr = expr;
    } //-- setUnionExpr
    
    /**
     * Evaluates the expression and returns the XPath result.
     *
     * @param context The XPathContext to use during evaluation.
     * @return The XPathResult (not null).
     * @exception XPathException if an error occured while 
     * evaluating this expression.
    **/
    public XPathResult evaluate(XPathContext context)
        throws XPathException
    {
        if (error != null) error.evaluate( context );
        
        NodeSet nodes = null;
        
        if (_pathExpr != null) {
            nodes = (NodeSet)_pathExpr.evaluate(context, true);
        }
        if (_unionExpr != null) {
            nodes.add((NodeSet)_unionExpr.evaluate(context));
        }
            
        if (nodes == null) 
            return context.newNodeSet();
            
        return nodes;
        
    } //-- evaluate
    
    /**
     * Returns the String representation of this UnionExpr
     *
     * @return the String representation of this UnionExpr
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        toString(sb);
        return sb.toString();
    } //-- toString
    
    /**
     * Returns the String representation of this UnionExpr
     *
     * @return the String representation of this UnionExpr
     */
    private void toString(StringBuffer sb) {
        if (_pathExpr != null) 
            sb.append(_pathExpr.toString());
        if (_unionExpr != null) {
            sb.append(" | ");
            _unionExpr.toString(sb);
        }
    } //-- toString

    /**
     * Retrieves the PathExpr that matches the given node. If more
     * than one PathExpr matches the given node, the most specific
     * PathExpr will be returned.
     * @param node the node to test for matching
     * @return the matching PathExpr or null if none match
    **/
    public PathExpr getMatchingExpr
        (XPathNode node, XPathContext context) 
        throws XPathException
    {
        if (error != null) error.evaluate( context );

        PathExpr match = null;
        if (_pathExpr != null) {
            if (_pathExpr.matches(node, context))
                match = _pathExpr;
        }
        if (_unionExpr != null) {
            PathExpr tmp = _unionExpr.getMatchingExpr(node, context);
            if (tmp != null) {
                if (match == null) return tmp;
                else {
                    if (tmp.getDefaultPriority() > 
                        match.getDefaultPriority())
                        match = tmp;
                }
            }
        }
        return match;
    } //-- getMatchingExpr
    
    
    /**
     * Determines if the given node is matched by this MatchExpr with
     * respect to the given context.
     * @param node the node to determine a match for
     * @param context the XPathContext
     * @return true if the given node is matched by this MatchExpr
     * @exception XPathException when an error occurs during
     * evaluation
    **/
    public boolean matches(XPathNode node, XPathContext context)
        throws XPathException
    {
        if (error != null) error.evaluate(null);

        if (_pathExpr != null) {
            if (_pathExpr.matches(node, context))
                return true;
        }
        if (_unionExpr != null)
            return _unionExpr.matches(node, context);
        return false;
    } //-- matches
    
} // -- UnionExpr
