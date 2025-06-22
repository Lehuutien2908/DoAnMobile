package com.example.doanmobile.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.example.doanmobile.activity.EditMovieActivity;
import com.example.doanmobile.model.Movie;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;

public class MovieAdminAdapter extends RecyclerView.Adapter<MovieAdminAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;

    public MovieAdminAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_admin, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvTitle.setText(movie.getName());

        Glide.with(context)
                .load(movie.getImgMovie())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgMovie);

        // Nút sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditMovieActivity.class);
            intent.putExtra("movie", movie);
            intent.putExtra("movieId", movie.getId());
            context.startActivity(intent);
        });

        // Nút xoá có xác nhận
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc muốn xoá phim \"" + movie.getName() + "\" không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        FirebaseFirestore.getInstance().collection("movies")
                                .document(movie.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Đã xoá phim", Toast.LENGTH_SHORT).show();
                                    movieList.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Lỗi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgMovie;
        ImageButton btnDelete, btnEdit;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.movie_name);  // hoặc tvTitle tùy XML
            imgMovie = itemView.findViewById(R.id.imgMovie);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
