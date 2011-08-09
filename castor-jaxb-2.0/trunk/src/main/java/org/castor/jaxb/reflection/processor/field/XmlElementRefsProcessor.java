package org.castor.jaxb.reflection.processor.field;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XmlElementRefs.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlElementRefsFieldProcessor")
public class XmlElementRefsProcessor extends BaseFieldProcessor {

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
        if ((annotation instanceof XmlElementRefs) && (info instanceof JaxbFieldNature)) {
            XmlElementRefs xmlElementRefs = (XmlElementRefs) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlElementRefs);
            fieldInfo.setXmlElementRefs(true);
            //
            XmlElementRef[] xmlElementRefArray = xmlElementRefs.value();
            for (int i = 0; i < xmlElementRefArray.length; i++) {
                XmlElementRef xmlElementRef = xmlElementRefArray[i];
                fieldInfo.addElementRefsElementRef(
                        (!ELEMENT_REF_NAME_DEFAULT.equals(xmlElementRef.name())) ? xmlElementRef.name() : null,
                        (!ELEMENT_REF_NAMESPACE_DEFAULT.equals(xmlElementRef.namespace())) ? xmlElementRef.namespace()
                                : null,
                        (!XmlElementRef.DEFAULT.class.equals(xmlElementRef.type())) ? xmlElementRef.type() : null);
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
        return XmlElementRefs.class;
    } // -- forAnnotationClass
}