package com.afindoinf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity
{

    Button btn_login;
    EditText username_Et, password_Et;
    CheckBox mRemember;
    Boolean isRemember = false;
    SharedPreferences sharedPreferences;
    private String username, password;
    private final String Url = "absensi/login.php";

    String IP ="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        btn_login   = findViewById(R.id.btn_login);
        username_Et = findViewById(R.id.username_Et);
        password_Et = findViewById(R.id.password_Et);

        mRemember   = findViewById(R.id.checkbox);

        IP = getString(R.string.ip);

        sharedPreferences = getSharedPreferences("NAMA_PENGGUNA", MODE_PRIVATE);
        isRemember = sharedPreferences.getBoolean("CHECKBOX", false);

        btn_login.setOnClickListener(view -> login());
    }

    final Custom_loading_Bar loading_bar = new Custom_loading_Bar(LoginScreen.this);

    private void login()
    {
        loading_bar.StartDialog();

        username = username_Et.getText().toString().trim();
        password = password_Et.getText().toString().trim();

        if(!username.equals("") && !password.equals(""))
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, IP+Url, response -> {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    final boolean status = jsonObject.getBoolean("status");
                    if(status)
                    {
                        //simpan session
                        JSONObject data = jsonObject.getJSONObject("data");
                        String UserName   = data.getString("UserName");
                        String ActualName = data.getString("ActualName");
                        String Address    = data.getString("Address");
                        String Phone      = data.getString("Phone");
                        String Email      = data.getString("Email");
                        String UserStatus = data.getString("UserStatus");
                        String LevelID    = data.getString("LevelID");
                        String IsAktif    = data.getString("IsAktif");
                        String KodeDivisi = data.getString("KodeDivisi");

                        boolean check = mRemember.isChecked();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserName", UserName);
                        editor.putString("ActualName", ActualName);
                        editor.putString("Address", Address);
                        editor.putString("Phone", Phone);
                        editor.putString("Email", Email);
                        editor.putString("UserStatus", UserStatus);
                        editor.putString("LevelID", LevelID);
                        editor.putString("IsAktif", IsAktif);
                        editor.putString("KodeDivisi", KodeDivisi);
                        editor.putBoolean("CHECKBOX", check);
                        editor.apply();

                        loading_bar.CloseDialog();
                        startActivity(new Intent(LoginScreen.this, DashboardScreen.class));
                        finish();
                    }
                    else
                    {
                        //error message
                        loading_bar.CloseDialog();
                        Toast.makeText(LoginScreen.this, "Invalid Login Id/Password", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }, error -> {
                loading_bar.CloseDialog();
                Toast.makeText(LoginScreen.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();
                    data.put("UserName", username);
                    data.put("UserPsw", password);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        else
        {
            loading_bar.CloseDialog();
            Toast.makeText(this, "Kolom tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
    }
}