package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class TransferMoney extends AppCompatActivity {
    EditText etWID, etAmount, etRemarks;
    LinearLayout lnTransfer, lnBack;
    TextView tvAmount;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String walletID, amount;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_money);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        walletID = sp.getString("walletID", "NOWALLETID");

        etWID = findViewById(R.id.etWID);
        etAmount = findViewById(R.id.etAmount);
        etRemarks = findViewById(R.id.etRemarks);

        lnTransfer = findViewById(R.id.lnTransfer);
        lnBack = findViewById(R.id.lnBack);

        tvAmount = findViewById(R.id.tvAmount);

        amount = sp.getString("amount", "0");
        tvAmount.setText("PHP " + amount);

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        lnTransfer.setOnClickListener(
                view -> {
                    if (etWID.getText().toString().equals("") || etAmount.getText().toString().equals("")) {
                        Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_LONG).show();
                    } else {
                        if (etWID.getText().toString().length() != 13) {
                            Toast.makeText(this, "Wallet ID must be 13 characters", Toast.LENGTH_LONG).show();
                        } else {
                            if (etWID.getText().toString().equals(walletID.replace("WID", ""))) {
                                Toast.makeText(this, "Unable to send to yourself", Toast.LENGTH_LONG).show();
                                etWID.setText("");
                            } else {
                                if (Double.parseDouble(amount.replace(",", "")) < Double.parseDouble(etAmount.getText().toString())) {
                                    Toast.makeText(this, "The amount you can send is PHP" + amount, Toast.LENGTH_LONG).show();
                                } else {
                                    SendMoney(walletID, etWID.getText().toString(), etAmount.getText().toString());
                                }
                            }
                        }
                    }
                }
        );

        etAmount.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String text = arg0.toString();
                if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                    etAmount.setText(text.substring(0, text.length() - 1));
                    etAmount.setSelection(etAmount.getText().length());
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            public void afterTextChanged(Editable arg0) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
    }

    public void SendMoney(String wid_from, String wid_to, String amount) {
        dialog.show();

        Links application = (Links) getApplication();
        String transfer = application.transfer;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, transfer,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");
                        String ref = obj.getString("ref");
                        String date = obj.getString("date");
                        String cur_amount = obj.getString("amount");

                        dialog.dismiss();

                        if (error) {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        } else {
                            Intent goToReceipt = new Intent(TransferMoney.this, receipt.class);
                            goToReceipt.putExtra("label1", "Amount successfully sent");
                            goToReceipt.putExtra("label2", wid_to);
                            goToReceipt.putExtra("label3", "PHP " + cur_amount);
                            goToReceipt.putExtra("label4", "Reference Number");
                            goToReceipt.putExtra("label5", ref);
                            goToReceipt.putExtra("label6", etRemarks.getText().toString());
                            goToReceipt.putExtra("label7", date);
                            startActivity(goToReceipt);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error", e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error", error.toString());
                    Toast.makeText(this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wid_from", wid_from);
                params.put("wid_to", wid_to);
                params.put("amount", amount);
                params.put("remarks", etRemarks.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}