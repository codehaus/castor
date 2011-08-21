/*
 * Copyright 2011 Jakub Narloch
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

package org.castor.jaxb.reflection.processor.field;

import org.castor.core.nature.BaseNature;
import org.castor.jaxb.reflection.info.JaxbFieldNature;
import org.castor.jaxb.reflection.processor.BaseFieldProcessor;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Annotation;

/**
 * Processor for {@link XmlJavaTypeAdapter} annotation.
 *
 * @author Jakub Narloch, jmnarloch AT gmail DOT org
 * @version 1.0
 */
@Component("xmlJavaTypeAdapterProcessor")
public class XmlJavaTypeAdapterProcessor extends BaseFieldProcessor{

    /**
     * {@inheritDoc}
     */
    @Override
    public <I extends BaseNature, A extends Annotation> boolean processAnnotation(I info, A annotation) {

        if ((annotation instanceof XmlJavaTypeAdapter) && (info instanceof JaxbFieldNature)) {
            XmlJavaTypeAdapter xmlJavaTypeAdapter = (XmlJavaTypeAdapter) annotation;
            JaxbFieldNature fieldInfo = (JaxbFieldNature) info;
            this.annotationVisitMessage(xmlJavaTypeAdapter);
            fieldInfo.setXmlJavaTypeAdapter(xmlJavaTypeAdapter.value());
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Annotation> forAnnotationClass() {
        return XmlJavaTypeAdapter.class;
    }
}
