package org.castor.jaxb.reflection.processor.packagge;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlAccessorType;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbPackageNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XmlAccessorType.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlAccessorTypeProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.NatureExtendable,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
            final I info, final A annotation) {
        if ((annotation instanceof XmlAccessorType)
                && (info instanceof JaxbPackageNature)) {
            XmlAccessorType xmlAccessorType = (XmlAccessorType) annotation;
            JaxbPackageNature packageInfo = (JaxbPackageNature) info;
            this.annotationVisitMessage(xmlAccessorType);
            //
            packageInfo.setAccessType(xmlAccessorType.value());
            // NONE, PROPERTY, FIELD, PUBLIC_MEMBER
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see AnnotationProcessor#forAnnotationClass()
     */
    public final Class<? extends Annotation> forAnnotationClass() {
        return XmlAccessorType.class;
    }
}