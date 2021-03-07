package com.example.admin.myapplication4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ListOfMember extends AppCompatActivity implements FriendFragment.bus{

    private BottomNavigationView bottomNavigationView;
    public static PrefConfig prefConfig;
    private int saveState;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_member);

        mContext = this;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNabigationView);

        prefConfig = new PrefConfig(this);

        if(findViewById(R.id.mainFrame) !=null){
            if(savedInstanceState !=null){
                return;
            }

        if(getIntent().getStringExtra("chat_key") != null){


            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new ChatFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.Bottom2);


            Intent intent = new Intent(this,Second.class);
            intent.putStringArrayListExtra("multi_name",getIntent().getExtras().getStringArrayList("multi_name"));
            Log.d("TAG", "onCreate: "+getIntent().getExtras().getStringArrayList("multi_name"));
            intent.putExtra("chat_key",getIntent().getStringExtra("chat_key"));
            Log.d("TAG", "onCreate: "+getIntent().getStringExtra("chat_key"));
            startActivity(intent);
        }else if(prefConfig.readLoginStatus()){
            getSupportFragmentManager().beginTransaction().add(R.id.mainFrame,new FriendFragment()).commit();
        }else{
                Intent intent = new Intent(this,Login.class);
                startActivity(intent);
                finish();
            }


//            if(prefConfig.readLoginStatus()){
//                getSupportFragmentManager().beginTransaction().add(R.id.mainFrame,new FriendFragment()).commit();
//
//
//            }else{
//                Intent intent = new Intent(this,SelectLogin.class);
//                startActivity(intent);
//            }

        }




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.Bottom1){

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,new FriendFragment()).commit();

                }else if(item.getItemId()== R.id.Bottom2) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new ChatFragment()).commit();


                }else if(item.getItemId()==R.id.Bottom3){

                   getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,new AddFriendFragment()).commit();

               }


                return true;
            }
        });

    }
    @Override
    public void loadAndsave(String image,String status) {

            SharedPreferences sharedPreferences = getSharedPreferences("save",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("status",status);
            editor.putString("image",image);

            editor.apply();

    }

    public void move(){
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new ChatFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.Bottom2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {   // 프래그먼트에서 onActivityResult를 사용 할 수 있게 해준다 밑에코드가
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
