package myapp;


import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;


public class Product
    implements Serializable
{


    public int       id;


    public String    name;


    public float     price;


    public ProductGroup     group;


    public Vector           detail;


    public Vector           category;


    public int getId() { return id; }

    public String getName() { return name; }

    public float getPrice() { return price; }

    public ProductGroup getGroup() { return group; }

    public Enumeration getDetail() { return detail.elements(); }

    public Enumeration getCategory() { return category.elements(); }


}
