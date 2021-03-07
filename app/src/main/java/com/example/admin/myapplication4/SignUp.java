package com.example.admin.myapplication4;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    private EditText user_name,user_password,confirm_user_password,user_email;
    private Button btnSignUp,user_email_btn,back;
    TextView id_info,pwd_info,confirm_pwd_info,confrim_user_email;
    String email,keep_email_num,keep_email_name;

    public static PrefConfig prefConfig;
    public static ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        prefConfig = new PrefConfig(this);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        user_name = (EditText)findViewById(R.id.user_name);
        user_password =(EditText)findViewById(R.id.user_password);
        confirm_user_password =(EditText)findViewById(R.id.confirm_user_password);
        user_email = (EditText)findViewById(R.id.user_email);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        id_info = (TextView) findViewById(R.id.id_info);
        pwd_info = (TextView) findViewById(R.id.pwd_info);
        confirm_pwd_info = (TextView) findViewById(R.id.confirm_pwd_info);
        confrim_user_email = (TextView) findViewById(R.id. confrim_user_email);
        user_email_btn = (Button) findViewById(R.id.user_email_btn);
        back = (Button) findViewById(R.id.back);

        emailConfirmClick();

        confrim_user_email.setEnabled(false);
        back.setEnabled(false);


        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(user_name.getText().toString().equals("admin")){
                    id_info.setText("사용이 불가한 아이디 입니다.");
                }else if(user_name.getText().toString().length() < 5){
                    id_info.setText("5글자 이상 적어주세요");
                }else{
                    id_info.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        user_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(user_password.getText().toString().length() <6) {
                   pwd_info.setText("6글자 이상 적어주세요");
               }else{
                   pwd_info.setText("");
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confirm_user_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!user_password.getText().toString().equals(confirm_user_password.getText().toString())){
                    confirm_pwd_info.setText("비밀번호를 다시 확인해주세요");
                }else{
                    confirm_pwd_info.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_name.getText().toString().length() <5 || user_name.getText().toString().equals("admin") ||
                        !user_password.getText().toString().equals(confirm_user_password.getText().toString()) || user_email.getText().toString().equals("")){
                    Toast.makeText(SignUp.this, "정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                }else if(!user_email.getText().toString().equals(keep_email_name)){
                    Toast.makeText(SignUp.this, "이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else{
                    performSignUp();

                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_email_btn.setEnabled(true);
                user_email_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                user_email.setText("");
                user_email.setEnabled(true);
                confrim_user_email.setText("");
                confrim_user_email.setEnabled(false);
                back.setBackgroundColor(Color.LTGRAY);
                back.setEnabled(false);
            }
        });
    }
    public void performSignUp(){
        String name = user_name.getText().toString();
        String password = user_password.getText().toString();
        String email = user_email.getText().toString();

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String token = FirebaseInstanceId.getInstance().getToken();


        Call<User> call = apiInterface.performSignUp(name,password,email,token);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(response.body().getResponse().equals("ok")){
                   prefConfig.displayToast("환영합니다! 회원가입이 완료되었습니다.");


                   finish();
                }else if(response.body().getResponse().equals("exist")){
                   prefConfig.displayToast("이미 존재하는 아이디 입니다.");
                }else if(response.body().getResponse().equals("error")){
                   prefConfig.displayToast("네트워크를 확인해주세요");
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    public void emailConfirmClick(){



            user_email_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (user_email.getText().toString().length() > 7){

                        user_email_btn.setEnabled(false);
                        user_email_btn.setBackgroundColor(Color.LTGRAY);
                        user_email.setEnabled(false);
                        confrim_user_email.setEnabled(true);
                        back.setEnabled(true);
                        back.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        email = user_email.getText().toString();

                    Call<User> call = apiInterface.emailConfirm(email);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            keep_email_num = response.body().email_num;
                            keep_email_name = response.body().email;
                            Toast.makeText(SignUp.this, "해당 이메일로 인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "onResponse: " + keep_email_num);

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });
                }else{

                    }
                }
            });

    }

}
