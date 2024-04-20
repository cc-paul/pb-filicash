package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kwjj.filicash.rv_history.transactionAdapter;
import com.kwjj.filicash.rv_history.transactionData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TranspoHistory extends AppCompatActivity {
    View vwReceived,vwSent;
    LinearLayout lnReceived,lnSent,lnBack;
    RecyclerView rvHistory;
    TextView tvAmount,tvRecords;

    String walletID;

    private RecyclerView.Adapter adapter;

    Dialog dialog;

    Intent intent;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transpo_history);

        vwReceived = findViewById(R.id.vwReceived);
        vwSent = findViewById(R.id.vwSent);

        lnReceived = findViewById(R.id.lnReceived);
        lnSent = findViewById(R.id.lnSent);
        lnBack = findViewById(R.id.lnBack);

        rvHistory = findViewById(R.id.rvHistory);

        tvAmount = findViewById(R.id.tvAmount);
        tvRecords = findViewById(R.id.tvRecords);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        walletID = sp.getString("walletID", "Wallet ID not found");

        vwReceived.setVisibility(View.VISIBLE);
        vwSent.setVisibility(View.INVISIBLE);

        intent  = getIntent();
        Bundle extras = intent.getExtras();

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        loadHistory("out");
    }

    public void loadHistory(String flow) {
        ArrayList<transactionData> list = new ArrayList<>();

        Links application = (Links) getApplication();
        String historyAPI = application.transpo_history;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, historyAPI,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(TranspoHistory.this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrHistory = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrHistory.length(); i++) {
                                JSONObject current_obj = arrHistory.getJSONObject(i);

                                list.add(new transactionData(
                                        current_obj.getString("flow"),
                                        current_obj.getString("flow"),
                                        current_obj.getString("label"),
                                        "₱" + current_obj.getString("amount"),
                                        current_obj.getString("date"),
                                        current_obj.getString("time"),
                                        current_obj.getInt("isPrintable")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                            rvHistory.setLayoutManager(mLayoutManager);

                            adapter = new transactionAdapter(list);
                            rvHistory.setAdapter(adapter);

                            tvRecords.setText("Total Records : " + list.size());
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(TranspoHistory.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Toast.makeText(TranspoHistory.this, "Something went wrong", Toast.LENGTH_LONG).show();
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