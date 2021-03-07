package com.example.admin.myapplication4;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    ListView chatListview;
    TextView chat_room_id,chat_room_content;
    ChatRoomDataAdapter adapter;
    ArrayList<ChatRoomData> chatRoomData = new ArrayList<>();;
    String getRoomName,getFriend,getLastword,check,getAllFriends ;
    int position;
    PrefConfig prefConfig;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_chat, container, false);



       chat_room_id = (TextView) view.findViewById(R.id.chat_room_id);
       chat_room_content = (TextView) view.findViewById(R.id.chat_room_content);
       chatListview = (ListView) view.findViewById(R.id.chatListview);
       prefConfig = new PrefConfig(getActivity());


    load();




       adapter = new ChatRoomDataAdapter(chatRoomData,getActivity());
       chatListview.setAdapter(adapter);

        loadRoomName();



        itemClick();
        longItemClick();


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice,
                new IntentFilter("fcm"));


        return view;
    }

    void loadRoomName() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("room", MODE_PRIVATE);
        getFriend = sharedPreferences.getString("friend", null);
        getLastword = sharedPreferences.getString("lastWord", null);
      //  getRoomName = sharedPreferences.getString("roomName",null);
        getAllFriends = sharedPreferences.getString("a",null);

        Log.d("TAG", "loadRoomName: "+getFriend);





        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("check", MODE_PRIVATE);
        check = sharedPreferences1.getString("check", null);
        Log.d("TAG", "loadRoomName:"+check);
        if (check != null) {
            Log.d("TAG", "loadRoomName:"+chatRoomData.size());
            if(chatRoomData.size() == 0){
                chatRoomData.add(0, new ChatRoomData(getFriend, getLastword));
                chatRoomData.get(0).setAll_friend(getAllFriends);
                adapter.notifyDataSetChanged();
            }else {
                try {
                    for (int i = 0; i < chatRoomData.size(); i++) {
                        Log.d("TAG", "loadRoomName123: 체크 널이 아님");
                        if (chatRoomData.get(i).getChat_room_id().equals(getFriend)) {
                            chatRoomData.remove(i);
                            chatRoomData.add(0, new ChatRoomData(getFriend, getLastword));
                            chatRoomData.get(0).setAll_friend(getAllFriends);
                            Log.d("TAG", "loadRoomName2: "+getFriend);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                        else {
                            if(getFriend != null) {
                                chatRoomData.add(0, new ChatRoomData(getFriend, getLastword));
                                chatRoomData.get(0).setAll_friend(getAllFriends);

                                for (int j = 1; j < chatRoomData.size(); j++) {
                                    if (chatRoomData.get(j).getChat_room_id().equals(getFriend)) {
                                        Log.d("TAG", "loadRoomName1: " + getFriend);
                                        chatRoomData.remove(j);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    }

                                }

                                adapter.notifyDataSetChanged();


                                break;
                            }

                        }

                    }

                }catch(Exception e){}
            }
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.clear();
            editor.commit();
        }




    }

    void itemClick(){
        chatListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String friend_all ;
                String friend;
                ArrayList<String> tmp_name = new ArrayList<>();
                friend_all = chatRoomData.get(i).getAll_friend();
                Log.d("TAG", "onItemClick:friend all "+friend_all);
                String chat_key = chatRoomData.get(i).getChat_room_id();

                try {
                    JSONObject jsonObject = new JSONObject(friend_all);



                        for(int j =0; j<jsonObject.length();j++){
                            friend = jsonObject.getString("friend"+j);
                            tmp_name.add(friend);
                        }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getActivity(),Realchat.class);
                intent.putStringArrayListExtra("multi_name",tmp_name);
                intent.putExtra("chat_key",chat_key);


                String turnResult = "turnResult";

                position = i;

                intent.putExtra("turnResult",turnResult);
                startActivityForResult(intent,100);

            }
        });
    }

    void longItemClick(){
        chatListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int position = i;

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("채팅룸 삭제");
                builder.setMessage("채팅룸을 삭제 하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                                String getClearName = chatRoomData.get(position).getChat_room_id();
                              //  Toast.makeText(getActivity(), ""+getClearName, Toast.LENGTH_SHORT).show();
                                chatRoomData.remove(position);

                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preference",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("task"+getClearName);
                                editor.commit();


                                adapter.notifyDataSetChanged();
                                save();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                             //   Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                               dialog.dismiss();
                            }
                        });
                builder.show();

                return false;


            }
        });
    }

    void save(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chatRoomData);

        editor.putString("task",json);
        editor.commit();


    }
    void load(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
       String json = sharedPreferences.getString("task",null);

        Type type = new TypeToken<ArrayList<ChatRoomData>>(){}.getType();
       chatRoomData = gson.fromJson(json,type);

       if(chatRoomData == null){
            chatRoomData = new ArrayList<>();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            if (requestCode == 100) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("room", MODE_PRIVATE);
                getFriend = sharedPreferences.getString("friend", null);
                String getLastword = sharedPreferences.getString("lastWord", null);
               // getRoomName = sharedPreferences.getString("roomName", null);
                getAllFriends = sharedPreferences.getString("a",null);



                chatRoomData.remove(position);
                Log.d("TAG", "onActivityResult: "+position);
               chatRoomData.add(0, new ChatRoomData(getFriend, getLastword));
                Log.d("TAG", "onActivityResult: "+getFriend);
                chatRoomData.get(0).setAll_friend(getAllFriends);
                Log.d("TAG", "onActivityResult: "+getAllFriends);
                adapter.notifyDataSetChanged();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

            }

        }else if(requestCode == RESULT_CANCELED){
            if(check != null){
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("room", MODE_PRIVATE);
                getFriend = sharedPreferences.getString("friend", null);
                String getLastword = sharedPreferences.getString("lastWord", null);
             //   getRoomName = sharedPreferences.getString("roomName", null);
                getAllFriends = sharedPreferences.getString("a",null);



                chatRoomData.remove(position);
                chatRoomData.add(0, new ChatRoomData(getFriend, getLastword));
                chatRoomData.get(0).setAll_friend(getAllFriends);
                adapter.notifyDataSetChanged();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
            }
        }
    }





    @Override
    public void onResume() {
        super.onResume();
        loadRoomName();
    }

    @Override
    public void onStop() {
        super.onStop();

        save();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
         //   String sender = intent.getStringExtra("sender");
            String message = intent.getStringExtra("message");
          //  String room = intent.getStringExtra("room");
            String all = intent.getStringExtra("all");

            String me = prefConfig.readName();
            String add_name = "나";
            try {
                JSONObject jsonObject = new JSONObject(all);

                for(int i =0 ; i<jsonObject.length();i++){

                    String friend_all = jsonObject.getString("friend"+i);
                    if(!me.toString().equals(friend_all)){
                        add_name += "/"+friend_all;
                    }
                    Log.d("TAG", "onReceive: addname = "+add_name);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (chatRoomData.size() == 0) {
                chatRoomData.add(0, new ChatRoomData(add_name, message));
                chatRoomData.get(0).setAll_friend(all);
                adapter.notifyDataSetChanged();
               // Log.d("TAG", "onReceive: "+chatRoomData.get(0).getAll_friend());

            } else {
                try {
                    for (int i = 0; i < chatRoomData.size(); i++) {

                        if (chatRoomData.get(i).getChat_room_id().equals(add_name)) {
                            chatRoomData.remove(i);
                            Log.d("TAG", "loadRoomName122: "+add_name);
                            chatRoomData.add(0, new ChatRoomData(add_name, message));
                            chatRoomData.get(0).setAll_friend(all);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                        else {
                            chatRoomData.add(0, new ChatRoomData(add_name, message));
                            chatRoomData.get(0).setAll_friend(all);
                            Log.d("TAG", "loadRoomName233: "+add_name);
                            for(int j =1;j<chatRoomData.size();j++){
                                if(chatRoomData.get(j).getChat_room_id().equals(add_name)){

                                    chatRoomData.remove(j);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }

                            }
                            adapter.notifyDataSetChanged();
                            break;
                        }

                    }

                } catch (Exception e) {}
                }
            save();
        }

    };

}
