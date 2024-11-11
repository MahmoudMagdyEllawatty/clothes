package com.clothes.clothesapp.activities.tailor.ui.works;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.callback.GalleryCallback;
import com.clothes.clothesapp.controller.GalleryController;
import com.clothes.clothesapp.databinding.FragmentHomeBinding;
import com.clothes.clothesapp.model.Gallery;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WorksFragment extends Fragment {

    private RecyclerView galleryList;
    private LoadingHelper loadingHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_works, container, false);

        galleryList = root.findViewById(R.id.advices_list);
        loadingHelper = new LoadingHelper(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        galleryList.setLayoutManager(mLayoutManager);
        galleryList.setItemAnimator(new DefaultItemAnimator());

        ImageButton add = root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedData.gallery = null;
                Intent intent = new Intent(getActivity(),WorkActivity.class);
                startActivity(intent);
            }
        });


        loadData();
        return root;

    }

    private void loadData(){
        loadingHelper.showLoading("loading data...");
        new GalleryController().getTailorGalleries(SharedData.currentTailor, new GalleryCallback() {
            @Override
            public void onSuccess(ArrayList<Gallery> gallery) {
                loadingHelper.dismissLoading();
                galleryList.setAdapter(new GalleryAdapter(gallery));
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
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.gallery_row,parent,false);
            return new GalleryAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
            Gallery tailor = gallery.get(position);



            holder.title.setText(tailor.getName());

            Picasso.get()
                    .load(tailor.getImage())
                    .into(holder.image);

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedData.gallery = tailor;
                    Intent intent = new Intent(getActivity(),WorkActivity.class);
                    startActivity(intent);
                }
            });

            holder.gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedData.gallery = tailor;
                    Intent intent = new Intent(getActivity(),GalleryActivity.class);
                    startActivity(intent);
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LoadingHelper(getActivity())
                            .showDialog(getString(R.string.delete_work), getString(R.string.are_you_sure),
                                    getString(R.string.delete), getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new GalleryController().delete(tailor, new GalleryCallback() {
                                                @Override
                                                public void onSuccess(ArrayList<Gallery> galleries) {
                                                    Toast.makeText(getActivity(), getString(R.string.deleted_successfully), Toast.LENGTH_SHORT).show();
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


            TextView title;
            ImageView image;
            ImageButton edit,delete,gallery;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.name);
                image = itemView.findViewById(R.id.image);
                edit = itemView.findViewById(R.id.edit);
                delete = itemView.findViewById(R.id.delete);
                gallery = itemView.findViewById(R.id.gallery);
            }
        }
    }
}