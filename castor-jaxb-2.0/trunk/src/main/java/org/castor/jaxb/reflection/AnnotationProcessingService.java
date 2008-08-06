/*
 * Copyright 2007 Joachim Grueneis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.castor.jaxb.reflection;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.ReflectionInfo;

/**
 * An abstract implementation for annotation processing.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public abstract class AnnotationProcessingService {
    /** Logger to use. */
    private static final Log LOG = LogFactory.getLog(AnnotationProcessingService.class);
    /** List of annotation processors. */
    private Map < Class < ? extends Annotation > , AnnotationProcessor > _annotationProcessorMap =
        new HashMap < Class < ? extends Annotation > , AnnotationProcessor > ();

    /**
     * To add an annotation processor. 
     * @param annotationProcessor the AnnotationProcessor to add
     */
    protected final void addAnnotationProcessor(final AnnotationProcessor annotationProcessor) {
        if (annotationProcessor != null) {
            _annotationProcessorMap.put(
                    annotationProcessor.forAnnotationClass(), 
                    annotationProcessor);
        }
    }
    
    /**
     * To write a debug message when visiting an annotation at the logger specified.
     * @param log the logger to write to
     * @param annotation the annotation bisited
     */
    protected final void annotationVisitMessage(final Log log, final Annotation annotation) {
        if (log.isDebugEnabled()) {
            log.debug("Annotation: " + annotation.toString() + " visited.");
        }        
    }
    
    /**
     * Process an array of annotations.
     * @param info the {@link ReflectionInfo} object to write the annotation information read into
     * @param annotations an array of annotations
     */
    public final < I extends BaseNature > void processAnnotations(
            final I info, final Annotation[] annotations) {
        for (int i = 0; i < annotations.length; i++) {
            processAnnotation(info, annotations[i]);
        }
    }
    
    /**
     * Processes a single annotation.
     * @param info the {@link ReflectionInfo} object to write the annotation information read into
     * @param annotation the annotation to process
     */
    public final < I extends BaseNature, A extends Annotation > void processAnnotation(
            final I info, final A annotation) {
//        boolean processed = false;
        AnnotationProcessor annotationProcessor = 
            _annotationProcessorMap.get(annotation.annotationType());
        if (annotationProcessor != null) {
            annotationProcessor.processAnnotation(info, annotation);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No annotation processor known for annotation: " + annotation);
            }
        }
    }

    /**
     * The interface each specific annotation processor has to
     * fulfill.
     * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
     * @version $Id$
     */
    public interface AnnotationProcessor {
        /**
         * Returns for which Annotation this processor is meant.
         * @return the Class for which this processor is meant
         */
        Class < ? extends Annotation > forAnnotationClass();
        /**
         * The processing action of this processor. If an annotation
         * is given which is not supported by this processor nothing
         * happens.
         * @param info the Info class that should be filled with the information read
         * @param annotation the annotation to process
         */
        < I extends BaseNature, A extends Annotation > void processAnnotation(
                final I info, final A annotation);
    }
}
