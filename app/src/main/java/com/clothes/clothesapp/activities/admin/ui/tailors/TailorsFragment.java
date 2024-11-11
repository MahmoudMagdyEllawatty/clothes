package com.clothes.clothesapp.activities.admin.ui.tailors;

import android.app.AlertDialog;
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
import com.clothes.clothesapp.callback.TailorCallback;
import com.clothes.clothesapp.controller.TailorController;
import com.clothes.clothesapp.databinding.FragmentHomeBinding;
import com.clothes.clothesapp.model.Tailor;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TailorsFragment extends Fragment {

    private RecyclerView tailorsList;
    private LoadingHelper loadingHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tailorsList = root.findViewById(R.id.advices_list);
        loadingHelper = new LoadingHelper(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        tailorsList.setLayoutManager(mLayoutManager);
        tailorsList.setItemAnimator(new DefaultItemAnimator());



        loadData();
        return root;

    }

    private void loadData(){
        loadingHelper.showLoading("loading data...");
        new TailorController().getTailors(new TailorCallback() {
            @Override
            public void onSuccess(ArrayList<Tailor> tailors) {
                loadingHelper.dismissLoading();
                tailorsList.setAdapter(new TailorsAdapter(tailors));
            }

            @Override
            public void onFail(String msg) {
                loadingHelper.dismissLoading();
            }


        });
    }

    private class TailorsAdapter extends RecyclerView.Adapter<TailorsAdapter.ViewHolder> {
        ArrayList<Tailor> tailors;

        public TailorsAdapter(ArrayList<Tailor> tailors) {
            this.tailors = tailors;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.tailor_row,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Tailor tailor = tailors.get(position);



            holder.title.setText(tailor.getName());

            if(tailor.getState() == 0){
                holder.content.setText(getString(R.string.waiting));
            }else if(tailor.getState() == 1){
                holder.content.setText(getString(R.string.accepted));
            }else{
                holder.content.setText(getString(R.string.rejected));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInfoDialog(tailor);
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

            if(tailor.getState() == 1){
                accept.setVisibility(View.INVISIBLE);
            }else if(tailor.getState() == -1){
                reject.setVisibility(View.INVISIBLE);
            }

            if(tailor.getAvatar().isEmpty()){
                tailor.setAvatar("DDS");
            }
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
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tailor.setState(1);
                    loadingHelper.showLoading("Updating State");
                    new TailorController().Save(tailor, new TailorCallback() {
                        @Override
                        public void onSuccess(ArrayList<Tailor> tailors) {
                            dialog.dismiss();
                            loadingHelper.dismissLoading();
                            Toast.makeText(getContext(), getString(R.string.tailor_accepted_successfully), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tailor.setState(-1);
                    loadingHelper.showLoading("Updating State");
                    new TailorController().Save(tailor, new TailorCallback() {
                        @Override
                        public void onSuccess(ArrayList<Tailor> tailors) {
                            loadingHelper.dismissLoading();
                            dialog.dismiss();
                            Toast.makeText(getContext(), getString(R.string.tailor_rejected_successfully), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });
                }
            });


            dialog.show();

        }

        @Override
        public int getItemCount() {
            return tailors.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{


            TextView title,content;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.name);
                content = itemView.findViewById(R.id.state);
            }
        }
    }
}