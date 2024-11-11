package com.clothes.clothesapp.model;

import java.util.ArrayList;

public class Gallery {
    private String key;
    private String name;
    private String description;
    private Tailor tailor;
    private String image;
    private ArrayList<String> images;

    public Gallery() {
    }

    public Gallery(String key, String name, String description, Tailor tailor, String image, ArrayList<String> images) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.tailor = tailor;
        this.image = image;
        this.images = images;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Tailor getTailor() {
        return tailor;
    }

    public void setTailor(Tailor tailor) {
        this.tailor = tailor;
    }
}
