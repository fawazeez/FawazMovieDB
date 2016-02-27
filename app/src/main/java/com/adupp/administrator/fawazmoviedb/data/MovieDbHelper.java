package com.adupp.administrator.fawazmoviedb.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.adupp.administrator.fawazmoviedb.data.MovieContract;

/**
 * Created by fawaz on 2/20/2016.
 */
public class MovieDbHelper  extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION=4;
    static final String DATABASE_NAME = "movie.db";
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_LIST = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "("+
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY,"+
                MovieContract.MovieEntry.COLUMN_MOVIE_NAME + "  TEXT UNIQUE NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_RATING + " TEXT , " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_FAVORITE + " TEXT  "+
                ");";
        db.execSQL(SQL_CREATE_MOVIE_LIST);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
