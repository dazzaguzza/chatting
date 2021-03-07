package com.example.admin.myapplication4;

public class ChatData {

    String chat_content;
    String chat_who;
    String image_chat;
    String image_url;
    String psa_image;

    public String getPsa_image() {
        return psa_image;
    }

    public void setPsa_image(String psa_image) {
        this.psa_image = psa_image;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_chat() {
        return image_chat;
    }

    public void setImage_chat(String image_chat) {
        this.image_chat = image_chat;
    }

    public ChatData(String chat_content, String chat_who,String image_chat, String image_url,String psa_image) {
        this.chat_content = chat_content;
        this.chat_who = chat_who;
        this.image_chat = image_chat;
        this.image_url = image_url;
        this.psa_image = psa_image;
    }

    public String getChat_content() {
        return chat_content;
    }

    public void setChat_content(String chat_content) {
        this.chat_content = chat_content;
    }

    public String getChat_who() {
        return chat_who;
    }

    public void setChat_who(String chat_who) {
        this.chat_who = chat_who;
    }
}
