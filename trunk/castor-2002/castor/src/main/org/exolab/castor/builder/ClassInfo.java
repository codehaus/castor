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

import org.exolab.castor.xml.schema.*;
import org.exolab.castor.xml.schema.types.*;
import org.exolab.castor.builder.types.*;
import org.exolab.castor.xml.JavaXMLNaming;
import org.exolab.castor.xml.Resolver;
import org.exolab.javasource.*;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class holds the necessary information
 * so that the source generator can properly create 
 * the necessary Classes for the Object model.
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class ClassInfo  {
    
    
    private Vector    elements       = null;
    private Vector    atts           = null;
    
    private String packageName = null;
    private String elementName = null;
    private String className = null;
    
    private String nsPrefix  = null;
    private String nsURI     = null;
    
    private boolean _allowTextContent = false;
    
    /**
     * The XML Schema type for this ClassInfo
    **/
    private XSType dataType = null;
    
    /**
     * A flag to signal derivation
    **/
    private boolean derived = false;
    
    /**
     * The ClassInfo with which this ClassInfo derives from
    **/
    private ClassInfo sourceInfo = null;

    private boolean _abstract = false;
    
    /**
     * Creates a new ClassInfo for the given XML Schema element declaration
     * @param element the XML Schema element declaration to create the 
     * ClassInfo for
     * @param resolver the ClassInfoResolver for resolving "derived" types.
    **/
    public ClassInfo(ElementDecl element, ClassInfoResolver resolver)
    {
        this.elementName = element.getName();
        this.nsPrefix    = element.getSchemaAbbrev();
        this.nsURI       = element.getSchemaName();
        this.className   = JavaXMLNaming.toJavaClassName(elementName);
        
        Archetype archetype = element.getArchetype();
        
        boolean derived = false;
        
        if (archetype != null) {
            init(archetype, resolver);
        }
        else {
            Datatype datatype = element.getDatatype();
            if (datatype != null) {
                init(datatype, resolver);
            }
            else {
                dataType = new XSClass(new JClass(this.className));
            }
                
        }
    } //-- ClassInfo
    
    /**
     * Creates a new ClassInfo for the given XML Schema element declaration
     * @param element the XML Schema element declaration to create the 
     * ClassInfo for
     * @param resolver the ClassInfoResolver for resolving "derived" types.
     * @param packageName the package to use when generating source
     * from this ClassInfo
    **/
    public ClassInfo
        (ElementDecl element, ClassInfoResolver resolver, String packageName) 
    {
        this(element, resolver);
        this.packageName = packageName;
    } //-- ClassInfo
    
    /**
     * Creates a new ClassInfo for the given XML Schema type declaration.
     * The type declaration must be a top-level declaration.
     * @param type the XML Schema type declaration to create the 
     * ClassInfo for
     * @param resolver the ClassInfoResolver for resolving "derived" types.
    **/
    public ClassInfo(Archetype type, ClassInfoResolver resolver) 
    {
        if (type == null)
            throw new IllegalArgumentException("null archetype");
            
        if (!type.isTopLevel())
            throw new IllegalArgumentException("Archetype is not top-level.");
            
        this._abstract = true;
        
        this.elementName = type.getName();
        this.className   = JavaXMLNaming.toJavaClassName(elementName);
        init(type, resolver);
        
    } //-- ClassInfo
    
    /**
     * Initializes this ClassInfo using the given Datatype
     * @param datatype the Datatype for this ClassInfo
     * @param resolver the ClassInfoResolver for resolving "derived" types.
    **/
    private void init(Datatype datatype, ClassInfoResolver resolver) {
        
        this.dataType = TypeConversion.convertType(datatype);
        if (datatype instanceof BuiltInType) return;
        
        //-- modify package name so we don't have
        //-- name collisions, since XML Schema uses
        //-- separate namespaces for elements and datatypes
        if (this.packageName == null)
            this.packageName = "types";
        else
            this.packageName += ".types";
            
        Schema schema = datatype.getSchema();
        
        if (schema != null) {
            this.nsURI = schema.getTargetNamespace();
        }
        
    } //-- init
    
    /**
     * Initializes this ClassInfo using the given Archetype
     * and makes the initial call to #process
     * @param archetype the Archetype for this ClassInfo
     * @param resolver the ClassInfoResolver for resolving "derived" types.
    **/    
    private void init(Archetype archetype, ClassInfoResolver resolver) {
        
        
        atts     = new Vector(3);
        elements = new Vector(5);
        
        this.dataType = new XSClass(new JClass(className));
        
        Schema schema = archetype.getSchema();
        
        if (schema != null) {
            this.nsURI = schema.getTargetNamespace();
        }
            
        //- Handle derived types
        if (archetype.getSource() != null) {
            derived = true;
            String sourceName = archetype.getSource();
            Archetype source = schema.getArchetype(sourceName);
            if (source != null) {
                
                ClassInfo classInfo = null;
                
                if (resolver != null) 
                    classInfo = resolver.resolve(source);
                    
                if (classInfo == null)
                    classInfo = new ClassInfo(source, resolver);
                
                if (resolver != null) 
                    resolver.bindReference(source, classInfo);
                    
                sourceInfo = classInfo;
                
                //-- copy members from super class
                addMembers(sourceInfo.getAttributeMembers());
                addMembers(sourceInfo.getElementMembers());
            }
            else {
                //-- will this ever be null, if we have a valid Schema?
                //-- ignore for now...but add comment in case we
                //-- ever see it.
                System.out.print("ClassInfo#init: ");
                System.out.print("A referenced archetype is null: ");
                System.out.println(sourceName);
            }
        }
        
        //---------------------/
        //- handle attributes -/
        //---------------------/
        //-- loop throug each attribute
        Enumeration enum = archetype.getAttributeDecls();
        while (enum.hasMoreElements()) {
            processAttribute((AttributeDecl)enum.nextElement());
        }
        //------------------------/
        //- handle content model -/
        //------------------------/
        //-- check contentType
        ContentType contentType = archetype.getContent();
            
        //-- create text member
        if ((contentType == ContentType.textOnly) ||
            (contentType == ContentType.mixed) ||
            (contentType == ContentType.any)) 
        {
            _allowTextContent = true;
                
            if (contentType == ContentType.any) {
                addMember(MemberFactory.createMemberForAny());
            }
                
        }
        process(archetype);
    } //-- init
    
    //------------------/
    //- Public Methods -/
    //------------------/
    
    /**
     * Adds the given SGMember to this ClassInfo
     * @param member the SGMember to add
    **/
    public void addMember(SGMember member) {
        if (member.getXMLNodeType() == SGMember.ATTRIBUTE) {
            if (atts == null) atts = new Vector(3);
            atts.addElement(member);
        }
        else {
            if (elements == null) elements = new Vector(5);
            elements.addElement(member);
        }
    } //-- addMember
    
    /**
     * Adds the given set of SGMembers to this ClassInfo
     * @param members an Array of SGMember objects
    **/
    public void addMembers(SGMember[] members) {
        for (int i = 0; i < members.length; i++)
            addMember(members[i]);
    } //-- addMembers
    
    /**
     * @return true if Classes created with this ClassInfo allow
     * text content
    **/
    public boolean allowsTextContent() {
        return this._allowTextContent;
    } //-- allowsTextContent
    
    /**
     * Returns true if the given member is a member of this ClassInfo
     * @return true if the given member is a member of this ClassInfo
    **/
    public boolean contains(SGMember member) {
        boolean val = false;
        
        if (atts != null) 
            if (atts.contains(member)) return true;
            
        if (elements != null) 
            if (elements.contains(member)) return true;
            
        if (sourceInfo != null) 
            return sourceInfo.contains(member);
            
        return false;
    } //-- contains
    
    /**
     * @return an array of attribute members
    **/
    public SGMember[] getAttributeMembers() {
        SGMember[] members = null;
        if (atts != null) {
            members = new SGMember[atts.size()];
            atts.copyInto(members);
        }
        else members = new SGMember[0];
        return members;
    } //-- getAttributeMembers
    
    /**
     * Returns the class name for this ClassInfo
     * @return the class name that should be used when creating classes
     * from this ClassInfo
    **/
    public String getClassName() {
        return this.className;
    } //-- getClassName
    
    /**
     * Returns the XML Schema data type for this ClassInfo
     * @return the XML Schema data type for this ClassInfo
    **/
    public XSType getDataType() {
        return dataType;
    } //-- XSType
    
    /**
     * Returns the XML element name
     * @return the XML element name
    **/
    public String getElementName() {
        return this.elementName;
    } //-- getElementName
    
    /**
     * @return an array of element members
    **/
    public SGMember[] getElementMembers() {
        SGMember[] members = null;
        if (elements != null) {
            members = new SGMember[elements.size()];
            elements.copyInto(members);
        }
        else members = new SGMember[0];
        return members;
    } //-- getElementMembers
    
    /**
     * @return the namespace prefix to use when marshalling as XML.
    **/
    public String getNameSpacePrefix() {
        return nsPrefix;
    } //-- getNameSpacePrefix
    
    /**
     * @return the namespace URI used when marshalling and unmarshalling as XML.
    **/
    public String getNameSpaceURI() {
        return nsURI;
    } //-- getNameSpaceURI
    
    /**
     * Returns the package name for this ClassInfo
     * @return the package name that should be used when creating classes
     * from this ClassInfo
    **/
    public String getPackageName() {
        return this.packageName;
    } //-- getPackageName
    
    /**
     * Returns, if necessary, the ClassInfo in which this ClassInfo
     * derives from
     * @return the ClassInfo in which this ClassInfo derives from
    **/
    public ClassInfo getSuperClassInfo() {
        return sourceInfo;
    } //-- getSuperClassName
    
    /**
     * Returns true if the class created by using this ClassInfo should 
     * be declared as abstract, otherwise false
     * @return true if the class created by using this ClassInfo should 
     * be declared as abstract, otherwise false
    **/
    public boolean isAbstract() {
        return this._abstract;
    } //-- isAbstract
    
    /**
     * Returns true if this Class created by this ClassInfo
     * is "derived" from another class, otherwise false.
     * @return true if this Class created by this ClassInfo
     * is "derived" from another class, otherwise false.
    **/
    public boolean isDerived() {
        return derived;
    } //-- isDerived
    
    /**
     * Returns true if the given member was derived from another ClassInfo,
     * otherwise false if the member is local.
     * @return true if the given member was derived from another ClassInfo,
     * otherwise false will be returned
    **/
    public boolean isDerived(SGMember member) {
        if ((member == null) || (sourceInfo == null)) 
            return false;
            
        return sourceInfo.contains(member);
    } //-- isDerived
    
    /**
     * Sets the class name for this SGClass
     * @param className the name to use for class name
     * @exception IllegalArgumentException when the given className is not
     * a valid Java Class Name
    **/
    public void setClassName(String className) {
        this.className = className;
    } //-- setClassName
    
    /**
     * Sets the package name for this ClassInfo
     * @param packageName the package name to use for this ClassInfo
    **/
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    } //-- setPackageName
    
    //-------------------/        
    //- Private Methods -/        
    //-------------------/        
        
    /**
     * Processes the given XML attribute declaration and adds
     * an SGMember to this ClassInfo for the declaration
     * @param attribute the XML attribute declaration to process
    **/
    private void processAttribute(AttributeDecl attribute) {
        
        String typeRef = attribute.getDatatypeRef();
        
        String memberName = "v"+
            JavaXMLNaming.toJavaClassName(attribute.getName());
            
        SGMember member = null;
                
        Datatype datatype = attribute.getDatatype();
        XSType   xsType = null;
        
        if (datatype != null)
            xsType = TypeConversion.convertType(datatype);
        else
            xsType = new XSString();
            
        switch (xsType.getType()) {
            case XSType.INTEGER:
                member = new SGMember(xsType, memberName);
                member.setXMLNodeType(SGMember.ATTRIBUTE);
                
                member.setCodeHelper(
                    new IntegerCodeHelper((XSInteger)xsType)
                );
                    
                break;
            case XSType.ID:
                member = new SGId(memberName);
                break;
            case XSType.IDREF:
                member = new SGIdRef(memberName);
                break;
            default:
                member = new SGMember(xsType, memberName);
                member.setXMLNodeType(SGMember.ATTRIBUTE);
                break;
        }
        member.setSchemaType(attribute.getDatatypeRef());
        member.setXMLName(attribute.getName());
        member.setRequired(attribute.getRequired());
        atts.addElement(member);
    } //-- processAttributes
    
    /**
     * Processes the given ContentModelGroup into a set of members
     * for this ClassInfo
     * @param contentModel the ContentModelGroup to process
    **/
    private void process(ContentModelGroup contentModel) {
        
        //------------------------------/
        //- handle elements and groups -/
        //------------------------------/
                
        Enumeration enum = contentModel.enumerate();
                
        SGMember member = null;
        while (enum.hasMoreElements()) {
                    
            Structure struct = (Structure)enum.nextElement();
            switch(struct.getStructureType()) {
                case Structure.ELEMENT:
                    member = MemberFactory.createMember((ElementDecl)struct);
                    addMember(member);
                    break;
                case Structure.GROUP:
                    process((Group)struct);
                    break;
                default:
                    break;
            }
        }
            
    }
    
} //-- ClassInfo
