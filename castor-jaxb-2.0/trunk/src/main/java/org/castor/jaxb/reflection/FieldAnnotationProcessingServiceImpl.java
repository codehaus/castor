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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * To process all field (attribute and method) specific annotations and put the
 * results info FieldInfo instances.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @author <a href="mailto:wguttmn AT codehaus DOT org">Werner Guttmann</a>
 * @version $Id$
 */
@Component("fieldAnnotationProcessingService")
public class FieldAnnotationProcessingServiceImpl extends BaseAnnotationProcessingService {

    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public void setFieldAnnotationProcessors(List<AnnotationProcessor> fieldAnnotationProcessors) {
        for (AnnotationProcessor annotationProcessor : fieldAnnotationProcessors) {
            addAnnotationProcessor(annotationProcessor);
        }
    }

}
