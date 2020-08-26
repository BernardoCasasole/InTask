package com.example.chatapp.model;

public class User {

    private String name;
    private String surname;
    private String id;
    private Boolean setted_image;
    private Boolean verified;
    private String mail;
    private int ratings;
    private float average_ratings;
    private String location;
    private String typeReg;

    public String getLocation() {
        return location;
    }

    public String getTypeReg() {
        return typeReg;
    }

    public User(String name, String surname, String id, Boolean setted_image, Boolean verified, String mail, int ratings, float average_ratings, String location, String typeReg) {
        this.name = name;
        this.surname = surname;
        this.id = id;
        this.setted_image = setted_image;
        this.verified = verified;
        this.mail = mail;
        this.ratings = ratings;
        this.average_ratings = average_ratings;
        this.location = location;
        this.typeReg = typeReg;
    }

    public User(){


    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getId() {
        return id;
    }

    public Boolean getSetted_image() {
        return setted_image;
    }

    public Boolean getVerified() {
        return verified;
    }

    public String getMail() {
        return mail;
    }

    public int getRatings() {
        return ratings;
    }

    public float getAverage_ratings() {
        return average_ratings;
    }
}
