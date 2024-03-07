package com.example.lidertrade.seller.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import com.example.lidertrade.R;
import com.example.lidertrade.deliverer.helpers.NumberTextWatcherForThousand;
import com.example.lidertrade.seller.activities.SellerHomeActivity;
import com.example.lidertrade.seller.adapters.SCFCartAdapter;
import com.example.lidertrade.seller.adapters.SellerCartCreditAdapter;
import com.example.lidertrade.seller.models.SellerCreditModel;
import com.example.lidertrade.seller.models.SellerCreditProductsModel;
import com.example.lidertrade.seller.models.SellerOrderModel;
import com.example.lidertrade.seller.models.SellerShoppingCartModel;
import com.example.lidertrade.seller.models.SellerSoldProductsModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.location.LocationRequest;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SellerCartFragment extends Fragment {
    View view1;
    FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;
    CardView makePaymentBtn, makeCreditBtn;
    FusedLocationProviderClient client;
    Long stringCustomerInitialPayment = Long.valueOf(0);
    Boolean isLocationPermitted;
    CollectionReference shoppingCartCollection, sellingCollection, soldProductsCollection, creditCollection, creditProductsCollection;
    DocumentReference orderDocRef, creditDocRef, soldProdDocRef, creditProdDocRef;
    private ArrayList<SellerShoppingCartModel> shoppingCartModelList;
    private ArrayList<SellerShoppingCartModel> shoppingCartModelListForCredit;
    SCFCartAdapter shoppingCartAdapter;
    SellerCartCreditAdapter sellerCartCreditAdapter;
    ImageView emptyCartImg;
    ArrayList<String> sellerStat;
    FirebaseUser user;
    ProgressBar progressBar;
    String idForStats;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    String sellerId;
    RecyclerView cartActivityRecyclerVeiw, creditProductRecycler;
    TextView shoppingCartTotalPrice;
    LinearLayout totalPriceLinearLayout;


    public SellerCartFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.seller_cart_fragment, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){assert user != null;sellerId = user.getUid();}else{sellerId = "null";}
        db = FirebaseFirestore.getInstance();

        sellingCollection = db.collection("Orders");
        creditCollection = db.collection("Credits");
        soldProductsCollection = db.collection("SoldProducts");
        creditProductsCollection = db.collection("CreditProducts");
        shoppingCartCollection = db.collection("ShoppingCart");

        orderDocRef = sellingCollection.document();
        creditDocRef = creditCollection.document();
        progressBar = view.findViewById(R.id.progressBar);
        cartActivityRecyclerVeiw = view.findViewById(R.id.cartActivityRecyclerVeiw);
        shoppingCartModelList = new ArrayList<>();
        isLocationPermitted = false;
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        cartActivityRecyclerVeiw.setHasFixedSize(true);
        cartActivityRecyclerVeiw.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        shoppingCartTotalPrice = view.findViewById(R.id.shoppingCartTotalPrice);
        shoppingCartAdapter = new SCFCartAdapter(shoppingCartModelList, getContext(), shoppingCartTotalPrice);
        cartActivityRecyclerVeiw.setAdapter(shoppingCartAdapter);
        shoppingCartAdapter.setOnItemClickListener(position -> {
            progressBar.setVisibility(View.VISIBLE);
            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setContentText("Haqiqatan ham o'chirmoqchimisiz?")
                    .setCancelText("Yo'q")
                    .setConfirmText("Ha")
                    .showCancelButton(true)
                    .setCancelClickListener(sweetAlertDialog -> {
                        progressBar.setVisibility(View.GONE);
                        sweetAlertDialog.cancel();
                    })
                    .setConfirmClickListener(sweetAlertDialog1 -> {
                        FirebaseFirestore.getInstance().collection("ShoppingCart")
                                .document(shoppingCartModelList.get(position).getCartId()).delete()
                                .addOnCompleteListener(task -> {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setContentText("Muvafaqqiyatli o'chirildi!")
                                                .setConfirmText("OK!")
                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                .show();
                                        sweetAlertDialog1.cancel();
                                    }
                                    else {
                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setContentText("Jarayonda xatolik!")
                                                .setConfirmText("OK!")
                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                .show();
                                        sweetAlertDialog1.cancel();
                                    }
                                });
                        shoppingCartModelList.remove(position);
                        shoppingCartAdapter.notifyItemRemoved(position);
                    })
                    .show();
        });
        emptyCartImg = view.findViewById(R.id.emptyCartImg);
        view1 = view.findViewById(R.id.view);
        makePaymentBtn = view.findViewById(R.id.makePaymentBtn);
        makeCreditBtn = view.findViewById(R.id.makeCreditBtn);

        makePaymentBtn.setOnClickListener(view1 -> getCustomerDataForInCash());
        makeCreditBtn.setOnClickListener(view2 -> getCustomerDataForCredit());
        loadShoppingCartRecyclerviewData();
        return view;
    }
    //    Removing all documents from ShoppingCart Start
    private void freeingShoppingCart() {
        shoppingCartCollection.whereEqualTo("sellerId", sellerId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    db.collection("ShoppingCart").document(d.getId())
                            .delete();
                }
            }
        });
    }
    //    Removing all documents from ShoppingCart End
    private Boolean checkRequiredField(EditText textView, TextInputLayout textInputLayout) {
        if (textView.length() == 0) {
            textInputLayout.setError("Maydonga ma'lumot kiriting");
            return false;
        }
        else{
            textInputLayout.setError("");
            return true;
        }
    }
    private void loadShoppingCartRecyclerviewData() {
        shoppingCartCollection.whereEqualTo("sellerId", sellerId).addSnapshotListener((value, error) -> {
            if (error!=null){
                Log.e(null,"onEvent", error);
                return;
            }
            if (value != null && !value.isEmpty()){
                shoppingCartModelList.clear();
                for (QueryDocumentSnapshot d:value){
                    if(Integer.parseInt(d.get("prodQuantity").toString())>0){
                        SellerShoppingCartModel dataModal = d.toObject(SellerShoppingCartModel.class);
                        shoppingCartModelList.add(dataModal);
                        shoppingCartTotalPrice.setText(String.valueOf(d.get("prodTotalPrice")));
                        int sum=0,i;
                        for(i=0; i< shoppingCartModelList.size(); i++)
                            sum = sum+(shoppingCartModelList.get(i).getCashPrice()* shoppingCartModelList.get(i).getProdQuantity());
                        shoppingCartTotalPrice.setText(String.format("%s so'm",decim.format(sum)));
                        shoppingCartAdapter.notifyDataSetChanged();
                        db.collection("ShoppingCart").document(d.getId()).update("prodQuantity",dataModal.getProdQuantity());
                    }else{
                        d.getReference().delete();
                    }
                }
            }else {
                emptyCartImg.setVisibility(View.VISIBLE);
                shoppingCartTotalPrice.setVisibility(View.INVISIBLE);
                makePaymentBtn.setVisibility(View.INVISIBLE);
                makeCreditBtn.setVisibility(View.INVISIBLE);
                view1.setVisibility(View.INVISIBLE);

            }
        });
    }

//    ------------------------------ Selling for credit Starts -----------------------------------------//
    private Boolean isValidPassportNo(String str){
        return str.matches("^([A-Z]{2}[0-9]{7}$)");
    }

    private void updateLabel(EditText customerBirthDay, Calendar myCalendar){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        customerBirthDay.setText(dateFormat.format(myCalendar.getTime()));
    }
    private void updateLabel2(EditText customerPassportGivenDate, Calendar myCalendar){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        customerPassportGivenDate.setText(dateFormat.format(myCalendar.getTime()));
    }
    private void getCustomerDataForCredit() {

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.seller_cart_fragment_credit_dialog);
        final ImageView dialogDismiss = dialog.findViewById(R.id.dialogDismiss);
        dialogDismiss.setOnClickListener(view -> dialog.dismiss());

        final EditText customerBirthDay = dialog.findViewById(R.id.customerBirthDay);
        final EditText customerPassportGivenDate = dialog.findViewById(R.id.customerPassportGivenDate);
        final Calendar myCalendar= Calendar.getInstance();
        final Calendar myCalendar2= Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel(customerBirthDay, myCalendar);
            }
        };
        customerBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(requireContext(),
                        date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        DatePickerDialog.OnDateSetListener date2 =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH,month);
                myCalendar2.set(Calendar.DAY_OF_MONTH,day);
                updateLabel2(customerPassportGivenDate, myCalendar2);
            }
        };
        customerPassportGivenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(requireContext(),
                        date2,myCalendar2.get(Calendar.YEAR),myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        final EditText customerPhoneNumber1 = dialog.findViewById(R.id.customerPhoneNumber1);
        final EditText customerPhoneNumber2 = dialog.findViewById(R.id.customerPhoneNumber2);
        final EditText customerPassport = dialog.findViewById(R.id.customerPassport);
        final EditText customerName = dialog.findViewById(R.id.customerName);
        final EditText customerHouse = dialog.findViewById(R.id.customerHouse);
        final EditText customerStreet = dialog.findViewById(R.id.customerStreet);
        final EditText customerVillage = dialog.findViewById(R.id.customerVillage);
        final EditText customerDistrict = dialog.findViewById(R.id.customerDistrict);
        final EditText customerPassportGiven = dialog.findViewById(R.id.customerPassportGiven);
        final EditText customerJob = dialog.findViewById(R.id.customerJob);
        final EditText customerStatus = dialog.findViewById(R.id.customerStatus);
        final EditText customerSalary = dialog.findViewById(R.id.customerSalary);
        customerSalary.addTextChangedListener(new NumberTextWatcherForThousand(customerSalary));
        final EditText customerInitialPayment = dialog.findViewById(R.id.customerInitialPayment);
        customerInitialPayment.addTextChangedListener(new NumberTextWatcherForThousand(customerInitialPayment));

        creditProductRecycler = dialog.findViewById(R.id.creditProductRecycler);
        shoppingCartModelListForCredit = new ArrayList<>();
        creditProductRecycler.setHasFixedSize(true);
        creditProductRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        sellerCartCreditAdapter = new SellerCartCreditAdapter(shoppingCartModelListForCredit, requireContext());
        creditProductRecycler.setAdapter(sellerCartCreditAdapter);

        loadShoppingCartForCredit();


        final TextInputLayout forCustomerInitialPayment = dialog.findViewById(R.id.forCustomerInitialPayment);
        final TextInputLayout forCustomerName = dialog.findViewById(R.id.forCustomerName);
        final TextInputLayout forCustomerBirthDay = dialog.findViewById(R.id.forCustomerBirthDay);
        final TextInputLayout forCustomerPassportGivenDate = dialog.findViewById(R.id.forCustomerPassportGivenDate);
        final TextInputLayout forCustomerPhoneNumber1 = dialog.findViewById(R.id.forCustomerPhoneNumber1);
        final TextInputLayout forCustomerPhoneNumber2 = dialog.findViewById(R.id.forCustomerPhoneNumber2);
        final TextInputLayout forCustomerPassport = dialog.findViewById(R.id.forCustomerPassport);
        final TextInputLayout forCustomerHouse = dialog.findViewById(R.id.forCustomerHouse);
        final TextInputLayout forCustomerStreet = dialog.findViewById(R.id.forCustomerStreet);
        final TextInputLayout forCustomerVillage = dialog.findViewById(R.id.forCustomerVillage);
        final TextInputLayout forCustomerDistrict = dialog.findViewById(R.id.forCustomerDistrict);
        final TextInputLayout forCustomerPassportGiven = dialog.findViewById(R.id.forCustomerPassportGiven);
        final TextInputLayout forCustomerJob = dialog.findViewById(R.id.forCustomerJob);
        final TextInputLayout forCustomerStatus = dialog.findViewById(R.id.forCustomerStatus);
        final TextInputLayout forCustomerSalary = dialog.findViewById(R.id.forCustomerSalary);
        final CheckBox checkboxOrderLocation = dialog.findViewById(R.id.checkboxOrderLocation);
        checkboxOrderLocation.setOnCheckedChangeListener((compoundButton, b) -> {if(b){ isLocationPermitted = true;}else isLocationPermitted=false;});
        final RadioGroup radioGroup1 = dialog.findViewById(R.id.radioGroup1);
        final RadioButton radioButton1 = dialog.findViewById(R.id.radioButton1);
        final RadioButton radioButton2 = dialog.findViewById(R.id.radioButton2);
        final RadioButton radioButton3 = dialog.findViewById(R.id.radioButton3);
        final RadioButton radioButton4 = dialog.findViewById(R.id.radioButton4);
        radioGroup1.clearCheck();
        shoppingCartCollection.whereEqualTo("sellerId", sellerId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()){
                long tq = 0;
                ArrayList<String> ids = new ArrayList<>();
                for(DocumentSnapshot d:queryDocumentSnapshots.getDocuments()){
                    if(d.exists() && d.get("totalCreditPrice")!=null){
                        tq += Integer.parseInt(Objects.requireNonNull(d.get("totalCreditPrice")).toString());
                        ids.add(d.getId());
                        if(ids.size() == queryDocumentSnapshots.getDocuments().size()){
//                            shoppingCartTotalPrice.setText(String.format("%s so'm",decim.format(sum)));
                            long finalTq = tq;
                            radioButton1.setText(" 3 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((tq +tq*9/100 )/3))+" so'mdan");
                            radioButton2.setText(" 6 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((tq +tq*18/100)/6))+" so'mdan");
                            radioButton3.setText(" 9 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((tq +tq*27/100)/9))+" so'mdan");
                            radioButton4.setText("12 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((tq +tq*36/100)/12))+" so'mdan");

                            customerInitialPayment.addTextChangedListener(new TextWatcher() {
                                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    try {
                                        if(charSequence.length()>0){
                                            long initPay = Long.parseLong(String.valueOf(charSequence).trim()
                                                    .replaceAll("[^A-Za-z0-9]", ""));
                                            radioButton1.setText(" 3 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*9/100 -initPay)/3))+" so'mdan");
                                            radioButton2.setText(" 6 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*18/100-initPay)/6))+" so'mdan");
                                            radioButton3.setText(" 9 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*27/100-initPay)/9))+" so'mdan");
                                            radioButton4.setText("12 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*36/100-initPay)/12))+" so'mdan");
                                        }
                                        else{
                                            radioButton1.setText(" 3 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*9/100 )/3))+" so'mdan");
                                            radioButton2.setText(" 6 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*18/100)/6))+" so'mdan");
                                            radioButton3.setText(" 9 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*27/100)/9))+" so'mdan");
                                            radioButton4.setText("12 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*36/100)/12))+" so'mdan");
                                        }
                                    }
                                    catch (NumberFormatException e){
                                        radioButton1.setText(" 3 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*9/100 )/3))+" so'mdan");
                                        radioButton2.setText(" 6 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*18/100)/6))+" so'mdan");
                                        radioButton3.setText(" 9 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*27/100)/9))+" so'mdan");
                                        radioButton4.setText("12 Oylik Kredit:       Oyiga: "+ String.valueOf(decim.format((finalTq +finalTq*36/100)/12))+" so'mdan");

                                    }

                                }

                                @Override public void afterTextChanged(Editable editable) {}
                            });
                        }
                    }
                }
            }
        });

        radioGroup1.setOnCheckedChangeListener((radioGroup, checkedId) -> {RadioButton radioButton = (RadioButton) radioGroup .findViewById(checkedId);});
        final CardView confirmCustomerDetail = dialog.findViewById(R.id.confirmCustomerDetail);
        confirmCustomerDetail.setOnClickListener(view -> {
            System.out.println(customerInitialPayment.getText());
            RadioButton radioButton;
            int selectedId = radioGroup1.getCheckedRadioButtonId();
            if (selectedId == -1) { radioButton = radioButton1;}
            else {radioButton = radioGroup1.findViewById(selectedId);}
            if (customerPhoneNumber1.length() < 9 && customerPhoneNumber1.length() > 0) {
                forCustomerPhoneNumber1.setError("Xaridor telefon raqami noto'g'ri kiritildi");
                return;
            }
            else if (customerPhoneNumber2.length() < 9 && customerPhoneNumber2.length() > 0) {
                forCustomerPhoneNumber2.setError("Xaridor telefon raqami noto'g'ri kiritildi");
                return;
            }
            else if (!isValidPassportNo(customerPassport.getText().toString())) {
                forCustomerPassport.setError("Xaridor passportini kiritishda xatolik");}


//            else if(checkRequiredField(customerName, forCustomerName) && checkRequiredField(customerPhoneNumber1, forCustomerPhoneNumber1) &&
//            checkRequiredField(customerHouse, forCustomerHouse) && checkRequiredField(customerStreet, forCustomerStreet) &&
//            checkRequiredField(customerVillage, forCustomerVillage) && checkRequiredField(customerDistrict, forCustomerDistrict) &&
//            checkRequiredField(customerPassportGiven, forCustomerPassportGiven) && checkRequiredField(customerJob, forCustomerJob) &&
//            checkRequiredField(customerStatus, forCustomerStatus) && checkRequiredField(customerSalary, forCustomerSalary) &&
//            checkRequiredField(customerBirthDay, forCustomerBirthDay) && checkRequiredField(customerPassportGivenDate, forCustomerPassportGivenDate) &&
//            checkRequiredField(customerPassport, forCustomerPassport) && checkRequiredField(customerPhoneNumber2, forCustomerPhoneNumber2))
//            {

                if(isLocationPermitted){
                    askRequest();
                }
                creditMainAction(customerName,customerPhoneNumber1, customerPhoneNumber2, customerHouse,
                        customerStreet, customerVillage,customerDistrict, customerPassportGiven,customerJob,
                        customerStatus, customerSalary, customerBirthDay, customerPassportGivenDate,
                        customerPassport, radioButton, customerInitialPayment);
//            }


        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    private void creditMainAction(EditText customerName, EditText customerPhoneNumber1, EditText customerPhoneNumber2, EditText customerHouse,
                                  EditText customerStreet, EditText customerVillage, EditText customerDistrict, EditText customerPassportGiven,
                                  EditText customerJob, EditText customerStatus, EditText customerSalary, EditText customerBirthDay,
                                  EditText customerPassportGivenDate, EditText customerPassport, RadioButton radioButton, EditText customerInitialPayment) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
            .setContentText("Buyurtmani tasdiqlaysizmi!")
            .setCancelText("Yo'q")
            .setConfirmText("Ha")
            .showCancelButton(true)
            .setCancelClickListener(SweetAlertDialog::cancel)
            .setConfirmClickListener(sweetAlertDialog1 -> {
                progressBar.setVisibility(View.VISIBLE);
                String stringCustomerName=customerName.getText().toString().trim();
                String stringCustomerPhoneNumber1="+998"+customerPhoneNumber1.getText().toString().trim();
                String stringCustomerPhoneNumber2="+998"+customerPhoneNumber2.getText().toString().trim();
                String stringCustomerHouse=customerHouse.getText().toString().trim();

                stringCustomerInitialPayment= Long.parseLong(customerInitialPayment.getText().toString().trim().replaceAll("[^A-Za-z0-9]", ""));
                String stringCustomerJob=customerJob.getText().toString().trim();
                String stringCustomerStatus=customerStatus.getText().toString().trim();
                String stringCustomerSalary=customerSalary.getText().toString().trim().replaceAll("[^A-Za-z0-9]", "");
                String stringCustomerBirthDay=customerBirthDay.getText().toString().trim();
                String stringCustomerStreet=customerStreet.getText().toString().trim();
                String stringCustomerVillage=customerVillage.getText().toString().trim();
                String stringCustomerDistrict=customerDistrict.getText().toString().trim();
                String stringCustomerPassportGiven=customerPassportGiven.getText().toString().trim();
                String stringCustomerPassportGivenDate=customerPassportGivenDate.getText().toString().trim();
                String stringCustomerPassport=customerPassport.getText().toString().trim();
                System.out.println(stringCustomerInitialPayment);
                if (!(TextUtils.isEmpty(stringCustomerName)) && !(TextUtils.isEmpty(stringCustomerPhoneNumber1)) && !(TextUtils.isEmpty(stringCustomerJob)) &&
                        !(TextUtils.isEmpty(stringCustomerPhoneNumber2)) && !(TextUtils.isEmpty(stringCustomerHouse)) && !(TextUtils.isEmpty(stringCustomerStatus)) &&
                        !(TextUtils.isEmpty(stringCustomerSalary)) && !(TextUtils.isEmpty(stringCustomerBirthDay)) && !(TextUtils.isEmpty(stringCustomerStreet)) &&
                        !(TextUtils.isEmpty(stringCustomerVillage)) && !(TextUtils.isEmpty(stringCustomerDistrict)) && !(TextUtils.isEmpty(stringCustomerPassportGiven)) &&
                        !(TextUtils.isEmpty(stringCustomerPassportGivenDate)) && !(TextUtils.isEmpty(stringCustomerPassport)) &&
                        !(TextUtils.isEmpty(stringCustomerInitialPayment.toString()))){
                    progressBar.setVisibility(View.GONE);
                    SellerCreditModel sellerCreditModel = new SellerCreditModel(stringCustomerPhoneNumber1, stringCustomerPhoneNumber2, stringCustomerPassport,
                           stringCustomerName, stringCustomerHouse, stringCustomerStreet, stringCustomerVillage, stringCustomerDistrict, stringCustomerPassportGiven,
                            stringCustomerJob, stringCustomerStatus, stringCustomerSalary, stringCustomerBirthDay, stringCustomerPassportGivenDate, sellerId, creditDocRef.getId(),
                            new Date().getTime(), 0, null, Timestamp.now(), null, 0, 0,
                            0, radioButton.getText().toString(),stringCustomerInitialPayment );
                    creditDocRef.set(sellerCreditModel);
                    setCreditProduct();
                    if(isLocationPermitted){
                        askRequest();
                    }
                    loadShoppingCartDataUpdateCreditOrder();
                    freeingShoppingCart();
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                            .setContentText("Buyurtma tasdiqlandi va yetkazish uchun kutilmoqda!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.cancel();
                                Intent intent = new Intent(requireContext(), SellerHomeActivity.class);
                                startActivity(intent);
                            })
                            .show();


                }else{
                    progressBar.setVisibility(View.GONE);
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Jarayonda xatolik!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }

                sweetAlertDialog1.cancel();
            })
            .show();

    }

    private void loadShoppingCartForCredit() {
            shoppingCartCollection.whereEqualTo("sellerId", sellerId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document:task.getResult().getDocuments()){
                        if (document.exists()) {
                            SellerShoppingCartModel dataModal = document.toObject(SellerShoppingCartModel.class);
                            shoppingCartModelListForCredit.add(dataModal);
                            sellerCartCreditAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            });
    }
    private void loadShoppingCartDataUpdateCreditOrder() {

        shoppingCartCollection.whereEqualTo("sellerId", sellerId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int tp;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        tp = 0;
                        int tq = 0;

                        for (DocumentSnapshot d : list) {

                            int prodQ = Integer.parseInt(Objects.requireNonNull(d.get("prodQuantity")).toString());
                            tq += prodQ;
                            tp += Integer.parseInt(Objects.requireNonNull(d.get("totalCreditPrice")).toString());
                            db.collection("products").document(d.get("productId").toString()).get()
                                    .addOnSuccessListener(dd -> {
                                        int realProdQu = Integer.parseInt(dd.get("productQuantity").toString());
                                        if(realProdQu-prodQ>=0){
                                            dd.getReference().update("productQuantity", realProdQu-prodQ);
                                        }
                                    });
                        }

                        creditDocRef.update("cartTotalPrice", tp,
                                "cartTotalQuantity", tq);

                    } else {
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Bazada ma'lumot topilmadi!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                    }

                });
    }

    private void setCreditProduct() {
        shoppingCartCollection.whereEqualTo("sellerId", sellerId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                ArrayList<String> creditProductsList = new ArrayList<>();
                ArrayList<String> productsList = new ArrayList<>();
                final int[] i = {0};
                for (DocumentSnapshot d : list) {
                    db.collection("products").document(d.get("productId").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot dd) {
                            if(dd.exists()){
                                DocumentReference creditProdDocRefd = creditProductsCollection.document();
                                SellerCreditProductsModel sellerCreditProductsModel = new SellerCreditProductsModel(Objects.requireNonNull(d.get("productId")).toString(),
                                        Objects.requireNonNull(d.get("productName")).toString(),
                                        creditProdDocRefd.getId(), creditDocRef.getId(),
                                        Integer.parseInt(Objects.requireNonNull(d.get("prodQuantity")).toString()),
                                        Integer.parseInt(Objects.requireNonNull(d.get("creditPrice")).toString()),
                                        Integer.parseInt(Objects.requireNonNull(dd.get("boughtPrice")).toString())
                                );
                                creditProdDocRefd.set(sellerCreditProductsModel);
                                creditProductsList.add(creditProdDocRefd.getId());
                                productsList.add(Objects.requireNonNull(d.get("productId")).toString());

                                i[0]++;
                                if(i[0] == list.size()){
                                    creditDocRef.update("soldProductsList", creditProductsList, "productsList", productsList);
                                }
                            }
                        }
                    });
                }

            } else {
                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Bazada ma'lumot topilmadi!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
        });
    }


    //    ------------------------------ Selling for credit Ends -----------------------------------------//

//    -----------------------------------------------------------------------------------------------------//
//    -----------------------------------------------------------------------------------------------------//
//    -----------------------------------------------------------------------------------------------------//
//    -----------------------------------------------------------------------------------------------------//

    //    ------------------------------ Selling in cash Starts -----------------------------------------//
    private void getCustomerDataForInCash() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.seller_cart_fragment_cash_dialog);

        final EditText customerName = dialog.findViewById(R.id.customerName);
        final EditText customerHomeLocation = dialog.findViewById(R.id.customerHomeLocation);
        final EditText customerPhoneNumber = dialog.findViewById(R.id.customerPhoneNumber);
        final TextInputLayout forCustomerPhoneNumber = dialog.findViewById(R.id.forCustomerPhoneNumber);
        final TextInputLayout forCustomerName = dialog.findViewById(R.id.forCustomerName);
        final TextInputLayout forCustomerHomeLocation = dialog.findViewById(R.id.forCustomerHomeLocation);
        final CheckBox checkboxOrderLocation = dialog.findViewById(R.id.checkboxOrderLocation);
        checkboxOrderLocation.setOnCheckedChangeListener((compoundButton, b) -> {if(b){ isLocationPermitted = true;}else isLocationPermitted=false;});

        final CardView confirmCustomerDetail = dialog.findViewById(R.id.confirmCustomerDetail);
        confirmCustomerDetail.setOnClickListener(view -> {
            if (customerPhoneNumber.length() < 9 && customerPhoneNumber.length() > 0) {
                forCustomerPhoneNumber.setError("Xaridor telefon raqami noto'g'ri kiritildi");
            }
            else if(checkRequiredField(customerName, forCustomerName)&&checkRequiredField(customerHomeLocation, forCustomerHomeLocation)&&
                    checkRequiredField(customerPhoneNumber, forCustomerPhoneNumber))
            {
                if(isLocationPermitted){
                    askRequest();
                }
                cashMainAction(customerName,customerHomeLocation, customerPhoneNumber);
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
    private void cashMainAction(EditText customerName, EditText customerHomeLocation, EditText customerPhoneNumber) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                .setContentText("Buyurtmani tasdiqlaysizmi!")
                .setCancelText("Yo'q")
                .setConfirmText("Ha")
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    progressBar.setVisibility(View.VISIBLE);
                    String text1=customerHomeLocation.getText().toString().trim();
                    String text2=customerName.getText().toString().trim();
                    String text3="+998"+customerPhoneNumber.getText().toString().trim();

                    if (!(TextUtils.isEmpty(text1)) && !(TextUtils.isEmpty(text2)) && !(TextUtils.isEmpty(text3))){
                        progressBar.setVisibility(View.GONE);
                        SellerOrderModel sellerOrderPlacementModel = new SellerOrderModel(sellerId, orderDocRef.getId(), text2, text1, text3,
                                0,0,0,null, Timestamp.now(), null,null,0,
                                0,0,null);

                        orderDocRef.set(sellerOrderPlacementModel);
                        setSoldProduct();
                        if(isLocationPermitted){
                            askRequest();
                        }
                        loadShoppingCartDataUpdateOrder();
                        freeingShoppingCart();
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                                .setContentText("Buyurtma tasdiqlandi va yetkazish uchun kutilmoqda!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.cancel();
                                    Intent intent = new Intent(requireContext(), SellerHomeActivity.class);
                                    startActivity(intent);
                                })
                                .show();


                    }else{
                        progressBar.setVisibility(View.GONE);
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Jarayonda xatolik!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                    }

                    sweetAlertDialog1.cancel();
                })
                .show();
    }
    private void setSoldProduct() {
        shoppingCartCollection.whereEqualTo("sellerId", sellerId).get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                ArrayList<String> soldProductsList = new ArrayList<>();
                ArrayList<String> productsList = new ArrayList<>();
                final int[] i = {0};
                for (DocumentSnapshot d : list) {

                    DocumentReference soldProdDocRefd = soldProductsCollection.document();
                    db.collection("products").document(d.get("productId").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot dd) {
                            if(dd.exists()){
                                SellerSoldProductsModel sellerSoldProductsModel = new SellerSoldProductsModel(Objects.requireNonNull(d.get("productId")).toString(),
                                        Objects.requireNonNull(d.get("productName")).toString(),
                                        soldProdDocRefd.getId(), orderDocRef.getId(),
                                        Integer.parseInt(Objects.requireNonNull(d.get("prodQuantity")).toString()),
                                        Integer.parseInt(Objects.requireNonNull(d.get("cashPrice")).toString()),
                                        Integer.parseInt(Objects.requireNonNull(dd.get("boughtPrice")).toString())
                                         );
                                soldProdDocRefd.set(sellerSoldProductsModel);
                                soldProductsList.add(soldProdDocRefd.getId());
                                productsList.add(Objects.requireNonNull(d.get("productId")).toString());
                                i[0]++;
                                if(i[0] == list.size()){
                                    orderDocRef.update("soldProductsList", soldProductsList, "productsList", productsList);
                                }
                            }
                        }
                    });
                }
            } else {
                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Bazada ma'lumot topilmadi!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
        });
    }
    //Load Shoppingcart Collection and operate on it Start
    private void loadShoppingCartDataUpdateOrder() {
        shoppingCartCollection.whereEqualTo("sellerId", sellerId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int tp;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        tp = 0;
                        int tq = 0;
                        Map<String, Object> paymentMap = new HashMap<>();
                        paymentMap.put("Naqd pul", 0);
                        paymentMap.put("Karta orqali", 0);
                        paymentMap.put("Click orqali", 0);
                        paymentMap.put("Hisob Raqam orqali", 0);
                        for (DocumentSnapshot d : list) {
                            int prodQ = Integer.parseInt(Objects.requireNonNull(d.get("prodQuantity")).toString());
                            int prodP =  Integer.parseInt(Objects.requireNonNull(d.get("totalCashPrice")).toString());
                            tq += prodQ;
                            tp +=prodP;
                            db.collection("products").document(d.get("productId").toString()).get()
                                    .addOnSuccessListener(dd -> {
                                        int realProdQu = Integer.parseInt(dd.get("productQuantity").toString());
                                        if(realProdQu-prodQ>=0){
                                            dd.getReference().update("productQuantity", realProdQu-prodQ);
                                        }
                                    });

                        }

                        orderDocRef.update("cartTotalPrice", tp,
                                "cartTotalQuantity", tq, "orderPlacedTime", new Date().getTime(), "paymentMap", paymentMap);

                    } else {
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Bazada ma'lumot topilmadi!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                    }

                });
    }
//Load Shoppingcart Collection and operate on it End

//    ------------------------------ Selling in cash Ends -----------------------------------------//



//    ------------------------------ Getting Current location START -----------------------------------------//
    private void askRequest() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
        else {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION },100);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0)&& (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        }
        else {
            Toast.makeText(getActivity(),  "Permission denied",Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        LocationManager locationManager = (LocationManager)getActivity().getSystemService( Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            client.getLastLocation().addOnCompleteListener(
                    task -> {
                        Location location= task.getResult();
                        if (location != null) {
                            orderDocRef.update("sellerGeoPoint", new GeoPoint(location.getLatitude(), location.getLongitude()));
                            creditDocRef.update("sellerGeoPoint", new GeoPoint(location.getLatitude(), location.getLongitude()));
                        }
                        else {

                            LocationRequest locationRequest  = new LocationRequest().setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000).setFastestInterval(1000).setNumUpdates(1);

                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void
                                onLocationResult( LocationResult locationResult)
                                {
                                    Location location1= locationResult.getLastLocation();
                                    orderDocRef.update("sellerGeoPoint", new GeoPoint(location1.getLatitude(), location1.getLongitude()));
                                    creditDocRef.update("sellerGeoPoint", new GeoPoint(location1.getLatitude(), location1.getLongitude()));
                                }
                            };
                            client.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper());
                        }
                    });
        }
        else {
            startActivity( new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
//    ------------------------------ Getting Current location END -----------------------------------------//
}
