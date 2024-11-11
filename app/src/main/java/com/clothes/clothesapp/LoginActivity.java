package com.clothes.clothesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clothes.clothesapp.activities.admin.AdminDashboard;
import com.clothes.clothesapp.activities.customer.CustomerDashboard;
import com.clothes.clothesapp.activities.tailor.TailorDashboard;
import com.clothes.clothesapp.callback.CustomerCallback;
import com.clothes.clothesapp.callback.TailorCallback;
import com.clothes.clothesapp.controller.CustomersController;
import com.clothes.clothesapp.controller.TailorController;
import com.clothes.clothesapp.model.Customer;
import com.clothes.clothesapp.model.Tailor;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button login;
    TextView register;
    LoadingHelper loadingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingHelper = new LoadingHelper(this);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectTypeDialog();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                loadingHelper.showLoading("Validating Login ..");

                if(email.getText().toString().equals("admin@clothes.com") && password.getText().toString().equals("123456")){
                    loadingHelper.dismissLoading();
                    Intent intent = new Intent(LoginActivity.this, AdminDashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    new TailorController().checkLogin(email.getText().toString(), password.getText().toString(), new TailorCallback() {
                        @Override
                        public void onSuccess(ArrayList<Tailor> tailors) {
                            if(!tailors.isEmpty()){
                                loadingHelper.dismissLoading();
                                SharedData.currentTailor = tailors.get(0);
                                if(SharedData.currentTailor.getState() == 0){
                                    Toast.makeText(LoginActivity.this, getString(R.string.please_wait_for_admin_approval), Toast.LENGTH_SHORT).show();
                                }else if(SharedData.currentTailor.getState() == 1){
                                    Intent intent = new Intent(LoginActivity.this, TailorDashboard.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(LoginActivity.this, getString(R.string.your_account_has_been_rejected), Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                new CustomersController().checkLogin(email.getText().toString(),
                                        password.getText().toString(), new CustomerCallback() {
                                            @Override
                                            public void onSuccess(ArrayList<Customer> customers) {
                                                loadingHelper.dismissLoading();
                                                if(!customers.isEmpty()){
                                                    SharedData.currentCustomer = customers.get(0);
                                                    if(SharedData.currentCustomer.getState() == 0){
                                                        Toast.makeText(LoginActivity.this, getString(R.string.please_wait_for_admin_approval), Toast.LENGTH_SHORT).show();
                                                    }else if(SharedData.currentCustomer.getState() == 1){
                                                        Intent intent = new Intent(LoginActivity.this, CustomerDashboard.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }else{
                                                        Toast.makeText(LoginActivity.this, getString(R.string.your_account_has_been_rejected), Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    Toast.makeText(LoginActivity.this,getString(R.string.invalid_email_or_password), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFail(String msg) {
                                                loadingHelper.dismissLoading();
                                                Toast.makeText(LoginActivity.this, getString(R.string.invalid_email_or_password), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onFail(String msg) {
                            loadingHelper.dismissLoading();
                            Toast.makeText(LoginActivity.this, getString(R.string.invalid_email_or_password), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private void selectTypeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.type_dialog,null);
        builder.setView(view);

        TextView tailor = view.findViewById(R.id.tailor);
        TextView customer = view.findViewById(R.id.customer);

        AlertDialog dialog = builder.create();
        tailor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                SharedData.type = 2;
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                SharedData.type = 3;
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }
}