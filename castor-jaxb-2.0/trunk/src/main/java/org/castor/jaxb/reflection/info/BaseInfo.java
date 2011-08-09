package org.castor.jaxb.reflection.info;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.castor.core.nature.PropertyHolder;

public class BaseInfo implements PropertyHolder {

    private Map<String, Object> properties = new HashMap<String, Object>();

    private Set<String> natures = new HashSet<String>();

    protected Map<String, Object> getProperties() {
        return this.properties;
    }

    protected void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    protected Set<String> getNatures() {
        return this.natures;
    }

    protected void setNatures(Set<String> natures) {
        this.natures = natures;
    }

    public final void addNature(final String nature) {
        this.natures.add(nature);
    }

    public final boolean hasNature(final String nature) {
        return this.natures.contains(nature);
    }

    public final Object getProperty(final String name) {
        return this.properties.get(name);
    }

    public final void setProperty(final String name, final Object value) {
        this.properties.put(name, value);
    }

}
