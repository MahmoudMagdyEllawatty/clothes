package com.clothes.clothesapp.controller;

import androidx.annotation.NonNull;

import com.clothes.clothesapp.callback.TailorCallback;
import com.clothes.clothesapp.model.Tailor;
import com.clothes.clothesapp.utils.Config;
import com.clothes.clothesapp.utils.FirebaseHelper;
import com.clothes.clothesapp.utils.SharedData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class TailorController {
    
    private final String node = Config.TAILORS_NODE;
    ArrayList<Tailor> complaints = new ArrayList<>();
    FirebaseHelper<Tailor> helper = new FirebaseHelper<Tailor>();

    public void Save(final Tailor tailor, final TailorCallback callback){
        if(tailor.getKey().equals("")){
            tailor.setKey(helper.getKey(node));
        }

        helper.save(node,tailor.getKey(),tailor)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            complaints.add(tailor);
                            callback.onSuccess(complaints);
                        }else{
                            callback.onFail(task.getException().toString());
                        }
                    }
                });
    }


    public void delete(Tailor tailor, TailorCallback callback){
        helper.delete(node, tailor.getKey())
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


    public void getTailors( final TailorCallback callback){
        helper.getAll(node, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots == null && e != null){
                    callback.onFail(e.getLocalizedMessage());
                }else {
                    ArrayList<Tailor> complaints1 = new ArrayList<>();
                    assert queryDocumentSnapshots != null;
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        Tailor tailor = snap.toObject(Tailor.class);

                            complaints1.add(tailor);
                    }
                    callback.onSuccess(complaints1);
                }
            }
        });
    }


    public void checkLogin(String email,String password, final TailorCallback callback){
        helper.getAll(node, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots == null && e != null){
                    callback.onFail(e.getLocalizedMessage());
                }else {
                    if(SharedData.currentTailor != null)
                        return;
                    ArrayList<Tailor> complaints1 = new ArrayList<>();
                    assert queryDocumentSnapshots != null;
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        Tailor tailor = snap.toObject(Tailor.class);
                        if(tailor.getEmail().equals(email) && tailor.getPassword().equals(password))
                            complaints1.add(tailor);
                    }
                    callback.onSuccess(complaints1);
                }
            }
        });
    }

    


}
