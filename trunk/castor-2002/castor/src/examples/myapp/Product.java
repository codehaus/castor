package myapp;


import java.util.Vector;
import java.io.Serializable;


public class Product
    implements Serializable
{

    public int       id;


    private boolean  flag;


    public String    name = "";


    public float     price;


    public ProductGroup     group;


    //    public Vector           detail;
    public ProductDetail     detail;


    public String toString()
    {
        return ( Integer.toString( id ) ) + " " +
            ( name == null ? "<no-name>" : name ) + " $" + price + " " +
            ( group == null ? "<no-group>" : "[" + group.toString() + "]" ) + " " +
            ( detail == null ? "<no-detail>" : detail.toString() );
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
