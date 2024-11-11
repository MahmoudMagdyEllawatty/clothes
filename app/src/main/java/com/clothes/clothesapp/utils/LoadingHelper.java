package com.clothes.clothesapp.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class LoadingHelper {
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;
    public LoadingHelper(Context context) {
        this.progressDialog = new ProgressDialog(context);
        this.builder = new AlertDialog.Builder(context);
    }


    public void showLoading(String message){
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissLoading(){
        progressDialog.dismiss();
    }



    public void showDialog(String title,
                           String message,
                           String positiveText,
                           String negativeText,
                           DialogInterface.OnClickListener positive,
                           DialogInterface.OnClickListener negative
                           ){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText,positive)
                .setNegativeButton(negativeText,negative);
        builder.show();
    }
}
