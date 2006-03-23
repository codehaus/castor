package ctf.jdo.tc7x;

public class SelfReferentialChild extends SelfReferentialParent {

    private String description;
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
