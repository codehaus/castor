/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.castor.builder;


import java.util.Properties;
import java.io.OutputStream;
import java.io.Writer;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.net.URL;
import org.exolab.castor.util.Configuration;
import org.exolab.castor.util.Messages;


/**
 * Provides default configuration for Castor Source Generator component from the
 * <tt>castorbuilder.properties</tt> configuration file. 
 * <p>
 * The configuration file is loaded from the Java <tt>lib</tt>
 * directory, the classpath and the Castor JAR. Properties set in the
 * classpath file takes precedence over properties set in the Java library
 * configuration file and properties set in the Castor JAR, allowing for
 * each customization. All three files are named <tt>castorbuilder.properties</tt>.
 * <p>
 * For example, to change the parser in use, specify that all
 * documents should be printed with identantion or turn debugging on,
 * create a new configuration file in the current directory, instead
 * of modifying the global one.
 *
 * @author <a href="mailto:afawcett@coda.com">Andrew Fawcett</a>
 */
public abstract class SourceGeneratorConfiguration
{

    /**
     * Names of properties used in the configuration file.
     */
    public static class Property
    {
		/**
         * Property specifying how element's and type's are mapped into a Java
         * class hierarchy by the Source Generator. 
         * The value must contain one of the following.
         * 'element' outputs a Java class hierarchy based on element 
         * names used in the XML Schema. This is the default.
         * 'type' outputs a Java class hierarchy based on the type 
         * information defined in the XML Schema.
         * <pre>
         * org.exolab.castor.builder.javaclassmapping
         * </pre>
         */
        public static final String JavaClassMapping = "org.exolab.castor.builder.javaclassmapping";

		/**
		 * Property listing mapping between XML namespaces and Java packages.
		 */
		public static final String NamespacePackages = "org.exolab.castor.builder.nspackages";

        /**
         * The name of the configuration file.
         * <pre>
         * castor.properties
         * </pre>
         */
        public static final String FileName = "castorbuilder.properties";

        static final String ResourceName = "/org/exolab/castor/builder/castorbuilder.properties";
    }

    // Some static string definitions
	private static final String ELEMENT_VALUE = "element";
	private static final String TYPE_VALUE = "type";

    private static final int    ELEMENT_BINDING = 0;
    private static final int    TYPE_BINDING    = 1;
    
    /**
     * The flag indicating which type of binding we are
     * configured for
    **/
    private static int  bindingType = ELEMENT_BINDING;
    
	/**
     * The default properties loaded from the configuration file.
     */
    private static Properties _default;

	/**
	 * Namespace URL to Java package mapping
	 */
	private static java.util.Hashtable _nspackages;

    /**
     * Returns the default configuration file. Changes to the returned
     * properties set will affect all Castor functions relying on the
     * default configuration.
     *
     * @return The default configuration
     */
    public static synchronized Properties getDefault()
    {
        if ( _default == null ) {
            load();
        }
        return _default;
    }


    /**
     * Returns a property from the default configuration file.
     * Equivalent to calling <tt>getProperty</tt> on the result
     * of {@link #getDefault}.
     *
     * @param name The property name
     * @param default The property's default value
     * @return The property's value
     */
    public static String getProperty( String name, String defValue )
    {
        return getDefault().getProperty( name, defValue );
    }

	/**
	 * Tests the org.exolab.castor.builder.javaclassmapping property for the 'element' value.
	 * @return True if the Source Generator is mapping schema elements to Java classes.
	 */	
	public static boolean mappingSchemaElement2Java()
	{
	    //-- call getDefault to ensure we loaded the properties
	    getDefault();
	    
	    return (bindingType == ELEMENT_BINDING);  
	}

	/**
	 * Tests the org.exolab.castor.builder.javaclassmapping property for the 'type' value.
	 * @return True if the Source Generator is mapping schema types to Java classes.
	 */	
	public static boolean mappingSchemaType2Java()
	{
	    //-- call getDefault to ensure we loaded the properties
	    getDefault();
	    
	    return (bindingType == TYPE_BINDING);  
	}
	
	/**
	 * Gets a Java package to an XML namespace URL
	 */
	public static String getJavaPackage(String nsURL)
	{
		//-- Ensure properties have been loaded
		getDefault();
		
		// Lookup Java package via NS
		String javaPackage = (String) _nspackages.get(nsURL);
		if(javaPackage==null)
			return "";
		else
			return javaPackage;
	}

	/**
	 * Gets the qualified class name given an XML namespace URL
	 */
	public static String getQualifiedClassName(String nsURL, String className)
	{
		//-- Ensure properties have been loaded
		getDefault();
		
		// Lookup Java package via NS and append class name
		String javaPackage = getJavaPackage(nsURL);
		if (javaPackage.length()>0)
			javaPackage+='.';
		return javaPackage+className;
	}
	
    /**
     * Called by {@link #getDefault} to load the configuration the
     * first time. Will not complain about inability to load
     * configuration file from one of the default directories, but if
     * it cannot find the JAR's configuration file, will throw a
     * run time exception.
     */
    protected static void load()
    {
		_default = Configuration.loadProperties( Property.ResourceName, Property.FileName);
		
		// Parse XML namespace and package list
		_nspackages = new Hashtable();
		String prop = _default.getProperty( Property.NamespacePackages, "");		
		StringTokenizer tokens = new StringTokenizer(prop, ",");
		while(tokens.hasMoreTokens())
		{
			String token = tokens.nextToken();
			int comma = token.indexOf("=");
			if(comma==-1)
				continue;
			String ns = token.substring(0,comma).trim();
			String javaPackage = token.substring(comma+1).trim();
			_nspackages.put(ns, javaPackage);
		}
		
		initBindingType();
    }
    
    /**
     * Called by #load to initialize the binding type
    **/
    protected static void initBindingType() {
		String prop = getDefault().getProperty( Property.JavaClassMapping,  ELEMENT_VALUE);
		if (prop.toLowerCase().equals(TYPE_VALUE))
		    bindingType = TYPE_BINDING;
    } //-- initBindingType
    
} //-- SourceGeneratorConfiguration
