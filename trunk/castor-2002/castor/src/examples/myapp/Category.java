package myapp;


import java.util.Vector;
import java.io.Serializable;


public class Category
    implements Serializable
{

    public int    id;


    public String name;


    public Vector product;


    public int getId() { return id; }


    public String getName() { return name; }


    public String getProducts() {
        String str;

        str = "";
        for ( int i = 0 ; i < product.size() ; ++i ) {
            if ( i > 0 )
                str = str + ",";
            str = str + ( (Product) product.elementAt( i ) ).getName();
        }
        return str;
    }


    public String toString()
    {
        return id + "/" + name;
    }


}
