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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;

import javax.xml.bind.ValidationEventHandler;

import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.transform.Source; 
import javax.xml.stream.XMLEventReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.Result;

import org.w3c.dom.Node;

/**
 * 
 * @author Bruce Snyder
 * @version $Revision$
 */
public interface Unmarshaller { 

	public void setEventHandler(ValidationEventHandler handler); 
	
	public ValidationEventHandler getEventHandler();
	
	public Object getProperty(java.lang.String name); 
	
	public void setProperty(java.lang.String name, java.lang.Object value); 
	
	public void setSchema(javax.xml.validation.Schema schema); 
	
	public Schema getSchema();
	
	public UnmarshallerHandler getUnmarshallerHandler();
	
	public void setListener(Unmarshaller.Listener); 
	
	public Unmarshaller.Listener getListener(); 
	
	public Object unmarshal(File file);
	
	public Object unmarshal(URL url);
	
	public Object unmarshal(InputStream stream); 
	
	public Object unmarshal(InputSource inputSource); 
	
	public Object unmarshal(Node node); 
	
	public Object unmarshal(Source source); 
	
	public Object unmarshal(XMLStreamReader streamReader);
	
	public Object unmarshal(XMLEventReader eventReader); 
	
	public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType);
	
	public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType);
	
	public <T> JAXBElement<T> unmarshal(XMLStreamReader streamReader, Class<T> declaredType); 
	
	public <T> JAXBElement<T> unmarshal(XMLEventReader eventReader, Class<T> declaredType); 
	
	public void marshal(Object e, Writer writer); 
	
	public void marshal(Object e, OutputStream os); 
	
	public void marshal(Object e, ContentHandler handler); 
	
	public void marshal(Object e, Result result); 
	
	public void marshal(Object e, Node node); 
	
	public void marshal(Object e, XMLStreamWriter writer);
	
	public Node getNode(Object contentTree); 
	
	public class Listener {

	}
}
