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

public class DetailRencanaPanen extends BottomSheetDialogFragment {

    // variable layout
    private Button btnOke;
    private TextView txtTglPanen;
    private TextView txtMitra;
    private TextView txtNoDo;
    private TextView txtRit;
    private TextView txtBerat;
    private TextView txtEkor;
    private TextView txtBerangkat;
    private TextView txtTibaFarm;
    private TextView txtMulai;
    private TextView txtSelesai;
    private TextView txtTibaRpa;
    private TextView txtPotong;

    // variable lain
    SharedPrefManager spm;
    DatabaseHelper dbh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_rencana_panen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        spm = new SharedPrefManager(getContext());
        dbh = new DatabaseHelper(getContext());

        txtTglPanen = view.findViewById(R.id.txtTglPanen);
        txtMitra = view.findViewById(R.id.txtMitraRencana);
        txtNoDo = view.findViewById(R.id.txtNoDoRencana);
        txtRit = view.findViewById(R.id.txtRitRencana);
        txtBerat = view.findViewById(R.id.txtBeratRencana);
        txtEkor = view.findViewById(R.id.txtEkorRencana);
        txtBerangkat = view.findViewById(R.id.txtRencanaBrngkt);
        txtTibaFarm = view.findViewById(R.id.txtRencanaTibaFarm);
        txtMulai = view.findViewById(R.id.txtRencanaMulai);
        txtSelesai = view.findViewById(R.id.txtRencanaSelesai);
        txtTibaRpa = view.findViewById(R.id.txtRencanaTibaRpa);
        txtPotong = view.findViewById(R.id.txtRencanaPotong);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showDetailRencana();
            }
        }, 500);

        btnOke = view.findViewById(R.id.btnOkeRencana);
        btnOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void showDetailRencana() {
        Cursor c = dbh.detailRencanaPanen(getArguments().getString("no_do"));
        c.moveToLast();

        txtMitra.setText(getArguments().getString("mitra"));
        txtNoDo.setText(getArguments().getString("no_do"));
        txtRit.setText(getArguments().getString("rit"));
        txtTglPanen.setText(c.getString(c.getColumnIndex("tgl_panen")));
        txtBerat.setText(c.getString(c.getColumnIndex("berat")));
        txtEkor.setText(String.valueOf(c.getInt(c.getColumnIndex("ekor"))));
        txtBerangkat.setText(c.getString(c.getColumnIndex("jam_brngkt")));
        txtTibaFarm.setText(c.getString(c.getColumnIndex("jam_tiba_farm")));
        txtMulai.setText(c.getString(c.getColumnIndex("jam_mulai_panen")));
        txtSelesai.setText(c.getString(c.getColumnIndex("jam_selesai_panen")));
        txtTibaRpa.setText(c.getString(c.getColumnIndex("jam_tiba_rpa")));
        txtPotong.setText(c.getString(c.getColumnIndex("jam_siap_potong")));

        /*
        Call<ResponseBody> callResponse = RetrofitInstance.menuAPI().getDetailRencana(no_do);
        callResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("content");
                        Log.e("CEK", jsonArray.toString());

                        if (jsonArray.length() > 0) {
                            JSONObject item = jsonArray.getJSONObject(0);

                            txtTglPanen.setText(item.getString("TGL_PANEN"));
                            txtBerat.setText(item.getString("BERAT"));
                            txtEkor.setText(item.getString("JUMLAH"));
                            txtBerangkat.setText(item.getString("jam_brngkt"));
                            txtTibaFarm.setText(item.getString("jam_tiba_farm"));
                            txtMulai.setText(item.getString("MULAI_PANEN"));
                            txtSelesai.setText(item.getString("SELESAI_PANEN"));
                            txtTibaRpa.setText(item.getString("jam_tiba_rpa"));
                            txtPotong.setText(item.getString("jam_potong"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Response gagal !", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Response Error !", Toast.LENGTH_LONG).show();
            }
        });
        */
    }
}
