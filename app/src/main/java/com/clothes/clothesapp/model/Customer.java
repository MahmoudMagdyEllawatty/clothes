package com.clothes.clothesapp.model;

public class Customer {
    private String key;
    private String name;
    private String email;
    private String password;
    private int state;
    private String image;

    public Customer() {
    }

    public Customer(String key, String name, String email, String password, int state,String image) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = state;
        this.image = image;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
