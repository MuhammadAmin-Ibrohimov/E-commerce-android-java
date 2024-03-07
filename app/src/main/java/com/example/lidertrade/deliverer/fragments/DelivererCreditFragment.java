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
import com.example.lidertrade.deliverer.adapters.DelivererCreditFragmentAdapter;
import com.example.lidertrade.deliverer.adapters.DelivererPendingFragmentAdapter;
import com.example.lidertrade.deliverer.adapters.DelivererSendingFragmentAdapter;
import com.example.lidertrade.deliverer.models.DelivererCreditModel;
import com.example.lidertrade.deliverer.models.DelivererPendingFragmentModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DelivererCreditFragment extends Fragment {

    private View view;

    private ArrayList<DelivererCreditModel> delivererCreditModel;

    private DelivererCreditFragmentAdapter delivererCreditFragmentAdapter;
    private RecyclerView orderCreditRecyclerView;

    FirebaseFirestore db;
    CollectionReference order;

    public DelivererCreditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.deliverer_credit_fragment, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // CATEGORY!!!!!!!!!!!!!
        orderCreditRecyclerView = (RecyclerView) view.findViewById(R.id.orderCreditRecyclerView);
        delivererCreditModel = new ArrayList<>();
        orderCreditRecyclerView.setHasFixedSize(true);
        orderCreditRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        order = db.collection("Credits");
        delivererCreditFragmentAdapter = new DelivererCreditFragmentAdapter(delivererCreditModel, getContext());
        orderCreditRecyclerView.setAdapter(delivererCreditFragmentAdapter);

        loadOrderData();

        return view;
    }


    private void loadOrderData() {
        order.whereEqualTo("packageStatus", 0).orderBy("orderPlacedTime", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error!=null){
                String Tag = null;
                Log.e(Tag,"onEvent", error);
                return;
            }
            if (value != null){
                delivererCreditModel.clear();
                for (QueryDocumentSnapshot d:value){
                    DelivererCreditModel dataModal = d.toObject(DelivererCreditModel.class);
                    delivererCreditModel.add(dataModal);
                    delivererCreditFragmentAdapter.notifyDataSetChanged();
                }
            }else {
                String Tag = null;
                Log.e(Tag,"Xatolik bor!!!!");
            }
        });
    }
}