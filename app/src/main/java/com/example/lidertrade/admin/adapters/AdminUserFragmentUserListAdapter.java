package com.example.lidertrade.admin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lidertrade.R;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminUserFragmentUserListAdapter extends RecyclerView.Adapter<AdminUserFragmentUserListAdapter.ViewHolder> {
    private ArrayList<AdminUserModel> dataModalArrayList;
    private Context context;
    FirebaseFirestore db;
    private OnItemClickListener listener;
    private TextView orderListTotalPrice;

    // constructor class for our Adapter
    public AdminUserFragmentUserListAdapter(ArrayList<AdminUserModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }
    public AdminUserFragmentUserListAdapter() {
    }
    public void setFilteredList(ArrayList<AdminUserModel> filteredList) {
        this.dataModalArrayList = filteredList;
    }

    @NonNull
    @Override
    public AdminUserFragmentUserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.admin_user_fragment_user_list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserFragmentUserListAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        AdminUserModel modal = dataModalArrayList.get(position);
        holder.userListName.setText(modal.getUserName());
        holder.userListPhone.setText(String.valueOf(modal.getUserPhone()));
        holder.userListUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo(modal, holder);
            }
        });


    }

    private void updateUserInfo(AdminUserModel modal, ViewHolder holder) {
        db = FirebaseFirestore.getInstance();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_user_fragment_user_list_dialog);

        //Initializing the views of the dialog.
        final EditText userListNameUpdateInput = dialog.findViewById(R.id.userListNameUpdateInput);
        userListNameUpdateInput.setText(modal.getUserName());
        final EditText userListPhoneUpdateInput = dialog.findViewById(R.id.userListPhoneUpdateInput);
        userListPhoneUpdateInput.setText(modal.getUserPhone());
        final EditText userListUsernameUpdateInput = dialog.findViewById(R.id.userListUsernameUpdateInput);
        userListUsernameUpdateInput.setText(modal.getUserUsername());
        final EditText userListPasswordUpdateInput = dialog.findViewById(R.id.userListPasswordUpdateInput);
        userListPasswordUpdateInput.setText(modal.getUserPassword());
        final EditText userListAddressUpdateInput = dialog.findViewById(R.id.userListAddressUpdateInput);
        userListAddressUpdateInput.setText(modal.getUserAddress());
        final EditText addUserUserCashSalaryPercent = dialog.findViewById(R.id.addUserUserCashSalaryPercent);
        addUserUserCashSalaryPercent.setText(String.valueOf(modal.getCashSalaryPercent()));
        final EditText addUserUserCreditSalaryPercent = dialog.findViewById(R.id.addUserUserCreditSalaryPercent);
        addUserUserCreditSalaryPercent.setText(String.valueOf(modal.getCreditSalaryPercent()));
        final String[] uLNUI = new String[1];
        final String[] uLPUI = new String[1];
        final String[] uLUUI = new String[1];
        final String[] uLPaUI = new String[1];
        final String[] uLAUI = new String[1];
        final String[] uLCaSP = new String[1];
        final String[] uLCrSP = new String[1];
        addUserUserCashSalaryPercent.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {uLCaSP[0] = charSequence.toString();}
            @Override public void afterTextChanged(Editable editable) { }
        });
        addUserUserCreditSalaryPercent.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {uLCrSP[0] = charSequence.toString();}
            @Override public void afterTextChanged(Editable editable) { }
        });
        userListNameUpdateInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {uLNUI[0] = charSequence.toString();}
            @Override public void afterTextChanged(Editable editable) { }
        });
        userListPhoneUpdateInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {uLPUI[0] = charSequence.toString();}
            @Override public void afterTextChanged(Editable editable) { }
        });
        userListUsernameUpdateInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {uLUUI[0] = charSequence.toString();}
            @Override public void afterTextChanged(Editable editable) { }
        });
        userListPasswordUpdateInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {uLPaUI[0] = charSequence.toString();}
            @Override public void afterTextChanged(Editable editable) { }
        });
        userListAddressUpdateInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {uLAUI[0] = charSequence.toString();}
            @Override public void afterTextChanged(Editable editable) { }
        });

        CardView userListAddressUpdateCompleteCard = dialog.findViewById(R.id.userListAddressUpdateCompleteCard);
        userListAddressUpdateCompleteCard.setOnClickListener(v -> {
            holder.progressBar.setVisibility(View.VISIBLE);
            String un = modal.getUserName(), up = modal.getUserPhone(), uu = modal.getUserUsername(),
                    ua = modal.getUserAddress(), upa = modal.getUserPassword();
            int ucas = modal.getCashSalaryPercent(), ucrs = modal.getCreditSalaryPercent();

            DocumentReference usersDoc = db.collection("Users").document(modal.getUserId());
            if (uLNUI[0]==null && uLPUI[0]==null && uLUUI[0]==null && uLAUI[0]==null && uLPaUI[0]==null && uLCaSP[0]==null && uLCrSP[0]==null ){
                holder.progressBar.setVisibility(View.GONE);
                new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("O'zgartirish kiritilmadi!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else {
                if(!Objects.equals(uLCaSP[0], modal.getCashSalaryPercent())   && uLCaSP[0] != null){
                    ucas = Integer.parseInt(uLCaSP[0]);
                }
                if(!Objects.equals(uLCrSP[0], modal.getCreditSalaryPercent())   && uLCrSP[0] != null){
                    ucrs = Integer.parseInt(uLCrSP[0]);
                }
                if(!Objects.equals(uLNUI[0], modal.getUserName())   && uLNUI[0] != null){
                    un = (uLNUI[0]);
                }
                if((!Objects.equals(uLPUI[0], modal.getUserPhone()))   && uLPUI[0] != null){
                    up = (uLPUI[0]);
                }
                if(!Objects.equals(uLUUI[0], modal.getUserUsername())   && uLUUI[0] != null){
                    String uEmail = uLUUI[0]+"@lider.trade";
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(modal.getUserEmail(), modal.getUserPassword());
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.updateEmail(uEmail).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            holder.progressBar.setVisibility(View.GONE);
                                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setContentText("Muvafaqqiyatli o'zgartirildi!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        } else {
                                            holder.progressBar.setVisibility(View.GONE);
                                            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Jarayonda xatolik!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        }
                                    });
                                } else {
                                    holder.progressBar.setVisibility(View.GONE);
                                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                            .setContentText("Jarayonda xatolik!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                            .show();
                                }
                            });


                    uu = (uLUUI[0]);
                }
                if(!Objects.equals(uLAUI[0], modal.getUserAddress()) && uLAUI[0] != null){
                    ua = (uLAUI[0]);
                }
                if((!Objects.equals(uLPaUI[0], modal.getUserPassword()))  && uLPaUI[0] != null){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(modal.getUserEmail(), modal.getUserPassword());
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.updatePassword(uLPaUI[0]).addOnCompleteListener(task12 -> {
                                        if (task12.isSuccessful()) {
                                            holder.progressBar.setVisibility(View.GONE);
                                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setContentText("Muvafaqqiyatli o'zgartirildi!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        } else {
                                            holder.progressBar.setVisibility(View.GONE);
                                            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Jarayonda xatolik!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        }
                                    });
                                } else {
                                    holder.progressBar.setVisibility(View.GONE);
                                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                            .setContentText("Jarayonda xatolik!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                            .show();
                                }
                            });

                    upa = (uLPaUI[0]);
                }
                usersDoc.update("userName", un, "userPhone",up, "userUsername", uu,
                        "userAddress", ua, "userPassword", upa, "cashSalaryPercent", ucas, "creditSalaryPercent", ucrs).addOnSuccessListener(unused -> {
                    holder.progressBar.setVisibility(View.GONE);
                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Muvafaqqiyatli o'zgartirildi!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }).addOnFailureListener(e -> {
                    holder.progressBar.setVisibility(View.GONE);
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Jarayonda xatolik!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                });
            }

            dialog.dismiss();
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }


    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView userListName, userListPhone;
        ProgressBar progressBar;
        CardView userListUpdate, userListDelete;
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // initializing the views of recycler views.
            userListName = itemView.findViewById(R.id.userListName);
            userListPhone = itemView.findViewById(R.id.userListPhone);
            userListUpdate = itemView.findViewById(R.id.userListUpdate);
            userListDelete = itemView.findViewById(R.id.userListDelete);
            progressBar = itemView.findViewById(R.id.progressBar);

            userListDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public  void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }
}
