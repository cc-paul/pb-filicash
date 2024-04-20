package com.kwjj.filicash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Terms extends AppCompatActivity {
    LinearLayout lnBack,lnGoToSignUp;
    ImageView imgFB,imgTwitter,imgInstagram;

    Intent intent;
    Boolean hideRegisterButton = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        lnBack = findViewById(R.id.lnBack);
        lnGoToSignUp = findViewById(R.id.lnGoToSignUp);

        imgFB = findViewById(R.id.imgFB);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgInstagram = findViewById(R.id.imgInstagram);

        intent  = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("hideRegisterButton")) {
                hideRegisterButton = getIntent().getExtras().getBoolean("hideRegisterButton");
            }
        }


        if (hideRegisterButton) {
            lnGoToSignUp.setVisibility(View.GONE);
        }

        lnBack.setOnClickListener(
            view -> {
                super.onBackPressed();
            }
        );

        lnGoToSignUp.setOnClickListener(
            view -> {
                Intent gotoRegister = new Intent(Terms.this, Register.class);
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
    }
}