/*
 * (C) Copyright Keith Visco 1999  All rights reserved.
 *
 * The contents of this file are released under an Open Source 
 * Definition (OSD) compliant license; you may not use this file 
 * execpt in compliance with the license. Please see license.txt, 
 * distributed with this file. You may also obtain a copy of the
 * license at http://www.clc-marketing.com/xslp/license.txt
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

import org.exolab.adaptx.xpath.expressions.PrimaryExpr;

/**
 * This class represents a FilterExpr as defined by the
 * XSL Working Draft
 * <PRE>
 * [8] FilterExpr ::= NodeExpr ( '[' BooleanExpr ']' )?
 * </PRE>
 * @author <a href="mailto:kvisco@ziplink.net">Keith Visco</a>
 * @version $Revision$ $Date$
**/
final class FilterExpr
    extends FilterBase
{

    
    private final PrimaryExpr    primaryExpr;


    private final ErrorExpr      error;
    

      //----------------/
     //- Constructors -/
    //----------------/
    

    /**
     * Creates a new FilterExpr
    **/
    protected FilterExpr( PrimaryExpr primary ) 
    {
        super( NO_OP );
        this.primaryExpr = primary;
        error = null;
    } //-- FilterExpr



    protected FilterExpr( PrimaryExpr primary, int ancestryOp ) 
    {
        super( ancestryOp );
        this.primaryExpr = primary;
        error = null;
    } //-- FilterExpr

    
    /**
     * Creates a new FilterExpr wrapper for the given ErrorExpr.
     * This allows errors to be deferred until run-time.
     * @param error the ErrorExpr to "wrap".
    **/
    protected FilterExpr(ErrorExpr error) {
        super( NO_OP );
        this.error = error;
        primaryExpr = null;
    } //-- FilterExpr

    
      //------------------/
     //- Public Methods -/
    //------------------/


    public short getExprType()
    {
        return XPathExpression.FILTER_EXPR;
    }

    
    /**
     * Evaluates the expression and returns the XPath result.
     *
     * @param context The XPathContext to use during evaluation.
     * @return The XPathResult (not null).
     * @exception XPathException if an error occured while 
     * evaluating this expression.
    **/
    public XPathResult evaluate( XPathContext context )
        throws XPathException
    {
        if (error != null) error.evaluate( context );
        
        if (primaryExpr == null) return context.newNodeSet();
        
        XPathResult result = primaryExpr.evaluate( context );
        NodeSet nodeSet = null;
        
        switch (result.getResultType()) {
            case XPathResult.NODE_SET:
                nodeSet = (NodeSet)result;
                break;
            default:
                System.out.println("ExprResult: " + result.getResultType());
                throw new XPathException
                    ("expecting NodeSet or TreeFragment as the result of the "+
                        "expression: " + primaryExpr);
        }
        
        //-- filter nodes
        if (hasPredicates()) {
            evaluatePredicates(nodeSet, context);
        }
        return nodeSet;
        
    } //-- evaluate
    
    /**
     * Determines the priority of a PatternExpr as follows:
     * <PRE>
     *  From the 19991116 XSLT 1.0 Recommendation:
     *  + If the pattern has the form of a QName preceded by a
     *    ChildOrAttributeAxisSpecifier or has the form 
     *    processing-instruction(Literal) then the priority is 0.
     *  + If the pattern has the form NCName:* preceded by a 
     *    ChildOrAttributeAxisSpecifier, then the priority is -0.25
     *  + Otherwise if the pattern consists of just a NodeTest 
     *    preceded by a ChildOrAttributeAxisSpecifier then the
     *    priority is -0.5
     *  + Otherwise the priority is 0.5
     * </PRE>
     * @return the priority for this PatternExpr
    **/
    public double getDefaultPriority() {
        return 0.5;
    } //-- getDefaultPriority
    
    
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
        NodeSet nodes = (NodeSet) evaluate( context );
        evaluatePredicates(nodes, context);
        return nodes.contains(node);
    } //-- matches
    
    /**
     * Returns the String representation of this FilterExpr
     * @return the String representation of this FilterExpr
    **/
    public String toString() {
        if (primaryExpr == null)
            return super.toString();
        else {
            StringBuffer strbuf = new StringBuffer();
            strbuf.append(primaryExpr.toString());    
            strbuf.append(super.toString());
            return strbuf.toString();
        }
    } //-- toString
    
    
    public PrimaryExpr getPrimaryExpr() {
        return this.primaryExpr;
    } //-- getPrimaryExpr


} //-- FilterExpr
