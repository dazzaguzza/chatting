package com.example.admin.myapplication4;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Show_Friend_Profile extends AppCompatActivity {

    ImageView friendImg,chatImg,header_cover_image;
    TextView friendId,friendStatus;
    public static ApiInterface apiInterface;
    String getFriend;
    PrefConfig prefConfig;
    ArrayList<String> tmpArray = new ArrayList<>();
    LinearLayout linear;
    Animation down;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__friend__profile);



        friendImg = (ImageView) findViewById(R.id.friendImg);
        chatImg = (ImageView) findViewById(R.id.chatImg);
        friendId = (TextView) findViewById(R.id.friendId);
        friendStatus = (TextView) findViewById(R.id.friendStatus);
        linear = (LinearLayout) findViewById(R.id.linear);
        header_cover_image = (ImageView) findViewById(R.id.header_cover_image);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);


        down = AnimationUtils.loadAnimation(this,R.anim.down);
        friendImg.setAnimation(down);
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        friendImg.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
        if (Build.VERSION.SDK_INT >= 21) {
            friendImg.setClipToOutline(true);
        }
        linear.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
        if (Build.VERSION.SDK_INT >= 21) {
            linear.setClipToOutline(true);
        }


        Glide.with(Show_Friend_Profile.this).load(R.raw.chatting)
                .asGif().into(chatImg);


        getFriend =getIntent().getStringExtra("friend");

       Call<User> call = apiInterface.getFriend(getFriend);
       call.enqueue(new Callback<User>() {
           @Override
           public void onResponse(Call<User> call, Response<User> response) {
                friendId.setText(response.body().user_name);
                friendStatus.setText(response.body().user_message);
                Glide.with(Show_Friend_Profile.this).load("http://13.124.254.43/"+response.body().user_image).error(R.drawable.backgroundimage).into(friendImg);


                   Glide.with(Show_Friend_Profile.this).load("http://13.124.254.43/" + response.body().user_image)
                           .bitmapTransform(new BlurTransformation(Show_Friend_Profile.this)).error(R.drawable.backgroundimage_blur).into(header_cover_image);


               Log.d("TAG", "onResponse: "+response.body().user_image);
           }

           @Override
           public void onFailure(Call<User> call, Throwable t) {

           }
       });


       chatImg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               prefConfig = new PrefConfig(Show_Friend_Profile.this);
               String me = prefConfig.readName();
               tmpArray.add(me);
               tmpArray.add(getFriend);

               Collections.sort(tmpArray);
               String addName="나";
               for (int i =0; i<tmpArray.size();i++){
                   if(!tmpArray.get(i).toString().equals(me)){
                       addName += "/"+tmpArray.get(i).toString();
                   }
               }
               ((ListOfMember)ListOfMember.mContext).move();

               Intent intent = new Intent(Show_Friend_Profile.this, Realchat.class);
               intent.putStringArrayListExtra("multi_name",tmpArray);
               intent.putExtra("chat_key",addName);
               startActivity(intent);
               finish();
           }
       });
    }
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

}
