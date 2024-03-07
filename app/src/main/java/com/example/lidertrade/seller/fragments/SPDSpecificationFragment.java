package com.example.lidertrade.seller.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminProductSpecificationsModel;
import com.example.lidertrade.seller.adapters.SPDSpecificationFragmentAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SPDSpecificationFragment extends Fragment {
        View view;
        RecyclerView productSpecification;
        ArrayList<AdminProductSpecificationsModel> sPDAModel;
        FirebaseFirestore db;
        SPDSpecificationFragmentAdapter spdSpecificationFragmentAdapter;
        CollectionReference products;
        TextView productDescription, idProductDesc;
        public SPDSpecificationFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.s_p_d_specification_fragment, container, false);
            db = FirebaseFirestore.getInstance();
            products = db.collection("products");
            Bundle bundle=getArguments();


            productSpecification = view.findViewById(R.id.productSpecification);
            idProductDesc = view.findViewById(R.id.idProductDesc);
            sPDAModel = new ArrayList<>();
            productSpecification.setHasFixedSize(true);
            productSpecification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            spdSpecificationFragmentAdapter = new SPDSpecificationFragmentAdapter(sPDAModel, getContext());
            productSpecification.setAdapter(spdSpecificationFragmentAdapter);
            spdSpecificationFragmentAdapter.notifyDataSetChanged();

            if (bundle != null)
            {
                String prodId = bundle.getString("productId");
                loadDataToRW(prodId);
            }

            return view;
        }

        private void loadDataToRW(String prodId) {
            products.document(prodId).get().addOnSuccessListener(d -> {
                if(d.exists()){
                    idProductDesc.setText(d.get("productDescription").toString());
                    AdminProductModel prod = d.toObject(AdminProductModel.class);
                    assert prod != null;
                    List<AdminProductSpecificationsModel> list = prod.getProductSpecification();
                    sPDAModel.addAll(list);
                    spdSpecificationFragmentAdapter.notifyDataSetChanged();
                }
            });
        }

    }