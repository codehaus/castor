package xml.srcgen.template;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.exolab.castor.builder.SourceGenerator;
import org.xml.sax.InputSource;

public class TestSourceGenerator extends TestCase {

    public void testGeneration() throws Exception {
        SourceGenerator generator = new SourceGenerator();
        String xmlSchema = getClass().getResource("test.xsd").toExternalForm();
        InputSource inputSource = new InputSource(xmlSchema);
        generator.setDestDir("./src/bugs");
        generator.setSuppressNonFatalWarnings(true);
        
        generateSource(generator, inputSource);
    }

//    public void testGenerationWithBinding() throws Exception {
//        SourceGenerator generator = new SourceGenerator();
//        String xmlSchema = getClass().getResource("test.xsd").toExternalForm();
//        InputSource inputSource = new InputSource(xmlSchema);
//        generator.setBinding(new InputSource(getClass().getResource("binding.xml").toExternalForm()));
//        generator.setDestDir("./src/bugs");
//        generator.setSuppressNonFatalWarnings(true);
//        
//        generator.generateSource(inputSource, getClass().getPackage().getName() + ".generated");
//    }
//
//    public void testGenerationWithBuilderProperties() throws Exception {
//        SourceGenerator generator = new SourceGenerator();
//        String xmlSchema = getClass().getResource("test.xsd").toExternalForm();
//        InputSource inputSource = new InputSource(xmlSchema);
//        generator.setDestDir("./src/bugs");
//        generator.setSuppressNonFatalWarnings(true);
//        
//        Properties properties = new Properties();
//        properties.load(getClass().getResource("special-castorbuilder.properties").openStream());
//        generator.setDefaultProperties(properties);
//        
//        generator.generateSource(inputSource, getClass().getPackage().getName() + ".generated");
//    }
//
//    public void testGenerationWithBindingAndBuilderProperties() throws Exception {
//        SourceGenerator generator = new SourceGenerator();
//        String xmlSchema = getClass().getResource("test.xsd").toExternalForm();
//        InputSource inputSource = new InputSource(xmlSchema);
//        generator.setBinding(new InputSource(getClass().getResource("binding.xml").toExternalForm()));
//        generator.setDestDir("./src/bugs");
//        generator.setSuppressNonFatalWarnings(true);
//        
//        Properties properties = new Properties();
//        properties.load(getClass().getResource("special-castorbuilder.properties").openStream());
//        generator.setDefaultProperties(properties);
//        
//        generator.generateSource(inputSource, getClass().getPackage().getName() + ".generated");
//    }
    
    private void generateSource(SourceGenerator generator, InputSource inputSource) throws IOException {
        generator.generateSource(inputSource, getClass().getPackage().getName() + ".generated");
    }
}

