

package jdo;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;



public class TestPKsContractCategory {
    private int _id;
	private ArrayList _contract;

    public void setId( int id ) {
        _id = id;
    }
    public int getId() {
        return _id;
    }
	public void setContract( ArrayList contract ) {
		_contract = contract;
	}
	public ArrayList getContract() {
		return _contract;
	}
}