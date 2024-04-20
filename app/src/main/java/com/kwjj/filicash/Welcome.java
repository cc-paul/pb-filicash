package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Welcome extends AppCompatActivity {
    LinearLayout lnSignUp,lnLogin;
    ImageView imgFB,imgTwitter,imgInstagram;
    TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        lnSignUp = findViewById(R.id.lnSignUp);
        lnLogin = findViewById(R.id.lnLogin);

        imgFB = findViewById(R.id.imgFB);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgInstagram = findViewById(R.id.imgInstagram);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        lnSignUp.setOnClickListener(
            view -> {
                Intent gotoTerms = new Intent(Welcome.this, Terms.class);
                startActivity(gotoTerms);
            }
        );

        tvForgotPassword.setOnClickListener(
                view -> {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                    View mView = layoutInflaterAndroid.inflate(R.layout.email_dialog, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
                    alertDialogBuilderUserInput.setView(mView);

                    final EditText etEmail =  mView.findViewById(R.id.etEmail);
                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    if (!validateEmail(etEmail.getText().toString())) {
                                        Toast.makeText(Welcome.this,"Please provide proper email address",Toast.LENGTH_LONG).show();
                                    } else {
                                        Intent gotoForgot = new Intent(Welcome.this, ForgotPassword.class);
                                        gotoForgot.putExtra("email",etEmail.getText().toString());
                                        startActivity(gotoForgot);
                                    }
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


                    etEmail.addTextChangedListener(new TextWatcher() {

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
                }
        );

        lnLogin.setOnClickListener(
            view -> {
                Intent gotoLogin = new Intent(Welcome.this, Login.class);
                startActivity(gotoLogin);
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
    }

    private boolean validateEmail(String data) {
        Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher emailMatcher = emailPattern.matcher(data);
        return emailMatcher.matches();
    }
}