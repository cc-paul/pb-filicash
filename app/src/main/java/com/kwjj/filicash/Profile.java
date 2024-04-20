package com.kwjj.filicash;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.kwjj.filicash.rv_approval.imageAdapter;
import com.kwjj.filicash.rv_approval.imageDataList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    TextView tvFullName, tvWalletID, tvIsVerified;

    String walletID;
    String isVerified;
    String fullName;
    String password;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    ImageView imgFB, imgTwitter, imgInstagram, imgCopy;

    CardView crdTerms, crdWallet, crdProfile, crdHistory;

    LinearLayout lnRef, lnVerification, lnProfileSettings;

    TextView tvChangePassword,tvChangePIN;

    ImageView imgExit;

    Dialog dialog;

    Integer deleteIndex;
    ArrayList<String> imageList = new ArrayList<>();

    private ClipData myClip;
    private ClipboardManager myClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        editor = sp.edit();

        tvFullName = findViewById(R.id.tvFullName);
        tvWalletID = findViewById(R.id.tvWalletID);
        tvIsVerified = findViewById(R.id.tvIsVerified);
        tvChangePIN = findViewById(R.id.tvChangePIN);

        imgFB = findViewById(R.id.imgFB);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgInstagram = findViewById(R.id.imgInstagram);
        imgCopy = findViewById(R.id.imgCopy);

        crdTerms = findViewById(R.id.crdTerms);
        crdWallet = findViewById(R.id.crdWallet);
        crdProfile = findViewById(R.id.crdProfile);
        crdHistory = findViewById(R.id.crdHistory);

        imgExit = findViewById(R.id.imgExit);

        lnRef = findViewById(R.id.lnRef);
        lnVerification = findViewById(R.id.lnVerification);
        lnProfileSettings = findViewById(R.id.lnProfileSettings);

        tvChangePassword = findViewById(R.id.tvChangePassword);

        walletID = sp.getString("walletID", "Wallet ID not found");
        isVerified = sp.getInt("isVerified", 0) == 1 ? "Verified" : "Not Verified";
        fullName = sp.getString("fullName", "");
        password = sp.getString("password", "");

        tvWalletID.setText(walletID);
        tvIsVerified.setText(isVerified);
        tvFullName.setText(fullName);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.setCancelable(false);

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

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

        crdTerms.setOnClickListener(
                view -> {
                    Intent gotoTerms = new Intent(Profile.this, Terms.class);
                    gotoTerms.putExtra("hideRegisterButton", true);
                    startActivity(gotoTerms);
                }
        );

        crdWallet.setOnClickListener(
                view -> {
                    Intent gotoPin = new Intent(Profile.this, pin.class);
                    startActivity(gotoPin);
                }
        );

        crdHistory.setOnClickListener(
                view -> {
                    Intent gotoHistory = new Intent(Profile.this, TranspoHistory.class);
                    startActivity(gotoHistory);
                }
        );

        lnVerification.setOnClickListener(
                view -> {
                    CheckAccount();
                }
        );

        imgExit.setOnClickListener(view -> {
            Intent gotoLogin = new Intent(Profile.this, Welcome.class);
            startActivity(gotoLogin);
            finishAffinity();
        });

//        lnRef.setOnLongClickListener(v -> {
//            if (sp.getInt("isVerified",0) != 1) {
//                AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
//                alertDialog.setTitle("Hello Tester");
//                alertDialog.setMessage("For testing purposes we will set you as verified. Proceed?");
//                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
//                        (dialog, which) -> {
//                            dialog.dismiss();
//                            verifyAccount();
//                        });
//                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
//                        (dialog, which) -> dialog.dismiss());
//
//                alertDialog.show();
//            }
//            return true;
//        });

        imgCopy.setOnClickListener(
                view -> {
                    myClip = ClipData.newPlainText("text", walletID.replace("WID", ""));
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(getApplicationContext(), "Wallet ID has been copied", Toast.LENGTH_LONG).show();
                }
        );

        crdProfile.setOnClickListener(view -> {
            if (isVerified.equals("Verified")) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText etPassword = (EditText) mView.findViewById(R.id.etPassword);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if (etPassword.getText().toString().equals(password)) {
                                    //lnProfileSettings.setVisibility(View.VISIBLE);
                                    Intent gotoViewProfile = new Intent(Profile.this, ViewProfile.class);
                                    startActivity(gotoViewProfile);
                                } else {
                                    Toast.makeText(Profile.this,"Password is incorrect",Toast.LENGTH_LONG).show();
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


                etPassword.addTextChangedListener(new TextWatcher() {

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
            } else {
                Toast.makeText(Profile.this,"This feature is only available for verified users",Toast.LENGTH_LONG).show();
            }
        });

        tvChangePassword.setOnClickListener(view -> {
            Intent gotoChangepass = new Intent(Profile.this, ChangePassword.class);
            startActivity(gotoChangepass);
        });

        tvChangePIN.setOnClickListener(view -> {
            if (sp.getInt("isVerified", 0) == 1) {
                Intent gotoChangePIN = new Intent(Profile.this, ChangePIN.class);
                startActivity(gotoChangePIN);
            } else {
                Toast.makeText(Profile.this, "This feature can be use once you are verified", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void verifyAccount() {
        Links application = (Links) getApplication();
        String verify = application.verify;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, verify,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        dialog.dismiss();
                        Toast.makeText(Profile.this, message, Toast.LENGTH_LONG).show();

                        if (!error) {
                            editor.putInt("isVerified", 1);
                            editor.commit();
                            tvIsVerified.setText("Verified");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error", e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(Profile.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error", error.toString());
                    Toast.makeText(Profile.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
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


    public void CheckAccount() {
        Links application = (Links) getApplication();
        String verificationApi = application.verification_checker;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, verificationApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        if (error) {
                            dialog.dismiss();
                            Toast.makeText(Profile.this, message, Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrAccess = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrAccess.length(); i++) {
                                JSONObject current_obj = arrAccess.getJSONObject(i);

                                Integer verified = current_obj.getInt("isVerified");
                                String status = current_obj.getString("status");
                                imageList.clear();

                                if (verified == 1) {
                                    dialog.dismiss();
                                    Toast.makeText(Profile.this, "You are already verified", Toast.LENGTH_LONG).show();
                                    tvIsVerified.setText("Verified");
                                    editor.putInt("isVerified", 1);
                                    editor.commit();
                                } else if (status.equals("Pending")) {
                                    dialog.dismiss();
                                    Toast.makeText(Profile.this, "You already sent a request. Kindly wait for the verification", Toast.LENGTH_LONG).show();
                                } else {
                                    /* Delete folder */

                                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + walletID + "/temp.jpg");
                                    storageReference.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri_main) {
                                                    StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images/" + walletID);

                                                    listRef.listAll().addOnSuccessListener(listResult -> {
                                                        for (StorageReference file : listResult.getItems()) {
                                                            file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    Log.e("Link", uri.toString());
                                                                    imageList.add(uri.toString());
                                                                }
                                                            }).addOnSuccessListener(uri -> {
                                                                deleteIndex = 0;
                                                                deleteImages();
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            dialog.dismiss();
                                                            Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception_main) {
                                                    int errorCode = ((StorageException) exception_main).getErrorCode();
                                                    if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {


                                                        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                                                        StorageReference imageRef = mStorageRef.child("images/" + walletID + "/" + "temp.jpg");

                                                        imageRef.putFile(Uri.parse(getURLForResource(R.drawable.ads1)))
                                                                .addOnSuccessListener(taskSnapshot -> {
                                                                    imageRef.getDownloadUrl().addOnSuccessListener(downloadPhotoUrl -> {
                                                                        dialog.dismiss();
                                                                        Intent gotoForApproval = new Intent(Profile.this, ForApproval.class);
                                                                        startActivity(gotoForApproval);
                                                                        Log.e("Success", "File has been created");
                                                                    });
                                                                })
                                                                .addOnFailureListener(exception -> {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error", e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(Profile.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error", error.toString());
                    dialog.dismiss();
                    Toast.makeText(Profile.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
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

    public void checkVerify() {
        Links application = (Links) getApplication();
        String verificationApi = application.verification_checker;

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, verificationApi,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Boolean error = obj.getBoolean("error");
                        String message = obj.getString("message");

                        if (error) {
                            dialog.dismiss();
                            Toast.makeText(Profile.this, message, Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arrAccess = obj.getJSONArray("result");
                            for (Integer i = 0; i < arrAccess.length(); i++) {
                                JSONObject current_obj = arrAccess.getJSONObject(i);

                                Integer verified = current_obj.getInt("isVerified");
                                String status = current_obj.getString("status");
                                String fullName = current_obj.getString("fullName");
                                imageList.clear();

                                dialog.dismiss();

                                if (verified == 1) {

                                    editor.putString("walletID", walletID);
                                    editor.putInt("isVerified", 1);
                                    editor.putString("fullName", fullName);
                                    editor.commit();

                                    walletID = sp.getString("walletID", "Wallet ID not found");
                                    isVerified = sp.getInt("isVerified", 0) == 1 ? "Verified" : "Not Verified";
                                    fullName = sp.getString("fullName", "");

                                    tvWalletID.setText(walletID);
                                    tvIsVerified.setText(isVerified);
                                    tvFullName.setText(fullName);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.e("Error", e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(Profile.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // error.toString()
                    Log.e("Error", error.toString());
                    dialog.dismiss();
                    Toast.makeText(Profile.this, "Something went wrong.Please try again later", Toast.LENGTH_LONG).show();
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

    public void deleteImages() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(imageList.get(deleteIndex));

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteIndex++;
                if (deleteIndex < imageList.size()) {
                    deleteImages();
                } else {
                    dialog.dismiss();
                    Intent gotoForApproval = new Intent(Profile.this, ForApproval.class);
                    startActivity(gotoForApproval);
                }
            }
        });
    }

    public String getURLForResource(int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }

    @Override
    public void onResume() {
        super.onResume();

        checkVerify();
        lnProfileSettings.setVisibility(View.GONE);
        password = sp.getString("password", "");
    }
}