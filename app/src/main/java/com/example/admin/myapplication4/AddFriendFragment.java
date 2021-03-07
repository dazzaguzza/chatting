package com.example.admin.myapplication4;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFriendFragment extends Fragment {

    ListView add_friend_list_view;
    ImageView add_friend_image;
    SearchView search;
    TextView add_friend_id, add_friend_write;
    AddFriendAdapter addFriendAdapter;
    ArrayList<ListviewUser> addfriend = new ArrayList<>();
    PrefConfig prefConfig;
    public static ApiInterface apiInterface;

    public AddFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        add_friend_write = (TextView) view.findViewById(R.id.add_friend_write);
        add_friend_id = (TextView) view.findViewById(R.id.add_friend_id);
        search = (SearchView) view.findViewById(R.id.search_view);
        add_friend_image = (ImageView) view.findViewById(R.id.add_friend_image);
        add_friend_list_view = (ListView) view.findViewById(R.id.add_friend_ListView);

        addFriendAdapter = new AddFriendAdapter(getActivity(), addfriend);
        add_friend_list_view.setAdapter(addFriendAdapter);
        prefConfig = new PrefConfig(getActivity());
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);


        final String login_user_name = prefConfig.readName();
        Call<List<User>> call = apiInterface.addfriend(login_user_name);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                for (int i = 0; i < response.body().size(); i++) {


                    ListviewUser listviewUser = new ListviewUser();

                    listviewUser.setId(response.body().get(i).user_name);
                    listviewUser.setWrite(response.body().get(i).user_message);

                    listviewUser.setImage("http://13.124.254.43/" + response.body().get(i).user_image);

                    addfriend.add(listviewUser);


                }
                addFriendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

       search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String s) {

               return false;
           }

           @Override
           public boolean onQueryTextChange(String s) {
               addFriendAdapter.getFilter().filter(s);
               return false;
           }
       });

        return view;
    }

}
