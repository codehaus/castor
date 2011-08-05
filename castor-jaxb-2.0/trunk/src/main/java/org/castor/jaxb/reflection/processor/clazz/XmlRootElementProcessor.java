package org.castor.jaxb.reflection.processor.clazz;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlRootElement;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XMLRootElement.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlRootElementProcessor extends BaseFieldProcessor {

    /** XmlRootElement.name default value. */
    public static final String ROOT_ELEMENT_NAME_DEFAULT = "##default";
    /** XmlRootElement.namespace default value. */
    public static final String ROOT_ELEMENT_NAMESPACE_DEFAULT = "##default";

    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlRootElement) && (info instanceof JaxbClassNature)) {
            XmlRootElement xmlRootElement = (XmlRootElement) annotation;
            JaxbClassNature classInfo = (JaxbClassNature) info;
            this.annotationVisitMessage(xmlRootElement);
            classInfo.setXmlRootElement(true);
            if (!ROOT_ELEMENT_NAME_DEFAULT.equals(xmlRootElement.name())) {
                classInfo.setRootElementName(xmlRootElement.name());
            }
            if (!ROOT_ELEMENT_NAMESPACE_DEFAULT.equals(xmlRootElement.namespace())) {
                classInfo.setRootElementNamespace(xmlRootElement.namespace());
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see AnnotationProcessor#forAnnotationClass()
     */
    public Class<? extends Annotation> forAnnotationClass() {
        return XmlRootElement.class;
    }
}