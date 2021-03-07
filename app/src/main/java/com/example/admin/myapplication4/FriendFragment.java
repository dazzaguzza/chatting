package com.example.admin.myapplication4;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {


    FriendAdapter friendAdapter;
    ArrayList<ListviewUser> arrayList = new ArrayList<>();
    ListviewUser listviewUser = new ListviewUser();
    ListviewUser listviewUser3 = new ListviewUser();
    ListView listView;
    PrefConfig prefConfig;
    bus bus;
    public static ApiInterface apiInterface;
    FloatingActionButton fab;
    ListviewUser listviewUser1;


    final int ITEM_VIEW_TYPE_MINE = 0;
    final int ITEM_VIEW_TYPE_FRIEND = 1;
    final int ITEM_VIEW_TYPE_TEXT = 2;

    public interface bus {
        public void loadAndsave(String image, String status);
    }


    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);


        listView = view.findViewById(R.id.friendList);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        prefConfig = new PrefConfig(getActivity());
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);


        friendAdapter = new FriendAdapter(getActivity(), arrayList);
        listView.setAdapter(friendAdapter);


        floating();
        listviewClick();
        return view;
    }

    void settingAll() {

        final String login_user_name = prefConfig.readName();
        Call<List<User>> call = apiInterface.findme(login_user_name);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                listviewUser.setMyId(response.body().get(0).login_user_name);
                listviewUser.setMyWrite(response.body().get(0).login_user_message);
                listviewUser.setMyImage("http://13.124.254.43/" + response.body().get(0).login_user_image);
                listviewUser.setType(ITEM_VIEW_TYPE_MINE);

                arrayList.add(0, listviewUser);

                bus.loadAndsave(getString(R.string.image_url) + response.body().get(0).login_user_image, response.body().get(0).login_user_message);
                prefConfig.writeImage("http://13.124.254.43/" + response.body().get(0).login_user_image);

                listviewUser3.setType(ITEM_VIEW_TYPE_TEXT);
                arrayList.add(1, listviewUser3);


                for (int i = 1; i < response.body().size(); i++) {


                    listviewUser1 = new ListviewUser();

                    listviewUser1.setId(response.body().get(i).friend);
                    listviewUser1.setWrite(response.body().get(i).user_message);
                    listviewUser1.setType(ITEM_VIEW_TYPE_FRIEND);
                    listviewUser1.setImage("http://13.124.254.43/" + response.body().get(i).user_image);

                    arrayList.add(i + 1, listviewUser1);

                }

                friendAdapter.notifyDataSetChanged();


//                  listviewUser.setMyId(response.body().get(0).login_user_name);
//                  listviewUser.setMyWrite(response.body().get(0).login_user_message);
//                  listviewUser.setMyImage("http://13.124.254.43/" + response.body().get(0).login_user_image);
//                  listviewUser.setType(ITEM_VIEW_TYPE_MINE);
//
//                  arrayList.add(0, listviewUser);
//
//               //   bus.loadAndsave("http://13.124.254.43/" + response.body().get(0).login_user_image, response.body().get(0).login_user_message);
//
//
//                    listviewUser3.setType(ITEM_VIEW_TYPE_TEXT);
//                    arrayList.add(1, listviewUser3);
//
//
//              for(int i =0; i<response.body().size()-1;i++){
//                  ListviewUser listviewUser1 = new ListviewUser();
//                  listviewUser1.setType(ITEM_VIEW_TYPE_FRIEND);
//                  listviewUser1.setMyId(response.body().get(i+1).friend);
//                  listviewUser1.setMyWrite(response.body().get(i+1).user_message);
//                  listviewUser1.setMyImage("http://13.124.254.43/" + response.body().get(i+1).user_image);
//                  Log.d("TAG", "onResponse: "+response.body().get(i+1).friend);
//                  Log.d("TAG", "onResponse: "+response.body().get(i+1).user_message);
//                  arrayList.add(i+2, listviewUser1);
//                  Log.d("TAG", "onResponse: "+arrayList.get(2).getId());
//              }
//
//
//                friendAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

    }

    void listviewClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    Intent intent = new Intent(getActivity(), Profile.class);
                    startActivity(intent);
                } else if (i == 1) {

                } else {
                    Intent intent = new Intent(getActivity(), Show_Friend_Profile.class);
                    intent.putExtra("friend", arrayList.get(i).getId());
                    startActivity(intent);

                }
            }
        });
    }

    void floating(){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getId() == listView.getId()) {
                    final int currentFirstVisibleItem = listView.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        fab.setClickable(false);
                        fab.hide();
                    }else if(currentFirstVisibleItem < mLastFirstVisibleItem){
                        fab.setClickable(true);
                        fab.show();
                    }
                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getActivity(),ChooseMulti.class);
                intent.putExtra("multi",arrayList);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayList.clear();
        settingAll();
        friendAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        bus = (FriendFragment.bus) activity;
    }
}
