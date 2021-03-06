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
 * Copyright 2002-2003 (C) Intalio Inc. All Rights Reserved.
 *
 * $Id$
 */

package org.exolab.castor.builder.binding;

import org.exolab.castor.builder.BindingComponent;
import org.exolab.castor.builder.GroupNaming;
import org.exolab.castor.builder.SourceGenerator;
import org.exolab.castor.builder.TypeConversion;
import org.exolab.castor.builder.types.XSClass;
import org.exolab.castor.builder.types.XSType;
import org.exolab.castor.xml.JavaNaming;
import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Form;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.ModelGroup;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.XMLType;
import org.exolab.javasource.JClass;

/**
 * <p>This class is the implementation of BindingComponent from an XML Schema
 * point of view. This specific implementation wraps an XML Schema annotated structure.
 *
 * <p>The XML Schema structure can be only of four different types:
 * <ul>
 *     <li>Element: it represents an XML Schema element.</li>
 *     <li>ComplexType: it represents an XML Schema complexType.</li>
 *     <li>ModelGroup: it represents an XML Schema Model group definition.</li>
 *     <li>Group: it represents an XML Schema Model Group.</li>
 * </ul>
 *
 * <p>The three first items can be customized using a binding file.
 * Thus the XMLBindingComponent class takes into account the presence or not
 * of a custom binding document in the computation of the needed information for the Source Generator
 * to generate java classes from an XML Schema.
 * <p>The customizable items are detailled in the binding file documentation.
 *
 * <p>This class acts like a <i>window</i> on a particular XML Schema structure that the user controls
 * by changing the view on the Annotated Structure he is interested in.
 *
 * @todo: add the link to the documentation.
 *
 * @see org.exolab.castor.builder.BindingComponent

 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision$ $Date$
 */
public class XMLBindingComponent implements BindingComponent {


   /**
    * The Extended Binding used to retrieve the ComponentBindingType
    */
   private ExtendedBinding _binding;

   /**
    * The binding component to use, if no binding component is present then
    * the default behavior applies.
    */
    private ComponentBindingType _compBinding;

    /**
     * The XML Schema Annotated structure encapsulated in that XMLBinding object
     */
    private Annotated _annotated;

    /**
     * The prefix used to generate the java names
     */
    private String _prefix;

    /**
     * The suffix used to generate the java names
     */
    private String _suffix;

    /**
	 * The type of the XMLBinding. -1 is no component binding have been defined.
	 */
	private short _type = -1;


    /**
     * caches of several computations
     */
    private int _hashCode =-1;
    private String _javaClassName = null;
    private String _javaMemberName = null;
    private String _javaPackage = null;
    private FieldType _member = null;
    private ClassType _class = null;
    private Interface _interface = null;
    private Schema _schema = null;

    /**
     * A GroupNaming helper class used to named anonymous groups
     */
    private static GroupNaming _groupNaming = null;
    static {
        _groupNaming = new GroupNaming();
    }


    /**
     * Constructs an XMLBindingComponent from an XML Schema Component.
     *
     */
    public XMLBindingComponent() {
    }


    /**
     * Returns the Binding Object Model on which this XMLBindingComponent 
     * will query information.
     *
     * @return the Extended Binding Object Model that wraps the information located in a
     * binding file
     */
    public ExtendedBinding getBinding() {
        return _binding;
    } //-- getBinding

    /**
     * Sets the Binding Object Model on which this XMLBindingComponent will query information.
     *
     * @param binding the Extended Binding Object Model that wraps the information located in a
     * binding file
     */
    public void setBinding(ExtendedBinding binding) {
        _binding = binding;
    }

   /**
    * <p>Sets the <i>window</i> on the given Annotated XML Schema structure.
    * Once the window is set on a particular XML Schema structure all the information
    * returned by this class are relative to that XML Schema structure.
    *
    * @param annotated an Annotated XML Schema structure.
    * @see org.exolab.castor.xml.schema.Annotated
    */
    public void setView(Annotated annotated) {
        if (annotated == null)
            throw new IllegalArgumentException("The XML Schema annotated structure is null.");
        _annotated = annotated;

        //--reset all the variables
        int _hashCode =-1;
        _javaClassName = null;
        _javaMemberName = null;
        _javaPackage = null;
        _schema = null;
        _member = null;
        _class = null;
        _interface = null;
        _type = -1;
        _prefix = null;
        _suffix = null;
        
        
        //--look up for the particular componentBinding relative to the
        //-- given annotated structure
        if (_binding != null) {
            _compBinding = _binding.getComponentBindingType(annotated);
                NamingXMLType naming = _binding.getNamingXML();
                if (naming != null) {
                    
                    switch (annotated.getStructureType()) {
                        case Structure.COMPLEX_TYPE:
                            if (naming.getComplexTypeName() != null) {
                                _prefix = naming.getComplexTypeName().getPrefix();
                                _suffix = naming.getComplexTypeName().getSuffix();
                            }
                            break;
                        case Structure.ELEMENT:
                            if (naming.getElementName() != null) {
                               _prefix = naming.getElementName().getPrefix();
                                _suffix = naming.getElementName().getSuffix();
                            }
                            break;
                        case Structure.MODELGROUP:
                            if (naming.getModelGroupName() != null) {
                                _prefix = naming.getModelGroupName().getPrefix();
                                _suffix = naming.getModelGroupName().getSuffix();
                            }
                            break;
                        default:
                            break;
                    }
                }//--naming != null;
        }//--binding != null

        if (_compBinding != null) {
            ComponentBindingTypeChoice choice = _compBinding.getComponentBindingTypeChoice();
            if (choice.getInterface() != null) {
                _type = INTERFACE;
                _interface = choice.getInterface();
            }
            else if (choice.getJavaClass() != null) {
                _type = CLASS;
                _class = choice.getJavaClass();
            }
            else if (choice.getMember() != null) {
                _type = MEMBER;
                _member = choice.getMember();
            } else {
                String err = "Illegal Binding component:";
                err += "it does not define a class, an interface or a member binding.";
                throw new IllegalStateException(err);
            }
        }
        
   }//--setView



   //--Object manipulation methods

   /**
    * Returns true if the given Object is equal to this instance of XMLBindingComponent.
    *
    * @return true if the given Object is equal to this instance of XMLBindingComponent.
    * @see java.lang.Object#equals(java.lang.Object)
    */
    public boolean equals(Object object) {

        if (object == null)
            return false;

        boolean result = false;
        if (object instanceof XMLBindingComponent) {
            XMLBindingComponent temp = (XMLBindingComponent)object;
            result = _annotated.equals(temp.getAnnotated());
            if (_compBinding != null) {
                if (temp.getComponentBinding() != null)
                   result = result && (_compBinding.equals(temp.getComponentBinding()));
                else
                    result = false;
            }
            else if (temp.getComponentBinding() != null) {
                result = false;
            }
        }
        else
           result = false;
        return result;
    }

    /**
     * Returns the hashCode value for this object.
     *
     * @return the hashcode value for this object.
     * @see java.lang.hashCode()
     */
    public int hashCode() {

        if (_hashCode == -1) {
            int compBindingHash = 0;
            if (_compBinding != null) {
               compBindingHash = _compBinding.getName().hashCode();
            }
            //WARNING: THE CASTOR SOM doesn't override hashCode or equals
            _hashCode = 37*(_annotated.hashCode()) + compBindingHash;
        }
        return _hashCode;
    }

    /**
    * Returns the ComponentBinding used in that XMLBindingComponent
    * to retrieve customized information.
    *
    * @return the ComponentBinding used in that XMLBinding.
    */
    protected ComponentBindingType getComponentBinding() {
        return _compBinding;
    }


    //--XML Schema information methods
   /**
    * Returns the XML Schema annotated structure used in this XMLBindingComponent.
    *
    * @return the XML Schema annotated structure used in this XMLBindingComponent.
    */
    public Annotated getAnnotated() {
        return _annotated;
    }



   /**
    * Returns true if the binding of this XMLBindingComponent will require
    * the generation of 2 java classes.
    * Indeed an a nested Model Group that can occur more than once is described
    * by the SourceGenerator with a wrapper class.
    *
    * @return true if the binding of this XMLBindingComponent will require
    * the generation of 2 java classes.
    */
    public boolean createGroupItem() {
        int maxOccurs = 0;
        boolean result = false;
        switch (_annotated.getStructureType()) {
            case Structure.ELEMENT:
                XMLType type = ((ElementDecl)_annotated).getType();
                if (type.isComplexType()) {
                    maxOccurs = ((ComplexType)type).getMaxOccurs();
                    if (((maxOccurs > 1) || (maxOccurs < 0)) && (type.getName() == null))
                        result = true;
                }

                break;
            case Structure.COMPLEX_TYPE:
                    maxOccurs = ((ComplexType)_annotated).getMaxOccurs();
                    if ((maxOccurs > 1) || (maxOccurs < 0))
                        result = true;
                    break;
            case Structure.MODELGROUP:
            case Structure.GROUP:
                Group group = (Group)_annotated;
                maxOccurs = group.getMaxOccurs();
                if ((maxOccurs > 1) || (maxOccurs < 0))
                    result = true;
                break;
            case Structure.ATTRIBUTE:
            default:
                break;

        }
        return result;
    }

    /**
     * Returns the schemaLocation of the parent schema of the wrapped structure.
     *
     * @return the schemaLocation of the parent schema of the wrapped structure.
     */
    public String getSchemaLocation() {
        String location = null;
        Schema schema = getSchema();
        if (schema != null)
            location = schema.getSchemaLocation();

        return location;
    }

    /**
     * Returns the targetNamespace of the parent schema of the wrapped structure.
     *
     * @return the targetNamespace of the parent schema of the wrapped structure.
     */
    public String getTargetNamespace() {
        String result = null;
        Schema schema = null;
        Form form = null;
        
        switch (_annotated.getStructureType()) {
            case Structure.ATTRIBUTE:
                AttributeDecl attribute = (AttributeDecl)_annotated;
                //-- resolve reference
                if (attribute.isReference())
                    attribute = attribute.getReference();
                
                schema = attribute.getSchema();
                
                //-- top-level (use targetNamespace of schema)
                if (attribute.getParent() == schema)
                    break;
                
                //-- check form (qualified or unqualified)
                form = attribute.getForm();
                if (form == null) {
                    form = schema.getAttributeFormDefault();
                }
                if ((form == null) || form.isUnqualified()) {
                    //-- no targetNamespace by default
                    return null;
                }
                //-- use targetNamespace of schema
                break;
            case Structure.ELEMENT:
                //--resolve reference?
                ElementDecl element = (ElementDecl)_annotated;
                if (element.isReference())
                    element = element.getReference();
                 
                schema = element.getSchema();
                //-- top-level (use targetNamespace of schema)
                if (element.getParent() == schema)
                    break;
                
                //-- check form (qualified or unqualified)
                form = element.getForm();
                if (form == null) {
                    form = schema.getElementFormDefault();
                }
                if ((form == null) || form.isUnqualified()) {
                    //-- no targetNamespace by default
                    return null;
                }
                //-- use targetNamespace of schema
                break;
            default:
                break;
        }
        if (schema == null) 
            schema = getSchema();
        if (schema != null)
            result = schema.getTargetNamespace();
        return result;
    } //-- getTargetNamespace

    /**
     * Returns the underlying Schema of the wrapped structure.
     *
     * @return the parent schema of the wrapped structure.
     */
    public Schema getSchema() {
        if (_schema == null) {

            switch (_annotated.getStructureType()) {
                case Structure.ATTRIBUTE:
                    //--resolve reference?
                    AttributeDecl attribute = (AttributeDecl)_annotated;
                    if (attribute.isReference())
                        attribute = attribute.getReference();
                
                    _schema = attribute.getSchema();
                    attribute = null;
                    break;
                case Structure.ELEMENT:
                    //--resolve reference?
                    ElementDecl element = (ElementDecl)_annotated;
                    if (element.isReference())
                        element = element.getReference();
                    _schema = element.getSchema();
                    element = null;
                    break;
                case Structure.COMPLEX_TYPE:
                    _schema = ((ComplexType)_annotated).getSchema();
                    break;
                case Structure.MODELGROUP:
                    //--resolve reference?
                    ModelGroup group = (ModelGroup)_annotated;
                    if (group.isReference())
                        group = group.getReference();
                
                    _schema = group.getSchema();
                    group = null;
                    break;
                case Structure.GROUP:
                    Structure parent = ((Group)_annotated).getParent();
                    short structure = parent.getStructureType();
                    while (structure == Structure.GROUP) {
                        parent = ((Group)parent).getParent();
                        structure = parent.getStructureType();
                    }
                    if (structure == Structure.COMPLEX_TYPE)
                        _schema = ((ComplexType)parent).getSchema();
                    else if (structure == Structure.MODELGROUP)
                        _schema = ((ModelGroup)parent).getSchema();
                    break;
                case Structure.SIMPLE_TYPE:
                case Structure.UNION:
                    _schema = ((SimpleType)_annotated).getSchema();
                    break;
                default:
                    break;
            }
        }
        
        return _schema;
    }

   /**
    * <p>Returns the XMLType of the underlying structure. The XMLType of an
    * element being its XML Schema type, the XMLType of a ComplexType being itself
    * and the XMLType of an attribute being its XML Schema simpleType.
    * Null is returned for a Model Group.
    *
    * @return the XMLType of the underlying structure.
    */
    public XMLType getXMLType() {
        XMLType result = null;
        switch (_annotated.getStructureType()) {
            case Structure.ELEMENT:
                result = ((ElementDecl)_annotated).getType();
                break;
            case Structure.COMPLEX_TYPE:
                result = ((ComplexType)_annotated);
                break;
            case Structure.ATTRIBUTE:
                result = ((AttributeDecl)_annotated).getSimpleType();
                break;
            case Structure.MODELGROUP:
            default:
                break;
        }
        return result;
    }

    /**
    * Returns the XML name declared in the XML Schema for this XMLBindingComponent.
    *
    * @return the XML name declared in the XML Schema for this XMLBindingComponent.
    */
    public String getXMLName() {
        String result = null;

        switch (_annotated.getStructureType()) {
            case Structure.ELEMENT:
                result = ((ElementDecl)_annotated).getName();
                break;
            case Structure.COMPLEX_TYPE:
                result = ((ComplexType)_annotated).getName();
                break;
            case Structure.ATTRIBUTE:
                result = ((AttributeDecl)_annotated).getName();
                break;
            case Structure.MODELGROUP:
            case Structure.GROUP:
                result = ((Group)_annotated).getName();
                break;
            default:
                break;

        }
        return result;
    }


    //--Implementation of BindingComponent

    /**
     * Returns the value specified in the XML Schema for the XML Schema component
     * wrapped in this XMLBindingComponent.
     * The value returned is the <i>default</i> or <i>fixed</i> value for an Element
     * or an Attribute.
     *
     * @return the value specified in the XML Schema for the XML Schema annotated structure
     * wrapped in this XMLBindingComponent.
     */
    public String getValue() {
        String result = null;
        switch (_annotated.getStructureType()) {
            case Structure.ELEMENT:
                result = ((ElementDecl)_annotated).getDefaultValue();
                if (result == null)
                    result = ((ElementDecl)_annotated).getFixedValue();
                break;
            case Structure.ATTRIBUTE:
                result = ((AttributeDecl)_annotated).getDefaultValue();
                if (result == null)
                    result = ((AttributeDecl)_annotated).getFixedValue();
                break;
            case Structure.COMPLEX_TYPE:
            case Structure.MODELGROUP:
            default:
                break;
        }

        return result;
    }

   /**
    * Returns a valid Java Class Name corresponding to this XMLBindingComponent.
    * This name is not qualified, this is only a local Java class name.
    *
    * @return a valid Java Class Name corresponding to this XMLBindingComponent.
    * This name is not qualified, this is only a local Java class name.
    * @see #getQualifiedName
    */
    public String getJavaClassName() {

        if (_javaClassName == null) {
            String result = null;
            //--is there a class name defined (local name)
            if (_compBinding != null) {
                switch (getType()) {
                     case CLASS:
                        result =  _class.getName();
                        break;
                     case INTERFACE:
                        result = _interface.getName();
                        break;
                     default:
                         break;
                }
            }

            if (result == null || result.length() <= 0) {

                 //--is there a reference?
                 if (_annotated.getStructureType() == Structure.ELEMENT) {
                     ElementDecl element = (ElementDecl)_annotated;
                     if (element.isReference()) {
                         Annotated temp = _annotated;
                         setView(element.getReference());
                         result = getJavaClassName();
                         setView(temp);
                         temp = null;
                     }
                     element = null;
                 }
                 
                  //--Still null?
                if (result == null || result.length() <= 0) {
                     //--create the name
                     result = getXMLName();
                     //--create a java name for an anonymous group
                     if ( (result == null) &&
                         ((_annotated.getStructureType() == Structure.GROUP)||
                          (_annotated.getStructureType() == Structure.MODELGROUP)) ) {

                        result = _groupNaming.createClassName((Group)_annotated);
                        if (result == null) {
                            String err = "Unable to create name for group.";
                            throw new IllegalStateException(err);
                        }
                     }
                 }
                 
            }

            if (_prefix != null)
                result = _prefix + result;
            if (_suffix != null)
                result = result + _suffix;
            _javaClassName = JavaNaming.toJavaClassName(result);
        }
        /**
         * @todo ADD A SWITCH TO DETERMINE WETHER OR NOT TO USE JAVA CONVENTIONS
         * FOR THE JAVA CLASS NAME (SEE JAXB)
         */
        return _javaClassName;
    }

    /**
    * Returns a valid Java Member Name corresponding to this XMLBindingComponent.
    * This name is not qualified, this is only a local Java Member name.
    *
    * @return a valid Java Member Name corresponding to this XMLBindingComponent.
    * This name is not qualified, this is only a local Java member name.
    * @see #getQualifiedName
    */
    public String getJavaMemberName() {

        if (_javaMemberName == null) {
            String result = null;
            if (_compBinding != null) {
                switch (getType()) {
                    case CLASS:
                        result =  _class.getName();
                        break;
                    case INTERFACE:
                        result = _interface.getName();
                        break;
                    case MEMBER:
                        result = _member.getName();
                        break;
                    default:
                        break;
                }
            }
            if (result == null || result.length() <= 0) {

                Annotated temp = null;
                if (_annotated.getStructureType() == Structure.ATTRIBUTE) {
                     AttributeDecl att = (AttributeDecl)_annotated;
                     if (att.isReference()) {
                         temp = _annotated;
                         setView(att.getReference());
                         result = getJavaMemberName();
                         setView(temp);
                     }
                     att = null;
                }
                else if (_annotated.getStructureType() == Structure.ELEMENT) {
                     ElementDecl element = (ElementDecl)_annotated;
                     if (element.isReference()) {
                         temp = _annotated;
                         setView(element.getReference());
                         result = getJavaMemberName();
                         setView(temp);
                     }
                     element = null;
                }
                temp = null;
                
                //--Still null?
                if (result == null || result.length() <= 0) {
                    //--create the name
                    result = getXMLName();
                    //--create a java name for an anonymous group
                    if ( (result == null) &&
                          ((_annotated.getStructureType() == Structure.GROUP)||
                          (_annotated.getStructureType() == Structure.MODELGROUP)) )
                    {

                        result = _groupNaming.createClassName((Group)_annotated);
                        if (result == null) {
                            String err = "Unable to create name for group.";
                            throw new IllegalStateException(err);
                        }
                    }
                }
            }
            if (_prefix != null)
                result = _prefix + result;
            if (_suffix != null)
                result = result + _suffix;
            _javaMemberName = JavaNaming.toJavaMemberName(result);
        }
        /**
         * @todo ADD A SWITCH TO DETERMINE WETHER OR NOT TO USE JAVA CONVENTIONS
         * FOR THE JAVA CLASS NAME (SEE JAXB)
         */
        return _javaMemberName;
    }

    /**
     * <p>Returns the fully qualified name used for generating a java name that
     * represents this XMLBindingComponent.
     * <p>The fully qualified name is computed according the following priority
     * order:
     * <ul>
     *     <li>If the XMLBinding wraps a class binding then the package name is
     *     the name defined locally in the &lt;java-class&gt; element.
     *     More precisely the package name will be the value of the attribute
     *     package.</li>
     *     <li>Else the package name will be computed from the schemaLocation
     *     of the parent schema.</li>
     *     <li>Else the package name will be computed from the target namespace
     *     of the parent schema.</li>
     * </ul>
     *
     * Note: the computation of the namespace is a direct look-up for a defined
     * mapping (Namespace, package) or (schema location, package).
     *
     * @return the fully qualified name used for generating a java name that
     * represents this XMLBindingComponent.
     */
    public String getQualifiedName() {

        String result = getJavaClassName();
        String packageName = getJavaPackage();
        if (packageName != null && packageName.length()>0) {
             packageName += '.';
             result = packageName + result;
        }
        return result;
    }

    /**
     * Returns the java package associated with this XML BindingComponent.
     * The algorithm used to resolve the package is defined according to the following
     * priorities:
     * <ol>
     *    <li>The package defined locally in the class declaration inside the binding file is used.</li>
     *    <li>If no package has been defined locally then a lookup to a defined mapping
     *    {targetNamespace, package name} is performed.</li>
     *    <li>If no package has been defined locally then a lookup to a defined mapping
     *    {schema location, package name} is performed.</li>
     * </ol>
     *
     * @return the java package associated with this XML BindingComponent.
     */
    public String getJavaPackage() {

        if (_javaPackage == null) {
            String packageName = null;
            String schemaLocation = getSchemaLocation();
            String targetNamespace = getTargetNamespace();
            
            if (_compBinding != null) {
                switch (getType()) {
                    case CLASS:
                        packageName = _class.getPackage();
                        break;
                    default:
                        break;
                }//--switch
            }
            if  ((packageName == null)|| (packageName.length() ==0) ) {
                //--highest priority is schemaLocation
                if (schemaLocation != null) {
                    packageName = SourceGenerator.lookupPackageLocation(schemaLocation);
                }
                if ( (targetNamespace != null) &&
                    ( packageName == null || packageName.length() ==0))
                {
                    //--look for a namespace mapping
                    packageName = SourceGenerator.lookupPackageNamespace(targetNamespace);
                }
            }
            _javaPackage = packageName;
        }
        return _javaPackage;
    }



   /**
	 * <p>Returns the upper bound of the collection that is generated from
	 * this BindingComponent. The upper bound is a positive integer. -1 is returned
	 * to indicate that the upper bound is unbounded.
	 * <p>In the case of an XML Schema component,
	 * the upper bound corresponds to the XML Schema maxOccurs attribute, if any.
	 *
	 * @return an int representing the lower bound of the collection generated
	 * from this BindingComponent. -1 is returned to indicate that the upper bound is unbounded.
	 * 1 is the default value.
	 */
    public int getUpperBound() {
        switch (_annotated.getStructureType()) {

            case Structure.ELEMENT:
                return ((ElementDecl)_annotated).getMaxOccurs();

            case Structure.COMPLEX_TYPE:
                return ((ComplexType)_annotated).getMaxOccurs();

            case Structure.GROUP:
            case Structure.MODELGROUP:
                return ((Group)_annotated).getMaxOccurs();

            case Structure.ATTRIBUTE:
            default:
               break;
        }
        return 1;
    }

   /**
	 * Returns the lower bound of the collection that is generated from
	 * this BindingComponent. The lower bound is a positive integer.
	 * In the case of an XML Schema component, it corresponds to the
	 * XML Schema minOccurs attribute, if any.
	 *
	 * @return an int representing the lower bound of the collection generated
	 * from this BindingComponent. 0 is returned by default.
	 */
    public int getLowerBound() {
        switch (_annotated.getStructureType()) {

            case Structure.ELEMENT:
                return ((ElementDecl)_annotated).getMinOccurs();

            case Structure.COMPLEX_TYPE:
                return ((ComplexType)_annotated).getMinOccurs();

            case Structure.GROUP:
            case Structure.MODELGROUP:
                return ((Group)_annotated).getMinOccurs();

            case Structure.ATTRIBUTE:
                if (((AttributeDecl)_annotated).isRequired())
                    return 1;
                break;
            default:
               break;
        }
        return 0;
    }

    ////////METHODS RELATED TO A CLASS BINDING
    /**
     * Returns the name of a super class for the current XMLBinding.
     * Null is returned if this XMLBinding is not meant to be mapped to
     * a java class.
     *
     * @return the name of a super class for the current XMLBinding.
     * Null is returned if this XMLBinding is not meant to be mapped to
     * a java class
     */
    public String getExtends() {
        if (getType() == CLASS) {
            return _class.getExtends();
        }
        else return SourceGenerator.getProperty(SourceGenerator.Property.SUPER_CLASS, null);

    }

    /**
     * Returns an array of the different interface names implemented by the class
     * that will represent the current XMLBindingComponent. Null is returned if
     * no class binding is defined for the wrapped XML Schema structure.
     * @return
     */
    public String[] getImplements() {
        if (getType() == CLASS) {
            return _class.getImplements();
        }
        else return null;

    }

    /**
     * Returns true if bound properties must be generated for the class
     * that will represent the current XMLBindingComponent.
     *
     * @return true if bound properties must be generated for the class
     * the class that will represent the current XMLBindingComponent.
     */
    public boolean hasBoundProperties() {
        if (getType() == CLASS) {
            if (_class.hasBound())
                return _class.getBound();
        }
        return SourceGenerator.boundPropertiesEnabled();
    }

    /**
     * Returns true if equal method must be generated for the class
     * that will represent the current XMLBindingComponent.
     *
     * @return true if equal method must be generated for the class
     * the class that will represent the current XMLBindingComponent.
     */
    public boolean hasEquals() {
        if (getType() == CLASS) {
            if (_class.hasEquals())
                return _class.getEquals();
        }
        return SourceGenerator.equalsMethod();
    }

    /**
     * Returns true if the class that will represent the current XMLBindingComponent
     * must be abstract.
     *
     * @return true if the class that will represent the current XMLBindingComponent
     * must be abstract.
     */
    public boolean isAbstract() {
        boolean result = false;
        if (getType() == CLASS) {
           if (_class.hasAbstract())
               result = _class.getAbstract();
        }
        if (!result) {
            result = (_annotated.getStructureType() == Structure.COMPLEX_TYPE);
            result = (result && SourceGenerator.mappingSchemaElement2Java());
        }
        return result;
    }

    /**
     * Returns true if the class that will represent the current XMLBindingComponent
     * must be final.
     *
     * @return true if the class that will represent the current XMLBindingComponent
     * must be final.
     */
    public boolean isFinal() {
        if (getType() == CLASS) {
            return _class.getFinal();
        }
        else return false;
    }

    /**
     * Returns true if the wrapped XML Schema component is fixed (i.e the value
     * used is fixed).
     *
     * @return true if the wrapped XML Schema component is fixed (i.e the value
     * used is fixed).
     */
    public boolean isFixed() {

        switch (_annotated.getStructureType()) {
            case Structure.ELEMENT:
                String fixed = ((ElementDecl)_annotated).getFixedValue();
                return (fixed != null);

            case Structure.ATTRIBUTE:
                return ((AttributeDecl)_annotated).isFixed();

            case Structure.GROUP:
            case Structure.COMPLEX_TYPE:
            case Structure.MODELGROUP:
            default:
                break;
        }
        return false;
    }

    ////////METHODS RELATED TO A MEMBER BINDING
    /**
     * Returns true if the member represented by that XMLBindingComponent
     * is to be represented by an Object wrapper. For instance an int will be
     * represented by a java Integer if the property is set to true.
     *
     * @return true if the member represented by that XMLBindingComponent
     * is to be represented by an Object wrapper.
     */
    public boolean useWrapper() {
        boolean result = false;
        if (_type == XMLBindingComponent.MEMBER) {
            if (_member.hasWrapper())
               result = _member.getWrapper();
        }
        else {
            result = SourceGenerator.usePrimitiveWrapper();
        }
        return result;
    }

   /**
    * <p>Returns the XSType that corresponds to the Java type chosen to represent
    * the XML Schema component represented by this XMLBindingComponent.
    * An XSType is an abstraction of a Java type used in the Source Generator. It
    * wraps a JType as well as the necessary methods to convert to/from String.
    * <p>If a name of java type is specified then this name will have higher
    * priority than the simpleType resolution.
    *
    * @return an XSType
    */
    public XSType getJavaType() {

        //--no need for caching it is called only once
        XSType result = null;
        boolean useWrapper = useWrapper();
        XMLType type = getXMLType();

        if (type.isComplexType()) {
            result = new XSClass(new JClass(getJavaClassName()));
        }
        else {
            if (_type == MEMBER) {
                String javaType = _member.getJavaType();
                if (javaType != null && javaType.length() >0 )
                    result = TypeConversion.convertType(javaType);
            }
        }

        if (result == null) {
            //--simpleType or AnyType
            if (type.isSimpleType()) {
                String packageName = null;
                if (((SimpleType)type).getSchema() != getSchema()) {
                    XMLBindingComponent comp = new XMLBindingComponent();
                    comp.setBinding(_binding);
                    comp.setView(type);
                    packageName = comp.getJavaPackage();
                }
                else packageName = getJavaPackage();
                result = TypeConversion.convertType((SimpleType)type, useWrapper, packageName);
            }
        }
        
        return result;
    }

    /**
     * Returns the collection name specified in the binding file. If no collection
     * was specified, null will be returned and the default collection settings
     * will be used.
     *
     * @return a string that represents the collection name specified in
     * the binding file. If no collection was specified, null will be returned
     * and the default collection settings will be used.
     *
     */
    public String getCollectionType() {
        String result = null;
        if (_type == MEMBER) {
            result = _member.getCollection().toString();
        }
        return result;
    }

   /**
    * Returns the fully qualified name of the Validator to use.
    *
    * @return the fully qualified name of the Validator to use.
    */
    public String getValidator() {
        if (_type == MEMBER) {
            return _member.getValidator();
        }
        return null;
    }


   /**
    * Returns the fully qualified name of the XMLFieldHandler to use.
    *
    * @return the fully qualified name of the XMLFieldHandler to use.
    */
    public String getXMLFieldHandler() {
        if (_type == MEMBER) {
            return _member.getHandler();
        }
        return null;
    }


    /**
	 * <p>Returns the type of this component binding. A component binding can
	 * be of three different types:
	 * <ul>
	 *     <li>Interface: it represents the binding to a java interface.</li>
	 *     <li>Class: it represents the binding to a java class.</li>
	 *     <li>Member: it represents the binding to a java class member.</li>
	 * </ul>
	 * <p>-1 is returned if the component binding is null.
	 *
	 * @return the type of this component binding. A component binding can
	 * be of three different types:
	 * <ul>
	 *     <li>Interface: it represents the binding to a java interface.</li>
	 *     <li>Class: it represents the binding to a java class.</li>
	 *     <li>Member: it represents the binding to a java class member.</li>
	 * </ul>
	 * -1 is returned if the component binding is null.
	 */
	public short getType() {
	    return _type;
	}

}