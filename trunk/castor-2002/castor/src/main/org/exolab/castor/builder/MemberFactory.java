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

package org.exolab.castor.builder;

import org.exolab.castor.builder.types.*;
import org.exolab.castor.xml.JavaXMLNaming;
import org.exolab.castor.xml.schema.*;
import org.exolab.castor.xml.schema.types.BuiltInType;

import org.exolab.javasource.JClass;
import org.exolab.javasource.JSourceCode;
import org.exolab.javasource.JType;

import java.util.Enumeration;

/**
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class MemberFactory {
    

    /** 
     * The FieldInfo factory.
    */
    private FieldInfoFactory infoFactory = null;

    
    /** Constructor that creates a default type factory.
    */
    public MemberFactory() {
        this(new FieldInfoFactory());
    } //-- MemberFactory
    

    /** 
     * Creates a new MemberFactory using the given FieldInfo factory.
     * @param infoFactory the FieldInfoFactory to use
    **/
    public MemberFactory(FieldInfoFactory infoFactory) 
    {
        super();    
        
        //this.resolver = resolver;
        
        if (infoFactory == null)
            this.infoFactory = new FieldInfoFactory();
        else
            this.infoFactory = infoFactory;
            
            
    } //-- MemberFactory
    

    /**
     * Creates a FieldInfo for content models that support "any" element.
     * @return the new FieldInfo
    **/
    public FieldInfo createFieldInfoForAny() {
        
        XSType xsType = new XSClass(SGTypes.Object, "any");
        String memberName = "obj";
        String vName = "_anyObject";
        String xmlName = "object";
        CollectionInfo cInfo = infoFactory.createCollection(xsType, vName, xmlName);
        XSList xsList = cInfo.getXSList();
        xsList.setMinimumSize(0);
        cInfo.setRequired(false);
        cInfo.setNodeName(xmlName);
        return cInfo;
    } //-- createFieldInfoForAny()
        
    /**
     * Creates a FieldInfo for Text content
     * @return the new FieldInfo
    **/
    public FieldInfo createFieldInfoForText() {
        
        XSType xsType = new XSString();
        String fieldName = "_content";
        FieldInfo fInfo = infoFactory.createFieldInfo(new XSString(),fieldName);
        fInfo.setNodeType(XMLInfo.TEXT_TYPE);
        fInfo.setComment("internal character storage");
        fInfo.setRequired(false);
        fInfo.setNodeName("#text");
        fInfo.setDefaultValue("\"\"");
        return fInfo;
        
    } //-- createFieldInfoForText
    
    
    /**
     * Creates a FieldInfo object for the given attribute
     * declaration
     * @param attribute the XML attribute declaration to create the
     * FieldInfo for
     * @return the FieldInfo for the given attribute declaration
    **/
    public FieldInfo createFieldInfo
        (AttributeDecl attribute, ClassInfoResolver resolver) 
    {
        
        String memberName 
            = JavaXMLNaming.toJavaMemberName(attribute.getName());
            
        if (!memberName.startsWith("_")) 
            memberName = "_"+memberName;
            
        FieldInfo fieldInfo = null;
                
        Datatype datatype = attribute.getDatatype();
        XSType   xsType = null;
        
        boolean enumeration = false;
        
        if (datatype != null) {
            
            if (datatype.hasFacet(Facet.ENUMERATION)) {
                enumeration = true;
            
                //-- LOok FoR CLasSiNfO iF ReSoLvR is NoT NuLL
                ClassInfo cInfo = null;
                if (resolver != null) {
                    cInfo = resolver.resolve(datatype);
                }
            
                if (cInfo != null)
                    xsType = cInfo.getSchemaType();
            }
            else xsType = TypeConversion.convertType(datatype);
        }
        else
            xsType = new XSString();
            
        switch (xsType.getType()) {
            case XSType.INTEGER:
                fieldInfo = infoFactory.createFieldInfo(xsType, memberName);
                fieldInfo.setCodeHelper(
                    new IntegerCodeHelper((XSInteger)xsType)
                );
                    
                break;
            case XSType.ID:
                fieldInfo = infoFactory.createIdentity(memberName);
                break;
            default:
                fieldInfo = infoFactory.createFieldInfo(xsType, memberName);
                break;
        }
        
        fieldInfo.setNodeName(attribute.getName());
        fieldInfo.setNodeType(XMLInfo.ATTRIBUTE_TYPE);
        fieldInfo.setRequired(attribute.getRequired());
        
        if (xsType.getType() == XSType.STRING) {
            String def = attribute.getDefault();
            if ((def != null) && (def.length() > 0)) {
                char ch = def.charAt(0);
                switch (ch) {
                    case '\'':
                    case '\"':
                        fieldInfo.setDefaultValue(def);
                        break;
                    default:
                        fieldInfo.setDefaultValue("\"" + def + "\"");
                        break;
                }
            }
        }
        else fieldInfo.setDefaultValue(attribute.getDefault());
        
        //fieldInfo.setSchemaType(attribute.getDatatypeRef());
        
        //-- add annotated comments
        String comment = createComment(attribute);
        if (comment != null) fieldInfo.setComment(comment);
        
        return fieldInfo;
    } //-- createFieldInfo
        
    /**
     * Creates a member based on the given ElementDecl
     * @param element the ElementDecl to create the member from
    **/
    public FieldInfo createFieldInfo
        (ElementDecl element, ClassInfoResolver resolver) 
    {
        
        //-- check whether this should be a Vector or not
        int maxOccurs = element.getMaximumOccurance();
        int minOccurs = element.getMinimumOccurance();
        
        ElementDecl eDecl = element;
        if (eDecl.isReference()) {
            ElementDecl eRef = eDecl.getReference();
            if (eRef == null) {
                String err = "unable to resolve element reference: ";
                err += element.getName();
                System.out.println(err);
                return null;
            }
            else eDecl = eRef;
        }
                
        //-- determine type
                
        JSourceCode jsc     = null;
        FieldInfo fieldInfo = null;
        XSType   xsType     = null;
        
        Datatype datatype = eDecl.getDatatype();
        if (datatype != null) {
            
            //-- handle special case for enumerated types
            if (datatype.hasFacet(Facet.ENUMERATION)) {
                //-- LOok FoR CLasSiNfO iF ReSoLvR is NoT NuLL
                ClassInfo cInfo = null;
                if (resolver != null) {
                    cInfo = resolver.resolve(datatype);
                }
                
                if (cInfo != null)
                    xsType = cInfo.getSchemaType();
            }
            else xsType = TypeConversion.convertType(datatype);
        }
        else {
            String className = JavaXMLNaming.toJavaClassName(eDecl.getName());
            xsType = new XSClass(new JClass(className));
        }
                
        String fieldName = JavaXMLNaming.toJavaMemberName(eDecl.getName());
        if (fieldName.charAt(0) != '_') 
            fieldName = "_"+fieldName;
                
        if (maxOccurs != 1) {
            String vName = fieldName+"List";
            CollectionInfo cInfo 
                = infoFactory.createCollection(xsType, vName, eDecl.getName());
                
            XSList xsList = cInfo.getXSList();
            xsList.setMaximumSize(maxOccurs);
            xsList.setMinimumSize(minOccurs);
            fieldInfo = cInfo;
                    
        }
        else {
            fieldInfo = infoFactory.createFieldInfo(xsType, fieldName);
        }
        fieldInfo.setRequired(minOccurs > 0);
        fieldInfo.setNodeName(eDecl.getName());
        
        //-- add annotated comments
        
        //-- use elementRef first if necessary
        String comment = null;
        Enumeration enum = element.getAnnotations();
        if (enum.hasMoreElements())
            comment = createComment((Annotation)enum.nextElement());
        else {
            comment = createComment(eDecl);
        }
        
        if (comment != null) fieldInfo.setComment(comment);
        
        
        return fieldInfo;
    } //-- createFieldInfo(ElementDecl)
        
    /**
     * Creates a comment to be used in Javadoc from
     * the given Annotated Structure.
     * @param annotated the Annotated structure to process
     * @return the generated comment
    **/
    private String createComment(Annotated annotated) {
        
        //-- process annotations
        Enumeration enum = annotated.getAnnotations();
        if (enum.hasMoreElements()) {
            //-- just use first annotation
            return createComment((Annotation) enum.nextElement());
        }
        return null;
    } //-- createComment
        
    /**
     * Creates a comment to be used in Javadoc from the given Annotation
     * @param annotation the Annotation to create the comment from
     * @return the generated comment
    **/
    private String createComment(Annotation annotation) {        
        if (annotation == null) return null;
        Enumeration enum = annotation.getInfo();        
        if (enum.hasMoreElements()) {
            //-- just use first <info>
            Info info = (Info) enum.nextElement();
            return info.getContent();
        }
        return null;
    } //-- createComment
        
} //-- MemberFactory