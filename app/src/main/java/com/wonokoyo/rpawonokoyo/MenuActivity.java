package com.wonokoyo.rpawonokoyo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wonokoyo.rpawonokoyo.connection.RetrofitInstance;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.panenactivity.KonfirmasiPanen;
import com.wonokoyo.rpawonokoyo.panenactivity.ScanSjPanen;
import com.wonokoyo.rpawonokoyo.utilities.CustomDialog;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    // variable layout
    private Button btnRencana;
    private Button btnMulai;
    private Button btnRealisasi;
    private Button btnSiapPotong;
    private CircleImageView imgLogout;
    private TextView txtNotif;
    private TextView txtPetunjukSopir;

    // variable lainnya
    CustomDialog cd;
    SharedPrefManager spm;
    DatabaseHelper dbh;
    ProgressDialog pd;

    private static final int REQUEST_WIFI_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set full screen dengan status bar
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestPermissions(new String[] {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_WIFI_PERMISSION);
        setContentView(R.layout.activity_menu);

        cd = new CustomDialog();
        spm = new SharedPrefManager(MenuActivity.this);
        dbh = new DatabaseHelper(this);
        pd = new ProgressDialog(this);

        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd.alertDialogLogout(MenuActivity.this, new CustomDialog.alertDialogCallBack() {
                    @Override
                    public void action(Boolean val, String pin) {
                        if (val) {
                            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            spm.clearLogin();
                        }
                    }
                });
            }
        });

        btnRencana = findViewById(R.id.btnRencana);
        btnRencana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                pd.setMessage("Sedang memproses");
                pd.show();
                pd.setCancelable(false);

                if (!dbh.skipPullRencana(df.format(date))) {
                    pullDataDo();
                } else {
                    Intent intent = new Intent(MenuActivity.this, RencanaPanen.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        });

        btnMulai = findViewById(R.id.btnMulai);
        btnMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ScanSjPanen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnRealisasi = findViewById(R.id.btnRealisasi);
        btnRealisasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RealisasiPanen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnSiapPotong = findViewById(R.id.btnReadyPotong);
        btnSiapPotong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Mohon tunggu, sedang upload data");
                pd.show();
                pd.setCancelable(false);
                saveRealisasiPanen();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // set default rit dan nomor do
                        pd.dismiss();
                    }
                },1000);
            }
        });

        txtNotif = findViewById(R.id.txtNotifikasi);
        notifJumlahRencana();
        disableButtonRealisasiAndReady();

        // text untuk menampilkan petujuk
        txtPetunjukSopir = findViewById(R.id.txtPetunjukSopir);
        txtPetunjukSopir.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtPetunjukSopir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd.alertDialogPetunjuk(R.layout.fragment_petunjuk_rencana_panen, MenuActivity.this,
                        new CustomDialog.alertDialogCallBack() {
                    @Override
                    public void action(Boolean val, String pin) {
                        if (val) {

                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        pd.dismiss();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WIFI_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Butuh akses status WiFi untuk melanjutkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void notifJumlahRencana() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        String end = sdf.format(tomorrow);

        int jumlah = dbh.notifJumlahByFarm(start, end);
        if (jumlah > 0) {
            txtNotif.setText(String.valueOf(jumlah));
            btnMulai.setEnabled(true);
            txtNotif.setVisibility(View.VISIBLE);
        } else {
            btnMulai.setEnabled(false);
        }
    }

    public void disableButtonRealisasiAndReady() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        // sementara
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date yesterday = calendar.getTime();

        int count = dbh.countRealisasiPanenNotUploaded(sdf.format(yesterday));
        if (count > 0) {
            btnRealisasi.setEnabled(true);
            btnSiapPotong.setEnabled(true);
        } else {
            btnRealisasi.setEnabled(false);
            btnSiapPotong.setEnabled(false);
        }
    }

    public void pullDataDo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        String end = sdf.format(tomorrow);

        Call<ResponseBody> callResponse = RetrofitInstance.menuAPI().getRencanaPanen(start, end);
        callResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("content");
                        if (jsonArray.length() > 0) {
                            for (int a = 0; a < jsonArray.length(); a++) {
                                JSONObject item = jsonArray.getJSONObject(a);
                                /*if (item.getString("mitra").equalsIgnoreCase("wangkal") &&
                                        item.getString("tipe").equalsIgnoreCase("do") &&
                                        dbh.cekDoRencanaExist(item.getString("no_do"))) {
                                    dbh.insertRencana(item.getString("no_do"), item.getString("no_sj"),
                                            item.getString("noreg"), item.getString("rit"), item.getString("kg"),
                                            item.getInt("ekor"), item.getString("tanggal"),
                                            item.getString("nopol"), item.getString("id_sopir"),
                                            item.getString("sopir"), item.getString("mitra"),
                                            item.getString("alamat_farm"), item.getString("jam_brngkt"),
                                            item.getString("jam_tiba_farm"), item.getString("mulai_panen"),
                                            item.getString("selesai_panen"), item.getString("jam_tiba_rpa"),
                                            item.getString("jam_siap_potong"), item.getString("nik_timpanen"),
                                            item.getString("nama_timpanen"), item.getString("wifi_ssid"),
                                            item.getString("kandang"));
                                }*/

                                if (item.getString("tipe").equalsIgnoreCase("do") &&
                                        dbh.cekDoRencanaExist(item.getString("no_do"))) {
                                    dbh.insertRencana(item.getString("no_do"), item.getString("no_sj"),
                                            item.getString("noreg"), item.getString("rit"), item.getString("kg"),
                                            item.getInt("ekor"), item.getString("tanggal"),
                                            item.getString("nopol"), item.getString("id_sopir"),
                                            item.getString("sopir"), item.getString("mitra"),
                                            item.getString("alamat_farm"), item.getString("jam_brngkt"),
                                            item.getString("jam_tiba_farm"), item.getString("mulai_panen"),
                                            item.getString("selesai_panen"), item.getString("jam_tiba_rpa"),
                                            item.getString("jam_siap_potong"), item.getString("nik_timpanen"),
                                            item.getString("nama_timpanen"), item.getString("wifi_ssid"),
                                            item.getString("kandang"));
                                }
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    Intent intent = new Intent(MenuActivity.this, RencanaPanen.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                }
                            },1000);
                        } else {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    cd.alertDialogYes("Info", "Tidak ada DO hari ini", MenuActivity.this,
                                            new CustomDialog.alertDialogCallBack() {
                                        @Override
                                        public void action(Boolean val, String pin) {

                                        }
                                    });
                                }
                            },1000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MenuActivity.this, "Respon Error !", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    Toast.makeText(MenuActivity.this, "Cek kembali koneksi anda", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }

                if (t.getMessage().contains("failed to connect")) {
                    Toast.makeText(MenuActivity.this, "Koneksi ke server gagal", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            }
        });
    }

    public void reverseRealisasiPanen() {
        Cursor c = dbh.getRealisasiPanenUploaded();

        for (int a = 0; a < c.getCount(); a++) {
            c.moveToNext();
            dbh.reverseRealisasi(c.getString(c.getColumnIndex("no_do")));
        }
    }

    public void saveRealisasiPanen() {
        final ArrayList<JSONObject> arrayJson = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        // sementara
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date yesterday = calendar.getTime();

        Cursor c = dbh.getRealisasiPanen(sdf.format(yesterday));
        final ArrayList<String> arrayNoDo = new ArrayList<>();

        if (c.getCount() > 0) {
            for (int a = 0; a < c.getCount(); a++) {
                c.moveToNext();

                Cursor d = dbh.getRencanaByDo(c.getString(c.getColumnIndex("no_do")));
                d.moveToNext();

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("no_do", c.getString(c.getColumnIndex("no_do")));
                    jsonObject.put("no_sj", d.getString(d.getColumnIndex("no_sj")));
                    jsonObject.put("rit", c.getString(c.getColumnIndex("rit")));
                    jsonObject.put("tgl_panen", c.getString(c.getColumnIndex("tgl_panen")));
                    jsonObject.put("jam_mulai_panen", c.getString(c.getColumnIndex("jam_mulai_panen")));
                    jsonObject.put("jam_selesai_panen", c.getString(c.getColumnIndex("jam_selesai_panen")));
                    jsonObject.put("bb_rata", c.getString(c.getColumnIndex("bb_avg")).replace(",", "."));
                    jsonObject.put("tara_total", c.getString(c.getColumnIndex("tara_total")).replace(",", "."));
                    jsonObject.put("tara_tandu", c.getString(c.getColumnIndex("tara_tandu")).replace(",", "."));
                    jsonObject.put("bruto", c.getString(c.getColumnIndex("bruto")).replace(",", "."));
                    jsonObject.put("netto", c.getString(c.getColumnIndex("netto")).replace(",", "."));
                    jsonObject.put("ekor", c.getString(c.getColumnIndex("ekor")));
                    jsonObject.put("tgl_realsj", date);
                    jsonObject.put("nik_timpanen", spm.getSpIdSopir());

                    arrayJson.add(jsonObject);
                    arrayNoDo.add(c.getString(c.getColumnIndex("no_do")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Call<ResponseBody> saveRealisasi = RetrofitInstance.menuAPI().saveRealisasi(arrayJson.toString());
            saveRealisasi.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Boolean inserted = jsonObject.getBoolean("content");

                            if (inserted) {
                                for (int a = 0; a < arrayNoDo.size(); a++) {
                                    Log.e("CEK", arrayNoDo.get(a));
                                    dbh.updateRealisasiUpload(arrayNoDo.get(a));
                                    saveDetailTaraKeranjang(arrayNoDo.get(a));
                                }
                                spm.saveSPString(SharedPrefManager.SP_RIT, "");
                                spm.saveSPString(SharedPrefManager.SP_NOMOR_DO, "");
                                spm.saveSPString(SharedPrefManager.SP_SESSION, "");

                                btnRealisasi.setEnabled(false);
                                btnSiapPotong.setEnabled(false);
                                Toast.makeText(MenuActivity.this, "Data realisasi berhasil disimpan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MenuActivity.this, "Data realisasi gagal simpan", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t.getMessage().equalsIgnoreCase("timeout")) {
                        Toast.makeText(MenuActivity.this, "Koneksi ke server gagal", Toast.LENGTH_LONG).show();
                    }

                    if (t.getMessage().contains("failed to connect")) {
                        Toast.makeText(MenuActivity.this, "Cek kembali koneksi anda", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void saveDetailTaraKeranjang(final String no_do) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        ArrayList<JSONObject> arrayJson = new ArrayList<>();

        String rit = dbh.getRitByDo(no_do);
        Cursor c = dbh.getListTaraByRitAndDate(rit, date);

        if (c.getCount() > 0) {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToNext();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("urut", c.getInt(c.getColumnIndex("ke")));
                    jsonObject.put("tara_kg", c.getString(c.getColumnIndex("tara_kg")).replace(",", "."));

                    arrayJson.add(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Call<ResponseBody> saveTara = RetrofitInstance.menuAPI().saveDetailTara(arrayJson.toString(), no_do);
            saveTara.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Boolean inserted = jsonObject.getBoolean("content");

                            if (inserted) {
                                Toast.makeText(MenuActivity.this, "Detail Tara Keranjang Berhasil Simpan", Toast.LENGTH_SHORT).show();
                                saveDetailTimbang(no_do);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MenuActivity.this, "Data tara gagal simpan", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t.getMessage().equalsIgnoreCase("timeout")) {
                        Toast.makeText(MenuActivity.this, "Koneksi ke server gagal", Toast.LENGTH_LONG).show();
                    }

                    if (t.getMessage().contains("failed to connect")) {
                        Toast.makeText(MenuActivity.this, "Cek kembali koneksi anda", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void saveDetailTimbang(String no_do) {
        ArrayList<JSONObject> arrayJson = new ArrayList<>();
        Cursor c = dbh.getNextTimbangKe(no_do);

        if (c.getCount() > 0) {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToNext();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("urut", c.getInt(c.getColumnIndex("urutan")));
                    jsonObject.put("kg", c.getString(c.getColumnIndex("kg")).replace(",", "."));
                    jsonObject.put("ekor", c.getInt(c.getColumnIndex("ekor")));

                    arrayJson.add(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Call<ResponseBody> saveTimbang = RetrofitInstance.menuAPI().saveDetailTimbang(arrayJson.toString(), no_do);
            saveTimbang.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Boolean inserted = jsonObject.getBoolean("content");

                            if (inserted) {
                                Toast.makeText(MenuActivity.this, "Detail Timbang Berhasil Simpan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MenuActivity.this, "Data timbang gagal simpan", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t.getMessage().equalsIgnoreCase("timeout")) {
                        Toast.makeText(MenuActivity.this, "Koneksi ke server gagal", Toast.LENGTH_LONG).show();
                    }

                    if (t.getMessage().contains("failed to connect")) {
                        Toast.makeText(MenuActivity.this, "Cek kembali koneksi anda", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
