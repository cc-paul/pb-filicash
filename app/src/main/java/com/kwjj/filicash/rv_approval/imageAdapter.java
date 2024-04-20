package com.kwjj.filicash.rv_approval;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kwjj.filicash.ForApproval;
import com.kwjj.filicash.R;
import com.kwjj.filicash.rv_history.transactionAdapter;
import com.kwjj.filicash.rv_history.transactionData;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;


public class imageAdapter extends RecyclerView.Adapter<imageAdapter.MyViewHolder> {
    private ArrayList<imageDataList> imageDataList;
    Dialog imageOption,dialogLoad;

    public imageAdapter(ArrayList<imageDataList> imageData) {
        this.imageDataList = imageData;
    }

    @Override
    public imageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_images, parent, false);
        Fresco.initialize(itemView.getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        builder.setView(R.layout.progress);
        dialogLoad = builder.create();
        dialogLoad.setCancelable(false);


        return new imageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull imageAdapter.MyViewHolder holder, int position) {
        imageDataList imageData = imageDataList.get(position);
        Glide.with(holder.view.getContext()).load(imageData.imageUrl).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.imgDocu);

        holder.imgDocu.setOnClickListener(
                view ->  {
                    AlertDialog.Builder imageOptionBuilder = new AlertDialog.Builder(holder.view.getContext());
                    imageOptionBuilder.setMessage("What do you want to do with this image?")
                            .setCancelable(true)
                            .setPositiveButton("View", (dialog, id) -> {
                                ArrayList<String> imageList = new ArrayList<>();

                                for (Integer i = 0;i < imageDataList.size(); i++) {
                                    imageList.add(imageDataList.get(i).imageUrl);
                                }

                                new ImageViewer.Builder(holder.view.getContext(), imageList)
                                        .setStartPosition(position)
                                        .show();
                            })
                            .setNegativeButton("Delete", (dialog, id) -> {
                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(imageData.imageUrl);

                                dialogLoad.show();

                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialogLoad.dismiss();

                                        if(holder.view.getContext() instanceof ForApproval){
                                            ((ForApproval)holder.view.getContext()).loadImages();
                                        }
                                    }
                                });
                            });
                    imageOption = imageOptionBuilder.create();
                    imageOption.show();
                }
        );
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView imgDocu;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;

            imgDocu = view.findViewById(R.id.imgDocu);
        }
    }
}
