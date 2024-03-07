package com.example.lidertrade.seller.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lidertrade.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SPDDescriptionFragment extends Fragment {
    View view;
    FirebaseFirestore db;
    CollectionReference products;
    TextView productDescription;
    public SPDDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.s_p_d_description_fragment, container, false);
        productDescription = view.findViewById(R.id.productDescription);
        db = FirebaseFirestore.getInstance();
        products = db.collection("products");
        Bundle bundle=getArguments();
        if (bundle != null)
        {
            String prodId = bundle.getString("productId");
            products.document(prodId).get().addOnSuccessListener(d -> {
                if(d.exists()){
                    productDescription.setText(d.get("productDescription").toString());
                }
            });

        }

        return view;
    }

}