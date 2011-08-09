package org.castor.jaxb.test.functional.accessOrderClassLavel;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entityWithUndefinedOrder")
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
public class EntityWithUndefinedOrder {

    @XmlElement
    private String number;
    
    @XmlElement
    private String name;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
