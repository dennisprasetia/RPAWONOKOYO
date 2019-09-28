package com.wonokoyo.rpawonokoyo.panenactivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wonokoyo.rpawonokoyo.MenuActivity;
import com.wonokoyo.rpawonokoyo.R;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.model.ModelRencanaPanen;
import com.wonokoyo.rpawonokoyo.utilities.CustomDialog;
import com.wonokoyo.rpawonokoyo.utilities.RecycleKonfirmasi;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class KonfirmasiPanen extends AppCompatActivity {

    // variable layout
    private RecyclerView recyclerView;
    private ProgressBar pbKonfirmasi;
    private CircleImageView imgBackKonfirmasi;
    private TextView txtBantuan;

    // variable lainnya
    SharedPrefManager spm;
    DatabaseHelper dbh;
    CustomDialog cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_konfirmasi_panen);

        spm = new SharedPrefManager(this);
        dbh = new DatabaseHelper(this);
        cd = new CustomDialog();

        imgBackKonfirmasi = findViewById(R.id.imgBackKonfirmasi);
        imgBackKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KonfirmasiPanen.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        pbKonfirmasi = findViewById(R.id.pbKonfirmasi);
        recyclerView = findViewById(R.id.recycleKonfirmasi);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setLayoutManager(new LinearLayoutManager(KonfirmasiPanen.this));
                recyclerView.setAdapter(new RecycleKonfirmasi(dataRencana(), KonfirmasiPanen.this));
                pbKonfirmasi.setVisibility(View.INVISIBLE);
            }
        },1000);

        txtBantuan = findViewById(R.id.txtBantuanMulai);
        txtBantuan.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd.alertDialogPetunjuk(R.layout.fragment_petunjuk_mulai_panen, KonfirmasiPanen.this,
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

    public List<ModelRencanaPanen> dataRencana() {
        final List<ModelRencanaPanen> data = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        String end = sdf.format(tomorrow);

        Cursor c = dbh.getRencanaPanenByFarm(start, end);

        for (int a = 0; a < c.getCount(); a++) {
            c.moveToNext();

            Pattern pattern = Pattern.compile("[0-9]");
            Matcher matcher = pattern.matcher(c.getString(c.getColumnIndex("rit")));

            if (matcher.find()) {
                ModelRencanaPanen mrp = new ModelRencanaPanen();
                mrp.setRit(c.getString(c.getColumnIndex("rit")));
                mrp.setNama_mitra(c.getString(c.getColumnIndex("nama_pelanggan")));
                mrp.setKandang(c.getString(c.getColumnIndex("kandang")));
                mrp.setSsid(c.getString(c.getColumnIndex("ssid")));
                mrp.setNo_do(c.getString(c.getColumnIndex("no_do")));
                mrp.setAlamat(c.getString(c.getColumnIndex("alamat_farm")));

                data.add(mrp);
            }
        }

        if (data.size() == 0) {
            c.moveToFirst();
            for (int a = 0; a < c.getCount(); a++) {
                Pattern pattern = Pattern.compile("[0-9]");
                Matcher matcher = pattern.matcher(c.getString(c.getColumnIndex("rit")));

                if (!matcher.find()) {
                    ModelRencanaPanen mrp = new ModelRencanaPanen();

                    String noreg = c.getString(c.getColumnIndex("noreg")).trim();
                    String kandang = noreg.substring(noreg.length()-2);

                    mrp.setRit(c.getString(c.getColumnIndex("rit")));
                    mrp.setNama_mitra(c.getString(c.getColumnIndex("nama_pelanggan")));
                    mrp.setKandang(kandang);
                    mrp.setNo_do(c.getString(c.getColumnIndex("no_do")));
                    mrp.setAlamat(c.getString(c.getColumnIndex("alamat_farm")));

                    data.add(mrp);
                }

                c.moveToNext();
            }
        }

        return data;
    }
}
