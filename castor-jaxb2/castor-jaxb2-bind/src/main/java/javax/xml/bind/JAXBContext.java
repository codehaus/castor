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
 *  The entry point for a Java application into the JAXB framework. This class 
 *  is used to manage the binding between XML element names to Java class. 
 *  
 *  Associated with a <tt>jaxb.index</tt> file to list Java to XML binding for 
 *  any non-schema-derived classes. 
 *  
 *  To avoid the overhead involved in creating a JAXBContext instance, a JAXB 
 *  application is encouraged to reuse a JAXBContext instance. An implementation 
 *  of abstract class JAXBContext is required to be thread-safe, thus, multiple 
 *  threads in an application can share the same JAXBContext instance.
 *  
 * @version $Revision$
 */
public abstract class JAXBContext {
	
	static final String JAXB_CONTEXT_FACTORY; 
	
	/**
	 * Initializes the JAXBContext using a list of schema-derived classes in 
	 * the form of the <tt>ctxPath</tt>.
	 * 
	 * @param ctxPath
	 * @return an instance of the JAXBContext
	 */
	public static JAXBContext newInstance(String ctxPath); 
	
	public static JAXBContext newInstance(String ctxPath, ClassLoader classLoader); 
	
	public static JAXBContext newInstance(Class classesToBeBound); 
	
	public abstract Unmarshaller createUnmarshaller(); 
	
	public abstract Marshaller createMarshaller(); 
	
	public abstract JAXBIntrospector createJAXBIntrospector(); 
	
	public abstract <T> Binder<T> createBinder(Class<T> domType); 
	
	public abstract Binder<org.w3c.dom.Node> createBinder(); 
	
	public abstract void generateSchema(SchemaOutputResolver); 

}
