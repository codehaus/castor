package myapp;


import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;


public class Product
    implements Serializable
{


    public int       id;


    private boolean  flag;


    public String    name = "";


    public float     price;


    public ProductGroup     group;


    public Vector           detail;


    public Vector           category;


    public int getId() { return id; }

    public String getName() { return name; }

    public float getPrice() { return price; }

    public ProductGroup getGroup() { return group; }

    public Enumeration getDetail() { return detail.elements(); }


    public String toString()
    {
        return ( Integer.toString( id ) ) + " " +
            ( name == null ? "<no-name>" : name ) + " $" + price + " " +
            ( group == null ? "<no-group>" : "[" + group.toString() + "]" ) + " " +
            ( detail == null ? "<no-detail>" : toString( detail ) );
    }


    private String toString( Vector vector )
    {
        String str;

        str = "{";
        for ( int i = 0 ; i < vector.size() ; ++ i ) {
            if ( i > 0 )
                str = str + ",";
            str = str + vector.elementAt( i ).toString();
        }
        return str + "}";
    }


}
