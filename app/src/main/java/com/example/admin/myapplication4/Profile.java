package com.example.admin.myapplication4;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    private static final int CAMERA_CODE = 0;
    private static final int GALLERY_CODE = 1;
    EditText msgEdt;
    TextView nameTxt;
    ImageView changePsa,changeBtn;
    String imageload,msg,imagePath,upload,callImage,callStatus;
    int check;
    String checkAgain;
    Uri uri;
    PrefConfig prefConfig;
    ProgressDialog progressDialog;
    Bitmap bm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));
        }

        changePsa = (ImageView)findViewById(R.id.changePsa);
        changeBtn = (ImageView)findViewById(R.id.changeBtn);
        msgEdt = (EditText)findViewById(R.id.msgEdt);
        nameTxt = (TextView)findViewById(R.id.nameTxt);
        prefConfig = new PrefConfig(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("????????? ??????????????????...");


        Toolbar toolbar = (Toolbar) findViewById(R.id.chToolbar);  //????????????
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //???????????? ?????? ??????
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back); //???????????? ?????? ?????????

        clickPsa();
        change();
        requirePermission();
        load();
      //  Glide.with(this).load(R.drawable.nullimage).into(changePsa);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //???????????? ?????? ????????? ???
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void clickPsa(){
        changePsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean camera = ContextCompat.checkSelfPermission                //????????? ??????
                        (view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

                boolean write = ContextCompat.checkSelfPermission                //?????? ??????
                        (view.getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if(camera && write){
                    // ???????????? ????????? ?????? ??????
                    showlist();

                }else{
                    Toast.makeText(Profile.this, "?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void requirePermission(){

        String [] permissions = new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionNeeded = new ArrayList<>();

        for(String permission : permissions){

            if(ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED){
                //** ????????? ????????? ????????? ?????? ????????? ????????? ????????? ?????? ????????? ?????? ?????? ?????? ?????? ?????? ?????? ??????*/
                listPermissionNeeded.add(permission);
            }
        }

        if(!listPermissionNeeded.isEmpty()){
            //** ?????? ?????? ?????????  ????????? ?????? ??????*/
            ActivityCompat.requestPermissions(this,listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),1);
        }
    }

    public void showlist(){        //** ????????? ????????? ????????? */

        final CharSequence[] items ={"Camera","Gallery","Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("???????????????")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(items[i] == items[0]){
                            takePicture();
                        }else if(items[i] == items[1]){
                            takeGallery();
                        }else{
                            uri = null;
                            imageload=null;
                            imagePath=null;
                            check = 2;


                            changePsa.setImageResource(R.drawable.backgroundimage);
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void takePicture(){  //** ????????? ???????????? */
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            File photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this,"com.example.admin.myapplication4.fileprovider",photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            startActivityForResult(intent,CAMERA_CODE);
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public void takeGallery(){   /** ???????????? ?????? */

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_CODE);
    }

    public File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  //* prefix */
                ".jpg",         //* suffix */
                storageDir      //* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagePath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){

            if(requestCode == CAMERA_CODE){


                Glide.with(this).load(imagePath).into(changePsa);
                check = 0;
                galleryAddPic();


            }else if(requestCode == GALLERY_CODE){

                uri = data.getData();
                imageload = uri.toString();
                Glide.with(this).load(imageload).error(R.drawable.backgroundimage).into(changePsa);
                check = 1;
            }

        }else{
            //    Toast.makeText(this, "?????????????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    public void galleryAddPic() {  /** ????????? ?????? ???????????? ?????? */
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void change(){
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(imagePath != null && check == 0){
                    progressDialog.show();
                    upload = imagePath;
                    sendingData();

//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inSampleSize = 4;
//                    bm = BitmapFactory.decodeFile(imagePath, options);
//
//
//                    try {
//                        ExifInterface exif= new ExifInterface(imagePath);
//                        int exifOrientation = exif.getAttributeInt(
//                                ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
//                        int exifDegree = exifOrientationToDegrees(exifOrientation);
//                        bm=rotate(bm,exifDegree);
//
//                        uri= getImageUri(Profile.this,bm);
//                        upload = getPathFromUri(uri);
//                        sendingData();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }





                //    save(upload);
                }
                if(imageload != null && check == 1){
                    progressDialog.show();
                    upload = getPathFromUri(uri);
                    sendingData();
              //      save(upload);
                }
                if(imageload ==null && imagePath == null && check == 2){
                    msg = msgEdt.getText().toString();
                    String name =  prefConfig.readName();
              //      save(null);

                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<User> call = apiInterface.without(msg,name);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                       finish();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });
                }
                if(check == 4){
                   checkAgain = "ok";
                    msg = msgEdt.getText().toString();
                    String name =  prefConfig.readName();

                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<User> call = apiInterface.uploadAgain(msg,name,checkAgain);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            finish();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });

                }

            }
        });
    }



    public String getPathFromUri(Uri uri){     // Uri ??? String??? file path
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }

//    public static final Uri getUriToDrawable(@NonNull Context context,
//                                             @AnyRes int drawableId) {
//        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                "://" + context.getResources().getResourcePackageName(drawableId)
//                + '/' + context.getResources().getResourceTypeName(drawableId)
//                + '/' + context.getResources().getResourceEntryName(drawableId) );
//        return imageUri;
//    }

    void sendingData(){

        msg = msgEdt.getText().toString();
        String name =  prefConfig.readName();


        Map<String, RequestBody> map = new HashMap<>();

        File file = new File(upload);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"),file);
        map.put("file\"; filename=\""+file.getName() + "\"",requestBody);
        RequestBody eMsg = RequestBody.create(MediaType.parse("text/plain"),msg);
        RequestBody uName = RequestBody.create(MediaType.parse("text/plain"),name);


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<User> call = apiInterface.uploadProfile("token",map,eMsg,uName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(Profile.this, "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void load(){
        String callName = prefConfig.readName();
        SharedPreferences sharedPreferences = getSharedPreferences("save",MODE_PRIVATE);
        callImage = sharedPreferences.getString("image",null);
        callStatus = sharedPreferences.getString("status",null);

        nameTxt.setText(callName);
        msgEdt.setText(callStatus);
        Glide.with(this).load(callImage).error(R.drawable.backgroundimage).into(changePsa);
        check = 4;

    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }












    /**
     * ???????????? ??????????????????.
     *
     * @param bitmap ????????? ?????????
     * @param degrees ?????? ??????
     * @return ????????? ?????????
     */
//    public Bitmap rotate(Bitmap bitmap, int degrees) { //?????? ?????? ?????????
//        if (degrees != 0 && bitmap != null) {
//            Matrix m = new Matrix();
//            m.setRotate(degrees);
//            try {
//                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
//                        bitmap.getWidth(), bitmap.getHeight(), m, true);
//                if (bitmap != converted) {
//                    bitmap = null;
//                    bitmap = converted;
//                    converted = null;
//                }
//            } catch (OutOfMemoryError ex) {
//                Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
//            }
//        }
//        return bitmap;
//    }

    /**
     * EXIF????????? ??????????????? ???????????? ?????????
     *
     * @param exifOrientation EXIF ?????????
     * @return ?????? ??????
     */
//    public int exifOrientationToDegrees(int exifOrientation) {
//        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
//        {
//            return 90;
//        }
//        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
//        {
//            return 180;
//        }
//        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
//        {
//            return 270;
//        }
//        return 0;
//    }










}
