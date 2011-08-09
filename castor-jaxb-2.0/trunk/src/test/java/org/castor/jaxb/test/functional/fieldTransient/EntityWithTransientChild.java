package org.castor.jaxb.test.functional.fieldTransient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entityWithTransientChild")
public class EntityWithTransientChild {

    private String name;

    private TransientChild child;

    public TransientChild getChild() {
        return child;
    }

    public void setChild(TransientChild child) {
        this.child = child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
