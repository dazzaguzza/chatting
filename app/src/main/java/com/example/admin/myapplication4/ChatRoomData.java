package com.example.admin.myapplication4;

public class ChatRoomData {

    String chat_room_id;
    String chat_room_content;
   // String socket_room_name;
    String all_friend;

    public String getAll_friend() {
        return all_friend;
    }

    public void setAll_friend(String all_friend) {
        this.all_friend = all_friend;
    }

    public ChatRoomData(String chat_room_id, String chat_room_content) {
        this.chat_room_id = chat_room_id;
        this.chat_room_content = chat_room_content;

    }

    public String getChat_room_id() {
        return chat_room_id;
    }

    public void setChat_room_id(String chat_room_id) {
        this.chat_room_id = chat_room_id;
    }

    public String getChat_room_content() {
        return chat_room_content;
    }

    public void setChat_room_content(String chat_room_content) {
        this.chat_room_content = chat_room_content;
    }

}
