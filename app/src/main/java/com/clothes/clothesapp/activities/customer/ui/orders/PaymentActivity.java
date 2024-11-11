package com.clothes.clothesapp.activities.customer.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.activities.customer.CustomerDashboard;
import com.clothes.clothesapp.callback.OrderCallback;
import com.clothes.clothesapp.controller.OrderController;
import com.clothes.clothesapp.model.Order;
import com.clothes.clothesapp.utils.SharedData;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {

    EditText name,no,month,year,cvv;
    Spinner type;
    Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        name = findViewById(R.id.name_on_card);
        no = findViewById(R.id.card_no);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        cvv  = findViewById(R.id.cvv);

        type = findViewById(R.id.card_type);

        pay = findViewById(R.id.pay);


        double price = SharedData.order.getPrice();

        ((TextView)findViewById(R.id.amount)).setText(String.format("Total : %s KWD", price));

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkData()){
                    SharedData.order.setState(2);

                    new OrderController().Save(SharedData.order, new OrderCallback() {
                        @Override
                        public void onSuccess(ArrayList<Order> employers) {

                            Toast.makeText(PaymentActivity.this, getString(R.string.order_paid_successfully), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PaymentActivity.this, CustomerDashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });

                }else{
                    Toast.makeText(PaymentActivity.this, getString(R.string.please_complete_data), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkData(){
        boolean state = true;

        if(name.getText().toString().isEmpty()){
            state = false;
            name.setError("Required");
        }else{
            name.setError(null);
        }

        if(no.getText().toString().isEmpty()){
            state = false;
            no.setError("Required");
        }else{
            no.setError(null);
        }


        if(month.getText().toString().isEmpty()){
            state = false;
            month.setError("Required");
        }else{
            month.setError(null);
        }


        if(year.getText().toString().isEmpty()){
            state = false;
            year.setError("Required");
        }else{
            year.setError(null);
        }


        if(cvv.getText().toString().isEmpty()){
            state = false;
            cvv.setError("Required");
        }else{
            cvv.setError(null);
        }


        return  state;
    }
}