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

package org.castor.jaxb.test.functional;

import org.castor.jaxb.test.functional.fieldAdapter.CustomType;
import org.castor.jaxb.test.functional.fieldAdapter.CustomTypeAdapter;
import org.castor.jaxb.test.functional.fieldAdapter.ElementWithAdapter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 *
 */
public class TypeAdapterMarshallingTest extends BaseFunctionalTest {

    @Before
    @Override
    public void setUp() throws JAXBException {
        StringBuilder builder = new StringBuilder();
        builder.append("org.castor.jaxb.test.functional.fieldAdapter");
        javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(builder.toString());
        marshaller = context.createMarshaller();
    }

    @Test
    public void testXmlJavaFieldAdapter() throws Exception {
        String expectedXml = "<element><value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:java=\"http://java.sun.com\" xsi:type=\"java:java.lang.String\">custom</value>"
                + "</element>";

        ElementWithAdapter element = new ElementWithAdapter();
        element.setCustomType(new CustomType());
        element.getCustomType().setValue("custom");

        String actualXml = marshal(element);

        System.out.println(actualXml);
        assertXmlEquals("Marshaller written invalid result.", expectedXml, actualXml);
    }
}
