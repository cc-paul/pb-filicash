package com.kwjj.filicash;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class pin extends AppCompatActivity {
    LinearLayout lnButton1,lnButton2,lnButton3,lnButton4,lnButton5,lnButton6,lnButton7,lnButton8,lnButton9,lnButton0,lnButtonClose,lnBack;
    EditText etPin;
    TextView tvPinTitle,tvSavePin;

    Dialog dialog,notVerified,noPin;

    SharedPreferences sp;

    String walletID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        walletID = sp.getString("walletID","Wallet ID not found");

        lnButton1 = findViewById(R.id.lnButton1);
        lnButton2 = findViewById(R.id.lnButton2);
        lnButton3 = findViewById(R.id.lnButton3);
        lnButton4 = findViewById(R.id.lnButton4);
        lnButton5 = findViewById(R.id.lnButton5);
        lnButton6 = findViewById(R.id.lnButton6);
        lnButton7 = findViewById(R.id.lnButton7);
        lnButton8 = findViewById(R.id.lnButton8);
        lnButton9 = findViewById(R.id.lnButton9);
        lnButton0 = findViewById(R.id.lnButton0);
        lnButtonClose = findViewById(R.id.lnButtonClose);
        lnBack = findViewById(R.id.lnBack);
        tvPinTitle = findViewById(R.id.tvPinTitle);
        tvSavePin = findViewById(R.id.tvSavePin);

        etPin = findViewById(R.id.etPin);

        lnButton1.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "1");
        });

        lnButton2.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "2");
        });

        lnButton3.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "3");
        });

        lnButton4.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "4");
        });

        lnButton5.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "5");
        });

        lnButton6.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "6");
        });

        lnButton7.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "7");
        });

        lnButton8.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "8");
        });

        lnButton9.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "9");
        });

        lnButton0.setOnClickListener(view -> {
            etPin.setText(etPin.getText() + "0");
        });

        lnButtonClose.setOnClickListener(view -> {
            etPin.setText("");
        });

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        tvSavePin.setOnClickListener(
                view -> {
                    if (etPin.getText().toString().equals("")) {
                        Toast.makeText(pin.this, "Please provide a pin", Toast.LENGTH_LONG).show();
                    } else {
                        savePin();
                    }
                }
        );


        etPin.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4 && tvSavePin.getVisibility() == View.INVISIBLE) {
                    checkPin();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        AlertDialog.Builder notVerifiedBuilder = new AlertDialog.Builder(this);
        notVerifiedBuilder.setMessage("Your account is not yet verified. Unable to use wallet")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    lnBack.performClick();
                });
        notVerified = notVerifiedBuilder.create();

        AlertDialog.Builder noPinBuilder = new AlertDialog.Builder(this);
        noPinBuilder.setMessage("Your pin is not yet setup. Proceed in creating?")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    noPin.dismiss();
                    tvSavePin.setVisibility(View.VISIBLE);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    lnBack.performClick();
                });
        noPin = noPinBuilder.create();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        CheckAccount();
    }

    public void checkPin() {
        Links application = (Links) getApplication();
        String pin_find = application.pin_find;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, pin_find,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        dialog.dismiss();
                        Toast.makeText(pin.this, message, Toast.LENGTH_LONG).show();
                        etPin.setText("");

                        if (!error) {
                            Intent gotoWallet = new Intent(pin.this, Wallet.class);
                            startActivity(gotoWallet);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error",e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(pin.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    Toast.makeText(pin.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wid", walletID);
                params.put("pin", etPin.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void savePin() {
        Links application = (Links) getApplication();
        String pin_create = application.pin_create;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, pin_create,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        dialog.dismiss();
                        Toast.makeText(pin.this, message, Toast.LENGTH_LONG).show();

                        if (!error) {
                            tvPinTitle.setText("PIN Confirmation");
                            tvSavePin.setVisibility(View.INVISIBLE);
                            etPin.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error",e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(pin.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    Toast.makeText(pin.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wid", walletID);
                params.put("pin", etPin.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void CheckAccount() {
        Links application = (Links) getApplication();
        String verificationApi = application.verification_checker;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, verificationApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        if (error) {
                            dialog.dismiss();
                            lnBack.performClick();
                            Toast.makeText(pin.this, message, Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrAccess = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrAccess.length(); i++) {
                                JSONObject current_obj = arrAccess.getJSONObject(i);

                                Integer verified = current_obj.getInt("isVerified");
                                String  pin      = current_obj.getString("pin");

                                Log.e("Error",pin);
                                dialog.dismiss();

                                if (verified == 1) {
                                    if (pin.equals("-")) {
                                        noPin.show();
                                        tvPinTitle.setText("PIN Registration");
                                    }
                                } else {
                                    notVerified.show();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error",e.getMessage());
                        dialog.dismiss();
                        lnBack.performClick();
                        Toast.makeText(pin.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    lnBack.performClick();
                    Toast.makeText(pin.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wid", walletID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}