package com.clothes.clothesapp.callback;

import com.clothes.clothesapp.model.Complaint;
import com.clothes.clothesapp.model.Gallery;

import java.util.ArrayList;

public interface GalleryCallback {
    void onSuccess(ArrayList<Gallery> galleries);
    void onFail(String msg);
}
