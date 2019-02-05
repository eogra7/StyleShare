package com.evanogra.styleshare;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Evan on 2/17/2016.
 */
@JsonIgnoreProperties({"key"})
public class Comment {
    private String author;
    private String body;
    private String postID;
    private String key;
    private long time;

    @JsonIgnore
    public String getKey() {
        return key;
    }

    @JsonIgnore
    public void setKey(String key) {
        this.key = key;
    }

    public Comment() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public String getPostID() {
        return postID;
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
