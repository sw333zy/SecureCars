package com.example.demo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    //car details
    @NotNull
    private String manufacturer;
    @NotNull
    private String model;
    @NotNull
    private int year;
    @NotNull
    private double msrp;
    private String carpic;
    //each car has a user
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    //each car belongs to a specific category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    //car, users, id, and categories
    public Car() {
    }

    public Car(Category category) {
        this.category = category;
    }

    public Car(User user) {this.user = user;}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //car attributes

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getMsrp() {
        return msrp;
    }

    public void setMsrp(double msrp) {
        this.msrp = msrp;
    }

    //car pic for cloudinary

    public String getCarpic() {
        return carpic;
    }

    public void setCarpic(String carpic) {
        this.carpic = carpic;
    }





}