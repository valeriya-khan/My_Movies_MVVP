package com.demo.mymoviesmvvp.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiMovieFactory {
    private static ApiMovieFactory apiMovieFactory;
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private static final String API_KEY = "37b6626ea54efbad5cd916be7a000fcf";

    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final String MIN_VOTE_COUNT = "1000";

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;
    public ApiMovieFactory() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public static ApiMovieFactory getInstance(){
        if(apiMovieFactory ==null){
            apiMovieFactory = new ApiMovieFactory();
        }
        return apiMovieFactory;
    }

    public ApiMovieService getApiMovieService(){
        return retrofit.create(ApiMovieService.class);
    }

}
