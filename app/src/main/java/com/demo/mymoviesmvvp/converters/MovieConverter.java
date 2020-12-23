package com.demo.mymoviesmvvp.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MovieConverter {

    @TypeConverter
    public String genreIdsToString(List<Integer> genreIds){
        return new Gson().toJson(genreIds);
    }

    @TypeConverter
    public List<Integer> stringToListGenreIds(String genreIdsAsString){
        Gson gson = new Gson();
        ArrayList objects = gson.fromJson(genreIdsAsString,ArrayList.class);
        ArrayList<Integer> genreIds = new ArrayList<>();
        for(Object o: objects){
            genreIds.add(gson.fromJson(o.toString(),Integer.class));
        }
        return genreIds;
    }


}
