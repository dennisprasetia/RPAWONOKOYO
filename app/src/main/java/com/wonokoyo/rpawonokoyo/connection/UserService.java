package com.wonokoyo.rpawonokoyo.connection;

import com.wonokoyo.rpawonokoyo.utilities.Vars;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET(Vars.API_LOGIN)
    Call<ResponseBody> login(@Query("username") String username, @Query("password") String password,
                             @Query("device_id") String imei);

    @GET(Vars.API_REGISTER_IMEI)
    Call<ResponseBody> registerImei(@Query("id_sopir") String id_sopir,@Query("nama") String nama,
                                    @Query("username") String username, @Query("password") String password,
                                    @Query("device_id") String imei, @Query("nomor_telp") String nomor_telp);

    @GET(Vars.API_LOGIN_FINGERPRINT)
    Call<ResponseBody> loginFingerprint(@Query("device_id") String imei);

    @GET(Vars.API_DATA_SOPIR)
    Call<ResponseBody> getDataSopir();

    @GET(Vars.API_DATA_TIMPANEN)
    Call<ResponseBody> getDataTimpanen();
}
