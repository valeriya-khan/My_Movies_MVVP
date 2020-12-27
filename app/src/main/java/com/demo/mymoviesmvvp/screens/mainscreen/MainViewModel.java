package com.demo.mymoviesmvvp.screens.mainscreen;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.content.AsyncTaskLoader;

import com.demo.mymoviesmvvp.api.ApiMovieFactory;
import com.demo.mymoviesmvvp.api.ApiMovieService;
import com.demo.mymoviesmvvp.data.MoviesDatabase;
import com.demo.mymoviesmvvp.pojo.FavouriteMovie;
import com.demo.mymoviesmvvp.pojo.Movie;
import com.demo.mymoviesmvvp.pojo.MovieResponse;
import com.demo.mymoviesmvvp.pojo.Trailer;
import com.demo.mymoviesmvvp.pojo.TrailerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private static LiveData<List<Movie>> movies;
    private static LiveData<List<FavouriteMovie>> favouriteMovies;
    private static MutableLiveData<List<Trailer>> trailers;
    private static MoviesDatabase database;
    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String BIG_POSTER_SIZE = "w780";

    private ApiMovieFactory apiMovieFactory;
    private ApiMovieService apiMovieService;
    private CompositeDisposable compositeDisposable;
    private static final String API_KEY = "37b6626ea54efbad5cd916be7a000fcf";

    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final String MIN_VOTE_COUNT = "1000";

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MoviesDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        favouriteMovies = database.movieDao().getAllFavouriteMovies();
        trailers = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
        apiMovieFactory = ApiMovieFactory.getInstance();
        apiMovieService = apiMovieFactory.getApiMovieService();
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public MutableLiveData<List<Trailer>> getTrailers() {
        return trailers;
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public String getBigPosterPath(String posterPath){
        return BASE_POSTER_URL+BIG_POSTER_SIZE+posterPath;
    }

    private void insertMovies(List<Movie> movies){
        new InsertMoviesTask().execute(movies);
    }

    private static class InsertMoviesTask extends AsyncTask<List<Movie>,Void,Void>{
        @Override
        protected Void doInBackground(List<Movie>... lists) {
            if(lists!=null && lists.length>0){
                database.movieDao().insertMovies(lists[0]);
            }
            return null;
        }
    }

    private void deleteAllMovies(){
        new DeleteAllMovies().execute();
    }

    public Movie getMovieById(int id){
        try {
            return new GetMovieByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetMovieByIdTask extends AsyncTask<Integer,Void,Movie>{
        @Override
        protected Movie doInBackground(Integer... integers) {
            if(integers!=null && integers.length>0){
                return database.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    public FavouriteMovie getFavouriteMovieById(int id){
        try {
            return new GetFavouriteMovieByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetFavouriteMovieByIdTask extends AsyncTask<Integer,Void,FavouriteMovie>{
        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if(integers!=null && integers.length>0){
                return database.movieDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class DeleteAllMovies extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            database.movieDao().deleteAllMovies();
            return null;
        }
    }

    public void insertFavouriteMovie(FavouriteMovie favouriteMovie){
        new InsertFavouriteMovieTask().execute(favouriteMovie);
    }

    private static class InsertFavouriteMovieTask extends AsyncTask<FavouriteMovie,Void,Void>{
        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovies) {
            if(favouriteMovies!=null && favouriteMovies.length>0){
                database.movieDao().insertFavouriteMovie(favouriteMovies[0]);
            }
            return null;
        }
    }

    public void deleteFavouriteMovie(FavouriteMovie favouriteMovie){
        new DeleteFavouriteMovieTask().execute(favouriteMovie);
    }

    private static class DeleteFavouriteMovieTask extends AsyncTask<FavouriteMovie,Void,Void>{
        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovies) {
            if(favouriteMovies!=null && favouriteMovies.length>0){
                database.movieDao().deleteFavouriteMovie(favouriteMovies[0]);
            }
            return null;
        }
    }



    public void  loadData(String lang, boolean isTopRated, int page){

        String methodOfSort;
        if(isTopRated){
            methodOfSort = SORT_BY_TOP_RATED;
        }else {
            methodOfSort = SORT_BY_POPULARITY;
        }
        Disposable disposable = apiMovieService.getMovies(API_KEY,lang,methodOfSort,MIN_VOTE_COUNT,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                        deleteAllMovies();
                        List<Movie> movies = movieResponse.getResults();
                        insertMovies(movies);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void loadTrailers(String lang, int movieId){

        Disposable disposable = apiMovieService.getTrailers(movieId,API_KEY,lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TrailerResponse>() {
                    @Override
                    public void accept(TrailerResponse trailerResponse) throws Exception {
                        trailers.setValue(trailerResponse.getResults());
                    }
                },new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i("myres","Fail");
            }
        });
        compositeDisposable.add(disposable);
    }



    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
