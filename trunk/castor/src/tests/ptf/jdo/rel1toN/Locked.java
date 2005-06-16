package ptf.jdo.rel1toN;

import java.util.ArrayList;
import java.util.Collection;

public class Locked {
    //-------------------------------------------------------------------------
    
    private Integer         _id;
    private String          _name;
    private boolean         _in;
    private boolean         _out;
    private Collection      _states = new ArrayList();

    //-------------------------------------------------------------------------
    
    public Integer getId() { return _id; }
    public void setId(Integer id) { _id = id; }
    
    public String getName() { return _name; }
    public void setName(String name) { _name = name; }
    
    public boolean getIn() { return _in; }
    public void setIn(boolean in) { _in = in; }
    
    public boolean getOut() { return _out; }
    public void setOut(boolean out) { _out = out; }
    
    public Collection getStates() { return _states; }
    public void setStates(Collection states) {
        _states = states;
    }
    public void addState(State state) {
        if ((state != null) && (!_states.contains(state))) {
            _states.add(state);
            state.setLocked(this);
        }
    }
    public void removeState(State state) {
        if ((state != null) && (_states.contains(state))) {
            _states.remove(state);
            state.setLocked(null);
        }
    }
    
    //-------------------------------------------------------------------------
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<Locked id='"); sb.append(_id);
        sb.append("' name='"); sb.append(_name);
        sb.append("' in='"); sb.append(_in);
        sb.append("' out='"); sb.append(_out);
        sb.append("'/>\n");
        
        return sb.toString();
    }
    
    //-------------------------------------------------------------------------
}
