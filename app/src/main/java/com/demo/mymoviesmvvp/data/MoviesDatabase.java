package com.demo.mymoviesmvvp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.demo.mymoviesmvvp.pojo.FavouriteMovie;
import com.demo.mymoviesmvvp.pojo.Movie;

@Database(entities = {Movie.class, FavouriteMovie.class},version = 2,exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {
    private static MoviesDatabase database;
    private static final String DB_NAME = "movies.db";
    private static Object LOCK = new Object();

    public static MoviesDatabase getInstance(Context context){
        synchronized (LOCK){
            if(database==null){
                database = Room.databaseBuilder(context,MoviesDatabase.class,DB_NAME)
                        .fallbackToDestructiveMigration().build();
            }
        }
        return database;
    }

    public abstract MovieDao movieDao();
}
