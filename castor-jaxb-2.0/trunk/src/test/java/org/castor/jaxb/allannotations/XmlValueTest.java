/*
 * Copyright 2008 Joachim Grueneis
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
package org.castor.jaxb.allannotations;

import org.castor.jaxb.JAXBContext;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlValue;
import java.io.StringWriter;
import java.math.BigDecimal;

/**
 * @author joachim
 *
 */
public class XmlValueTest {

    @Test
    public void testFullCycle() throws JAXBException {
        javax.xml.bind.JAXBContext context = JAXBContext.newInstance(ForXmlValue.class);
        Marshaller m = context.createMarshaller();
        StringWriter sw = new StringWriter();
        ForXmlValue fxv = new ForXmlValue();
        fxv.content = new BigDecimal(4711);
        m.marshal(fxv, sw);
        System.out.println(sw.toString());
    }

    private class ForXmlValue {
        @XmlValue
        private BigDecimal content;
    }
}
