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

import javax.xml.bind.annotation.XmlAccessType;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.castor.jaxb.integrationtests.tests.test1.USAddress;
import org.castor.jaxb.reflection.info.JaxbPackageNature;
import org.castor.jaxb.reflection.info.PackageInfo;

/**
 * To test all annotation processings ClassNonnotationProcessingService is capable of.
 * these are: XMLType, XmlRootElement, XmlTransient, XmlSeeAlso, XmlAccessorType and
 * XmlAccessorOrder.
 * 
 * @author Joachim Grueneis, jgrueneis_at_gmail_dot_com
 * @version $Id$
 */
public class PackageAnnotationProcessingServiceTest extends TestCase {

    /**
     * @param name
     */
    public PackageAnnotationProcessingServiceTest(final String name) {
        super(name);
    }
    
    /**
     * Test method for {@link org.castor.jaxb.reflection
     * .AnnotationProcessingService#processAnnotations(org.castor.jaxb.reflection.info.Info, 
     * java.lang.annotation.Annotation[])}.
     */
    public final void testProcessAnnotations() {
        Class < USAddress > clazz = USAddress.class;
        Package pack = clazz.getPackage();
        Annotation[] annotations = pack.getAnnotations();
        PackageAnnotationProcessingService paps = new PackageAnnotationProcessingService();
        JaxbPackageNature packageInfo = new JaxbPackageNature(new PackageInfo(pack.getName()));
        paps.processAnnotations(packageInfo, annotations);
        Assert.assertEquals(XmlAccessType.PROPERTY, packageInfo.getAccessType());
    }
}
