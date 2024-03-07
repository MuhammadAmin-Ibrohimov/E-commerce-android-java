package com.example.lidertrade.seller.models;

import java.io.Serializable;

public class SellerShoppingCartModel implements Serializable {
    private String cartId, sellerId, productId, productName, prodPic;
    private int cashPrice, creditPrice, prodQuantity, totalCashPrice, totalCreditPrice;
    public SellerShoppingCartModel(){

    }

    public SellerShoppingCartModel(String cartId, String sellerId, String productId, String productName, String prodPic,
                                   int cashPrice, int creditPrice, int prodQuantity, int totalCashPrice, int totalCreditPrice) {
        this.cartId = cartId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.productName = productName;
        this.prodPic = prodPic;
        this.cashPrice = cashPrice;
        this.creditPrice = creditPrice;
        this.prodQuantity = prodQuantity;
        this.totalCashPrice = totalCashPrice;
        this.totalCreditPrice = totalCreditPrice;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProdPic() {
        return prodPic;
    }

    public void setProdPic(String prodPic) {
        this.prodPic = prodPic;
    }

    public int getCashPrice() {
        return cashPrice;
    }

    public void setCashPrice(int cashPrice) {
        this.cashPrice = cashPrice;
    }

    public int getCreditPrice() {
        return creditPrice;
    }

    public void setCreditPrice(int creditPrice) {
        this.creditPrice = creditPrice;
    }

    public int getProdQuantity() {
        return prodQuantity;
    }

    public void setProdQuantity(int prodQuantity) {
        this.prodQuantity = prodQuantity;
    }

    public int getTotalCashPrice() {
        return totalCashPrice;
    }

    public void setTotalCashPrice(int totalCashPrice) {
        this.totalCashPrice = totalCashPrice;
    }

    public int getTotalCreditPrice() {
        return totalCreditPrice;
    }

    public void setTotalCreditPrice(int totalCreditPrice) {
        this.totalCreditPrice = totalCreditPrice;
    }
}
