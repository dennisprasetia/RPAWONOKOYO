package com.wonokoyo.rpawonokoyo.panenactivity;

import android.app.ProgressDialog;
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
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;
import com.wonokoyo.rpawonokoyo.model.TimbangManager;
import com.wonokoyo.rpawonokoyo.utilities.CustomDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimbangAyam extends AppCompatActivity {
    // variable socket timbangan
    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "192.168.0.3";
    Thread threadBerat;

    // variable layout
    private TextView txtSisaEkor;
    private TextView txtSisaBerat;
    private Button btnNext;
    private Button btnSelesai;
    private EditText etKe;
    private EditText etBerat;
    private EditText etEkor;
    private TextView txtBantuan;
    private Button btnRefresh;

    private TextView txtTaraAvg1;
    private TextView txtTaraAvg2;

    // variable lain
    SharedPrefManager spm;
    DatabaseHelper dbh;
    TimbangManager tm;
    ProgressDialog pd;
    CustomDialog cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_timbang_ayam);

        spm = new SharedPrefManager(this);
        dbh = new DatabaseHelper(this);
        tm = new TimbangManager(this);
        pd = new ProgressDialog(this);
        pd.setMessage("Mohon Tunggu");

        cd = new CustomDialog();

        txtSisaEkor = findViewById(R.id.txtSisaEkor);
        txtSisaBerat = findViewById(R.id.txtSisaBerat);
        etKe = findViewById(R.id.etKe);
        etBerat = findViewById(R.id.etBerat);
        etEkor = findViewById(R.id.etEkor);
        setEkorBeratRencana();
        setTaraAwal();

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput(etBerat.getText().toString(), etEkor.getText().toString())) {
                    int urutan = Integer.parseInt(etKe.getText().toString());
                    String kg = etBerat.getText().toString();
                    int jumlah = Integer.parseInt(etEkor.getText().toString());
                    saveTimbangAyam(urutan, kg, jumlah);

                    if (threadBerat.isAlive()) {
                        threadBerat.interrupt();
                    }

                    threadBerat = threadBeratTimbang();
                    threadBerat.start();
                }
            }
        });

        btnSelesai = findViewById(R.id.btnSelesai);
        btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd.alertDialog("Informasi", "Apakah anda yakin Selesaikan penimbangan dan Simpan penimbangan ?",
                        TimbangAyam.this, new CustomDialog.alertDialogCallBack() {
                            @Override
                            public void action(Boolean val, String pin) {
                                if (val) {
                                    if (validataSelesai(etBerat.getText().toString(), etEkor.getText().toString())) {
                                        int urutan = Integer.parseInt(etKe.getText().toString());
                                        String kg = etBerat.getText().toString();
                                        int jumlah = Integer.parseInt(etEkor.getText().toString());
                                        saveTimbangAyam(urutan, kg, jumlah);
                                    }

                                    pd.show();

                                    Date date = Calendar.getInstance().getTime();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    tm.saveSPString(TimbangManager.TM_SELESAI, df.format(date));

                                    saveHasilTimbang();
                                    spm.saveSPString(SharedPrefManager.SP_SESSION, "");
                                    spm.saveSPBoolean(SharedPrefManager.SP_PANEN, false);
                                }
                            }
                        });
            }
        });

        setTimbangKe();
        spm.saveSPString(SharedPrefManager.SP_SESSION, "timbang");

        txtBantuan = findViewById(R.id.txtBantuanAyam);
        txtBantuan.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd.alertDialogPetunjuk(R.layout.fragment_petunjuk_timbang_ayam, TimbangAyam.this,
                        new CustomDialog.alertDialogCallBack() {
                            @Override
                            public void action(Boolean val, String pin) {
                                if (val) {

                                }
                            }
                        });
            }
        });

        threadBerat = threadBeratTimbang();
        threadBerat.start();

        btnRefresh = findViewById(R.id.btnRefreshTimbang);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (threadBerat.isAlive()) {
                    threadBerat.interrupt();
                }

                threadBerat = threadBeratTimbang();
                threadBerat.start();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    public void setTaraAwal() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Cursor tara = dbh.getListTaraByRitAndDate(spm.getSpRit(), df.format(date));

        Double taraSample = 0.0;
        for (int a = 0; a < tara.getCount(); a++) {
            tara.moveToNext();
            taraSample = taraSample + Double.parseDouble(tara.getString(tara.getColumnIndex("tara_kg")));
        }

        Double tara_awal = (taraSample / 25) * 2;
        tm.saveSPString(TimbangManager.TM_TARA, String.format("%.2f", tara_awal));

        txtTaraAvg1 = findViewById(R.id.txtTaraDiTimbang1);
        txtTaraAvg1.setText(String.format("%.2f", (taraSample / 25)));

        txtTaraAvg2 = findViewById(R.id.txtTaraDiTimbang2);
        txtTaraAvg2.setText(tm.getTmTara());
    }

    public void setEkorBeratRencana() {
        Cursor c = dbh.getEkorBeratRencana(spm.getSpNomorDo());
        c.moveToLast();

        int ekorsisa = Integer.parseInt(c.getString(c.getColumnIndex("ekor"))) -
                Integer.parseInt(tm.getTmJumlahReal());
        Double beratsisa = Double.valueOf(c.getDouble(c.getColumnIndex("berat"))) -
                Double.valueOf(tm.getTmBeratReal());

        txtSisaEkor.setText(String.valueOf(ekorsisa));
        txtSisaBerat.setText(String.format("%.2f", beratsisa));
    }

    public Boolean validateInput(String berat, String jumlah) {
        if (berat.trim().length() == 0) {
            Toast.makeText(TimbangAyam.this, "Data berat kosong", Toast.LENGTH_LONG).show();
            return false;
        }

        if (jumlah.trim().length() == 0) {
            Toast.makeText(TimbangAyam.this, "Data ekor kosong", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public Boolean validataSelesai(String berat, String jumlah) {
        if (berat.trim().length() == 0) {
            return false;
        }

        if (jumlah.trim().length() == 0) {
            return false;
        }

        return true;
    }

    public void setTimbangKe() {
        Cursor c = dbh.getNextTimbangKe(spm.getSpNomorDo());
        if (c.getCount() > 0) {
            c.moveToLast();
            int ke = c.getInt(c.getColumnIndex("urutan")) + 1;
            etKe.setText(String.valueOf(ke));
        } else {
            int ke = 1;
            etKe.setText(String.valueOf(ke));
        }
    }

    //save berat dan ekor timbangan ke
    public void saveTimbangAyam(int ke, String kg, int jumlah) {
        Boolean isInserted = dbh.insertTimbangAyam(spm.getSpNomorDo(), ke, String.format("%.2f", Double.valueOf(kg)), jumlah);
        if (isInserted) {
            Toast.makeText(TimbangAyam.this, "Penimbangan berhasil", Toast.LENGTH_SHORT);
            Double netto_timbang = Double.valueOf(kg) - Double.valueOf(tm.getTmTara());
            setSisa(jumlah, String.format("%.2f", netto_timbang));
            setTimbangKe();

//            if (ke % 10 == 0) {
//                Intent intent = new Intent(TimbangAyam.this, TaraKeranjang.class);
//                startActivity(intent);
//            } else {
//                setTimbangKe();
//            }
        }
    }

    public void setSisa(int jumlah, String berat) {
        int ekorsisa = Integer.parseInt(txtSisaEkor.getText().toString()) - jumlah;
        Double beratsisa = Double.valueOf(txtSisaBerat.getText().toString()) - Double.valueOf(berat);
        Double beratreal = Double.valueOf(tm.getTmBeratReal()) + Double.valueOf(berat) + Double.valueOf(tm.getTmTara());

        tm.saveSPString(TimbangManager.TM_BERAT_REAL, String.valueOf(beratreal));
        tm.saveSPString(TimbangManager.TM_JUMLAH_REAL, String.valueOf(Integer.parseInt(tm.getTmJumlahReal()) + jumlah));

        txtSisaEkor.setText(String.valueOf(ekorsisa));
        txtSisaBerat.setText(String.format("%.2f",beratsisa));

        etBerat.setText("");
        etEkor.setText("");
//        etBerat.requestFocus();
        etEkor.requestFocus();
    }

    public void saveHasilTimbang() {
//        Date date = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        Cursor tara = dbh.getListTaraByRitAndDate(spm.getSpRit(), df.format(date));
        int jumlah_timbang;

        // ambil perhitungan timbang ke
        if (validataSelesai(etBerat.getText().toString(), etEkor.getText().toString())) {
            jumlah_timbang = Integer.valueOf(etKe.getText().toString());
        } else {
            jumlah_timbang = Integer.valueOf(etKe.getText().toString()) - 1;
        }

//        Double taraSample = 0.0;
//        for (int a = 0; a < tara.getCount(); a++) {
//            tara.moveToNext();
//            taraSample = taraSample + Double.parseDouble(tara.getString(tara.getColumnIndex("tara_kg")));
//        }

        // hitung total tara, netto, dan berat rata-rata
        Double total_tara = Double.valueOf(tm.getTmTara()) * jumlah_timbang;
        Double netto = Double.valueOf(tm.getTmBeratReal()) - Double.valueOf(total_tara);
        Double bb_avg = netto / Double.valueOf(tm.getTmJumlahReal());

        Cursor c = dbh.detailRencanaPanen(spm.getSpNomorDo());
        c.moveToLast();

        Boolean isInserted = dbh.insertRealisasi(spm.getSpNomorDo(), c.getString(c.getColumnIndex("nama_pelanggan")),
                c.getString(c.getColumnIndex("rit")), c.getString(c.getColumnIndex("nopol")),
                c.getString(c.getColumnIndex("tgl_panen")), tm.getTmMulai(), tm.getTmSelesai(),
                String.format("%.2f", total_tara), String.format("%.2f", bb_avg),
                String.format("%.2f", Double.valueOf(tm.getTmBeratReal())),
                String.format("%.2f", netto), Integer.parseInt(tm.getTmJumlahReal()));

        if (isInserted) {
            if (pd.isShowing()) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Intent intent = new Intent(TimbangAyam.this, HasilTimbang.class);
                        startActivity(intent);
                        finish();
                    }
                },1000);
            }
        }
    }

    public Thread threadBeratTimbang() {
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
                                etBerat.setText(split[split.length - 2]);
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
}
