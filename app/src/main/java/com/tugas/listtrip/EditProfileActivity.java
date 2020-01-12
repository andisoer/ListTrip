package com.tugas.listtrip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
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

public class EditProfileActivity extends AppCompatActivity {

    TextInputEditText tilName, tilEmail, tilPhone, tilAddress, tilPassword;
    SharedPreferences sharedData;
    Button btnUpdate;
    ProgressDialog progressDialog;

    private String name, email, address,phone, password, idUser;
    private String TAG = EditProfileActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sharedData = getSharedPreferences("profileData", Context.MODE_PRIVATE);
        idUser = sharedData.getString("idUser", null);
        name = sharedData.getString("name", null);
        email = sharedData.getString("email", null);
        address = sharedData.getString("address", null);
        phone = sharedData.getString("phone", null);
        password = sharedData.getString("password", null);

        initView();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.tbEditProfile);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tilName = findViewById(R.id.textInputName);
        tilEmail = findViewById(R.id.textInputEmail);
        tilAddress = findViewById(R.id.textInputAddress);
        tilPhone = findViewById(R.id.textInputPhone);
        tilPassword = findViewById(R.id.textInputPassword);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tilPassword.getText().toString().equals(password)){
                    cekForm();
                }else{
                    Toast.makeText(EditProfileActivity.this, R.string.password_not_match, Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        setData();

    }

    private void cekForm() {
        name = tilName.getText().toString();
        email = tilEmail.getText().toString();
        address = tilAddress.getText().toString();
        phone = tilPhone.getText().toString();

        if(TextUtils.isEmpty(name)){
            tilName.setError(getResources().getString(R.string.fill_name));
        }else if(TextUtils.isEmpty(email)){
            tilEmail.setError(getResources().getString(R.string.fill_email));
        }else if(TextUtils.isEmpty(address)){
            tilAddress.setError(getResources().getString(R.string.fill_address));
        }else if(TextUtils.isEmpty(phone)){
            tilPhone.setError(getResources().getString(R.string.fill_phone));
        }else{
            tilName.setError(null);
            tilEmail.setError(null);
            tilAddress.setError(null);
            tilPhone.setError(null);
            updateProfile();
        }
    }

    private void updateProfile() {
        progressDialog.show();
        ApiEndPoints mConfigApi = ConfigApi.getApiService();
        mConfigApi.edit_profil(idUser, name, email, address, phone).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if(error){

                            String message = jsonObject.getString("message");
                            Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }else{
                            setSessionValue();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                Log.e(TAG+" updateProfile", "onFailure : "+t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        tilName.setText(name);
        tilEmail.setText(email);
        tilAddress.setText(address);
        tilPhone.setText(phone);
        tilPassword.setText(password);
    }

    private void setSessionValue() {
        SharedPreferences.Editor edit = sharedData.edit();
        edit.putString("name", name);
        edit.putString("email", email);
        edit.putString("phone", phone);
        edit.putString("address", address);
        edit.putString("password", password);
        edit.apply();

        progressDialog.dismiss();
        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
        finish();

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
        return super.onOptionsItemSelected(item);
    }
}
