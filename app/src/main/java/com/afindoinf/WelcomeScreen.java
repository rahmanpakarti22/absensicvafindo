package com.afindoinf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeScreen extends AppCompatActivity
{

    Boolean isRemember = false;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomescreen);

        sharedPreferences = getSharedPreferences("NAMA_PENGGUNA", MODE_PRIVATE);
        isRemember = sharedPreferences.getBoolean("CHECKBOX", false);

        new Handler().postDelayed(() ->
        {
            //apabila tidak ada sesion login maka pengguna harus melakukan login
            if (isRemember)
            {
                startActivity(new Intent(WelcomeScreen.this, DashboardScreen.class));
            }
            else
            {
                startActivity(new Intent(WelcomeScreen.this, LoginScreen.class));
            }
            finish();
        },400);
    }
}