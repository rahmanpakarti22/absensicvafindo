package com.afindoinf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PresensiScreen extends AppCompatActivity implements LocationListener
{

    ImageButton btn_back;
    ImageView foto_Iv;
    TextView lokasi_anda_Tv, jarak_Tv, tanggal_absen_Tv, jam_absen_Tv, keterlambatan1_Tv, keterlambatan2_Tv, username_Tv, kodeabsen_tv, kategori_Tv;
    CardView warning_Cv, tidskterlihat;
    Button btn_hadir, ambil_foto_Ll;
    SwitchCompat keterangan_Sw;
    LinearLayout keterangan_Ly;
    CheckBox izin_Cb, sakit_Cb;
    EditText des_keterangan_Et;

    String IP ="";

    SharedPreferences preferences;
    private String KodeAbsen, UserName, Tanggal, JamMasuk, Keterlambatan, Kategori, Keterangan;
    private final String Url = "absensi/inserttrabsensi.php";

    private static final int LOCATION_REQUEST_CODE = 200;
    private String[] locationPermisiions;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presensi_screen);

        btn_back          = findViewById(R.id.btn_back);
        ambil_foto_Ll     = findViewById(R.id.ambil_foto_Ll);
        lokasi_anda_Tv    = findViewById(R.id.lokasi_anda_Tv);
        foto_Iv           = findViewById(R.id.foto_Iv);
        jarak_Tv          = findViewById(R.id.jarak_Tv);
        username_Tv       = findViewById(R.id.username_Tv);
        kodeabsen_tv      = findViewById(R.id.kodeabsen_tv);
        tanggal_absen_Tv  = findViewById(R.id.tanggal_absen_Tv);
        jam_absen_Tv      = findViewById(R.id.jam_absen_Tv);
        keterlambatan1_Tv = findViewById(R.id.keterlambatan1_Tv);
        keterlambatan2_Tv = findViewById(R.id.keterlambatan2_Tv);
        kategori_Tv       = findViewById(R.id.kategori_Tv);
        tidskterlihat     = findViewById(R.id.tidskterlihat);
        warning_Cv        = findViewById(R.id.warning_Cv);
        btn_hadir         = findViewById(R.id.btn_hadir);

        keterangan_Sw     = findViewById(R.id.keterangan_Sw);
        keterangan_Ly     = findViewById(R.id.keterangan_Ly);
        des_keterangan_Et = findViewById(R.id.des_keterangan_Et);

        izin_Cb     = findViewById(R.id.izin_Cb);
        sakit_Cb    = findViewById(R.id.sakit_Cb);

        btn_hadir.setVisibility(View.GONE);
        warning_Cv.setVisibility(View.GONE);
        tidskterlihat.setVisibility(View.GONE);
        keterlambatan2_Tv.setVisibility(View.GONE);
        keterangan_Ly.setVisibility(View.GONE);

        IP = getString(R.string.ip);

        hari();

        //get currendate
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tanggal_absen_Tv.setText(date);

        preferences = getSharedPreferences("NAMA_PENGGUNA", MODE_PRIVATE);
        String username   = preferences.getString("UserName", "");
        username_Tv.setText(username);

        //Mengola data perizinan
        locationPermisiions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        btn_back.setOnClickListener(view ->
        {
            startActivity(new Intent(PresensiScreen.this, DashboardScreen.class));
            finish();
        });

        ambil_foto_Ll.setOnClickListener(view ->
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (ContextCompat.checkSelfPermission(PresensiScreen.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(PresensiScreen.this, new String[]
                            {
                                    Manifest.permission.CAMERA
                            }, 100);
                }
                else
                {
                    openCamera();
                }
                //deteksi lokasi
                if (checkLocationPermission())
                {
                    //sudah diizinkan
                    detectLocation();
                }
                else
                {
                    //belum diizinkan, meminta untuk diizinkan
                    RequestLocationPermisiion();
                }
            }
            else
            {
                Toast.makeText(this, "Akses Ditolak", Toast.LENGTH_SHORT).show();
            }
        });

        btn_hadir.setOnClickListener(view ->
                absen());

        keterangan_Sw.setOnCheckedChangeListener((compoundButton, b) ->
        {
            if (keterangan_Sw.isChecked())
            {
                keterangan_Ly.setVisibility(View.VISIBLE);
            }
            else
            {
                keterangan_Ly.setVisibility(View.GONE);
            }
        });
    }

    private void hari()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day)
        {
            case Calendar.SUNDAY:
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
                getTime();
                keterlambatan1();
                break;
            case Calendar.SATURDAY:
                getTime();
                keterlambatan2();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void absen()
    {
        KodeAbsen = kodeabsen_tv.getText().toString().trim();
        UserName  = username_Tv.getText().toString().trim();
        Tanggal   = tanggal_absen_Tv.getText().toString().trim();
        JamMasuk  = jam_absen_Tv.getText().toString().trim();
        Keterlambatan  = keterlambatan2_Tv.getText().toString().trim();
        Kategori  = kategori_Tv.getText().toString().trim();
        Keterangan = des_keterangan_Et.getText().toString().trim();

        final Random random = new Random();

        String randomNumber1 = String.valueOf(random.nextInt(100));
        String randomNumber2 = String.valueOf(random.nextInt(100));
        String randomNumber3 = String.valueOf(random.nextInt(100));

        kodeabsen_tv.setText(randomNumber1+randomNumber2+randomNumber3+10);

        if(!KodeAbsen.equals(""))
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
                        String KodeAbsen      = data.getString("KodeAbsen");
                        String UserName       = data.getString("UserName");
                        String Tanggal        = data.getString("Tanggal");
                        String JamMasuk       = data.getString("JamMasuk");
                        String Keterlambatan  = data.getString("Keterlambatan");
                        String Kategori       = data.getString("Kategori");
                        String Keterangan     = data.getString("Keterangan");
                    }
                    else
                    {
                        //error message
                        Toast.makeText(PresensiScreen.this, "Error 606 Silahkan Coba Kembali", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(PresensiScreen.this, error.toString().trim(), Toast.LENGTH_SHORT).show())
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> data = new HashMap<>();
                    data.put("KodeAbsen", KodeAbsen);
                    data.put("UserName", UserName);
                    data.put("Tanggal", Tanggal);
                    data.put("JamMasuk", JamMasuk);
                    data.put("Keterlambatan", Keterlambatan);
                    data.put("Kategori", Kategori);
                    data.put("Keterangan", Keterangan);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        else
        {
            Toast.makeText(this, "Mencoba Terhubung Dengan Server!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTime()
    {
        Timer updateTime = new Timer();
        updateTime.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    Date waktu = Calendar.getInstance().getTime();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
                    String time = timeFormat.format(waktu);
                    jam_absen_Tv.setText(time);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        },0,1000);
    }

    private void keterlambatan1()
    {
        Timer updateTimer = new Timer();
        updateTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    Date waktu = Calendar.getInstance().getTime();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault());
                    String time2 = timeFormat.format(waktu);

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
                    Date date1 = format.parse("08:00:00 am");
                    Date date2 = format.parse(time2);
                    assert date1 != null;
                    assert date2 != null;
                    long mils  = date1.getTime() - date2.getTime();
                    int hours  = (int) (mils/(1000*60*60)%24);
                    int menit  = (int) (mils/(1000*60)) %60;
                    int mins   = (int) (mils/(1000*60));
                    long total  = hours + mins;

                    String keterlambatan = hours+" Jam "+menit+" Menit ";
                    String diftotal = total+" Menit";
                    keterlambatan1_Tv.setText(keterlambatan);
                    keterlambatan2_Tv.setText(diftotal);

                    String kategori_keterlambatan = keterlambatan2_Tv.getText().toString().trim();
                    switch (kategori_keterlambatan) {
                        case "0 Menit":
                        case "3 Menit":
                        case "4 Menit":
                        case "1 Menit":
                        case "2 Menit":
                        case "5 Menit":
                        case "6 Menit":
                        case "7 Menit":
                        case "8 Menit":
                        case "9 Menit":
                        case "10 Menit":
                        case "11 Menit":
                        case "12 Menit":
                        case "13 Menit":
                        case "14 Menit":
                        case "15 Menit":
                        case "16 Menit":
                        case "17 Menit":
                        case "18 Menit":
                        case "19 Menit":
                        case "20 Menit":
                        case "21 Menit":
                        case "22 Menit":
                        case "23 Menit":
                        case "24 Menit":
                        case "25 Menit":
                        case "26 Menit":
                        case "27 Menit":
                        case "28 Menit":
                        case "29 Menit":
                        case "30 Menit":
                        case "31 Menit":
                        case "32 Menit":
                        case "33 Menit":
                        case "34 Menit":
                        case "35 Menit":
                        case "36 Menit":
                        case "37 Menit":
                        case "38 Menit":
                        case "39 Menit":
                        case "40 Menit":
                        case "41 Menit":
                        case "42 Menit":
                        case "43 Menit":
                        case "44 Menit":
                        case "45 Menit":
                        case "-1 Menit":
                        case "-2 Menit":
                        case "-3 Menit":
                        case "-4 Menit":
                        case "-5 Menit":
                        case "-6 Menit":
                        case "-7 Menit":
                        case "-8 Menit":
                        case "-9 Menit":
                        case "-10 Menit":
                            kategori_Tv.setText("A");
                            break;
                        case "-11 Menit":
                        case "-13 Menit":
                        case "-12 Menit":
                        case "-14 Menit":
                        case "-15 Menit":
                            kategori_Tv.setText("B");
                            break;
                        case "-16 Menit":
                        case "-17 Menit":
                        case "-18 Menit":
                        case "-19 Menit":
                        case "-20 Menit":
                        case "-21 Menit":
                        case "-22 Menit":
                        case "-23 Menit":
                        case "-24 Menit":
                        case "-25 Menit":
                            kategori_Tv.setText("C");
                            break;
                        case "-30 Menit":
                            kategori_Tv.setText("D");
                            break;
                        case "-45 Menit":
                        case "-50 Menit":
                            kategori_Tv.setText("E");
                            break;
                        default:
                            izin_Cb.setOnCheckedChangeListener((compoundButton, b) ->
                            {
                                if (izin_Cb.isChecked())
                                {
                                    kategori_Tv.setText("I");
                                }
                                else
                                {
                                    kategori_Tv.setText("");
                                }
                            });
                            sakit_Cb.setOnCheckedChangeListener((compoundButton, b) -> {
                                if (sakit_Cb.isChecked())
                                {
                                    kategori_Tv.setText("S");
                                }
                                else
                                {
                                    kategori_Tv.setText("");
                                }
                            });
                            break;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        },0,1000);

    }

    private void keterlambatan2()
    {
        Timer updateTimer = new Timer();
        updateTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    Date waktu = Calendar.getInstance().getTime();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault());
                    String time2 = timeFormat.format(waktu);

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
                    Date date1 = format.parse("09:00:00 am");
                    Date date2 = format.parse(time2);
                    assert date1 != null;
                    assert date2 != null;
                    long mils  = date1.getTime() - date2.getTime();
                    int hours  = (int) (mils/(1000*60*60)%24);
                    int menit  = (int) (mils/(1000*60)) %60;
                    int mins   = (int) (mils/(1000*60));
                    long total  = hours + mins;

                    String keterlambatan = hours+" Jam "+menit+" Menit ";
                    String diftotal = total+" Menit";
                    keterlambatan1_Tv.setText(keterlambatan);
                    keterlambatan2_Tv.setText(diftotal);

                    String kategori_keterlambatan = keterlambatan2_Tv.getText().toString().trim();
                    switch (kategori_keterlambatan) {
                        case "0 Menit":
                        case "1 Menit":
                        case "2 Menit":
                        case "3 Menit":
                        case "4 Menit":
                        case "5 Menit":
                        case "6 Menit":
                        case "7 Menit":
                        case "8 Menit":
                        case "10 Menit":
                        case "9 Menit":
                        case "12 Menit":
                        case "11 Menit":
                        case "13 Menit":
                        case "14 Menit":
                        case "15 Menit":
                        case "16 Menit":
                        case "17 Menit":
                        case "18 Menit":
                        case "19 Menit":
                        case "20 Menit":
                        case "21 Menit":
                        case "22 Menit":
                        case "23 Menit":
                        case "24 Menit":
                        case "25 Menit":
                        case "26 Menit":
                        case "27 Menit":
                        case "28 Menit":
                        case "29 Menit":
                        case "30 Menit":
                        case "31 Menit":
                        case "32 Menit":
                        case "33 Menit":
                        case "34 Menit":
                        case "35 Menit":
                        case "36 Menit":
                        case "37 Menit":
                        case "38 Menit":
                        case "39 Menit":
                        case "40 Menit":
                        case "41 Menit":
                        case "42 Menit":
                        case "43 Menit":
                        case "44 Menit":
                        case "45 Menit":
                        case "-1 Menit":
                        case "-2 Menit":
                        case "-3 Menit":
                        case "-4 Menit":
                        case "-5 Menit":
                        case "-6 Menit":
                        case "-7 Menit":
                        case "-8 Menit":
                        case "-10 Menit":
                        case "-9 Menit":
                            kategori_Tv.setText("A");
                            break;
                        case "-11 Menit":
                        case "-12 Menit":
                        case "-13 Menit":
                        case "-15 Menit":
                        case "-14 Menit":
                            kategori_Tv.setText("B");
                            break;
                        case "-16 Menit":
                        case "-17 Menit":
                        case "-18 Menit":
                        case "-19 Menit":
                        case "-20 Menit":
                        case "-21 Menit":
                        case "-22 Menit":
                        case "-23 Menit":
                        case "-24 Menit":
                        case "-25 Menit":
                            kategori_Tv.setText("C");
                            break;
                        case "-30 Menit":
                            kategori_Tv.setText("D");
                            break;
                        case "-45 Menit":
                        case "-50 Menit":
                            kategori_Tv.setText("E");
                            break;
                        default:
                            izin_Cb.setOnCheckedChangeListener((compoundButton, b) ->
                            {
                                if (izin_Cb.isChecked())
                                {
                                    kategori_Tv.setText("I");
                                }
                                else
                                {
                                    kategori_Tv.setText("");
                                }
                            });
                            sakit_Cb.setOnCheckedChangeListener((compoundButton, b) ->
                            {
                                if (sakit_Cb.isChecked())
                                {
                                    kategori_Tv.setText("S");
                                }
                                else
                                {
                                    kategori_Tv.setText("");
                                }
                            });
                            break;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        },0,1000);

    }

    private void openCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100)
        {
            assert data != null;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            foto_Iv.setImageBitmap(bitmap);
        }
    }

    private void detectLocation()
    {
        Toast.makeText(this, "Mohon Tunggu Beberapa Saat", Toast.LENGTH_SHORT).show();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
    }

    private boolean checkLocationPermission()
    {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
    }

    private void RequestLocationPermisiion()
    {
        ActivityCompat.requestPermissions(this, locationPermisiions, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        //Lokasi terdeteksi
        latitude  = location.getLatitude();
        longitude = location.getLongitude();

        findAddres();
    }

    private void findAddres()
    {
        //Menemukan alamat : Negara, provinsi, kota
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try
        {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address   = addresses.get(0).getAddressLine(0); // alamat lengkap

            //Set alamat
            lokasi_anda_Tv.setText(address);
        }
        catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        getKmFromLatLong();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void getKmFromLatLong()
    {
        Location loc1 = new Location("");
        loc1.setLatitude(latitude);
        loc1.setLongitude(longitude);
        Location loc2 = new Location("");
        loc2.setLatitude(Double.parseDouble("-7.565788662174627"));
        loc2.setLongitude(Double.parseDouble("112.22194576059749"));
        double distanceInMeters = loc1.distanceTo(loc2);
        jarak_Tv.setText(String.format("%.2f", distanceInMeters / 1000) + "km");

        String jarak_anda = jarak_Tv.getText().toString().trim();

        switch (jarak_anda) {
            case "0,03km":
            case "0,04km":
            case "0,05km":
            case "0,06km":
            case "0,07km":
            case "0,08km":
            case "0,09km":
            case "0,10km":
            case "0,11km":
            case "0,12km":
            case "0,13km":
            case "0,14km":
            case "0,15km":
            case "0,16km":
            case "0,17km":
            case "0,18km":
            case "0,19km":
            case "0,20km":
            case "0,21km":
            case "0,22km":
            case "0,23km":
            case "0,24km":
            case "0,25km":
            case "0,26km":
            case "0,27km":
            case "0,28km":
            case "0,29km":
            case "0,30km":
            case "0,31km":
            case "0,32km":
            case "0,33km":
            case "0,34km":
            case "0,35km":
            case "0,36km":
            case "0,37km":
            case "0,38km":
            case "0,39km":
            case "0,40km":
            case "0,41km":
            case "0,42km":
            case "0,43km":
            case "0,44km":
            case "0,45km":
            case "0,46km":
            case "0,47km":
            case "0,48km":
            case "0,49km":
            case "0,50km":
            case "0,51km":
            case "0,52km":
            case "0,53km":
            case "0,54km":
            case "0,55km":
            case "0,56km":
            case "0,57km":
            case "0,58km":
            case "0,59km":
            case "0,60km":
            case "0,61km":
            case "0,62km":
            case "0,63km":
            case "0,64km":
            case "0,65km":
            case "0,66km":
            case "0,67km":
            case "0,68km":
            case "0,69km":
            case "0,70km":
            case "0,71km":
            case "0,72km":
            case "0,73km":
            case "0,74km":
            case "0,75km":
            case "0,76km":
            case "0,77km":
            case "0,78km":
            case "0,79km":
            case "0,80km":
            case "0,81km":
            case "0,82km":
            case "0,83km":
            case "0,84km":
            case "0,85km":
            case "0,86km":
            case "0,87km":
            case "0,88km":
            case "0,89km":
            case "0,90km":
            case "0,91km":
            case "0,92km":
            case "0,93km":
            case "0,94km":
            case "0,95km":
            case "0,96km":
            case "0,97km":
            case "0,98km":
            case "0,99km":
            case "1,00km":
                btn_hadir.setVisibility(View.VISIBLE);
                warning_Cv.setVisibility(View.GONE);
                break;
            default:
                btn_hadir.setVisibility(View.GONE);
                warning_Cv.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider)
    {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider)
    {
        //Gps lokasi dimatikan
        Toast.makeText(this, "Silahkan aktifkan lokasi", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(PresensiScreen.this, DashboardScreen.class);
        startActivity(intent);
        finish();
    }
}