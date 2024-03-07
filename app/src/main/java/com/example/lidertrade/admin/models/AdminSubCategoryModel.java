package com.example.lidertrade.admin.models;

import java.io.Serializable;
import java.util.ArrayList;

public class AdminSubCategoryModel implements Serializable {


    String subcategoryName,subcategoryPic, id, categoryId;
    ArrayList<String> products;

    public AdminSubCategoryModel() {
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public String getSubcategoryPic() {
        return subcategoryPic;
    }

    public void setSubcategoryPic(String subcategoryPic) {
        this.subcategoryPic = subcategoryPic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }

    public AdminSubCategoryModel(String subcategoryName, String subcategoryPic, String id, String categoryId, ArrayList<String> products) {
        this.subcategoryName = subcategoryName;
        this.subcategoryPic = subcategoryPic;
        this.id = id;
        this.categoryId = categoryId;
        this.products = products;
    }
}
