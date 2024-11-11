package com.clothes.clothesapp.activities.customer.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.activities.customer.CustomerDashboard;
import com.clothes.clothesapp.activities.tailor.ui.works.GalleryActivity;
import com.clothes.clothesapp.callback.GalleryCallback;
import com.clothes.clothesapp.callback.ImageCallback;
import com.clothes.clothesapp.callback.OrderCallback;
import com.clothes.clothesapp.controller.ComplaintsController;
import com.clothes.clothesapp.controller.GalleryController;
import com.clothes.clothesapp.controller.OrderController;
import com.clothes.clothesapp.model.Gallery;
import com.clothes.clothesapp.model.Order;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateOrderActivity extends AppCompatActivity {

    CircleImageView dress;
    EditText color,size,description;
    Button sendOrder;

    LoadingHelper loadingHelper;
    String imageURL = "";
    public static final int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        dress = findViewById(R.id.dress);

        color = findViewById(R.id.color);
        size = findViewById(R.id.size);
        description = findViewById(R.id.description);
        sendOrder = findViewById(R.id.send_order);

        loadingHelper = new LoadingHelper(this);

        dress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if(checkReadPermission()){
                        pickImage();
                    }
                }else{
                    pickImage();
                }
            }
        });

        sendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(color.getText() == null){
                    color.setError("Required");
                    return;
                }else if(color.getText().toString().isEmpty()){
                    color.setError("Required");
                    return;
                }


                if(size.getText() == null){
                    size.setError("Required");
                    return;
                }else if(size.getText().toString().isEmpty()){
                    size.setError("Required");
                    return;
                }


                if(description.getText() == null){
                    description.setError("Required");
                    return;
                }else if(description.getText().toString().isEmpty()){
                    description.setError("Required");
                    return;
                }


                if(imageURL.isEmpty()){
                    Toast.makeText(CreateOrderActivity.this, getString(R.string.please_select_image_first), Toast.LENGTH_SHORT).show();
                    return;
                }


                Order order = new Order("",
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime()),
                        size.getText().toString(),
                        color.getText().toString(),
                        description.getText().toString(),
                        imageURL,
                        0,
                        SharedData.currentCustomer,
                        SharedData.tailor,0);


                loadingHelper.showLoading("Sending Order");
                new OrderController().Save(order, new OrderCallback() {
                    @Override
                    public void onSuccess(ArrayList<Order> orders) {
                        loadingHelper.dismissLoading();
                        Toast.makeText(CreateOrderActivity.this, "Order Sent Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CreateOrderActivity.this, CustomerDashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkReadPermission(){
        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},2);
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImage();
            }
        }
    }

    private void pickImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }


            loadingHelper.showLoading(getString(R.string.uploading_image));
            new ComplaintsController().uploadImage(data.getData(), new ImageCallback() {

                @Override
                public void onSuccess(String URL) {
                    imageURL = URL;
                    loadingHelper.dismissLoading();
                    Picasso.get()
                            .load(imageURL)
                            .into(dress);
                }

                @Override
                public void onFail(String msg) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(CreateOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                }


            });
        }
    }
}