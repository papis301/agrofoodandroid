package com.pisco.agrofood;

public class ProductModel {
    private int id;
    private String name;
    private double price;
    private String description;
    private String images;
    private String imageUrl;
    private String firebaseId;
    private String telephone;
    private String createdAt;

    public ProductModel(int id, String name, double price, String description, String images, String telephone) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.images = images;
        this.telephone = telephone;
    }

    public ProductModel() {

    }

    public ProductModel(int id, String name, String price, String description, String images, String telephone) {
    }

    public ProductModel(int id, String name, String telephone, double price, String images) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.price = price;
        this.images = images;
    }



    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImages() { return images; }


    public void setName(String name) { this.name = name; }

    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
