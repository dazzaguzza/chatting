package com.example.admin.myapplication4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ChooseMulti extends AppCompatActivity {

    Button cancle_chat,start_chat;
    ListView multi_add_friend_ListView;
    MultiChatAdapter multiChatAdapter;
    ArrayList<MultiChatData> multiChatDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_multi);

        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        cancle_chat =  (Button) findViewById(R.id.cancle_chat);
        start_chat = (Button) findViewById(R.id.start_chat);
        multi_add_friend_ListView = (ListView) findViewById(R.id.multi_add_friend_ListView);

        multiChatDataArrayList = new ArrayList<>();
        multiChatAdapter = new MultiChatAdapter(multiChatDataArrayList,this);

        multi_add_friend_ListView.setAdapter(multiChatAdapter);

        animate();

        ArrayList<ListviewUser> list = (ArrayList<ListviewUser>) getIntent().getSerializableExtra("multi");
        for(int i =2; i<list.size();i++){
            multiChatDataArrayList.add(i-2,new MultiChatData(list.get(i).getImage(),list.get(i).getId()));
        }
        multiChatAdapter.notifyDataSetChanged();



        cancle();
        startChat();
    }

    void cancle(){
        cancle_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    void startChat(){
        start_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((ListOfMember)ListOfMember.mContext).move();
                multiChatAdapter.sendCheck();



                finish();
            }
        });
    }
    void animate(){
        // 리스트뷰 아이템 차례대로 하나씩 보여주기
        AnimationSet animationSet = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(50);
        animationSet.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0.0f,
                Animation.RELATIVE_TO_SELF,0.0f,
                Animation.RELATIVE_TO_SELF,-1.0f,
                Animation.RELATIVE_TO_SELF,0.0f);

        animation.setDuration(200);
        animationSet.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(animationSet,0.5f);
        multi_add_friend_ListView.setLayoutAnimation(controller);
    }
    @Override
    public void finish() {
        super.finish();

        this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}
