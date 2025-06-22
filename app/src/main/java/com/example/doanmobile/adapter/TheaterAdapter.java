package com.example.doanmobile.adapter;

import android.content.Intent;
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
    private OnTheaterClickListener listener;

    // Interface cho sự kiện click
    public interface OnTheaterClickListener {
        void onTheaterClick(Theater theater);
    }

    public TheaterAdapter(List<Theater> theaterList, OnTheaterClickListener listener) {
        this.theaterList = theaterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theater, parent, false);
        return new TheaterViewHolder(view, listener);
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

        public TheaterViewHolder(@NonNull View itemView, OnTheaterClickListener listener) {
            super(itemView);
            theaterImage = itemView.findViewById(R.id.theater_image);
            theaterName = itemView.findViewById(R.id.theater_name);
            theaterAddress = itemView.findViewById(R.id.theater_address);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTheaterClick((Theater) v.getTag()); // Get the Theater object from tag
                }
            });
        }

        public void bind(Theater theater) {
            itemView.setTag(theater); // Set the Theater object as tag
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