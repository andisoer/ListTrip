package com.tugas.listtrip;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tugas.listtrip.adapter.AdapterListWishlist;
import com.tugas.listtrip.api.ApiEndPoints;
import com.tugas.listtrip.api.ConfigApi;
import com.tugas.listtrip.config.Config;
import com.tugas.listtrip.model.Wishlist;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartFragment extends Fragment {

    RecyclerView rvListWishlist;
    ProgressBar pbLoadData;
    GridLayoutManager gridManager;

    View emptyDataData;

    List<Wishlist> listWishlist;
    AdapterListWishlist adapter;

    SharedPreferences sharedData;

    private String id_user;
    private String TAG = MainActivity.class.getSimpleName()+" cartFragment";

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cart, container, false);

        Toolbar toolbar = v.findViewById(R.id.tbSearch);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        rvListWishlist = v.findViewById(R.id.rvWishlist);
        pbLoadData = v.findViewById(R.id.pbLoadListWishlist);
        emptyDataData = v.findViewById(R.id.layoutEmptyDataWishlist);

        gridManager = new GridLayoutManager(getContext(), 2);
        adapter = new AdapterListWishlist(listWishlist, getContext());

        rvListWishlist.setAdapter(adapter);
        rvListWishlist.setLayoutManager(gridManager);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedData = getActivity().getSharedPreferences("profileData", Context.MODE_PRIVATE);
        id_user = sharedData.getString("idUser", null);
        listWishlist = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getData();
    }

    private void getData() {
        showLoading(true);
        ApiEndPoints baseApiService = ConfigApi.getApiService();
        baseApiService.select_wishlist(id_user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()){

                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean error = jsonObject.getBoolean(Config.TAG_error);

                        if(error){

                            String message = jsonObject.getString(Config.TAG_message);
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            showLoading(false);
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            emptyDataData.setVisibility(View.VISIBLE);

                        }else{

                            JSONArray jsonArray = jsonObject.getJSONArray(Config.TAG_data);

                            for(int i = 0; i < jsonArray.length(); i++){

                                JSONObject objectData = jsonArray.getJSONObject(i);

                                Wishlist wishlist = new Wishlist();
                                wishlist.setId(objectData.getString(Config.TAG_id));
                                wishlist.setId_destination(objectData.getString(Config.TAG_id_destination));
                                wishlist.setName(objectData.getString(Config.TAG_name));
                                wishlist.setView(objectData.getString(Config.TAG_view));
                                wishlist.setRating(objectData.getString(Config.TAG_rating));
                                wishlist.setTicket(objectData.getString(Config.TAG_ticket));

                                listWishlist.add(wishlist);

                            }
                            showLoading(false);
                            adapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        showLoading(false);
                        e.printStackTrace();
                        emptyDataData.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        showLoading(false);
                        e.printStackTrace();
                        emptyDataData.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                Log.e(TAG+ " getData", "onFailure : "+t.getMessage());
                emptyDataData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean state){
        if(state){
            pbLoadData.setVisibility(View.VISIBLE);
        }else{
            pbLoadData.setVisibility(View.GONE);
        }
    }
}
