package org.castor.jaxb.reflection.processor.field;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlAttribute;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XmlAttribute.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlAttributeFieldProcessor")
public class XmlAttributeProcessor extends BaseFieldProcessor {

    /** XmlAttribute.name default is ##default. */
    public static final String ATTRIBUTE_NAME_DEFAULT = "##default";
    /** XmlAttribute.namespace default is ##default. */
    public static final String ATTRIBUTE_NAMESPACE_DEFAULT = "##default";

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlAttribute) && (info instanceof JaxbFieldNature)) {
            XmlAttribute xmlAttribute = (XmlAttribute) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlAttribute);
            fieldInfo.setXmlAttribute(true);
            if (!ATTRIBUTE_NAME_DEFAULT.equals(xmlAttribute.name())) {
                fieldInfo.setAttributeName(xmlAttribute.name());
            }
            if (!ATTRIBUTE_NAMESPACE_DEFAULT.equals(xmlAttribute.namespace())) {
                fieldInfo.setAttributeNamespace(xmlAttribute.namespace());
            }
            fieldInfo.setAttributeRequired(xmlAttribute.required());
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
        return XmlAttribute.class;
    } // -- forAnnotationClass
}