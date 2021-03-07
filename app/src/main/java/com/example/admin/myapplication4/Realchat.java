package com.example.admin.myapplication4;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.java_websocket.util.Base64.encodeBytes;

public class Realchat extends AppCompatActivity {

    ListView listView;
    chat_adapter adapter;
    EditText chatEd;
    ImageButton sendBtn,contentBtn;
    com.github.nkzawa.socketio.client.Socket socket;
    ArrayList<ChatData> chatData = new ArrayList<>();

    String friend, me, roomName, check, message,all_friends,myImage;
    PrefConfig prefConfig;
    String turnResult;
    private static final int GALLERY_CODE = 1;
    Uri uri;
    public static ApiInterface apiInterface;
    ArrayList<String> multi_name;
    public static Activity _Main_Activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realchat);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        listView = (ListView) findViewById(R.id.chat_listview);
        chatEd = (EditText) findViewById(R.id.chatEd);
        sendBtn = (ImageButton) findViewById(R.id.sendBtn);
        contentBtn = (ImageButton) findViewById(R.id.contentBtn);
        prefConfig = new PrefConfig(this);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        _Main_Activity = Realchat.this;




        try {
            turnResult = getIntent().getStringExtra("turnResult");
        } catch (NullPointerException e) {
        }

        final JSONObject data = new JSONObject();
        me = prefConfig.readName();
        myImage = prefConfig.readImage();
        if(getIntent().hasExtra("multi_name")){


            multi_name = getIntent().getExtras().getStringArrayList("multi_name");
            roomName = getIntent().getStringExtra("chat_key");
            try {
                //  data.put("me",me);

                for (int i = 0; i < multi_name.size(); i++) {

                    data.put("friend" + i, multi_name.get(i).toString());

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        //Toast.makeText(this, "" + friend, Toast.LENGTH_SHORT).show();
        loadChatContent();

        adapter = new chat_adapter(this, chatData);
        listView.setAdapter(adapter);


        chatEd.requestFocus();
        clickContent();



        try {

            socket = IO.socket("http://13.124.254.43:9000");
            socket.emit("name", data);
            socket.emit("my_nick_name", me);


        } catch (URISyntaxException e) {
            Log.d("TAG", "onCreate: " + e.toString());
        }


        socket.connect();
      //  socket.on("roomName", room);
        socket.on("message", handling);
        socket.on("message_image",handling_image);
        socket.on("all_friends",get_All_Friends);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = chatEd.getText().toString();

                JSONObject sendData = new JSONObject();
                try{
                    sendData.put("who",me);
                    sendData.put("words",message);
                    sendData.put("psa",myImage);
                    socket.emit("message",sendData);
                    chatEd.setText("");
                    sendmessage(message, null,null,null);

                    if(getIntent().hasExtra("multi_name")){
                        JSONObject data1 = new JSONObject();

                        roomName = "나";
                        for(int i =0; i<multi_name.size();i++) {


                            String check_friend = multi_name.get(i).toString();
                            Log.d("TAG", "run:roomName cc "+check_friend);
                            if(!me.toString().equals(check_friend)) {
                                roomName += "/"+check_friend;
                                Log.d("TAG", "run:roomName cc"+check_friend);
                                data1.put("friend" + i, check_friend);

                            }
                        }
                        Log.d("TAG1", "onClick: "+data1);
                        socket.emit("friends_nick_names",data1);
                        Log.d("TAG", "run:roomName "+roomName);
                    }

                }catch(JSONException e){
                }
                Log.d("TAG", "run:roomName "+roomName);

                check = "in";


            }
        });


    }


    private Emitter.Listener get_All_Friends = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    all_friends = args[0].toString();
                    Log.d("TAG", "run: all_friends " + all_friends);

                    roomName = "나";
                    try {
                        JSONObject jsonObject = new JSONObject(all_friends);

                        for(int i =0 ; i<jsonObject.length();i++){

                            String friend_all = jsonObject.getString("friend"+i);
                            if(!me.toString().equals(friend_all)){
                                roomName += "/"+friend_all;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("TAG", "run:roomName "+roomName);

                }
            });
        }
    };


    private Emitter.Listener handling = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String json = args[0].toString();
                    Log.d("TAG", "json: " + json);
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(json);
                        if (jsonResponse.has("words")) {
                            String getWho = jsonResponse.getString("who");
                            String getwords = jsonResponse.getString("words");
                            String getPsa = jsonResponse.getString("psa");
                            sendmessage(getwords, getWho, null,getPsa);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();
                    check = "in";
                }
            });
        }
    };

    private Emitter.Listener handling_image = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String json = args[0].toString();
                    Log.d("TAG", "json: "+json);
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(json);
                        if (jsonResponse.has("image")) {
                            String getWho = jsonResponse.getString("who");
                            String getImage = jsonResponse.getString("image");
                            String getPsa = jsonResponse.getString("psa");
                            sendmessage(null,getWho,getImage,getPsa);
                            Log.d("TAG", "run111111: " + getImage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    check = "in";

                }
            });
        }
    };



    public void sendmessage(String message, String who,String image,String psa) {
        chatData.add(new ChatData(message, who,image,null,psa));

        adapter.notifyDataSetChanged();

    }

    void saveRoomName() {

        if (check == "in" && turnResult == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("room", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("friend", roomName);
            Log.d("TAG", "run:roomName "+  editor.putString("friend", roomName));
            editor.putString("lastWord", chatData.get(chatData.size() - 1).getChat_content());
            if (chatData.get(chatData.size() - 1).getChat_content() == null) {
                editor.putString("lastWord", "(이미지)");
            }

            editor.putString("a",all_friends);

            Log.d("TAG", "run: all_friends 1" + editor.putString("a",all_friends));
          //  editor.putString("roomName", roomName);
            editor.commit();
        }


    }

    void saveChatContent() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preference", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chatData);

        editor.putString("task" + roomName, json);

        editor.apply();


    }

    void loadChatContent() {

        SharedPreferences sharedPreferences = getSharedPreferences("shared preference", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task" + roomName, null);

        Type type = new TypeToken<ArrayList<ChatData>>() {
        }.getType();
        chatData = gson.fromJson(json, type);

        if (chatData == null) {
            chatData = new ArrayList<>();
        }


    }

    void check() {
        SharedPreferences sharedPreferences = getSharedPreferences("check", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("check", check);
        editor.commit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveRoomName();
        saveChatContent();
        check();

        if (check == "in" && turnResult != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("room", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("friend",roomName);
            Log.d("TAG", "run:roomName "+  editor.putString("friend", roomName));
            editor.putString("lastWord", chatData.get(chatData.size() - 1).getChat_content());

            if (chatData.get(chatData.size() - 1).getChat_content() == null) {
                editor.putString("lastWord", "(이미지)");
            }
            editor.putString("a",all_friends);
            Log.d("TAG", "run: all_friends2 " + editor.putString("a",all_friends));
        //    editor.putString("roomName", roomName);
            editor.putString("turnResult", turnResult);
            editor.commit();
            setResult(RESULT_CANCELED);

        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        Log.d("test", "onDestroy: 12312312313");
    }


    @Override
    public void onBackPressed() {


        if (check == "in" && turnResult != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("room", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("friend", roomName);
            Log.d("TAG", "run:roomName "+  editor.putString("friend", roomName));
            editor.putString("lastWord", chatData.get(chatData.size() - 1).getChat_content());

            if(chatData.get(chatData.size() - 1).getChat_content() == null){
                editor.putString("lastWord", "(이미지)");
            }
            editor.putString("a",all_friends);
            Log.d("TAG", "run: all_friends 3" +  editor.putString("a",all_friends));
         //   editor.putString("roomName", roomName);
            editor.putString("turnResult", turnResult);
            editor.commit();
            setResult(RESULT_OK);

        }

        super.onBackPressed();
    }

    void clickContent(){
        contentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requirePermission();
                boolean camera = ContextCompat.checkSelfPermission                //카메라 권한
                        (view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

                boolean write = ContextCompat.checkSelfPermission                //쓰기 권한
                        (view.getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if(camera && write){
                    // 사진찍는 인텐트 코드 넣기
                   showAlert();

                }else{
                    Toast.makeText(Realchat.this, "사용 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void requirePermission(){

        String [] permissions = new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionNeeded = new ArrayList<>();

        for(String permission : permissions){

            if(ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED){
                //** 권한이 허가가 안됬을 경우 나중에 권한이 거부된 것만 모아서 다시 요청 하기 위해 저장 하는 부분*/
                listPermissionNeeded.add(permission);
            }
        }

        if(!listPermissionNeeded.isEmpty()){
            //** 권한 거부 된것을  재요청 하는 부분*/
            ActivityCompat.requestPermissions(this,listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),1);
        }
    }

    void showAlert(){
        final CharSequence[] items ={"Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("선택하세요")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(items[i] == items[0]){
                            takeGallery();
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void takeGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_CODE){
                uri = data.getData();

                chatData.add(new ChatData(null,null,uri.toString(),null,null));
                adapter.notifyDataSetChanged();
                check = "in";

                Map<String, RequestBody> map = new HashMap<>();


                String path= getRealPathFromURI(uri);
                File file = new File(path);

                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"),file);
                map.put("file\"; filename=\""+file.getName() + "\"",requestBody);
                RequestBody uName = RequestBody.create(MediaType.parse("text/plain"),me);


                ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Call<User> call = apiInterface.fcm_image("token",map,uName);


                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d("TAG", "onResponse: 업로드완료"+response.body().getSend_image());

                        String send_image = response.body().getSend_image();

                        sendImage(send_image);




                        JSONObject Image_data = new JSONObject();

                        roomName = "나";
                        for(int i =0; i<multi_name.size();i++) {


                            String check_friend = multi_name.get(i).toString();
                            Log.d("TAG", "run:roomName cc "+check_friend);
                            if(!me.toString().equals(check_friend)) {
                                roomName += "/"+check_friend;
                                Log.d("TAG", "run:roomName cc"+check_friend);
                                try {
                                    Image_data.put("friend" + i, check_friend);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        socket.emit("friend_nick_name_image",Image_data);









                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

            }
        }

    }


    public void sendImage(String url)
    {
        JSONObject sendData = new JSONObject();
        try{
            sendData.put("who",me);
            sendData.put("image", url);
            sendData.put("psa",myImage);
            socket.emit("message_image",sendData);
        }catch(JSONException e){
        }
    }



    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,10,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }






}
