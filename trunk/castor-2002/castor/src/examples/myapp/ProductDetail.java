package myapp;


import java.io.Serializable;


public class ProductDetail
    implements Serializable
{


    public int     id;


    public Product product;


    public String  name;


    public int getId() { return id; }

    public String getName() { return name; }

    public Product getProduct() { return product; }


    public String toString()
    {
        return id + " " + name + 
            ( product == null ? "" : " for " + product.name );
    }


}
