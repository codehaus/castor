package jdo;


public class TestObject
{


    private int    _id;


    private String _name;


    static final int       DefaultId = 3;


    static final String    DefaultName = "three";


    public TestObject()
    {
        _id = DefaultId;
        _name = DefaultName;
    }


    public void setId( int id )
    {
        _id = id;
    }


    public int getId()
    {
        return _id;
    }


    public void setName( String name )
    {
        _name = name;
    }


    public String getName()
    {
        return _name;
    }


    public String toString()
    {
        return _id + " / " + _name;
    }


}
