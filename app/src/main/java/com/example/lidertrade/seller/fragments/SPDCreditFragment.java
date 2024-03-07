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

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

public class SPDCreditFragment extends Fragment {
        View view;
        DecimalFormat decim = new DecimalFormat("#,###.##");
        FirebaseFirestore db;
        CollectionReference products;
        TextView  threeMonth, sixMonth, nineMonth, twelveMonth;
        public SPDCreditFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.s_p_d_credit_fragment, container, false);

            threeMonth = view.findViewById(R.id.threeMonth);
            sixMonth = view.findViewById(R.id.sixMonth);
            nineMonth = view.findViewById(R.id.nineMonth);
            twelveMonth = view.findViewById(R.id.twelveMonth);
            db = FirebaseFirestore.getInstance();
            products = db.collection("products");
            Bundle bundle=getArguments();
            if (bundle != null)
            {
                String prodId = bundle.getString("productId");
                products.document(prodId).get().addOnSuccessListener(d -> {
                    if(d.exists() && d.get("creditPayment")!=null && d.get("creditPrice")!=null){
                        long soldPrice = Long.parseLong(Objects.requireNonNull(d.get("creditPrice")).toString());
                        long creditPrice = soldPrice+soldPrice*36/100;
                        for (Map.Entry<String,Object> map:((Map<String, Object>) Objects.requireNonNull(d.get("creditPayment"))).entrySet()){
                            if(Objects.equals(map.getKey(), "12 oylik")){
                                twelveMonth.setText("Oyiga "+decim.format(Integer.parseInt(map.getValue().toString())) +" so'mdan");
                            }
                            else if(Objects.equals(map.getKey(), "9 oylik")){
                                nineMonth.setText("Oyiga "+decim.format(Integer.parseInt(map.getValue().toString())) +" so'mdan");
                            }
                            else if(Objects.equals(map.getKey(), "6 oylik")){
                                sixMonth.setText("Oyiga "+decim.format(Integer.parseInt(map.getValue().toString())) +" so'mdan");
                            }
                            else if(Objects.equals(map.getKey(), "3 oylik")){
                                threeMonth.setText("Oyiga "+decim.format(Integer.parseInt(map.getValue().toString())) +" so'mdan");
                            }
                        }


                    }
                });

            }

            return view;
        }

    }