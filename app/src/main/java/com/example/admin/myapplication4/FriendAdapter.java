package com.example.admin.myapplication4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FriendAdapter extends BaseAdapter {

    Context context;
    ArrayList<ListviewUser> arrayList;

    LayoutInflater layoutInflater;

    final int ITEM_VIEW_TYPE_MINE = 0;
    final int ITEM_VIEW_TYPE_FRIEND = 1;
    final int ITEM_VIEW_TYPE_TEXT = 2;
    final int ITEM_VIEW_TYPE_MAX =  3;

    public FriendAdapter(Context context, ArrayList<ListviewUser> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

       // layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }





    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position).getType();
    }






    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        int viewType = getItemViewType(position);

        if(view == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        }
            if (viewType == ITEM_VIEW_TYPE_FRIEND) {

                view = layoutInflater.inflate(R.layout.friend_list_item, viewGroup, false);

                ImageView tv_image = (ImageView) view.findViewById(R.id.image);
                TextView tv_id = (TextView) view.findViewById(R.id.id);
                TextView tv_write = (TextView) view.findViewById(R.id.write);


                tv_image.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
                if (Build.VERSION.SDK_INT >= 21) {
                    tv_image.setClipToOutline(true);
                }

                String image_position = arrayList.get(position).getImage();
                Glide.with(context).load(image_position).error(R.drawable.backgroundimage).into(tv_image);

                tv_id.setText(arrayList.get(position).getId());
                tv_write.setText(arrayList.get(position).getWrite());



            }
            else if (viewType == ITEM_VIEW_TYPE_MINE) {

                view = layoutInflater.inflate(R.layout.only_me_list_item, viewGroup, false);

                ImageView tv_myImage = (ImageView) view.findViewById(R.id.myImage);
                TextView tv_myId = (TextView) view.findViewById(R.id.myId);
                TextView tv_myWrite = (TextView) view.findViewById(R.id.myWrite);


                tv_myImage.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
                if (Build.VERSION.SDK_INT >= 21) {
                    tv_myImage.setClipToOutline(true);
                }

                String myImage_position = arrayList.get(position).getMyImage();

                Glide.with(context).load(myImage_position).error(R.drawable.backgroundimage).into(tv_myImage);

                tv_myId.setText(arrayList.get(position).getMyId());
                tv_myWrite.setText(arrayList.get(position).getMyWrite());

            }else if(viewType == ITEM_VIEW_TYPE_TEXT){

                view =   layoutInflater.inflate(R.layout.text_list_item, viewGroup, false);

                TextView txt = (TextView) view.findViewById(R.id.txt);
            }



        return view;
    }


}
