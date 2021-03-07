package com.example.admin.myapplication4;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private EditText edId,edPassword;
    private Button btnLogin;
    public static ApiInterface apiInterface;
    PrefConfig prefConfig;
    TextView txtSignUp,find_pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        prefConfig = new PrefConfig(Login.this);

        edId = findViewById(R.id.edId);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUp = findViewById(R.id.txtSignUp);
        find_pwd = findViewById(R.id.find_pwd);

        SpannableString content = new SpannableString("회원가입 하기");
        content.setSpan(new StyleSpan(Typeface.BOLD_ITALIC),0,content.length(),0);

        txtSignUp.setText(content);


        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                performLogin();
            }
        });

        find_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,FindPassword.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin(){
        String id = edId.getText().toString();
        String password = edPassword.getText().toString();

        Call<User> call = apiInterface.performLogin(id,password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.body().getResponse().equals("ok")){


                 login(response.body().getLogin_user_name());

                }else if(response.body().getResponse().equals("failed")){
                    prefConfig.displayToast("정보가 올바르지 않습니다");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        edPassword.setText("");
    }
    public void login(String name){
        prefConfig.writeName(name);
        prefConfig.writeLoginStatus(true);
        Intent intent = new Intent(this,ListOfMember.class);
        startActivity(intent);
    }


    @Override
    public void finish() {
        super.finish();

        this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }


}
