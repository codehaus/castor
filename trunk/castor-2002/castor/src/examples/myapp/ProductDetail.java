package myapp;


public class ProductDetail
{


    public int     id;


    public Product product;


    public String  name;


    public String toString()
    {
        return id + " " + name + 
            ( product == null ? "" : " for " + product.name );
    }


}
