package com.tugas.listtrip;

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

    View viewEmptyData, viewNetworkError;

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
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.destination);

        rvListDestination = v.findViewById(R.id.rvListDestination);
        pbLoadListDestination = v.findViewById(R.id.pbLoadListDestination);
        viewEmptyData = v.findViewById(R.id.layoutEmptyDataSearch);
        viewNetworkError = v.findViewById(R.id.layoutNetworkErroWishlist);
        gridManager = new GridLayoutManager(getContext(), 2);
        adapter = new AdapterListDestination(listDestination, getContext());

        rvListDestination.setAdapter(adapter);
        rvListDestination.setLayoutManager(gridManager);

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                        String message = jsonObject.getString(Config.TAG_message);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        viewEmptyData.setVisibility(View.VISIBLE);
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
                    Toast.makeText(getContext(), R.string.parse_error, Toast.LENGTH_SHORT).show();
                    viewEmptyData.setVisibility(View.VISIBLE);
                    showLoading(false);
                }catch (IOException e){
                    e.printStackTrace();
                    Log.e(TAG, "error : "+e.getMessage());
                    Toast.makeText(getContext(), R.string.something_error, Toast.LENGTH_SHORT).show();
                    viewEmptyData.setVisibility(View.VISIBLE);
                    showLoading(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), R.string.time_out_error, Toast.LENGTH_SHORT).show();
                viewNetworkError.setVisibility(View.VISIBLE);
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
        MenuItem searchItem = menu.findItem(R.id.item_menu_search);
        searchItem.setIcon(R.drawable.ic_action_search);
        SearchView searchView = new SearchView(getContext());
        searchView.setQueryHint(getResources().getString(R.string.search_destination));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                List<Destination> listDestinationFiltered = new ArrayList<>();
                for (Destination data : listDestination){
                    String name = data.getName().toLowerCase();
                    if (name.contains(newText)){
                        listDestinationFiltered.add(data);
                    }
                }
                adapter.setFilter(listDestinationFiltered);
                return true;
            }
        });
        searchItem.setActionView(searchView);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.item_menu_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
