package com.evanogra.styleshare;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 2/11/2016.
 */
@JsonIgnoreProperties({"key"})
public class Post implements Serializable{
    private String author;
    private String body;
    private String image_url;
    private long time_uploaded;
    private String title;
    private String key;
    public Post() {
    }

    public Post(String author, String title, String body, String image_url, long time_uploaded) {

        this.author = author;
        this.title = title;
        this.body = body;
        this.image_url = image_url;
        this.time_uploaded = time_uploaded;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImage_url() {
        return image_url;
    }

    public long getTime_uploaded() {
        return time_uploaded;
    }

    @JsonIgnore
    public void setKey(String key) {
        this.key = key;
    }

    @JsonIgnore
    public String getKey() {
        return key;
    }
}
