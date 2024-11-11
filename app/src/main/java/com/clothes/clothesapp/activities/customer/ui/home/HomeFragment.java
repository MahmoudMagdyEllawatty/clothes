package com.clothes.clothesapp.activities.customer.ui.home;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.activities.tailor.ui.works.WorkActivity;
import com.clothes.clothesapp.callback.TailorCallback;
import com.clothes.clothesapp.controller.TailorController;
import com.clothes.clothesapp.model.Tailor;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private RecyclerView tailorsList;
    private LoadingHelper loadingHelper;
    ArrayList<Tailor> allTailors,tailors;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_search_tailors, container, false);

        tailorsList = root.findViewById(R.id.tailors_list);
        loadingHelper = new LoadingHelper(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        tailorsList.setLayoutManager(mLayoutManager);
        tailorsList.setItemAnimator(new DefaultItemAnimator());

        ImageButton filter = root.findViewById(R.id.search);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedData.gallery = null;
                Intent intent = new Intent(getActivity(), WorkActivity.class);
                startActivity(intent);
            }
        });


        loadData();
        return root;
    }

    private void loadData(){
        loadingHelper.showLoading("loading data...");
        new TailorController().getTailors( new TailorCallback() {
            @Override
            public void onSuccess(ArrayList<Tailor> gallery) {
                loadingHelper.dismissLoading();
                tailors = new ArrayList<>();
                allTailors = new ArrayList<>();
                for (Tailor tailor : gallery){
                    if(tailor.getState() == 1){
                        tailors.add(tailor);
                        allTailors.add(tailor);
                    }
                }

                tailorsList.setAdapter(new TailorAdapter(tailors));
            }

            @Override
            public void onFail(String msg) {
                loadingHelper.dismissLoading();
            }


        });
    }

    private class TailorAdapter extends RecyclerView.Adapter<TailorAdapter.ViewHolder> {
        ArrayList<Tailor> tailorsData;

        public TailorAdapter(ArrayList<Tailor> gallery) {
            this.tailorsData = gallery;
        }

        @NonNull
        @Override
        public TailorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.tailor_customer_row,parent,false);
            return new TailorAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TailorAdapter.ViewHolder holder, int position) {
            Tailor tailor = tailorsData.get(position);

            Picasso.get()
                    .load(tailor.getAvatar())
                    .error(R.drawable.avatar)
                    .placeholder(R.drawable.avatar)
                    .into(holder.image);

            holder.title.setText(tailor.getName());

            holder.designs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedData.tailor = tailor;
                    Intent intent = new Intent(getActivity(),TailorDesignsActivity.class);
                    startActivity(intent);
                }
            });

            holder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInfoDialog(tailor);
                }
            });

            holder.createOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedData.tailor = tailor;
                    Intent intent = new Intent(getActivity(),CreateOrderActivity.class);
                    startActivity(intent);
                }
            });

        }

        private void showInfoDialog(Tailor tailor){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.tailor_info,null);

            builder.setView(view);
            LinearLayout actionsArea = view.findViewById(R.id.actions_area);
            CircleImageView image = view.findViewById(R.id.profile_image);
            TextView name = view.findViewById(R.id.name);
            TextView phone = view.findViewById(R.id.phone);
            TextView address = view.findViewById(R.id.address);
            TextView email = view.findViewById(R.id.email);
            TextView state = view.findViewById(R.id.state);
            TextView accept = view.findViewById(R.id.accept);
            TextView reject = view.findViewById(R.id.reject);

            actionsArea.setVisibility(View.GONE);

            Picasso.get()
                            .load(tailor.getAvatar())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                                    .into(image);

            email.setText(tailor.getEmail());
            name.setText(tailor.getName());
            phone.setText(tailor.getPhone());
            address.setText(tailor.getAddress());
            if(tailor.getState() == 0){
                state.setText(getString(R.string.waiting));
            }else if(tailor.getState() == 1){
                state.setText(getString(R.string.accepted));
            }else{
                state.setText(getString(R.string.rejected));
            }
            AlertDialog dialog = builder.create();


            dialog.show();

        }


        @Override
        public int getItemCount() {
            return tailorsData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{


            TextView title;
            CircleImageView image;
            ImageView designs,info,createOrder;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.name);
                image = itemView.findViewById(R.id.profile_image);
                designs = itemView.findViewById(R.id.designs);
                info = itemView.findViewById(R.id.info);
                createOrder = itemView.findViewById(R.id.create_order);
            }
        }
    }

}