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

import javax.xml.validation.Schema;

/**
 * 
 * @author Bruce Snyder
 * @version $Revision$
 *
 */
public interface Marshaller {
	
	public static final String JAXB_ENCODING; 
	
	public static final String JAXB_FORMATTED_OUTPUT; 
	
	public static final String JAXB_SCHEMA_LOCATION; 
	
	public static final String JAXB_NO_NAMESPACE_SCHEMA_LOCATION; 
	
	public static final String JAXB_FRAGMENT; 

	public Object getProperty(String name); 
	
	public void setProperty(String name, Object value); 
	
	public void setEventHandler(ValidationEventHandler handler); 
	
	public ValidationEventHandler getEventHandler(); 
	
	void setSchema(Schema schema);
	
	public Schema getSchema();
	
	void setListener(Unmarshaller.Listener);
	
	public Unmarshaller.Listener getListener();
	
	public class Listener {
		
	}
}
