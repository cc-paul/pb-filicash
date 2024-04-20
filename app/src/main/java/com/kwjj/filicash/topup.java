package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class topup extends AppCompatActivity {
    Integer userID;

    LinearLayout lnBack,lnConfirm;
    EditText etRefNumber,etAmount;
    TextView tvBankName;
    ImageView imgBankLogo;

    Intent intent;

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        userID = sp.getInt("id",0);

        lnBack = findViewById(R.id.lnBack);
        lnConfirm = findViewById(R.id.lnConfirm);
        etRefNumber = findViewById(R.id.etRefNumber);
        etAmount  = findViewById(R.id.etAmount);
        tvBankName = findViewById(R.id.tvBankName);
        imgBankLogo = findViewById(R.id.imgBankLogo);

        intent  = getIntent();
        Bundle extras = intent.getExtras();

        tvBankName.setText(extras.getString("BankName"));
        imgBankLogo.setImageResource(extras.getInt("BankLogo"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        etRefNumber.setOnLongClickListener(v -> {
            etRefNumber.setText(generateRandomPassword(11).toUpperCase());
            return true;
        });

        lnConfirm.setOnClickListener(view -> {
            if (etRefNumber.getText().toString().equals("") || etAmount.getText().toString().equals("")) {
                Toast.makeText(topup.this, "All fields are required", Toast.LENGTH_LONG).show();
            } else {
                if (etRefNumber.getText().length() != 11) {
                    Toast.makeText(topup.this, "Reference number must be 11 digits", Toast.LENGTH_LONG).show();
                } else {
                    if (Double.parseDouble(etAmount.getText().toString()) > 100000) {
                        Toast.makeText(topup.this, "Maximum Top Up is PHP100,000", Toast.LENGTH_LONG).show();
                    } else {
                        Links application = (Links) getApplication();
                        String topup = application.topup;

                        dialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, topup,
                                response -> {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        Boolean error = obj.getBoolean("error");
                                        String message = obj.getString("message");
                                        String date = obj.getString("date");
                                        String amount = obj.getString("amount");


                                        dialog.dismiss();
                                        Toast.makeText(topup.this, message, Toast.LENGTH_LONG).show();

                                        if (!error) {
                                            Intent goToReceipt = new Intent(topup.this, receipt.class);
                                            goToReceipt.putExtra("label1","Successfully top up to");
                                            goToReceipt.putExtra("label2",tvBankName.getText().toString());
                                            goToReceipt.putExtra("label3","PHP " + amount);
                                            goToReceipt.putExtra("label4","Reference Number");
                                            goToReceipt.putExtra("label5",etRefNumber.getText().toString());
                                            goToReceipt.putExtra("label6","-");
                                            goToReceipt.putExtra("label7",date);
                                            startActivity(goToReceipt);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                        Log.e("Error",e.getMessage());
                                        dialog.dismiss();
                                        Toast.makeText(topup.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                                    }
                                },
                                error -> {
                                    // error.toString()
                                    Log.e("Error",error.toString());
                                    Toast.makeText(topup.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("userID", userID.toString());
                                params.put("refNumber", etRefNumber.getText().toString());
                                params.put("amount", etAmount.getText().toString());
                                params.put("bankName", tvBankName.getText().toString());
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        requestQueue.add(stringRequest);
                    }
                }
            }
        });

        etRefNumber.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
    }

    private static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}