package com.example.lidertrade.seller.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SHFAllProductsModel implements Serializable {



    String productName, id;
    int price;
    ArrayList<String > imageUrl;

    public SHFAllProductsModel(){

    }

    public SHFAllProductsModel(String productName, ArrayList<String > imageUrl, String id, int price) {
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.id = id;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ArrayList<String > getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ArrayList<String > imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
