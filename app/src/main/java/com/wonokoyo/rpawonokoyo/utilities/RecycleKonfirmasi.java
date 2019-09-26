package com.wonokoyo.rpawonokoyo.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wonokoyo.rpawonokoyo.R;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.model.ModelRencanaPanen;
import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;
import com.wonokoyo.rpawonokoyo.model.TimbangManager;
import com.wonokoyo.rpawonokoyo.panenactivity.TaraKeranjang;
import com.wonokoyo.rpawonokoyo.panenactivity.TimbangAyam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecycleKonfirmasi extends RecyclerView.Adapter<RecycleKonfirmasi.RecycleViewHolder> {

    private List<ModelRencanaPanen> mData;
    private Context mContext;

    SharedPrefManager spm;
    CustomDialog cd;
    TimbangManager tm;
    DatabaseHelper dbh;

    public RecycleKonfirmasi(List<ModelRencanaPanen> data, Context context) {
        this.mData = data;
        this.mContext = context;
        spm = new SharedPrefManager(context);
        tm = new TimbangManager(context);
        cd = new CustomDialog();
        dbh = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_view, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecycleViewHolder holder, int position) {
        final ModelRencanaPanen mrp = mData.get(position);

        Pattern pattern = Pattern.compile("[0-9]");
        final Matcher matcher = pattern.matcher(mrp.getRit());
        if(matcher.find()) {
            holder.cardView.setCardBackgroundColor(Color.argb(255,0,150,136));
        } else {
            holder.cardView.setCardBackgroundColor(Color.argb(255,244,81,30));
        }

        holder.txtRit.setText("Rit : " + mrp.getRit());
        holder.txtMitra.setText(mrp.getNama_mitra());
        holder.txtKandang.setText(mrp.getKandang());
        holder.txtNoDo.setText(mrp.getNo_do());
        holder.txtAlamat.setText(mrp.getAlamat());
        holder.txtDetail.setVisibility(View.INVISIBLE);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mrp.getSsid().equalsIgnoreCase(getCurrentSsid(mContext))) {
                    tampilKonfirmasi(mrp);
                } else {
                    // isi dengan alert dialog salah kandang karena ssid berbeda
                }
            }
        });
    }

    public String getCurrentSsid(Context context) {
          String ssid = "";
          ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
          NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
          if (networkInfo.isConnected()) {
              final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
              final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
              if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                  ssid = connectionInfo.getSSID();
              }
          }
          return ssid;
    }

    private void tampilKonfirmasi(final ModelRencanaPanen mrp) {
        cd.alertDialogKonfirmasi(mrp.getNo_do(), mContext, new CustomDialog.alertDialogCallBack() {
            @Override
            public void action(Boolean val, String pin) {
                if (val) {
                    if (pin.equalsIgnoreCase("123456")) {
                        spm.saveSPString(SharedPrefManager.SP_NOMOR_DO, mrp.getNo_do());
                        spm.saveSPBoolean(SharedPrefManager.SP_PANEN, true);

                        if (spm.getSpRit().equalsIgnoreCase("")) {
                            spm.saveSPString(SharedPrefManager.SP_RIT, mrp.getRit());
                        } else {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Cursor c = dbh.getListTaraByRitAndDate(spm.getSpRit(), df.format(new Date()));

                            // cek apakah tara sudah selesai atau belum
                            if (c.getCount() < 5) {
                                spm.saveSPString(SharedPrefManager.SP_SESSION, "tara");
                            } else {
                                spm.saveSPString(SharedPrefManager.SP_SESSION, "timbang");
                            }
                        }

                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        tm.saveSPString(TimbangManager.TM_MULAI, df.format(date));

                        goToLastSession();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtRit;
        TextView txtMitra;
        TextView txtKandang;
        TextView txtNoDo;
        TextView txtAlamat;
        TextView txtDetail;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.layoutCardView);
            txtRit = itemView.findViewById(R.id.txtJenisRit);
            txtMitra = itemView.findViewById(R.id.txtMitraCard);
            txtKandang = itemView.findViewById(R.id.txtKandangCard);
            txtNoDo = itemView.findViewById(R.id.txtNoDoCard);
            txtAlamat = itemView.findViewById(R.id.txtAlamatCard);
            txtDetail = itemView.findViewById(R.id.txtDetail);
        }
    }

    private void goToLastSession() {
        if (spm.getSpSession().equalsIgnoreCase("tara")) {
            Intent intent = new Intent(mContext, TaraKeranjang.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            ((Activity) mContext).finish();
        } else if (spm.getSpSession().equalsIgnoreCase("timbang")) {
            Intent intent = new Intent(mContext, TimbangAyam.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            ((Activity) mContext).finish();
        } else {
            Intent intent = new Intent(mContext, TaraKeranjang.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            ((Activity) mContext).finish();
        }
    }
}
