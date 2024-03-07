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
import com.example.lidertrade.deliverer.adapters.DelivererSendingFragmentAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DelivererSendingFragment extends Fragment {

    private View view;

    private ArrayList<AdminOrderModel> orderSendingListFragmentModel;

    private DelivererSendingFragmentAdapter orderSendingListFragmentAdapter;
    private RecyclerView orderSendingListFragmentRecyclerView;

    FirebaseFirestore db;
    CollectionReference order;

    public DelivererSendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.deliverer_sending_fragment, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
// CATEGORY!!!!!!!!!!!!!
        orderSendingListFragmentRecyclerView = (RecyclerView) view.findViewById(R.id.orderSendingListRecyclerView);
        orderSendingListFragmentModel = new ArrayList<>();
        orderSendingListFragmentRecyclerView.setHasFixedSize(true);
        orderSendingListFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        order = db.collection("Orders");
        orderSendingListFragmentAdapter = new DelivererSendingFragmentAdapter(orderSendingListFragmentModel, getContext());
        orderSendingListFragmentRecyclerView.setAdapter(orderSendingListFragmentAdapter);

        loadOrderData();

        return view;
    }


    private void loadOrderData() {
        order.whereEqualTo("packageStatus", 1).orderBy("orderPlacedTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    String Tag = null;
                    Log.e(Tag,"onEvent", error);
                    return;
                }
                if (value != null){
                    orderSendingListFragmentModel.clear();
                    for (QueryDocumentSnapshot d:value){
                        AdminOrderModel dataModal = d.toObject(AdminOrderModel.class);
                        orderSendingListFragmentModel.add(dataModal);
                        orderSendingListFragmentAdapter.notifyDataSetChanged();
                    }
                }else {
                    String Tag = null;
                    Log.e(Tag,"Xatolik bor!!!!");
                }
            }
        });
    }
}