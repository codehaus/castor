package ptf.jdo.rel1toN;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Type {
    //-------------------------------------------------------------------------
    
	private Integer 		_id;
    private String			_number;
    private String			_description;
    private Collection      _equipments = new ArrayList();
    private String          _note;
    private Date            _createdAt;
    private String          _createdBy;
    private Date            _updatedAt;
    private String          _updatedBy;

    //-------------------------------------------------------------------------

    public Integer getId() { return _id; }
    public void setId(Integer id) { _id = id; }

    public String getNumber() { return _number; }
    public void setNumber(String number) { _number = number; }
    
    public String getDescription() { return _description; }
    public void setDescription(String description) { _description = description; }
    
    public Collection getEquipments() { return _equipments; }
    public void setEquipments(Collection equipments) {
        _equipments = equipments;
    }
    public void addEquipment(Equipment equipment) {
        if ((equipment != null) && (!_equipments.contains(equipment))) {
            _equipments.add(equipment);
            equipment.setType(this);
        }
    }
    public void removeEquipment(Equipment equipment) {
        if ((equipment != null) && (_equipments.contains(equipment))) {
            _equipments.remove(equipment);
            equipment.setType(null);
        }
    }

    public String getNote() { return _note; }
    public void setNote(String note) { _note = note; }
    
    public Date getCreatedAt() { return _createdAt; }
    public void setCreatedAt(Date createdAt) { _createdAt = createdAt; }
    
    public String getCreatedBy() { return _createdBy; }
    public void setCreatedBy(String createdBy) { _createdBy = createdBy; }
    
    public void setCreated(Date createdAt, String createdBy) {
        _createdAt = createdAt;
        _createdBy = createdBy;
    }

    public Date getUpdatedAt() { return _updatedAt; }
    public void setUpdatedAt(Date updatedAt) { _updatedAt = updatedAt; }
    
    public String getUpdatedBy() { return _updatedBy; }
    public void setUpdatedBy(String updatedBy) { _updatedBy = updatedBy; }
    
    public void setUpdated(Date updatedAt, String updatedBy) {
        _updatedAt = updatedAt;
        _updatedBy = updatedBy;
    }
    
    //-------------------------------------------------------------------------
    
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuffer sb = new StringBuffer();
        
        sb.append("<Type id='"); sb.append(_id);
        sb.append("' number='"); sb.append(_number);
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
        sb.append("'/>\n");
        
        return sb.toString();
    }
    
    //-------------------------------------------------------------------------
}
