package com.clothes.clothesapp.callback;

import com.clothes.clothesapp.model.Complaint;

import java.util.ArrayList;

public interface ComplaintCallback {
    void onSuccess(ArrayList<Complaint> complaints);
    void onFail(String msg);
}
