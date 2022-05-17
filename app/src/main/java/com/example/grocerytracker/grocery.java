package com.example.grocerytracker;

public class grocery {
    String name;
    String type;
    Integer quantity;
    String date;
    String imageUri;

    public grocery() {
    }

    public grocery(String name, String type, Integer quantity, String date, String imageUri) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.date = date;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
