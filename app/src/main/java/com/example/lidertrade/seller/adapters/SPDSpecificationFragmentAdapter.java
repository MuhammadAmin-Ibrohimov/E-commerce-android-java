package com.example.lidertrade.seller.adapters;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.cardview.widget.CardView;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;
        import com.bumptech.glide.load.engine.DiskCacheStrategy;
        import com.example.lidertrade.R;
        import com.example.lidertrade.admin.models.AdminProductSpecificationsModel;
        import com.example.lidertrade.seller.models.SHFSaleModel;

        import java.util.ArrayList;

public class SPDSpecificationFragmentAdapter extends RecyclerView.Adapter<SPDSpecificationFragmentAdapter.ViewHolder> {

    ArrayList<AdminProductSpecificationsModel> homeSaleModelArrayList;
    Context context;

    public SPDSpecificationFragmentAdapter(ArrayList<AdminProductSpecificationsModel> homeSaleModelArrayList, Context context) {
        this.homeSaleModelArrayList = homeSaleModelArrayList;
        this.context = context;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.s_p_d_specification_fragment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminProductSpecificationsModel modal = homeSaleModelArrayList.get(position);
        holder.specName.setText(modal.getName()+":");
        holder.specValue.setText(modal.getField());

    }

    @Override
    public int getItemCount() {
        return homeSaleModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView specName, specValue;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            specValue = itemView.findViewById(R.id.specValue);
            specName = itemView.findViewById(R.id.specName);

        }
    }
}
