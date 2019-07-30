package com.wonokoyo.rpawonokoyo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wonokoyo.rpawonokoyo.connection.RetrofitInstance;
import com.wonokoyo.rpawonokoyo.databaselokal.DatabaseHelper;
import com.wonokoyo.rpawonokoyo.utilities.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registrasi extends AppCompatActivity {

    // variable layout
    private Button btnAktifasi;
    private EditText etIMEI;
    private EditText etNomorTelp;
    private AutoCompleteTextView etNamaRegister;
    private EditText etUsernameRegister;
    private EditText etPasswordRegister;
    private EditText etConfirmPassword;

    private Context mContext;

    // variable lain
    DatabaseHelper dbh;
    CustomDialog cd;
    ProgressDialog pd;
    private String[] daftarNamaSopir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registrasi);

        dbh = new DatabaseHelper(this);
        pd = new ProgressDialog(this);
        cd = new CustomDialog();

        mContext = this;

        pullDataSopir();

        etIMEI = findViewById(R.id.etIMEI);
        etNomorTelp = findViewById(R.id.etNomorTelp);
        etNamaRegister = findViewById(R.id.autoCompleteNama);
        etUsernameRegister = findViewById(R.id.etUsernameRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        etIMEI.setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        btnAktifasi = findViewById(R.id.btnAktifasi);
        btnAktifasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateField()) {
                    pd.setMessage("Please wait");
                    pd.show();
                    pd.setCancelable(false);

                    String id_sopir = dbh.getIdSopir(etNamaRegister.getText().toString());
                    Call<ResponseBody> callResponse = RetrofitInstance.userAPI().registerImei(id_sopir, etNamaRegister.getText().toString(),
                            etUsernameRegister.getText().toString(), etPasswordRegister.getText().toString(), etIMEI.getText().toString(),
                            etNomorTelp.getText().toString());
                    callResponse.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    String inserted = jsonObject.getString("content");

                                    if (inserted.equalsIgnoreCase("exist")) {
                                        cd.alertAkun(Registrasi.this, 0, new CustomDialog.alertDialogCallBack() {
                                            @Override
                                            public void action(Boolean val, String pin) {
                                                if (val) {
                                                    if (pd.isShowing())
                                                        pd.dismiss();
                                                }
                                            }
                                        });
                                    } else if (inserted.equalsIgnoreCase("true")) {
                                        if (pd.isShowing()) {
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();

                                                    Toast.makeText(Registrasi.this, "Akun telat dibuat dan aktif", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(Registrasi.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            },1000);
                                        }
                                    } else {
                                        cd.alertAkun(Registrasi.this, 1, new CustomDialog.alertDialogCallBack() {
                                            @Override
                                            public void action(Boolean val, String pin) {

                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(Registrasi.this, "Tidak terhubung", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            if (t.getMessage().equalsIgnoreCase("timeout")) {
                                Toast.makeText(Registrasi.this, "Koneksi ke server gagal", Toast.LENGTH_LONG).show();
                            }

                            if (t.getMessage().contains("failed to connect")) {
                                Toast.makeText(Registrasi.this, "Cek Kembali Koneksi Anda", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        etConfirmPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode > KeyEvent.KEYCODE_0 && keyCode < KeyEvent.KEYCODE_Z) || keyCode == KeyEvent.KEYCODE_DEL)
                    return cekPassword();

                return false;
            }
        });
    }

    public void pullDataSopir() {
        Call<ResponseBody> ambilDaftarSopir = RetrofitInstance.userAPI().getDataSopir();
        ambilDaftarSopir.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("content");

                        if (jsonArray.length() > 0) {
                            daftarNamaSopir = new String[jsonArray.length()];
                            for (int a = 0; a < jsonArray.length(); a++) {
                                JSONObject item = jsonArray.getJSONObject(a);

                                daftarNamaSopir[a] = item.getString("nama_sopir");
                                dbh.insertSopir(item.getString("id_sopir"), item.getString("nama_sopir"));
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, daftarNamaSopir);
                        etNamaRegister.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("CEK", "GAGAL");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    Toast.makeText(Registrasi.this, "Koneksi ke server gagal", Toast.LENGTH_LONG).show();
                }

                if (t.getMessage().contains("failed to connect")) {
                    Toast.makeText(Registrasi.this, "Cek kembali koneksi anda", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean validateField() {
        String nama = etNamaRegister.getText().toString().trim();
        String username = etUsernameRegister.getText().toString().trim();
        String password = etPasswordRegister.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();
        String telepon = etNomorTelp.getText().toString().trim();

        if (nama.isEmpty()) {
            etNamaRegister.setError("Harap isi nama");
            etNamaRegister.requestFocus();
            return false;
        }
        if (username.isEmpty()) {
            etUsernameRegister.setError("Harap isi username");
            etUsernameRegister.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPasswordRegister.setError("Harap isi password");
            etPasswordRegister.requestFocus();
            return false;
        }
        if (confirm.isEmpty()) {
            etConfirmPassword.setError("Harap isi password");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (!confirm.equals(password)) {
            etConfirmPassword.setError("Password tidak sama");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (telepon.isEmpty()) {
            etNomorTelp.setError("Harap isi telepon");
            etNomorTelp.requestFocus();
            return false;
        }

        return true;
    }

    public boolean cekPassword() {
        String password = etPasswordRegister.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (!password.equals(confirm)) {
            etConfirmPassword.setError("Password harus sama !");
            return false;
        }
        return true;
    }

}
