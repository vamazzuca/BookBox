package com.cmput301f20t14.bookbox.entities;

import android.net.Uri;

import java.io.Serializable;


/**
 * A class that contains all the information necessary to represent an Image
 * @author Alex Mazzuca
 * @version 2020.11.04
 */
public class Image implements Serializable {

    private Integer height;
    private Integer width;
    private Uri uri;
    private String url;

    /**
     * Constructs an image
     * @param height The height of the image
     * @param width The width of the image
     * @param uri The uri of the image
     * @param url The url who owns the image
     */
    public Image(Integer height, Integer width, Uri uri, String url) {
        this.height = height;
        this.width = width;
        this.uri = uri;
        this.url = url;
    }

    /**
     * Get the Height of the image
     * @return A Integer of the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Set the height of the image
     * @param height Integer of the height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * Get the Width of the image
     * @return A Integer of the Width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Set the Width of the image
     * @param width Integer of the Width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * Set the Uri of the image
     * @return A Uri of the image
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * Get the Uri of the image
     * @param uri Uri of the image
     */
    public void setUri(Uri uri) {
        this.uri = uri;
    }

    /**
     * Get the Url of the book
     * @return A String of the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the url of the image
     * @param url String of the url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
