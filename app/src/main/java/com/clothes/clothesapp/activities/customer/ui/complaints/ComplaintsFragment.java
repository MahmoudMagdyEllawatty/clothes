package com.clothes.clothesapp.activities.customer.ui.complaints;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.activities.admin.ui.complaints.NotificationsFragment;
import com.clothes.clothesapp.activities.customer.ui.orders.CustomerOrderDetailsActivity;
import com.clothes.clothesapp.activities.tailor.ui.works.WorkActivity;
import com.clothes.clothesapp.callback.ComplaintCallback;
import com.clothes.clothesapp.callback.OrderCallback;
import com.clothes.clothesapp.controller.ComplaintsController;
import com.clothes.clothesapp.controller.OrderController;
import com.clothes.clothesapp.model.Complaint;
import com.clothes.clothesapp.model.Order;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;

import java.util.ArrayList;

public class ComplaintsFragment extends Fragment {

    private RecyclerView ordersList;
    private LoadingHelper loadingHelper;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_works, container, false);

        ordersList = root.findViewById(R.id.advices_list);
        loadingHelper = new LoadingHelper(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        ordersList.setLayoutManager(mLayoutManager);
        ordersList.setItemAnimator(new DefaultItemAnimator());

        ImageButton add = root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddComplaintDialog();
            }
        });

        
        loadData();
        return root;
    }

    private void showAddComplaintDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_complaint_info,null);

        builder.setView(view);

        EditText title = view.findViewById(R.id.title);
        EditText description = view.findViewById(R.id.description);
        TextView accept = view.findViewById(R.id.accept);
        TextView reject = view.findViewById(R.id.reject);


        AlertDialog dialog = builder.create();
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText() == null){
                    title.setError("Required");
                    return;
                }
                else if(title.getText().toString().isEmpty()){
                    title.setError("Required");
                    return;
                }


                if(description.getText() == null){
                    description.setError("Required");
                    return;
                }
                else if(description.getText().toString().isEmpty()){
                    description.setError("Required");
                    return;
                }
                Complaint complaint = new Complaint();
                complaint.setReply("");
                complaint.setCustomer(SharedData.currentCustomer);
                complaint.setDescription(description.getText().toString());
                complaint.setKey("");
                complaint.setTitle(title.getText().toString());

                loadingHelper.showLoading("Updating State");
                new ComplaintsController().Save(complaint, new ComplaintCallback() {
                    @Override
                    public void onSuccess(ArrayList<Complaint> complaints) {
                        dialog.dismiss();
                        loadingHelper.dismissLoading();
                        Toast.makeText(getContext(), getString(R.string.complaint_added_successfully), Toast.LENGTH_SHORT).show();
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
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void loadData(){
        loadingHelper.showLoading("loading data...");
        new ComplaintsController().getCustomerComplaints(SharedData.currentCustomer, new ComplaintCallback() {
            @Override
            public void onSuccess(ArrayList<Complaint> gallery) {
                loadingHelper.dismissLoading();
                ordersList.setAdapter(new ComplaintAdapter(gallery));
            }

            @Override
            public void onFail(String msg) {
                loadingHelper.dismissLoading();
            }


        });
    }

    private class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {
        ArrayList<Complaint> complaints;

        public ComplaintAdapter(ArrayList<Complaint> complaints) {
            this.complaints = complaints;
        }

        @NonNull
        @Override
        public ComplaintAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.complaint_row,parent,false);
            return new ComplaintAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ComplaintAdapter.ViewHolder holder, int position) {
            Complaint complaint = complaints.get(position);


            holder.title.setText(new StringBuilder().append(getString(R.string.customer_)).append(complaint.getCustomer().getName()).toString());
            holder.content.setText(new StringBuilder().append(getString(R.string.title_)).append(complaint.getTitle()).append(getString(R.string.description_)).append(complaint.getDescription()).toString());
            holder.reply.setText(new StringBuilder().append(getString(R.string.reply)).append(complaint.getReply()).toString());




        }

        @Override
        public int getItemCount() {
            return complaints.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{


            TextView title,content,reply;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.name);
                content = itemView.findViewById(R.id.state);
                reply = itemView.findViewById(R.id.reply);
            }
        }
    }


}