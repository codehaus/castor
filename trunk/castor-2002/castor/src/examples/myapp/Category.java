package myapp;


import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;


public class Category
    implements Serializable
{


    public int       id;


    public Vector    product;


    public String    name;


    public int getId() { return id; }

    public String getName() { return name; }

    public Enumeration getProduct() { return product.elements(); }


}

