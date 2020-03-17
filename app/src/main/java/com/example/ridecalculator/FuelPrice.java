package com.example.ridecalculator;

public class FuelPrice {
    private int id;
    private String city;
    private double price;
    private String fuel_type;


    public FuelPrice(int id, String city, double price, String fuel_type){
        this.id=id;
        this.city=city;
        this.price=price;
        this.fuel_type = fuel_type;
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
    public String getFuelType() {return this.fuel_type;}
    public void  setId(int id){
        this.id=id;
    }
    public void setCity(String city){
        this.city=city;
    }
    public void setPrice(double price){
        this.price=price;
    }
    public void setFuelType(String fuel_type) {this.fuel_type = fuel_type;}
}
