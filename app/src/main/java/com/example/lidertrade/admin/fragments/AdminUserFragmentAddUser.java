package com.example.lidertrade.admin.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminUserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class AdminUserFragmentAddUser extends Fragment {
    TextView addUserUserNameShow, addUserUserAddressShow, addUserUserUsernameShow, addUserUserPasswordShow;
    TextInputLayout addUserUserNameL;
    TextInputLayout addUserUserAddressL;
    TextInputLayout addUserUserUsernameL;
    TextInputLayout addUserUserPasswordL;
    TextInputLayout addUserUserPhoneL, addUserUserCashSalaryPercentL,addUserUserCreditSalaryPercentL;
    TextInputEditText addUserUserName, addUserUserAddress, addUserUserUsername,
            addUserUserPassword, addUserUserPhone, addUserUserCashSalaryPercent,addUserUserCreditSalaryPercent;
    ImageView addUserUserImageShow;
    ProgressBar progressBar;
    CardView addUserUserImage, addUserCancelCard, addUserConfirmCard;
    AutoCompleteTextView addUserUserStatus;

    String Id;
    String test;
    private FirebaseAuth firebaseAuth;
    FirebaseOptions firebaseOptions;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    CollectionReference users;
    String defLoadImg, cusLoadImg;
    Uri imageUri;
    Button btnChoose, btnReset, btnSave;
    public String imgUrl;
    String[] items = {"Sotuvchi", "Yetkazuvchi"};
    ArrayList arrayList = new ArrayList(Arrays.asList(items));


    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.admin_user_fragment_add_user, container, false);
        //get firebase instance
        db = FirebaseFirestore.getInstance();
        users = db.collection("Users");


        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.admin_user_fragment_add_user_dropdown, arrayList);
        addUserUserStatus = v.findViewById(R.id.addUserUserStatus);
        addUserUserStatus.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        //TextViews for showing details
        addUserUserNameShow = v.findViewById(R.id.addUserUserNameShow);
        addUserUserImageShow = v.findViewById(R.id.addUserUserImageShow);
        addUserUserAddressShow = v.findViewById(R.id.addUserUserAddressShow);
        addUserUserUsernameShow = v.findViewById(R.id.addUserUserUsernameShow);
        addUserUserPasswordShow = v.findViewById(R.id.addUserUserPasswordShow);

        defLoadImg = (addUserUserImageShow.getDrawable().toString());

//        Firebase Instantiating
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

//      InputLayouts
        addUserUserNameL = v.findViewById(R.id.addUserUserNameL);
        addUserUserAddressL = v.findViewById(R.id.addUserUserAddressL);
        addUserUserPasswordL = v.findViewById(R.id.addUserUserPasswordL);
        addUserUserUsernameL = v.findViewById(R.id.addUserUserUsernameL);
        addUserUserPhoneL = v.findViewById(R.id.addUserUserPhoneL);
        addUserUserPhoneL = v.findViewById(R.id.addUserUserPhoneL);
        addUserUserCashSalaryPercentL = v.findViewById(R.id.addUserUserCashSalaryPercentL);
        addUserUserCreditSalaryPercentL = v.findViewById(R.id.addUserUserCreditSalaryPercentL);


        //Edit Texts
        addUserUserName = v.findViewById(R.id.addUserUserName);
        addUserUserAddress = v.findViewById(R.id.addUserUserAddress);
        addUserUserUsername = v.findViewById(R.id.addUserUserUsername);
        addUserUserPassword = v.findViewById(R.id.addUserUserPassword);
        addUserUserPhone = v.findViewById(R.id.addUserUserPhone);
        addUserUserCreditSalaryPercent = v.findViewById(R.id.addUserUserCreditSalaryPercent);
        addUserUserCashSalaryPercent = v.findViewById(R.id.addUserUserCashSalaryPercent);

        // Edittext Listeners
        EditTextListener(addUserUserNameL, addUserUserName, addUserUserNameShow);
        EditTextListener(addUserUserAddressL, addUserUserAddress, addUserUserAddressShow);
        EditTextListener(addUserUserUsernameL, addUserUserUsername, addUserUserUsernameShow);
        EditTextListener(addUserUserPasswordL, addUserUserPassword, addUserUserPasswordShow);
        EditTextListener(addUserUserPhoneL, addUserUserPhone, null);
        EditTextListener(addUserUserCreditSalaryPercentL, addUserUserCreditSalaryPercent, null);
        EditTextListener(addUserUserCashSalaryPercentL, addUserUserCashSalaryPercent, null);

        //CardViews
        addUserUserImage = v.findViewById(R.id.addUserUserImage);
        addUserCancelCard = v.findViewById(R.id.addUserCancelCard);
        addUserConfirmCard = v.findViewById(R.id.addUserConfirmCard);



        addUserConfirmCard.setOnClickListener(v1 -> {
            if (addUserUserName.length() == 0) {
                addUserUserNameL.setError("Foydalanuvchi FISH ini kiriting");
                return;
            } else if (addUserUserAddress.length() == 0) {
                addUserUserAddressL.setError("Foydalanuvchi manzilini kiriting");
                return;

            } else if (addUserUserUsername.length() == 0) {
                addUserUserUsernameL.setError("Foydalanuvchi nomini kiriting");
                return;

            } else if (addUserUserPassword.length() == 0) {
                addUserUserPasswordL.setError("Foydalanuvchi parolini kiriting");
                return;
            } else if (addUserUserPassword.length() < 6) {
                addUserUserPasswordL.setError("Foydalanuvchi paroli kamida 6 simvoldan iborat bo'lishi kerak");
                return;
            }  else if (addUserUserPhone.length() == 0) {
                addUserUserPhoneL.setError("Foydalanuvchi telefon raqamini kiriting");
                return;
            } else if (addUserUserPhone.length() < 9) {
                addUserUserPhoneL.setError("Noto'g'ri  telefon raqami kiritildi");
                return;
            }  else if (addUserUserCashSalaryPercent.length() == 0) {
                addUserUserCashSalaryPercentL.setError("Foydalanuvchi telefon raqamini kiriting");
                return;
            }  else if (addUserUserCreditSalaryPercent.length() == 0) {
                addUserUserCreditSalaryPercentL.setError("Foydalanuvchi telefon raqamini kiriting");
                return;
            }
            addUserUserUsername.getText().toString();
            users.whereEqualTo("userUsername",addUserUserUsername.getText().toString() ).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()){
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Bunday FOYDALANUVCHI NOMI mavjud!")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(sweetAlertDialog2 -> sweetAlertDialog2.cancel())
                                    .show();
                        }
                        else {
                            uploadDataToTheServer("+998"+addUserUserPhone.getText().toString(), addUserUserAddress.getText().toString(),
                                    addUserUserName.getText().toString(), addUserUserUsername.getText().toString(),
                                    addUserUserPassword.getText().toString(), addUserUserStatus.getText().toString(),
                                    addUserUserCashSalaryPercent.getText().toString(), addUserUserCreditSalaryPercent.getText().toString());
                        }
                    });

        });

        ActivityResultLauncher<String> getContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        addUserUserImageShow.setImageURI(result);
                        imageUri = result;
                    }
                }

        );

        ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        addUserUserImageShow.setImageBitmap(bitmap);
                        addUserUserImageShow.setMaxHeight(170);
                    }
                }

        );

        addUserUserImage.setOnClickListener(view -> getContent.launch("image/*"));

        addUserUserImageShow.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentActivityResultLauncher.launch(intent);
        });
        return v;
    }



    private void EditTextListener(TextInputLayout textInputLayout, TextInputEditText textInputEditText, TextView textView) {

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textView != null){
                    textView.setText(s);
                }
                textInputLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.admin_user_status_menu, menu);
    }

    public void uploadDataToTheServer(String phonenum, String addrs, String fullname, String uname, String pswd,
                                      String employeeStatus, String cashPricePercent, String creditPricePercent) {

        if (phonenum.isEmpty() || addrs.isEmpty() || fullname.isEmpty() || uname.isEmpty() ||
                pswd.isEmpty() || cashPricePercent.isEmpty() || creditPricePercent.isEmpty()) {
            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Xatolik bor!").setConfirmText("OK!")
                    .setConfirmClickListener(SweetAlertDialog::cancel).show();
        }else{
            cusLoadImg = (addUserUserImageShow.getDrawable().toString());
            if (employeeStatus.isEmpty()){
                employeeStatus = "Sotuvchi";
            }
            String userN = (Objects.requireNonNull(uname).replaceAll("\\s", "").toLowerCase());
            String userE = userN+"@lider.trade";
            if (!Objects.equals(cusLoadImg, defLoadImg)){
                Bitmap bitmap = ((BitmapDrawable) addUserUserImageShow.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
                byte[] data = baos.toByteArray();
                StorageReference subRef = storageReference.child("UsersImages/" + userN);
                UploadTask uploadTask = subRef.putBytes(data);
                progressBar.setVisibility(View.VISIBLE);

                String finalEmployeeStatus = employeeStatus;
                uploadTask.addOnSuccessListener(taskSnapshot -> subRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    String url = uri.toString();

                    firebaseAuth.createUserWithEmailAndPassword(userE, pswd).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            DocumentReference documentReference = db.collection("Users").document(userId);
                            AdminUserModel adminUserFragmentAddUserModel = new AdminUserModel(
                                    fullname, finalEmployeeStatus, phonenum,userE, userN,  pswd,url, addrs, userId,
                                    Integer.parseInt(cashPricePercent),Integer.parseInt(creditPricePercent) );
                            documentReference.set(adminUserFragmentAddUserModel).addOnSuccessListener(unused -> {
                                progressBar.setVisibility(View.GONE);
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("Yangi foydalanuvchi yaratildi!").setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel).show();
                            }).addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Xatolik bor!").setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel).show();
                            });
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Xatolik bor!").setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel).show();
                        }
                    });

                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Xatolik bor!").setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel).show();
                }));
            }
            else{
                String finalEmployeeStatus1 = employeeStatus;
                firebaseAuth.createUserWithEmailAndPassword(userE, pswd).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                        DocumentReference documentReference = db.collection("Users").document(userId);
                        AdminUserModel adminUserFragmentAddUserModel = new AdminUserModel(
                                fullname, finalEmployeeStatus1, phonenum,userE, userN,  pswd,null, addrs, userId,
                                Integer.parseInt(cashPricePercent),Integer.parseInt(creditPricePercent));
                        documentReference.set(adminUserFragmentAddUserModel).addOnSuccessListener(unused -> {
                            progressBar.setVisibility(View.GONE);
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Yangi foydalanuvchi yaratildi!").setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel).show();
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Xatolik bor!").setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel).show();
                        });
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Xatolik bor!").setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel).show();
                    }
                });
            }


            addUserUserName.setText("");
            addUserUserAddress.setText("");
            addUserUserUsername.setText("");
            addUserUserPassword.setText("");
            addUserUserCashSalaryPercent.setText("");
            addUserUserCreditSalaryPercent.setText("");
            addUserUserPhone.getText().clear();
            addUserUserPhone.clearFocus();
            addUserUserStatus.clearFocus();
            addUserUserImageShow.setImageResource(R.drawable.lider);
        }

    }
}