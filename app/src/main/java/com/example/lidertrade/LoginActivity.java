package com.example.lidertrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.lidertrade.admin.activities.AdminMainActivity;
import com.example.lidertrade.deliverer.activities.DelivererMainActivity;
import com.example.lidertrade.seller.activities.SellerHomeActivity;
import com.example.lidertrade.seller.activities.SellerSubcategoryActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText userLogin, userPassword;
    private TextInputLayout userLoginField, userPasswordField;
    private ProgressBar progressbar;
    public static final String SHARED_PREFS = "sharedPrefs";
    private CheckBox checkBox;
    private Button submitBtn;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.login_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userLogin = findViewById(R.id.userLogin);
        userPassword = findViewById(R.id.userPassword);
        submitBtn = findViewById(R.id.submitBtn);
        progressbar = findViewById(R.id.progressBar);
        userLoginField = findViewById(R.id.userLoginField);
        userPasswordField = findViewById(R.id.userPasswordField);

        userLogin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { userLoginField.setError(null); }
            @Override public void afterTextChanged(Editable editable) { }
        });
        userPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { userPasswordField.setError(null); }
            @Override public void afterTextChanged(Editable editable) { }
        });

        submitBtn.setOnClickListener(view -> {
            progressbar.setVisibility(View.VISIBLE);
            userLogin();
        });
    }

    private void userLogin() {
        String username = userLogin.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            userLoginField.setError("Foydalanuchi nomini kiritng");
        } else {
            progressbar.setVisibility(View.VISIBLE);
            password.length();
            String email = username + "@lider.trade";
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        String id = user.getUid();
                        System.out.println(id);
                        DocumentReference doc = db.collection("Users").document(id);
                        doc.get().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    System.out.println(documentSnapshot.getData());
                                    String data = Objects.requireNonNull(documentSnapshot.get("userStatus")).toString();
                                    switch (data) {
                                        case "Sotuvchi": {
                                            Intent i = new Intent(LoginActivity.this, SellerHomeActivity.class);
                                            startActivity(i);
                                            break;
                                        }
                                        case "Admin": {
                                            Intent i = new Intent(LoginActivity.this, AdminMainActivity.class);
                                            startActivity(i);
                                            break;
                                        }
                                        case "Yetkazuvchi": {
                                            Intent i = new Intent(LoginActivity.this, DelivererMainActivity.class);
                                            startActivity(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                        progressbar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Xatolik bor!").setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel).show();
                        progressbar.setVisibility(View.GONE);
                    });
        }


    }
}