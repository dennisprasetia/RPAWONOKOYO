package com.wonokoyo.rpawonokoyo.panenactivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wonokoyo.rpawonokoyo.R;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.utilities.CustomDialog;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaraKeranjang extends AppCompatActivity {
    // variable socket timbangan
    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "192.168.100.10";
//    private static final String SERVER_IP = "192.168.0.3";
//    private static final String SERVER_IP = "192.168.3.212";

    // variable layout
    private Button btnLanjut;
    private Button btnRefresh;
    private TextView txtTara1;
    private TextView txtTara2;
    private TextView txtTara3;
    private TextView txtTara4;
    private TextView txtTara5;
//    private TextView txtTara6;
    private EditText etTara;
    private TextView txtBantuan;

    private TextView txtTaraAvg1;
    private TextView txtTaraAvg2;

    // variable lain
    CustomDialog cd;
    DatabaseHelper dbh;
    SharedPrefManager spm;
    int count;
    Thread threadBerat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tara_keranjang);

        cd = new CustomDialog();
        dbh = new DatabaseHelper(this);
        spm = new SharedPrefManager(this);

        txtTara1 = findViewById(R.id.txtTara1);
        txtTara2 = findViewById(R.id.txtTara2);
        txtTara3 = findViewById(R.id.txtTara3);
        txtTara4 = findViewById(R.id.txtTara4);
        txtTara5 = findViewById(R.id.txtTara5);
//        txtTara6 = findViewById(R.id.txtTara6);

        etTara = findViewById(R.id.etTara);

        btnLanjut = findViewById(R.id.btnLanjut);
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = dbh.getListTaraByDo(spm.getSpNomorDo());
                count = c.getCount();
                count++;

                if (count < 6) {
                    saveTaraKeranjang(String.valueOf(etTara.getText()));

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getTaraKeranjang(spm.getSpNomorDo());
                            etTara.setText("");
                            if (count == 5)
                                updateTaraAvg(spm.getSpNomorDo());
                        }
                    }, 300);
                } else {
                    Intent intent = new Intent(TaraKeranjang.this, TimbangAyam.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        });

        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (threadBerat.isAlive()) {
                    threadBerat.interrupt();
                }

                threadBerat = threadBeratTara();
                threadBerat.start();
            }
        });

        getTaraKeranjang(spm.getSpNomorDo());
        spm.saveSPString(SharedPrefManager.SP_SESSION, "tara");

        txtBantuan = findViewById(R.id.txtBantuanTara);
        txtBantuan.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd.alertDialogPetunjuk(R.layout.fragment_petunjuk_timbang_tara, TaraKeranjang.this,
                        new CustomDialog.alertDialogCallBack() {
                            @Override
                            public void action(Boolean val, String pin) {
                                if (val) {

                                }
                            }
                        });
            }
        });

        threadBerat = threadBeratTara();
        threadBerat.start();
    }

    @Override
    public void onBackPressed() {
        cd.alertDialogTimbangan(this, new CustomDialog.alertDialogCallBack() {
            @Override
            public void action(Boolean val, String pin) {
                if (val) {
                    Toast.makeText(TaraKeranjang.this, "Terima Kasih", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveTaraKeranjang(String tara_kg) {
        Cursor c = dbh.getListTaraByDo(spm.getSpNomorDo());
        int ke;

        if (c.getCount() > 0) {
            c.moveToLast();
            ke = c.getInt(c.getColumnIndex("ke")) + 1;
        } else {
            ke = 1;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Boolean isInserted = dbh.insertTaraKeranjang(spm.getSpNomorDo(), spm.getSpRit(), ke, tara_kg, df.format(new Date()));

        if (isInserted) {
            Toast.makeText(TaraKeranjang.this, "Berat tara tersimpan", Toast.LENGTH_SHORT);

            if (threadBerat.isAlive()) {
                threadBerat.interrupt();
            }

            threadBerat = threadBeratTara();
            threadBerat.start();

//            Intent intent = new Intent(TaraKeranjang.this, TimbangAyam.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            finish();
        }
    }

    public void getTaraKeranjang(String nomor_do) {
        Cursor c = dbh.getListTaraByDo(nomor_do);

        if (c.getCount() > 0) {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToNext();
                setTaraText(c.getString(c.getColumnIndex("ke")), c.getString(c.getColumnIndex("tara_kg")));
            }
        }
    }

    public void setTaraText(String seq, String berat) {
        switch (seq) {
            case "1" :
                txtTara1.setText(berat);
                break;
            case "2" :
                txtTara2.setText(berat);
                break;
            case "3" :
                txtTara3.setText(berat);
                break;
            case "4" :
                txtTara4.setText(berat);
                break;
            case "5" :
                txtTara5.setText(berat);
                break;
//            case "6" :
//                txtTara6.setText(berat);
//                break;
        }
    }

    public Thread threadBeratTara() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(SERVER_IP, SERVERPORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final String response = in.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response != null) {
                                String[] split = response.split(" ");
                                etTara.setText(split[split.length - 2]);
                            }
                        }
                    });

                    socket.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateTaraAvg(String nomor_do) {
        Cursor c = dbh.getListTaraByDo(nomor_do);

        Double taraSample = 0.0;
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToNext();
            taraSample = taraSample + Double.parseDouble(c.getString(c.getColumnIndex("tara_kg")));
        }

        txtTaraAvg1 = findViewById(R.id.txtTaraAvg1);
        txtTaraAvg1.setText(String.format("%.2f", (taraSample / 25)));

        txtTaraAvg2 = findViewById(R.id.txtTaraAvg2);
        txtTaraAvg2.setText(String.format("%.2f", ((taraSample / 25) * 2)));
    }
}
