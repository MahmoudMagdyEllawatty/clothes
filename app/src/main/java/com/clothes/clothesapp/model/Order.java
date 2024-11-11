package com.clothes.clothesapp.model;

public class Order {
    private String key;
    private String date;
    private String size;
    private String color;
    private String details;
    private String imageURL;
    private  int state;
    private  Customer customer;
    private Tailor tailor;
    private double price;


    public Order() {
    }

    public Order(String key, String date, String size, String color, String details, String imageURL, int state, Customer customer, Tailor tailor, double price) {
        this.key = key;
        this.date = date;
        this.size = size;
        this.color = color;
        this.details = details;
        this.imageURL = imageURL;
        this.state = state;
        this.customer = customer;
        this.tailor = tailor;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Tailor getTailor() {
        return tailor;
    }

    public void setTailor(Tailor tailor) {
        this.tailor = tailor;
    }
}
