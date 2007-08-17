package org.castor.spring.xml.entity;

public class Product {

    private int id;
    private String name;
    private String catalog;
    
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCatalog() {
        return catalog;
    }
    public void setCatalog(String catalogName) {
        this.catalog = catalogName;
    }
}
