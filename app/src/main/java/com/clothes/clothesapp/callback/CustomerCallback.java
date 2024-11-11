package com.clothes.clothesapp.callback;

import com.clothes.clothesapp.model.Complaint;
import com.clothes.clothesapp.model.Customer;

import java.util.ArrayList;

public interface CustomerCallback {
    void onSuccess(ArrayList<Customer> customers);
    void onFail(String msg);
}
