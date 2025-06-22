package com.example.doanmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.example.doanmobile.model.ComboModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ComboViewHolder> {

    private Context context;
    private List<ComboModel> comboList;
    private OnComboQuantityChangeListener listener;

    public interface OnComboQuantityChangeListener {
        void onQuantityChanged();
    }

    public void setOnComboQuantityChangeListener(OnComboQuantityChangeListener listener) {
        this.listener = listener;
    }

    public ComboAdapter(Context context, List<ComboModel> comboList) {
        this.context = context;
        this.comboList = comboList;
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_combo, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        ComboModel combo = comboList.get(position);

        holder.tvComboName.setText(combo.getName());
        holder.tvComboDescription.setText(combo.getDescription());
        holder.tvComboPrice.setText(formatCurrency(combo.getPrice()));
        holder.tvQuantity.setText(String.valueOf(combo.getQuantity()));

        // Load image using Glide
        if (combo.getImgCombo() != null && !combo.getImgCombo().isEmpty()) {
            Glide.with(context)
                    .load(combo.getImgCombo())
                    .placeholder(R.drawable.placeholder_image) // Placeholder image
                    .error(R.drawable.error_image) // Error image if loading fails
                    .into(holder.imgCombo);
        } else {
            holder.imgCombo.setImageResource(R.drawable.placeholder_image);
        }

        holder.btnPlus.setOnClickListener(v -> {
            combo.setQuantity(combo.getQuantity() + 1);
            holder.tvQuantity.setText(String.valueOf(combo.getQuantity()));
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (combo.getQuantity() > 0) {
                combo.setQuantity(combo.getQuantity() - 1);
                holder.tvQuantity.setText(String.valueOf(combo.getQuantity()));
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comboList.size();
    }

    public static class ComboViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCombo;
        TextView tvComboName, tvComboDescription, tvComboPrice, tvQuantity;
        Button btnMinus, btnPlus;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCombo = itemView.findViewById(R.id.imgCombo);
            tvComboName = itemView.findViewById(R.id.tvComboName);
            tvComboDescription = itemView.findViewById(R.id.tvComboDescription);
            tvComboPrice = itemView.findViewById(R.id.tvComboPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }

    private String formatCurrency(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount);
    }
} 