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
package org.castor.jaxb.integrationtests.tests.test1;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
@XmlType(propOrder={"street", "city", "state", "zip", "name"})
public class USAddress {
    public String getName() { return ""; }
    public void setName(String name) { }

    public String getStreet() { return ""; }
    public void setStreet(String street) { }

    public String getZip() { return ""; }
    public void setZip(String zip) { }

    public String getCity() { return ""; }
    public void setCity(String city) { }

    public String getState() { return ""; }
    public void setState(String state) { }
}
