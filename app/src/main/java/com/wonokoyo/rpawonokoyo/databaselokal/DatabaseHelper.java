package com.wonokoyo.rpawonokoyo.databaselokal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.wonokoyo.rpawonokoyo.model.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "panen.db";
    public static final String TABLE_TARA = "tara";
    public static final String TABLE_TIMBANG = "timbang";
    public static final String TABLE_RENCANA = "recana";
    public static final String TABLE_REALISASI = "realisasi";
    public static final String TABLE_PIN = "pinpanen";
    public static final String TABLE_USER = "user";
    public static final String TABLE_SOPIR = "m_sopir";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tara = "create table " + TABLE_TARA + "(no_do text, rit text, ke integer, tara_kg text, tgl_tara text)";
        db.execSQL(tara);

        String timbang = "create table " + TABLE_TIMBANG + "(no_do text, urutan integer, kg text, ekor integer)";
        db.execSQL(timbang);

        String rencana = "create table " + TABLE_RENCANA + "(no_do text, no_sj text, rit text, " +
                "berat decimal, ekor int, tgl_panen text, nopol text, id_sopir text, sopir text, nama_pelanggan text, " +
                "alamat_farm text, jam_brngkt text, jam_tiba_farm text, jam_mulai_panen text, " +
                "jam_selesai_panen text, jam_tiba_rpa text, jam_siap_potong text, nik_timpanen text, " +
                "nama_timpanen text)";
        db.execSQL(rencana);

        String realisasi = "create table " + TABLE_REALISASI + "(no_do text, nama_pelanggan text, " +
                "rit text, nopol text, tgl_panen text, jam_mulai_panen text, jam_selesai_panen text, " +
                "tara_total text, bb_avg numeric, bruto numeric, netto numeric, ekor integer, status int)";
        db.execSQL(realisasi);

        String user = "create table " + TABLE_USER + "(id_sopir text, sopir text, username text, device_id text)";
        db.execSQL(user);

        String sopir = "create table " + TABLE_SOPIR + "(id_sopir text, nama text)";
        db.execSQL(sopir);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String tara = "drop table if exists " + TABLE_TARA;
        db.execSQL(tara);

        String timbang = "drop table if exists " + TABLE_TIMBANG;
        db.execSQL(timbang);

        String rencana = "drop table if exists " + TABLE_RENCANA;
        db.execSQL(rencana);

        String realisasi = "drop table if exists " + TABLE_REALISASI;
        db.execSQL(realisasi);

        String user = "drop table if exists " + TABLE_USER;
        db.execSQL(user);

        onCreate(db);
    }

    public void clearDB() {
        SQLiteDatabase db = this.getWritableDatabase();

        String tara = "drop table if exists " + TABLE_TARA;
        db.execSQL(tara);

        String timbang = "drop table if exists " + TABLE_TIMBANG;
        db.execSQL(timbang);

        String rencana = "drop table if exists " + TABLE_RENCANA;
        db.execSQL(rencana);

        String realisasi = "drop table if exists " + TABLE_REALISASI;
        db.execSQL(realisasi);

        String user = "drop table if exists " + TABLE_USER;
        db.execSQL(user);

        onCreate(db);
    }

    public boolean insertTaraKeranjang(String no_do, String rit, Integer ke, String tara_kg, String tgl_tara) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("no_do", no_do);
        cv.put("rit", rit);
        cv.put("ke", ke);
        cv.put("tara_kg", tara_kg);
        cv.put("tgl_tara", tgl_tara);
        long result = db.insert(TABLE_TARA, null, cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getListTaraByDo(String no_do) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_TARA + " WHERE no_do = '" + no_do + "'";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public Cursor getListTaraByRitAndDate(String rit, String tgl) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_TARA + " WHERE rit = '" + rit + "' AND tgl_tara = '" + tgl + "'";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public String getTanggalTaraByRit(String rit) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_TARA + " WHERE rit = '" + rit + "'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToNext();

        return c.getString(c.getColumnIndex("tgl_tara"));
    }

    public int getCountTaraByTanggal(String tgl) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_TARA + " WHERE tgl_tara = '" + tgl + "'";
        Cursor c = db.rawQuery(sql, null);

        return c.getCount();
    }

    public boolean insertTimbangAyam(String no_do, Integer urutan, String kg, Integer jumlah) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("no_do", no_do);
        cv.put("urutan", urutan);
        cv.put("kg", kg);
        cv.put("ekor", jumlah);
        long result = db.insert(TABLE_TIMBANG, null, cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public int getLastTimbang(String no_do) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_RENCANA + " WHERE no_do = '" + no_do + "'";
        Cursor c = db.rawQuery(sql, null);

        return c.getCount();
    }

    public boolean insertRencana(String no_do, String no_sj, String rit, String berat, int ekor, String tgl_panen,
                                 String nopol, String id_sopir, String sopir, String nama_pelanggan , String alamat_farm,
                                 String jam_brngkt, String jam_tiba_farm, String jam_mulai_panen, String jam_selesai_panen,
                                 String jam_tiba_rpa, String jam_siap_potong, String nik_timpanen,
                                 String nama_timpanen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("no_do", no_do);
        cv.put("no_sj", no_sj);
        cv.put("rit", rit);
        cv.put("berat", berat);
        cv.put("ekor", ekor);
        cv.put("tgl_panen", tgl_panen);
        cv.put("nopol", nopol);
        cv.put("id_sopir", id_sopir);
        cv.put("sopir", sopir);
        cv.put("nama_pelanggan", nama_pelanggan);
        cv.put("alamat_farm", alamat_farm);
        cv.put("jam_brngkt", jam_brngkt);
        cv.put("jam_tiba_farm", jam_tiba_farm);
        cv.put("jam_mulai_panen", jam_mulai_panen);
        cv.put("jam_selesai_panen", jam_selesai_panen);
        cv.put("jam_tiba_rpa", jam_tiba_rpa);
        cv.put("jam_siap_potong", jam_siap_potong);
        cv.put("nik_timpanen", nik_timpanen);
        cv.put("nama_timpanen", nama_timpanen);
        long result = db.insert(TABLE_RENCANA, null, cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean cekDoRencanaExist(String no_do) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_RENCANA + " WHERE no_do = '" + no_do + "'";
        Cursor c = db.rawQuery(sql, null);

        c.getCount();

        if (c.getCount() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean skipPullRencana(String tgl_panen) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_RENCANA + " WHERE tgl_panen = '" + tgl_panen + "' AND " +
                "no_do NOT IN (SELECT no_do FROM " + TABLE_REALISASI + ")";
        Cursor c = db.rawQuery(sql, null);

        c.getCount();

        if (c.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int notifJumlah(String id_sopir, String tgl_panen) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_RENCANA + " WHERE id_sopir = '" + id_sopir + "' AND tgl_panen = '" + tgl_panen + "'" +
                "AND no_do NOT IN (SELECT no_do FROM " + TABLE_REALISASI + ")";
        Cursor c = db.rawQuery(sql, null);

        return c.getCount();
    }

    public Cursor getRencanaPanen(String id_sopir, String tgl_panen) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_RENCANA + " WHERE id_sopir = '" + id_sopir + "' AND tgl_panen = '" + tgl_panen + "'" +
                "AND no_do NOT IN (SELECT no_do FROM " + TABLE_REALISASI + ")";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public Cursor getEkorBeratRencana(String no_do) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT ekor, berat FROM " + TABLE_RENCANA + " WHERE no_do = '" + no_do + "'";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public Cursor detailRencanaPanen(String no_do) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_RENCANA + " WHERE no_do = '" + no_do + "'";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public boolean insertRealisasi(String no_do, String pelanggan, String rit, String nopol, String tanggal,
                                   String mulai, String selesai, String tara_total, String bb_avg, String bruto,
                                   String netto, Integer jumlah) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nama_pelanggan", pelanggan);
        cv.put("no_do", no_do);
        cv.put("rit", rit);
        cv.put("nopol", nopol);
        cv.put("tgl_panen", tanggal);
        cv.put("jam_mulai_panen", mulai);
        cv.put("jam_selesai_panen", selesai);
        cv.put("tara_total", tara_total);
        cv.put("bb_avg", bb_avg);
        cv.put("bruto", bruto);
        cv.put("netto", netto);
        cv.put("ekor", jumlah);
        cv.put("status", 0);
        long result = db.insert(TABLE_REALISASI, null, cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getNextTimbangKe(String nomor_do) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_TIMBANG + " WHERE no_do = '" + nomor_do + "'";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public Cursor getHasilTimbang(String no_do) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_REALISASI + " WHERE no_do = '" + no_do + "'";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public Cursor getRealisasiPanen(String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_REALISASI + " WHERE tgl_panen = '" + date + "' AND status = 0";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    public int countRealisasiPanenNotUploaded(String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_REALISASI + " WHERE tgl_panen = '" + date + "' AND status = 0";
        Cursor c = db.rawQuery(sql, null);

        return c.getCount();
    }

    public void updateRealisasiUpload(String no_do) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("status", 1);
        db.update(TABLE_REALISASI, cv, "no_do = ?", new String[]{no_do});
    }

    public String getRitByDo(String no_do) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_RENCANA + " WHERE no_do = '" + no_do + "'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        return c.getString(c.getColumnIndex("rit"));
    }

    public void reverseRealisasi(String no_do) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("status", 0);
        db.update(TABLE_REALISASI, cv, "no_do = ?", new String[]{no_do});
    }

    public Cursor getRealisasiPanenUploaded() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_REALISASI + " WHERE tgl_panen = '" + date + "' AND status = 1";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    // masih belum bisa dijalankan
    public void deleteRealisasi(String no_do) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REALISASI, "no_do", new String[]{no_do});
    }

    public boolean insertUser(String id_sopir, String sopir, String username, String device_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id_sopir", id_sopir);
        cv.put("sopir", sopir);
        cv.put("username", username);
        cv.put("device_id", device_id);
        long result = db.insert(TABLE_USER, null, cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void insertSopir(String id_sopir, String nama) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id_sopir", id_sopir);
        cv.put("nama", nama);
        db.insert(TABLE_SOPIR, null, cv);
    }

    public String getIdSopir(String nama) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_SOPIR + " WHERE nama = '" + nama + "'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        return c.getString(c.getColumnIndex("id_sopir"));
    }

    public boolean userIsExists(String device_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_USER + " WHERE device_id = '" + device_id + "'";
        Cursor c = db.rawQuery(sql, null);

        if (c.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String verifyUser(String device_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_USER + " WHERE device_id = '" + device_id + "'";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToLast();
            return c.getString(c.getColumnIndex("id_sopir"));
        } else {
            return "";
        }
    }

    public void cleanTaraAndTimbang(String no_do) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "DELETE FROM " + TABLE_TARA + " WHERE no_do = '" + no_do + "'";
        db.rawQuery(sql, null);

        sql = "DELETE FROM " + TABLE_TIMBANG + " WHERE no_do = '" + no_do + "'";
        db.rawQuery(sql, null);
    }
}
