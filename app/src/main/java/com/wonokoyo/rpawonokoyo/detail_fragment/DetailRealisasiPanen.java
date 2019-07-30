package com.wonokoyo.rpawonokoyo.detail_fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wonokoyo.rpawonokoyo.R;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;

public class DetailRealisasiPanen extends BottomSheetDialogFragment {

    // variable layout
    private TextView txtTgl;
    private TextView txtMitra;
    private TextView txtNoDo;
    private TextView txtRit;
    private TextView txtBBAvg;
    private TextView txtTotalTara;
    private TextView txtBruto;
    private TextView txtNetto;
    private TextView txtEkor;
    private TextView txtMulaiReal;
    private TextView txtSelesaiReal;
    private Button btnOke;

    // variable lain
    SharedPrefManager spm;
    DatabaseHelper dbh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_realisasi_panen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        spm = new SharedPrefManager(getContext());
        dbh = new DatabaseHelper(getContext());

        txtTgl = view.findViewById(R.id.txtTglPanenRealisasi);
        txtMitra = view.findViewById(R.id.txtMitraRealisasi);
        txtNoDo = view.findViewById(R.id.txtNoDoRealisasi);
        txtRit = view.findViewById(R.id.txtRitRealisasi);
        txtBBAvg = view.findViewById(R.id.txtBBRata2);
        txtTotalTara = view.findViewById(R.id.txtTotalTara);
        txtBruto = view.findViewById(R.id.txtBruto);
        txtNetto = view.findViewById(R.id.txtNetto);
        txtEkor = view.findViewById(R.id.txtEkorRealisasi);
        txtMulaiReal = view.findViewById(R.id.txtRealisasiMulai);
        txtSelesaiReal = view.findViewById(R.id.txtRealisasiSelesai);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showDetailRealisasi();
            }
        }, 500);

        btnOke = view.findViewById(R.id.btnOkeRealisasi);
        btnOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void showDetailRealisasi() {
        Cursor c = dbh.getHasilTimbang(getArguments().getString("no_do"));
        c.moveToLast();

        txtTgl.setText(c.getString(c.getColumnIndex("tgl_panen")));
        txtMitra.setText(c.getString(c.getColumnIndex("nama_pelanggan")));
        txtNoDo.setText(c.getString(c.getColumnIndex("no_do")));
        txtRit.setText(c.getString(c.getColumnIndex("rit")));
        txtBBAvg.setText(c.getString(c.getColumnIndex("bb_avg")));
        txtTotalTara.setText(c.getString(c.getColumnIndex("tara_total")));
        txtBruto.setText(c.getString(c.getColumnIndex("bruto")));
        txtNetto.setText(c.getString(c.getColumnIndex("netto")));
        txtEkor.setText(c.getString(c.getColumnIndex("ekor")));
        txtMulaiReal.setText(c.getString(c.getColumnIndex("jam_mulai_panen")));
        txtSelesaiReal.setText(c.getString(c.getColumnIndex("jam_selesai_panen")));
    }
}
