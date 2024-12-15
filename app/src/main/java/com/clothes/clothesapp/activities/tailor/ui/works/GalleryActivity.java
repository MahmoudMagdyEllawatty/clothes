package com.clothes.clothesapp.activities.tailor.ui.works;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView galleryList;
    private LoadingHelper loadingHelper;
    String imageURL = "";
    public static final int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        galleryList = findViewById(R.id.advices_list);
        loadingHelper = new LoadingHelper(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        galleryList.setLayoutManager(mLayoutManager);
        galleryList.setItemAnimator(new DefaultItemAnimator());

        if(SharedData.gallery.getImages() == null){
            SharedData.gallery.setImages(new ArrayList<>());
        }
        ImageButton add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
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

        galleryList.setAdapter(new GalleryAdapter(SharedData.gallery.getImages()));
        
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkReadPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES);
            if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.READ_MEDIA_IMAGES},2);
                return false;
            }else{
                return true;
            }
        }else{
            int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},2);
                return false;
            }else{
                return true;
            }
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
                    ArrayList<String> images = SharedData.gallery.getImages();
                    images.add(imageURL);
                    SharedData.gallery.setImages(images);

                    new GalleryController().Save(SharedData.gallery, new GalleryCallback() {
                        @Override
                        public void onSuccess(ArrayList<Gallery> galleries) {
                            Toast.makeText(GalleryActivity.this, getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });
                }

                @Override
                public void onFail(String msg) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(GalleryActivity.this, msg, Toast.LENGTH_SHORT).show();
                }


            });
        }
    }


    private class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
        ArrayList<String> gallery;

        public GalleryAdapter(ArrayList<String> gallery) {
            this.gallery = gallery;
        }

        @NonNull
        @Override
        public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(GalleryActivity.this)
                    .inflate(R.layout.gallery_image,parent,false);
            return new GalleryAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
            String tailor = gallery.get(position);



           

            Picasso.get()
                    .load(tailor)
                    .into(holder.image);

          
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LoadingHelper(GalleryActivity.this)
                            .showDialog(getString(R.string.delete_image), getString(R.string.are_you_sure),
                                    getString(R.string.delete), getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ArrayList<String> images = SharedData.gallery.getImages();
                                            images.remove(holder.getAdapterPosition());
                                            SharedData.gallery.setImages(images);
                                            new GalleryController().Save(SharedData.gallery, new GalleryCallback() {
                                                @Override
                                                public void onSuccess(ArrayList<Gallery> galleries) {
                                                    Toast.makeText(GalleryActivity.this, getString(R.string.deleted_successfully), Toast.LENGTH_SHORT).show();
                                                    onBackPressed();
                                                }

                                                @Override
                                                public void onFail(String msg) {

                                                }
                                            });
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                }
            });




        }


        @Override
        public int getItemCount() {
            return gallery.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{


            ImageView image;
            ImageButton delete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);


                image = itemView.findViewById(R.id.image);
                delete = itemView.findViewById(R.id.delete);
            }
        }
    }
}