package org.castor.jaxb.reflection.processor;

import java.lang.annotation.Annotation;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.nature.BaseNature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFieldProcessor implements AnnotationProcessor {
    
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public abstract Class<? extends Annotation> forAnnotationClass();

    public abstract <I extends BaseNature, A extends Annotation> boolean processAnnotation(I info, A annotation);

    /**
     * To write a debug message when visiting an annotation at the logger
     * specified.
     * 
     * @param log
     *            the logger to write to
     * @param annotation
     *            the annotation bisited
     */
    protected final void annotationVisitMessage(final Annotation annotation) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Annotation: " + annotation.toString() + " visited.");
        }
    }

}
