package com.wonokoyo.rpawonokoyo.utilities;

import android.app.Activity;
import android.content.Context;

public class EvListener {
    public void OutApp(final Context context) {
        CustomDialog cd = new CustomDialog();
        cd.alertDialog("", "Yakin ingin keluar aplikasi ?", context, new CustomDialog.alertDialogCallBack() {
            @Override
            public void action(Boolean val, String pin) {
                if (val) {
                    ((Activity) context).finish();
                }
            }
        });
    }
}
