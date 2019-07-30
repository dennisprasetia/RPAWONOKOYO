package com.wonokoyo.rpawonokoyo.utilities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wonokoyo.rpawonokoyo.R;
import com.wonokoyo.rpawonokoyo.detail_fragment.DetailRencanaPanen;
import com.wonokoyo.rpawonokoyo.model.ModelRencanaPanen;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecycleRencana extends RecyclerView.Adapter<RecycleRencana.RecycleViewHolder> {

    private List<ModelRencanaPanen> mData;
    private FragmentManager mFragmentManager;

    public RecycleRencana(List<ModelRencanaPanen> data, FragmentManager fragmentManager) {
        this.mData = data;
        this.mFragmentManager = fragmentManager;
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
        Matcher matcher = pattern.matcher(mrp.getRit());
        if(matcher.find()) {
            holder.cardView.setCardBackgroundColor(Color.argb(255,0,150,136));
        } else {
            holder.cardView.setCardBackgroundColor(Color.argb(255,244,81,30));
        }

        holder.txtRit.setText("Rit : " + mrp.getRit());
        holder.txtMitra.setText(mrp.getNama_mitra());
        holder.txtNoDo.setText(mrp.getNo_do());
        holder.txtAlamat.setText(mrp.getAlamat());
        holder.txtDetail.setVisibility(View.INVISIBLE);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("no_do", mrp.getNo_do());
                bundle.putString("rit", mrp.getRit());
                bundle.putString("mitra", mrp.getNama_mitra());

                DetailRencanaPanen detailRencanaPanen = new DetailRencanaPanen();
                detailRencanaPanen.setCancelable(false);
                detailRencanaPanen.setArguments(bundle);
                detailRencanaPanen.show(mFragmentManager, "Detail Rencana Panen");
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
        TextView txtNoDo;
        TextView txtAlamat;
        TextView txtDetail;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.layoutCardView);
            txtRit = itemView.findViewById(R.id.txtJenisRit);
            txtMitra = itemView.findViewById(R.id.txtMitraCard);
            txtNoDo = itemView.findViewById(R.id.txtNoDoCard);
            txtAlamat = itemView.findViewById(R.id.txtAlamatCard);

            txtDetail = itemView.findViewById(R.id.txtDetail);
        }
    }
}
