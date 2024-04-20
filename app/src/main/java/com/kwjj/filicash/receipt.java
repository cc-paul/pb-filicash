package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class receipt extends AppCompatActivity {
    LinearLayout lnBack,lnDownload,lnReceiptContainer;
    TextView tvLabel1,tvLabel2,tvLabel3,tvLabel4,tvLabel5,tvLabel6,tvLabel7;

    Dialog backWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        lnBack = findViewById(R.id.lnBack);
        lnDownload = findViewById(R.id.lnDownload);
        lnReceiptContainer = findViewById(R.id.lnReceiptContainer);

        tvLabel1 = findViewById(R.id.tvLabel1);
        tvLabel2 = findViewById(R.id.tvLabel2);
        tvLabel3 = findViewById(R.id.tvLabel3);
        tvLabel4 = findViewById(R.id.tvLabel4);
        tvLabel5 = findViewById(R.id.tvLabel5);
        tvLabel6 = findViewById(R.id.tvLabel6);
        tvLabel7 = findViewById(R.id.tvLabel7);

        Intent intent = getIntent();
        tvLabel1.setText(intent.getStringExtra("label1"));
        tvLabel2.setText(intent.getStringExtra("label2"));
        tvLabel3.setText(intent.getStringExtra("label3"));
        tvLabel4.setText(intent.getStringExtra("label4"));
        tvLabel5.setText(intent.getStringExtra("label5"));
        tvLabel6.setText(intent.getStringExtra("label6"));
        tvLabel7.setText(intent.getStringExtra("label7"));

        if (tvLabel6.getText().toString().equals("-")) {
            tvLabel6.setVisibility(View.GONE);
        }

        AlertDialog.Builder backWalletBuilder = new AlertDialog.Builder(this);
        backWalletBuilder.setMessage("Thank you for using Filicash")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    super.onBackPressed();
                });
        backWallet = backWalletBuilder.create();

        lnBack.setOnClickListener(
                view -> {
                    backWallet.show();
                }
        );

        lnDownload.setOnClickListener(
                view -> {
                    PermissionListener permissionlistener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            File file = saveBitMap(receipt.this,lnReceiptContainer);
                            if (file != null) {
                                Toast.makeText(receipt.this,"Receipt has been saved to your gallery",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            Toast.makeText(receipt.this, "Some permissions were denied. Unable to use this function", Toast.LENGTH_LONG).show();
                        }
                    };

                    new TedPermission(this)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("Storage is required.\n\nPlease turn on permissions at [Setting] > [Permission]")
                            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .check();
                }
        );
    }

    @Override
    public void onBackPressed() {
        backWallet.show();
    }

    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"FiliCash");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
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