package com.example.lidertrade.seller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.seller.adapters.SCFCategoryAdapter;
import com.example.lidertrade.seller.models.SellerCategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SellerCategoryFragment extends Fragment {
    private ArrayList<SellerCategoryModel> sellerCategoryModelArrayList;
    private RecyclerView cFCategoryRV;
    private SCFCategoryAdapter categoryAdapter;
    FirebaseFirestore db;
    CollectionReference categories;

    public SellerCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.seller_category_fragment, container, false);
        db = FirebaseFirestore.getInstance();

        cFCategoryRV = (RecyclerView) v.findViewById(R.id.cFCategoryRV);
        sellerCategoryModelArrayList = new ArrayList<>();
        cFCategoryRV.setHasFixedSize(true);
        cFCategoryRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        categories = db.collection("Categories");
        categoryAdapter = new SCFCategoryAdapter(sellerCategoryModelArrayList, getContext());
        cFCategoryRV.setAdapter(categoryAdapter);
        loadCategoryRecyclerviewData();

        return v;
    }

    private void loadCategoryRecyclerviewData() {
        categories.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                SellerCategoryModel dataModal = d.toObject(SellerCategoryModel.class);
                                sellerCategoryModelArrayList.add(dataModal);
                                assert dataModal != null;
                                categoryAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(getContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}