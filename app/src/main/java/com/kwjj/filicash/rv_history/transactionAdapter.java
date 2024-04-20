package com.kwjj.filicash.rv_history;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwjj.filicash.QRSend;
import com.kwjj.filicash.QRoutput;
import com.kwjj.filicash.R;

import java.util.ArrayList;

public class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.MyViewHolder> {
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String walletID;

    private ArrayList<transactionData> transactionDataList;

    public transactionAdapter(ArrayList<transactionData> transactionData) {
        this.transactionDataList = transactionData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        transactionData transactionData = transactionDataList.get(position);
        holder.tvLabel.setText(transactionData.getLabel());
        holder.tvAmount.setText(transactionData.getAmount());
        holder.tvDate.setText(transactionData.getDate());
        holder.tvTime.setText(transactionData.getTime());
        holder.tvIsprintable.setVisibility(View.GONE);

        if (transactionData.getFlow().equals("in")) {
            holder.imgFlow.setImageResource(R.drawable.arrow_received);
        } else {


            if (transactionData.getIsPrintable() == 1) {
                holder.imgFlow.setImageResource(R.drawable.arrow_with_qr_brown_big);
            } else {
                holder.imgFlow.setImageResource(R.drawable.arrow_sent);
            }
        }




        holder.lnHistory.setOnClickListener(view -> {
            if (transactionData.getIsPrintable() == 1) {
                sp = view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
                walletID = sp.getString("walletID","NOWALLETID");

                Intent gotoQRGen = new Intent(view.getContext(), QRoutput.class);
                gotoQRGen.putExtra("walletID",walletID);
                gotoQRGen.putExtra("amount",transactionData.getAmount().replace("₱","").replace(",",""));
                gotoQRGen.putExtra("note","This QR is intended to be scanned by the receiver");
                gotoQRGen.putExtra("deduct",false);
                gotoQRGen.putExtra("historyRef",transactionData.historyRef);
                view.getContext().startActivity(gotoQRGen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvLabel,tvAmount,tvDate,tvTime,tvIsprintable;
        public final ImageView imgFlow;
        public final LinearLayout lnHistory;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvLabel = view.findViewById(R.id.tvLabel);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvDate = view.findViewById(R.id.tvDate);
            tvTime = view.findViewById(R.id.tvTime);
            imgFlow = view.findViewById(R.id.imgFlow);
            lnHistory = view.findViewById(R.id.lnHistory);
            tvIsprintable = view.findViewById(R.id.tvIsprintable);
        }
    }
}
