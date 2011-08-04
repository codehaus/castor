package org.castor.test;

import javax.xml.bind.annotation.XmlAttribute;

public class ChildWithNameAsAttribute {

    @XmlAttribute
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
