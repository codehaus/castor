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


import org.castor.core.annotationprocessing.BaseAnnotationProcessingService;
import org.castor.jaxb.reflection.processor.field.XmlAnyAttributeProcessor;
import org.castor.jaxb.reflection.processor.field.XmlAnyElementProcessor;
import org.castor.jaxb.reflection.processor.field.XmlAttachmentRefProcessor;
import org.castor.jaxb.reflection.processor.field.XmlAttributeProcessor;
import org.castor.jaxb.reflection.processor.field.XmlElementProcessor;
import org.castor.jaxb.reflection.processor.field.XmlElementRefProcessor;
import org.castor.jaxb.reflection.processor.field.XmlElementRefsProcessor;
import org.castor.jaxb.reflection.processor.field.XmlElementWrapperProcessor;
import org.castor.jaxb.reflection.processor.field.XmlElementsProcessor;
import org.castor.jaxb.reflection.processor.field.XmlEnumValueProcessor;
import org.castor.jaxb.reflection.processor.field.XmlIDProcessor;
import org.castor.jaxb.reflection.processor.field.XmlIDREFProcessor;
import org.castor.jaxb.reflection.processor.field.XmlInlineBinaryDataProcessor;
import org.castor.jaxb.reflection.processor.field.XmlListProcessor;
import org.castor.jaxb.reflection.processor.field.XmlMimeTypeProcessor;
import org.castor.jaxb.reflection.processor.field.XmlMixedProcessor;
import org.castor.jaxb.reflection.processor.field.XmlTransientProcessor;
import org.castor.jaxb.reflection.processor.field.XmlValueProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * To process all field (attribute and method) specific annotations and put the
 * results info FieldInfo instances.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
@Service("fieldAnnotationProcessingService")
public class FieldAnnotationProcessingServiceImpl extends BaseAnnotationProcessingService {

    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructs a AnnotationProcessingService what means to register all
     * available AnnotationProcessing classes.
     */
    public FieldAnnotationProcessingServiceImpl() {
        super();
        addAnnotationProcessor(new XmlElementProcessor());
        // no root element on a field!! addAnnotationProcessor(new
        // XmlRootElementProcessor());
        addAnnotationProcessor(new XmlElementsProcessor());
        addAnnotationProcessor(new XmlElementRefProcessor());
        addAnnotationProcessor(new XmlElementRefsProcessor());
        addAnnotationProcessor(new XmlElementWrapperProcessor());
        addAnnotationProcessor(new XmlAnyElementProcessor());
        addAnnotationProcessor(new XmlAttributeProcessor());
        addAnnotationProcessor(new XmlAnyAttributeProcessor());
        addAnnotationProcessor(new XmlTransientProcessor());
        addAnnotationProcessor(new XmlValueProcessor());
        addAnnotationProcessor(new XmlIDProcessor());
        addAnnotationProcessor(new XmlIDREFProcessor());
        addAnnotationProcessor(new XmlListProcessor());
        addAnnotationProcessor(new XmlMixedProcessor());
        addAnnotationProcessor(new XmlMimeTypeProcessor());
        addAnnotationProcessor(new XmlAttachmentRefProcessor());
        addAnnotationProcessor(new XmlInlineBinaryDataProcessor());
        addAnnotationProcessor(new XmlEnumValueProcessor());
    }
}
