package com.clothes.clothesapp.activities.tailor.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.callback.OrderCallback;
import com.clothes.clothesapp.controller.OrderController;
import com.clothes.clothesapp.model.Order;
import com.clothes.clothesapp.utils.SharedData;
import com.google.protobuf.DescriptorProtos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    ImageView image;
    TextView name,state,date,size,color,description;
    Button action,reject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        state = findViewById(R.id.state);
        date = findViewById(R.id.date);
        size = findViewById(R.id.size);
        color = findViewById(R.id.color);
        description = findViewById(R.id.description);
        action = findViewById(R.id.action);
        reject = findViewById(R.id.reject);

        Picasso.get()
                .load(SharedData.order.getImageURL())
                .into(image);
        
        name.setText(SharedData.order.getCustomer().getName());

        Order tailor = SharedData.order;
        if(tailor.getState() == 0){
            state.setText(R.string.waiting_for_tailor);
            state.setTextColor(getColor(R.color.gray));
            action.setText(R.string.accept);
        }else if(tailor.getState() == 1){
            state.setText(R.string.waiting_for_customer_payment);
            state.setTextColor(getColor(R.color.gray));
            action.setVisibility(View.GONE);
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
        

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoneyDialog();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedData.order.setState(-1);
                new OrderController().Save(SharedData.order, new OrderCallback() {
                    @Override
                    public void onSuccess(ArrayList<Order> orders) {
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.order_accepted), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });
            }
        });
        
        
    }


    private void showMoneyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.money_dialog,null);
        builder.setView(view);

        EditText money = view.findViewById(R.id.money);
        TextView save = view.findViewById(R.id.save);
        TextView cancel = view.findViewById(R.id.cancel);

        AlertDialog dialog = builder.create();

        dialog.show();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(money.getText() == null){
                    money.setError("Required");
                    return;
                }else if(money.getText().toString().isEmpty()){
                    money.setError("Required");
                    return;
                }
                dialog.dismiss();
                SharedData.order.setPrice(Double.parseDouble(money.getText().toString()));
                SharedData.order.setState(1);
                new OrderController().Save(SharedData.order, new OrderCallback() {
                    @Override
                    public void onSuccess(ArrayList<Order> orders) {
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.order_accepted), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }
}