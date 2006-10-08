package org.springframework.xml.castor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Marshaller;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ListableBeanFactory;

public class CastorMarshallerFactoryBean extends AbstractCastorPrototypingXMLFactoryBean {

    /**
     * Log instance
     */
    private static final Log LOG = LogFactory.getLog(CastorMarshallerFactoryBean.class);
    
    /**
     * Indicates whether or not to emit debug information
     **/
    private boolean debug = false;
    /**
     * Sets the encoding for the serializer.
     */
    private String encoding;
    
    /**
     * Indicates whether or not to marshal as a complete document or not.
    **/
    private boolean marshalAsDocument = true;
    
    /**
     * Indicates whether or not to use xsi:type
     **/
    private boolean marshalExtendedType = true;
    
    /**
     * Value for the xsi:noNamespaceSchemaLocation attribute
     */
    private String noNamespaceSchemaLocation;
    
    /**
     * Name of the root element to use
     */
    private String rootElement;
    
    /**
     * Value for the xsi:schemaLocation attribute
     */
    private String schemaLocation;
    
    /**
     * Indicates whether to suppress namespaces
     */
    private boolean suppressNamespaces = false;
    
    /**
     * Indicates whether to suppress the xsi:type attribute
     */
    private boolean suppressXSIType = false;
    
    /**
     * Indicates whether to suppress the XML declaration
     */
    private boolean suppressXMLDeclaration = false;
    
    /**
     * Indicates whether to use xsi:type attributes at the root
     */
    private boolean useXSITypeAtRoot = false;
    
    /**
     * Indicates whether to enable validation
     **/
    private boolean validation = true;

    /**
     * Return an instance (possibly shared or independent) of the object managed
     * by this factory. As with a BeanFactory, this allows support for both the
     * Singleton and Prototype design pattern.
     * <p>
     * If this method returns null, the factory will consider the FactoryBean as
     * not fully initialized and throw a corresponding
     * FactoryBeanNotInitializedException.
     * 
     * @return an instance of the bean (should not be null; a null value will be
     *         considered as an indication of incomplete initialization)
     * @throws Exception
     *             in case of creation errors
     * @see FactoryBeanNotInitializedException
     */
    public Object getObject() throws Exception {
        Marshaller marshaller = new Marshaller();
        marshaller.setResolver(this.getResolver());
        marshaller.setDebug(this.debug);
        // marshaller.setEncoding(this.encoding);
        marshaller.setMarshalAsDocument(this.marshalAsDocument);
        marshaller.setMarshalExtendedType(this.marshalExtendedType);
        marshaller.setNoNamespaceSchemaLocation(this.noNamespaceSchemaLocation);
        marshaller.setRootElement(this.rootElement);
        marshaller.setSchemaLocation(this.schemaLocation);
        marshaller.setSuppressNamespaces(this.suppressNamespaces);
        marshaller.setSuppressXSIType(this.suppressXSIType);
        marshaller.setSupressXMLDeclaration(this.suppressXMLDeclaration);
        marshaller.setUseXSITypeAtRoot(this.useXSITypeAtRoot);
        marshaller.setValidation(this.validation);
        return marshaller;
    }

    /**
     * Return the type of object that this FactoryBean creates, or null if not
     * known in advance. This allows to check for specific types of beans
     * without instantiating objects, for example on autowiring.
     * <p>
     * For a singleton, this should try to avoid singleton creation as far as
     * possible; it should rather estimate the type in advance. For prototypes,
     * returning a meaningful type here is advisable too.
     * <p>
     * This method can be called <i>before</i> this FactoryBean has been fully
     * initialized. It must not rely on state created during initialization; of
     * course, it can still use such state if available.
     * <p>
     * <b>NOTE:</b> Autowiring will simply ignore FactoryBeans that return null
     * here. Therefore it is highly recommended to implement this method
     * properly, using the current state of the FactoryBean.
     * 
     * @return the type of object that this FactoryBean creates, or null if not
     *         known at the time of the call
     * @see ListableBeanFactory#getBeansOfType
     */
    public Class getObjectType() {
        return Marshaller.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.xml.castor.AbstractCastorPrototypingXMLFactoryBean#getLog()
     */
    protected Log getLog() {
        return CastorMarshallerFactoryBean.LOG;
    }

    /**
     * To turn debugging on and off
     * @param debug True if debug information should be emitted
    **/
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Sets the encoding for the serializer.      *
     * @param encoding The encoding to set on the serializer
    **/
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Sets whether or not to marshal as a document which includes
     * the XML declaration, and if necessary the DOCTYPE declaration.
     * By default the Marshaller will marshal as a well formed
     * XML document including the XML Declaration.
     * 
     * If the given boolean is false, the Marshaller will marshal 
     * as a well formed XML fragment (no XML declaration or DOCTYPE).
     *
     * This method is basically the same as calling 
     * #setSupressXMLDeclaration(true);
     * 
     * @param asDocument True if to marshal as a complete XML document.
     * @see #setSupressXMLDeclaration
     */
    public void setMarshalAsDocument(boolean marshalAsDocument) {
        this.marshalAsDocument = marshalAsDocument;
    }

    /**
     * Indicates whether to use the 'xsi:type' attribute to marshall a field 
     * value that extended the defined field type; default is <tt>true</tt>.
     */
    public void setMarshalExtendedType(boolean marshalExtendedType) {
        this.marshalExtendedType = marshalExtendedType;
    }

    /**
     * Sets the value for the xsi:noNamespaceSchemaLocation attribute.
     * When set, this attribute will appear on the root element of the 
     * marshalled document.
     *
     * @param schemaLocation the URI location of the schema to which the marshalled 
     * document is an instance of.
    **/
    public void setNoNamespaceSchemaLocation(String noNamespaceSchemaLocation) {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
    }

    /**
     * Sets the name of the root element to use.
     * @param rootElement The name of the root element to use.
     */
    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Sets the value for the xsi:schemaLocation attribute.
     * When set, this attribute will appear on the root element
     * of the marshalled document.
     *
     * @param schemaLocation the URI location of the schema
     * to which the marshalled document is an instance of.
    **/
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    /**
     * Sets whether or not namespaces are output. By default, the Marshaller 
     * will output namespace declarations and prefix elements and attributes 
     * with their respective namespace prefix. This method can be used to prevent
     * the usage of namespaces.
     * 
     * @param suppressNamespaces True if to prevent namespaces from being output.
     */
    public void setSuppressNamespaces(boolean suppressNamespaces) {
        this.suppressNamespaces = suppressNamespaces;
    }

    /**
     * Sets whether or not to marshal as a document which includes
     * the XML declaration, and if necessary the DOCTYPE declaration.
     * By default the Marshaller will marshal as a well formed
     * XML document including the XML Declaration.
     * 
     * If the given boolean is true, the Marshaller will marshal 
     * as a well formed XML fragment (no XML declaration or DOCTYPE).
     * 
     * This method is basically the same as calling 
     * #setMarshalAsDocument(false);
     * 
     * @param supressXMLDeclaration a boolean that when true
     * includes that generated XML should not contain 
     * the XML declaration.
     * @see #setMarshalAsDocument
     */
    public void setSuppressXMLDeclaration(boolean suppressXMLDeclaration) {
        this.suppressXMLDeclaration = suppressXMLDeclaration;
    }

    /**
     * Sets whether or not the xsi:type attribute should appear
     * on the marshalled document.
     *
     * @param suppressXSIType a boolean that when true will prevent
     * xsi:type attribute from being used in the marshalling process.
     */
    public void setSuppressXSIType(boolean suppressXSIType) {
        this.suppressXSIType = suppressXSIType;
    }

    /**
     * Sets whether or not to output <tt>xsi:type</tt> at the root element. 
     * This is usually needed when the root element type cannot be determined 
     * by the element name alone. By default, <tt>xsi:type</tt> will not be
     * output on the root element.
     * 
     * @param useXSITypeAtRoot True to indicate that the <tt>xsi:type</tt> should 
     * be output on the root element.
     */
    public void setUseXSITypeAtRoot(boolean useXSITypeAtRoot) {
        this.useXSITypeAtRoot = useXSITypeAtRoot;
    }

    /**
     * Sets whether or not to validate the object model before marshalling. By default 
     * validation is enabled.
     * 
     * This method is really for debugging.
     * 
     * I do not recommend turning off validation, since you could marshal a 
     * document, which you can then not unmarshal. If you know the object model
     * is guaranteed to be valid, disabling validation will improve performace.
     *
     * @param validate True if to validate the object model before marshalling.
    **/
    public void setValidation(boolean validation) {
        this.validation = validation;
    }
}