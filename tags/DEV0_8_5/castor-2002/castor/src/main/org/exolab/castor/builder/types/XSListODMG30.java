package org.exolab.castor.builder.types;


import org.exolab.castor.builder.types.*;
import org.exolab.javasource.*;


/**
 * A list type for ODMG 3.0 that adapts the Castor preset list type.
 * @author <a href="mailto:frank.thelen@poet.de">Frank Thelen</a>
 * @version $Revision$ $Date$
**/
public class XSListODMG30 extends XSList {
    
    
    int maxSize = -1; //-- undefined
    int minSize = 0; 
    
    XSType contentType = null;
    
    /**
     * The JType represented by this XSType
    **/
    private static final JType jType 
        //= new JClass("java.util.Vector");
        = new JClass("org.odmg.DArray");
        
    private String value = null;
    
    public XSListODMG30(XSType contentType) {
        super(contentType);
        this.contentType = contentType;
    } //-- XSListODMG30
    
    
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
    
} //-- XSListODMG30
