package org.castor.jaxb.allannotations;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.math.BigDecimal;

/**
 *
 */
@XmlRootElement
public class ForXmlValue {
    @XmlValue
    public BigDecimal content;
}
