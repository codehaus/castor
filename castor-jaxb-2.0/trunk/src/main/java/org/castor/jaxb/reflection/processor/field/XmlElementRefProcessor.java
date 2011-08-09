package org.castor.jaxb.reflection.processor.field;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlElementRef;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XmlElementRef.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlElementRefFieldProcessor")
public class XmlElementRefProcessor extends BaseFieldProcessor {

    /** XmlElementRef.name default is ##default. */
    public static final String ELEMENT_REF_NAME_DEFAULT = "##default";
    /** XmlElementRef.namespace default is ##default. */
    public static final String ELEMENT_REF_NAMESPACE_DEFAULT = "";

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlElementRef) && (info instanceof JaxbFieldNature)) {
            XmlElementRef xmlElementRef = (XmlElementRef) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlElementRef);
            fieldInfo.setXmlElementRef(true);
            if (!ELEMENT_REF_NAME_DEFAULT.equals(xmlElementRef.name())) {
                fieldInfo.setElementRefName(xmlElementRef.name());
            }
            if (!ELEMENT_REF_NAMESPACE_DEFAULT.equals(xmlElementRef.namespace())) {
                fieldInfo.setElementRefNamespace(xmlElementRef.namespace());
            }
            if (!XmlElementRef.DEFAULT.class.equals(xmlElementRef.type())) {
                fieldInfo.setElementRefType(xmlElementRef.type());
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
        return XmlElementRef.class;
    }
}