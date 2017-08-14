package com.example.nguyentrung.docbao.model;

import java.io.Serializable;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class News implements Serializable {
    private String title;
    private String urlImage;
    private String description;
    private String link;
    private String date;
    private int type;

    public News(String title, String urlImage, String description, String link, String date, int type) {
        this.title = title;
        this.urlImage = urlImage;
        this.description = description;
        this.link = link;
        this.date = date;
        this.type = type;
    }
    public News() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return title + "\n" + urlImage  + "\n" + description + "\n" + link  + "\n" + date + "\n" + type;
    }
}