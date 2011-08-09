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

import java.util.List;

import org.castor.core.annotationprocessing.AnnotationProcessor;
import org.castor.core.annotationprocessing.BaseAnnotationProcessingService;
import org.castor.jaxb.reflection.info.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to process all class related annotations and put the results into a
 * {@link ClassInfo} instance.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class ClassAnnotationProcessingServiceImpl extends BaseAnnotationProcessingService {

    public final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    public void setClassAnnotationProcessors(List<AnnotationProcessor> classAnnotationProcessors) {
        for (AnnotationProcessor annotationProcessor : classAnnotationProcessors) {
            addAnnotationProcessor(annotationProcessor);
        }
    }

}
