package com.clothes.clothesapp.activities.tailor.ui.works;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.clothes.clothesapp.callback.GalleryCallback;
import com.clothes.clothesapp.callback.ImageCallback;
import com.clothes.clothesapp.controller.ComplaintsController;
import com.clothes.clothesapp.controller.GalleryController;
import com.clothes.clothesapp.model.Gallery;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WorkActivity extends AppCompatActivity {

    ImageView select_image;
    EditText title,content;
    Button save;
    LoadingHelper loadingHelper;
    Gallery gallery;
    String imageURL = "";
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        select_image = findViewById(R.id.select_image);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        save = findViewById(R.id.save);

        loadingHelper = new LoadingHelper(this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText() == null){
                    title.setError("Required");
                    return;
                }else if(title.getText().toString().equals("")){
                    title.setError("Required");
                    return;
                }

                if(content.getText() == null){
                    content.setError("Required");
                    return;
                }else if(content.getText().toString().equals("")){
                    content.setError("Required");
                    return;
                }

                if(imageURL.equals("")){
                    Toast.makeText(WorkActivity.this,getString( R.string.please_select_image_first), Toast.LENGTH_SHORT).show();
                    return;
                }

                gallery.setImage(imageURL);
                gallery.setDescription(content.getText().toString());
                gallery.setName(title.getText().toString());
                gallery.setTailor(SharedData.currentTailor);

                new GalleryController().Save(gallery, new GalleryCallback() {
                    @Override
                    public void onSuccess(ArrayList<Gallery> pregnants) {
                        Toast.makeText(WorkActivity.this, getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });
            }
        });


        if(SharedData.gallery == null){
            gallery = new Gallery();
            gallery.setKey("");
        }else{
            gallery = SharedData.gallery;
            title.setText(gallery.getName());
            content.setText(gallery.getDescription());
            imageURL = gallery.getImage();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                select_image.setBackground(null);
            }
            Picasso.get().load(imageURL)
                    .into(select_image);
        }

        select_image.setOnClickListener(new View.OnClickListener() {
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

            select_image.setBackground(null);
            Picasso.get()
                    .load(data.getData())
                    .into(select_image);


            loadingHelper.showLoading(getString(R.string.uploading_image));
            new ComplaintsController().uploadImage(data.getData(), new ImageCallback() {

                @Override
                public void onSuccess(String URL) {
                    imageURL = URL;
                    loadingHelper.dismissLoading();
                }

                @Override
                public void onFail(String msg) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(WorkActivity.this, msg, Toast.LENGTH_SHORT).show();
                }


            });
        }
    }
}