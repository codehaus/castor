package org.castor.jaxb.reflection.processor.packagge;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlAccessorOrder;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbPackageNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XmlAccessorOrder.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlAccessorOrderProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
            final I info, final A annotation) {
        if ((annotation instanceof XmlAccessorOrder)
                && (info instanceof JaxbPackageNature)) {
            XmlAccessorOrder xmlAccessorOrder = (XmlAccessorOrder) annotation;
            JaxbPackageNature packageInfo = (JaxbPackageNature) info;
            this.annotationVisitMessage(xmlAccessorOrder);
            //
            packageInfo.setAccessOrder(xmlAccessorOrder.value());
            // UNDEFINED, ALPHABETICAL
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
        return XmlAccessorOrder.class;
    }
}