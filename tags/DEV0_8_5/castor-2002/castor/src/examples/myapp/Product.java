package myapp;


import java.util.Vector;
import java.util.Enumeration;


public class Product
{


    private int          _id;


    private String       _name;


    private float        _price;


    private ProductGroup _group;


    /** Java 1.2
    private java.util.ArrayList  _details = new java.util.ArrayList();
     */
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


    /** Java 1.2
    public java.util.ArrayList getDetails()
    {
        return _details;
    }
    */


    public Vector getDetails()
    {
        return _details;
    }


    public void addDetail( ProductDetail detail )
    {
        _details.add( detail );
        detail.setProduct( this );
    }


    public Vector getCategories()
    {
        return _categories;
    }


    public void addCategory( Category category )
    {
        if ( _categories.contains( category ) ) {
            _categories.addElement( category );
            category.addProduct( this );
        }
    }


    public String toString()
    {
        return _id + " " + _name;
    }


}
