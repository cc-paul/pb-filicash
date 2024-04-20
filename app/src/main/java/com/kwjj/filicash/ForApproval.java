package com.kwjj.filicash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kwjj.filicash.imageupload.imageData;
import com.kwjj.filicash.rv_approval.imageAdapter;
import com.kwjj.filicash.rv_approval.imageDataList;
import com.kwjj.filicash.rv_history.transactionData;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForApproval extends AppCompatActivity {
    LinearLayout lnRequirements,lnSave,lnBack;
    FloatingActionButton btnAddFile;
    RecyclerView rv_images;
    TextView tvCount;
    EditText etFirstName,etLastName,etAddress,etMobile;


    ArrayList<imageData> ImageList = new ArrayList<>();
    ArrayList<imageDataList> list = new ArrayList<>();

    Integer index;
    Integer attachSize = 0;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String walletID;

    Dialog dialog,requirements,dgConfirm;

    private StorageReference mStorageRef;

    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_approval);

        lnRequirements = findViewById(R.id.lnRequirements);
        btnAddFile = findViewById(R.id.btnAddFile);
        rv_images = findViewById(R.id.rv_images);
        tvCount = findViewById(R.id.tvCount);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etAddress = findViewById(R.id.etAddress);
        etMobile = findViewById(R.id.etMobile);
        lnSave = findViewById(R.id.lnSave);
        lnBack = findViewById(R.id.lnBack);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();
        walletID = sp.getString("walletID", "Wallet ID not found");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnAddFile.setOnClickListener(
                view -> {
                    StartPermission("OnClick");
                }
        );

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String capitalizedText = WordUtils.capitalize(etFirstName.getText().toString());
                if (!capitalizedText.equals(etFirstName.getText().toString())) {
                    etFirstName.addTextChangedListener(new TextWatcher() {
                        int mStart = 0;
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            mStart = start + count;
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            etFirstName.setSelection(mStart);
                            etFirstName.removeTextChangedListener(this);
                        }
                    });
                    etFirstName.setText(capitalizedText);
                }
            }
        });

        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String capitalizedText = WordUtils.capitalize(etLastName.getText().toString());
                if (!capitalizedText.equals(etLastName.getText().toString())) {
                    etLastName.addTextChangedListener(new TextWatcher() {
                        int mStart = 0;
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            mStart = start + count;
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            etLastName.setSelection(mStart);
                            etLastName.removeTextChangedListener(this);
                        }
                    });
                    etLastName.setText(capitalizedText);
                }
            }
        });

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String capitalizedText = WordUtils.capitalize(etAddress.getText().toString());
                if (!capitalizedText.equals(etAddress.getText().toString())) {
                    etAddress.addTextChangedListener(new TextWatcher() {
                        int mStart = 0;
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            mStart = start + count;
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            etAddress.setSelection(mStart);
                            etAddress.removeTextChangedListener(this);
                        }
                    });
                    etAddress.setText(capitalizedText);
                }
            }
        });

        lnRequirements.setOnClickListener(
                view -> {
                    StringBuilder message = new StringBuilder();
                    message.append("Please submit any of the following documents");
                    message.append("\n");
                    message.append("\n");
                    message.append("Driver’s License");
                    message.append("\n");
                    message.append("Passport");
                    message.append("\n");
                    message.append("PhilHealth Card");
                    message.append("\n");
                    message.append("Philippine Postal ID");
                    message.append("\n");
                    message.append("PRC ID");
                    message.append("\n");
                    message.append("SSS ID");
                    message.append("\n");
                    message.append("UMID");
                    message.append("\n");
                    message.append("Voter’s ID");
                    message.append("\n");
                    message.append("HDMF (Pagibig) ID ");
                    message.append("\n");
                    message.append("National ID / Philsys ID");
                    message.append("\n");
                    message.append("Passport");
                    message.append("\n");

                    AlertDialog.Builder requirementsBuilder = new AlertDialog.Builder(this);
                    requirementsBuilder.setMessage(message)
                            .setCancelable(false)
                            .setTitle("Verification Requirements")
                            .setPositiveButton("OK", (dialog, id) -> {
                                requirements.dismiss();
                            });
                    requirements = requirementsBuilder.create();
                    requirements.show();
                }
        );

        lnSave.setOnClickListener(
                view -> {
                    if (etFirstName.getText().toString().equals("") || etLastName.getText().toString().equals("") || etMobile.getText().toString().equals("") || etAddress.getText().toString().equals("")) {
                        Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_LONG).show();
                    } else if (attachSize == 0) {
                        Toast.makeText(this,"Please provide the attachments needed",Toast.LENGTH_LONG).show();
                    } else if (etMobile.getText().toString().length() < 11) {
                        Toast.makeText(this,"Mobile Number is too short",Toast.LENGTH_LONG).show();
                    } else {
                        List<String> queryList = new ArrayList<>();

                        for (Integer i = 0; i < list.size(); i ++) {
                            String imgUrl = list.get(i).getImageUrl();
                            queryList.add("('"+ walletID +"','"+ imgUrl +"','tid')");
                        }

                        String query = TextUtils.join(",",queryList);
                        saveDetails(query);
                    }
                }
        );


        dialog.show();
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = mStorageRef.child("images/" + walletID + "/" + "temp.jpg");

        imageRef.putFile(Uri.parse(getURLForResource(R.drawable.ads1)))
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadPhotoUrl -> {
                        dialog.dismiss();
                        loadImages();
                    });
                })
                .addOnFailureListener(exception -> {
                    dialog.dismiss();
                    Toast.makeText(ForApproval.this,"Something went wrong. Please contact support",Toast.LENGTH_LONG).show();
                    super.onBackPressed();
                });
    }

    private void saveDetails(String query) {
        Links application = (Links) getApplication();
        String forApproval = application.for_approval;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, forApproval,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        dialog.dismiss();

                        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
                        confirmBuilder.setMessage(message)
                                .setCancelable(false)
                                .setTitle("Congratulations")
                                .setPositiveButton("OK", (dialog, id) -> {
                                    dgConfirm.dismiss();
                                    editor.putBoolean("disPlayDoNotShowRejected",true);
                                    editor.commit();

                                    if (!error) {
                                        super.onBackPressed();
                                    }
                                });
                        dgConfirm = confirmBuilder.create();
                        dgConfirm.show();

                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error",e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(ForApproval.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error",error.toString());
                    Toast.makeText(ForApproval.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("walletID", walletID);
                params.put("fName", etFirstName.getText().toString());
                params.put("lName", etLastName.getText().toString());
                params.put("mobile", etMobile.getText().toString());
                params.put("address", etAddress.getText().toString());
                params.put("query", query);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void StartPermission(String from) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(ForApproval.this, "Some permissions were denied. Unable to use this function", Toast.LENGTH_LONG).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Storage is required.\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }


    public String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ImageList.clear();

                if (data.getClipData() != null) {
                    Integer count = data.getClipData().getItemCount();

                    for (Integer i = 0; i < count; i++) {
                        Uri imageuri = data.getClipData().getItemAt(i).getUri();
                        String filename = getFileNameByUri(this, imageuri);

                        if (!ImageList.contains(imageuri)) {
                            ImageList.add(new imageData(imageuri, filename));
                        }
                    }
                } else if (data.getData() != null) {
                    Uri imageuri = data.getData();
                    String filename = getFileNameByUri(this, imageuri);
                    if (!ImageList.contains(imageuri)) {
                        ImageList.add(new imageData(imageuri, filename));
                    }
                }
            }

            //Log.e("Array", ImageList.toString());
            if (ImageList.size() != 0) {
                dialog.show();
                index = 0;
                DoTheTransfer();
            }
        }
    }

    public void DoTheTransfer() {
        Uri currentImage = ImageList.get(index).getImageuri();
        String fileName = ImageList.get(index).getFileName();
        //Log.e("File Name",fileName);

        StorageReference imageRef = mStorageRef.child("images/" + walletID + "/" + fileName);

        imageRef.putFile(currentImage)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadPhotoUrl -> {
                        Log.e("Image Link", downloadPhotoUrl.toString());

                        index++;
                        if (index < ImageList.size()) {
                            DoTheTransfer();
                        } else {
                            dialog.dismiss();
                            loadImages();
                        }
                    });
                })
                .addOnFailureListener(exception -> {
                    //dialog.dismiss();
                    Log.e("Error", "Unable to upload image");
                });
    }

    public void loadImages() {
        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images/" + walletID);

        list.clear();
        listRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference file : listResult.getItems()) {
                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!uri.toString().contains("temp.jpg")) {
                            list.add(new imageDataList(uri.toString()));
                        }
                    }
                }).addOnSuccessListener(uri -> {
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                    rv_images.setLayoutManager(mLayoutManager);

                    adapter = new imageAdapter(list);
                    rv_images.setAdapter(adapter);
                    tvCount.setText(list.size() + " Photo(s) attached");
                    attachSize = list.size();
                });
            }
        });
    }


    public static String getFileNameByUri(Context context, Uri uri) {
        String fileName = "unknown";//default fileName
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment().toString();
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            fileName = filePathUri.getLastPathSegment().toString();
        } else {
            fileName = fileName + "_" + filePathUri.getLastPathSegment();
        }
        return fileName;
    }
}