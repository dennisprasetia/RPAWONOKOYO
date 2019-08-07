package com.wonokoyo.rpawonokoyo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wonokoyo.rpawonokoyo.connection.RetrofitInstance;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncronizeData extends AppCompatActivity {
    // variable layout
    private ProgressBar pbSinkron;

    // variable lain
    SharedPrefManager spm;
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_syncronize_data);

        pbSinkron = findViewById(R.id.pbSinkron);
        pbSinkron.setMax(100);
        pbSinkron.setIndeterminate(false);

        spm = new SharedPrefManager(this);
        dbh = new DatabaseHelper(this);

        pullDataDo();
        cleanDBTaraAndTimbang();
    }

    public void pullDataDo() {
        Call<ResponseBody> callResponse = RetrofitInstance.menuAPI().getRencanaPanen("", "", spm.getSpIdSopir());
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
                                if (dbh.cekDoRencanaExist(item.getString("no_do"))) {
                                    dbh.insertRencana(item.getString("no_do"), item.getString("no_sj"),
                                            item.getString("rit"), item.getString("kg"),
                                            item.getInt("ekor"), item.getString("tanggal"),
                                            item.getString("nopol"), item.getString("id_sopir"),
                                            item.getString("sopir"), item.getString("mitra"),
                                            item.getString("alamat_farm"), item.getString("jam_brngkt"),
                                            item.getString("jam_tiba_farm"), item.getString("mulai_panen"),
                                            item.getString("selesai_panen"), item.getString("jam_tiba_rpa"),
                                            item.getString("jam_siap_potong"), item.getString("nik_timpanen"),
                                            item.getString("nama_timpanen"));

                                    double progres = ((a+1) / jsonArray.length()) * 100;
                                    pbSinkron.setProgress((int) progres);
                                } else {
                                    pbSinkron.setProgress(100);
                                }
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (pbSinkron.getProgress() < 100)
                                        pbSinkron.setProgress(100);

                                    Intent intent = new Intent(SyncronizeData.this, MenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },1000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SyncronizeData.this, "Respon Error !", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (pbSinkron.getProgress() < 100)
                    pbSinkron.setProgress(100);

                Intent intent = new Intent(SyncronizeData.this, MenuActivity.class);
                startActivity(intent);
                finish();

                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    Toast.makeText(SyncronizeData.this, "Cek kembali koneksi anda", Toast.LENGTH_LONG).show();
                }

                if (t.getMessage().contains("failed to connect")) {
                    Toast.makeText(SyncronizeData.this, "Koneksi ke server gagal", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cleanDBTaraAndTimbang() {
        if (!spm.getSpNomorDo().equals("") && spm.getSpPanen())
            dbh.cleanTaraAndTimbang(spm.getSpNomorDo());
    }
}
