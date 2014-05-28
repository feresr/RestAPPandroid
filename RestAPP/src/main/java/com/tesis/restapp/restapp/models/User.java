package com.tesis.restapp.restapp.models;

import retrofit.client.Header;

/**
 * Created by feresr on 5/24/14.
 */
public class User {

    //+++Static methods+++
    public final static String TAG_FIRSTNAME = "firstname";
    public final static String TAG_LASTNAME = "lastname";
    public final static String TAG_USERNAME = "username";
    private static Header token;

    public static void setToken(Header e ){
        token = e;
    }

    public static Header getToken(){
        return token;
    }


    private int id;
    private String firstname;
    private String lastname;
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    @Override
    public String toString() {
        return "Hi there, I'm " + this.firstname + " " + this.lastname + ". Mi id is: " + String.valueOf(id) + " Username: " + this.username;
    }
}
