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
 * Copyright 2000 (C) Intalio Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.xml.schema.reader;

//-- imported classes and packages
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.schema.*;
import org.xml.sax.*;

/**
 * A class for Unmarshalling extension elements
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class ExtensionUnmarshaller extends SaxUnmarshaller {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * The current SaxUnmarshaller
    **/
    private SaxUnmarshaller unmarshaller;

    /**
     * The current branch depth
    **/
    private int depth = 0;

    /**
     * The Attribute reference for the Attribute we are constructing
    **/
    private ComplexType _complexType = null;
    private Schema      _schema      = null;

    private boolean foundAnnotation  = false;
    private boolean foundAnyAttribute = false;
    private boolean foundAttributes  = false;
    private boolean foundModelGroup  = false;


      //----------------/
     //- Constructors -/
    //----------------/

    /**
     * Creates a new ExtensionUnmarshaller
     * @param complexType the ComplexType being unmarshalled
     * @param atts the AttributeList
     * @param resolver the resolver being used for reference resolving
    **/
    public ExtensionUnmarshaller
        (ComplexType complexType, AttributeList atts, Resolver resolver)
        throws SAXException
    {
        super();

        setResolver(resolver);

        _complexType = complexType;
        _schema      = complexType.getSchema();

        _complexType.setDerivationMethod(SchemaNames.EXTENSION);

        //-- base
        String base = atts.getValue(SchemaNames.BASE_ATTR);
        if ((base != null) && (base.length() > 0)) {

            XMLType baseType= _schema.getType(base);
		    if (baseType == null)
                _complexType.setBase(base); //the base type has not been read
		    else {
				 //--we cannot extend a simpleType in <complexContent>
				 if ( (baseType.isSimpleType()) &&
					  (_complexType.isComplexContent()) ) {
					String err = "In a 'complexContent', the base attribute "+
                    "must be a complexType but "+ base+" is a simpleType.\n";
                    error(err);
				 }
                _complexType.setBaseType(baseType);
		    }

        }

    } //-- ExtensionUnmarshaller

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the name of the element that this SaxUnmarshaller
     * handles
     * @return the name of the element that this SaxUnmarshaller
     * handles
    **/
    public String elementName() {
        return SchemaNames.EXTENSION;
    } //-- elementName

    /**
     * Returns the Object created by this SaxUnmarshaller
     * @return the Object created by this SaxUnmarshaller
    **/
    public Object getObject() {
        return null;
    } //-- getObject

    /**
     * @param name
     * @param atts
     * @see org.xml.sax.DocumentHandler
    **/
    public void startElement(String name, AttributeList atts)
        throws org.xml.sax.SAXException
    {
        //-- Do delagation if necessary
        if (unmarshaller != null) {
            unmarshaller.startElement(name, atts);
            ++depth;
            return;
        }

          //-- <anyAttribute>
        if (SchemaNames.ANY_ATTRIBUTE.equals(name)) {
           foundAnyAttribute = true;
            unmarshaller
                 = new WildcardUnmarshaller(_complexType, _schema, name, atts, getResolver());
        }

        //-- attribute declarations
        else if (SchemaNames.ATTRIBUTE.equals(name)) {
            foundAttributes = true;
            unmarshaller
                = new AttributeUnmarshaller(_schema, atts, getResolver());
        }
        //-- attribute group declarations
        else if (SchemaNames.ATTRIBUTE_GROUP.equals(name)) {

            //-- make sure we have an attribute group
            //-- reference and not a definition

            if (atts.getValue(SchemaNames.REF_ATTR) == null) {
                String err = "A complexType may contain referring "+
                    "attribute groups, but not defining ones.";
                error(err);
            }

            foundAttributes = true;
            unmarshaller
                = new AttributeGroupUnmarshaller(_schema, atts);
        }
        else if (SchemaNames.isGroupName(name)) {
            if (foundAttributes)
                error("'"+name+"' must appear before attribute "+
                    "definitions in an 'extension' element.");

            if (foundModelGroup)
                error("'"+name+"' cannot appear as a child of 'extension' "+
                    "if another 'all', 'sequence', 'choice' or "+
                    "'group' already exists.");

            if (_complexType.isSimpleContent())
                error("'"+name+"' may not appear in a 'extension' of "+
                    "'simpleContent'.");

            foundModelGroup = true;
            unmarshaller
                = new GroupUnmarshaller(_schema, name, atts, getResolver());
        }
        //-- element declarations
        else if (SchemaNames.ANY_ATTRIBUTE.equals(name)) {
            //-- not yet supported....
            error("anyAttribute is not yet supported.");
        }
        else if (name.equals(SchemaNames.ANNOTATION)) {
            if (foundAttributes || foundModelGroup)
                error("An annotation must appear as the first child of an " +
                    "'extension' element.");

            if (foundAnnotation)
                error("Only one (1) annotation may appear as the child of "+
                    "an 'extension' element.");

            foundAnnotation = true;
            unmarshaller = new AnnotationUnmarshaller(atts);
        }
        else illegalElement(name);

        unmarshaller.setDocumentLocator(getDocumentLocator());
    } //-- startElement

    /**
     *
     * @param name
    **/
    public void endElement(String name)
        throws org.xml.sax.SAXException
    {

        //-- Do delagation if necessary
        if ((unmarshaller != null) && (depth > 0)) {
            unmarshaller.endElement(name);
            --depth;
            return;
        }

        //-- have unmarshaller perform any necessary clean up
        unmarshaller.finish();

        //-- <anyAttribute>
        if (SchemaNames.ANY_ATTRIBUTE.equals(name)) {
            Wildcard wildcard =
                 ((WildcardUnmarshaller)unmarshaller).getWildcard();
            try {
                _complexType.setAnyAttribute(wildcard);
            } catch (SchemaException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        //-- attribute declarations
        else if (SchemaNames.ATTRIBUTE.equals(name)) {
            AttributeDecl attrDecl =
                ((AttributeUnmarshaller)unmarshaller).getAttribute();

            _complexType.addAttributeDecl(attrDecl);
        }
        //-- attribute groups
        else if (SchemaNames.ATTRIBUTE_GROUP.equals(name)) {
            AttributeGroupReference attrGroupRef =
                (AttributeGroupReference) unmarshaller.getObject();
            _complexType.addAttributeGroupReference(attrGroupRef);
        }
        //-- groups
        else if (SchemaNames.isGroupName(name)) {
            Group group = ((GroupUnmarshaller)unmarshaller).getGroup();
            _complexType.addGroup(group);
        }
        //-- annotation
        else if (SchemaNames.ANNOTATION.equals(name)) {
            Annotation ann = ((AnnotationUnmarshaller)unmarshaller).getAnnotation();
            _complexType.addAnnotation(ann);
        }

        unmarshaller = null;
    } //-- endElement

    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        //-- Do delagation if necessary
        if (unmarshaller != null) {
            unmarshaller.characters(ch, start, length);
        }
    } //-- characters

} //-- ExtensionUnmarshaller
