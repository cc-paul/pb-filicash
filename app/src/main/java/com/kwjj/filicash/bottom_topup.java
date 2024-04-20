package com.kwjj.filicash;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class bottom_topup extends BottomSheetDialogFragment {
    String BankName;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_topup, container, false);

        ImageView img711 = v.findViewById(R.id.img711);
        ImageView imgbpi = v.findViewById(R.id.imgbpi);
        ImageView imgcoinsph = v.findViewById(R.id.imgcoinsph);
        ImageView imgcliqq = v.findViewById(R.id.imgcliqq);
        ImageView imgunionbank = v.findViewById(R.id.imgunionbank);

        TextView tv711 = v.findViewById(R.id.tv711);
        TextView tvbpi = v.findViewById(R.id.tvbpi);
        TextView tvcoinsph = v.findViewById(R.id.tvcoinsph);
        TextView tvcliqq = v.findViewById(R.id.tvcliqq);
        TextView tvunionbank = v.findViewById(R.id.tvunionbank);


        img711.setOnClickListener(view -> {
            BankName = tv711.getText().toString();

            Intent gotoTopUp = new Intent(getActivity(), topup.class);
            gotoTopUp.putExtra("BankLogo",R.drawable.logo_711);
            gotoTopUp.putExtra("BankName",BankName);
            startActivity(gotoTopUp);
            dismiss();
        });

        imgbpi.setOnClickListener(view -> {
            BankName = tvbpi.getText().toString();

            Intent gotoTopUp = new Intent(getActivity(), topup.class);
            gotoTopUp.putExtra("BankLogo",R.drawable.logo_bpi);
            gotoTopUp.putExtra("BankName",BankName);
            startActivity(gotoTopUp);
            dismiss();
        });

        imgcoinsph.setOnClickListener(view -> {
            BankName = tvcoinsph.getText().toString();

            Intent gotoTopUp = new Intent(getActivity(), topup.class);
            gotoTopUp.putExtra("BankLogo",R.drawable.logo_coinsph);
            gotoTopUp.putExtra("BankName",BankName);
            startActivity(gotoTopUp);
            dismiss();
        });

        imgcliqq.setOnClickListener(view -> {
            BankName = tvcliqq.getText().toString();

            Intent gotoTopUp = new Intent(getActivity(), topup.class);
            gotoTopUp.putExtra("BankLogo",R.drawable.logo_cliqq);
            gotoTopUp.putExtra("BankName",BankName);
            startActivity(gotoTopUp);
            dismiss();
        });

        imgunionbank.setOnClickListener(view -> {
            BankName = tvunionbank.getText().toString();

            Intent gotoTopUp = new Intent(getActivity(), topup.class);
            gotoTopUp.putExtra("BankLogo",R.drawable.logo_ub);
            gotoTopUp.putExtra("BankName",BankName);
            startActivity(gotoTopUp);
            dismiss();
        });

        return v;
    }
}
