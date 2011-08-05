package org.castor.jaxb.reflection.processor.clazz;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlTransient;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XmlTransient.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlTransientProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlTransient) && (info instanceof JaxbClassNature)) {
            XmlTransient xmlTransient = (XmlTransient) annotation;
            JaxbClassNature classInfo = (JaxbClassNature) info;
            this.annotationVisitMessage(xmlTransient);
            classInfo.setXmlTransient(true);
            // no annotation properties
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
        return XmlTransient.class;
    }
}