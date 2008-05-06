/*
 * Copyright 2008 Matthias Epheser
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
package org.castor.jaxb.sourcegeneration;

import org.exolab.castor.builder.SourceGenerator;
import org.exolab.castor.builder.binding.ExtendedBinding;
import org.exolab.castor.builder.factory.FieldInfoFactory;

/**
 * A custom {@link SourceGenerator} that uses a {@link Jaxb2AnnotationBuilder}
 * and forces the use of Java 5.
 * 
 * @author Matthias Epheser
 * @version $Id$
 *
 */
public class Jaxb2SourceGenerator extends SourceGenerator {

    /**
     * Creates the Source Generator and sets the custom properties.
     */
    public Jaxb2SourceGenerator() {
        super();
        initSpecialProperties();
    }

    /** 
     * Creates the Source Generator and sets the custom properties.
     * 
     * @param infoFactory A custom {@ FieldInfoFactory} to use.
     */
    public Jaxb2SourceGenerator(final FieldInfoFactory infoFactory) {
        super(infoFactory);
        initSpecialProperties();
    }

    /**
     * Creates the Source Generator and sets the custom properties.
     * 
     * @param infoFactory A custom {@ FieldInfoFactory} to use.
     * @param binding A custom {@link ExtendedBinding} to use.
     */
    public Jaxb2SourceGenerator(final FieldInfoFactory infoFactory,
            final ExtendedBinding binding) {
        super(infoFactory, binding);
        initSpecialProperties();
    }

    /**
     * inits the custom properties for jaxb source generation. Actually
     * sets the {@link Jaxb2AnnotationBuilder} and forces the use of
     * java 5.
     */
    private void initSpecialProperties() {
        this.addAnnotationBuilder(new Jaxb2AnnotationBuilder());
        this.setCreateMarshalMethods(false);
        this.forceUseJava50();
    }

    // TODO create object factory, or jaxb.index (.castor)

}
