package com.kwjj.filicash;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class bottom_cashout extends BottomSheetDialogFragment {
    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String amount,walletID;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_cashout, container, false);

        sp = v.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        amount = sp.getString("amount", "0");
        walletID = sp.getString("walletID", "Wallet ID not found");

        LinearLayout lnConfirm = v.findViewById(R.id.lnConfirm);
        EditText etAmount = v.findViewById(R.id.etAmount);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        lnConfirm.setOnClickListener(
                view -> {
                    if (etAmount.getText().toString().equals("")) {
                        Toast.makeText(v.getContext(),"Please add an amount to be cash out",Toast.LENGTH_LONG).show();
                    } else if (Double.parseDouble(etAmount.getText().toString()) > Double.parseDouble(amount.replace(",",""))) {
                        Toast.makeText(v.getContext(),"The amount you can cash out is " + amount,Toast.LENGTH_LONG).show();
                    } else {
                        dialog.show();

                        Links application = (Links) v.getContext().getApplicationContext();
                        String cashoutApi = application.cash_out;

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, cashoutApi,
                                response -> {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        Boolean error = obj.getBoolean("error");
                                        String message = obj.getString("message");
                                        dialog.dismiss();

                                        if (!error) {
                                            dismiss();

                                            Intent goToReceipt = new Intent(v.getContext(), receipt.class);
                                            goToReceipt.putExtra("label1","Successfully cash out");
                                            goToReceipt.putExtra("label2",walletID);
                                            goToReceipt.putExtra("label3","PHP " + etAmount.getText().toString());
                                            goToReceipt.putExtra("label4","Reference Number");
                                            goToReceipt.putExtra("label5",obj.getString("ref"));
                                            goToReceipt.putExtra("label6","You can use this receipt to any nearby bank");
                                            goToReceipt.putExtra("label7",obj.getString("date"));
                                            startActivity(goToReceipt);
                                        } else {
                                            Toast.makeText(v.getContext(), message, Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                        dialog.dismiss();
                                        Log.e("Error", e.getMessage());
                                        Toast.makeText(v.getContext(), "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                                    }
                                },
                                error -> {
                                    // error.toString()
                                    dialog.dismiss();
                                    Log.e("Error", error.toString());
                                    Toast.makeText(v.getContext(), "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("walletID", walletID);
                                params.put("amount", etAmount.getText().toString());
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                        requestQueue.add(stringRequest);
                    }
                }
        );

        return v;
    }
}
