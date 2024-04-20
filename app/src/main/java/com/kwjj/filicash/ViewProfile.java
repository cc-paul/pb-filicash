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
import android.os.Build;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewProfile extends AppCompatActivity {

    Dialog dialog;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    LinearLayout lnBack,lnAddress;
    EditText etFirstName,etLastName,etUserName,etEmail,etMobile,etAddress,etUserType;
    TextView tvChangePassword,tvChangePIN,tvLogout,tvDriverDetails;

    String walletID;

    Boolean isUserTypeAvailable = true;
    ArrayList<String> userList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        lnBack = findViewById(R.id.lnBack);
        lnAddress = findViewById(R.id.lnAddress);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        etUserType = findViewById(R.id.etUserType);

        tvChangePassword = findViewById(R.id.tvChangePassword);
        tvChangePIN = findViewById(R.id.tvChangePIN);
        tvLogout = findViewById(R.id.tvLogout);
        tvDriverDetails = findViewById(R.id.tvDriverDetails);

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        walletID = sp.getString("walletID", "Wallet ID not found");
        Log.e("Wallet ID",walletID);

        viewDetails(walletID);

        tvLogout.setOnClickListener(
                view -> {
                    Intent gotoLogin = new Intent(ViewProfile.this, Welcome.class);
                    startActivity(gotoLogin);
                    finishAffinity();
                }
        );

        tvChangePassword.setOnClickListener(
                view -> {
                    Intent gotoChangePass = new Intent(ViewProfile.this, ChangePassword.class);
                    startActivity(gotoChangePass);
                }
        );

        tvChangePIN.setOnClickListener(
                view -> {
                    Intent gotoChangePIN = new Intent(ViewProfile.this, ChangePIN.class);
                    startActivity(gotoChangePIN);
                }
        );

        tvDriverDetails.setOnClickListener(
                view -> {
                    Intent gotoDD = new Intent(ViewProfile.this, DriverDetails.class);
                    startActivity(gotoDD);
                }
        );

        etUserType.setOnClickListener(view -> {
            Intent gotoUserType = new Intent(ViewProfile.this, UserType.class);
            gotoUserType.putExtra("userTypeList",userList);
            startActivity(gotoUserType);
        });

        etAddress.setOnClickListener(view -> {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
            View mView = layoutInflaterAndroid.inflate(R.layout.addressdialog, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
            alertDialogBuilderUserInput.setView(mView);

            final EditText etAddress = (EditText) mView.findViewById(R.id.etAddress);
            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            updateAddress(walletID,etAddress.getText().toString());
                        }
                    })

                    .setNegativeButton("Cancel",
                            (dialogBox, id) -> dialogBox.cancel());

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


            etAddress.addTextChangedListener(new TextWatcher() {

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
        });
    }

    private void updateAddress(String walletID,String address) {
        Links application = (Links) getApplication();
        String changeAddress = application.update_address;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, changeAddress,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean error = jsonResponse.getBoolean("error");
                            String message = jsonResponse.getString("message");

                            Toast.makeText(ViewProfile.this, message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            if (!error) {
                                etAddress.setText(address);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(ViewProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(ViewProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wid", walletID);
                params.put("address", address);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void viewDetails(String walletID) {
        Links application = (Links) getApplication();
        String viewDetails = application.view_details;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, viewDetails,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response",response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");

                            dialog.dismiss();

                            if (error) {
                                Toast.makeText(ViewProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                lnBack.performClick();
                            } else {
                                JSONArray arrAccess = obj.getJSONArray("result");
                                for (Integer i = 0; i < arrAccess.length(); i++) {
                                    JSONObject current_obj = arrAccess.getJSONObject(i);

                                    etFirstName.setText(current_obj.getString("firstName"));
                                    etLastName.setText(current_obj.getString("lastName"));
                                    etUserName.setText(current_obj.getString("username"));
                                    etEmail.setText(current_obj.getString("email"));
                                    etMobile.setText(current_obj.getString("mobile"));
                                    etAddress.setText(current_obj.getString("address"));

                                    JSONArray arrUserType = current_obj.getJSONArray("userTypeList");

                                    if (arrUserType.length() == 0) {
                                        isUserTypeAvailable = false;
                                    } else {
                                        for (Integer j = 0; j < arrUserType.length(); j++) {
                                            JSONObject current_UserTypeObj = arrUserType.getJSONObject(j);

                                            String userType = current_UserTypeObj.getString("usertype");
                                            String discount = current_UserTypeObj.getString("discount");
                                            Boolean isDriver = current_UserTypeObj.getBoolean("isDriver");
                                            String id = current_UserTypeObj.getString("id");

                                            userList.add(userType + "~" + discount + "~" + isDriver);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(ViewProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            lnBack.performClick();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(ViewProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        lnBack.performClick();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("walletID", walletID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume(){
        super.onResume();

        etUserType.setText(sp.getString("userType", ""));

        if (!sp.getBoolean("isDriver", false)) {
            tvDriverDetails.setVisibility(View.GONE);
        } else {
            tvDriverDetails.setVisibility(View.VISIBLE);
        }
    }
}