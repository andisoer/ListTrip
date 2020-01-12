package com.tugas.listtrip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout tilPassword, tilConfirmPassword;
    TextInputEditText tieName, tieEmail, tiePassword, tieConfirmPassword, tiePhone, tieAddress;
    Button btnRegister;

    ApiEndPoints mApiService;

    private String TAG = RegisterActivity.class.getSimpleName();
    ProgressDialog proges;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        mApiService = ConfigApi.getApiService();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.tbRegister);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tilPassword = findViewById(R.id.tilPasswordRegister);
        tilConfirmPassword = findViewById(R.id.tilConfirmPasswordRegister);
        tieName = findViewById(R.id.tieNameRegister);
        tieEmail = findViewById(R.id.tieEmailRegister);
        tiePassword = findViewById(R.id.tiePasswordRegister);
        tieConfirmPassword = findViewById(R.id.tieConfirmPassworRegister);
        tiePhone = findViewById(R.id.tiePhoneRegister);
        tieAddress = findViewById(R.id.tieAddressRegister);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm();
            }
        });
        proges = new ProgressDialog(this);
        proges.setMessage(getResources().getString(R.string.loading));
        proges.setCancelable(false);
    }

    private void checkForm() {
        String name = tieName.getText().toString();
        String email = tieEmail.getText().toString();
        String password = tiePassword.getText().toString();
        String confirmPassword = tieConfirmPassword.getText().toString();
        String phone = tiePhone.getText().toString();
        String address = tieAddress.getText().toString();

        if(TextUtils.isEmpty(name)){
            tieName.setError(getResources().getString(R.string.fill_name));
        }else if(TextUtils.isEmpty(email)){
            tieEmail.setError(getResources().getString(R.string.fill_email));
        }else if(TextUtils.isEmpty(password)){
            tilPassword.setError(getResources().getString(R.string.fill_password));
        }else if(TextUtils.isEmpty(confirmPassword)){
            tilConfirmPassword.setError(getResources().getString(R.string.fill_confirm_pass));
        }else if(TextUtils.isEmpty(phone)){
            tiePhone.setError(getResources().getString(R.string.fill_phone));
        }else if(TextUtils.isEmpty(address)){
            tieAddress.setError(getResources().getString(R.string.fill_address));
        }else{
            if(confirmPassword.equals(password)){
                tilPassword.setError(null);
                tilConfirmPassword.setError(null);
                tieName.setError(null);
                tieEmail.setError(null);
                tiePassword.setError(null);
                tieConfirmPassword.setError(null);
                tiePhone.setError(null);
                tieAddress.setError(null);
                registerUser(name, email, password, phone, address);
            }else{
                tilConfirmPassword.setError(null);
                tilConfirmPassword.setError(getResources().getString(R.string.password_not_match));
            }
        }
    }

    private void registerUser(String name, String email, String password, String phone, String address) {
        proges.show();
        mApiService.register_user(name, email, phone, address, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        boolean error = json.getBoolean(Config.TAG_error);
                        if (error){
                            proges.dismiss();
                            String message = json.getString(Config.TAG_message);
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }else{
                            proges.dismiss();
                            Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e(TAG+" registerUser", "onFailure "+t.getMessage());
                Toast.makeText(RegisterActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
