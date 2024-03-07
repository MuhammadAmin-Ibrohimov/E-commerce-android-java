package com.example.lidertrade.seller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminOrderFragmentCompletedAdapter;
import com.example.lidertrade.admin.adapters.AdminOrderFragmentUncompletedAdapter;
import com.example.lidertrade.admin.models.AdminBrandModel;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.seller.adapters.SHFAllProductsAdapter;
import com.example.lidertrade.seller.adapters.SellerSearchFragmentAdapter;
import com.example.lidertrade.seller.models.SHFAllProductsModel;
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

public class SellerSearchFragment extends Fragment {

    private View view;

    private ArrayList<AdminProductModel> orderPendingListFragmentModel;

    private SellerSearchFragmentAdapter orderPendingListFragmentAdapter;
    private RecyclerView orderListRecyclerView;

    FirebaseFirestore db;
    CollectionReference orders;
    SearchView searchView;

    public SellerSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.seller_search_fragment, container, false);
        db = FirebaseFirestore.getInstance();
// CATEGORY!!!!!!!!!!!!!
        orderListRecyclerView = (RecyclerView) view.findViewById(R.id.allProdRecyclerView);
        orderPendingListFragmentModel = new ArrayList<>();
        orderListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        orderListRecyclerView.setLayoutManager(layoutManager);
        orders = db.collection("products");
        orderPendingListFragmentAdapter = new SellerSearchFragmentAdapter(orderPendingListFragmentModel, getContext());
        orderListRecyclerView.setAdapter(orderPendingListFragmentAdapter);
        searchView = view.findViewById(R.id.allProdSearchView);
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
        orders.addSnapshotListener((value, error) -> {
                    if (error!=null){
                        String Tag = null;
                        Log.e(Tag,"onEvent", error);
                        return;
                    }
                    System.out.println(orderPendingListFragmentAdapter.getItemCount());
                    if (value != null && !value.isEmpty()){
                        orderPendingListFragmentModel.clear();

                        for (QueryDocumentSnapshot d:value){
                            AdminProductModel dataModal = d.toObject(AdminProductModel.class);
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
    private void filterList(String s) {
        ArrayList<AdminProductModel> filteredList = new ArrayList<>();


        for (AdminProductModel model: orderPendingListFragmentModel){
            if (model.getProductName().toLowerCase().contains(s.toLowerCase().trim()) ){
                filteredList.add(model);
            }

        }
        this.orderPendingListFragmentAdapter.setFilteredList(filteredList);
        this.orderListRecyclerView.setAdapter(orderPendingListFragmentAdapter);
    }
}