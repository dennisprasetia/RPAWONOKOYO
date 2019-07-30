package com.wonokoyo.rpawonokoyo;

import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.model.ModelRencanaPanen;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;
import com.wonokoyo.rpawonokoyo.utilities.RecycleRealisasi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RealisasiPanen extends AppCompatActivity {

    // variable layout
    private CircleImageView imgBack;
    private TextView txtTglRealisasi;
    private RecyclerView recyclerRealisasi;
    private ProgressBar pbRealisasi;

    // variable lain
    SharedPrefManager spm;
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_realisasi_panen);

        spm = new SharedPrefManager(this);
        dbh = new DatabaseHelper(this);

        imgBack = findViewById(R.id.imgBackRealisasi);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtTglRealisasi = findViewById(R.id.txtTanggalRealisasi);
        pbRealisasi = findViewById(R.id.pbRealisasi);

        final List<ModelRencanaPanen> dataRealisasi = dataRealisasi();

        recyclerRealisasi = findViewById(R.id.recycleRealisasi);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerRealisasi.setLayoutManager(new LinearLayoutManager(RealisasiPanen.this));
                recyclerRealisasi.setAdapter(new RecycleRealisasi(dataRealisasi, getSupportFragmentManager()));

                pbRealisasi.setVisibility(View.INVISIBLE);
            }
        },1000);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public List<ModelRencanaPanen> dataRealisasi() {
        final List<ModelRencanaPanen> data = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        Cursor c = dbh.getRealisasiPanen(date);

        boolean isSetted = false;
        for (int a = 0; a < c.getCount(); a++) {
            c.moveToNext();

            if (!isSetted) {
                txtTglRealisasi.setText(c.getString(c.getColumnIndex("tgl_panen")));
                isSetted = true;
            }

            ModelRencanaPanen mrp = new ModelRencanaPanen();
            mrp.setRit(c.getString(c.getColumnIndex("rit")));
            mrp.setNama_mitra(c.getString(c.getColumnIndex("nama_pelanggan")));
            mrp.setNo_do(c.getString(c.getColumnIndex("no_do")));

            data.add(mrp);
        }

        return data;
    }
}
