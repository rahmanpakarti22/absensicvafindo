package com.afindoinf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardScreen extends AppCompatActivity
{
    private static final String TAG = "DashboardScreen";

    Button btn_presensi;
    LinearLayout profile_Ly;
    TextView namaPegawai_Tv, username_Tv, angka1_Tv, angka2_Tv, angka3_Tv, angka4_Tv, angka5_Tv, angka6_Tv, angka7_Tv, btn_rekap;
    SharedPreferences preferences;
    RecyclerView absensi_Rv;

    String IP ="";

    private String username;
    private final String Url = "absensi/readrekapabsensi.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        btn_presensi = findViewById(R.id.btn_presensi);
        profile_Ly = findViewById(R.id.profile_Ly);
        username_Tv = findViewById(R.id.username_Tv);

        namaPegawai_Tv = findViewById(R.id.namaPegawai_Tv);
        absensi_Rv     = findViewById(R.id.absensi_Rv);

        btn_rekap      = findViewById(R.id.btn_rekap);

        angka1_Tv = findViewById(R.id.angka1_Tv);
        angka2_Tv = findViewById(R.id.angka2_Tv);
        angka3_Tv = findViewById(R.id.angka3_Tv);
        angka4_Tv = findViewById(R.id.angka4_Tv);
        angka5_Tv = findViewById(R.id.angka5_Tv);
        angka6_Tv = findViewById(R.id.angka6_Tv);
        angka7_Tv = findViewById(R.id.angka7_Tv);

        IP = getString(R.string.ip);


        preferences = getSharedPreferences("NAMA_PENGGUNA", MODE_PRIVATE);
        String fullname   = preferences.getString("ActualName", "");
        String username   = preferences.getString("UserName", "");
        namaPegawai_Tv.setText(fullname);
        username_Tv.setText(username);

        username_Tv.setVisibility(View.GONE);

        kategoriAbsen();

        btn_presensi.setOnClickListener(view ->
        {
            startActivity(new Intent(DashboardScreen.this, PresensiScreen.class));
            finish();
        });

        profile_Ly.setOnClickListener(view ->
        {
            startActivity(new Intent(DashboardScreen.this, ProfileScreen.class));
            finish();
        });

        btn_rekap.setOnClickListener(view ->
        {
            startActivity(new Intent(DashboardScreen.this, RekapAbsensiScreen.class));
            finish();
        });
    }

    private void kategoriAbsen()
    {
        username = username_Tv.getText().toString().trim();

        if(!username.equals(""))
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, IP+Url, response -> {
                Log.d(TAG, "onResponse: "+response);
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    String A = jsonObject1.getString("A");
                    String B = jsonObject1.getString("B");
                    String C = jsonObject1.getString("C");
                    String D = jsonObject1.getString("D");
                    String E = jsonObject1.getString("E");
                    String S = jsonObject1.getString("S");
                    String I = jsonObject1.getString("I");
                    angka1_Tv.setText(A);
                    angka2_Tv.setText(B);
                    angka3_Tv.setText(C);
                    angka4_Tv.setText(D);
                    angka5_Tv.setText(E);
                    angka6_Tv.setText(S);
                    angka7_Tv.setText(I);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(DashboardScreen.this, "Tidak Dapat Terhubung Ke Server", Toast.LENGTH_SHORT).show();
                //Toast.makeText(DashboardScreen.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();
                    data.put("UserName", username);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        else
        {
            Toast.makeText(this, "Gagal Terhubung Ke Server", Toast.LENGTH_SHORT).show();
        }
    }
}