package org.castor.jaxb.reflection.processor.clazz;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlEnum;

import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XmlEnum.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlEnumProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlEnum) && (info instanceof JaxbClassNature)) {
            XmlEnum xmlEnum = (XmlEnum) annotation;
            JaxbClassNature classInfo = (JaxbClassNature) info;
            this.annotationVisitMessage(xmlEnum);
            classInfo.setXmlEnum(true);
            classInfo.setEnumClass(xmlEnum.value());
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.EnumAnnotationProcessingService
     *      .AnnotationProcessor#forAnnotationClass()
     */
    public Class<? extends Annotation> forAnnotationClass() {
        return XmlEnum.class;
    }
}