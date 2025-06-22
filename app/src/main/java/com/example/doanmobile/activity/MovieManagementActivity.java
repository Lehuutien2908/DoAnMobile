package com.example.doanmobile.activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.adapter.MovieAdminAdapter;
import com.example.doanmobile.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class MovieManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_management);

        recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerMovies.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu phim mẫu
        List<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie("1", "Avengers: Endgame", "https://upload.wikimedia.org/wikipedia/vi/2/2d/Avengers_Endgame_bia_teaser.jpg", "active"));
        movieList.add(new Movie("2", "Spider-Man: No Way Home", "https://upload.wikimedia.org/wikipedia/vi/7/71/%C3%81p_ph%C3%ADch_phim_Ng%C6%B0%E1%BB%9Di_Nh%E1%BB%87n_kh%C3%B4ng_c%C3%B2n_nh%C3%A0.jpg", "active"));
        movieList.add(new Movie("3", "The Batman", "https://beam-images.warnermediacdn.com/BEAM_LWM_DELIVERABLES/dfa50804-e6f6-4fa2-a732-693dbc50527b/37082735-6715-11ef-96ad-02805d6a02df?host=wbd-images.prod-vod.h264.io&partner=beamcom", "active"));
        movieList.add(new Movie("4", "Oppenheimer", "https://upload.wikimedia.org/wikipedia/vi/thumb/2/21/Oppenheimer_%E2%80%93_Vietnam_poster.jpg/250px-Oppenheimer_%E2%80%93_Vietnam_poster.jpg", "active"));
        movieList.add(new Movie("5", "Inside Out 2", "https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/3/image/c5f0a1eff4c394a251036189ccddaacd/1/0/1080x1350-insideout.jpg", "active"));

        // Gán adapter
        MovieAdminAdapter adapter = new MovieAdminAdapter(this, movieList);
        recyclerMovies.setAdapter(adapter);
    }
}
