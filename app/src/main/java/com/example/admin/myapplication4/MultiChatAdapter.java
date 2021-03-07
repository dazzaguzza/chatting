package com.example.admin.myapplication4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.preference.PreferenceManager;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiChatAdapter extends BaseAdapter{

    ArrayList<MultiChatData> multiChatDataArrayList;
    Context context;
    ArrayList<String> tmp_name = new ArrayList<>();
    PrefConfig prefConfig;


    public MultiChatAdapter(ArrayList<MultiChatData> multiChatDataArrayList, Context context) {
        this.multiChatDataArrayList = multiChatDataArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return multiChatDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return multiChatDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean isChecked(int position){
        return multiChatDataArrayList.get(position).checked;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if (view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.multi_chat_item,viewGroup,false);
        }
        ImageView tv_imageMulti = (ImageView) view.findViewById(R.id.image_multi);
        TextView tv_idMulti = (TextView) view.findViewById(R.id.id_multi);
        CheckBox tv_checkMulti =(CheckBox) view.findViewById(R.id.check_multi);

        tv_imageMulti.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
        if (Build.VERSION.SDK_INT >= 21) {
            tv_imageMulti.setClipToOutline(true);
        }


        String image_position = multiChatDataArrayList.get(position).getImage_multi();
        Glide.with(context).load(image_position).error(R.drawable.backgroundimage).into(tv_imageMulti);

        tv_idMulti.setText(multiChatDataArrayList.get(position).getId_multi());
        tv_checkMulti.setChecked(multiChatDataArrayList.get(position).checked);

        tv_checkMulti.setTag(position);
        tv_checkMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean newState = !multiChatDataArrayList.get(position).isChecked();
                multiChatDataArrayList.get(position).checked = newState;

                if(multiChatDataArrayList.get(position).isChecked() == true) {
                    tmp_name.add(multiChatDataArrayList.get(position).getId_multi());
                    Log.d("TAG", "onClick: "+multiChatDataArrayList.get(position).isChecked()+" , "+tmp_name);
                }else{
                    tmp_name.remove(multiChatDataArrayList.get(position).getId_multi());
                    Log.d("TAG", "onClick: "+multiChatDataArrayList.get(position).isChecked()+" , "+tmp_name);
                }



            }
        });

        tv_checkMulti.setChecked(isChecked(position));
        return view;
    }

    public void sendCheck(){
        prefConfig = new PrefConfig(context);
       String me = prefConfig.readName();
        tmp_name.add(me);

        Collections.sort(tmp_name);
        String addName="나";
        for (int i =0; i<tmp_name.size();i++){
            if(!tmp_name.get(i).toString().equals(me)){
                addName += "/"+tmp_name.get(i).toString();
            }
        }


        Log.d("TAG", "sendCheck: 정렬 "+tmp_name);
        Log.d("TAG", "sendCheck: 방이름"+addName);
        Intent intent = new Intent(context,Realchat.class);
        intent.putStringArrayListExtra("multi_name",tmp_name);
        intent.putExtra("chat_key",addName);
        context.startActivity(intent);
    }
}
