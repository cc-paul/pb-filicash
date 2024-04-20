package com.kwjj.filicash.imageupload;

import android.net.Uri;

public class imageData {
    Uri imageuri;
    String fileName;

    public imageData(Uri imageuri, String fileName) {
        this.imageuri = imageuri;
        this.fileName = fileName;
    }
    
    public Uri getImageuri() {
        return imageuri;
    }

    public void setImageuri(Uri imageuri) {
        this.imageuri = imageuri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
