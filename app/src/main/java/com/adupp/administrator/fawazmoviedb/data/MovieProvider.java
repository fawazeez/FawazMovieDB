package com.adupp.administrator.fawazmoviedb.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.adupp.administrator.fawazmoviedb.data.MovieContract;

/**
 * Created by fawaz on 2/20/2016.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildMatcher();
    static final int FAVMOVIE = 100;
    static final int MOVIE = 200;

    private static final SQLiteQueryBuilder sMovieQuery ;

    static {sMovieQuery=new SQLiteQueryBuilder();
    sMovieQuery.setTables(MovieContract.MovieEntry.TABLE_NAME);}

    private static final String sFavoriteSelection = MovieContract.MovieEntry.TABLE_NAME + "."+ MovieContract.MovieEntry.COLUMN_FAVORITE + "= 'Y'";
    private static final String sMovieSelection = MovieContract.MovieEntry.TABLE_NAME + "."+ MovieContract.MovieEntry.COLUMN_MOVIE_ID + "= ?";
     static UriMatcher buildMatcher() {
         final UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
         matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.BASE_PATH,FAVMOVIE);
         matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.BASE_PATH+"/#",MOVIE);
         return matcher;
    }

    private MovieDbHelper movieDbHelper;
    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri))
        {
            case FAVMOVIE:
                retCursor = getFavoriteMovie(uri, projection, sortOrder);
                break;
            case MOVIE:
                retCursor = getMovie(uri,projection,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri : "+uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    private Cursor getMovie(Uri uri, String[] projection, String sortOrder) {
        String[] selectionArgs = new String[]{ uri.getLastPathSegment()};
        return sMovieQuery.query(movieDbHelper.getReadableDatabase(),projection,sMovieSelection,selectionArgs,null,null,sortOrder);
    }

    private Cursor getFavoriteMovie(Uri uri, String[] projection, String sortOrder) {
//        String[] selectionArgs = new String[]{"Y"};
        String selection=sFavoriteSelection;
        return sMovieQuery.query(movieDbHelper.getReadableDatabase(),projection,selection,null,null,null,sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVMOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown Uri :"+uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match)
        {
            case FAVMOVIE:
            {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if ( _id  > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
int rowsDeleted;
        switch (match)
        {
            case MOVIE:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection))
                    rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,sMovieSelection,new String[]{id});
                else {
                    rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                }
                break;
            case FAVMOVIE:
                if(TextUtils.isEmpty(selection))
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,sFavoriteSelection,selectionArgs);
                else
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,selection,selectionArgs);
break;
            default:
                throw new UnsupportedOperationException("UnKnown Uri" + uri);

        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (match) {
            case MOVIE:
                String id = uri.getLastPathSegment();
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, sMovieSelection, new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);

        }
        return rowsUpdated;
    }
}
