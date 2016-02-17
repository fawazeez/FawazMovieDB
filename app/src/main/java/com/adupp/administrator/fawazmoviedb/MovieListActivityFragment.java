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

    public static GridViewAdapter MovieListAdapter ;
    public static ArrayList<Griditem> movieListArray;
    public MovieListActivityFragment() {
    }
    private void UpdateMovieList()
    {
        String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.option_sort_key), getString(R.string.option_sort_popularity_value));
        FetchMovieTask  movieTask = new FetchMovieTask(getActivity());
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

        movieListArray = new ArrayList<Griditem>();
        MovieListAdapter = new GridViewAdapter(getActivity(),R.layout.movie_grid_item_layout,movieListArray);
        GridView movieGrid= (GridView)v.findViewById(R.id.movieGridView);
        movieGrid.setAdapter(MovieListAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Griditem item = (Griditem) parent.getItemAtPosition(position);
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


}
