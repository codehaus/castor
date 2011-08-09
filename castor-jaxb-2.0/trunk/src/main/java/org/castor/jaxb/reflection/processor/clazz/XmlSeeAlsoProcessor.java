package org.castor.jaxb.reflection.processor.clazz;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XmlSeeAlso.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlSeeAlsoClassProcessor")
public class XmlSeeAlsoProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlSeeAlso) && (info instanceof JaxbClassNature)) {
            XmlSeeAlso xmlSeeAlso = (XmlSeeAlso) annotation;
            JaxbClassNature classInfo = (JaxbClassNature) info;
            this.annotationVisitMessage(xmlSeeAlso);
            classInfo.setXmlSeeAlso(true);
            classInfo.setSeeAlsoClasses(xmlSeeAlso.value());
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
        return XmlSeeAlso.class;
    }
}