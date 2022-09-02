package com.afindoinf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RekapAbsensiScreen extends AppCompatActivity
{
    private static final String TAG = "RekapAbsensiScreen";

    String IP ="";
    SharedPreferences preferences;
    RecyclerView absensi_Rv;
    TextView username_Tv;

    ImageButton btn_back;

    private String username;
    private final String Url = "absensi/readdataabsensi.php";

    private ArrayList<ModelAbsen> absenList;
    private AdapterAbsensi adapterAbsensi;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_absensi_screen);

        username_Tv = findViewById(R.id.username_Tv);
        absensi_Rv     = findViewById(R.id.absensi_Rv);

        IP = getString(R.string.ip);

        btn_back = findViewById(R.id.btn_back);

        preferences = getSharedPreferences("NAMA_PENGGUNA", MODE_PRIVATE);
        String username   = preferences.getString("UserName", "");
        username_Tv.setText(username);
        username_Tv.setVisibility(View.GONE);

        loadData();

        btn_back.setOnClickListener(view ->
        {
            startActivity(new Intent(RekapAbsensiScreen.this, DashboardScreen.class));
            finish();
        });
    }

    private void loadData()
    {
        username = username_Tv.getText().toString().trim();

        absenList = new ArrayList<>();

        if(!username.equals(""))
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, IP+Url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    Log.d(TAG, "onResponse: "+response);
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i=0; i<jsonArray.length(); i++)
                        {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String KodeAbsen     = jsonObject1.getString("KodeAbsen");
                            String UserName      = jsonObject1.getString("UserName");
                            String Tanggal       = jsonObject1.getString("Tanggal");
                            String JamMasuk      = jsonObject1.getString("JamMasuk");
                            String Keterlambatan = jsonObject1.getString("Keterlambatan");
                            String Kategori      = jsonObject1.getString("Kategori");

                            ModelAbsen modelAbsen = new ModelAbsen(KodeAbsen, UserName, Tanggal, JamMasuk, Keterlambatan, Kategori);
                            absenList.add(modelAbsen);
                        }
                        adapterAbsensi = new AdapterAbsensi(RekapAbsensiScreen.this, absenList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RekapAbsensiScreen.this);
                        absensi_Rv.setLayoutManager(linearLayoutManager);
                        absensi_Rv.setHasFixedSize(true);
                        absensi_Rv.setAdapter(adapterAbsensi);
                        adapterAbsensi.notifyDataSetChanged();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(RekapAbsensiScreen.this, "Tidak Dapat Terhubung Ke Server", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(DashboardScreen.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
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