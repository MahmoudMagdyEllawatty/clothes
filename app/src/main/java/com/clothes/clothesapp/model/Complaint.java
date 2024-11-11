package com.clothes.clothesapp.model;

public class Complaint {
    private String key;
    private String title;
    private String description;
    private Customer customer;
    private String reply;

    public Complaint() {
    }

    public Complaint(String key, String title, String description, Customer customer, String reply) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.customer = customer;
        this.reply = reply;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
