package org.exolab.castor.builder;


import org.exolab.castor.builder.*;
import org.exolab.castor.builder.types.*;
import org.exolab.castor.xml.JavaXMLNaming;
import org.exolab.javasource.*;


/**
 * A helper used for generating source that deals with Java 2 Collections.
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision$ $Date$
**/
public class CollectionInfoJ2 extends CollectionInfo {
 
        
    /**
     * @param contextType the content type of the collection, ie. the type
     * of objects that the collection will contain
     * @param name the name of the Collection
     * @param elementName the element name for each element in collection
    **/
    public CollectionInfoJ2(XSType contentType, String name, String elementName) 
    {
        super(contentType, name, elementName);
        setSchemaType(new XSListJ2(contentType));
    } //-- CollectionInfoJ2
    
    /**
     * Creates code for initialization of this Member
     * @param jsc the JSourceCode in which to add the source to
    **/
    public void generateInitializerCode(JSourceCode jsc) {
        jsc.add(getName());
        jsc.append(" = new ArrayList();");
    } //-- generateConstructorCode
        
    //------------------/
    //- Public Methods -/
    //------------------/
    
    public JMethod[] createAccessMethods() {
        
        JMethod[] methods = new JMethod[4];
        
        JMethod method = null;
        
        JParameter contentParam 
            = new JParameter(getContentType().getJType(), getContentName());
            
        JSourceCode jsc = null;
        
        String cName = JavaXMLNaming.toJavaClassName(getElementName());
        
        method = new JMethod(null, "add"+cName);
        methods[ 0 ] = method;
        method.addException(SGTypes.IndexOutOfBoundsException);
        method.addParameter(contentParam);
        createAddMethod(method);
                            
        JType jType = getContentType().getJType();
        method = new JMethod(new JClass("java.util.Enumeration"), "get"+cName);
        methods[ 1 ] = method;
        createGetMethod(method);

        method = new JMethod(JType.Boolean, "remove"+cName);
        methods[ 2 ] = method;
        method.addParameter(contentParam);
        createRemoveMethod(method);
        
        method = new JMethod(null, "clear"+cName);
        methods[ 3 ] = method;
        createClearMethod(method);
        
        return methods;
    } //-- createAccessMethods


    /** 
     * Creates implementation of add method.
    **/
    public void createAddMethod(JMethod method) {
        
        JSourceCode jsc = method.getSourceCode();
        
        int maxSize = getXSList().getMaximumSize();
        if (maxSize > 0) {
            jsc.add("if (!(");
            jsc.append(getName());
            jsc.append(".size() < ");
            jsc.append(Integer.toString(maxSize));
            jsc.append(")) {");
            jsc.indent();
            jsc.add("throw new IndexOutOfBoundsException();");
            jsc.unindent();
            jsc.add("}");
        }
        jsc.add(getName());
        jsc.append(".add(");
        jsc.append(getContentType().createToJavaObjectCode(getContentName()));
        jsc.append(");");
        
    } //-- createAddMethod
    
                    
    /** 
     * Creates implementation of Enumeration get() method.
    **/
    public void createGetMethod(JMethod method) {

        JSourceCode jsc = method.getSourceCode();
        JType jType = method.getReturnType();
                    
        jsc.add("return new org.exolab.castor.util.IteratorEnumeration( ");
        jsc.append(getName());
        jsc.append(".iterator() );");
    } //-- createGetMethod

    /** 
     * Creates implementation of remove(Object) method.
    **/
    public void createRemoveMethod(JMethod method) {
        JSourceCode jsc = method.getSourceCode();
        
        jsc.add("return ");
        jsc.append(getName());
        jsc.append(".remove(");
        jsc.append(getContentName());
        jsc.append(");");
    } //-- createRemoveMethod

    /** 
     * Creates implementation of clear() method.
    **/
    public void createClearMethod (JMethod method) {
        JSourceCode jsc = method.getSourceCode();
        jsc.add(getName());
        jsc.append(".clear();");
        
    } //-- createClearMethod
    

} //-- CollectionInfoJ2

