package com.clothes.clothesapp.callback;

import com.clothes.clothesapp.model.Complaint;
import com.clothes.clothesapp.model.Order;

import java.util.ArrayList;

public interface OrderCallback {
    void onSuccess(ArrayList<Order> orders);
    void onFail(String msg);
}
