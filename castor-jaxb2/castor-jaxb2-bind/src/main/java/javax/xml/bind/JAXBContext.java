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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.w3c.dom.Node;

/**
 * The entry point for a Java application into the JAXB framework. This class is
 * used to manage the binding between XML element names to Java class.
 * 
 * Associated with a <tt>jaxb.index</tt> file to list Java to XML binding for
 * any non-schema-derived classes.
 * 
 * To avoid the overhead involved in creating a JAXBContext instance, a JAXB
 * application is encouraged to reuse a JAXBContext instance. An implementation
 * of abstract class JAXBContext is required to be thread-safe, thus, multiple
 * threads in an application can share the same JAXBContext instance.
 * 
 * @author Bruce Snyder
 * @version $Revision$
 */
public abstract class JAXBContext {

	private static JAXBContext instance = null;

	public static final String JAXB_CONTEXT_FACTORY = "javax.xml.bind.context.factory";

	/**
	 * Prohibits instantiation of this class. Use one of the
	 * <tt>newInstance()</tt> methods instead.
	 */
	protected JAXBContext() {
	}

	/**
	 * Initializes the JAXBContext using a list of schema-derived classes in the
	 * form of the <tt>listOfPackages</tt>.
	 * 
	 * @param listOfPackages
	 * @return an instance of the JAXBContext
	 */
	public static JAXBContext newInstance(String listOfPackages)
			throws JAXBException {
		return newInstance(listOfPackages, Thread.currentThread()
				.getContextClassLoader());
	}

	public static JAXBContext newInstance(String listOfPackages,
			ClassLoader classLoader) throws JAXBException {
		return newInstance(listOfPackages, classLoader, Collections
				.<String, Object> emptyMap());
	}

	public static JAXBContext newInstance(String listOfPackages,
			ClassLoader classLoader, Map<String, ?> properties)
			throws JAXBException {
		return locateJAXBContext(listOfPackages, classLoader, properties);
	}

	public static JAXBContext newInstance(String listOfPackages,
			String className, ClassLoader classLoader, Map<String, ?> properties) {
		// throws JAXBException {
		Class c;

		try {
			c = classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new JAXBException(e.toString(), e);
		}

		String methodName = "createContext";
		Object jaxbContext = null;

		try {
			// Attempt to invoke createContext(String, ClassLoader, Map)
			Method m = c.getMethod(methodName, String.class, ClassLoader.class,
					Map.class);
			jaxbContext = m.invoke(m, listOfPackages, classLoader);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// Attempt to invoke createContext(String, ClassLoader)
			Method m = c.getMethod(methodName, String.class, ClassLoader.class);
			jaxbContext = m.invoke(m, listOfPackages, classLoader);
		} catch (NoSuchMethodException e) {
			new JAXBException(e.toString(), e);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!(jaxbContext instanceof JAXBContext)) {
			// TODO
		}

		// TODO
		return null;
	}

	public static JAXBContext newInstance(Class[] classesToBeBound)
			throws JAXBException {

	}

	public static JAXBContext newInstance(Class[] classesToBeBound,
			Map<String, ?> properties) throws JAXBException {

	}

	public abstract Unmarshaller createUnmarshaller();

	public abstract Marshaller createMarshaller();

	public abstract JAXBIntrospector createJAXBIntrospector();

	public abstract <T> Binder<T> createBinder(Class<T> domType);

	public abstract Binder<Node> createBinder();

	public abstract void generateSchema(SchemaOutputResolver resolver);

	private JAXBContext locateJAXBContext(String listOfPackages,
			ClassLoader classLoader, Map<String, ?> properties)
			throws JAXBException {
		// Map<String, ?> props = null;
		String factoryClassName;

		for (StringTokenizer tok = new StringTokenizer(listOfPackages, ":"); tok
				.hasMoreTokens();) {
			String packageName = tok.nextToken();
			StringBuilder propertiesFile = new StringBuilder(packageName
					.replace(".", "/")).append("/jaxb.properties");
			Properties props = loadJAXBPropertiesFile(propertiesFile
					.toString(), classLoader);

			Map<String, ?> map = convertPropertiesToMap(props);

			if (props.containsKey(JAXB_CONTEXT_FACTORY)) {
				factoryClassName = props
						.getProperty(JAXB_CONTEXT_FACTORY);
				return newInstance(listOfPackages, factoryClassName,
						classLoader, map);
			}
		}

		// TODO
		return null;
	}

	private Properties loadJAXBPropertiesFile(String propertiesFile,
			ClassLoader classLoader) throws JAXBException {
		URL url = null;
		Properties props = null;

		try {
			InputStream is = classLoader.getResourceAsStream(propertiesFile);
			props.load(is);
			is.close();
		} catch (IOException e) {
			throw new JAXBException(e.toString(), e);
		}

		if (props == null) {
			throw new JAXBException("Unable to load properties file: "
					+ propertiesFile);
		}

		return props;
	}

	private Map<String, ?> convertPropertiesToMap(Properties properties) {
		Map map = new HashMap();

		for (Enumeration<Object> keys = properties.keys(); keys
				.hasMoreElements();) {
			String propName = (String) keys.nextElement();
			map.put(propName, properties.getProperty(propName));
		}
		return map;
	}

}
