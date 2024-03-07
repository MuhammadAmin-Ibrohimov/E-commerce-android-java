package com.example.lidertrade.seller.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SellerCategoryModel implements Serializable {
    String categoryName, categoryPic;

    ArrayList<String> subcategories;
    public SellerCategoryModel(){

    }

    public SellerCategoryModel(String categoryName, String categoryPic, ArrayList<String> subcategories) {
        this.categoryName = categoryName;
        this.categoryPic = categoryPic;
        this.subcategories = subcategories;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryPic() {
        return categoryPic;
    }

    public ArrayList<String> getSubcategories() {
        return subcategories;
    }

}
