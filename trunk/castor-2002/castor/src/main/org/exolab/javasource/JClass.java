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

package org.exolab.javasource;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.Vector;

/**
 * A representation of the Java Source code for a Java Class. This is 
 * a useful utility when creating in memory source code
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class JClass extends JType {

    private static final String DEFAULT_HEADER
        = "Add code header here";

    /**
     * The version for JavaDoc
     * I needed to separate this line to prevent CVS from
     * expanding it! ;-)
    **/
    private static final String version = "$"+"Revision$ $"+"Date$";

    /**
     * The code header to be displayed at the top of the source file
    **/
    private String codeHeader = null;

    /**
     * List of imported classes and packages
    **/
    private Vector imports = null;

    private String superClass = null;
    
    /**
     * The list of member variables of this JClass
    **/
    private JNamedMap members    = null;

    /**
     * The list of constructors for this JClass
    **/
    private Vector constructors  = null;

    /**
     * The set of interfaces implemented by this JClass
    **/
    private Vector interfaces    = null;
    
    
    private JDocComment jdc      = null;
    /**
     * The list of methods of this JClass
    **/
    private Vector methods       = null;

    /**
     * The JModifiers for this JClass, which allows us to
     * change the resulting qualifiers
    **/
    private JModifiers modifiers = null;
    
    /**
     * The package to which this JClass belongs
    **/
    private String packageName   = null;
    
    /**
     * Creates a new Class with the given name
     * @param name the name of the Class to create
     * @exception IllegalArgumentException when the given name
     * is not a valid Class name
    **/
    public JClass(String name) 
        throws IllegalArgumentException
    {
        this(name, false);
    } //-- JClass


    /**
     * Creates a new Class with the given name
     * @param name the name of the Class to create
     * @exception IllegalArgumentException when the given name
     * is not a valid Class name
    **/
    public JClass(String name, boolean isArray) 
        throws IllegalArgumentException
    {
        super(name, isArray);
        this.packageName = getPackageFromClassName(name);
        imports       = new Vector();
        interfaces    = new Vector();
        jdc           = new JDocComment();
        constructors  = new Vector();
        members       = new JNamedMap();
        methods       = new Vector();
        modifiers     = new JModifiers();
        
        //-- initialize default Java doc
        jdc.addDescriptor(JDocDescriptor.createVersionDesc(version));
        
    } //-- JClass

    
    public void addImport(String className) {
        if (className == null) return;
        if (className.length() == 0) return;

        //-- getPackageName
        String pkgName = getPackageFromClassName(className);
        
        if (pkgName != null) {
            if (pkgName.equals(this.packageName)) return;
            if (pkgName.equals("java.lang")) return;
                
            //-- for readabilty keep import list sorted, and make sure
            //-- we do not include more than one of the same import
            for (int i = 0; i < imports.size(); i++) {
                String imp = (String)imports.elementAt(i);
                if (imp.equals(className)) return;
                if (imp.compareTo(className) > 0) {
                    imports.insertElementAt(className, i);
                    return;
                }
            }
            imports.addElement(className);
        }
    } //-- addImport
    
    public void addInterface(String interfaceName) {
        
        if (!interfaces.contains(interfaceName))
            interfaces.addElement(interfaceName);
            
    } //-- addInterface
    
    /**
     * Adds the given Constructor to this classes list of constructors.
     * The constructor must have been created with this JClass' 
     * createConstructor.
     * @exception IllegalArgumentException
    **/
    public void addConstructor(JConstructor constructor) 
        throws IllegalArgumentException
    {
        if (constructor == null)
            throw new IllegalArgumentException("Constructors cannot be null");
        if (constructor.getDeclaringClass() == this) {
            
            /** check signatures (add later) **/
            
            constructors.addElement(constructor);
        }
        else {
            String err = "The given JConstructor was not created ";
            err += "by this JClass";
            throw new IllegalArgumentException(err);
        }
    }
    
    public void addMember(JMember jMember)
        throws IllegalArgumentException 
    {
        if (jMember == null) {
            throw new IllegalArgumentException("Class members cannot be null");
        }
        if (members.get(jMember.getName()) != null) {
            String err = "duplicate name found: " + jMember.getName();
            throw new IllegalArgumentException(err);
        }
        members.put(jMember.getName(), jMember);
        
        // if member is of a type not imported by this class
        // then add import
        JType type = jMember.getType();
        if (!type.isPrimitive()) {
            addImport( ((JClass)type).getName());
        }
        
    } //-- addMember
    
    public void addMethod(JMethod jMethod)
        throws IllegalArgumentException 
    {
        if (jMethod == null) {
            throw new IllegalArgumentException("Class methods cannot be null");
        }
        //-- check method name and signatures
        
        //if (methods.get(jMember.getName()) != null) {
        //    String err = "duplicate name found: " + jMember.getName();
        //    throw new IllegalArgumentException(err);
        //}
        methods.addElement(jMethod);
        
        //-- check parameter packages to make sure we have them
        //-- in our import list
        
        String[] pkgNames = jMethod.getParameterClassNames();
        for (int i = 0; i < pkgNames.length; i++) {
            addImport(pkgNames[i]);
        }
        //-- check return type to make sure it's included in the 
        //-- import list
        JType jType = jMethod.getReturnType();
        if (jType != null) {
            if (!jType.isPrimitive()) {
                addImport( ((JClass)jType).getName());
            }
        }
        //-- check exceptions 
        JClass[] exceptions = jMethod.getExceptions();
        for (int i = 0; i < exceptions.length; i++) {
            addImport(exceptions[i].getName());
        }
    } //-- addMethod
    
    public void addMethods(JMethod[] jMethods) 
        throws IllegalArgumentException
    {
        for (int i = 0; i < jMethods.length; i++) 
            addMethod(jMethods[i]);
    } //-- addMethods

    public JConstructor createConstructor() {
        return new JConstructor(this);
    } //-- createConstructor
    
    public JType createArray() {
        return new JClass(getName(), true);
    } //-- createArray
    
    public JConstructor getConstructor(int index) {
        return (JConstructor)constructors.elementAt(index);
    } //-- getConstructor
    
    public JConstructor[] getConstructors() {
        
        int size = constructors.size();
        JConstructor[] jcArray = new JConstructor[size];
        
        for (int i = 0; i < constructors.size(); i++) {
            jcArray[i] = (JConstructor)constructors.elementAt(i);
        }
        return jcArray;
    } //-- getConstructors
    
    /**
     * Returns the Java Doc comment for this JClass
     * @return the JDocComment for this JClass
    **/
    public JDocComment getJDocComment() {
        return jdc;
    } //-- getJDocComment
    
    /**
     * Returns the member with the given name, or null if no member
     * was found with the given name
     * @param name the name of the member to return
     * @return the member with the given name, or null if no member
     * was found with the given name
    **/
    public JMember getMember(String name) {
        return (JMember)members.get(name);
    } //-- getMember
    
    public JMember[] getMembers() {
        int size = members.size();
        JMember[] marray = new JMember[size];
        for (int i = 0; i < size; i++) {
            marray[i] = (JMember)members.get(i);
        }
        return marray;
    } //-- getMembers
    
    public JMethod[] getMethods() {
        int size = methods.size();
        JMethod[] marray = new JMethod[size];
        
        for (int i = 0; i < methods.size(); i++) {
            marray[i] = (JMethod)methods.elementAt(i);
        }
        return marray;
    } //-- getMethods
    
    public JMethod getMethod(String name, int startIndex) {
        for (int i = startIndex; i < methods.size(); i++) {
            JMethod jMethod = (JMethod)methods.elementAt(i);
            if (jMethod.getName().equals(name)) return jMethod;
        }
        return null;
    }
    
    public JMethod getMethod(int index) {
        return (JMethod)methods.elementAt(index);
    } 
    
    /**
     * Returns the JModifiers which allows the qualifiers to be changed
     * @return the JModifiers for this JClass
    **/
    public JModifiers getModifiers() {
        return modifiers;
    } //-- getModifiers
    
    /**
     * Returns the name of the package that this JClass is a member of
     * @return the name of the package that this JClass is a member of,
     * or null if there is no current package name defined
    **/ 
    public String getPackageName() {
        return this.packageName;
    } //-- getPackageName
    
    public static boolean isValidClassName(String name) {
        //** add class name validation */
        return true;
    } //-- isValidClassName

    public void print() {
        print(null);
    } //-- printSrouce
    
    /**
     * Prints the source code for this JClass
     * @param lineSeparator the line separator to use at the end of each line.
     * If null, then the default line separator for the runtime platform will
     * be used.
    **/
    public void print(String lineSeparator) {

        //-- open output file
        String name = getLocalName();
        String filename = name + ".java";
        
        if ((packageName != null) && (packageName.length() > 0)) {
            String path = packageName.replace('.',File.separatorChar);
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            filename = path+File.separator+filename;
        }
        
        File file = new File(filename);
        JSourceWriter jsw = null;
        try {
            jsw = new JSourceWriter(new FileWriter(file));
        }
        catch(java.io.IOException ioe) {
            System.out.println("unable to create class file: " + filename);
            return;
        }
        
        if (lineSeparator == null) {
            lineSeparator = System.getProperty("line.separator");
        }
        jsw.setLineSeparator(lineSeparator);
        
        StringBuffer buffer = new StringBuffer();
        //-- write class header
        jsw.writeln("/*");
        if (codeHeader == null)
            printlnWithPrefix(" * ", DEFAULT_HEADER, jsw);
        else 
            printlnWithPrefix(" * ", codeHeader, jsw);
            
        //-- separate this line so CVS doesn't expand it
        jsw.writeln(" * $"+"Id"+"$ ");
        jsw.writeln(" */");
        jsw.writeln();
        jsw.flush();
        
        //-- print package name
        if ((packageName != null) && (packageName.length() > 0)) {
            
            buffer.setLength(0);
            buffer.append("package ");
            buffer.append(packageName);
            buffer.append(';');
            jsw.writeln(buffer.toString());
            jsw.writeln();
        }
        
        //-- print imports
        jsw.writeln("  //---------------------------------/");
        jsw.writeln(" //- Imported classes and packages -/");
        jsw.writeln("//---------------------------------/");
        jsw.writeln();
        for (int i = 0; i < imports.size(); i++) {
            jsw.write("import ");
            jsw.write(imports.elementAt(i));
            jsw.writeln(';');
        }
        jsw.writeln();
        
        //------------/
        //- Java Doc -/
        //------------/
        
        jdc.print(jsw);
        
        //-- print class information
        //-- we need to add some JavaDoc API adding comments
        
        buffer.setLength(0);
        
        if (modifiers.isPrivate()) {
            buffer.append("private ");
        }
        else if (modifiers.isPublic()) {
            buffer.append("public ");
        }
        
        if (modifiers.isAbstract()) {
            buffer.append("abstract ");
        }
        
        buffer.append("class ");
        buffer.append(getLocalName());
        buffer.append(' ');
        if (superClass != null) {
            buffer.append("extends ");
            buffer.append(superClass);
            buffer.append(' ');
        }
        if (interfaces.size() > 0) {
            int iSize = interfaces.size();
            boolean endl = false;
            if ((iSize > 1) || (superClass != null)) {
                jsw.writeln(buffer.toString());
                buffer.setLength(0);
                endl = true;
            }
            buffer.append("implements ");
            for (int i = 0; i < iSize; i++) {
                if (i > 0) buffer.append(", ");
                buffer.append(interfaces.elementAt(i));
            }
            if (endl) {
                jsw.writeln(buffer.toString());
                buffer.setLength(0);
            }
            else buffer.append(' ');
        }
        
        buffer.append('{');
        jsw.writeln(buffer.toString());
        buffer.setLength(0);
        jsw.writeln();
        
        jsw.indent();
        
        //-- declare members
        
        if (members.size() > 0) {
            jsw.writeln();
            jsw.writeln("  //--------------------/");
            jsw.writeln(" //- Member Variables -/");
            jsw.writeln("//--------------------/");
            jsw.writeln();
        }
        
        for (int i = 0; i < members.size(); i++) {

            JMember jMember = (JMember)members.get(i);
            
            //-- print Java comment
            jsw.writeln("/**");
            jsw.write(" * ");
            String comment = jMember.getComment();
            if (comment != null) jsw.write(comment);
            jsw.writeln();
            jsw.writeln("**/");
            
            // -- print member
            jsw.write((JMember)members.get(i));
            jsw.writeln(';');
            jsw.writeln();
        }
        
        //-- print constructors
        if (constructors.size() > 0) {
            jsw.writeln();
            jsw.writeln("  //----------------/");
            jsw.writeln(" //- Constructors -/");
            jsw.writeln("//----------------/");
            jsw.writeln();
        }
        for (int i = 0; i < constructors.size(); i++) {
            JConstructor jConstructor = (JConstructor)constructors.elementAt(i);
            jConstructor.print(jsw);
            jsw.writeln();
        }
        
        //-- print methods
        if (methods.size() > 0) {
            jsw.writeln();
            jsw.writeln("  //-----------/");
            jsw.writeln(" //- Methods -/");
            jsw.writeln("//-----------/");
            jsw.writeln();
        }

        for (int i = 0; i < methods.size(); i++) {
            JMethod jMethod = (JMethod)methods.elementAt(i);
            jMethod.print(jsw);
            jsw.writeln();
        }
        
        jsw.unindent();
        jsw.writeln('}');
        jsw.flush();
        jsw.close();
    } //-- printSource
    
    /**
     * Sets the super Class that this class extends
     * @param superClass the super Class that this Class extends
    **/
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    } //-- setSuperClass
    
    //-------------------/
    //- Private Methods -/
    //-------------------/
    
    /**
     * 
    **/
    private void printlnWithPrefix(String prefix, String source, JSourceWriter jsw) {
        jsw.write(prefix);
        if (source == null) return;
        
        char[] chars = source.toCharArray();
        int lastIdx = 0;
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch == '\n') {
                //-- free buffer
                jsw.write(chars,lastIdx,(i-lastIdx)+1);
                lastIdx = i+1;
                if (i < chars.length) {
                    jsw.write(prefix);
                }
            }
        }
        //-- free buffer
        if (lastIdx < chars.length) {
            jsw.write(chars, lastIdx, chars.length-lastIdx);
        }
        jsw.writeln();
        
    } //-- printWithPrefix
    
    
    private static String getPackageFromClassName(String className) {
        int idx = -1;
        if ((idx = className.lastIndexOf('.')) > 0) {
            return className.substring(0, idx);
        }
        return null;
    } //-- getPackageFromClassName
    
    /**
     * Test drive method...to be removed or commented out
    **
    public static void main(String[] args) {
        JClass testClass = new JClass("Test");
        
        testClass.addImport("java.util.Vector");
        testClass.addMember(new JMember(JType.Int, "x"));
        JClass jcString = new JClass("String");
        
        JMember jMember = new JMember(jcString, "myString");
        jMember.getModifiers().makePrivate();
        testClass.addMember(jMember);
        
        //-- create constructor
        JConstructor cons = testClass.createConstructor();
        testClass.addConstructor(cons);
        cons.getSourceCode().add("this.x = 6;");
        
        JMethod jMethod = new JMethod(JType.Int, "getX");
        jMethod.setSourceCode("return this.x;");
        testClass.addMethod(jMethod);
        
        testClass.print();
    } //-- main
    /* */
    
} //-- JClass