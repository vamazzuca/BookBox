package com.cmput301f20t14.bookbox.entities;

import android.net.Uri;

import java.io.Serializable;

public class Image implements Serializable {

    private Integer height;
    private Integer width;
    private Uri uri;


    public Image(Integer height, Integer width, Uri uri) {
        this.height = height;
        this.width = width;
        this.uri = uri;
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
