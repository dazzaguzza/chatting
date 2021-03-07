package com.example.admin.myapplication4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatRoomDataAdapter extends BaseAdapter {

    ArrayList<ChatRoomData> chatRoomData = new ArrayList<>();
    Context context;


    public ChatRoomDataAdapter(ArrayList<ChatRoomData> chatRoomData, Context context) {
        this.chatRoomData = chatRoomData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return chatRoomData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatRoomData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int positon, View view, ViewGroup viewGroup) {

        if(view == null){
          LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          view = layoutInflater.inflate(R.layout.chat_list_view_item,viewGroup,false);
        }

        TextView chat_room_id = (TextView) view.findViewById(R.id.chat_room_id);
        TextView chat_room_content =(TextView) view.findViewById(R.id.chat_room_content);

        chat_room_id.setText(chatRoomData.get(positon).getChat_room_id());
        chat_room_content.setText(chatRoomData.get(positon).getChat_room_content());

        return view;
    }
}
