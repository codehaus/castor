package ptf.jdo.rel1toN;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Service {
    //-------------------------------------------------------------------------
    
	private Integer			_id;
	private Equipment	    _equipment;
	private Integer 		_number;
	private String 			_name;
	private String			_description;
	private Date			_date;
	private boolean			_flag1;
	private boolean 		_flag2;
	private boolean 		_flag3;
	private boolean 		_flag4;
	private String			_note;
	private Date			_createdAt;
	private String			_createdBy;
	private Date			_updatedAt;
	private String			_updatedBy;
    
    //-------------------------------------------------------------------------
	
    public Integer getId() { return _id; }
    public void setId(Integer id) { _id = id; }

    public Equipment getEquipment() { return _equipment; }
    public void setEquipment(Equipment equipment) { _equipment = equipment; }
    
    public Integer getNumber() { return _number; }
    public void setNumber(Integer number) { _number = number; }
    
    public String getName() { return _name; }
    public void setName(String name) { _name =  name; }
    
	public String getDescription() { return _description; }
    public void setDescription(String description) { _description = description; }
    
    public Date getDate() { return _date; }
    public void setDate(Date date) { _date = date; }
    
    public boolean getFlag1() { return _flag1; }
    public void setFlag1(boolean flag1) { _flag1 = flag1; }
    
    public boolean getFlag2() { return _flag2; }
    public void setFlag2(boolean flag2) { _flag2 = flag2; }
    
    public boolean getFlag3() { return _flag3; }
    public void setFlag3(boolean flag3) { _flag3 = flag3; }
    
    public boolean getFlag4() { return _flag4; }
    public void setFlag4(boolean flag4) { _flag4 = flag4; }
    
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
        
        sb.append("<Service id='"); sb.append(_id);
        sb.append("' number='"); sb.append(_number);
        sb.append("' name='"); sb.append(_name);
        sb.append("' description='"); sb.append(_description);
        sb.append("' date='"); sb.append(df.format(_date));
        sb.append("' flag1='"); sb.append(_flag1);
        sb.append("' flag2='"); sb.append(_flag2);
        sb.append("' flag3='"); sb.append(_flag3);
        sb.append("' flag4='"); sb.append(_flag4);
        sb.append("' note='"); sb.append(_note);
        sb.append("' createdAt='"); sb.append(df.format(_createdAt));
        sb.append("' createdBy='"); sb.append(_createdBy);
        sb.append("' updatedAt='"); sb.append(df.format(_updatedAt));
        sb.append("' updatedBy='"); sb.append(_updatedBy);
        sb.append("'>\n");

        sb.append(_equipment);

        sb.append("</Service>\n");
        
        return sb.toString();
    }
    
    //-------------------------------------------------------------------------
}
