package com.redhat.coolstore.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Serializable {

    private static final long serialVersionUID = -7304814269819778382L;
    private String itemId;
    private String name;
    private String desc;
    private double price;
    private Integer quantity;
    private String category;
    private String image;

    public Product() {

    }

    public Product(String itemId, String name, String desc, double price, String category, String image) {
        super();
        this.itemId = itemId;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.category = category;
        this.image = image;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product [itemId=" + itemId + ", name=" + name + ", desc="
                + desc + ", price=" + price + ", quantity=" + quantity + "]";
    }


}
