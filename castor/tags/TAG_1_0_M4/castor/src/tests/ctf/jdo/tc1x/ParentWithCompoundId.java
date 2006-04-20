package ctf.jdo.tc1x;

public class ParentWithCompoundId {
    
    private Integer id1;
    private Integer id2;
    private String name;
    
    public Integer getId1() {
        return id1;
    }
    
    public void setId1(final Integer id) {
        this.id1 = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }

    public Integer getId2() {
        return id2;
    }

    public void setId2(Integer id2) {
        this.id2 = id2;
    }
}
