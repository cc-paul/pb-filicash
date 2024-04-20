package com.kwjj.filicash.rv_location;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwjj.filicash.LocationList;
import com.kwjj.filicash.MainActivity;
import com.kwjj.filicash.R;
import com.kwjj.filicash.UserType;
import com.kwjj.filicash.rv_history.transactionAdapter;
import com.kwjj.filicash.rv_history.transactionData;

import java.util.ArrayList;

public class locationAdapter extends RecyclerView.Adapter<locationAdapter.MyViewHolder> {

    Activity activity;
    private ArrayList<locationData> locationDataList;


    public locationAdapter(ArrayList<locationData> locationData) {
        this.locationDataList = locationData;
    }

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    public locationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_location, parent, false);
        return new locationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull locationAdapter.MyViewHolder holder, int position) {
        locationData locationData = locationDataList.get(position);

        holder.tvLocation.setText(locationData.getLocation());

        holder.lnRowUser.setOnClickListener(view -> {
            sp = holder.view.getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
            editor = sp.edit();

            editor.putString("id", "" + locationData.getId());
            editor.putString("location",locationData.getLocation());
            editor.putString("price",locationData.getPrice().toString());
            editor.commit();

            if(holder.view.getContext() instanceof LocationList){
                ((LocationList)holder.view.getContext()).getBack();
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvLocation;
        public final LinearLayout lnRowUser;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvLocation = view.findViewById(R.id.tvLocation);
            lnRowUser = view.findViewById(R.id.lnRowUser);
        }
    }
}
