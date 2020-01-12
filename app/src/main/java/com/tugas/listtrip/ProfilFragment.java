package com.tugas.listtrip;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ProfilFragment extends Fragment {

    TextView tvName, tvEmail, tvPhone, tvAddress;
    SharedPreferences sharedData;

    private String name, email, address, phone;
    public ProfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profil, container, false);
        tvName = v.findViewById(R.id.name);
        tvEmail = v.findViewById(R.id.email);
        tvAddress = v.findViewById(R.id.address);
        tvPhone = v.findViewById(R.id.phone);

        Toolbar toolbar = v.findViewById(R.id.tbFragmentProfil);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedData = getActivity().getSharedPreferences("profileData" , Context.MODE_PRIVATE);
        name = sharedData.getString("name", null);
        email = sharedData.getString("email", null);
        address = sharedData.getString("address", null);
        phone = sharedData.getString("phone" , null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setData(name,email,address,phone );
        setHasOptionsMenu(true);
    }


    private void setData(String name, String email, String address, String phone){
        tvName.setText(name);
        tvPhone.setText(phone);
        tvAddress.setText(address);
        tvEmail.setText(email);

    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.logout);
        alertDialogBuilder.setMessage(R.string.prompt_logout);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteSessionValues();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.show();
    }

    private void deleteSessionValues() {
        SharedPreferences.Editor editor = sharedData.edit();
        editor.putString("idUser", null);
        editor.putString("name", null);
        editor.putString("email", null);
        editor.putString("phone", null);
        editor.putString("address", null);
        editor.putString("password", null);
        editor.putBoolean("login_status", false);
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(intent);

    }

    private void intentToLocaleSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editprofil, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuEditProfil){
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);

        }else if(item.getItemId() == R.id.menuChangeLanguage){
            intentToLocaleSettings();
        }else if(item.getItemId() == R.id.menuLogout){
            showLogoutDialog();
        }
        return true;
    }
}
