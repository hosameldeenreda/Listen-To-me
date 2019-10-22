package com.example.listentomiii;

public class User {

    private String id;
    private String username;
    private String imageURL;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private double longitude;
    private double latitude;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(double longitude, double latitude, String id,String username) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.username=username;
    }


    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
