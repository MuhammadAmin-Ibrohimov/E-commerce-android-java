package com.example.lidertrade.seller.models;

public class SHFSaleModel {
    String saleName, description;
    String salePic;

    public SHFSaleModel() {
    }

    public SHFSaleModel(String saleName, String description, String salePic) {
        this.saleName = saleName;
        this.description = description;
        this.salePic = salePic;
    }

    public String getSaleName() {
        return saleName;
    }

    public String getDescription() {
        return description;
    }

    public String getSalePic() {
        return salePic;
    }
}
