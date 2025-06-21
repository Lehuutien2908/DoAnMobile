package com.example.doanmobile.adapter;

import android.app.AlertDialog;
import android.content.Context;
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
import com.example.doanmobile.model.Movie;

import java.util.List;

public class AdminMovieAdapter extends RecyclerView.Adapter<AdminMovieAdapter.MovieAdminVH> {

    private final Context context;
    private final List<Movie> movieList;

    public AdminMovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item_admin, parent, false);
        return new MovieAdminVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdminVH holder, int position) {
        Movie movie = movieList.get(position);

        holder.tvTitle.setText(movie.getName());

        // ✅ Kiểm tra null trước khi load ảnh
        if (movie.getImgMovie() != null && !movie.getImgMovie().isEmpty()) {
            Glide.with(context)
                    .load(movie.getImgMovie())
                    .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageResource(R.drawable.ic_launcher_background); // ảnh mặc định
        }

        holder.btnEdit.setOnClickListener(v -> {
            Toast.makeText(context, "Sửa: " + movie.getName(), Toast.LENGTH_SHORT).show();
            // TODO: mở activity sửa phim hoặc hiển thị dialog
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xoá phim")
                    .setMessage("Bạn có chắc muốn xoá '" + movie.getName() + "'?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        movieList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Đã xoá " + movie.getName(), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieAdminVH extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle;
        ImageButton btnEdit, btnDelete;

        public MovieAdminVH(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.MovieName_admin);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
