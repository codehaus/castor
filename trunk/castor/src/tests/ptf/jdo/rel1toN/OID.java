package ptf.jdo.rel1toN;

public class OID {
    //-------------------------------------------------------------------------
    
    private Integer _id;

    //-------------------------------------------------------------------------

    public Integer getId() { return _id; }
    public void setId(Integer id) { _id = id; }
    
    //-------------------------------------------------------------------------

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<OID identity='");
        sb.append(_id);
        sb.append("'/>\n");
        
        return sb.toString();
    }
    
    //-------------------------------------------------------------------------
}
