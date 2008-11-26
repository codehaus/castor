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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.core.annotationprocessing.BaseAnnotationProcessingService;

/**
 * An abstract implementation for annotation processing.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public abstract class XMLBaseAnnotationProcessingService extends
        BaseAnnotationProcessingService {
    
    /** Logger to use. */
    private static final Log LOG = LogFactory
            .getLog(XMLBaseAnnotationProcessingService.class);

    /**
     * To write a debug message when visiting an annotation at the logger
     * specified.
     * 
     * @param log
     *            the logger to write to
     * @param annotation
     *            the annotation bisited
     */
    protected final void annotationVisitMessage(final Log log,
            final Annotation annotation) {
        if (log.isDebugEnabled()) {
            log.debug("Annotation: " + annotation.toString() + " visited.");
        }
    }

}