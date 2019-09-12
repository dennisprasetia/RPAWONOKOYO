package com.wonokoyo.rpawonokoyo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.model.ModelRencanaPanen;
import com.wonokoyo.rpawonokoyo.utilities.CustomDialog;
import com.wonokoyo.rpawonokoyo.utilities.RecycleRencana;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RencanaPanen extends AppCompatActivity {

    // variable layout
    private CircleImageView imgBack;
    private TextView txtDetail;
    private LinearLayout linearRencana;
    private RecyclerView recyclerView;
    private TextView txtTglRencana;
    private TextView txtNopol;
    private TextView txtSopir;
    private ProgressBar progressBar;

    // variable lain
    SharedPrefManager spm;
    DatabaseHelper dbh;
    CustomDialog cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rencana_panen);

        spm = new SharedPrefManager(this);
        dbh = new DatabaseHelper(this);
        cd = new CustomDialog();

        imgBack = findViewById(R.id.imgBackRencana);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RencanaPanen.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        txtTglRencana = findViewById(R.id.txtTanggalRencana);
        txtNopol = findViewById(R.id.txtNopol);
        txtSopir = findViewById(R.id.txtSopir);

        final List<ModelRencanaPanen> dataRencana = dataRencana();

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recycleRencana);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setLayoutManager(new LinearLayoutManager(RencanaPanen.this));
                recyclerView.setAdapter(new RecycleRencana(dataRencana, getSupportFragmentManager()));

                progressBar.setVisibility(View.INVISIBLE);
            }
        },1000);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        String end = sdf.format(tomorrow);
        /*if (dbh.notifJumlah(spm.getSpIdSopir(), date) == 0) {
            cd.alertDialogYes("Info", "DO telah dilaksanakan semua", RencanaPanen.this,
                    new CustomDialog.alertDialogCallBack() {
                        @Override
                        public void action(Boolean val, String pin) {

                        }
                    });
        }*/

        if (dbh.notifJumlahByFarm(start, end) == 0) {
            cd.alertDialogYes("Info", "DO telah dilaksanakan semua", RencanaPanen.this,
                    new CustomDialog.alertDialogCallBack() {
                        @Override
                        public void action(Boolean val, String pin) {

                        }
                    });
        }
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

        boolean isSetted = false;
        for (int a = 0; a < c.getCount(); a++) {
            c.moveToNext();

            if (!isSetted) {
                txtTglRencana.setText(c.getString(c.getColumnIndex("tgl_panen")));
                txtNopol.setText(c.getString(c.getColumnIndex("nopol")));
                txtSopir.setText(c.getString(c.getColumnIndex("sopir")));
                isSetted = true;
            }

            String noreg = c.getString(c.getColumnIndex("noreg")).trim();
            String kandang = noreg.substring(noreg.length()-2);

            ModelRencanaPanen mrp = new ModelRencanaPanen();
            mrp.setRit(c.getString(c.getColumnIndex("rit")));
            mrp.setNama_mitra(c.getString(c.getColumnIndex("nama_pelanggan")));
            mrp.setKandang(kandang);
            mrp.setNo_do(c.getString(c.getColumnIndex("no_do")));
            mrp.setAlamat(c.getString(c.getColumnIndex("alamat_farm")));

            data.add(mrp);
        }

        return data;
    }
}
