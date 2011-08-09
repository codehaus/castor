package org.castor.jaxb.reflection.processor.field;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlMixed;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XmlMixed.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlMixedFieldProcessor")
public class XmlMixedProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlMixed) && (info instanceof JaxbFieldNature)) {
            XmlMixed xmlMixed = (XmlMixed) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlMixed);
            fieldInfo.setXmlMixed(true);
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
        return XmlMixed.class;
    }
}