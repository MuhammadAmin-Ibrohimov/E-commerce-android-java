package com.example.lidertrade.admin.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminOrderFragmentCreditAdapter;
import com.example.lidertrade.deliverer.models.DelivererCreditModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminOrderFragmentCredit extends Fragment {
    private View view;
    SearchView searchView;
    private ArrayList<DelivererCreditModel> delivererCreditModel;
    private AdminOrderFragmentCreditAdapter delivererCreditFragmentAdapter;
    private RecyclerView orderCreditRecyclerView;

    FirebaseFirestore db;
    CollectionReference order;

    public AdminOrderFragmentCredit() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_order_fragment_credit, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
// CATEGORY!!!!!!!!!!!!!
        orderCreditRecyclerView = (RecyclerView) view.findViewById(R.id.orderCreditRecyclerView);
        delivererCreditModel = new ArrayList<>();
        orderCreditRecyclerView.setHasFixedSize(true);
        orderCreditRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        order = db.collection("Credits");
        delivererCreditFragmentAdapter = new AdminOrderFragmentCreditAdapter(delivererCreditModel, getContext());
        orderCreditRecyclerView.setAdapter(delivererCreditFragmentAdapter);
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
        loadOrderData();

        return view;
    }

    private void loadOrderData() {
        order.orderBy("orderPlacedTime", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
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

    private void filterList(String s) {
        ArrayList<DelivererCreditModel> filteredList = new ArrayList<>();
        for (DelivererCreditModel model: delivererCreditModel){
            if (model.getCustomerName().toLowerCase().contains(s.toLowerCase().trim())   ||
                    String.valueOf(model.getPackageStatus()).toLowerCase().contains(s.toLowerCase().trim())){
                filteredList.add(model);
            }
            else if (model.getCustomerPhoneNumber1().toLowerCase().contains(s.toLowerCase().trim()) ){
                filteredList.add(model);
            }
            else if (model.getCustomerVillage().toLowerCase().contains(s.toLowerCase().trim()) ){
                filteredList.add(model);
            }else if (model.getCustomerDistrict().toLowerCase().contains(s.toLowerCase().trim()) ){
                filteredList.add(model);
            }
        }
        this.delivererCreditFragmentAdapter.setFilteredList(filteredList);
        this.orderCreditRecyclerView.setAdapter(delivererCreditFragmentAdapter);
    }
}