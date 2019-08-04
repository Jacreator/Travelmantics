package com.example.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable {

    private String title;
    private String id;
    private String description;
    private String imageUrl;
    private String price;
    private String imageName;

    public TravelDeal(){}
    public TravelDeal(String title, String description, String imageUrl, String price, String imageName) {
        this.setTitle(title);
        this.setId(id);
        this.setDescription(description);
        this.setImageUrl(imageUrl);
        this.setPrice(price);
        this.setImageName(imageName);
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
