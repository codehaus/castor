package myapp;


import java.io.Serializable;


public class ProductGroup
    implements Serializable
{


    public int        id;


    // Not null field
    public String     name = "";


    public int getId() { return id; }
    public String getName() { return name; }


    public String toString()
    {
	return id + " " +   ( name == null ? "<no-group>" : name );
    }


}

