package com.tugas.listtrip;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.tugas.listtrip.api.ApiEndPoints;
import com.tugas.listtrip.api.ConfigApi;
import com.tugas.listtrip.config.Config;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailWisataActivity extends AppCompatActivity {

    private String name, ticket, description, id_destination, photo, id_user;
    private TextView tvTicket, tvDescription, tvCountRating, tvAddRating, tvAverageRating, tvUserRating;
    private ImageView ivDestination;
    private RatingBar ratingBarDestination, ratingBarDestinationUser;
    private ProgressBar pbRatingBar1, pbRatingBar2, pbRatingBar3,pbRatingBar4, pbRatingBar5;
    private Button btnAddWishlist;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressDialog progressDialog;
    private ApiEndPoints baseApiService;
    private boolean isWishlisted = false, isRated = false;

    public static final String DETAIL_DESTINATION = "DETAIL_DESTINATION";
    private String TAG = DetailWisataActivity.class.getSimpleName();
    private String rataRating, ratingUser, jumlah_perating;
    private float rating, rating_1, rating_2, rating_3, rating_4, rating_5;

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

        tvDescription = findViewById(R.id.tvDescriptionDetailDestination);
        tvCountRating = findViewById(R.id.tvRatingCountDetailDestination);
        tvTicket = findViewById(R.id.tvTicketDetailDestination);
        tvAddRating = findViewById(R.id.tvAddRating);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvUserRating = findViewById(R.id.tvUserRating);
        btnAddWishlist = findViewById(R.id.btnAddToWishlist);
        ivDestination = findViewById(R.id.ivDetailDestination);
        ratingBarDestination = findViewById(R.id.ratingBarDestination);
        ratingBarDestinationUser = findViewById(R.id.ratingBarDestinationUser);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarDetailDestination);
        pbRatingBar1 = findViewById(R.id.ratingBar1);
        pbRatingBar2 = findViewById(R.id.ratingBar2);
        pbRatingBar3 = findViewById(R.id.ratingBar3);
        pbRatingBar4 = findViewById(R.id.ratingBar4);
        pbRatingBar5 = findViewById(R.id.ratingBar5);

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));

        ratingBarDestinationUser.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = v;
            }
        });

        tvAddRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rating == 0){
                    Toast.makeText(DetailWisataActivity.this, R.string.please_rate, Toast.LENGTH_SHORT).show();
                }else{
                    insertRating();
                }
            }
        });

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
        progressDialog.setMessage(getResources().getString(R.string.loading));
    }

    private void addView() {
        baseApiService.update_view(id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
    }

    private void setButtonWishlist() {
        if(isWishlisted){
            btnAddWishlist.setText(R.string.remove_from_wishlist);
        }else{
            btnAddWishlist.setText(R.string.add_to_wishlist);
        }
    }

    private void setTextViewRatePlace(){
        if(isRated){
            tvAddRating.setText(R.string.update_review);
        }else{
            tvAddRating.setText(R.string.add_review);
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
                        Toast.makeText(DetailWisataActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailWisataActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                Log.e(TAG+" checkStatusDestination", "onFailure : "+t.getMessage());
                Toast.makeText(DetailWisataActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDetailWisata() {
        baseApiService.select_detail_destination(id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if(error){
                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            getRatingUser();
                        }else{
                            JSONObject objectData = jsonObject.getJSONObject(Config.TAG_data);
                            name = objectData.getString(Config.TAG_name);
                            ticket = objectData.getString(Config.TAG_ticket);
                            description = objectData.getString(Config.TAG_description);
                            photo = objectData.getString(Config.TAG_photo);
                            getRatingUser();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailWisataActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailWisataActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.getMessage();
                Log.e(TAG+" getDetailWisata", "onFailure : "+t.getMessage());
                Toast.makeText(DetailWisataActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromWishlist() {
        progressDialog.show();
        baseApiService.delete_wishlist(id_user, id_destination).enqueue(new Callback<ResponseBody>() {
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

                            isWishlisted = false;
                            setButtonWishlist();
                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(DetailWisataActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(DetailWisataActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                Log.e(TAG+" removeFromWishlist", "onFailure : "+t.getMessage());
                Toast.makeText(DetailWisataActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
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
                        Log.e(TAG, "onResponse: "+e.getMessage());
                        Toast.makeText(DetailWisataActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: "+e.getMessage());
                        Toast.makeText(DetailWisataActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                Log.e(TAG+" addToWishlist", "onFailure : "+t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(DetailWisataActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertRating(){
        baseApiService.update_rating(id_user, id_destination, String.valueOf(rating)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if (error){

                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();

                        }else{

                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            ratingBarDestinationUser.setRating(rating);
                            isRated = true;
                            setTextViewRatePlace();
                            getRatingUser();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: "+e.getMessage());
                        Toast.makeText(DetailWisataActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: "+e.getMessage());
                        Toast.makeText(DetailWisataActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(DetailWisataActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRatingDestinaton(){
        baseApiService.select_rating_destination(id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()){

                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if (error){

                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            setData();

                        }else {

                            JSONObject objectData = jsonObject.getJSONObject(Config.TAG_data);
                            JSONObject objectRating = objectData.getJSONObject(Config.TAG_rating);
                            rating_1 = BigDecimal.valueOf(objectRating.getDouble(Config.TAG_rating1)).floatValue();
                            rating_2 = BigDecimal.valueOf(objectRating.getDouble(Config.TAG_rating2)).floatValue();
                            rating_3 = BigDecimal.valueOf(objectRating.getDouble(Config.TAG_rating3)).floatValue();
                            rating_4 = BigDecimal.valueOf(objectRating.getDouble(Config.TAG_rating4)).floatValue();
                            rating_5 = BigDecimal.valueOf(objectRating.getDouble(Config.TAG_rating5)).floatValue();
                            jumlah_perating = objectData.getString(Config.TAG_jumlah_perating);
                            rataRating = objectData.getString(Config.TAG_rataRating);
                            setData();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: "+e.getMessage());
                        Toast.makeText(DetailWisataActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: "+e.getMessage());
                        Toast.makeText(DetailWisataActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(DetailWisataActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRatingUser() {
        baseApiService.select_rating_user(id_user, id_destination).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if (error){
                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(DetailWisataActivity.this, message, Toast.LENGTH_SHORT).show();
                            getRatingDestinaton();

                        }else{
                            JSONObject objectData = jsonObject.getJSONObject(Config.TAG_data);
                            ratingUser = objectData.getString(Config.TAG_rating_user);
                            isRated = true;
                            setTextViewRatePlace();
                            getRatingDestinaton();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: "+e.getMessage());
                        Toast.makeText(DetailWisataActivity.this, R.string.parse_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: "+e.getMessage());
                        Toast.makeText(DetailWisataActivity.this, R.string.something_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(DetailWisataActivity.this, R.string.time_out_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        collapsingToolbarLayout.setTitle(name);

        tvDescription.setText(description);
        tvCountRating.setText(jumlah_perating);
        tvAverageRating.setText(rataRating);
        tvUserRating.setText(ratingUser);

        pbRatingBar1.setProgress(Math.round(rating_1));
        pbRatingBar2.setProgress(Math.round(rating_2));
        pbRatingBar3.setProgress(Math.round(rating_3));
        pbRatingBar4.setProgress(Math.round(rating_4));
        pbRatingBar5.setProgress(Math.round(rating_5));

        NumberFormat numberFormat = new DecimalFormat("#,###");
        int price = Integer.parseInt(ticket);
        String ticketPrice = numberFormat.format(price);
        tvTicket.setText(String.format("IDR %s / Person", ticketPrice));

        if (!TextUtils.isEmpty(rataRating) && !rataRating.equals("null")){
            ratingBarDestination.setRating(Float.parseFloat(rataRating));
        }

        if (!TextUtils.isEmpty(ratingUser) && !ratingUser.equals("null")){
            ratingBarDestinationUser.setRating(Float.parseFloat(ratingUser));
        }

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
