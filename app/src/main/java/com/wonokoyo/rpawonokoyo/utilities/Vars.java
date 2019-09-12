package com.wonokoyo.rpawonokoyo.utilities;

public class Vars {
    // base path untuk akses api
    //public static final String BASE_PATH = "http://192.168.203.99/api_rpa/";
//    public static final String BASE_PATH = "http://192.168.203.99/ekspedisi.dev/api/mus/";
    public static final String BASE_PATH = "http://192.168.111.17/ekspedisimus/api/mus/";

    // untuk login menggunakan fingerprint
    //public static final String API_LOGIN = "index.php?action=login";
    public static final String API_LOGIN = "login";
    //public static final String API_LOGIN_FINGERPRINT = "index.php?action=login_fingerprint";
    public static final String API_LOGIN_FINGERPRINT = "login_fingerprint";
    public static final String API_REGISTER_IMEI = "register_user";
    public static final String API_DATA_SOPIR = "get_data_sopir";

    // untuk menu activity
    //public static final String API_GET_COUNT_RENCANA = "index.php?action=get_count_rencana";
    public static final String API_GET_COUNT_RENCANA = "get_count_rencana";
    //public static final String API_GET_RENCANA_PANEN = "index.php?action=get_rencana_panen";
    public static final String API_GET_RENCANA_PANEN = "get_do_between_date";
//    public static final String API_GET_RENCANA_PANEN = "get_do_by_farm";
//    public static final String API_GET_RENCANA_PANEN = "get_do_by_date";
    //public static final String API_GET_DETAIL_RENCANA = "index.php?action=detail_rencana_panen";
    public static final String API_SAVE_REALISASI = "save_realisasi";
    public static final String API_SAVE_TARA = "save_tara";
    public static final String API_SAVE_TIMBANG = "save_timbang";
}
