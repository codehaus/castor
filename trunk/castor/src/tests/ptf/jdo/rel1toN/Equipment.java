package ptf.jdo.rel1toN;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Equipment {
    //-------------------------------------------------------------------------
    
	private Integer 		_id;
    private Type			_type;
    private String			_number;
    private String			_description;
    private Supplier	    _supplier;
    private Integer			_delivery;
    private Double			_cost;
    private String			_serial;
    private State		    _state;
    private Reason	        _reason;
    private Integer			_count;
    private Collection      _services = new ArrayList();
    private String          _note;
    private Date            _createdAt;
    private String          _createdBy;
    private Date            _updatedAt;
    private String          _updatedBy;

    //-------------------------------------------------------------------------
    
    public Integer getId() { return _id; }
    public void setId(Integer id) { _id = id; }

    public Type getType() { return _type; }
    public void setType(Type type) { _type = type; }
    
    public String getNumber() { return _number; }
    public void setNumber(String number) { _number = number; }
    
    public String getDescription() { return _description; }
    public void setDescription(String description) { _description = description; }

    public Supplier getSupplier() { return _supplier; }
    public void setSupplier(Supplier supplier) { _supplier = supplier; }
    
    public Integer getDelivery() { return _delivery; }
    public void setDelivery(Integer delivery) {_delivery = delivery; }
    
    public Double getCost() { return _cost; }
    public void setCost(Double cost) { _cost = cost; }
    
    public String getSerial() { return _serial; }
    public void setSerial(String serial) { _serial = serial; }
    
    public State getState() { return _state; }
    public void setState(State state) { _state = state; }
    
    public Reason getReason() { return _reason; }
    public void setReason(Reason reason) { _reason = reason; }

    public Integer getCount() { return _count; }
    public void setCount(Integer count) { _count = count; }

    public Collection getServices() { return _services; }
    public void setServices(Collection services) {
        _services = services;
    }
    public void addService(Service service) {
        if ((service != null) && (!_services.contains(service))) {
            _services.add(service);
            service.setEquipment(this);
        }
    }
    public void removeService(Service service) {
        if ((service != null) && (_services.contains(service))) {
            _services.remove(service);
            service.setEquipment(null);
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
        
        sb.append("<Equipment id='"); sb.append(_id);
        sb.append("' number='"); sb.append(_number);
        sb.append("' description='"); sb.append(_description);
        sb.append("' delivery='"); sb.append(_delivery);
        sb.append("' cost='"); sb.append(_cost);
        sb.append("' serial='"); sb.append(_serial);
        sb.append("' count='"); sb.append(_count);
        sb.append("' note='"); sb.append(_note);
        sb.append("' createdAt='"); sb.append(df.format(_createdAt));
        sb.append("' createdBy='"); sb.append(_createdBy);
        sb.append("' updatedAt='"); sb.append(df.format(_updatedAt));
        sb.append("' updatedBy='"); sb.append(_updatedBy);
        sb.append("'>\n");

        sb.append(_type);
        sb.append(_supplier);
        sb.append(_state);
        sb.append(_reason);

        sb.append("</Equipment>\n");
        
        return sb.toString();
    }
    
    //-------------------------------------------------------------------------
}
