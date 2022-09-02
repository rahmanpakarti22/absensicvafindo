package com.afindoinf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileScreen extends AppCompatActivity
{

    TextView UserName_Tv, ActualName_Tv, Address_Tv, Email_Tv, Phone_Tv, UserStatus_Tv, LevelID_Tv, IsAktif_Tv, KodeDivisi_Tv;
    SharedPreferences preferences;
    Button buttonLogout;
    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        UserName_Tv    = findViewById(R.id.UserName_Tv);
        ActualName_Tv  = findViewById(R.id.ActualName_Tv);
        Address_Tv     = findViewById(R.id.Address_Tv);
        Phone_Tv       = findViewById(R.id.Phone_Tv);
        Email_Tv       = findViewById(R.id.Email_Tv);
        UserStatus_Tv  = findViewById(R.id.UserStatus_Tv);
        LevelID_Tv     = findViewById(R.id.LevelID_Tv);
        IsAktif_Tv     = findViewById(R.id.IsAktif_Tv);
        KodeDivisi_Tv  = findViewById(R.id.KodeDivisi_Tv);

        btn_back       = findViewById(R.id.btn_back);

        buttonLogout   = findViewById(R.id.buttonLogout);

        preferences = getSharedPreferences("NAMA_PENGGUNA", MODE_PRIVATE);
        String username   = preferences.getString("UserName", "");
        String fullname   = preferences.getString("ActualName", "");
        String Address    = preferences.getString("Address", "");
        String phone      = preferences.getString("Phone", "");
        String email      = preferences.getString("Email", "");
        String userStatus = preferences.getString("UserStatus", "");
        String levelID    = preferences.getString("LevelID", "");
        String isaktif    = preferences.getString("IsAktif", "");
        String kodedivisi = preferences.getString("KodeDivisi", "");

        UserName_Tv.setText(username);
        ActualName_Tv.setText(fullname);
        Address_Tv.setText(Address);
        Phone_Tv.setText(phone);
        Email_Tv.setText(email);
        UserStatus_Tv.setText(userStatus);
        LevelID_Tv.setText(levelID);
        IsAktif_Tv.setText(isaktif);
        KodeDivisi_Tv.setText(kodedivisi);

        buttonLogout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();
        });

        btn_back.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileScreen.this, DashboardScreen.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(ProfileScreen.this, DashboardScreen.class);
        startActivity(intent);
        finish();
    }
}