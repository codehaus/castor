package org.castor.jaxb.reflection.processor.clazz;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlType;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbClassNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

/**
 * Annotation processor for XMLType.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@Component("xmlTypeClassProcessor")
public class XmlTypeProcessor extends BaseFieldProcessor {

    /** Default String for name property. */
    public static final String XML_TYPE_NAME_DEFAULT = "##default";
    /** Default String for namespace property. */
    public static final String XML_TYPE_NAMESPACE_DEFAULT = "##default";
    /** XmlType.propOrder default value. */
    public static final String[] XML_TYPE_PROP_ORDER_DEFAUL = { "" };
    /** XmlType.factoryMethod default value. */
    public static final String XML_TYPE_FACTORY_METHOD = "";

    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlType) && (info instanceof JaxbClassNature)) {
            XmlType xmlType = (XmlType) annotation;
            JaxbClassNature classInfo = (JaxbClassNature) info;
            this.annotationVisitMessage(xmlType);
            //
            classInfo.setXmlType(true);
            if (!XML_TYPE_NAME_DEFAULT.equals(xmlType.name())) {
                classInfo.setTypeName(xmlType.name());
            }
            if (!XML_TYPE_PROP_ORDER_DEFAUL.equals(xmlType.propOrder())) {
                classInfo.setTypeProperties(xmlType.propOrder());
            }
            if (!XML_TYPE_NAMESPACE_DEFAULT.equals(xmlType.namespace())) {
                classInfo.setTypeNamespace(xmlType.namespace());
            }
            if (!XmlType.DEFAULT.class.equals(xmlType.factoryClass())) {
                classInfo.setTypeFactoryClass(xmlType.factoryClass());
            }
            if (!XML_TYPE_FACTORY_METHOD.equals(xmlType.factoryMethod())) {
                classInfo.setTypeFactoryMethod(xmlType.factoryMethod());
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
        return XmlType.class;
    }
}