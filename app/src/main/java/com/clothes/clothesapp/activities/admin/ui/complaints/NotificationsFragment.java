package com.clothes.clothesapp.activities.admin.ui.complaints;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.clothes.clothesapp.callback.ComplaintCallback;
import com.clothes.clothesapp.callback.ComplaintCallback;
import com.clothes.clothesapp.controller.ComplaintsController;
import com.clothes.clothesapp.controller.ComplaintsController;
import com.clothes.clothesapp.databinding.FragmentNotificationsBinding;
import com.clothes.clothesapp.model.Complaint;
import com.clothes.clothesapp.model.Complaint;
import com.clothes.clothesapp.utils.LoadingHelper;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private RecyclerView complaintsList;
    private LoadingHelper loadingHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        complaintsList = root.findViewById(R.id.advices_list);
        loadingHelper = new LoadingHelper(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        complaintsList.setLayoutManager(mLayoutManager);
        complaintsList.setItemAnimator(new DefaultItemAnimator());



        loadData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
    }

    private void loadData(){
        loadingHelper.showLoading("loading data...");
        new ComplaintsController().getComplaints(new ComplaintCallback() {
            @Override
            public void onSuccess(ArrayList<Complaint> complaints) {
                loadingHelper.dismissLoading();
                complaintsList.setAdapter(new ComplaintAdapter(complaints));
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(complaint.getReply().isEmpty()){
                        showInfoDialog(complaint);
                    }
                }
            });


        }

        private void showInfoDialog(Complaint complaint){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.complaint_info,null);

            builder.setView(view);

            EditText reply = view.findViewById(R.id.reply);
            TextView accept = view.findViewById(R.id.accept);
            TextView reject = view.findViewById(R.id.reject);


            AlertDialog dialog = builder.create();
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(reply.getText() == null){
                        reply.setError("Required");
                        return;
                    }
                    if(reply.getText().toString().isEmpty()){
                        reply.setError("Required");
                        return;
                    }
                    complaint.setReply(reply.getText().toString());
                    loadingHelper.showLoading("Updating State");
                    new ComplaintsController().Save(complaint, new ComplaintCallback() {
                        @Override
                        public void onSuccess(ArrayList<Complaint> complaints) {
                            dialog.dismiss();
                            loadingHelper.dismissLoading();
                            Toast.makeText(getContext(), getString(R.string.complaint_accepted_successfully), Toast.LENGTH_SHORT).show();
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