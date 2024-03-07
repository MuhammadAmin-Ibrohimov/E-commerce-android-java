package com.example.lidertrade.admin.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminUserFragmentUserListAdapter;
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.example.lidertrade.admin.models.AdminUserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
public class AdminUserFragmentUserList extends Fragment {

    private View view;
    SearchView searchView;
    ProgressBar progressBar;

    private ArrayList<AdminUserModel> adminUserFragmentAddUserModel;

    private AdminUserFragmentUserListAdapter adminUserFragmentUserListAdapter;
    private RecyclerView userLisRecyclerView;

    FirebaseFirestore db;
    CollectionReference users;

    public AdminUserFragmentUserList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_user_fragment_user_list, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.progressBar);
        userLisRecyclerView = view.findViewById(R.id.userLisRecyclerView);
        adminUserFragmentAddUserModel = new ArrayList<>();
        userLisRecyclerView.setHasFixedSize(true);
        userLisRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        users = db.collection("Users");
        adminUserFragmentUserListAdapter = new AdminUserFragmentUserListAdapter(adminUserFragmentAddUserModel, getContext());
        userLisRecyclerView.setAdapter(adminUserFragmentUserListAdapter);
        adminUserFragmentUserListAdapter.setOnItemClickListener(position -> new SweetAlertDialog(requireActivity(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Eslatma")
                .setContentText("Foydalanuvchiga tegishli barcha ma'lumotlar ham o'chirib yuboriladi. Rozimisiz?")
                .setCancelText("Yo'q")
                .setConfirmText("Ha")
                .setCancelButtonBackgroundColor(Color.CYAN)
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmButtonBackgroundColor(Color.RED)
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    progressBar.setVisibility(View.VISIBLE);
                    DocumentReference docRef = users.document(adminUserFragmentAddUserModel.get(position).getUserId());
                    docRef.get().addOnSuccessListener(d -> {
                        if(d.exists() && d.get("userImage")!=null){
                            StorageReference photoRef = FirebaseStorage.getInstance()
                                    .getReferenceFromUrl((String) Objects.requireNonNull(d.get("userImage")));
                            photoRef.delete()
                                    .addOnSuccessListener(aVoid ->
                                        docRef .delete()
                                        .addOnSuccessListener(unused -> {
                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                    .setContentText("Muvafaqqiyatli o'chirildi!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                            progressBar.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Jarayonda xatolik!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                            progressBar.setVisibility(View.GONE);

                                        }))
                                    .addOnFailureListener(exception -> {
                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setContentText("Jarayonda xatolik!")
                                                .setConfirmText("OK!")
                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                .show();
                                        progressBar.setVisibility(View.GONE);
                            });

                            progressBar.setVisibility(View.GONE);
                        }else if(d.exists() && d.get("userImage")==null){
                            docRef .delete()
                                    .addOnSuccessListener(unused -> {
                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setContentText("Muvafaqqiyatli o'chirildi!")
                                                .setConfirmText("OK!")
                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                .show();
                                        progressBar.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener(e -> {
                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setContentText("Jarayonda xatolik!")
                                                .setConfirmText("OK!")
                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                .show();
                                        progressBar.setVisibility(View.GONE);

                                    });
                        }
                        else{
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Jarayonda xatolik!")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                    .show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    sweetAlertDialog1.cancel();
                })
                .show());

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

        loadUsersList();

        return view;
    }
    private void loadUsersList() {
        users.addSnapshotListener((value, error) -> {
            if (error!=null){
                String Tag = null;
                Log.e(Tag,"onEvent", error);
                return;
            }
            if (value != null && !value.isEmpty()){
                adminUserFragmentAddUserModel.clear();

                for (QueryDocumentSnapshot d:value){
                    System.out.println(d.getData());
                    if (!(Objects.requireNonNull(d.get("userStatus"))).toString().equals("Admin")){

                        AdminUserModel dataModal = d.toObject(AdminUserModel.class);
                        adminUserFragmentAddUserModel.add(dataModal);
                        adminUserFragmentUserListAdapter.notifyDataSetChanged();
                    }
                }
            }else {
                adminUserFragmentAddUserModel.clear();
                String Tag = null;
                Log.e(Tag,"Xatolik bor!!!!");
                adminUserFragmentUserListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void filterList(String s) {
        ArrayList<AdminUserModel> filteredList = new ArrayList<>();

        for (AdminUserModel model: adminUserFragmentAddUserModel){
            if (model.getUserName().toLowerCase().contains(s.toLowerCase().trim()) ||
                    model.getUserPhone().toLowerCase().contains(s.toLowerCase().trim())){
                filteredList.add(model);
            }

        }
        this.adminUserFragmentUserListAdapter.setFilteredList(filteredList);
        this.userLisRecyclerView.setAdapter(adminUserFragmentUserListAdapter);
    }
}