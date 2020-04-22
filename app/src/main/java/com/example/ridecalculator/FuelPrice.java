package com.example.ridecalculator;

import java.util.ArrayList;

public class FuelPrice {
    private int id;
    private String city;
    private double price;
    private String fuel_type;
    private String response;
    private String state;
    private ArrayList<FuelPrice> cityList;

    public FuelPrice(int id, String city, double price, String fuel_type, String response, String state, ArrayList<FuelPrice> cityList){
        this.id=id;
        this.city=city;
        this.state = state;
        this.price=price;
        this.fuel_type = fuel_type;
        this.response = response;
        this.cityList = cityList;
    }

    public ArrayList<FuelPrice> getCityList() {return this.cityList;}
    public String getState() {return this.state;}
    public String getResponse() {return this.response;}
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
    public void setResponse(String response) {this.response = response;}
    public void setState(String state) {this.state = state;}
    public void setCityList(ArrayList<FuelPrice> cityList) {this.cityList = cityList;}
}
