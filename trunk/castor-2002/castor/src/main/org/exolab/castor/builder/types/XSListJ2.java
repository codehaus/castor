package org.exolab.castor.builder.types;


import org.exolab.castor.builder.types.*;
import org.exolab.javasource.*;


/**
 * A list type for Java 2 collection that adapts the Castor preset list type.
 * @author <a href="mailto:arkin@intalio.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
**/
public class XSListJ2 extends XSList {
    
    
    int maxSize = -1; //-- undefined
    int minSize = 0; 
    
    XSType contentType = null;
    
    /**
     * The JType represented by this XSType
    **/
    private static final JType jType 
        = new JClass("java.util.ArrayList");
        
    private String value = null;
    
    public XSListJ2(XSType contentType) {
        super(contentType);
        this.contentType = contentType;
    } //-- XSListJ2
    
    
    /**
     * Returns the JType that this XSType represents
     * @return the JType that this XSType represents
    **/
    public JType getJType() {
        return this.jType;
    }
    
    public int getMinimumSize() {
        return minSize;
    } //-- getMinimumSize

    public int getMaximumSize() {
        return maxSize;
    } //-- getMaximumSize
    
    public XSType getContentType() {
        return contentType;
    }
    
    public void setMaximumSize(int size) {
        maxSize = size;
    } //-- setMaximumSize
    
    public void setMinimumSize(int size) {
        minSize = size;
    } //-- setMinimumSize
    
} //-- XSListJ2

