package org.castor.jaxb.reflection.processor.field;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang3.StringUtils;
import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XMLElement.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlElementWrapperProcessor extends BaseFieldProcessor {

    /** XmlElementWrapper.name default is ##default. */
    public static final String ELEMENT_WRAPPER_NAME_DEFAULT = "##default";
    /** XmlElementWrapper.namespace default is ##default. */
    public static final String ELEMENT_WRAPPER_NAMESPACE_DEFAULT = "##default";

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
            final I info, final A annotation) {
        if ((annotation instanceof XmlElementWrapper)
                && (info instanceof JaxbFieldNature)) {
            XmlElementWrapper xmlElementWrapper = (XmlElementWrapper) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlElementWrapper);
            fieldInfo.setXmlElementWrapper(true);
            if (!ELEMENT_WRAPPER_NAME_DEFAULT.equals(xmlElementWrapper
                    .name())) {
                fieldInfo.setElementWrapperName(xmlElementWrapper.name());
            } else {
                //TODO[WG]: nit sure this is the right place
                // default naming handling
                String xmlElementName = fieldInfo.getElementName();
                if (StringUtils.isNotEmpty(xmlElementName)) {
                    fieldInfo.setElementWrapperName(xmlElementName);
                }
                
            }
            fieldInfo.setElementWrapperNillable(xmlElementWrapper
                    .nillable());
            fieldInfo.setElementWrapperRequired(xmlElementWrapper
                    .required());
            if (!ELEMENT_WRAPPER_NAMESPACE_DEFAULT.equals(xmlElementWrapper
                    .namespace())) {
                fieldInfo.setElementWrapperNamespace(xmlElementWrapper
                        .namespace());
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
        return XmlElementWrapper.class;
    }
}