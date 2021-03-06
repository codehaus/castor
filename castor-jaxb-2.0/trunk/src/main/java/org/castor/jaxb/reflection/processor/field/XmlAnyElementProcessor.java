package org.castor.jaxb.reflection.processor.field;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.W3CDomHandler;
import javax.xml.bind.annotation.XmlAnyElement;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XmlAnyElement.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlAnyElementFieldProcessor")
public class XmlAnyElementProcessor extends BaseFieldProcessor {

    /** XmlAnyElement.value default is W3CDomHandler. */
    public static final Class<?> ANY_ELEMENT_DOMHANDLER_DEFAULT = W3CDomHandler.class;

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlAnyElement) && (info instanceof JaxbFieldNature)) {
            XmlAnyElement xmlAnyElement = (XmlAnyElement) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlAnyElement);
            fieldInfo.setXmlAnyElement(true);
            fieldInfo.setAnyElementLax(xmlAnyElement.lax());
            if (!ANY_ELEMENT_DOMHANDLER_DEFAULT.equals(xmlAnyElement.value())) {
                fieldInfo.setAnyElementDomHandler(xmlAnyElement.value());
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
        return XmlAnyElement.class;
    }
}