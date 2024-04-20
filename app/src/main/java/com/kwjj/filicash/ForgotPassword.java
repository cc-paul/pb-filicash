package com.kwjj.filicash;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
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
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ForgotPassword extends AppCompatActivity {
    LinearLayout lnBack,lnSave;
    EditText etCode,etNewPassword,etRepeatNewPassword;

    Intent intent;

    Dialog dialog;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        lnBack = findViewById(R.id.lnBack);
        lnSave = findViewById(R.id.lnSave);

        etCode = findViewById(R.id.etCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        etRepeatNewPassword = findViewById(R.id.etRepeatNewPassword);

        intent  = getIntent();
        Bundle extras = intent.getExtras();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        email = extras.getString("email","");

        getOtp(extras.getString("email",""));

        lnSave.setOnClickListener(view -> {
            if (etCode.getText().toString().equals("") || etNewPassword.getText().toString().equals("") || etRepeatNewPassword.getText().toString().equals("")) {
                Toast.makeText(this,"Please fill in all required fields",Toast.LENGTH_LONG).show();
            } else if (!etNewPassword.getText().toString().equals(etRepeatNewPassword.getText().toString())) {
                Toast.makeText(this,"Password does not match",Toast.LENGTH_LONG).show();
            } else if (etNewPassword.getText().toString().length() < 8 || etRepeatNewPassword.getText().toString().length() < 8) {
                Toast.makeText(this,"Password must be at least 8 characters",Toast.LENGTH_LONG).show();
            } else {
                changePassword();
            }
        });

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );
    }

    public void getOtp(String Email) {
        Links application = (Links) getApplication();
        String otp = application.otp;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, otp,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            Log.e("Login", response);

                            if (error) {
                                Toast.makeText(ForgotPassword.this, message, Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } else {
                                sendEmail(Email,message);
                            }
                        } catch (JSONException | AddressException e) {
                            e.printStackTrace();

                            Toast.makeText(ForgotPassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(ForgotPassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", Email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void sendEmail(String email,String cur_message) throws AddressException {
        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final String senderEmail = "filicashph@gmail.com";
                final String password = "Filicash_123";
                final String messageToSend = cur_message;

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, password);
                    }
                });

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(senderEmail));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                    message.setSubject("Code for Password Change");
                    message.setText(messageToSend);
                    Transport.send(message);

                    Toast.makeText(this, "OTP has been sent. Please check your Email or Spam", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } catch (MessagingException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this,"Unable to send email. Please check your Internet and try again later",Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }


    private void changePassword() {
        Links application = (Links) getApplication();
        String changePass = application.forgotpass_change;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, changePass,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            Toast.makeText(ForgotPassword.this, message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            if (!error) {
                                lnBack.performClick();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(ForgotPassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.toString()

                        Toast.makeText(ForgotPassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("otp", etCode.getText().toString());
                params.put("password", etNewPassword.getText().toString());
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}