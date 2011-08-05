package org.castor.jaxb.reflection.processor.packagge;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlSchemaTypes;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.PackageAnnotationProcessingServiceImpl;
import org.castor.jaxb.reflection.info.JaxbPackageNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XmlSchemaTypes.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlSchemaTypesProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlSchemaTypes) && (info instanceof JaxbPackageNature)) {
            XmlSchemaTypes xmlSchemaTypes = (XmlSchemaTypes) annotation;
            JaxbPackageNature packageInfo = (JaxbPackageNature) info;
            this.annotationVisitMessage(xmlSchemaTypes);
            //
            packageInfo.addSchemaTypeArray(xmlSchemaTypes.value());
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
        return XmlSchemaTypes.class;
    }
}