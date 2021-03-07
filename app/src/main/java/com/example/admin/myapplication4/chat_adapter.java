package com.example.admin.myapplication4;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class chat_adapter extends BaseAdapter{


    ArrayList<ChatData> chatData = new ArrayList<>();
    Context context;


    public chat_adapter(Context context,ArrayList<ChatData> chatData){
            this.context = context;
            this.chatData = chatData;
    }


    @Override
    public int getCount() {
        return chatData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.chat_item,viewGroup,false);
        }

        TextView chat_content = (TextView) view.findViewById(R.id.text_chat);
        TextView chat_who = (TextView) view.findViewById(R.id.user);
        ImageView image_chat = (ImageView) view.findViewById(R.id.image_chat);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        ImageView chat_psa = (ImageView) view.findViewById(R.id.chat_psa);

        if(chatData.get(position).getChat_who() == null && chatData.get(position).getImage_chat() ==null && chatData.get(position).getImage_url() == null){
            chat_who.setVisibility(View.GONE);
            chat_who.setText(chatData.get(position).getChat_who());
            chat_content.setVisibility(View.VISIBLE);

            String image_position = chatData.get(position).getImage_chat();
            Glide.with(context).load(image_position).into(image_chat);
            image_chat.setVisibility(View.GONE);

            chat_psa.setVisibility(View.GONE);

            chat_content.setText(chatData.get(position).getChat_content());
            chat_content.setBackgroundResource(R.drawable.me);
            layout.setGravity(Gravity.RIGHT);

        }else if(chatData.get(position).getChat_who() != null && chatData.get(position).getImage_chat() ==null && chatData.get(position).getImage_url() == null) {
            chat_who.setVisibility(View.VISIBLE);
            chat_who.setText(chatData.get(position).getChat_who());
            chat_content.setVisibility(View.VISIBLE);
            chat_content.setText(chatData.get(position).getChat_content());

            String image_position = chatData.get(position).getImage_chat();
            Glide.with(context).load(image_position).into(image_chat);
            image_chat.setVisibility(View.GONE);

            chat_psa.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
            if (Build.VERSION.SDK_INT >= 21) {
                chat_psa.setClipToOutline(true);
            }

            chat_psa.setVisibility(View.VISIBLE);
            String chatPsa_position = chatData.get(position).getPsa_image();
            Glide.with(context).load(chatPsa_position).error(R.drawable.backgroundimage).into(chat_psa);

            chat_content.setBackgroundResource(R.drawable.you);
            layout.setGravity(Gravity.LEFT);
        }else if(chatData.get(position).getChat_who() != null && chatData.get(position).getImage_chat() !=null && chatData.get(position).getImage_url() == null){
            image_chat.setVisibility(View.VISIBLE);
            chat_who.setVisibility(View.VISIBLE);
            chat_who.setText(chatData.get(position).getChat_who());

            chat_content.setVisibility(View.GONE);
            String home ="http://13.124.254.43/";
            String image_position = chatData.get(position).getImage_chat();
            Glide.with(context).load(home+image_position).into(image_chat);

            chat_psa.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
            if (Build.VERSION.SDK_INT >= 21) {
                chat_psa.setClipToOutline(true);
            }


            chat_psa.setVisibility(View.VISIBLE);
            String chatPsa_position = chatData.get(position).getPsa_image();
            Glide.with(context).load(chatPsa_position).error(R.drawable.backgroundimage).into(chat_psa);


           // chatData.get(position).setChat_content(null);
            layout.setGravity(Gravity.LEFT);
        }else if(chatData.get(position).getChat_who() == null && chatData.get(position).getImage_chat() !=null && chatData.get(position).getImage_url() == null){
            image_chat.setVisibility(View.VISIBLE);
            chat_who.setVisibility(View.GONE);
            chat_who.setText(chatData.get(position).getChat_who());

            chat_content.setVisibility(View.GONE);

            String image_position = chatData.get(position).getImage_chat();
            Glide.with(context).load(image_position).into(image_chat);

            chat_psa.setVisibility(View.GONE);

            //chat_content.setText(chatData.get(position).getChat_content());
            layout.setGravity(Gravity.RIGHT);
        }else if(chatData.get(position).getChat_who() != null && chatData.get(position).getImage_chat() ==null && chatData.get(position).getImage_url() != null){
            String home ="http://13.124.254.43/";
            image_chat.setVisibility(View.VISIBLE);
            chat_who.setVisibility(View.VISIBLE);
            chat_who.setText(chatData.get(position).getChat_who());
            chat_content.setVisibility(View.GONE);
            Glide.with(context).load(home+chatData.get(position).getImage_url()).into(image_chat);


            chat_psa.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
            if (Build.VERSION.SDK_INT >= 21) {
                chat_psa.setClipToOutline(true);
            }

            chat_psa.setVisibility(View.VISIBLE);
            String chatPsa_position = chatData.get(position).getPsa_image();
            Glide.with(context).load(chatPsa_position).error(R.drawable.backgroundimage).into(chat_psa);


          //  chatData.get(position).setChat_content("(image)");
            layout.setGravity(Gravity.LEFT);
        }



        return view;
    }
}
