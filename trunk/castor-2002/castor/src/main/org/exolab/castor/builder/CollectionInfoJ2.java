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
    
    public void createAccessMethods(JClass jClass) {
        JMethod method = null;
        
        JType jType = getContentType().getJType();
        
        JParameter contentParam = new JParameter(jType, getContentName());
            
        JSourceCode jsc = null;
        
        String cName = JavaXMLNaming.toJavaClassName(getElementName());
        
        method = new JMethod(null, "add"+cName);
        method.addException(SGTypes.IndexOutOfBoundsException);
        method.addParameter(contentParam);
        createAddMethod(method);
                            
        jClass.addMethod(method);
        method = new JMethod(new JClass("java.util.Enumeration"), "get"+cName);
        createGetMethod(method);
        jClass.addMethod(method);

        method = new JMethod(JType.Boolean, "remove"+cName);
        method.addParameter(contentParam);
        createRemoveMethod(method);
        jClass.addMethod(method);
        
        method = new JMethod(null, "clear"+cName);
        createClearMethod(method);
        
        jClass.addMethod(method);
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
     * Creates implementation of Enumerate method.
     **/
    public void createEnumerateMethod(JMethod method) {
        
        JSourceCode jsc = method.getSourceCode();
        
        jsc.add("return new org.exolab.castor.util.IteratorEnumeration(");
        jsc.append(getName());
        jsc.append(".iterator());");
        
    } //-- createEnumerateMethod

    /** 
     * Creates implementation of object[] get() method.
    **/
    public void createGetMethod(JMethod method) {

        JSourceCode jsc = method.getSourceCode();
        JType jType = method.getReturnType();
        
        jsc.add("int size = ");
        jsc.append(getName());
        jsc.append(".size();");
        
        String variableName = getName()+".get(index)";
        
        JType compType = jType.getComponentType();
        
        jsc.add(compType.toString());
        jsc.append("[] mArray = new ");
        jsc.append(compType.getLocalName());
        jsc.append("[size]");
        //-- if component is an array, we must add [] after setting
        //-- size
        if (compType.isArray()) jsc.append("[]");
        jsc.append(";");
        
        jsc.add("for (int index = 0; index < size; index++) {");
        jsc.indent();
        jsc.add("mArray[index] = ");
        if (getContentType().getType() == XSType.CLASS) {
            jsc.append("(");
            jsc.append(jType.getLocalName());
            jsc.append(") ");
            jsc.append(variableName);
        }
        else {
            jsc.append(getContentType()
                       .createFromJavaObjectCode(variableName));
        }
        jsc.append(";");
        jsc.unindent();
        jsc.add("}");
        jsc.add("return mArray;");
    } //-- createGetMethod

    /** 
     * Creates implementation of the get(index) method.
    **/
    public void createGetByIndexMethod(JMethod method) {
        
        JSourceCode jsc = method.getSourceCode();
        JType jType = method.getReturnType();
        
        jsc.add("//-- check bounds for index");
        jsc.add("if ((index < 0) || (index > ");
        jsc.append(getName());
        jsc.append(".size())) {");
        jsc.indent();
        jsc.add("throw new IndexOutOfBoundsException();");
        jsc.unindent();
        jsc.add("}");
        
        jsc.add("");
        jsc.add("return ");
        
        String variableName = getName()+".get(index)";
        
        if (getContentType().getType() == XSType.CLASS) {
            jsc.append("(");
            jsc.append(jType.toString());
            jsc.append(") ");
            jsc.append(variableName);
        }
        else {
            jsc.append(getContentType().createFromJavaObjectCode(variableName));
        }
        jsc.append(";");
    } //-- createGetByIndex

    /** 
     * Creates implementation of set method.
    **/
    public void createSetMethod(JMethod method) {

        JSourceCode jsc = method.getSourceCode();
        
        jsc.add("//-- check bounds for index");
        jsc.add("if ((index < 0) || (index > ");
        jsc.append(getName());
        jsc.append(".size())) {");
        jsc.indent();
        jsc.add("throw new IndexOutOfBoundsException();");
        jsc.unindent();
        jsc.add("}");
        
        int maxSize = getXSList().getMaximumSize();
        if (maxSize != 0) {
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
        jsc.append(".set(index, ");
        jsc.append(getContentType().createToJavaObjectCode(getContentName()));
        jsc.append(");");
        
    } //-- createSetMethod
    
    /** 
     * Creates implementation of remove(Object) method.
    **/
    public void createRemoveMethod(JMethod method) {
        JSourceCode jsc = method.getSourceCode();
        
        jsc.add("return ");
        jsc.append(getName());
        jsc.append(".remove(");
        jsc.append(getContentType().createToJavaObjectCode(getContentName()));
        jsc.append(");");
    } //-- createRemoveMethod

    /** 
     * Creates implementation of remove(Object) method.
    **/
    public void createRemoveByObjectMethod(JMethod method) {
        
        JSourceCode jsc = method.getSourceCode();
        
        jsc.add("return ");
        jsc.append(getName());
        jsc.append(".remove(");
        jsc.append(getContentName());
        jsc.append(");");
        
    } //-- createRemoveByObjectMethod
    
    /** 
     * Creates implementation of remove(int i) method.
    **/
    public void createRemoveByIndexMethod(JMethod method) {
        
        JSourceCode jsc = method.getSourceCode();
        JType jType = method.getReturnType();
        
        jsc.add("Object obj = ");
        jsc.append(getName());
        jsc.append(".get(index);");
        jsc.add(getName());
        jsc.append(".remove(index);");
        jsc.add("return ");
        if (getContentType().getType() == XSType.CLASS) {
            jsc.append("(");
            jsc.append(jType.getName());
            jsc.append(") obj;");
        }
        else {
            jsc.append(getContentType().createFromJavaObjectCode("obj"));
            jsc.append(";");
        }
        
    } //-- createRemoveByIndexMethod

    /** 
     * Creates implementation of removeAll() method.
    **/
    public void createRemoveAllMethod (JMethod method) {

        JSourceCode jsc = method.getSourceCode();
        jsc.add(getName());
        jsc.append(".clear();");
        
    } //-- createRemoveAllMethod

    /** 
     * Creates implementation of clear() method.
    **/
    public void createClearMethod (JMethod method) {
        JSourceCode jsc = method.getSourceCode();
        jsc.add(getName());
        jsc.append(".clear();");
        
    } //-- createClearMethod
    

} //-- CollectionInfoJ2

