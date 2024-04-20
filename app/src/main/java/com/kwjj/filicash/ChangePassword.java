package com.kwjj.filicash;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    EditText etNewPassword,etRepeatNewPassword;
    LinearLayout lnSave, lnBack;

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etRepeatNewPassword = findViewById(R.id.etRepeatNewPassword);
        lnSave = findViewById(R.id.lnSave);
        lnBack = findViewById(R.id.lnBack);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        id = sp.getInt("id", 0);

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        lnSave.setOnClickListener(view -> {
            if (etNewPassword.getText().toString().equals("") || etRepeatNewPassword.getText().toString().equals("")) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            } else if (etNewPassword.getText().toString().length() < 8 || etRepeatNewPassword.getText().toString().length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_LONG).show();
            } else if (!etNewPassword.getText().toString().equals(etRepeatNewPassword.getText().toString())) {
                Toast.makeText(this,"Password does not match",Toast.LENGTH_LONG).show();
            } else {
                changePassword();
            }
        });
    }

    private void changePassword() {
        Links application = (Links) getApplication();
        String changePasswordApi = application.change_pass;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, changePasswordApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            Toast.makeText(ChangePassword.this, message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            if (!error) {
                                editor.putString("password", etNewPassword.getText().toString());
                                editor.commit();
                                lnBack.performClick();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(ChangePassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(ChangePassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", "" + id);
                params.put("password", etNewPassword.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}