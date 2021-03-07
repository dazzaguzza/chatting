package com.example.admin.myapplication4;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPassword extends AppCompatActivity {

    Button find_btn;
    EditText find_id, find_email;
    public static ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        find_btn = (Button) findViewById(R.id.find_btn);
        find_id = (EditText) findViewById(R.id.find_id);
        find_email = (EditText) findViewById(R.id.find_email);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = find_id.getText().toString();
                String email = find_email.getText().toString();
                if (id.equals("")) {
                    Toast.makeText(FindPassword.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (email.equals("")) {
                    Toast.makeText(FindPassword.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {

                    Call<User> call = apiInterface.findpassword(email, id);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(response.body().getResponse().equals("success")){
                                Toast.makeText(FindPassword.this, "이메일로 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                find_btn.setEnabled(false);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        find_btn.setEnabled(true);
                                    }
                                },60000);
                            }else if(response.body().getResponse().equals("fail")){
                                Toast.makeText(FindPassword.this, "아이디 또는 이메일이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });
                }


            }
        });

    }
}
