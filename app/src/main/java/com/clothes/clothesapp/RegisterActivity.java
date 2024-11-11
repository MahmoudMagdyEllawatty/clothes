package com.clothes.clothesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clothes.clothesapp.callback.CustomerCallback;
import com.clothes.clothesapp.callback.TailorCallback;
import com.clothes.clothesapp.controller.CustomersController;
import com.clothes.clothesapp.controller.TailorController;
import com.clothes.clothesapp.model.Customer;
import com.clothes.clothesapp.model.Tailor;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText name,email,password;
    Button login;
    LoadingHelper loadingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadingHelper = new LoadingHelper(this);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText() == null){
                    name.setError("Required");
                    return;
                }else if(name.getText().toString().equals("")){
                    name.setError("Required");
                    return;
                }

                if(email.getText() == null){
                    email.setError("Required");
                    return;
                }else if(email.getText().toString().equals("")){
                    email.setError("Required");
                    return;
                }

                if(password.getText() == null){
                    password.setError("Required");
                    return;
                }else if(password.getText().toString().equals("")){
                    password.setError("Required");
                    return;
                }

                loadingHelper.showLoading("Creating Account ..");

                if(SharedData.type == 2){
                    Tailor tailor = new Tailor("",name.getText().toString(),"","",email.getText().toString(),password.getText().toString(),0,"");
                    new TailorController().Save(tailor, new TailorCallback() {
                        @Override
                        public void onSuccess(ArrayList<Tailor> tailors) {
                            loadingHelper.dismissLoading();
                            Toast.makeText(RegisterActivity.this, "Account Created Successfully,Please wait admin approval", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });
                }else{
                    Customer customer = new Customer("",name.getText().toString(),email.getText().toString(),password.getText().toString(),0,"");
                    new CustomersController().Save(customer, new CustomerCallback() {
                        @Override
                        public void onSuccess(ArrayList<Customer> tailors) {
                            loadingHelper.dismissLoading();
                            Toast.makeText(RegisterActivity.this, "Account Created Successfully,Please wait admin approval", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });
                }

            }
        });
    }
}