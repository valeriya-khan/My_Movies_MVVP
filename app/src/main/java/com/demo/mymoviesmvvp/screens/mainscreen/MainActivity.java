package com.demo.mymoviesmvvp.screens.mainscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.demo.mymoviesmvvp.screens.detailscreen.DetailActivity;
import com.demo.mymoviesmvvp.R;
import com.demo.mymoviesmvvp.adapters.MovieAdapter;
import com.demo.mymoviesmvvp.pojo.Movie;
import com.demo.mymoviesmvvp.screens.favouritescreen.FavouriteActivity;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Switch switchSort;
    private TextView textViewTopRated;
    private TextView textViewPopular;
    private MovieAdapter adapter;
    private MainViewModel viewModel;
    private int page = 1;
    private static String lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lang = Locale.getDefault().getLanguage();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        switchSort = findViewById(R.id.switchSort);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewPopular = findViewById(R.id.textViewPopularity);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    textViewTopRated.setTextColor(getResources().getColor(R.color.design_default_color_secondary));
                    textViewPopular.setTextColor(getResources().getColor(R.color.white));
                }else{
                    textViewPopular.setTextColor(getResources().getColor(R.color.design_default_color_secondary));
                    textViewTopRated.setTextColor(getResources().getColor(R.color.white));
                }
                viewModel.loadData(lang.toString(),isChecked,page);
            }
        });
        switchSort.setChecked(false);
        recyclerView = findViewById(R.id.recyclerViewPosters);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                adapter.setMovies(movies);
            }
        });
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Movie movie = adapter.getMovies().get(position);
                intent.putExtra("movieId",movie.getId());
                intent.putExtra("source","MainActivity");
                startActivity(intent);
            }
        });
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
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSetPopularity(View view) {
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        switchSort.setChecked(true);
    }


}