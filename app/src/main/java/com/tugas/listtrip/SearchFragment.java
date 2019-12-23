package com.tugas.listtrip;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tugas.listtrip.adapter.AdapterListDestination;
import com.tugas.listtrip.api.ApiEndPoints;
import com.tugas.listtrip.api.ConfigApi;
import com.tugas.listtrip.config.Config;
import com.tugas.listtrip.model.Destination;

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


public class SearchFragment extends Fragment {

    RecyclerView rvListDestination;
    GridLayoutManager gridManager;
    ProgressBar pbLoadListDestination;
    SearchView searchView;

    List<Destination> listDestination;
    AdapterListDestination adapter;

    private String TAG = MainActivity.class.getSimpleName()+" searchFragment";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_search, container, false);

        Toolbar toolbar = v.findViewById(R.id.tbSearch);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        rvListDestination = v.findViewById(R.id.rvListDestination);
        pbLoadListDestination = v.findViewById(R.id.pbLoadListDestination);
        gridManager = new GridLayoutManager(getContext(), 2);
        adapter = new AdapterListDestination(listDestination, getContext());

        rvListDestination.setAdapter(adapter);
        rvListDestination.setLayoutManager(gridManager);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        listDestination = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getData();
    }

    private void getData() {
        showLoading(true);
        ApiEndPoints baseApiService = ConfigApi.getApiService();
        baseApiService.select_destination("auth").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.d(TAG, "response : "+response.message());

                try {

                    JSONObject jsonObject = new JSONObject(response.body().string());
                    boolean error = jsonObject.getBoolean(Config.TAG_error);

                    if(error){

                        showLoading(false);

                    }else{

                        JSONArray jsonArray = jsonObject.getJSONArray(Config.TAG_data);

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject objectData = jsonArray.getJSONObject(i);

                            Destination destination = new Destination();
                            destination.setId(objectData.getString(Config.TAG_id));
                            destination.setName(objectData.getString(Config.TAG_name));
                            destination.setPhoto(objectData.getString(Config.TAG_photo));
                            destination.setRating(objectData.getString(Config.TAG_rating));
                            destination.setDescription(objectData.getString(Config.TAG_description));
                            destination.setTicket(objectData.getString(Config.TAG_ticket));
                            destination.setView(objectData.getString(Config.TAG_view));
                            destination.setLocation(objectData.getString(Config.TAG_location));

                            listDestination.add(destination);

                        }

                        showLoading(false);
                        adapter.notifyDataSetChanged();

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e(TAG, "error "+e.getMessage());
                    Toast.makeText(getActivity(), "Gagal mengurai data", Toast.LENGTH_SHORT).show();
                    showLoading(false);
                }catch (IOException e){
                    e.printStackTrace();
                    Log.e(TAG, "error : "+e.getMessage());
                    showLoading(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
    }

    private void showLoading(boolean isLoading){
        if(isLoading){
            pbLoadListDestination.setVisibility(View.VISIBLE);
        }else{
            pbLoadListDestination.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.btnsearch, menu);

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.btnSearch).getActionView();
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.btnSearch){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
