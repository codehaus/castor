

package jdo;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;



public class TestPKsContract {
    private int _policyNo;
    private int _contractNo;
    private String _comment;

    public void setPolicyNo( int policy ) {
        _policyNo = policy;
    }
    public int getPolicyNo() {
        return _policyNo;
    }
    public void setContractNo( int no ) {
        _contractNo = no;
    }
    public int getContractNo() {
        return _contractNo;
    }
    public String getComment() {
        return _comment;
    }
    public void setComment( String s ) {
        _comment = s;
    }
}