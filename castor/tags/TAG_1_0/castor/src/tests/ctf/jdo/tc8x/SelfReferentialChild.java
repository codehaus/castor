package ctf.jdo.tc8x;

public final class SelfReferentialChild extends SelfReferentialParent {

    private String _description;
    
    public String getDescription() {
        return _description;
    }
    public void setDescription(final String description) {
        _description = description;
    }
}
