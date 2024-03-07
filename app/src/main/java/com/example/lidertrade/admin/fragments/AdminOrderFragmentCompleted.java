package com.example.lidertrade.admin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminOrderFragmentCompletedAdapter;
import com.example.lidertrade.admin.adapters.AdminOrderFragmentUncompletedAdapter;
import com.example.lidertrade.admin.models.AdminBrandModel;
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AdminOrderFragmentCompleted extends Fragment {

    private View view;

    private ArrayList<AdminOrderModel> orderPendingListFragmentModel;

    private AdminOrderFragmentCompletedAdapter orderPendingListFragmentAdapter;
    private RecyclerView orderListRecyclerView;

    FirebaseFirestore db;
    CollectionReference orders;
    SearchView searchView;

    public AdminOrderFragmentCompleted() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_order_fragment_completed, container, false);
        db = FirebaseFirestore.getInstance();
// CATEGORY!!!!!!!!!!!!!
        orderListRecyclerView = (RecyclerView) view.findViewById(R.id.orderListRecyclerView);
        orderPendingListFragmentModel = new ArrayList<>();
        orderListRecyclerView.setHasFixedSize(true);
        orderListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        orders = db.collection("Orders");
        orderPendingListFragmentAdapter = new AdminOrderFragmentCompletedAdapter(orderPendingListFragmentModel, getContext());
        orderListRecyclerView.setAdapter(orderPendingListFragmentAdapter);
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return false;
            }
        });
        loadOrdersData();

        return view;
    }
    private void loadOrdersData() {
        orders.orderBy("orderCompletedTime", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error!=null){
                        String Tag = null;
                        Log.e(Tag,"onEvent", error);
                        return;
                    }
                    if (value != null && !value.isEmpty()){
                        orderPendingListFragmentModel.clear();
                        for (QueryDocumentSnapshot d:value){
                            if      (Long.parseLong(Objects.requireNonNull(d.get("packageStatus")).toString()) == -1 ||
                                    Long.parseLong(Objects.requireNonNull(d.get("packageStatus")).toString()) == 2 ||
                                    Long.parseLong(Objects.requireNonNull(d.get("packageStatus")).toString()) == -2){
                                AdminOrderModel dataModal = d.toObject(AdminOrderModel.class);
                                orderPendingListFragmentModel.add(dataModal);
                                orderPendingListFragmentAdapter.notifyDataSetChanged();
                            }

                        }
                    }else {
                        orderPendingListFragmentModel.clear();
                        String Tag = null;
                        Log.e(Tag,"Xatolik bor!!!!");
                        orderPendingListFragmentAdapter.notifyDataSetChanged();
                    }

                });
    }
    private void filterList(String s) {
        ArrayList<AdminOrderModel> filteredList = new ArrayList<>();


        for (AdminOrderModel model: orderPendingListFragmentModel){
            if (model.getCustomerName().toLowerCase().contains(s.toLowerCase().trim())  ||
                    String.valueOf(model.getPackageStatus()).toLowerCase().contains(s.toLowerCase().trim())){
                filteredList.add(model);
            }

        }
        this.orderPendingListFragmentAdapter.setFilteredList(filteredList);
        this.orderListRecyclerView.setAdapter(orderPendingListFragmentAdapter);
    }
}