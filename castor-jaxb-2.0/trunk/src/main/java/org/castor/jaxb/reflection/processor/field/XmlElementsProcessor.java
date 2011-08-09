package org.castor.jaxb.reflection.processor.field;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XMLElements.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlElementsFieldProcessor")
public class XmlElementsProcessor extends BaseFieldProcessor {

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlElements) && (info instanceof JaxbFieldNature)) {
            XmlElements xmlElements = (XmlElements) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlElements);

            fieldInfo.setXmlElements(true);
            XmlElement[] xmlElementArray = xmlElements.value();
            for (int i = 0; i < xmlElementArray.length; i++) {
                XmlElement xmlElement = xmlElementArray[i];
                fieldInfo
                        .addElement(
                                (!XmlElementProcessor.ELEMENT_NAME_DEFAULT.equals(xmlElement.name())) ? xmlElement
                                        .name() : null,
                                (!XmlElementProcessor.ELEMENT_NAMESPACE_DEFAULT.equals(xmlElement.namespace())) ? xmlElement
                                        .name() : null,
                                xmlElement.nillable(),
                                xmlElement.required(),
                                (!XmlElement.DEFAULT.class.equals(xmlElement.type())) ? xmlElement.type() : null,
                                (!XmlElementProcessor.ELEMENT_DEFAULT_VALUE_DEFAULT.equals(xmlElement.defaultValue())) ? xmlElement
                                        .defaultValue() : null);
            }
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
        return XmlElements.class;
    } // -- forAnnotationClass
}