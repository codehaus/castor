package myapp;


import java.util.Vector;
import java.util.Enumeration;


public class Product
{


    private int          _id;


    private String       _name;


    private float        _price;


    private ProductGroup _group;


    private Vector       _details = new Vector();


    private Vector       _categories = new Vector();


    public int getId()
    {
        return _id;
    }


    public void setId( int id )
    {
        _id = id;
    }


    public String getName()
    {
        return _name;
    }


    public void setName( String name )
    {
        _name = name;
    }


    public float getPrice()
    {
        return _price;
    }


    public void setPrice( float price )
    {
        _price = price;
    }


    public ProductGroup getGroup()
    {
        return _group;
    }


    public void setGroup( ProductGroup group )
    {
        _group = group;
    }


    public ProductDetail createDetail()
    {
        return new ProductDetail();
    }


    public Vector getDetails()
    {
        return _details;
    }


    public void addDetail( ProductDetail detail )
    {
        _details.addElement( detail );
        detail.setProduct( this );
    }


    public String toString()
    {
        return _id + " " + _name;
    }


}
