package com.example.doanmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.example.doanmobile.model.Theater;
import java.util.List;

public class TheaterAdapter extends RecyclerView.Adapter<TheaterAdapter.TheaterViewHolder> {

    private List<Theater> theaterList;

    public TheaterAdapter(List<Theater> theaterList) {
        this.theaterList = theaterList;
    }

    @NonNull
    @Override
    public TheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theater, parent, false);
        return new TheaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheaterViewHolder holder, int position) {
        Theater theater = theaterList.get(position);
        holder.bind(theater);
    }

    @Override
    public int getItemCount() {
        return theaterList.size();
    }

    static class TheaterViewHolder extends RecyclerView.ViewHolder {
        ImageView theaterImage;
        TextView theaterName;
        TextView theaterAddress;

        public TheaterViewHolder(@NonNull View itemView) {
            super(itemView);
            theaterImage = itemView.findViewById(R.id.theater_image);
            theaterName = itemView.findViewById(R.id.theater_name);
            theaterAddress = itemView.findViewById(R.id.theater_address);
        }

        public void bind(Theater theater) {
            theaterName.setText(theater.getName());
            theaterAddress.setText(theater.getAddress());
            
            if (theater.getImage() != null && !theater.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                     .load(theater.getImage())
                     .placeholder(R.drawable.placeholder_image) 
                     .error(R.drawable.error_image) 
                     .into(theaterImage);
            }
        }
    }
} 