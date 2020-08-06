package com.wonokoyo.rpawonokoyo.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Path;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wonokoyo.rpawonokoyo.R;

public class CustomDialog {
    private EditText etPin;

    public AlertDialog.Builder alertDialog(String title, String message, Context context, final alertDialogCallBack adc) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setCancelable(false);

        ad.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(true, "");
                dialogInterface.cancel();
            }
        });

        ad.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(false, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public AlertDialog.Builder alertDialogKonfirmasi(String no_do, Context context, final alertDialogCallBack adc) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle("");
        ad.setMessage("Masukkan PIN jika ingin melanjutkan proses ke penimbangan dengan Nomor DO : " + no_do);
        ad.setCancelable(false);

        // Set up the input
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_pin, null);
        ad.setView(view);

        etPin = view.findViewById(R.id.etPin);

        ad.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pin = etPin.getText().toString();
                adc.action(true, pin);
                dialogInterface.cancel();
            }
        });

        ad.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(false, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public AlertDialog.Builder alertDialogPetunjuk(int layout, Context context, final alertDialogCallBack adc) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle("");
        ad.setCancelable(false);

        // Set up the input
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(layout, null);
        ad.setView(view);

        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(true, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public AlertDialog.Builder alertDialogTimbangan(Context context, final alertDialogCallBack adc) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle("WARNING");
        ad.setMessage("Harap selesaikan penimbangan !");
        ad.setCancelable(true);

        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(true, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }


    public AlertDialog.Builder alertDialogLogout(Context context, final alertDialogCallBack adc) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle("Log Out");
        ad.setMessage("Log out akun yang sedang berjalan ?");
        ad.setCancelable(true);

        ad.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(true, "");
                dialogInterface.cancel();
            }
        });

        ad.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(false, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public AlertDialog.Builder alertDialogYes(String title, String message, Context context, final alertDialogCallBack adc) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setCancelable(true);

        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(true, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public AlertDialog.Builder alertUserTidakTerdaftar(Context context, final alertDialogCallBack adc) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle("Warning");
        ad.setMessage("User belum terdaftar dan aktif. Silahkan tekan 'Ya' bila ingin mendaftar dan mengaktifkan akun, tekan 'Tidak' untuk batal.");
        ad.setCancelable(false);

        ad.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(true, "");
                dialogInterface.cancel();
            }
        });

        ad.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(false, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public AlertDialog.Builder alertAkun(Context context, int a, final alertDialogCallBack adc) {
        String[] msgs = new String[2];
        msgs[0] = "Nama karyawan atau Akun atau Device sudah terdaftar !";
        msgs[1] = "Nama karyawan tidak terdaftar. Silahkan coba lagi.";
        AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle("Informasi");
        ad.setMessage(msgs[a]);
        ad.setCancelable(true);

        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(true, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public AlertDialog.Builder alertWifi(String title, String message, Context context, final alertDialogCallBack adc) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setCancelable(false);

        ad.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(true, "");
                dialogInterface.cancel();
            }
        });

        ad.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adc.action(false, "");
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public AlertDialog.Builder alertNetwork(Context context) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ad.setTitle("Network Problem");
        ad.setMessage("Please connect to WIFI to proceed");
        ad.setCancelable(true);

        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = ad.create();
        alert.show();

        return ad;
    }

    public interface alertDialogCallBack {
        void action(Boolean val, String pin);
    }
}
