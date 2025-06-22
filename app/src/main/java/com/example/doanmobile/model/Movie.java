package com.example.doanmobile.model;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    private String name;
    private String imgMovie;
    private String imgBanner;
    private String status;
    private String duration;
    private String genre;
    private String age;
    private String description;

    public Movie() {
    }

    public Movie(String id, String name, String imgMovie, String imgBanner,
                 String status, String duration, String genre, String age, String description) {
        this.id = id;
        this.name = name;
        this.imgMovie = imgMovie;
        this.imgBanner = imgBanner;
        this.status = status;
        this.duration = duration;
        this.genre = genre;
        this.age = age;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImgMovie() {
        return imgMovie;
    }

    public String getImgBanner() {
        return imgBanner;
    }

    public String getStatus() {
        return status;
    }

    public String getDuration() {
        return duration;
    }

    public String getGenre() {
        return genre;
    }

    public String getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgMovie(String imgMovie) {
        this.imgMovie = imgMovie;
    }

    public void setImgBanner(String imgBanner) {
        this.imgBanner = imgBanner;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
