package com.kwjj.filicash;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kwjj.filicash.rv_history.transactionAdapter;
import com.kwjj.filicash.rv_history.transactionData;
import com.kwjj.filicash.rv_location.locationAdapter;
import com.kwjj.filicash.rv_location.locationData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationList extends AppCompatActivity {
    LinearLayout lnBack;
    TextView tvRecords;

    Dialog dialog;

    Intent intent;

    RecyclerView rvLocation;
    private RecyclerView.Adapter adapter;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        lnBack = findViewById(R.id.lnBack);
        rvLocation = findViewById(R.id.rvLocation);
        tvRecords = findViewById(R.id.tvRecords);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        intent  = getIntent();
        Bundle extras = intent.getExtras();

        loadLocationList(extras.getString("from","-"),extras.getString("to","-"));

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );
    }

    private void loadLocationList(String from,String to) {
        ArrayList<locationData> list = new ArrayList<>();

        Links application = (Links) getApplication();
        String locationApi = application.get_location;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, locationApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        Log.e("Response",response);

                        if (error){
                            Toast.makeText(LocationList.this, "Unable to get data from server", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrLocation = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrLocation.length(); i++) {
                                JSONObject current_obj = arrLocation.getJSONObject(i);

                                list.add(new locationData(
                                        current_obj.getInt("id"),
                                        current_obj.getString("location"),
                                        current_obj.getDouble("price")
                                ));
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                            rvLocation.setLayoutManager(mLayoutManager);
                            tvRecords.setText("Total Records : " + list.size());

                            adapter = new locationAdapter(list);
                            rvLocation.setAdapter(adapter);
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(LocationList.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Toast.makeText(LocationList.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("from", from);
                params.put("to", to);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getBack() {
        lnBack.performClick();
    }
}