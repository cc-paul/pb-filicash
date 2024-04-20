package com.kwjj.filicash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    Switch swRoundTrip;
    ImageView imgMenu,imgQR,imgSwap;
    LinearLayout lnMenu,lnBookNow,lnTransfoDetails,lnQR;
    TextView tvDateToday,tvTime,tvDeparture,tvArrival,tvFromAndTo,tvBook,tvPrice;
    Double discount;
    Double priceFrom;
    Double priceTo;
    Boolean times2 = false;
    Boolean isBooked = true;

    Dialog dialog,notVerified;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String locationStatus = "";

    String walletID;
    String isVerified;
    String fullName;
    String locationIDFrom;
    String locationIDTo;
    String amount = "";
    String checkRef = "";
    Boolean disPlayDoNotShowRejected;
    Boolean disPlayDoNotShowApproved;

    CountDownTimer waitTimer;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgQR = findViewById(R.id.imgQR);
        imgSwap = findViewById(R.id.imgSwap);
        imgMenu = findViewById(R.id.imgMenu);
        lnMenu = findViewById(R.id.lnMenu);
        lnBookNow = findViewById(R.id.lnBookNow);
        lnQR = findViewById(R.id.lnQR);
        lnTransfoDetails = findViewById(R.id.lnTransfoDetails);
        tvDateToday = findViewById(R.id.tvDateToday);
        tvTime = findViewById(R.id.tvTime);
        tvBook = findViewById(R.id.tvBook);
        tvDeparture = findViewById(R.id.tvDeparture);
        tvArrival = findViewById(R.id.tvArrival);
        tvFromAndTo = findViewById(R.id.tvFromAndTo);
        tvPrice = findViewById(R.id.tvPrice);
        swRoundTrip = findViewById(R.id.swRoundTrip);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        walletID = sp.getString("walletID","Wallet ID not found");
        isVerified = sp.getInt("isVerified",0) == 1 ? "Verified" : "Not Verified";
        fullName = sp.getString("fullName","");
        discount = Double.parseDouble(sp.getString("discount","0"));

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        disPlayDoNotShowApproved = sp.getBoolean("disPlayDoNotShowApproved",true);
        disPlayDoNotShowRejected = sp.getBoolean("disPlayDoNotShowRejected",true);

        editor.putString("location","");
        editor.commit();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        imgMenu.setOnClickListener(
                view -> {
                    Intent gotProfile = new Intent(MainActivity.this, Profile.class);
                    startActivity(gotProfile);
                    /* Pangit yung animation palitan */
                    //overridePendingTransition(R.anim.enter, R.anim.exit);
                }
        );

        lnMenu.setOnClickListener(
                view -> {
                    Intent gotProfile = new Intent(MainActivity.this, Profile.class);
                    startActivity(gotProfile);
                    /* Pangit yung animation palitan */
                    //overridePendingTransition(R.anim.enter, R.anim.exit);
                }
        );

        tvDateToday.setText(getDateTime("EEE, dd MMM YYYY"));

        tvDeparture.setOnClickListener(view -> {
            locationStatus = "from";

            Intent gotoLocation = new Intent(MainActivity.this, LocationList.class);
            gotoLocation.putExtra("from",tvArrival.getText().toString());
            gotoLocation.putExtra("to",tvDeparture.getText().toString());
            startActivity(gotoLocation);
        });

        tvArrival.setOnClickListener(view -> {
            locationStatus = "to";

            Intent gotoLocation = new Intent(MainActivity.this, LocationList.class);
            gotoLocation.putExtra("from",tvArrival.getText().toString());
            gotoLocation.putExtra("to",tvDeparture.getText().toString());
            startActivity(gotoLocation);
        });

        swRoundTrip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            times2 = !times2;
        });

        lnBookNow.setOnClickListener(view -> {
            getWalletDetails();
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tvTime.setText(getDateTime("hh:mm a"));
            }
        }, 0, 1000);

        checkAccount();
    }

    private String getDateTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void checkAccount() {
        Links application = (Links) getApplication();
        String verificationApi = application.verification_checker_main;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, verificationApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");
                        Log.e("Response",response);

                        if (error) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrAccess = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrAccess.length(); i++) {
                                JSONObject current_obj = arrAccess.getJSONObject(i);

                                Integer verified = current_obj.getInt("isVerified");
                                String status =  current_obj.getString("status");
                                String title,dmessage;

                                title = "";
                                dmessage = "";

                                if (!status.equals("Not Sent")) {
                                    if (!status.equals("Pending")) {
                                        if (status.equals("Approved")) {
                                            /* Verified */
                                            title = "Congratulations!";
                                            dmessage = "Your request has been approved. You may now use the full feature of this app.";

                                            dialog.dismiss();
                                            if (disPlayDoNotShowApproved) {
                                                showMessage(title, dmessage, status);
                                            }
                                        } else if (status.equals("Rejected")) {
                                            /* Rejected */
                                            title = "Were Sorry";
                                            dmessage = "Your request has been rejected. Kindly check the attachments you uploaded and try a new one.";

                                            dialog.dismiss();
                                            if (disPlayDoNotShowRejected) {
                                                showMessage(title, dmessage, status);
                                            }
                                        }

                                    } else {
                                        dialog.dismiss();
                                    }
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error",e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wid", walletID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getWalletDetails() {
        Links application = (Links) getApplication();
        String wallet_ref = application.wallet_ref;
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, wallet_ref,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        if (!error) {
                            JSONArray arrWallet = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrWallet.length(); i++) {
                                JSONObject current_obj = arrWallet.getJSONObject(i);

                                amount = current_obj.getString("amount");

                                if (isBooked) {
                                    if (tvArrival.getText().toString().equals("Select Route") || tvDeparture.getText().toString().equals("Select Route")) {
                                        Toast.makeText(this,"Please select arrival and departure",Toast.LENGTH_LONG).show();
                                    } else {

                                        int timesAmount = times2 ? 2 : 0;


                                        Double total = priceFrom + priceTo * timesAmount;
                                        total = total - (total * discount / 100);

                                        if (Double.parseDouble(amount.replaceAll(",","")) < total) {
                                            Toast.makeText(this,"Insufficient Amount. Your balance is " + amount,Toast.LENGTH_LONG).show();
                                        } else {
                                            tvBook.setText("Cancel Booking");

                                            tvPrice.setText("₱" + total.toString().replace(".0",""));
                                            lnQR.setVisibility(View.VISIBLE);

                                            tvDeparture.setEnabled(false);
                                            tvArrival.setEnabled(false);
                                            swRoundTrip.setEnabled(false);


                                            long ref = System.currentTimeMillis();
                                            String key = ref + "~:" + walletID + "~:" + locationIDFrom + "~:" + locationIDTo + "~:" + total;

                                            generateQR(key);
                                            checkRef = "" + ref;
                                            startChecking();

                                            isBooked = !isBooked;
                                        }
                                    }
                                } else {
                                    tvBook.setText("Start Booking");

                                    tvDeparture.setEnabled(true);
                                    tvArrival.setEnabled(true);
                                    swRoundTrip.setEnabled(true);
                                    lnQR.setVisibility(View.GONE);

                                    if(waitTimer != null) {
                                        waitTimer.cancel();
                                        waitTimer = null;
                                    }

                                    isBooked = !isBooked;
                                }
                            }

                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        dialog.dismiss();
                        Log.e("Error", e.getMessage());
                        Toast.makeText(MainActivity.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    dialog.dismiss();
                    Log.e("Error", error.toString());
                    Toast.makeText(MainActivity.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wid", walletID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void showMessage(String title,String dmessage,String status) {
        AlertDialog.Builder notVerifiedBuilder = new AlertDialog.Builder(this);
        notVerifiedBuilder
                .setTitle(title)
                .setMessage(dmessage)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    if (status.equals("Approved")) {
                        editor.putBoolean("disPlayDoNotShowApproved",false);
                    } else {
                        editor.putBoolean("disPlayDoNotShowRejected",false);
                    }
                    editor.commit();
                    Log.e("Message", String.valueOf(sp.getBoolean("disPlayDoNotShowRejected",true)));
                    notVerified.dismiss();
                });
        notVerified = notVerifiedBuilder.create();
        notVerified.show();
    }

    public void setLocation(String location) {
        if (!location.equals("")) {
            if (locationStatus.equals("from")) {
                SpannableString content = new SpannableString(location);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                tvDeparture.setText(content);
                priceFrom = Double.parseDouble(sp.getString("price","0"));
                locationIDFrom = sp.getString("id","0");
            } else {
                SpannableString content = new SpannableString(location);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                tvArrival.setText(content);
                priceTo = Double.parseDouble(sp.getString("price","0"));
                locationIDTo = sp.getString("id","0");
            }

            if (!tvDeparture.getText().toString().equals("Select Route") && !tvArrival.getText().toString().equals("Select Route")) {
                tvFromAndTo.setText(tvDeparture.getText().toString() + " to " + tvArrival.getText().toString());
                tvFromAndTo.setVisibility(View.VISIBLE);
            }
        }
    }

    public void generateQR(String data) {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, smallerDimension);
        qrgEncoder.setColorBlack(Color.parseColor("#0D5AB2"));
        //qrgEncoder.setColorBlack(Color.parseColor("#0D5AB2"));
        qrgEncoder.setColorWhite(Color.WHITE);
        bitmap = qrgEncoder.getBitmap();
        imgQR.setImageBitmap(bitmap);
    }

    public void startChecking() {
        waitTimer = new CountDownTimer(60000, 1500) {

            public void onTick(long millisUntilFinished) {
                checkIfScan();
            }

            public void onFinish() {
                if(waitTimer != null) {
                    waitTimer.cancel();
                    waitTimer = null;

                    if (isBooked) {
                        lnBookNow.performClick();
                    }
                }
            }
        }.start();
    }

    public void checkIfScan() {
        Links application = (Links) getApplication();
        String isScanned = application.isScanned;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, isScanned,
                response -> {
                    try {
                        Log.e("Is Scanned",response);

                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");

                        if (!error) {
                            if(waitTimer != null) {
                                waitTimer.cancel();
                                waitTimer = null;
                            }

                            dialog.show();

                            final Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                lnQR.setVisibility(View.GONE);
                                tvDeparture.setText("Set Route");
                                tvArrival.setText("Set Route");
                                lnBookNow.performClick();
                                tvFromAndTo.setVisibility(View.GONE);
                                dialog.dismiss();
                                swRoundTrip.setChecked(false);

                                runOnUiThread(() -> Toast.makeText(MainActivity.this,"Payment has been completed",Toast.LENGTH_LONG).show());
                            }, 2000);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error.toString()
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ref", checkRef);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume(){
        super.onResume();

        setLocation(sp.getString("location","Set Route"));

    }

    public void onDestroy() {
        super.onDestroy();
    }
}