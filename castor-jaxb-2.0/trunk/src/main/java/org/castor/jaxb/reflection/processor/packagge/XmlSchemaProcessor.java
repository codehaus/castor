package org.castor.jaxb.reflection.processor.packagge;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlSchema;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.PackageAnnotationProcessingService;
import org.castor.jaxb.reflection.info.JaxbPackageNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;

/**
 * Annotation processor for XmlSchema.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class XmlSchemaProcessor extends BaseFieldProcessor {
    /**
     * {@inheritDoc}
     * 
     * @see org.castor.annoproc.AnnotationProcessor#
     *      processAnnotation(org.castor.xml.introspection.ReflectionInfo,
     *      java.lang.annotation.Annotation)
     */
    public final <I extends BaseNature, A extends Annotation> boolean processAnnotation(final I info, final A annotation) {
        if ((annotation instanceof XmlSchema) && (info instanceof JaxbPackageNature)) {
            XmlSchema xmlSchema = (XmlSchema) annotation;
            JaxbPackageNature packageInfo = (JaxbPackageNature) info;
            this.annotationVisitMessage(xmlSchema);
            //
            packageInfo.addSchemaNsArray(xmlSchema.xmlns());
            packageInfo.setSchemaNamespace(xmlSchema.namespace());
            packageInfo.setSchemaElementForm(xmlSchema.elementFormDefault());
            packageInfo.setSchemaAttributeForm(xmlSchema.attributeFormDefault());
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
        return XmlSchema.class;
    }
}