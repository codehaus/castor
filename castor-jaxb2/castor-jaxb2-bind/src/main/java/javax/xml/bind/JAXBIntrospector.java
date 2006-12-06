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
 * 
 * @author Bruce Snyder
 * @version $Revision$
 *
 */
public abstract class JAXBIntrospector {

	public abstract boolean isElement(Object jaxbObj); 
	
	public abstract QName getElementName(Object jaxbElement); 
	
	public abstract static Object getValue(Object jaxbElement);
}
