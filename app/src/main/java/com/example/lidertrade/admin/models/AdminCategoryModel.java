package com.example.lidertrade.admin.models;

import java.io.Serializable;
import java.util.ArrayList;

public class AdminCategoryModel implements Serializable {
    String categoryName;
    String categoryPic;
    String id;
    ArrayList<String> subcategories;

    public AdminCategoryModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AdminCategoryModel(String categoryName, String categoryPic, ArrayList<String> subcategories) {
        this.categoryName = categoryName;
        this.categoryPic = categoryPic;
        this.subcategories = subcategories;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryPic() {
        return categoryPic;
    }

    public void setCategoryPic(String categoryPic) {
        this.categoryPic = categoryPic;
    }

    public ArrayList<String> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(ArrayList<String> subcategories) {
        this.subcategories = subcategories;
    }
}
