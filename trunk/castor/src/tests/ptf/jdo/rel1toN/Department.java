package ptf.jdo.rel1toN;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Department {
    //-------------------------------------------------------------------------
    
    private Integer         _id;
    private String          _name;
    private String          _description;
    private State   		_state;
    private String          _note;
    private Date            _createdAt;
    private String          _createdBy;
    private Date            _updatedAt;
    private String          _updatedBy;

    //-------------------------------------------------------------------------
    
    public Integer getId() { return _id; }
    public void setId(Integer id) { _id = id; }
    
    public String getName() { return _name; }
    public void setName(String name) { _name = name; }
    
    public String getDescription() { return _description; }
    public void setDescription(String description) { _description = description; }
    
    public State getState() { return _state; }
    public void setState(State state) { _state = state; }
    
    public final String getNote() { return _note; }
    public final void setNote(String note) { _note = note; }
    
    public final Date getCreatedAt() { return _createdAt; }
    public void setCreatedAt(Date createdAt) { _createdAt = createdAt; }

    public final String getCreatedBy() { return _createdBy; }
    public void setCreatedBy(String createdBy) { _createdBy = createdBy; }
    
    public final void setCreated(Date createdAt, String createdBy) {
        _createdAt = createdAt;    
        _createdBy = createdBy;
    }

    public final Date getUpdatedAt() { return _updatedAt; }
    public void setUpdatedAt(Date updatedAt) { _updatedAt = updatedAt; }

    public final String getUpdatedBy() { return _updatedBy; }
    public void setUpdatedBy(String updatedBy) { _updatedBy = updatedBy; }
    
    public final void setUpdated(Date updatedAt, String updatedBy) {
        _updatedAt = updatedAt;    
        _updatedBy = updatedBy;
    }
    
    //-------------------------------------------------------------------------
    
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuffer sb = new StringBuffer();
        
        sb.append("<Department id='"); sb.append(_id);
        sb.append("' name='"); sb.append(_name);
        sb.append("' description='"); sb.append(_description);
        sb.append("' note='"); sb.append(_note);
        sb.append("' createdAt='"); 
        if (_createdAt != null) {
            sb.append(df.format(_createdAt));
        } else {
            sb.append(_createdAt);
        }
        sb.append("' createdBy='"); sb.append(_createdBy);
        sb.append("' updatedAt='"); 
        if (_updatedAt != null) { 
            sb.append(df.format(_updatedAt));
        } else {
            sb.append(_updatedAt);
        }
        sb.append("' updatedBy='"); sb.append(_updatedBy);
        sb.append("'>\n");

        sb.append(_state);

        sb.append("</Department>\n");
        
        return sb.toString();
    }
    
    //-------------------------------------------------------------------------
}
