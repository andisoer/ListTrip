package com.tugas.listtrip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tugas.listtrip.api.ApiEndPoints;
import com.tugas.listtrip.api.ConfigApi;
import com.tugas.listtrip.config.Config;
import com.tugas.listtrip.model.Destination;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailWisataActivity extends AppCompatActivity {

    private String name, ticket, description, view, id_destination, location, photo, id_user;
    private TextView tvName, tvTicket, tvDescription, tvView;
    private ImageView ivDestination;
    private Button btnAddWishlist;
    private ProgressDialog progressDialog;
    private ApiEndPoints baseApiService;
    private boolean isWishlisted = false;

    public static final String DETAIL_DESTINATION = "DETAIL_DESTINATION";
    private String TAG = DetailWisataActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_wisata);

        baseApiService = ConfigApi.getApiService();

        initView();
        addView();
        getDetailWisata();
        checkStatusDestination();
        setButtonWishlist();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.tbDetailDestination);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        id_destination = intent.getStringExtra(DETAIL_DESTINATION);

        SharedPreferences sharedPreferences = getSharedPreferences("profileData", Context.MODE_PRIVATE);
        id_user = sharedPreferences.getString("idUser", null);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvName = findViewById(R.id.tvNameDetailDestination);
        tvDescription = findViewById(R.id.tvDescriptionDetailDestination);
        tvView = findViewById(R.id.tvViewDetailDestination);
        tvTicket = findViewById(R.id.tvTicketDetailDestination);
        btnAddWishlist = findViewById(R.id.btnAddToWishlist);
        ivDestination = findViewById(R.id.ivDetailDestination);

        btnAddWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWishlisted){
                    removeFromWishlist();
                }else{
                    addToWishlist();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading . . .");
    }

    private void addView() {
        baseApiService.update_view(id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void setButtonWishlist() {
        if(isWishlisted){
            btnAddWishlist.setText("Remove from wishlist");
        }else{
            btnAddWishlist.setText("Add to wishlist");
        }
    }

    private void checkStatusDestination() {
        baseApiService.select_check_wishlist(id_user, id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if(error){
                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            setButtonWishlist();
                        }else{

                            isWishlisted = jsonObject.getBoolean(Config.TAG_wishlist);
                            setButtonWishlist();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG+" checkStatusDestination", "onFailure : "+t.getMessage());
            }
        });
    }

    private void removeFromWishlist() {
        progressDialog.show();
        baseApiService.delete_wishlist(id_user, id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if(error){

                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }else{

                            isWishlisted = false;
                            setButtonWishlist();
                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG+" removeFromWishlist", "onFailure : "+t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void addToWishlist() {
        progressDialog.show();
        baseApiService = ConfigApi.getApiService();
        baseApiService.insert_wishlist(id_user, id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if(error){

                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }else{

                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            isWishlisted = true;
                            setButtonWishlist();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                Log.e(TAG+" addToWishlist", "onFailure : "+t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void getDetailWisata() {
        baseApiService.select_detail_destination(id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if(error){
                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                        }else{
                            JSONObject objectData = jsonObject.getJSONObject(Config.TAG_data);
                            name = objectData.getString(Config.TAG_name);
                            location = objectData.getString(Config.TAG_location);
                            ticket = objectData.getString(Config.TAG_ticket);
                            description = objectData.getString(Config.TAG_description);
                            photo = objectData.getString(Config.TAG_photo);
                            view = objectData.getString(Config.TAG_view);
                            setData();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.getMessage();
                Log.e(TAG+" getDetailWisata", "onFailure : "+t.getMessage());
            }
        });
    }

    private void setData() {
        tvName.setText(name);
        tvDescription.setText(description);
        tvTicket.setText(ticket);
        tvView.setText(view);
        Glide.with(this)
                .load(photo)
                .centerCrop()
                .into(ivDestination);
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
