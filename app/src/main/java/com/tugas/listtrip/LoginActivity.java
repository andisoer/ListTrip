package com.tugas.listtrip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.tugas.listtrip.api.ApiEndPoints;
import com.tugas.listtrip.api.ConfigApi;
import com.tugas.listtrip.config.Config;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText tieEmail, tiePassword;
    private TextView tvDontHaveAccount;
    private Button btnLogin;
    ApiEndPoints mApiService;
    ProgressDialog progress;
    SharedPreferences sharedObject;

    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedObject = getSharedPreferences("profileData", Context.MODE_PRIVATE);
        boolean login_status = sharedObject.getBoolean("login_status", false);
        if (login_status){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        initView();
        mApiService = ConfigApi.getApiService();

    }

    private void initView() {

        btnLogin = findViewById(R.id.btnLogin);
        tieEmail = findViewById(R.id.tieEmailLogin);
        tiePassword = findViewById(R.id.tiePasswordLogin);
        tvDontHaveAccount = findViewById(R.id.tvDontHaveAccount);

        btnLogin.setOnClickListener(this);
        tvDontHaveAccount.setOnClickListener(this);
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setCancelable(false);

    }

    private void intentToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnLogin:
                checkForm();
                break;
            case R.id.tvDontHaveAccount:
                intentToRegister();
                break;
        }
    }

    private void checkForm(){
        String email = tieEmail.getText().toString();
        String password_user = tiePassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            tieEmail.setError(getResources().getString(R.string.fill_email));
        }else if(TextUtils.isEmpty(password_user)){
            tiePassword.setError(getResources().getString(R.string.password));
        }else{
            loginUser(email,password_user);
        }
    }

    private void loginUser(String email, String password_user) {
        progress.show();
        mApiService.login(email, password_user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {

                if (response.isSuccessful()){
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        boolean error = json.getBoolean(Config.TAG_error);

                        if (error){
                            progress.dismiss();
                        }else{
                            JSONObject sendObject = json.getJSONObject("data");
                            String idUser   = sendObject.getString("id_user");
                            String name     = sendObject.getString("name");
                            String email    = sendObject.getString("email");
                            String phone    = sendObject.getString("phone");
                            String address  = sendObject.getString("address");
                            String password = sendObject.getString("password_user");
                            setSessionValue(idUser,name,email,phone,address, password);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                        Log.e(TAG+" loginUser","onResponse : "+e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                        Log.e(TAG+" loginUser","onResponse : "+e.getMessage());
                    }

                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
                Log.e(TAG+" loginUser","onFailure : "+t.getMessage());
            }
        });
    }

    private void setSessionValue(String idUser, String name, String email, String phone, String address, String password) {
        SharedPreferences.Editor edit = sharedObject.edit();
        edit.putString("idUser", idUser);
        edit.putString("name", name);
        edit.putString("email", email);
        edit.putString("phone", phone);
        edit.putString("address", address);
        edit.putString("password", password);
        edit.putBoolean("login_status", true);
        edit.apply();


        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
