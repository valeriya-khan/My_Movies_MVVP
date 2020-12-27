package com.demo.mymoviesmvvp.screens.detailscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.mymoviesmvvp.R;
import com.demo.mymoviesmvvp.adapters.TrailerAdapter;
import com.demo.mymoviesmvvp.pojo.FavouriteMovie;
import com.demo.mymoviesmvvp.pojo.Movie;
import com.demo.mymoviesmvvp.pojo.Trailer;
import com.demo.mymoviesmvvp.screens.favouritescreen.FavouriteActivity;
import com.demo.mymoviesmvvp.screens.mainscreen.MainActivity;
import com.demo.mymoviesmvvp.screens.mainscreen.MainViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private ImageView imageViewFavourite;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewOverview;
    private TextView textViewRelease;
    private RecyclerView recyclerViewTrailers;
    private MainViewModel viewModel;
    private String lang;
    private int movieId;
    private TrailerAdapter trailerAdapter;
    private FavouriteMovie favouriteMovie;
    private Movie movie;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        lang = Locale.getDefault().getLanguage();
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewFavourite = findViewById(R.id.imageViewAddToFavorite);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewOverview = findViewById(R.id.textViewOverview);
        textViewRelease = findViewById(R.id.textViewReleaseDate);
        recyclerViewTrailers = findViewById(R.id.recycleViewTrailers);
        trailerAdapter = new TrailerAdapter();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        Intent intent = getIntent();

        if(intent!=null && intent.hasExtra("movieId") && intent.hasExtra("source")){
            movieId = intent.getIntExtra("movieId",-1);
            source = intent.getStringExtra("source");
        }else{
            finish();
        }

        Log.i("myres",Integer.toString(movieId));
        switch (source){
            case "MainActivity":
                movie = viewModel.getMovieById(movieId);
                break;
            default:
                movie = viewModel.getFavouriteMovieById(movieId);
                break;
        }

        Picasso.get().load(viewModel.getBigPosterPath(movie.getPosterPath())).placeholder(R.drawable.poster_placeholder).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        textViewRelease.setText(movie.getReleaseDate());
        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        LiveData<List<Trailer>> trailersFromLiveData = viewModel.getTrailers();
        trailersFromLiveData.observe(this, new Observer<List<Trailer>>() {
            @Override
            public void onChanged(List<Trailer> trailers) {
                trailerAdapter.setTrailers(trailers);
                Log.i("myres", String.valueOf(trailers.get(0).getName()));
            }
        });
        viewModel.loadTrailers(lang,movieId);
        setFavourite();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickChangeFavourite(View view) {
        if(favouriteMovie==null){
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this,"Added to favourite",Toast.LENGTH_SHORT).show();
        }else{
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this,"Removed from favourite",Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite(){
        favouriteMovie = viewModel.getFavouriteMovieById(movieId);
        if(favouriteMovie==null){
            imageViewFavourite.setImageResource(R.drawable.favourite_add_to);
        }else{
            imageViewFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }
}