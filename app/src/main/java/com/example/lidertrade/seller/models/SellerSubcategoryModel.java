package com.example.lidertrade.seller.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SellerSubcategoryModel implements Serializable {
    String subcategoryId, subcategoryPic, subcategoryName;
    ArrayList<String> products;
    public SellerSubcategoryModel(){

    }

    public SellerSubcategoryModel(String subcategoryId, String subcategoryPic, String subcategoryName, ArrayList<String> products) {
        this.subcategoryId = subcategoryId;
        this.subcategoryPic = subcategoryPic;
        this.subcategoryName = subcategoryName;
        this.products = products;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getSubcategoryPic() {
        return subcategoryPic;
    }

    public void setSubcategoryPic(String subcategoryPic) {
        this.subcategoryPic = subcategoryPic;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }
}
