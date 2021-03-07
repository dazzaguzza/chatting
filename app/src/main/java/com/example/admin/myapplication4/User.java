package com.example.admin.myapplication4;

import com.google.gson.annotations.SerializedName;

public class User {

    String login_user_name;
    String login_user_message;
    String login_user_image;


    String response;
    String user_name;
    String user_message;
    String user_image;
    String email;
    String email_num;
    String friend;
    String send_image;

    public String getSend_image() {
        return send_image;
    }

    public String getFriend() {
        return friend;
    }

    public String getEmail_num() {
        return email_num;
    }

    public void setEmail_num(String email_num) {
        this.email_num = email_num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin_user_image() {
        return login_user_image;
    }

    public void setLogin_user_image(String login_user_image) {
        this.login_user_image = login_user_image;
    }


    public String getLogin_user_name() {
        return login_user_name;
    }

    public void setLogin_user_name(String login_user_name) {
        this.login_user_name = login_user_name;
    }

    public String getLogin_user_message() {
        return login_user_message;
    }

    public void setLogin_user_message(String login_user_message) {
        this.login_user_message = login_user_message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_message() {
        return user_message;
    }

    public void setUser_message(String user_message) {
        this.user_message = user_message;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }
}
