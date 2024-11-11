package com.clothes.clothesapp.activities.customer.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.activities.tailor.ui.works.GalleryActivity;
import com.clothes.clothesapp.activities.tailor.ui.works.WorkActivity;
import com.clothes.clothesapp.activities.tailor.ui.works.WorksFragment;
import com.clothes.clothesapp.callback.GalleryCallback;
import com.clothes.clothesapp.controller.GalleryController;
import com.clothes.clothesapp.model.Gallery;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TailorDesignsActivity extends AppCompatActivity {

    Button createOrder;
    RecyclerView tailorDesigns;
    
    LoadingHelper loadingHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailor_designs);

        loadingHelper = new LoadingHelper(this);
        
        tailorDesigns = findViewById(R.id.designs_list);
        tailorDesigns.setItemAnimator(new DefaultItemAnimator());
        tailorDesigns.setLayoutManager(new LinearLayoutManager(this));
        
        createOrder = findViewById(R.id.login);
        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TailorDesignsActivity.this,CreateOrderActivity.class);
                startActivity(intent);
            }
        });

        loadData();

    }

    private void loadData(){
        loadingHelper.showLoading("loading data...");
        new GalleryController().getTailorGalleries(SharedData.tailor, new GalleryCallback() {
            @Override
            public void onSuccess(ArrayList<Gallery> gallery) {
                loadingHelper.dismissLoading();
                ArrayList<Gallery> galleries = new ArrayList<>();
                for (Gallery g:gallery) {
                    if(g.getTailor().getKey().equals(SharedData.tailor.getKey())){
                        galleries.add(g);
                    }
                }
                tailorDesigns.setAdapter(new GalleryAdapter(galleries));
            }

            @Override
            public void onFail(String msg) {
                loadingHelper.dismissLoading();
            }


        });
    }


    private class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
        ArrayList<Gallery> gallery;

        public GalleryAdapter(ArrayList<Gallery> gallery) {
            this.gallery = gallery;
        }

        @NonNull
        @Override
        public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(TailorDesignsActivity.this)
                    .inflate(R.layout.tailor_gallery_row,parent,false);
            return new GalleryAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
            Gallery tailor = gallery.get(position);



            holder.title.setText(tailor.getName());
            holder.description.setText(tailor.getDescription());

            ArrayList<SlideModel> slides = new ArrayList<>();
            slides.add(new SlideModel(tailor.getImage(), ScaleTypes.CENTER_CROP));
            if(tailor.getImages() == null){
                tailor.setImages(new ArrayList<>());
            }
            for (String image: tailor.getImages()) {
                slides.add(new SlideModel(image, ScaleTypes.CENTER_CROP));
            }

            holder.imageSlider.setImageList(slides);
            holder.imageSlider.setSlideAnimation(AnimationTypes.CUBE_IN);
            holder.imageSlider.startSliding(2000);



        }


        @Override
        public int getItemCount() {
            return gallery.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{


            TextView title,description;
            ImageSlider imageSlider;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.title);
                description = itemView.findViewById(R.id.description);
                imageSlider = itemView.findViewById(R.id.image_slider);
            }
        }
    }
}