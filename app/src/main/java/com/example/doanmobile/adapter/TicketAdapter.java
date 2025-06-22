package com.example.doanmobile.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.model.ComboModel;
import com.example.doanmobile.model.TicketModel;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<TicketModel> ticketList;
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(TicketModel ticket);
    }

    public TicketAdapter(List<TicketModel> ticketList, OnTicketClickListener listener) {
        this.ticketList = ticketList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketModel ticket = ticketList.get(position);
        holder.txtMovie.setText(ticket.getMovieName());

        // Convert timestamp
        if (ticket.getCreatedAt() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dateStr = sdf.format(ticket.getCreatedAt().toDate());
            holder.txtCreatedAt.setText("Ngày đặt: " + dateStr);
        } else {
            holder.txtCreatedAt.setText("Ngày đặt: không rõ");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTicketClick(ticket);
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView txtMovie, txtCreatedAt;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMovie = itemView.findViewById(R.id.txtMovieTitle);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }
    }
}

