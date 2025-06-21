package com.example.doanmobile.model;

public class Movie {
    private String id;
    private String imgMovie;
    private String name;
    private String status;


    public Movie() {}

    public Movie(String id, String name, String imgMovie, String status) {
        this.id = id;
        this.name = name;
        this.imgMovie = imgMovie;
        this.status = status;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public String getImgMovie() { return imgMovie; }
    public String getStatus() { return status; }

}
