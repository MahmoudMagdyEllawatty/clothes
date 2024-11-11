package com.clothes.clothesapp.controller;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.clothes.clothesapp.callback.ComplaintCallback;
import com.clothes.clothesapp.callback.ImageCallback;
import com.clothes.clothesapp.model.Complaint;
import com.clothes.clothesapp.model.Customer;
import com.clothes.clothesapp.utils.Config;
import com.clothes.clothesapp.utils.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ComplaintsController {
    
    private final String node = Config.COMPLAINTS_NODE;
    ArrayList<Complaint> complaints = new ArrayList<>();
    FirebaseHelper<Complaint> helper = new FirebaseHelper<Complaint>();

    public void Save(final Complaint complaint, final ComplaintCallback callback){
        if(complaint.getKey().equals("")){
            complaint.setKey(helper.getKey(node));
        }

        helper.save(node,complaint.getKey(),complaint)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            complaints.add(complaint);
                            callback.onSuccess(complaints);
                        }else{
                            callback.onFail(task.getException().toString());
                        }
                    }
                });
    }


    public void delete(Complaint complaint, ComplaintCallback callback){
        helper.delete(node, complaint.getKey())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            complaints = new ArrayList<>();
                            callback.onSuccess(complaints);
                        }else{
                            callback.onFail(task.getException().toString());
                        }
                    }
                });
    }


    public void getComplaints( final ComplaintCallback callback){
        helper.getAll(node, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots == null && e != null){
                    callback.onFail(e.getLocalizedMessage());
                }else {
                    ArrayList<Complaint> complaints1 = new ArrayList<>();
                    assert queryDocumentSnapshots != null;
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        Complaint complaint = snap.toObject(Complaint.class);

                            complaints1.add(complaint);
                    }
                    callback.onSuccess(complaints1);
                }
            }
        });
    }

    public void getCustomerComplaints(Customer customer, final ComplaintCallback callback){
        helper.getAll(node, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots == null && e != null){
                    callback.onFail(e.getLocalizedMessage());
                }else {
                    ArrayList<Complaint> complaints1 = new ArrayList<>();
                    assert queryDocumentSnapshots != null;
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        Complaint complaint = snap.toObject(Complaint.class);
                        if(complaint.getCustomer().getKey().equals(customer.getKey()))
                            complaints1.add(complaint);
                    }
                    callback.onSuccess(complaints1);
                }
            }
        });
    }


    public void uploadImage(Uri uri, final ImageCallback callback){
        helper.uploadDoc(uri.toString(),uri)
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            callback.onSuccess(task.getResult().toString());
                        }else{
                            callback.onFail(task.getException().toString());
                        }
                    }
                });
    }


}
