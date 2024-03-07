package com.example.lidertrade.deliverer.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.example.lidertrade.deliverer.adapters.DelivererPendingFragmentAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DelivererPendingFragment extends Fragment {

    private View view;

    private ArrayList<AdminOrderModel> orderPendingListFragmentModel;

    private DelivererPendingFragmentAdapter orderPendingListFragmentAdapter;
    private RecyclerView orderListRecyclerView;

    FirebaseFirestore db;
    CollectionReference orders;

    public DelivererPendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.deliverer_pending_fragment, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
// CATEGORY!!!!!!!!!!!!!
        orderListRecyclerView = (RecyclerView) view.findViewById(R.id.orderListRecyclerView);
        orderPendingListFragmentModel = new ArrayList<>();
        orderListRecyclerView.setHasFixedSize(true);
        orderListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        orders = db.collection("Orders");
        orderPendingListFragmentAdapter = new DelivererPendingFragmentAdapter(orderPendingListFragmentModel, getContext());
        orderListRecyclerView.setAdapter(orderPendingListFragmentAdapter);

        loadOrdersData();

        return view;
    }
    private void loadOrdersData() {
        orders.whereEqualTo("packageStatus", 0).orderBy("orderPlacedTime", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error!=null){
                String Tag = null;
                Log.e(Tag,"onEvent", error);
                return;
            }
            if (value != null && !value.isEmpty()){
                orderPendingListFragmentModel.clear();

                for (QueryDocumentSnapshot d:value){
                    AdminOrderModel dataModal = d.toObject(AdminOrderModel.class);
                    orderPendingListFragmentModel.add(dataModal);

                    orderPendingListFragmentAdapter.notifyDataSetChanged();
                }
            }else {
                orderPendingListFragmentModel.clear();
                String Tag = null;
                Log.e(Tag,"Xatolik bor!!!!");
                orderPendingListFragmentAdapter.notifyDataSetChanged();
            }

        });
    }
}