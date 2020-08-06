package com.wonokoyo.rpawonokoyo.panenactivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.wonokoyo.rpawonokoyo.R;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;
import com.wonokoyo.rpawonokoyo.model.TimbangManager;
import com.wonokoyo.rpawonokoyo.utilities.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanSjPanen extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private final static int REQUEST_CAMERA_CODE = 121;
    private FrameLayout frameLayout;

    SharedPrefManager spm;
    TimbangManager tm;
    DatabaseHelper dbh;

    CustomDialog cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan_sj_panen);

        spm = new SharedPrefManager(this);
        tm = new TimbangManager(this);
        dbh = new DatabaseHelper(this);

        cd = new CustomDialog();

        mScannerView = new ZXingScannerView(this);

        frameLayout = findViewById(R.id.flScanner);

        requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
    }

    @Override
    public void handleResult(Result rawResult) {
        String no_sj = rawResult.getText();

        Toast.makeText(this, no_sj, Toast.LENGTH_SHORT).show();

        Cursor c = dbh.getRencanaBySj(no_sj);
        if (c.getCount() > 0) {
            c.moveToNext();

            spm.saveSPString(SharedPrefManager.SP_NOMOR_DO, c.getString(c.getColumnIndex("no_do")));
            spm.saveSPString(SharedPrefManager.SP_RIT, c.getString(c.getColumnIndex("rit")));
            spm.saveSPBoolean(SharedPrefManager.SP_PANEN, true);

            SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
            Cursor ct = dbh.getListTaraByRitAndDate(spm.getSpRit(), dft.format(new Date()));

            // Cek tara selesai atau belum dan cek tara jika rit sama maka akan skip
            if (ct.getCount() < 5) {
                spm.saveSPString(SharedPrefManager.SP_SESSION, "tara");
            } else {
                spm.saveSPString(SharedPrefManager.SP_SESSION, "timbang");
            }

            // Catat waktu mulai panen
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tm.saveSPString(TimbangManager.TM_MULAI, df.format(date));

            goToLastSession();
        } else {
            cd.alertDialogYes("Informasi", "DO tidak diketemukan, harap lakukan sinkron Rencana Panen",
                    this, new CustomDialog.alertDialogCallBack() {
                        @Override
                        public void action(Boolean val, String pin) {
                            if (val) {
                                mScannerView.resumeCameraPreview(ScanSjPanen.this);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                frameLayout.addView(mScannerView);
            } else {
                Toast.makeText(this, "Please grant camera permission to use the Scanner", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void goToLastSession() {
        if (spm.getSpSession().equalsIgnoreCase("tara")) {
            Intent intent = new Intent(this, TaraKeranjang.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            finish();
        } else if (spm.getSpSession().equalsIgnoreCase("timbang")) {
            Intent intent = new Intent(this, TimbangAyam.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            finish();
        } else {
            Intent intent = new Intent(this, TaraKeranjang.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            finish();
        }
    }
}
