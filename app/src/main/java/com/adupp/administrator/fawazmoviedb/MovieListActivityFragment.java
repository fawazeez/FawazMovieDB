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
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListActivityFragment extends Fragment {
    private static final String TAG = MovieListActivityFragment.class.getSimpleName();
    private static final String TITLE_INTENT_KEY = "Original Title";
    private static final String OVERVIEW_INTENT_KEY = "Synopsis ";
    private static final String POSTERPATH_INTENT_KEY = "Poter";
    private static final String USERRATING_INTENT_KEY = "User Rating";
    private static final String RELEASEDATE_INTENT_KEY = "Release Date";

    private GridViewAdapter MovieListAdapter ;
    private ArrayList<GridItem> movieListArray;
    public MovieListActivityFragment() {
    }
    private void UpdateMovieList()
    {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.option_sort_key), getString(R.string.option_sort_popularity_value));
        FetchMovieTask  movieTask = new FetchMovieTask();
        MovieListAdapter.clear();
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

        movieListArray = new ArrayList<GridItem>();
        MovieListAdapter = new GridViewAdapter(getActivity(),R.layout.movie_grid_item_layout,movieListArray);
        GridView movieGrid= (GridView)v.findViewById(R.id.movieGridView);
        movieGrid.setAdapter(MovieListAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);
                Intent detailAct = new Intent(getActivity(), MovieDetail.class);
                detailAct.putExtra(TITLE_INTENT_KEY, item.getOriginal_title());
                detailAct.putExtra(OVERVIEW_INTENT_KEY, item.getOverview());
                detailAct.putExtra(POSTERPATH_INTENT_KEY, item.getPoster_path());
                detailAct.putExtra(USERRATING_INTENT_KEY, item.getVote_average());
                detailAct.putExtra(RELEASEDATE_INTENT_KEY,item.getRelease_date());
                startActivity(detailAct);
            }
        });

        return v;
    }

    public class FetchMovieTask extends AsyncTask<String,Void,Integer>
    {
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
            GridItem movieItem;
            String re = null;

            JSONObject response = new JSONObject(result);
            JSONArray results = response.getJSONArray(OWM_RESULTS);
            String[] resultStr = new String[results.length()];
            for (int i = 0; i < results.length(); i++) {
                movieItem = new GridItem();
                JSONObject List = results.getJSONObject(i);
                movieItem.setId(List.getString(OWM_ID));
                movieItem.setPoster_path("http://image.tmdb.org/t/p/w185//" + List.optString(OWM_POSTER));
                movieItem.setOriginal_title(List.optString(OWM_ORIGINAL_TITLE));
                movieItem.setOverview(List.optString(OWM_OVERVIEW));
                movieItem.setRelease_date(List.optString(OWM_RELEASE_DATE));
                movieItem.setVote_average(List.getString(OWM_VOTE_AVERAGE));
//                if (List.optString(OWM_POSTER) != null && List.optString(OWM_POSTER)!= "")
                movieListArray.add(movieItem);

            }
            return movieListArray.size();
        }

        @Override
        protected void onPostExecute(Integer result) {
          //  MovieListAdapter.clear();
            if (result != 0) {
                MovieListAdapter.setGridData(movieListArray);
            }else
            {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
