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
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id$
 */
package org.exolab.castor.tests.framework;

import com.intalio.utils.XMLDiff;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.exolab.castor.tests.framework.ObjectModelInstanceBuilder;
import org.exolab.castor.tests.framework.CastorTestable;
import org.exolab.castor.tests.framework.testDescriptor.MarshallingTest;
import org.exolab.castor.tests.framework.testDescriptor.UnitTestCase;
import org.exolab.castor.tests.framework.testDescriptor.RootObject;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.Marshaller;

import org.xml.sax.InputSource;

import java.lang.reflect.Method;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

/**
 * This class encapsulate all the logic to run the test patterns for the
 * marshalling framework of castor. This include introspection and mapping.
 *
 * @author <a href="mailto:gignoux@intalio.com">Sebastien Gignoux</a>
 * @version $Revision$ $Date$
 */
public class MarshallingFrameworkTestCase extends TestCase {

    /**
     * Name of this test
     */
    protected String _name;

    /**
     * Mapping file to use for this test
     */
    protected Mapping _mapping;

    /**
     * The jarTest in which this test is specified
     */
    protected CastorJarTestCase _jarTest;

    /**
     * Contain the information for the configuration of all the test of this jar.
     */
    protected MarshallingTest _marshallingConf;

    /**
     * The unit test case this class represent
     */
    protected UnitTestCase _unitTest;

    /**
     * Place where the temporary file have to be put
     */
    protected File _outputRootFile;

    /**
     * The name of the input file for this test if any.
     */
    private String _inputName;

    /**
     * The name of the output file for this test if any.
     */
    private String _outputName;

    /**
     * The input file for this test if any.
     */
    private InputStream _input = null;

    /**
     * The output reference file for this test
     */
    private InputStream _output = null;

    /**
     * The name of the root class for this test
     */
    private String _rootClassName;

    /**
     * The root class for this test
     */
    private Class  _rootClass;

    /**
     * If true, the dumpFields() function has been implemented in the rootClass
     */
    private boolean _hasDump;

    /**
     * If true, the randomizeFields() function has been implemented in the rootClass
     */
    private boolean _hasRandom;

    /**
     * True if we expect a lot of info on what happen.
     */
    private static boolean _verbose;

    /**
     * The ouput the unmarshalling test.
     */
    private static Object _unmarshallingOutput;

    static {
        String v = System.getProperty(TestCaseAggregator.VERBOSE_PROPERTY);
        if (v!=null && v.equals("true"))
            _verbose = true;
        else
            _verbose = false; 
    }


    /**
     * Create a new test case for the given setup.
     */
    public MarshallingFrameworkTestCase(CastorJarTestCase jarTest, UnitTestCase unit, MarshallingTest marshalling, File outputRoot) {
        super(unit.getName());
        _name            = unit.getName();
        _jarTest         = jarTest;
        _marshallingConf = marshalling;
        _unitTest        = unit;
        _outputRootFile  = outputRoot;
        _hasRandom       = _marshallingConf.getRootObject().getRandom();
    }

    /**
     * Create a new test case with the same setup as the
     * MarshallingFrameworkTestCase given in parameter.
     */
    public MarshallingFrameworkTestCase(String name, MarshallingFrameworkTestCase mftc) {
        super(name);
        _name            = mftc._name;
        _jarTest         = mftc._jarTest;
        _marshallingConf = mftc._marshallingConf;
        _unitTest        = mftc._unitTest;
        _outputRootFile  = mftc._outputRootFile;
        
    }
    
    /**
     * Create a new MarshallingFrameworkTestCase with the given name.
     */
    public MarshallingFrameworkTestCase(String name) {
        super(name);
        _name = name;
    }

    /**
     * Return the test suite for this given test setup.
     */
    public Test suite() {
        
        TestSuite suite  = new TestSuite(_name);
        
        suite.addTest(new MarshallingFrameworkTestCase("testWithReferenceDocument", this));

        if (_hasRandom)
            suite.addTest(new MarshallingFrameworkTestCase("testWithRandomObject", this));

        return suite;
    }


    /**
     * Setup this test suite. Load the mapping file if any.
     */
    protected void setUp()
        throws java.lang.Exception {

        verbose("\n========================================");
        verbose("Setting up test for '" + _name + "' from '" + _jarTest.getName() + "'");

        _inputName  = _unitTest.getInput();
        _outputName = _unitTest.getOutput();

        if (_inputName != null)
            _input  = _jarTest.getClassLoader().getResourceAsStream(_inputName);

        if (_outputName != null)
            _output = _jarTest.getClassLoader().getResourceAsStream(_outputName);

        RootObject rootType = _marshallingConf.getRootObject();
        _rootClassName = rootType.getContent();
        _hasDump =   rootType.getDump();
        _hasRandom = rootType.getRandom();

        if (_rootClassName == null)
            throw new Exception("No object root found in test descriptor"); 

        _rootClass =  _jarTest.getClassLoader().loadClass(_rootClassName);

        // Try to load the mapping file if any, else we will use the introspector
        String mappingFilePath = _marshallingConf.getMappingFile();

        if (mappingFilePath == null) {
            verbose("### TESTING INTROSPECTION ###");
            _mapping = null;
        } else {
            verbose("### TESTING MAPPING ###");
            verbose("Mapping file: " + mappingFilePath);
            InputStream mappingFile = _jarTest.getClassLoader().getResourceAsStream(mappingFilePath);
            
            if (mappingFile == null)
                throw new Exception("Unable to locate the mapping file '" + _marshallingConf.getMappingFile() + "' in the jar '" + _jarTest.getName() + "'");
            
            _mapping = new Mapping(_jarTest.getClassLoader());
            _mapping.loadMapping(new InputSource(mappingFile));
        }
    }
    

    /**
     * Create an object model instance by using the randomize function. Marshal
     * it to a file, unmarshall again and check that the object is equals to the
     * first object.
     */
    public void testWithRandomObject()  
        throws java.lang.Exception {

        verbose("Test with randomly generated object");

        String outputName = _name.replace(' ', '_') + "-testWithRandomObject";

        // 1. Randomize an object model instance 
        verbose("Randomize an object model instance with '" + _rootClassName + "' as root");
        CastorTestable randomizedObject = ((CastorTestable)_rootClass.newInstance());
        assertNotNull("Randomized object model is null", randomizedObject);

        randomizedObject.randomizeFields();

        // 2. Dump the object in a file if possible
        if (_hasDump) {
            verbose("Dump the object into '" + outputName + "-ref.dump" +"'");
            FileWriter writer = new FileWriter(new File(_outputRootFile, outputName + "-ref.dump"));
            writer.write(((CastorTestable)randomizedObject).dumpFields());
            writer.close();
        }
            

        // 3. Marshal
        verbose("Marshal the object into '" + outputName + ".xml" +"'");
        File marshal_output = testMarshal(randomizedObject, outputName + ".xml");

        // 4. Unmarshal
        verbose("Unmarshal the file");
        Object  unmrashalledRandomizedObject= testUnmarshal(marshal_output);
        assertNotNull("Unmarshalled object from file '" + marshal_output.getName() + "' is null", unmrashalledRandomizedObject);

        // 5. Dump the unmarshalled object in a file if possible
        if (_hasDump) {
            verbose("Dump the object into '" + outputName + "-unmar.dump" +"'");
            FileWriter writer = new FileWriter(new File(_outputRootFile, outputName + "-unmar.dump"));
            writer.write(((CastorTestable)randomizedObject).dumpFields());
            writer.close();
        }
        
        // 6. compare to initial model instance
        boolean result = unmrashalledRandomizedObject.equals(randomizedObject);
        verbose("Compare to reference object: " + ((result)?"OK":" ### Failed ### "));
        assert("The initial randomized object and the one that have been created by marshal/unmrashal are different", result);
    }


    /**
     * Use reference input, output file and ObjectModelInstanceBuilder if any to marshall, unmarshall
     */
    public void testWithReferenceDocument()  
        throws java.lang.Exception {

        verbose("Test with reference document");

        // 1. Unmarshall Input file if any
        Object out = null;
        if (_input != null) {
            verbose("Umarshall '" + _inputName  + "'");
            out = testUnmarshal(_input);
        }

        // 2. Compare with ObjectModelInstanceBuilder if any
        String builderClassName = _unitTest.getObjectModelInstanceBuilder();
        Object generated = null;
        if (builderClassName != null) {
            generated = buildObjectModelInstance(builderClassName);
            assertNotNull("The generated object with '" + builderClassName + "' is null", generated);
        }

        if (out != null) {
            boolean result = generated.equals(out);
            verbose("Compare to reference object: " + ((result)?"OK":"Failed"));
            assert(result);
        } else if (generated != null) {
            // We don't have an input file, but we can use the hardcoded object for the next steps
            out = generated;
        } else {
            // we have to input file and no hardcoded object, we can't test nothing
            throw new Exception("Unable to have a valid input file or a hardcoded object in '" + _name + "'");
        }

        // 3. Marshall the object
        String outputName = _name.replace(' ', '_') + "-testWithReferenceDocument.xml";
        verbose("Marshal the object into '" + outputName +"'");
        File marshal_output = testMarshal(out, outputName);

        // 4. Compare with output file if any
        if (_output != null) {
            boolean result = XMLDiff.compareInputSource(new InputSource(_output), 
                                                        new InputSource(new FileInputStream(marshal_output)));
            verbose("Compare to reference file '" + _outputName + "': " + ((result)?"OK":"Failed"));
            assert("Marshalled object differ from the reference file", result);
        }

        // 5. umarshall outpur file and compare to ObjectModelInstanceBuilder
        Object outAgain = testUnmarshal(marshal_output);
        assertNotNull("Unmarshalled object from file '" + marshal_output.getName() + "' is null", outAgain);
        boolean result  = outAgain.equals(out);
        verbose("Compare to reference object: " + ((result)?"OK":" ### Failed ### "));
        assert("The unmarshalled object differ from the referene object", result);
    }
    
    /**
     * Clean up the tests.
     */
    protected void tearDown()
        throws java.lang.Exception {
        
        verbose("Test for '" + _name + "' complete");
        verbose("========================================");

    }


    /**
     * Marshall the object with the configuration of the test.
     */
    private File testMarshal(Object object, String fileName) 
        throws java.lang.Exception {

        File marshalOutput    = new File(_outputRootFile, fileName);
        Marshaller marshaller = new Marshaller(new FileWriter(marshalOutput));
        
        if (_mapping != null)
            marshaller.setMapping(_mapping);
        
        marshaller.marshal(object);

        return marshalOutput;
    }


    /**
     * Unmarshal the given file with the configuration of the test
     */
    private Object testUnmarshal(File file)
        throws java.lang.Exception {

        return testUnmarshal(new FileInputStream(file));
    }

    /**
     * Unmarshal the given stream with the configuration of the test
     */
    private Object testUnmarshal(InputStream stream)
        throws java.lang.Exception {

        Unmarshaller unmar;
        if (_mapping == null)
            unmar = new Unmarshaller(_rootClass, _jarTest.getClassLoader());
        else
            unmar = new Unmarshaller(_mapping);

        return unmar.unmarshal(new InputSource(stream));
    }


    /**
     * return an instance of the object model hardcoded in the given
     * ObjectModelInstanceBuilder.
     */
    private Object buildObjectModelInstance(String builderName) 
        throws java.lang.Exception {
        Class builderClass = _jarTest.getClassLoader().loadClass(builderName);
        ObjectModelInstanceBuilder builder = (ObjectModelInstanceBuilder)builderClass.newInstance();
        return builder.buildInstance();
    }

    /**
     * print the message if in verbose mode.
     */
    private void verbose(String message) {
        if (_verbose)
            System.out.println(message);
    }
}
