package org.castor.jaxb.reflection.processor.clazz;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlAccessorOrder;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XmlAccessorOrder.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlAccessorOrderClassProcessor")
public class XmlAccessorOrderProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlAccessorOrder) && (info instanceof JaxbClassNature)) {
            XmlAccessorOrder xmlAccessorOrder = (XmlAccessorOrder) annotation;
            JaxbClassNature classInfo = (JaxbClassNature) info;
            this.annotationVisitMessage(xmlAccessorOrder);
            classInfo.setXmlAccessorOrder(true);
            classInfo.setXmlAccessOrder(xmlAccessorOrder.value());
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
        return XmlAccessorOrder.class;
    }
}