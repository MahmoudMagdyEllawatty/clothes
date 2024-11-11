package com.clothes.clothesapp.activities.customer.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.activities.tailor.ui.orders.OrderDetailsActivity;
import com.clothes.clothesapp.callback.OrderCallback;
import com.clothes.clothesapp.controller.OrderController;
import com.clothes.clothesapp.model.Order;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;

import java.util.ArrayList;

public class OrdersFragment extends Fragment {

    private RecyclerView ordersList;
    private LoadingHelper loadingHelper;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersList = root.findViewById(R.id.orders_list);
        loadingHelper = new LoadingHelper(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        ordersList.setLayoutManager(mLayoutManager);
        ordersList.setItemAnimator(new DefaultItemAnimator());

        loadData();
        return root;
    }

    private void loadData(){
        loadingHelper.showLoading("loading data...");
        new OrderController().getCustomerOrders(SharedData.currentCustomer, new OrderCallback() {
            @Override
            public void onSuccess(ArrayList<Order> gallery) {
                loadingHelper.dismissLoading();
                ordersList.setAdapter(new OrderAdapter(gallery));
            }

            @Override
            public void onFail(String msg) {
                loadingHelper.dismissLoading();
            }


        });
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        ArrayList<Order> gallery;

        public OrderAdapter(ArrayList<Order> gallery) {
            this.gallery = gallery;
        }

        @NonNull
        @Override
        public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.order_row,parent,false);
            return new OrderAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
            Order tailor = gallery.get(position);

            holder.name.setText(tailor.getCustomer().getName());
            holder.date.setText(tailor.getDate());

            if(tailor.getState() == 0){
                holder.state.setText(R.string.waiting_for_tailor);
                holder.state.setTextColor(getActivity().getColor(R.color.gray));
            }else if(tailor.getState() == 1){
                holder.state.setText(R.string.waiting_for_customer_payment);
                holder.state.setTextColor(getActivity().getColor(R.color.gray));
            }else if(tailor.getState() == 2){
                holder.state.setText(R.string.accepted_paid);
                holder.state.setTextColor(getActivity().getColor(R.color.green));
            }else if(tailor.getState() == -1){
                holder.state.setText(R.string.rejected);
                holder.state.setTextColor(getActivity().getColor(R.color.red));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedData.order = tailor;
                    Intent intent = new Intent(getActivity(), CustomerOrderDetailsActivity.class);
                    startActivity(intent);
                }
            });

        }


        @Override
        public int getItemCount() {
            return gallery.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{


            TextView name,date,state;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.name);
                date = itemView.findViewById(R.id.date);
                state = itemView.findViewById(R.id.state);
            }
        }
    }


}