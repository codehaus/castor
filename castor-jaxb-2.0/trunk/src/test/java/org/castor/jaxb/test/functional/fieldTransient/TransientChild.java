package org.castor.jaxb.test.functional.fieldTransient;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class TransientChild {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
