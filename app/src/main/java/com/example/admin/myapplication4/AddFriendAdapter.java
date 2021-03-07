package com.example.admin.myapplication4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class AddFriendAdapter extends BaseAdapter implements Filterable {

    Context context;
    ArrayList<ListviewUser> addfriend;
    ArrayList<ListviewUser> searchArray;
    LayoutInflater layoutInflater;
    PrefConfig prefConfig;
    public static ApiInterface apiInterface;
    String getFriendName;
    ValueFilter valueFilter;


    public AddFriendAdapter(Context context, ArrayList<ListviewUser> addfriend) {
        this.context = context;
        this.addfriend = addfriend;
        this.searchArray = addfriend;
    }

    @Override
    public int getCount() {
        return addfriend.size();
    }

    @Override
    public Object getItem(int position) {
        return addfriend.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.add_friend_list_view_item, viewGroup, false);
        }
        prefConfig = new PrefConfig(context);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        ImageView tv_image = (ImageView) view.findViewById(R.id.add_friend_image);
        TextView tv_id = (TextView) view.findViewById(R.id.add_friend_id);
        TextView tv_write = (TextView) view.findViewById(R.id.add_friend_write);
        ImageButton tv_add_button = (ImageButton) view.findViewById(R.id.add_button);

        tv_image.setBackground(new ShapeDrawable(new OvalShape())); // 이미지뷰 둥글게 만들기
        if (Build.VERSION.SDK_INT >= 21) {
            tv_image.setClipToOutline(true);
        }



        String image_position = addfriend.get(position).getImage();
        Glide.with(context).load(image_position).error(R.drawable.backgroundimage).into(tv_image);

        tv_id.setText(addfriend.get(position).getId());
        tv_write.setText(addfriend.get(position).getWrite());

        tv_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("친구추가");
                builder.setMessage("친구를 추가 하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();

                                getFriendName = addfriend.get(position).getId();

                                Call<User> call = apiInterface.friend(prefConfig.readName(), getFriendName);
                                call.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {

                                        addfriend.remove(addfriend.get(position));

                                        notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {

                                    }
                                });

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


            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {

        if(valueFilter == null){
           valueFilter = new ValueFilter();
        }


        return valueFilter;
    }

    private class ValueFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if(constraint != null && constraint.length() > 0){
                ArrayList<ListviewUser> filterList = new ArrayList<>();

                for(int i =0; i< searchArray.size(); i++){
                    if((searchArray.get(i).getId().toUpperCase()).contains(constraint.toString().toUpperCase())){
                        ListviewUser listviewUser = new ListviewUser();

                        listviewUser.setId(searchArray.get(i).getId());
                        listviewUser.setImage(searchArray.get(i).getImage());
                        listviewUser.setWrite(searchArray.get(i).getWrite());

                        filterList.add(listviewUser);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            }else{
                results.count = searchArray.size();
                results.values = searchArray;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {

            addfriend = (ArrayList<ListviewUser>) filterResults.values;
            notifyDataSetChanged();

        }
    }
}
