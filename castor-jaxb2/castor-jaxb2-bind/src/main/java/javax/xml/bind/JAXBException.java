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
public class JAXBException extends Exception { 
	private String errorCode;
	
	private Throwable cause;
	
	public JAXBException(String message) {
		super(message);
	}
	
	public JAXBException(String message, String errorCode) {
		this(message, errorCode, null);
	}
	
	public JAXBException(Throwable t) {
		this(null, null, t);
	}
	
	public JAXBException(String message, Throwable t) {
		this(message, null, t);
	}
	
	public JAXBException(String message, String errorCode, Throwable t) {
		super(message);
		this.errorCode = errorCode;
		
	}
}
