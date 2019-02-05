package com.evanogra.styleshare;

import android.content.Intent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Evan on 2/26/2016.
 */
@JsonIgnoreProperties({"key", "YES", "NO"})
public class Poll {
    private String key;
    private String postID;
    private String title;
    private Map<String, Boolean> votes;

    public Poll() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
