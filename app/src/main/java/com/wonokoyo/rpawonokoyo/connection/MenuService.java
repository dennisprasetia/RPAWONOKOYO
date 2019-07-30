package com.wonokoyo.rpawonokoyo.connection;

import com.wonokoyo.rpawonokoyo.utilities.Vars;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MenuService {

    @GET(Vars.API_GET_COUNT_RENCANA)
    Call<ResponseBody> notifJumlah(@Query("id_sopir") String id_sopir);

    @GET(Vars.API_GET_RENCANA_PANEN)
//    Call<ResponseBody> getRencanaPanen(@Query("id_sopir") String id_sopir);
    Call<ResponseBody> getRencanaPanen(@Query("date") String date, @Query("id_sopir") String id_sopir);

    @GET(Vars.API_SAVE_REALISASI)
    Call<ResponseBody> saveRealisasi(@Query("data_realisasi") String arrayJson);

//    @GET(Vars.API_SAVE_REALISASI)
//    Call<ResponseBody> saveRealisasi(@Query("realisasi") ArrayList<JSONObject> arrayJson);

    @GET(Vars.API_SAVE_TARA)
    Call<ResponseBody> saveDetailTara(@Query("data_tara") String arrayJson, @Query("no_do") String no_do);

    @GET(Vars.API_SAVE_TIMBANG)
    Call<ResponseBody> saveDetailTimbang(@Query("data_timbang") String arrayJson, @Query("no_do") String no_do);
}
