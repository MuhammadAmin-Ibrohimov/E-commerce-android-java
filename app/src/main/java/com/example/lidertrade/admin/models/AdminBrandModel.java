package com.example.lidertrade.admin.models;

public class AdminBrandModel {
    String id, brandName, brandPic;

    public AdminBrandModel(){}

    public AdminBrandModel(String id, String brandName, String brandPic) {
        this.id = id;
        this.brandName = brandName;
        this.brandPic = brandPic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandPic() {
        return brandPic;
    }

    public void setBrandPic(String brandPic) {
        this.brandPic = brandPic;
    }
}

