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
import org.castor.jaxb.reflection.processor.packagge.XmlAccessorOrderProcessor;
import org.castor.jaxb.reflection.processor.packagge.XmlAccessorTypeProcessor;
import org.castor.jaxb.reflection.processor.packagge.XmlSchemaProcessor;
import org.castor.jaxb.reflection.processor.packagge.XmlSchemaTypeProcessor;
import org.castor.jaxb.reflection.processor.packagge.XmlSchemaTypesProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service class to precess all package level annotations.
 * 
 * @author Joachim Grueneis, jgrueneis_at_codehaus_dot_com
 * @version $Id$
 */
public class PackageAnnotationProcessingService extends BaseAnnotationProcessingService {

    public final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /** Default for names. */
    private static final String ANNOTATION_PROPERTY_NAME_DEFAULT = "##default";
    /** Default for namespace. */
    private static final String ANNOTATION_PROPERTY_NAMESPACE_DEFAULT = "##default";

    /**
     * Constructs a AnnotationProcessingService what means to register all
     * available AnnotationProcessing classes.
     */
    public PackageAnnotationProcessingService() {
        addAnnotationProcessor(new XmlSchemaProcessor());
        addAnnotationProcessor(new XmlAccessorTypeProcessor());
        addAnnotationProcessor(new XmlAccessorOrderProcessor());
        addAnnotationProcessor(new XmlSchemaTypeProcessor());
        addAnnotationProcessor(new XmlSchemaTypesProcessor());
    }
}
