package com.wonokoyo.rpawonokoyo.connection;

import com.wonokoyo.rpawonokoyo.utilities.UnsafeOkHttpClient;
import com.wonokoyo.rpawonokoyo.utilities.Vars;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit = null;

    static OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Vars.BASE_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    public static UserService userAPI() {
        return RetrofitInstance.getRetrofit().create(UserService.class);
    }

    public static MenuService menuAPI() {
        return RetrofitInstance.getRetrofit().create(MenuService.class);
    }
}
