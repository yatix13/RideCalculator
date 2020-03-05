package com.example.ridecalculator;

public class PetrolPrice {
    private int id;
    private String city;
    private double price;


    public PetrolPrice(int id, String city, double price){
        this.id=id;
        this.city=city;
        this.price=price;
    }
    public int getId(){
        return this.id;
    }
    public String getCity(){
        return this.city;
    }
    public double getPrice(){
        return this.price;
    }
    public void  setId(int id){
        this.id=id;
    }
    public void setCity(String city){
        this.city=city;
    }
    public void setPrice(double price){
        this.price=price;
    }
}
