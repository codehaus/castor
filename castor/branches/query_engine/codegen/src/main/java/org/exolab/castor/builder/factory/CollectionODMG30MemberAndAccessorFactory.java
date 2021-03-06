package org.exolab.castor.builder.factory;

import org.castor.xml.JavaNaming;
import org.exolab.castor.builder.SGTypes;
import org.exolab.castor.builder.info.CollectionInfo;
import org.exolab.castor.builder.info.FieldInfo;
import org.exolab.javasource.JClass;
import org.exolab.javasource.JMethod;
import org.exolab.javasource.JSourceCode;


/**
 * A JField factory for CollectionODMG30Member.
 */
public class CollectionODMG30MemberAndAccessorFactory extends CollectionMemberAndAccessorFactory {

    /**
     * Creates a new CollectionODMG30MemberAndAccessorFactory.
     * @param naming the javaNaming to use
     */
    public CollectionODMG30MemberAndAccessorFactory(final JavaNaming naming) {
        super(naming);
    }

    /**
     * {@inheritDoc}
     */
    public void generateInitializerCode(final FieldInfo fieldInfo, 
            final JSourceCode jsc) {
        jsc.add("this.");
        jsc.append(fieldInfo.getName());
        jsc.append(" = ODMG.getImplementation().newDArray();");
    }

    /**
     * {@inheritDoc}
     */
    protected void createEnumerateMethod(final CollectionInfo fieldInfo, 
            final JClass jClass, final boolean useJava50) {
        JMethod method = new JMethod("enumerate" + fieldInfo.getMethodSuffix(),
                SGTypes.createEnumeration(fieldInfo.getContentType().getJType(), useJava50),
                "an Enumeration over all elements of this collection");

        if (!jClass.hasImport("java.util.Vector")) {
            jClass.addImport("java.util.Vector");
        }

        if (!jClass.hasImport("java.util.Iterator")) {
            jClass.addImport("java.util.Iterator");
        }

        JSourceCode sourceCode = method.getSourceCode();
        sourceCode.add("Vector v = new Vector();"); // ODMG 3.0
        sourceCode.add("Iterator i = ");
        sourceCode.append(fieldInfo.getName());
        sourceCode.append(".iterator();");
        sourceCode.add("");
        sourceCode.add("while (i.hasNext()) {");
        sourceCode.indent();
        sourceCode.add("v.add(i.next());");
        sourceCode.unindent();
        sourceCode.add("");
        sourceCode.add("return v.elements();");

        jClass.addMethod(method);
    }
	
}
