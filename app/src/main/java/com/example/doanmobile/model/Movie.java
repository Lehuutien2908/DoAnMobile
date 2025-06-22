package com.example.doanmobile.model;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    private String imgMovie;
    private String name;
    private String status;
    private String duration;
    private String imgBanner;
    private String genre;
    private String description;
    private String age;

    public Movie() {}

    public Movie(String id, String name, String imgMovie, String status) {
        this.id = id;
        this.name = name;
        this.imgMovie = imgMovie;
        this.status = status;
    }

    public Movie(String id, String name, String imgMovie, String status, String duration, String imgBanner, String genre, String description, String age) {
        this.id = id;
        this.name = name;
        this.imgMovie = imgMovie;
        this.status = status;
        this.duration = duration;
        this.imgBanner = imgBanner;
        this.genre = genre;
        this.description = description;
        this.age = age;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public String getImgMovie() { return imgMovie; }
    public String getStatus() { return status; }
    public String getDuration() { return duration; }
    public String getImgBanner() { return imgBanner; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }
    public String getAge() { return age; }

    public void setImgMovie(String imgMovie) {
        this.imgMovie = imgMovie;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setImgBanner(String imgBanner) {
        this.imgBanner = imgBanner;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
