package com.kwjj.filicash;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class bottom_receive extends BottomSheetDialogFragment {
    String BankName;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_receive, container, false);

        LinearLayout lnScanQR = v.findViewById(R.id.lnScanQR);
        LinearLayout lnUploadQR = v.findViewById(R.id.lnUploadQR);

        lnUploadQR.setOnClickListener(
                view -> {
                    dismiss();

                    if(getActivity() instanceof Wallet){
                        ((Wallet)getActivity()).processQR("upload");
                    }
                }
        );

        lnScanQR.setOnClickListener(
                view -> {
                    dismiss();

                    if(getActivity() instanceof Wallet){
                        ((Wallet)getActivity()).processQR("scan");
                    }
                }
        );

        return v;
    }
}
