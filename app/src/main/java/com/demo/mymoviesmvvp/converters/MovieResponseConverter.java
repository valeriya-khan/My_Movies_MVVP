package com.demo.mymoviesmvvp.converters;

import androidx.room.TypeConverter;

import com.demo.mymoviesmvvp.pojo.Movie;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MovieResponseConverter {

    @TypeConverter
    public String listMoviesToString(List<Movie> movies){
        return new Gson().toJson(movies);
    }

    @TypeConverter
    public List<Movie> stringToListMovies(String moviesAsString){
        Gson gson = new Gson();
        ArrayList objects = gson.fromJson(moviesAsString,ArrayList.class);
        ArrayList<Movie> movies = new ArrayList<>();
        for(Object o: objects){
            movies.add(gson.fromJson(o.toString(),Movie.class));
        }
        return movies;
    }

}
