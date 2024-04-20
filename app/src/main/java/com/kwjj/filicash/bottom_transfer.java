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

public class bottom_transfer extends BottomSheetDialogFragment {

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_transfer, container, false);

        LinearLayout lnGenerateQR = v.findViewById(R.id.lnGenerateQR);
        LinearLayout lnTransfer = v.findViewById(R.id.lnTransfer);

        lnGenerateQR.setOnClickListener(
                view -> {
                    Intent gotoTopQR = new Intent(getActivity(), QRSend.class);
                    startActivity(gotoTopQR);
                    dismiss();
                }
        );

        lnTransfer.setOnClickListener(
                view -> {
                    Intent gotoToSendMoney = new Intent(getActivity(), TransferMoney.class);
                    startActivity(gotoToSendMoney);
                    dismiss();
                }
        );

        return v;
    }
}
