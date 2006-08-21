/*
 * Copyright 2006 Bruce Snyder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml.bind;

/**
 * The Binder class is responsible for maintaining the relationship between a 
 * infoset preserving view of an XML document with a possibly partial binding of 
 * the XML document to a JAXB representation.
 *
 * @version $Revision$
 *
 */
public abstract class Binder<XmlNode> { 
 
    public abstract Object unmarshal(XmlNode node); 
    
    public <T> JAXBElement<T> unmarshal(Node, Class<T> declaredType);
    
    public abstract void marshal(Object jaxbObjcet, XmlNode);
     
    public abstract XmlNode getXMLNode(Object jaxbObject); 
    
    public abstract Object getJAXBNode(XmlNode); 

    public abstract XmlNode updateXML(Object jaxbObject);
    
    public abstract XmlNode updateXML(Object jaxbObject, XmlNode node) throws JAXBException; 
    
    public abstract Object updateJAXB( XmlNode) throws JAXBException; 

    public abstract void setSchema(Schema); 
    
    public abstract Schema getSchema(); 
    
    public abstract void setEventHandler( ValidationEventHandler) throws JAXBException; 
    
    public abstract ValidationEventHandler getEventHandler() throws JAXBException; 
 
    public abstract void setProperty(String name, Object value) throws PropertyException; 
    
    public abstract Object getProperty(String name) throws PropertyException; 
}
