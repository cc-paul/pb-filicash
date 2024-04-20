package com.kwjj.filicash.rv_usertype;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwjj.filicash.R;
import com.kwjj.filicash.UserType;
import com.kwjj.filicash.ViewProfile;
import com.kwjj.filicash.rv_history.transactionAdapter;
import com.kwjj.filicash.rv_history.transactionData;

import java.util.ArrayList;

public class userAdapter extends RecyclerView.Adapter<userAdapter.MyViewHolder> {
    private ArrayList<userData> userAdapterList;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public userAdapter(ArrayList<userData> userData) {
        this.userAdapterList = userData;
    }

    @Override
    public userAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_usertype, parent, false);
        return new userAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull userAdapter.MyViewHolder holder, int position) {
        userData userData = userAdapterList.get(position);

        holder.tvUserType.setText(userData.getUserType());
        holder.tvDiscount.setText("Discount : " + userData.getDiscount() + " off");

        if (userData.getDriverNeeded()) {
            holder.lnIsDriver.setVisibility(View.VISIBLE);
        }

        holder.lnRowUser.setOnClickListener(view -> {
            sp = holder.view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
            editor = sp.edit();

            editor.putString("userTypeId",userData.getId());
            editor.putString("userType",userData.getUserType());
            editor.putBoolean("isDriver",userData.getDriverNeeded());
            editor.putString("discount",userData.getDiscount());
            editor.commit();

            if(holder.view.getContext() instanceof UserType){
                ((UserType)holder.view.getContext()).getBack();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userAdapterList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvUserType,tvDiscount,tvIsDriver;
        public final LinearLayout lnIsDriver,lnRowUser;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvUserType = view.findViewById(R.id.tvUserType);
            tvDiscount = view.findViewById(R.id.tvDiscount);
            tvIsDriver = view.findViewById(R.id.tvIsDriver);
            lnIsDriver = view.findViewById(R.id.lnIsDriver);
            lnRowUser = view.findViewById(R.id.lnRowUser);
        }
    }
}
