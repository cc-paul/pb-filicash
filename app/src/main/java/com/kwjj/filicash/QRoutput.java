package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRoutput extends AppCompatActivity {
    LinearLayout lnBack,lnGenerateQR,lnQRContainer;
    TextView tvAccountNumber,tvAmount,tvNote;
    ImageView imgQR;

    private Bitmap bitmap;

    Dialog dialog,downloadQRBuilder;

    String walletID,amount,historyRef;

    Boolean isDeduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_routput);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        lnBack = findViewById(R.id.lnBack);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvAmount = findViewById(R.id.tvAmount);
        imgQR = findViewById(R.id.imgQR);
        lnGenerateQR = findViewById(R.id.lnGenerateQR);
        lnQRContainer = findViewById(R.id.lnQRContainer);
        tvNote = findViewById(R.id.tvNote);

        walletID = getIntent().getStringExtra("walletID");
        amount = getIntent().getStringExtra("amount");
        String note = getIntent().getStringExtra("note");
        isDeduct = getIntent().getBooleanExtra("deduct",false);
        historyRef = getIntent().getStringExtra("historyRef");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        tvAccountNumber.setText("Account Number : " + walletID);
        tvAmount.setText("Amount : " + currencyFormat(amount).replace(".00",""));
        tvNote.setText(note);
        generateQR(walletID + "~:" + amount);

        lnGenerateQR.setOnClickListener(view -> {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    if (isDeduct) {
                        deductAmount(walletID,amount);
                    } else {
                        generateQR(historyRef + "~:" + walletID + "~:" + amount);

                        File file = saveBitMap(QRoutput.this,lnQRContainer);
                        if (file == null) {
                            Toast.makeText(QRoutput.this,"Error downloading the QR. Please report it to Customer Service",Toast.LENGTH_LONG).show();
                        } else {
                            AlertDialog.Builder downloadQR = new AlertDialog.Builder(QRoutput.this);
                            downloadQR.setMessage("The QR has been downloaded. You can check it at the Filicash Folder on your Pictures")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, id) -> {
                                        lnBack.performClick();
                                    });
                            downloadQRBuilder = downloadQR.create();
                            downloadQRBuilder.show();
                        }
                    }
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(QRoutput.this, "Some permissions were denied. Unable to use this function", Toast.LENGTH_LONG).show();
                }
            };

            new TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("Storage is required.\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        });

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );
    }

    public void deductAmount(String walletID,String amount) {
        Links application = (Links) getApplication();
        String QRDeduc = application.qr_deduct;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, QRDeduc,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");
                        String ref = obj.getString("ref");

                        dialog.dismiss();

                        if (error) {
                            Toast.makeText(QRoutput.this,message,Toast.LENGTH_LONG).show();
                        } else {


                            generateQR(ref + "~:" + walletID + "~:" + amount);

                            File file = saveBitMap(this,lnQRContainer);
                            if (file == null) {
                                Toast.makeText(QRoutput.this,"Error downloading the QR. Please report it to Customer Service",Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog.Builder downloadQR = new AlertDialog.Builder(this);
                                downloadQR.setMessage("The QR has been downloaded. You can check it at the Filicash Folder on your Pictures")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog, id) -> {
                                            lnBack.performClick();
                                        });
                                downloadQRBuilder = downloadQR.create();
                                downloadQRBuilder.show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error",e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(QRoutput.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    Toast.makeText(QRoutput.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wid", walletID);
                params.put("amount", amount);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"FiliCash");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            Toast.makeText(QRoutput.this,"Can't create directory to save the image",Toast.LENGTH_LONG).show();
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis()+".jpg";
        Log.e("ATG", filename);
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
            Toast.makeText(QRoutput.this,"" + e.getMessage(),Toast.LENGTH_LONG).show();
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);

        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            bgDrawable.draw(canvas);
        }   else{
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);
        return returnedBitmap;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, (path1, uri) -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}