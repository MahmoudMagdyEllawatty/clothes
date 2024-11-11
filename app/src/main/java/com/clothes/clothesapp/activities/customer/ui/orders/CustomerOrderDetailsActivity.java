package com.clothes.clothesapp.activities.customer.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.activities.tailor.ui.orders.OrderDetailsActivity;
import com.clothes.clothesapp.callback.OrderCallback;
import com.clothes.clothesapp.controller.OrderController;
import com.clothes.clothesapp.model.Order;
import com.clothes.clothesapp.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerOrderDetailsActivity extends AppCompatActivity {

    ImageView image;
    TextView name,state,date,size,color,description,price;
    Button action,reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_details);


        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        state = findViewById(R.id.state);
        date = findViewById(R.id.date);
        size = findViewById(R.id.size);
        color = findViewById(R.id.color);
        description = findViewById(R.id.description);
        action = findViewById(R.id.action);
        reject = findViewById(R.id.reject);
        price = findViewById(R.id.price);

        Picasso.get()
                .load(SharedData.order.getImageURL())
                .into(image);

        name.setText(SharedData.order.getTailor().getName());

        Order tailor = SharedData.order;
        if(tailor.getState() == 0){
            state.setText(R.string.waiting_for_tailor);
            state.setTextColor(getColor(R.color.gray));
            action.setVisibility(View.GONE);
        }else if(tailor.getState() == 1){
            state.setText(R.string.waiting_for_customer_payment);
            state.setTextColor(getColor(R.color.gray));
            action.setText(R.string.pay);
            reject.setVisibility(View.GONE);
        }else if(tailor.getState() == 2){
            state.setText(R.string.accepted_paid);
            state.setTextColor(getColor(R.color.green));
            action.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
        }else if(tailor.getState() == -1){
            state.setText(R.string.rejected);
            state.setTextColor(getColor(R.color.red));
            action.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
        }


        date.setText(SharedData.order.getDate());
        size.setText(SharedData.order.getSize());
        color.setText(SharedData.order.getColor());
        description.setText(SharedData.order.getDetails());
        price.setText(String.format("%s KWD", SharedData.order.getPrice()));


        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerOrderDetailsActivity.this,PaymentActivity.class);
                startActivity(intent);
            }
        });

    }
}