package com.example.lidertrade.seller.adapters;

        import android.content.Context;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;


        import com.bumptech.glide.Glide;
        import com.example.lidertrade.R;
        import com.example.lidertrade.admin.models.AdminProductModel;
        import com.example.lidertrade.deliverer.models.DelivererPendingActivityModel;
        import com.example.lidertrade.seller.models.SellerShoppingCartModel;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QuerySnapshot;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Objects;

public class SellerCartCreditAdapter extends RecyclerView.Adapter<SellerCartCreditAdapter.ViewHolder> {
    private ArrayList<SellerShoppingCartModel> dataModalArrayList;
    private Context context;
    FirebaseFirestore db;

    // constructor class for our Adapter
    public SellerCartCreditAdapter(ArrayList<SellerShoppingCartModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }
    public SellerCartCreditAdapter() {
    }


    @NonNull
    @Override
    public SellerCartCreditAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.seller_cartt_adapter_item, parent, false);
        return new SellerCartCreditAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerCartCreditAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        SellerShoppingCartModel modal = dataModalArrayList.get(position);
        db = FirebaseFirestore.getInstance();
        holder.orderSendingDetailProductName.setText(modal.getProductName());
        holder.orderSendingDetailProductQuantity.setText(String.valueOf(modal.getProdQuantity())+" dona");
        System.out.println(modal.getProductId());
        db.collection("products").document(modal.getProductId()).get().addOnSuccessListener(d -> {
            if (d.getData() != null){
                ArrayList<String> driverPermissions = (ArrayList<String>)d.get("imageUrl");
                String imageUrl = driverPermissions.get(0);
                Glide.with(context)
                        .load(Uri.parse(imageUrl))
                        .into(holder.orderSendingDetailProductImage);
            }else{
                Toast.makeText(context, "XATOOOOOO", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView orderSendingDetailProductQuantity, orderSendingDetailProductName;
        private ImageView orderSendingDetailProductImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            orderSendingDetailProductQuantity = itemView.findViewById(R.id.orderSendingDetailProductQuantity);
            orderSendingDetailProductName = itemView.findViewById(R.id.orderSendingDetailProductName);
            orderSendingDetailProductImage = itemView.findViewById(R.id.orderSendingDetailProductImage);

        }
    }
}
