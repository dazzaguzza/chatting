package com.example.admin.myapplication4;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {

        Log.d("Tag","From: "+message.getFrom());

        if(message.getData().size() >0){
            Log.d("Tag","Message data payload: "+message.getData());
        }
        if(message.getNotification() != null){
            Log.d("TAG", "Message Notification Body: "+message.getNotification().getBody());
        }

        if(message.getData().containsKey("message")) {
            sendMyNotification(message.getData().get("message"),message.getData().get("sender"),message.getData().get("friend"),message.getData().get("psa"));

            Log.d("TAG", "onMessageReceivedpsa: "+message.getData().get("psa"));


//            try {
//                JSONObject jsonObject = new JSONObject(message.getData().get("friend"));
//                for(int i =0 ; i<jsonObject.length();i++){
//
//                 
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


        }else if(message.getData().containsKey("image")){
            getImageFCM(message.getData().get("image"),message.getData().get("sender"),message.getData().get("friend"),message.getData().get("psa"));

            Log.d("TAG", "onMessageReceived: "+message.getData().get("image"));
        }
        }


    private void sendMyNotification(String message,String sender,String all,String psa) {

        String channelId = "channel";
        String channelName = "Channel Name";

        NotificationManager notifManager = (NotificationManager) getSystemService  (Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            notifManager.createNotificationChannel(mChannel);

        }

        //On click of notification it redirect to this Activity

        PrefConfig prefConfig = new PrefConfig(getApplicationContext());
        String me = prefConfig.readName();

        String add_name = "나";
        ArrayList<String> tmpArray = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(all);

            for(int i =0 ; i<jsonObject.length();i++){

                String friend_all = jsonObject.getString("friend"+i);
                tmpArray.add(friend_all);
                if(!me.toString().equals(friend_all)){
                    add_name += "/"+friend_all;
                }
                Log.d("TAG", "onReceive: addname = "+add_name);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = getBitmapFromURL(psa);
        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.backgroundimage);
        }
        Bitmap circle = getCircleBitmap(bitmap);

        Intent intent = new Intent(getApplicationContext(),ListOfMember.class);
        intent.putStringArrayListExtra("multi_name",tmpArray);
        intent.putExtra("chat_key",add_name);

            Log.d("TAG", "sendMyNotification: " + tmpArray);
            Log.d("TAG", "sendMyNotification: " + add_name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);



        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(circle)
                .setContentTitle("Blah Blah")
                .setContentText(sender+": "+message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0, notificationBuilder.build());

        String check = "in";
        SharedPreferences sharedPreferences1 = getSharedPreferences("check", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putString("check", check);
        editor1.commit();


        SharedPreferences sharedPreferences = getSharedPreferences("room", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putString("friend", add_name);

        editor.putString("lastWord", message);
       // editor.putString("roomName",all);
        editor.putString("a",all);
        editor.commit();





        Intent intent1 = new Intent("fcm");
      //  intent1.putExtra("sender",sender);
        intent1.putExtra("message",message);
       // intent1.putExtra("room",add_name);
        intent1.putExtra("all",all);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

        chatContentSaveAndLoad(sender,message,null,add_name,psa);

    }

    void chatContentSaveAndLoad(String sender,String message,String url,String room,String psa){
        ArrayList<ChatData> chatData = new ArrayList<>();
        SharedPreferences sharedPreferences2 = getSharedPreferences("shared preference",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences2.getString("task"+room,null);

        Type type = new TypeToken<ArrayList<ChatData>>(){}.getType();
        chatData = gson.fromJson(json,type);

        if(chatData == null){
            chatData = new ArrayList<>();
        }

        chatData.add(new ChatData(message,sender,null,url,psa));

        SharedPreferences sharedPreferences = getSharedPreferences("shared preference",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(chatData);

        editor.putString("task"+room,json1);

        editor.apply();

    }

    private void getImageFCM(String uri,String sender,String all,String psa){
        String channelId = "channel";
        String channelName = "Channel Name";

        NotificationManager notifManager = (NotificationManager) getSystemService  (Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            notifManager.createNotificationChannel(mChannel);

        }



        PrefConfig prefConfig = new PrefConfig(getApplicationContext());
        String me = prefConfig.readName();

        String add_name = "나";
        ArrayList<String> tmpArray = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(all);

            for(int i =0 ; i<jsonObject.length();i++){

                String friend_all = jsonObject.getString("friend"+i);
                tmpArray.add(friend_all);
                if(!me.toString().equals(friend_all)){
                    add_name += "/"+friend_all;
                }
                Log.d("TAG", "onReceive: addname = "+add_name);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bitmap circle;
        Bitmap bitmap = getBitmapFromURL(psa);
        if(bitmap != null) {
            circle = getCircleBitmap(bitmap);
        }else{
           bitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.backgroundimage);
            circle = getCircleBitmap(bitmap);
        }

        //On click of notification it redirect to this Activity
        String stringImage = "(이미지)";
        Intent intent = new Intent(getApplicationContext(),ListOfMember.class);
        intent.putStringArrayListExtra("multi_name",tmpArray);
        intent.putExtra("chat_key",add_name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(circle)
                .setContentTitle("Blah Blah")
                .setContentText(sender+": "+stringImage)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0, notificationBuilder.build());



        String check = "in";
        SharedPreferences sharedPreferences1 = getSharedPreferences("check", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putString("check", check);
        editor1.commit();


        SharedPreferences sharedPreferences = getSharedPreferences("room", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("lastWord", stringImage);
        editor.putString("friend", add_name);
        editor.putString("a",all);
        editor.commit();
        editor.commit();




        Intent intent1 = new Intent("fcm");
        //intent1.putExtra("sender",sender);
        intent1.putExtra("message",stringImage);
        intent1.putExtra("all",all);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);


        chatContentSaveAndLoad(sender,null,uri,add_name,psa);

    }

    public static Bitmap getBitmapFromURL(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        Rect srcRect, dstRect;
        float r;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width > height){
            output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
            int left = (width - height) / 2;
            int right = left + height;
            srcRect = new Rect(left, 0, right, height);
            dstRect = new Rect(0, 0, height, height);
            r = height / 2;
        }else{
            output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            int top = (height - width)/2;
            int bottom = top + width;
            srcRect = new Rect(0, top, width, bottom);
            dstRect = new Rect(0, 0, width, width);
            r = width / 2;
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        bitmap.recycle();

        return output;
    }
}
