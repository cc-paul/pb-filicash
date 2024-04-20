package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity {
    LinearLayout lnBack, lnSignUp;
    TextView tvSignIn;
    ImageView imgFB, imgTwitter, imgInstagram;
    EditText etUserName, etEmail, etPassword;

    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        lnBack = findViewById(R.id.lnBack);
        lnSignUp = findViewById(R.id.lnSignUp);

        tvSignIn = findViewById(R.id.tvSignIn);

        imgFB = findViewById(R.id.imgFB);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgInstagram = findViewById(R.id.imgInstagram);

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        tvSignIn.setOnClickListener(
                view -> {
                    Intent gotoRegister = new Intent(Register.this, Login.class);
                    startActivity(gotoRegister);
                    finish();
                }
        );

        imgFB.setOnClickListener(
                view -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/FiliCashPhilippines/")));
                }
        );

        imgTwitter.setOnClickListener(
                view -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/filicashph")));
                }
        );

        imgInstagram.setOnClickListener(
                view -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/fili_cash_ph/")));
                }
        );

        lnSignUp.setOnClickListener(
                view -> {
                    /* check if all fields are not empty */
                    if (etUserName.getText().toString().equals("") || etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("")) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                    } else {
                        /* check if email is formatted */
                        if (!validateEmail(etEmail.getText().toString().trim())) {
                            Toast.makeText(this, "Please provide a proper Email", Toast.LENGTH_LONG).show();
                        } else {
                            /* check password length */
                            if (etPassword.getText().toString().length() < 8) {
                                Toast.makeText(this, "Password must be 8 characters", Toast.LENGTH_LONG).show();
                            } else {
                                /* Check if email exist before saving */
                                saveAccount();
                            }
                        }
                    }
                }
        );
    }

    public void saveAccount() {
        Links application = (Links) getApplication();
        String registrationAPI = application.registrationAPI;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registrationAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            if (!error) {
                                tvSignIn.performClick();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", etUserName.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("password", etPassword.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean validateEmail(String data) {
        Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher emailMatcher = emailPattern.matcher(data);
        return emailMatcher.matches();
    }
}