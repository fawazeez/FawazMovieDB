package com.adupp.administrator.fawazmoviedb;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListActivityFragment extends Fragment {
    private static final String TAG = MovieListActivityFragment.class.getSimpleName();
    private GridViewAdapter MovieListAdapter ;
    private ArrayList<String> movieListArray;
    public MovieListActivityFragment() {
    }
    private void UpdateMovieList()
    {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.option_sort_key), getString(R.string.option_sort_popularity_value));
        FetchMovieTask  movieTask = new FetchMovieTask();
        movieTask.execute(sortBy);
    }

    @Override
    public void onStart() {
        super.onStart();
        UpdateMovieList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_list, container, false);


        movieListArray = new ArrayList<String>();
        MovieListAdapter = new GridViewAdapter(getActivity(),R.layout.movie_grid_item_layout,movieListArray);
        GridView movieGrid= (GridView)v.findViewById(R.id.movieGridView);
        movieGrid.setAdapter(MovieListAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailAct = new Intent(getActivity(), MovieDetail.class);
                detailAct.putExtra("MOVIELIST", MovieListAdapter.getItem(position));
                startActivity(detailAct);
            }
        });

        return v;
    }

    public class FetchMovieTask extends AsyncTask<String,Void,String[]>
    {
        @Override
        protected String[] doInBackground(String... params) {
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
                    return null;
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
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieFromJson(movieJsonStr);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String[] getMovieFromJson(String result)  throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_ID = "id";
            final String OWM_ORIGINAL_TITLE = "original_title";

            JSONObject response = new JSONObject(result);
            JSONArray results = response.getJSONArray(OWM_RESULTS);
            String[] resultStr = new String[results.length()];
            for (int i = 0; i < results.length(); i++) {
                JSONObject List = results.getJSONObject(i);
                int id = List.getInt(OWM_ID);
                String PosterPath = List.getString(OWM_POSTER);
                String title = List.getString(OWM_ORIGINAL_TITLE);

                resultStr[i]= "http://image.tmdb.org/t/p/w185" + PosterPath;
            }
            return resultStr;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            MovieListAdapter.clear();
            if (strings != null)
            {   movieListArray.addAll(Arrays.asList(strings));
                MovieListAdapter.setGridData(movieListArray);
            }
        }
    }
}
