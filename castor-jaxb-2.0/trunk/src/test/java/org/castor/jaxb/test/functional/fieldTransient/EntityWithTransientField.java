package org.castor.jaxb.test.functional.fieldTransient;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "entityWithTransientField")
public class EntityWithTransientField {

    @XmlTransient
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
