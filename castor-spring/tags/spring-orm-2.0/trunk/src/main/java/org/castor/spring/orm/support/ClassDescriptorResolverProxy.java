package org.castor.spring.orm.support;

import java.util.ArrayList;
import java.util.List;

public class ClassDescriptorResolverProxy {

    /**
     * A collection of package names. 
     */
    private List<String> packages = new ArrayList<String>();
    
    /**
     * A collection of class names.
     */
    private List<String> classes = new ArrayList<String>();

    /**
     * Adds a new package name.
     * @param packageName A new package name.
     */
    public void addPackage(String packageName) {
        this.packages .add(packageName);
    }

    /**
     * Adds a new class name.
     * @param className A new class name.
     */
    public void addClass(String className) {
        this.classes .add(className);
    }

    /**
     * Returns a collection of package names.
     * @return A collecgtion of package names.
     */
    public List<String> getPackages() {
        return packages;
    }

    /**
     * Returns a collection of class names.
     * @return A collection of class names.
     */
    public List<String> getClasses() {
        return classes;
    }

}
