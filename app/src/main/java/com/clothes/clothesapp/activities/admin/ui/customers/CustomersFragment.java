package com.clothes.clothesapp.activities.admin.ui.customers;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.callback.CustomerCallback;
import com.clothes.clothesapp.controller.CustomersController;
import com.clothes.clothesapp.model.Customer;
import com.clothes.clothesapp.utils.LoadingHelper;

import java.util.ArrayList;

public class CustomersFragment extends Fragment {

    private RecyclerView customersList;
    private LoadingHelper loadingHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home, container, false);

        customersList = root.findViewById(R.id.advices_list);
        loadingHelper = new LoadingHelper(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        customersList.setLayoutManager(mLayoutManager);
        customersList.setItemAnimator(new DefaultItemAnimator());



        loadData();
        return root;

    }

    private void loadData(){
        loadingHelper.showLoading("loading data...");
        new CustomersController().getCustomers(new CustomerCallback() {
            @Override
            public void onSuccess(ArrayList<Customer> customers) {
                loadingHelper.dismissLoading();
                customersList.setAdapter(new CustomersFragment.CustomersAdapter(customers));
            }

            @Override
            public void onFail(String msg) {
                loadingHelper.dismissLoading();
            }


        });
    }

    private class CustomersAdapter extends RecyclerView.Adapter<CustomersFragment.CustomersAdapter.ViewHolder> {
        ArrayList<Customer> customers;

        public CustomersAdapter(ArrayList<Customer> customers) {
            this.customers = customers;
        }

        @NonNull
        @Override
        public CustomersFragment.CustomersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.customer_row,parent,false);
            return new CustomersFragment.CustomersAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomersFragment.CustomersAdapter.ViewHolder holder, int position) {
            Customer customer = customers.get(position);



            holder.title.setText(customer.getName());

            if(customer.getState() == 0){
                holder.content.setText(getString(R.string.waiting));
            }else if(customer.getState() == 1){
                holder.content.setText(getString(R.string.accepted));
            }else{
                holder.content.setText(getString(R.string.rejected));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInfoDialog(customer);
                }
            });


        }

        private void showInfoDialog(Customer customer){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.customer_info,null);

            builder.setView(view);
            LinearLayout actionsArea = view.findViewById(R.id.actions_area);
            TextView name = view.findViewById(R.id.name);
            TextView email = view.findViewById(R.id.email);
            TextView state = view.findViewById(R.id.state);
            TextView accept = view.findViewById(R.id.accept);
            TextView reject = view.findViewById(R.id.reject);

            if(customer.getState() == 1){
                accept.setVisibility(View.INVISIBLE);
            }else if(customer.getState() == -1){
                reject.setVisibility(View.INVISIBLE);
            }

            email.setText(customer.getEmail());
            name.setText(customer.getName());
            if(customer.getState() == 0){
                state.setText(getString(R.string.waiting));
            }else if(customer.getState() == 1){
                state.setText(getString(R.string.accepted));
            }else{
                state.setText(getString(R.string.rejected));
            }
            AlertDialog dialog = builder.create();
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customer.setState(1);
                    loadingHelper.showLoading("Updating State");
                    new CustomersController().Save(customer, new CustomerCallback() {
                        @Override
                        public void onSuccess(ArrayList<Customer> customers) {
                            dialog.dismiss();
                            loadingHelper.dismissLoading();
                            Toast.makeText(getContext(), getString(R.string.customer_accepted_successfully), Toast.LENGTH_SHORT).show();
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
                    customer.setState(-1);
                    loadingHelper.showLoading("Updating State");
                    new CustomersController().Save(customer, new CustomerCallback() {
                        @Override
                        public void onSuccess(ArrayList<Customer> customers) {
                            loadingHelper.dismissLoading();
                            dialog.dismiss();
                            Toast.makeText(getContext(), getString(R.string.customer_rejected_successfully), Toast.LENGTH_SHORT).show();
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
            return customers.size();
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