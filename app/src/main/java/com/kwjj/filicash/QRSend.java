package com.kwjj.filicash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRSend extends AppCompatActivity {
    ImageView imgQR;
    EditText etAmount;
    LinearLayout lnGenerateQR,lnBack;
    TextView tvAmount;

    private Bitmap bitmap;

    String walletID,amount;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_send);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);

        imgQR = findViewById(R.id.imgQR);
        etAmount = findViewById(R.id.etAmount);
        lnGenerateQR = findViewById(R.id.lnGenerateQR);
        tvAmount = findViewById(R.id.tvAmount);
        lnBack = findViewById(R.id.lnBack);

        amount = sp.getString("amount", "0");
        tvAmount.setText("PHP " + amount);

        lnGenerateQR.setOnClickListener(view -> {
            if (etAmount.getText().toString().equals("")) {
                Toast.makeText(this, "Please add an amount", Toast.LENGTH_LONG).show();
            } else if (Double.parseDouble(amount.replace(",","")) < Double.parseDouble(etAmount.getText().toString())) {
                Toast.makeText(this, "The amount you can generate is PHP" + amount, Toast.LENGTH_LONG).show();
            } else {
                walletID = sp.getString("walletID","NOWALLETID");

                Intent gotoQRGen = new Intent(QRSend.this, QRoutput.class);
                gotoQRGen.putExtra("walletID",walletID);
                gotoQRGen.putExtra("amount",etAmount.getText().toString());
                gotoQRGen.putExtra("note","This QR is intended to be scanned by the receiver");
                gotoQRGen.putExtra("deduct",true);
                gotoQRGen.putExtra("historyRef","");
                startActivity(gotoQRGen);
                finish();
            }
        });

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        etAmount.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String text = arg0.toString();
                if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                    etAmount.setText(text.substring(0, text.length() - 1));
                    etAmount.setSelection(etAmount.getText().length());
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }
}