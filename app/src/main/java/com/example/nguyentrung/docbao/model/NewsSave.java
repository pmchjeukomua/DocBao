package com.example.nguyentrung.docbao.model;

import java.io.Serializable;

/**
 * Created by nguyentrung on 5/21/2017.
 */

public class NewsSave implements Serializable {
    private String img;
    private String title;
    private String pubdate;
    private String description;
    private String link;

    public NewsSave(String img, String tvTitle, String tvPubdate, String tvDescription, String link) {
        this.img = img;
        this.title = tvTitle;
        this.pubdate = tvPubdate;
        this.description = tvDescription;
        this.link = link;
    }

    public NewsSave() {

    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
