package com.evanogra.styleshare;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

/**
 * Created by Evan on 2/22/2016.
 */
@JsonIgnoreProperties({"key"})
public class User {

    private String name;
    private String chapter;
    private String email;
    private String profile_picture;
    private boolean setup;
    private String key;



    @JsonIgnore
    public String getKey() {
        return key;
    }

    @JsonIgnore
    public void setKey(String key) {
        this.key = key;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public String getChapter() {
        return chapter;
    }

    public String getEmail() {
        return email;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public boolean isSetup() {
        return setup;
    }

    @JsonIgnore
    public static void getRealName(String uid, final UserDataCallback callback) {
        Firebase usersRef = new Firebase("https://resplendent-inferno-4210.firebaseio.com/users");
        Query queryRef = usersRef.orderByKey().equalTo(uid);

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String realName = dataSnapshot.child("name").getValue().toString();
                callback.onFinished(realName);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @JsonIgnore
    public static void getProfilePicUrl(String uid, final UserDataCallback callback) {
        Firebase usersRef = new Firebase("https://resplendent-inferno-4210.firebaseio.com/users");
        Query queryRef = usersRef.orderByKey().equalTo(uid);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String url = dataSnapshot.child("profile_picture").getValue().toString();
                callback.onFinished(url);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
