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
 * Copyright 1999-2000 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.builder;

import org.exolab.javasource.*;
import org.exolab.castor.builder.types.*;
import org.exolab.castor.types.TimeDuration;
import org.exolab.castor.types.RecurringDuration;
import org.exolab.castor.types.Time;
import org.exolab.castor.builder.util.DescriptorJClass;

/**
 * A factory for creating the source code of descriptor classes
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class DescriptorSourceFactory {


    //-- org.exolab.castor.mapping
    private static JClass _ClassDescriptorClass
        = new JClass("org.exolab.castor.mapping.ClassDescriptor");

    private static JClass _FieldDescriptorClass
        = new JClass("org.exolab.castor.mapping.FieldDescriptor");

    //-- org.exolab.castor.xml
    private static JClass fdImplClass
        = new JClass("org.exolab.castor.xml.util.XMLFieldDescriptorImpl");


    private static JClass fdClass
        = new JClass("org.exolab.castor.xml.XMLFieldDescriptor");


    private static JType fdArrayClass = fdClass.createArray();

    private static JClass gvrClass
        = new JClass("org.exolab.castor.xml.GroupValidationRule");

    private static JClass vrClass
        = new JClass("org.exolab.castor.xml.ValidationRule");


    /**
     * Creates the Source code of a MarshalInfo for a given XML Schema
     * element declaration
     * @param element the XML Schema element declaration
     * @return the JClass representing the MarshalInfo source code
    **/
    public static JClass createSource(ClassInfo classInfo) {


        JMethod     method          = null;
        JSourceCode jsc             = null;
        JSourceCode vcode           = null;
        JClass      jClass          = classInfo.getJClass();
        String      className       = jClass.getName();
        String      localClassName  = jClass.getLocalName();


        String variableName = "_"+className;

        DescriptorJClass classDesc
            = new DescriptorJClass(className+"Descriptor", jClass);

        //-- get handle to default constuctor

        JConstructor cons = classDesc.getConstructor(0);
        jsc = cons.getSourceCode();
		jsc.add("super();");

        //-- Set namespace prefix
        String nsPrefix    = classInfo.getNamespacePrefix();
        if ((nsPrefix != null) && (nsPrefix.length() > 0)) {
            jsc.add("nsPrefix = \"");
            jsc.append(nsPrefix);
            jsc.append("\";");
        }

        //-- Set namespace URI
        String nsURI       = classInfo.getNamespaceURI();
        if ((nsURI != null) && (nsURI.length() > 0)) {
            jsc.add("nsURI = \"");
            jsc.append(nsURI);
            jsc.append("\";");
        }

        //-- set XML Name
        String xmlName     = classInfo.getNodeName();
        if (xmlName != null) {
            jsc.add("xmlName = \"");
            jsc.append(xmlName);
            jsc.append("\";");
        }

        jsc.add("XMLFieldDescriptorImpl  desc           = null;");
        jsc.add("XMLFieldHandler         handler        = null;");
        jsc.add("FieldValidator          fieldValidator = null;");

        //jsc.add("Class[] emptyClassArgs = new Class[0];");
        //jsc.add("Class[] classArgs = new Class[1];");

        //-- create validation method
        //method = new JMethod(vrClass.createArray(), "getValidationRules");
        //vcode = method.getSourceCode();
        //vcode.add("return rules;");
        //marshalInfo.addMethod(method);


        //-- create GroupValidationRule

        //jsc.add("gvr = new GroupValidationRule();");
        //jsc.add("BasicValidationRule bvr = null;");


        //-- handle text content
        if (classInfo.allowsTextContent()) {

            jsc.add("XMLFieldDescriptorImpl contentDesc = new XMLFieldDescriptorImpl(");
            jsc.append("String.class, \"_content\", \"PCDATA\", ");
            jsc.append("NodeType.Text);");

            jsc.add("contentDesc.setHandler( new XMLFieldHandler() {");
            jsc.indent();

            //-- read method
            jsc.add("public Object getValue( Object object ) ");
            jsc.indent();
            jsc.add("throws IllegalStateException");
            jsc.unindent();
            jsc.add("{");
            jsc.indent();
            jsc.add(localClassName);
            jsc.append(" target = (");
            jsc.append(localClassName);
            jsc.append(") object;");
            jsc.add("return target.getContent();");
            jsc.unindent();
            jsc.add("}");

            //-- write method
            jsc.add("public void setValue( Object object, Object value) ");
            jsc.indent();
            jsc.add("throws IllegalStateException, IllegalArgumentException");
            jsc.unindent();
            jsc.add("{");
            jsc.indent();
            jsc.add("try {");
            jsc.indent();
            jsc.add(localClassName);
            jsc.append(" target = (");
            jsc.append(localClassName);
            jsc.append(") object;");
            jsc.add("target.setContent( (String) value);");
            jsc.unindent();
            jsc.add("}");
            jsc.add("catch (Exception ex) {");
            jsc.indent();
            jsc.add("throw new IllegalStateException(ex.toString());");
            jsc.unindent();
            jsc.add("}");
            jsc.unindent();
            jsc.add("}");

            //-- newInstance method
            jsc.add("public Object newInstance( Object parent ) {");
            jsc.indent();
            jsc.add("return new String();");
            jsc.unindent();
            jsc.add("}");


            jsc.unindent();
            jsc.add("} );");
			jsc.add("addFieldDescriptor(contentDesc);");
        }

        FieldInfo[] atts = classInfo.getAttributeFields();

        //-- initialized rules
        //jsc.add("rules = new ValidationRule[");
        //jsc.append(Integer.toString(atts.length+1));
        //jsc.append("];");

        //--------------------------------/
        //- Create attribute descriptors -/
        //--------------------------------/

        jsc.add("//-- initialize attribute descriptors");
        jsc.add("");

        for (int i = 0; i < atts.length; i++) {

            FieldInfo member = atts[i];

            //-- skip transient members
            if (member.isTransient()) continue;

            boolean isEnumerated = false;

            jsc.add("//-- ");
            jsc.append(member.getName());

            XSType xsType = member.getSchemaType();
            jsc.add("desc = new XMLFieldDescriptorImpl(");
            jsc.append(classType(xsType.getJType()));
            jsc.append(", \"");
            jsc.append(member.getName());
            jsc.append("\", \"");
            jsc.append(member.getNodeName());
            jsc.append("\", NodeType.Attribute);");


            switch (xsType.getType()) {

                case XSType.STRING:
                    jsc.add("desc.setImmutable(true);");
                    break;
                case XSType.CLASS:
                    isEnumerated = ((XSClass)xsType).isEnumerated();
                    break;
                case XSType.IDREF:
                    jsc.add("desc.setReference(true);");
                    break;
                case XSType.ID:
                    jsc.add("this.identity = desc;");
                    break;
                default:
                    break;
            }
            //-- handler access methods


            jsc.add("handler = (new XMLFieldHandler() {");
            jsc.indent();

            //-- read method
            jsc.add("public Object getValue( Object object ) ");
            jsc.indent();
            jsc.add("throws IllegalStateException");
            jsc.unindent();
            jsc.add("{");
            jsc.indent();
            jsc.add(localClassName);
            jsc.append(" target = (");
            jsc.append(localClassName);
            jsc.append(") object;");
			//-- Handle optional primitives?
			if ((!xsType.isEnumerated()) && xsType.isPrimitive() && (!member.isRequired()) && (!member.isMultivalued()))
			{
				jsc.add("if(!target."+member.getHasMethodName()+"())");
				jsc.indent();
				jsc.add("return null;");
				jsc.unindent();
			}
            String value = "target."+member.getReadMethodName()+"()";
            jsc.add("return ");
            jsc.append(xsType.createToJavaObjectCode(value));
            jsc.append(";");
            jsc.unindent();
            jsc.add("}");

            //-- write method
            jsc.add("public void setValue( Object object, Object value) ");
            jsc.indent();
            jsc.add("throws IllegalStateException, IllegalArgumentException");
            jsc.unindent();
            jsc.add("{");
            jsc.indent();
            //-- check for null primitives
            if (xsType.isPrimitive()) {
                jsc.add("// ignore null values for primitives");
                jsc.add("if (value == null) return;");
                jsc.add("");
            }
            jsc.add("try {");
            jsc.indent();
            jsc.add(localClassName);
            jsc.append(" target = (");
            jsc.append(localClassName);
            jsc.append(") object;");
            jsc.add("target.");
            jsc.append(member.getWriteMethodName());
            jsc.append("( ");
            if (xsType.isPrimitive()) {
                jsc.append(xsType.createFromJavaObjectCode("value"));
            }
            else {
                jsc.append("(");
                jsc.append(xsType.getJType().toString());
                jsc.append(") value");
            }
            jsc.append(");");

            jsc.unindent();
            jsc.add("}");
            jsc.add("catch (Exception ex) {");
            jsc.indent();
            jsc.add("throw new IllegalStateException(ex.toString());");
            jsc.unindent();
            jsc.add("}");
            jsc.unindent();
            jsc.add("}");

            //-- newInstance method
            jsc.add("public Object newInstance( Object parent ) {");
            jsc.indent();
            jsc.add("return ");

            if (xsType.isEnumerated()       ||
                xsType.isPrimitive()        ||
                xsType.getJType().isArray() ||
                (xsType.getType() == XSType.STRING))
            {
                jsc.append("null;");
            }
            else {
                jsc.append(xsType.newInstanceCode());
            }
            jsc.unindent();
            jsc.add("}");


            jsc.unindent();
            jsc.add("} );");

            if (isEnumerated) {
                jsc.add("desc.setHandler( new EnumFieldHandler(");
                jsc.append(classType(xsType.getJType()));
                jsc.append(", handler));");
                jsc.add("desc.setImmutable(true);");
            }
            else if (xsType.getType() == XSType.TIME_INSTANT) {
                jsc.add("desc.setHandler( new DateFieldHandler(");
                jsc.append("handler));");
                jsc.add("desc.setImmutable(true);");
            }
            else if (xsType.getType() == XSType.DECIMAL) {
                jsc.add("desc.setHandler( new DecimalFieldHandler(");
                jsc.append("handler));");
                jsc.add("desc.setImmutable(true);");
            }
            else jsc.add("desc.setHandler(handler);");

            //-- namespace
            if (nsURI != null) {
                jsc.add("desc.setNameSpaceURI(\"");
                jsc.append(nsURI);
                jsc.append("\");");
            }

            if (member.isRequired()) {
                jsc.add("desc.setRequired(true);");
            }


			jsc.add("addFieldDescriptor(desc);");
            jsc.add("");

            //-- Add Validation Code
            jsc.add("//-- validation code for: ");
            jsc.append(member.getName());
            jsc.add("fieldValidator = new FieldValidator();");
            validationCode(member, jsc);
            jsc.add("desc.setValidator(fieldValidator);");
            jsc.add("");
        }


        //------------------------------/
        //- Create element descriptors -/
        //------------------------------/

        //jsc.add("rules[");
        //jsc.append(Integer.toString(atts.length));
        //jsc.append("] = gvr;");

        FieldInfo[] elements = classInfo.getElementFields();

        jsc.add("//-- initialize element descriptors");
        jsc.add("");

        for (int i = 0; i < elements.length; i++) {

            FieldInfo member = elements[i];

            //-- skip transient members
            if (member.isTransient()) continue;

            XSType xsType = member.getSchemaType();


            jsc.add("//-- ");
            jsc.append(member.getName());

            boolean any = false;
            boolean isEnumerated = false;

            //-- a hack, I know, I will change later (kv)
            if (member.getName().equals("_anyList")) {
                any = true;
                jsc.add("desc = (new XMLFieldDescriptorImpl(");
                jsc.append("Object.class, \"");
                jsc.append(member.getName());
                jsc.append("\", (String)null, NodeType.Element) { ");
                jsc.indent();
                jsc.add("public boolean matches(String xmlName) {");
                jsc.add("    return true;");
                jsc.add("}");
                jsc.unindent();
                jsc.add("});");
            }
            else {

                if (xsType.getType() == XSType.COLLECTION)
                    xsType = ((CollectionInfo)member).getContent().getSchemaType();

                jsc.add("desc = new XMLFieldDescriptorImpl(");
                jsc.append(classType(xsType.getJType()));
                jsc.append(", \"");
                jsc.append(member.getName());
                jsc.append("\", ");

                String nodeName = member.getNodeName();
                if (nodeName != null) {
                    jsc.append("\"");
                    jsc.append(member.getNodeName());
                    jsc.append("\"");
                }
                else {
                    jsc.append("(String)null");
                }

                jsc.append(", NodeType.Element);");

                switch (xsType.getType()) {

                    case XSType.CLASS:
                        isEnumerated = ((XSClass)xsType).isEnumerated();
                        break;
                    case XSType.STRING:
                        jsc.add("desc.setImmutable(true);");
                        break;
                    default:
                        break;
                }

            }

            //-- handler access methods

            jsc.add("handler = (new XMLFieldHandler() {");
            jsc.indent();

            //-- read method
            jsc.add("public Object getValue( Object object ) ");
            jsc.indent();
            jsc.add("throws IllegalStateException");
            jsc.unindent();
            jsc.add("{");
            jsc.indent();
            jsc.add(localClassName);
            jsc.append(" target = (");
            jsc.append(localClassName);
            jsc.append(") object;");
			//-- Handle optional primatives?
			if ((!xsType.isEnumerated()) && xsType.isPrimitive() && (!member.isRequired()) && (!member.isMultivalued()))
			{
				jsc.add("if(!target."+member.getHasMethodName()+"())");
				jsc.indent();
				jsc.add("return null;");
				jsc.unindent();
			}
			//-- Return field value
            jsc.add("return ");
            String value = "target."+member.getReadMethodName()+"()";
            if (member.isMultivalued()) jsc.append(value);
            else jsc.append(xsType.createToJavaObjectCode(value));
            jsc.append(";");
            jsc.unindent();
            jsc.add("}");

            //-- write method
            jsc.add("public void setValue( Object object, Object value) ");
            jsc.indent();
            jsc.add("throws IllegalStateException, IllegalArgumentException");
            jsc.unindent();
            jsc.add("{");
            jsc.indent();
            //-- check for null primitives
            if (xsType.isPrimitive()) {
                jsc.add("// ignore null values for primitives");
                jsc.add("if (value == null) return;");
                jsc.add("");
            }
            jsc.add("try {");
            jsc.indent();
            jsc.add(localClassName);
            jsc.append(" target = (");
            jsc.append(localClassName);
            jsc.append(") object;");
            jsc.add("target.");
            jsc.append(member.getWriteMethodName());
            jsc.append("( ");
            if (xsType.isPrimitive()) {
                jsc.append(xsType.createFromJavaObjectCode("value"));
            }
            else if (any) {
                jsc.append(" value ");
            }
            else {
                jsc.append("(");
                jsc.append(xsType.getJType().toString());
                jsc.append(") value");
            }
            jsc.append(");");

            jsc.unindent();
            jsc.add("}");
            jsc.add("catch (Exception ex) {");
            jsc.indent();
            jsc.add("throw new IllegalStateException(ex.toString());");
            jsc.unindent();
            jsc.add("}");
            jsc.unindent();
            jsc.add("}");

            //-- newInstance method
            jsc.add("public Object newInstance( Object parent ) {");
            jsc.indent();
            jsc.add("return ");


            if (any || isEnumerated ||
                xsType.isPrimitive() ||
                xsType.getJType().isArray() ||
                (xsType.getType() == XSType.STRING))
            {
                jsc.append("null;");
            }
            else {
                jsc.append(xsType.newInstanceCode());
            }
            jsc.unindent();
            jsc.add("}");


            jsc.unindent();
            jsc.add("} );");

            if (isEnumerated) {
                jsc.add("desc.setHandler( new EnumFieldHandler(");
                jsc.append(classType(xsType.getJType()));
                jsc.append(", handler));");
                jsc.add("desc.setImmutable(true);");
            }
            else if (xsType.getType() == XSType.TIME_INSTANT) {
                jsc.add("desc.setHandler( new DateFieldHandler(");
                jsc.append("handler));");
                jsc.add("desc.setImmutable(true);");
            }
            else if (xsType.getType() == XSType.DECIMAL) {
                jsc.add("desc.setHandler( new DecimalFieldHandler(");
                jsc.append("handler));");
                jsc.add("desc.setImmutable(true);");
            }
            else jsc.add("desc.setHandler(handler);");

            //-- namespace
            if (nsURI != null) {
                jsc.add("desc.setNameSpaceURI(\"");
                jsc.append(nsURI);
                jsc.append("\");");
            }

            if (member.isRequired()) {
                jsc.add("desc.setRequired(true);");
            }

            //-- mark as multi or single valued
            jsc.add("desc.setMultivalued("+member.isMultivalued());
            jsc.append(");");

			jsc.add("addFieldDescriptor(desc);");
            jsc.add("");

            //-- Add Validation Code
            jsc.add("//-- validation code for: ");
            jsc.append(member.getName());
            jsc.add("fieldValidator = new FieldValidator();");
            validationCode(member, jsc);
            jsc.add("desc.setValidator(fieldValidator);");
            jsc.add("");
        }

        return classDesc;

    } //-- createSource

    //-------------------/
    //- Private Methods -/
    //-------------------/

    /**
     * Returns the Class type (as a String) for the given XSType
    **/
    private static String classType(JType jType) {
        if (jType.isPrimitive()) {
            if (jType == JType.Int)
                return "java.lang.Integer.TYPE";
            else if (jType == JType.Double)
                return "java.lang.Double.TYPE";
            else if (jType == JType.Boolean) {
                return "java.lang.Boolean.TYPE";
            }
        }
        return jType.toString() + ".class";
    } //-- classType

    private static void validationCode(FieldInfo member, JSourceCode jsc) {

        //-- a hack, I know, I will change later
        if (member.getName().equals("_anyObject")) return;

        XSType xsType = member.getSchemaType();

        //-- create local copy of field
        //JMember jMember = member.createMember();

        if (xsType.getType() != XSType.COLLECTION) {
            if (member.isRequired()) {
                jsc.add("fieldValidator.setMinOccurs(1);");
            }
        }

        String fixed = member.getFixedValue();
        String pattern = null;

        //-- create proper validator

        switch (xsType.getType()) {

            case XSType.DECIMAL:
                jsc.add("{ //-- local scope");
                jsc.indent();
                jsc.add("DecimalValidator dv = new DecimalValidator();");
                XSDecimal xsDecimal = (XSDecimal)xsType;
                if (xsDecimal.hasMinimum()) {
                    java.math.BigDecimal min = xsDecimal.getMinExclusive();
                    if (min != null)
                        jsc.add("dv.setMinExclusive(new java.math.BigDecimal(");
                    else {
                        min = xsDecimal.getMinInclusive();
                        jsc.add("dv.setMinInclusive(new java.math.BigDecimal(");
                    }
                    jsc.append(min.toString()+")");
                    jsc.append(");");
                }
                if (xsDecimal.hasMaximum()) {
                    java.math.BigDecimal max = xsDecimal.getMaxExclusive();
                    if (max != null)
                        jsc.add("dv.setMaxExclusive(new java.math.BigDecimal(");
                    else {
                        max = xsDecimal.getMaxInclusive();
                        jsc.add("dv.setMaxInclusive(new java.math.BigDecimal(");
                    }
                    jsc.append(max.toString()+")");
                    jsc.append(");");
                }

                if (xsDecimal.getScale() != null) {
                    jsc.add("dv.setScale(");
                    jsc.append(xsDecimal.getScale().toString());
                    jsc.append(");");
                }

                if (xsDecimal.getPrecision() != null) {
                    jsc.add("dv.setPrecision(");
                    jsc.append(xsDecimal.getPrecision().toString());
                    jsc.append(");");
                }

                jsc.add("fieldValidator.setValidator(dv);");
                jsc.unindent();
                jsc.add("}");
                break;

            case XSType.NEGATIVE_INTEGER:
            case XSType.POSITIVE_INTEGER:
            case XSType.INTEGER:
                jsc.add("{ //-- local scope");
                jsc.indent();
                jsc.add("IntegerValidator iv = new IntegerValidator();");
                XSInteger xsInteger = (XSInteger)xsType;
                if (xsInteger.hasMinimum()) {
                    Integer min = xsInteger.getMinExclusive();
                    if (min != null)
                        jsc.add("iv.setMinExclusive(");
                    else {
                        min = xsInteger.getMinInclusive();
                        jsc.add("iv.setMinInclusive(");
                    }
                    jsc.append(min.toString());
                    jsc.append(");");
                }
                if (xsInteger.hasMaximum()) {
                    Integer max = xsInteger.getMaxExclusive();
                    if (max != null)
                        jsc.add("iv.setMaxExclusive(");
                    else {
                        max = xsInteger.getMaxInclusive();
                        jsc.add("iv.setMaxInclusive(");
                    }
                    jsc.append(max.toString());
                    jsc.append(");");
                }

                //-- fixed values
                if (fixed != null) {
                    //-- make sure we have a valid value...
                    Integer.parseInt(fixed);

                    jsc.add("iv.setFixedValue(");
                    jsc.append(fixed);
                    jsc.append(");");
                }
                //-- pattern facet
                pattern = xsInteger.getPattern();
                if (pattern != null) {
                    jsc.add("iv.setPattern(\"");
                    jsc.append(escapePattern(pattern));
                    jsc.append("\");");
                }

                jsc.add("fieldValidator.setValidator(iv);");
                jsc.unindent();
                jsc.add("}");
                break;
			case XSType.INT:
                jsc.add("{ //-- local scope");
                jsc.indent();
                jsc.add("IntegerValidator iv = new IntegerValidator();");
                XSInt xsInt = (XSInt)xsType;
                if (xsInt.hasMinimum()) {
                    Integer min = xsInt.getMinExclusive();
                    if (min != null)
                        jsc.add("iv.setMinExclusive(");
                    else {
                        min = xsInt.getMinInclusive();
                        jsc.add("iv.setMinInclusive(");
                    }
                    jsc.append(min.toString());
                    jsc.append(");");
                }
                if (xsInt.hasMaximum()) {
                    Integer max = xsInt.getMaxExclusive();
                    if (max != null)
                        jsc.add("iv.setMaxExclusive(");
                    else {
                        max = xsInt.getMaxInclusive();
                        jsc.add("iv.setMaxInclusive(");
                    }
                    jsc.append(max.toString());
                    jsc.append(");");
                }

                //-- fixed values
                if (fixed != null) {
                    //-- make sure we have a valid value...
                    Integer.parseInt(fixed);

                    jsc.add("iv.setFixedValue(");
                    jsc.append(fixed);
                    jsc.append(");");
                }
                //-- pattern facet
                pattern = xsInt.getPattern();
                if (pattern != null) {
                    jsc.add("iv.setPattern(\"");
                    jsc.append(escapePattern(pattern));
                    jsc.append("\");");
                }

                jsc.add("fieldValidator.setValidator(iv);");
                jsc.unindent();
                jsc.add("}");
                break;
            //-- int
            case XSType.LONG:
            {
                jsc.add("{ //-- local scope");
                jsc.indent();
                jsc.add("LongValidator lv = new LongValidator();");
                XSLong xsLong = (XSLong)xsType;
                if (xsLong.hasMinimum()) {
                    Long min = xsLong.getMinExclusive();
                    if (min != null)
                        jsc.add("lv.setMinExclusive(");
                    else {
                        min = xsLong.getMinInclusive();
                        jsc.add("lv.setMinInclusive(");
                    }
                    jsc.append(min.toString());
                    jsc.append("L);");
                }
                if (xsLong.hasMaximum()) {
                    Long max = xsLong.getMaxExclusive();
                    if (max != null)
                        jsc.add("lv.setMaxExclusive(");
                    else {
                        max = xsLong.getMaxInclusive();
                        jsc.add("lv.setMaxInclusive(");
                    }
                    jsc.append(max.toString());
                    jsc.append("L);");
                }

                //-- fixed values
                if (fixed != null) {
                    //-- make sure we have a valid value...
                    Long.parseLong(fixed);

                    jsc.add("lv.setFixedValue(");
                    jsc.append(fixed);
                    jsc.append(");");
                }
                //-- pattern facet
                pattern = xsLong.getPattern();
                if (pattern != null) {
                    jsc.add("lv.setPattern(\"");
                    jsc.append(escapePattern(pattern));
                    jsc.append("\");");
                }

                jsc.add("fieldValidator.setValidator(lv);");
                jsc.unindent();
                jsc.add("}");
                break;
            }
            case XSType.STRING:
                jsc.add("{ //-- local scope");
                jsc.indent();
                jsc.add("StringValidator sv = new StringValidator();");
                XSString xsString = (XSString)xsType;
                if (xsString.hasMinLength()) {
                    jsc.add("sv.setMinLength(");
                    jsc.append(Integer.toString(xsString.getMinLength()));
                    jsc.append(");");
                }
                if (xsString.hasMaxLength()) {
                    jsc.add("sv.setMaxLength(");
                    jsc.append(Integer.toString(xsString.getMaxLength()));
                    jsc.append(");");
                }
                //-- fixed values
                if (fixed != null) {
                    jsc.add("sv.setFixedValue(\"");
                    jsc.append(fixed);
                    jsc.append("\");");
                }
                //-- pattern facet
                pattern = xsString.getPattern();
                if (pattern != null) {
                    jsc.add("sv.setPattern(\"");
                    jsc.append(escapePattern(pattern));
                    jsc.append("\");");
                }

                jsc.add("fieldValidator.setValidator(sv);");
                jsc.unindent();
                jsc.add("}");
                break;
            case XSType.NCNAME:
                jsc.add("fieldValidator.setValidator(new NameValidator());");
                break;
            case XSType.NMTOKEN:
                jsc.add("fieldValidator.setValidator(new NameValidator(");
                jsc.append("NameValidator.NMTOKEN));");
                break;
            case XSType.COLLECTION:
                XSList xsList = (XSList)xsType;
                CollectionInfo cInfo = (CollectionInfo)member;
                FieldInfo content = cInfo.getContent();

                jsc.add("fieldValidator.setMinOccurs(");
                jsc.append(Integer.toString(xsList.getMinimumSize()));
                jsc.append(");");
                if (xsList.getMaximumSize() > 0) {
                    jsc.add("fieldValidator.setMaxOccurs(");
                    jsc.append(Integer.toString(xsList.getMaximumSize()));
                    jsc.append(");");
                }
                break;

            case XSType.TIME:
                jsc.add("{ //-- local scope");
                jsc.indent();
                jsc.add("TimeValidator tv = new TimeValidator();");
                XSTime xsTime = (XSTime)xsType;
                if (xsTime.hasMinimum()) {
                    jsc.add("try {");
                    jsc.indent();
                   RecurringDuration min = xsTime.getMinExclusive();
                    if (min != null)
                        jsc.add("tv.setMinExclusive(");
                    else {
                        min = xsTime.getMinInclusive();
                        jsc.add("tv.setMinInclusive(");
                    }
                    jsc.append("org.exolab.castor.types.Time.parse(\""
                                +min.toString()+"\"");
                    jsc.append(");");
                    jsc.unindent();
                    jsc.add("} catch (java.text.ParseException e) {");
                    jsc.indent();
                    jsc.add("System.out.println(e);");
                    jsc.add("e.printStackTrace();");
                    jsc.add("return;");
                    jsc.unindent();
                    jsc.add("}");

                }//hasMinimum?

                if (xsTime.hasMaximum()) {
                    jsc.add("try {");
                    jsc.indent();
                   RecurringDuration max = xsTime.getMaxExclusive();
                    if (max != null)
                        jsc.add("tv.setMaxExclusive(");
                    else {
                        max = xsTime.getMaxInclusive();
                        jsc.add("tv.setMaxInclusive(");
                    }
                    jsc.append("org.exolab.castor.types.Time.parse(\""
                                +max.toString()+"\"");
                    jsc.append(");");
                    jsc.unindent();
                    jsc.add("} catch (java.text.ParseException e) {");
                    jsc.indent();
                    jsc.add("System.out.println(e);");
                    jsc.add("e.printStackTrace();");
                    jsc.add("return;");
                    jsc.unindent();
                    jsc.add("}");

                }//hasMaximum
                //-- pattern facet

                jsc.add("fieldValidator.setValidator(tv);");
                jsc.unindent();
                jsc.add("}");
                break;
            //-- Time

            case XSType.CENTURY:
            case XSType.YEAR:
            case XSType.MONTH:
            case XSType.DATE:
            case XSType.TIME_PERIOD:
            case XSType.RECURRING_DURATION:
                jsc.add("{ //-- local scope");
                jsc.indent();
                jsc.add("RecurringDurationValidator rv = new RecurringDurationValidator();");
                XSRecurringDuration xsReccD = (XSRecurringDuration)xsType;
                if (xsReccD.hasMinimum()) {
                    jsc.add("try {");
                    jsc.indent();
                    RecurringDuration min = xsReccD.getMinExclusive();
                    if (min != null) {
                        jsc.add("org.exolab.castor.types.RecurringDuration min ="
                                +"org.exolab.castor.types.RecurringDuration.parseRecurring("
                                +"\""+min.toString()+"\");");
                        jsc.add("min.setDuration(\""+min.getDuration().toString()+"\");");
                        jsc.add("min.setPeriod(\""+min.getPeriod().toString()+"\");");
                        jsc.add("rv.setMinExclusive(");
                    } else {
                        min = xsReccD.getMinInclusive();
                        jsc.add("org.exolab.castor.types.RecurringDuration min ="
                                +"org.exolab.castor.types.RecurringDuration.parseRecurring("
                                +"\""+min.toString()+"\");");
                         jsc.add("min.setDuration(\""+min.getDuration().toString()+"\");");
                         jsc.add("min.setPeriod(\""+min.getPeriod().toString()+"\");");
                   jsc.add("rv.setMinInclusive(");
                    }
                    jsc.append("min");
                    jsc.append(");");
                    jsc.unindent();
                    jsc.add("} catch (java.text.ParseException e) {");
                    jsc.indent();
                    jsc.add("System.out.println(e);");
                    jsc.add("e.printStackTrace();");
                    jsc.add("return;");
                    jsc.unindent();
                    jsc.add("}");

                }//hasMinimum?

                if (xsReccD.hasMaximum()) {
                    jsc.add("try {");
                    jsc.indent();
                    RecurringDuration max = xsReccD.getMaxExclusive();
                    if (max != null) {
                        jsc.add("org.exolab.castor.types.RecurringDuration max ="
                                +"org.exolab.castor.types.RecurringDuration.parseRecurring("
                                +"\""+max.toString()+"\");");
                        jsc.add("max.setDuration(\""+max.getDuration().toString()+"\");");
                        jsc.add("max.setPeriod(\""+max.getPeriod().toString()+"\");");
                        jsc.add("rv.setMaxExclusive(");
                    }
                    else {
                        max = xsReccD.getMaxInclusive();
                        jsc.add("org.exolab.castor.types.RecurringDuration max ="
                                +"org.exolab.castor.types.RecurringDuration.parseRecurring("
                                +"\""+max.toString()+"\");");
                        jsc.add("max.setDuration(\""+max.getDuration().toString()+"\");");
                        jsc.add("max.setPeriod(\""+max.getPeriod().toString()+"\");");
                        jsc.add("rv.setMaxInclusive(");
                    }
                    jsc.append("max");
                    jsc.append(");");
                    jsc.unindent();
                    jsc.add("} catch (java.text.ParseException e) {");
                    jsc.indent();
                    jsc.add("System.out.println(e);");
                    jsc.add("e.printStackTrace();");
                    jsc.add("return;");
                    jsc.unindent();
                    jsc.add("}");

                }//hasMaximum
                //-- pattern facet

                jsc.add("fieldValidator.setValidator(rv);");
                jsc.unindent();
                jsc.add("}");
                break;
            //-- RecurringDuration

            case XSType.TIME_DURATION:
                jsc.add("{ //-- local scope");
                jsc.indent();
                jsc.add("TimeDurationValidator tv = new TimeDurationValidator();");
                XSTimeDuration xsTimeD = (XSTimeDuration)xsType;
                if (xsTimeD.hasMinimum()) {
                    TimeDuration min = xsTimeD.getMinExclusive();
                    if (min != null)
                        jsc.add("tv.setMinExclusive(");
                    else {
                        min = xsTimeD.getMinInclusive();
                        jsc.add("tv.setMinInclusive(");
                    }
                    /* it is better for a good understanding to use
                    the parse method with 'min.toSring()' but in that case
                    we have to deal with the ParseException*/
                    jsc.append("new org.exolab.castor.types.TimeDuration("+min.toLong()+"L)");
                    jsc.append(");");
                }
                if (xsTimeD.hasMaximum()) {
                    TimeDuration max = xsTimeD.getMaxExclusive();
                    if (max != null)
                        jsc.add("tv.setMaxExclusive(");
                    else {
                        max = xsTimeD.getMaxInclusive();
                        jsc.add("tv.setMaxInclusive(");
                    }
                    /* it is better for a good understanding to use
                    the parse method woth 'min.toSring()' but in that case
                    we have to deal with the ParseException*/
                    jsc.append("new org.exolab.castor.types.TimeDuration("+max.toLong()+"L)");
                    jsc.append(");");
                }

                //-- pattern facet

                jsc.add("fieldValidator.setValidator(tv);");
                jsc.unindent();
                jsc.add("}");
                break;
            //-- TimeDuration

            default:
                break;
        }
    } //-- validationCode

    /**
     * Escapes special characters in the given String so that it can
     * be printed correctly.
     *
     * @param str the String to escape
     * @return the escaped String, or null if the given String was null.
    **/
    private static String escapePattern(String str) {
        if (str == null) return str;

        //-- make sure we have characters to escape
        if (str.indexOf('\\') < 0) return str;

        StringBuffer sb = new StringBuffer();
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch == '\\') sb.append(ch);
            sb.append(ch);
        }
        return sb.toString();

    } //-- escape

} //-- DescriptorSourceFactory
