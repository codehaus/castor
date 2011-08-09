package org.castor.jaxb.reflection.processor.field;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlElement;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XMLElement.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlElementFieldProcessor")
public class XmlElementProcessor extends BaseFieldProcessor {
    
    /** XmlElement.name default is ##default. */
    public static final String ELEMENT_NAME_DEFAULT = "##default";
    /** XmlElement.namespace default is empty string. */
    public static final String ELEMENT_NAMESPACE_DEFAULT = "";
    /** XmlElement.defaultValue default is \u0000. */
    public static final String ELEMENT_DEFAULT_VALUE_DEFAULT = "\u0000";

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.ReflectionInfo,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlElement) && (info instanceof JaxbFieldNature)) {
            XmlElement xmlElement = (XmlElement) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlElement);
            //
            fieldInfo.setXmlElement(true);
            if (!ELEMENT_NAME_DEFAULT.equals(xmlElement.name())) {
                fieldInfo.setElementName(xmlElement.name());
            }
            fieldInfo.setElementNillable(xmlElement.nillable());
            fieldInfo.setElementRequired(xmlElement.required());
            if (!ELEMENT_NAMESPACE_DEFAULT.equals(xmlElement.namespace())) {
                fieldInfo.setElementNamespace(xmlElement.namespace());
            }
            if (!XmlElement.DEFAULT.class.equals(xmlElement.type())) {
                fieldInfo.setElementType(xmlElement.type());
            }
            if (!ELEMENT_DEFAULT_VALUE_DEFAULT.equals(xmlElement.defaultValue())) {
                fieldInfo.setElementDefaultValue(xmlElement.defaultValue());
            }
            return true;
        }
        return false;
    } // -- processAnnotation

    /**
     * {@inheritDoc}
     * 
     * @see AnnotationProcessor#forAnnotationClass()
     */
    public Class<? extends Annotation> forAnnotationClass() {
        return XmlElement.class;
    }
}