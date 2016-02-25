package com.adupp.administrator.fawazmoviedb.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.security.PublicKey;

/**
 * Created by fawaz on 2/20/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.adupp.administrator.fawazmoviedb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String BASE_PATH = "movie";


    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(BASE_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE  + "/movies";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE  + "/movie";

        public static final String TABLE_NAME= "movies";
        public static final String COLUMN_MOVIE_NAME= "original_title";
        public static final String COLUMN_MOVIE_ID= "movie_id";
        public static final String COLUMN_POSTER= "poster";
        public static final String COLUMN_SYNOPSIS= "overview";
        public static final String COLUMN_RATING= "vote_average";
        public static final String COLUMN_RELEASE_DATE= "release_date";
        public static final String COLUMN_FAVORITE = "favorite";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
}
