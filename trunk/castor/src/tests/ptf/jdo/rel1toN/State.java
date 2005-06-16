package ptf.jdo.rel1toN;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class State {
    //-------------------------------------------------------------------------

    private Integer     	_id;
    private String          _name;
    private Locked	        _locked;
    private boolean         _input = false;
    private boolean         _output = false;
    private boolean         _service = false;
    private boolean         _changeFrom = true;
    private boolean         _changeTo = true;
    private Collection      _departments = new ArrayList();
    private Collection      _equipments = new ArrayList();
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
    
    public Locked getLocked() { return _locked; }
    public void setLocked(Locked locked) { _locked = locked; }
    
    public boolean getInput() { return _input; }
    public void setInput(boolean input) { _input = input; }
    
    public boolean getOutput() { return _output; }
    public void setOutput(boolean output) { _output = output; }
    
    public boolean getService() { return _service; }
    public void setService(boolean service) { _service = service; }
    
    public boolean getChangeFrom() { return _changeFrom; }
    public void setChangeFrom(boolean changeFrom) { _changeFrom = changeFrom; }
    
    public boolean getChangeTo() { return _changeTo; }
    public void setChangeTo(boolean changeTo) { _changeTo = changeTo; }
    
    public Collection getDepartments() { return _departments; }
    public void setDepartments(Collection departments) {
        _departments = departments;
    }
    public void addDepartment(Department department) {
        if ((department != null) && (!_departments.contains(department))) {
            _departments.add(department);
            department.setState(this);
        }
    }
    public void removeDepartment(Department department) {
        if ((department != null) && (_departments.contains(department))) {
            _departments.remove(department);
            department.setState(null);
        }
    }
    
    public Collection getEquipments() { return _equipments; }
    public void setEquipments(Collection equipments) {
        _equipments = equipments;
    }
    public void addEquipment(Equipment equipment) {
        if ((equipment != null) && (!_equipments.contains(equipment))) {
            _equipments.add(equipment);
            equipment.setState(this);
        }
    }
    public void removeEquipment(Equipment equipment) {
        if ((equipment != null) && (_equipments.contains(equipment))) {
            _equipments.remove(equipment);
            equipment.setState(null);
        }
    }

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
        
        sb.append("<State id='"); sb.append(_id);
        sb.append("' name='"); sb.append(_name);
        sb.append("' input='"); sb.append(_input);
        sb.append("' output='"); sb.append(_output);
        sb.append("' service='"); sb.append(_service);
        sb.append("' changeFrom='"); sb.append(_changeFrom);
        sb.append("' changeTo='"); sb.append(_changeTo);
        sb.append("' note='"); sb.append(_note);
        sb.append("' createdAt='"); sb.append(df.format(_createdAt));
        sb.append("' createdBy='"); sb.append(_createdBy);
        sb.append("' updatedAt='"); sb.append(df.format(_updatedAt));
        sb.append("' updatedBy='"); sb.append(_updatedBy);
        sb.append("'>\n");

        sb.append(_locked);
        
        sb.append("</State>\n");

        return sb.toString();
    }
    
    //-------------------------------------------------------------------------
}
