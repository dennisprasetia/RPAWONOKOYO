package com.wonokoyo.rpawonokoyo.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

public class SharedPrefManager {
    public static final String SP_RPA_APP = "spRpaApp";
    public static final String SP_ID_SOPIR = "spIdSopir";
    public static final String SP_LOGIN = "spSudahLogin";
    public static final String SP_NOMOR_DO = "spNomorDo";
    public static final String SP_RIT = "spRit";
    public static final String SP_PANEN = "spSedangPanen";
    public static final String SP_SESSION = "spSesi";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context) {
//        sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp = context.getSharedPreferences(SP_RPA_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String key, String value) {
        spEditor.putString(key, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String key, Boolean value) {
        spEditor.putBoolean(key, value);
        spEditor.commit();
    }

    public String getSpIdSopir() {
        return sp.getString(SP_ID_SOPIR, "");
    }

    public Boolean getSpLogin() {
        return sp.getBoolean(SP_LOGIN, false);
    }

    public String getSpNomorDo() {
        return sp.getString(SP_NOMOR_DO, "");
    }

    public String getSpRit() {
        return sp.getString(SP_RIT, "");
    }

    public Boolean getSpPanen() {
        return sp.getBoolean(SP_PANEN, false);
    }

    public String getSpSession() {
        return sp.getString(SP_SESSION, "");
    }

    public void clearLogin() {
        saveSPString(SP_ID_SOPIR, "");
        saveSPBoolean(SP_LOGIN, false);
    }
}
