package com.demo.mymoviesmvvp.api;

import com.demo.mymoviesmvvp.pojo.MovieResponse;
import com.demo.mymoviesmvvp.pojo.TrailerResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiMovieService {
    static final String BASE_VIDEO_URL = "https://api.themoviedb.org/3/movie/%s/videos";
    static final String BASE_REVIEW_URL = "https://api.themoviedb.org/3/movie/%s/reviews";
    static final String PARAMS_API_KEY = "api_key";
    static final String PARAMS_LANG = "language";
    static final String PARAMS_SORT_BY = "sort_by";
    static final String PARAMS_PAGE = "page";
    static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";


    @GET("discover/movie")
    Observable<MovieResponse> getMovies(@Query(PARAMS_API_KEY) String apiKey,
                                        @Query(PARAMS_LANG) String lang,
                                        @Query(PARAMS_SORT_BY) String sortBy,
                                        @Query(PARAMS_MIN_VOTE_COUNT) String minCoint,
                                        @Query(PARAMS_PAGE) int page);

    @GET("movie/{id}/videos")
    Observable<TrailerResponse> getTrailers(@Path("id") int id,
                                            @Query(PARAMS_API_KEY) String apiKey,
                                            @Query(PARAMS_LANG) String lang);



}
