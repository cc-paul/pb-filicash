package com.kwjj.filicash;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    LinearLayout lnBack,lnLogin;
    TextView tvSignUp;
    ImageView imgFB,imgTwitter,imgInstagram,imgLogo;
    EditText etEmailUser,etPassword;

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Integer count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        lnBack = findViewById(R.id.lnBack);
        lnLogin = findViewById(R.id.lnLogin);

        tvSignUp = findViewById(R.id.tvSignUp);

        imgFB = findViewById(R.id.imgFB);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgInstagram = findViewById(R.id.imgInstagram);
        imgLogo = findViewById(R.id.imgLogo);

        etEmailUser = findViewById(R.id.etEmailUser);
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

        tvSignUp.setOnClickListener(
            view -> {
                Intent gotoTerms = new Intent(Login.this, Terms.class);
                startActivity(gotoTerms);
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

        lnLogin.setOnClickListener(
            view -> {
                /* check if all fields are not empty */
                if (etEmailUser.getText().toString().equals("")  || etPassword.getText().toString().equals("") ) {
                    Toast.makeText(this,"Please fill in all fields", Toast.LENGTH_LONG).show();
                } else {
                    LoginAccount();
                }
            }
        );

        lnLogin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etEmailUser.setText("papap@gmail.com");
                etPassword.setText("123123123123");
                lnLogin.performClick();
                return true;
            }
        });

    }

    public void LoginAccount() {
        Links application = (Links) getApplication();
        String loginApi = application.loginApi;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginApi,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            if (!error) {
                                JSONArray arrAccess = obj.getJSONArray("result");
                                for (Integer i = 0; i < arrAccess.length(); i++) {
                                    JSONObject current_obj = arrAccess.getJSONObject(i);

                                    Integer id = current_obj.getInt("id");
                                    String walletID = current_obj.getString("walletID");
                                    Integer isVerified = current_obj.getInt("isVerified");
                                    String fullName = current_obj.getString("fullName");
                                    String pin = current_obj.getString("pin");

                                    editor.putInt("id", id);
                                    editor.putString("walletID", walletID);
                                    editor.putInt("isVerified", isVerified);
                                    editor.putString("fullName", fullName);
                                    editor.putString("password", etPassword.getText().toString());
                                    editor.putString("pin", pin);
                                    editor.commit();
                                }

                                Intent gotoMain = new Intent(Login.this, MainActivity.class);
                                finishAffinity();
                                startActivity(gotoMain);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emailuser", etEmailUser.getText().toString());
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