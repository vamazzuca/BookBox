package com.cmput301f20t14.bookbox.entities;

public class Image {

    Integer height;
    Integer width;
    String url;


    public Image(Integer height, Integer width, String url) {
        this.height = height;
        this.width = width;
        this.url = url;
    }


    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
