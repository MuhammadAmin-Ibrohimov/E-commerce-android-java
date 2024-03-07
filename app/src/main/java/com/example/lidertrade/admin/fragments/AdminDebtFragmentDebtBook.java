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
import com.example.lidertrade.admin.adapters.AdminDebtFragmentDebtBookAdapter;
import com.example.lidertrade.deliverer.models.DebtBookModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminDebtFragmentDebtBook extends Fragment {

    private View view;

    SearchView searchView;
    private ArrayList<DebtBookModel> debtBookModel;

    private AdminDebtFragmentDebtBookAdapter adminDebtFragmentDebtBookAdapter;
    private RecyclerView orderCreditRecyclerView;

    FirebaseFirestore db;
    CollectionReference order;

    public AdminDebtFragmentDebtBook() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_debt_fragment_debt_book, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
// CATEGORY!!!!!!!!!!!!!
        orderCreditRecyclerView = (RecyclerView) view.findViewById(R.id.orderCreditRecyclerView);
        debtBookModel = new ArrayList<>();
        orderCreditRecyclerView.setHasFixedSize(true);
        orderCreditRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        order = db.collection("DebtBook");
        adminDebtFragmentDebtBookAdapter = new AdminDebtFragmentDebtBookAdapter(debtBookModel, getContext());
        orderCreditRecyclerView.setAdapter(adminDebtFragmentDebtBookAdapter);
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
        order.addSnapshotListener((value, error) -> {
            if (error!=null){
                String Tag = null;
                Log.e(Tag,"onEvent", error);
                return;
            }
            if (value != null){
                debtBookModel.clear();
                for (QueryDocumentSnapshot d:value){
                    System.out.println(d.getData());
                    DebtBookModel dataModal = d.toObject(DebtBookModel.class);
                    debtBookModel.add(dataModal);
                    adminDebtFragmentDebtBookAdapter.notifyDataSetChanged();
                }
            }else {
                String Tag = null;
                Log.e(Tag,"Xatolik bor!!!!");
            }
        });
    }

    private void filterList(String s) {
        ArrayList<DebtBookModel> filteredList = new ArrayList<>();


        for (DebtBookModel model: debtBookModel){
            if (model.getCustomerName().toLowerCase().contains(s.toLowerCase().trim()) ){
                filteredList.add(model);
            }
            else if (model.getCustomerAddress().toLowerCase().contains(s.toLowerCase().trim()) ){
                filteredList.add(model);
            }
            else if (model.getCustomerPhone().toLowerCase().contains(s.toLowerCase().trim()) ){
                filteredList.add(model);
            }
        }
        this.adminDebtFragmentDebtBookAdapter.setFilteredList(filteredList);
        this.orderCreditRecyclerView.setAdapter(adminDebtFragmentDebtBookAdapter);
    }
}