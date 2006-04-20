package ctf.jdo.tc7x;

import java.util.List;

public class SelfReferentialParent {
    
    private Integer id;
    private String name;
    private SelfReferentialParent parent;
    private List entities;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }

    public List getEntities() {
        return entities;
    }

    public void setEntities(List entities) {
        this.entities = entities;
    }

    public SelfReferentialParent getParent() {
        return parent;
    }

    public void setParent(SelfReferentialParent parent) {
        this.parent = parent;
    }

}
