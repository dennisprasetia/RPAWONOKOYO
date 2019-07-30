package com.wonokoyo.rpawonokoyo.model;

import android.content.Context;
import android.content.SharedPreferences;

public class TimbangManager {
    public static final String TM_RPA_APP = "tmRpaApp";
    public static final String TM_MULAI = "tmMulai";
    public static final String TM_SELESAI = "tmSelesai";
    public static final String TM_TARA = "tmTara";
    public static final String TM_BERAT_REAL = "tmBeratReal";
    public static final String TM_JUMLAH_REAL = "tmJumlahReal";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public TimbangManager(Context context) {
        sp = context.getSharedPreferences(TM_RPA_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String key, String value) {
        spEditor.putString(key, value);
        spEditor.commit();
    }

    public String getTmMulai() {
        return sp.getString(TM_MULAI, "0000-00-00 00:00:00");
    }

    public String getTmSelesai() {
        return sp.getString(TM_SELESAI, "0000-00-00 00:00:00");
    }

    public String getTmTara() {
        return sp.getString(TM_TARA, "0.0");
    }

    public String getTmJumlahReal() {
        return sp.getString(TM_JUMLAH_REAL, "0");
    }

    public String getTmBeratReal() {
        return sp.getString(TM_BERAT_REAL, "0.0");
    }

    public void clearSessionTimbang() {
        saveSPString(TM_MULAI, "0000-00-00 00:00:00");
        saveSPString(TM_SELESAI, "0000-00-00 00:00:00");
        saveSPString(TM_TARA, "0.0");
        saveSPString(TM_BERAT_REAL, "0.0");
        saveSPString(TM_JUMLAH_REAL, "0");
    }
}
