package com.adupp.administrator.fawazmoviedb;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.adupp.administrator.fawazmoviedb.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by fawaz on 2/17/2016.
 */
public class FetchMovieTask extends AsyncTask<String,Void,Integer>
{

    private static final String TAG = MovieListActivityFragment.class.getSimpleName();
    private  MovieListActivityFragment fragment;




    public FetchMovieTask(MovieListActivityFragment a) {
        this.fragment = a;
    }

    @Override
    protected Integer doInBackground(String... params) {

        Integer result = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

// Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String sort = params[0];

        try {
            final String TMDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String APP_KEY = "api_key";

            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon().appendQueryParameter(SORT_PARAM, sort)
                    .appendQueryParameter(APP_KEY, BuildConfig.TMDB_API_KEY).build();

            URL url= new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return result;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return result;
            }
            movieJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return result;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                    return result;
                }
            }
        }

        try {
            result =getMovieFromJson(movieJsonStr);

        }
        catch (JSONException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    private Integer getMovieFromJson(String result)  throws JSONException {

        final String OWM_RESULTS = "results";
        final String OWM_POSTER = "poster_path";
        final String OWM_ID = "id";
        final String OWM_ORIGINAL_TITLE = "original_title";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_OVERVIEW = "overview";
        Griditem movieItem;



        JSONObject response = new JSONObject(result);
        JSONArray results = response.getJSONArray(OWM_RESULTS);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(results.length());
        String mID ;
        String mTitle;
        String mOverView;
        String mReleaseDate;
        String mRating;
        String mPosterPath;
        for (int i = 0; i < results.length(); i++) {
            movieItem = new Griditem();

            ContentValues movieDetails = new ContentValues();
            JSONObject List = results.getJSONObject(i);
             mID =List.getString(OWM_ID);
             mTitle=List.optString(OWM_ORIGINAL_TITLE);
             mOverView=List.optString(OWM_OVERVIEW);
             mReleaseDate =List.optString(OWM_RELEASE_DATE);
             mRating= List.getString(OWM_VOTE_AVERAGE);
             mPosterPath= "http://image.tmdb.org/t/p/w185//" + List.optString(OWM_POSTER);
//            movieDetails.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,mID);
//            movieDetails.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, List.optString(OWM_ORIGINAL_TITLE));
//            movieDetails.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, mOverView);
//            movieDetails.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
//            movieDetails.put(MovieContract.MovieEntry.COLUMN_RATING, mRating);
//            movieDetails.put(MovieContract.MovieEntry.COLUMN_POSTER, mPosterPath);
//            movieDetails.put(MovieContract.MovieEntry.COLUMN_FAVORITE,"N");
//            cVVector.add(movieDetails);

            movieItem.setId(mID);
            movieItem.setPoster_path(mPosterPath);
            movieItem.setOriginal_title(mTitle);
            movieItem.setOverview(mOverView);
            movieItem.setRelease_date(mReleaseDate);
            movieItem.setVote_average(mRating);
            movieItem.setFavorite("N");
//         if (List.optString(OWM_POSTER) != null && List.optString(OWM_POSTER)!= "")
            MovieListActivityFragment.movieListArray.add(movieItem);

        }


        Griditem item = (Griditem) MovieListActivityFragment.movieListArray.get(0);

        return MovieListActivityFragment.movieListArray.size();
    }

    @Override
    protected void onPostExecute(Integer result) {
        //  MovieListAdapter.clear();
        if (result != 0) {
            MovieListActivityFragment.MovieListAdapter.setGridData(MovieListActivityFragment.movieListArray);
            this.fragment.OnInitialSelected();
        }
    }
}