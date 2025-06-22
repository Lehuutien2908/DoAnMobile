package com.example.doanmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.activity.MovieManagementActivity;
import com.example.doanmobile.model.Movie;

import java.util.List;

public class MovieAdminAdapter extends RecyclerView.Adapter<MovieAdminAdapter.MovieViewHolder> {

    private List<Movie> movieList;

    public MovieAdminAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    public MovieAdminAdapter(MovieManagementActivity movieManagementActivity, List<com.example.doanmobile.model.Movie> movieList) {
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public TextView movieTitle;
        public TextView movieDescription;

        public MovieViewHolder(View view) {
            super(view);
            movieTitle = view.findViewById(com.example.doanmobile.R.id.movie_title);
            movieDescription = view.findViewById(R.id.movieTitle);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manager_movie_admin, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.movieTitle.setText(movie.getTitle());
        holder.movieDescription.setText(movie.getDescription());
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // Class dữ liệu mẫu
    public static class Movie {
        private String title;
        private String description;

        public Movie(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }
}