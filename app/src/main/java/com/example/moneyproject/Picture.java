package com.example.moneyproject;

import android.media.Image;
import android.widget.ImageView;
//import android.widget.ImageView;

public class Picture {
    ImageView image;
    String path = "";
    String date= "";
    String location = "";
    String description = "";

    public Picture(String path){
        this.path = path;
    }
    Picture (String path, String date, String location, String description){
        this.path = path;
        this.date = date;
        this.location = location;
        this.description = description;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
