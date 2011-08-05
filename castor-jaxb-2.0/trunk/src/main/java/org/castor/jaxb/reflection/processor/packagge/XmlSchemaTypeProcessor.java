package org.castor.jaxb.reflection.processor.packagge;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlSchemaType;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.PackageAnnotationProcessingService;
import org.castor.jaxb.reflection.info.JaxbPackageNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XmlSchemaType.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlSchemaTypeProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.BaseNature,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(
            final I info, final A annotation) {
        if ((annotation instanceof XmlSchemaType)
                && (info instanceof JaxbPackageNature)) {
            XmlSchemaType xmlSchemaType = (XmlSchemaType) annotation;
            JaxbPackageNature packageInfo = (JaxbPackageNature) info;
            this.annotationVisitMessage(xmlSchemaType);
            //
            packageInfo.addSchemaType(xmlSchemaType.name(), xmlSchemaType
                    .namespace(), xmlSchemaType.type());
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see AnnotationProcessor#forAnnotationClass()
     */
    public final Class<? extends Annotation> forAnnotationClass() {
        return XmlSchemaType.class;
    }
}