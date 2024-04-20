package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class DriverDetails extends AppCompatActivity {
    LinearLayout lnBack,lnSave;
    EditText etVehicleName,etPlateNumber,etConductorsName;

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String walletID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        walletID = sp.getString("walletID", "Wallet ID not found");

        lnBack = findViewById(R.id.lnBack);
        lnSave = findViewById(R.id.lnSave);

        etVehicleName = findViewById(R.id.etVehicleName);
        etPlateNumber = findViewById(R.id.etPlateNumber);
        etConductorsName = findViewById(R.id.etConductorsName);

        lnBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        lnSave.setOnClickListener(view -> {
            if (etVehicleName.getText().toString().replaceAll(" ","").length() == 0 || etPlateNumber.getText().toString().replaceAll(" ","").length() == 0) {
                Toast.makeText(DriverDetails.this,"Provide at least Vehicle Name and Plate Number",Toast.LENGTH_LONG).show();
            } else {
                dialog.show();
                saveDriverDetails();
            }
        });
    }

    private void saveDriverDetails() {
        Links application = (Links) getApplication();
        String changeDriverAPI = application.update_driver;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, changeDriverAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            Toast.makeText(DriverDetails.this, message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            if (!error) {
                                lnBack.performClick();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(DriverDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(DriverDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("walletID", walletID);
                params.put("vehicleName", etVehicleName.getText().toString());
                params.put("plateNumber", etPlateNumber.getText().toString());
                params.put("conductorsName", etConductorsName.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}