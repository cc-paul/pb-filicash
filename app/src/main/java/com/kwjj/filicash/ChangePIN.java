package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePIN extends AppCompatActivity {
    EditText etNewPIN,etRepeatPIN;
    TextView tvSavePin;
    LinearLayout lnClear,lnButton1,lnButton2,lnButton3,lnButton4,lnButton5,lnButton6,lnButton7,lnButton8,lnButton9,lnButton0,lnBack;

    Boolean isStart = true;

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String pin;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_p_i_n);

        etNewPIN = findViewById(R.id.etNewPIN);
        etRepeatPIN = findViewById(R.id.etRepeatPIN);
        tvSavePin = findViewById(R.id.tvSavePin);
        lnClear = findViewById(R.id.lnClear);
        lnBack = findViewById(R.id.lnBack);
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

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        id = sp.getInt("id", 0);
        pin = sp.getString("pin","");

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        etNewPIN.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4) {
                    isStart = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        lnButton1.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "1");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "1");
            }
        });

        lnButton2.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "2");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "2");
            }
        });

        lnButton3.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "3");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "3");
            }
        });

        lnButton4.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "4");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "4");
            }
        });

        lnButton5.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "5");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "5");
            }
        });

        lnButton6.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "6");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "6");
            }
        });

        lnButton7.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "7");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "7");
            }
        });

        lnButton8.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "8");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "8");
            }
        });

        lnButton9.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "9");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "9");
            }
        });

        lnButton0.setOnClickListener(view -> {
            if (isStart) {
                etNewPIN.setText(etNewPIN.getText() + "0");
            } else {
                etRepeatPIN.setText(etRepeatPIN.getText() + "0");
            }
        });

        lnClear.setOnClickListener(view -> {
            etNewPIN.setText("");
            etRepeatPIN.setText("");
            isStart = true;
        });

        tvSavePin.setOnClickListener(view -> {
            if (etNewPIN.getText().toString().equals("") || etRepeatPIN.getText().toString().equals("")) {
                Toast.makeText(ChangePIN.this, "Please provide a pin", Toast.LENGTH_LONG).show();
            } else if (etNewPIN.getText().toString().length() < 4 || etRepeatPIN.getText().toString().length() < 4) {
                Toast.makeText(ChangePIN.this, "PIN must be 4 digits", Toast.LENGTH_LONG).show();
            } else if (!etNewPIN.getText().toString().equals(etRepeatPIN.getText().toString())) {
                Toast.makeText(ChangePIN.this, "PIN's are not match", Toast.LENGTH_LONG).show();
            } else {
                changePIN();
            }
        });

        loadPinDialog();
    }

    private void loadPinDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_pin, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText etPin = (EditText) mView.findViewById(R.id.etPin);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (etPin.getText().toString().equals(pin)) {
                            /* Do nothing */
                        } else {
                            Toast.makeText(ChangePIN.this,"PIN is incorrect",Toast.LENGTH_LONG).show();
                            lnBack.performClick();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        lnBack.performClick();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.man_medium);

        Button button1 = alertDialogAndroid.findViewById(android.R.id.button1);
        button1.setAllCaps(false);
        button1.setEnabled(false);
        button1.setAlpha(0.5f);
        button1.setTypeface(typeface);
        button1.setTextColor(getApplication().getResources().getColor(R.color.black));

        Button button2 = alertDialogAndroid.findViewById(android.R.id.button2);
        button2.setAllCaps(false);
        button2.setTypeface(typeface);
        button2.setTextColor(getApplication().getResources().getColor(R.color.black));


        etPin.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    button1.setEnabled(false);
                    button1.setAlpha(0.2f);
                } else {
                    button1.setEnabled(true);
                    button1.setAlpha(1f);
                }
            }
        });
    }

    private void changePIN() {
        Links application = (Links) getApplication();
        String changePINApi = application.change_pin;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, changePINApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");
                            Log.e("PIN Response",response);

                            Toast.makeText(ChangePIN.this, message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            if (!error) {
                                editor.putString("pin",etNewPIN.getText().toString());
                                editor.commit();
                                lnBack.performClick();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(ChangePIN.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(ChangePIN.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", "" + id);
                params.put("pin", etNewPIN.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}