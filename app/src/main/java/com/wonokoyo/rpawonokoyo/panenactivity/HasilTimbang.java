package com.wonokoyo.rpawonokoyo.panenactivity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wonokoyo.rpawonokoyo.MenuActivity;
import com.wonokoyo.rpawonokoyo.R;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;
import com.wonokoyo.rpawonokoyo.model.TimbangManager;

public class HasilTimbang extends AppCompatActivity {

    // variable layout
    private TextView txtTanggalHasil;
    private TextView txtNomorDoHasil;
    private TextView txtNopolHasil;
    private TextView txtMulaiReal;
    private TextView txtSelesaiReal;
    private TextView txtBeratRata;
    private TextView txtJumlahAkhir;
    private TextView txtBeratBruto;
    private TextView txtBeratTara;
    private TextView txtBeratNetto;
    private Button btnOkeHasil;

    // variable lain
    SharedPrefManager spm;
    TimbangManager tm;
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_hasil_timbang);

        spm = new SharedPrefManager(this);
        tm = new TimbangManager(this);
        dbh = new DatabaseHelper(this);
        tm.clearSessionTimbang();

        txtTanggalHasil = findViewById(R.id.txtTanggalHasil);
        txtNomorDoHasil = findViewById(R.id.txtNomorDoHasil);
        txtNopolHasil = findViewById(R.id.txtNopolHasil);
        txtMulaiReal = findViewById(R.id.txtMulaiReal);
        txtSelesaiReal = findViewById(R.id.txtSelesaiReal);
        txtBeratRata = findViewById(R.id.txtBeratRata);
        txtJumlahAkhir = findViewById(R.id.txtJumlahAkhir);
        txtBeratBruto = findViewById(R.id.txtBeratBruto);
        txtBeratTara = findViewById(R.id.txtBeratTara);
        txtBeratNetto = findViewById(R.id.txtBeratNetto);

        btnOkeHasil = findViewById(R.id.btnOkeHasil);
        btnOkeHasil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HasilTimbang.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewDetailPanen();
    }

    public void viewDetailPanen() {
        Cursor c = dbh.getHasilTimbang(spm.getSpNomorDo());

        if (c.getCount() > 0) {
            c.moveToLast();

            txtTanggalHasil.setText(c.getString(c.getColumnIndex("tgl_panen")));
            txtNomorDoHasil.setText(c.getString(c.getColumnIndex("no_do")));
            txtNopolHasil.setText(c.getString(c.getColumnIndex("nopol")));
            txtMulaiReal.setText(c.getString(c.getColumnIndex("jam_mulai_panen")).substring(11,16));
            txtSelesaiReal.setText(c.getString(c.getColumnIndex("jam_selesai_panen")).substring(11,16));
            txtBeratRata.setText(c.getString(c.getColumnIndex("bb_avg")));
            txtBeratTara.setText(c.getString(c.getColumnIndex("tara_total")));
            txtJumlahAkhir.setText(c.getString(c.getColumnIndex("ekor")));
            txtBeratBruto.setText(c.getString(c.getColumnIndex("bruto")));
            txtBeratNetto.setText(c.getString(c.getColumnIndex("netto")));
        }
    }
}
