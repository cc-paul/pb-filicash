package com.kwjj.filicash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static androidx.appcompat.app.AlertDialog.*;

public class Wallet extends AppCompatActivity {
    LinearLayout lnTopUp, lnTransfer, lnReceive, lnCashOut, lnHistory;
    TextView tvAmount,tvVehicleName,tvPlateNumber,tvConductorsName;
    ImageView imgAds1,imgAds2,imgAds3,imgAds4,imgAds5,imgAds6,imgScanFare;
    CardView crdDriverDetails;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String walletID;
    String command;
    Boolean isDriver;

    public static final int GALLERY = 0;

    Dialog dialog;

    ArrayList<String> adsImageList1 = new ArrayList<>();
    ArrayList<String> adsImageList2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        lnTopUp = findViewById(R.id.lnTopUp);
        lnTransfer = findViewById(R.id.lnTransfer);
        lnReceive = findViewById(R.id.lnReceive);
        lnCashOut = findViewById(R.id.lnCashOut);
        lnHistory = findViewById(R.id.lnHistory);
        tvAmount = findViewById(R.id.tvAmount);
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvPlateNumber = findViewById(R.id.tvPlateNumber);
        tvConductorsName = findViewById(R.id.tvConductorsName);
        imgAds1 = findViewById(R.id.imgAds1);
        imgAds2 = findViewById(R.id.imgAds2);
        imgAds3 = findViewById(R.id.imgAds3);
        imgAds4 = findViewById(R.id.imgAds4);
        imgAds5 = findViewById(R.id.imgAds5);
        imgAds6 = findViewById(R.id.imgAds6);
        imgScanFare = findViewById(R.id.imgScanFare);
        crdDriverDetails = findViewById(R.id.crdDriverDetails);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        walletID = sp.getString("walletID", "Wallet ID not found");
        isDriver = sp.getBoolean("isDriver", false);

        lnTopUp.setOnClickListener(
                view -> {
                    bottom_topup bottomSheet = new bottom_topup();
                    bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
                }
        );

        lnTransfer.setOnClickListener(
                view -> {
                    bottom_transfer bottomSheet = new bottom_transfer();
                    bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
                }
        );

        lnReceive.setOnClickListener(
                view -> {
                    command = "receive";
                    bottom_receive bottomSheet = new bottom_receive();
                    bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
                }
        );

        lnCashOut.setOnClickListener(
                view -> {
                    bottom_cashout bottomSheet = new bottom_cashout();
                    bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
                }
        );

        lnHistory.setOnClickListener(
                view -> {
                    Intent gotoHistory = new Intent(Wallet.this, history.class);
                    gotoHistory.putExtra("amount",tvAmount.getText().toString());
                    startActivity(gotoHistory);
                }
        );

        imgScanFare.setOnClickListener(
                view -> {
                    command = "qrfare";
                    ReadQRFromCamera();
                }
        );

        getWalletDetails();

        Builder builder = new Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        Fresco.initialize(this);
        adsImageList1.add(getURLForResource(R.drawable.ads1));
        adsImageList1.add(getURLForResource(R.drawable.ads2));
        adsImageList1.add(getURLForResource(R.drawable.ads5));
        adsImageList2.add(getURLForResource(R.drawable.ads3));
        adsImageList2.add(getURLForResource(R.drawable.ads4));
        adsImageList2.add(getURLForResource(R.drawable.ads6));

        imgAds1.setOnClickListener(
                view -> {
                    new ImageViewer.Builder(this, adsImageList1)
                            .setStartPosition(0)
                            .show();
                }
        );

        imgAds2.setOnClickListener(
                view -> {
                    new ImageViewer.Builder(this, adsImageList1)
                            .setStartPosition(1)
                            .show();
                }
        );

        imgAds3.setOnClickListener(
                view -> {
                    new ImageViewer.Builder(this, adsImageList1)
                            .setStartPosition(2)
                            .show();
                }
        );

        imgAds4.setOnClickListener(
                view -> {
                    new ImageViewer.Builder(this, adsImageList2)
                            .setStartPosition(0)
                            .show();
                }
        );

        imgAds5.setOnClickListener(
                view -> {
                    new ImageViewer.Builder(this, adsImageList2)
                            .setStartPosition(1)
                            .show();
                }
        );

        imgAds6.setOnClickListener(
                view -> {
                    new ImageViewer.Builder(this, adsImageList2)
                            .setStartPosition(2)
                            .show();
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        getWalletDetails();
    }

    public String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }

    public void getWalletDetails() {
        Links application = (Links) getApplication();
        String wallet_ref = application.wallet_ref;

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

                                String amount = current_obj.getString("amount");
                                String[] driverDetails = current_obj.getString("driverDetails").split("~:");

                                editor.putString("amount", amount);
                                editor.commit();
                                tvAmount.setText("PHP " + amount);
                                tvVehicleName.setText(driverDetails[0]);
                                tvPlateNumber.setText(driverDetails[1]);
                                tvConductorsName.setText(driverDetails[2]);

                                Log.e("Vehicle",driverDetails[0]);

                                if (isDriver && !driverDetails[0].equals("-")) {
                                    crdDriverDetails.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            Toast.makeText(Wallet.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error", e.getMessage());
                        Toast.makeText(Wallet.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error", error.toString());
                    Toast.makeText(Wallet.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
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

    public void processQR(String process) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (process.equals("upload")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY);
                } else {
                    ReadQRFromCamera();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(Wallet.this, "Some permissions were denied. Unable to use this function", Toast.LENGTH_LONG).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Camera and Storage are required.\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                ReadQRFromGallery(data.getData());
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result.getContents() != null) {
                if (command.equals("receive")) {
                    ProcessQR(result.getContents());
                } else {
                    ProcessQRFare(result.getContents());
                }
            }
        }
    }

    public void ReadQRFromCamera(){
//        IntentIntegrator integrator = new IntentIntegrator(Wallet.this);
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
//        integrator.setOrientationLocked(false);
//        integrator.setCameraId(0);
//        integrator.setBeepEnabled(true);
//        integrator.setBarcodeImageEnabled(false);

        IntentIntegrator integrator = new IntentIntegrator(Wallet.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();

        integrator.initiateScan();
    }

    public void ReadQRFromGallery(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                Log.e("TAG", "uri is not a bitmap," + uri.toString());
                return;
            }

            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();
            bitmap = null;
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();

            try {
                Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<>();
                decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                Result result = reader.decode(bBitmap);

                ProcessQR(result.getText());
            } catch (NotFoundException e) {
                Log.e("TAG", "decode exception", e);
                Toast.makeText(this, "Unable to read QR.", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            Log.e("TAG", "can not open file" + uri.toString(), e);
        }
    }

    public void ProcessQRFare (String data) {
        try {
            //ref + "~" + walletID + "~" + locationIDFrom + "~" + locationIDTo + "~" + total;

            String[] parameters = data.split("~:");
            String ref = parameters[0];
            String locationIDFrom = parameters[2];
            String locationIDTo = parameters[3];
            String total = parameters[4];
            String walletIDSender = parameters[1];
            String walletIDReceiver = walletID;

            dialog.show();

            Links application = (Links) getApplication();
            String get_fare = application.get_fare;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, get_fare,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");

                            dialog.dismiss();
                            Toast.makeText(Wallet.this, message, Toast.LENGTH_LONG).show();
                            getWalletDetails();

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.e("Error",e.getMessage());
                            dialog.dismiss();
                            Toast.makeText(Wallet.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        // error.toString()
                        Log.e("Error",error.toString());
                        dialog.dismiss();
                        Toast.makeText(Wallet.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("ref", ref);
                    params.put("locationIDFrom", locationIDFrom);
                    params.put("locationIDTo",locationIDTo);
                    params.put("total", total);
                    params.put("walletIDSender", walletIDSender);
                    params.put("walletIDReceiver", walletIDReceiver);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Error Y",e.getMessage());
            Toast.makeText(Wallet.this, "Unable to process QR", Toast.LENGTH_LONG).show();
        }
    }

    public void ProcessQR (String data) {
        try {
            String[] parameters = data.split("~:");
            String ref = parameters[0];
            String wid_from = parameters[1];
            String amount = parameters[2];

            if (wid_from.equals(walletID)) {
                Toast.makeText(Wallet.this, "Unable to receive the QR you generated", Toast.LENGTH_LONG).show();
            } else {
                Links application = (Links) getApplication();
                String receiveApi = application.qr_receive;

                dialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, receiveApi,
                        response -> {
                            try {
                                JSONObject obj = new JSONObject(response);
                                Boolean error = obj.getBoolean("error");
                                String message = obj.getString("message");
                                String date = obj.getString("date");
                                String cur_amount = obj.getString("amount");
                                Log.e("Response", response);

                                dialog.dismiss();

                                if (error) {
                                    Toast.makeText(Wallet.this, message, Toast.LENGTH_LONG).show();
                                } else {
                                    Intent goToReceipt = new Intent(Wallet.this, receipt.class);
                                    goToReceipt.putExtra("label1", "Amount successfully received");
                                    goToReceipt.putExtra("label2", wid_from);
                                    goToReceipt.putExtra("label3", "PHP " + cur_amount);
                                    goToReceipt.putExtra("label4", "Reference Number");
                                    goToReceipt.putExtra("label5", ref);
                                    goToReceipt.putExtra("label6", "-");
                                    goToReceipt.putExtra("label7", date);
                                    startActivity(goToReceipt);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                                Log.e("Error", e.getMessage());
                                dialog.dismiss();
                                Toast.makeText(Wallet.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                            }
                        },
                        error -> {
                            // error.toString()
                            Log.e("Error", error.toString());
                            Toast.makeText(Wallet.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("ref", ref);
                        params.put("wid_from", wid_from);
                        params.put("wid_to", walletID);
                        params.put("amount", amount);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
        } catch (Exception e) {
            Toast.makeText(Wallet.this, "Unable to process QR", Toast.LENGTH_LONG).show();
        }
    }
}