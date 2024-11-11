package com.clothes.clothesapp.callback;

import com.clothes.clothesapp.model.Complaint;
import com.clothes.clothesapp.model.Tailor;

import java.util.ArrayList;

public interface TailorCallback {
    void onSuccess(ArrayList<Tailor> tailors);
    void onFail(String msg);
}
