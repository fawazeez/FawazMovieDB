package com.adupp.administrator.fawazmoviedb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.adupp.administrator.fawazmoviedb.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListActivityFragment extends Fragment
{
    private static final String TAG = MovieListActivityFragment.class.getSimpleName();

    private static final String TITLE_INTENT_KEY = "Original Title";
    private static final String ID_INTENT_KEY = "ID";
    private static final String OVERVIEW_INTENT_KEY = "Synopsis ";
    private static final String POSTERPATH_INTENT_KEY = "Poster";
    private static final String USERRATING_INTENT_KEY = "User Rating";
    private static final String RELEASEDATE_INTENT_KEY = "Release Date";
    public static GridViewAdapter MovieListAdapter ;
    public static ArrayList<Griditem> movieListArray;
    private GridView movieGrid;
    public static int mPosition = GridView.INVALID_POSITION;
    public static String SELECTED_KEY="POSITIONSELECTED";
    private Callback callback;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_NAME = 1;
    static final int COL_MOVIE_ID = 2;
    static final int COL_RATING = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_SYNOPSIS = 5;
    static final int COL_POSTER = 6;
    static final int COL_FAVORITE = 7;
    public MovieListActivityFragment() {
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Griditem item,boolean initial);
    }
    public void UpdateMovieList()
    {
        String sortBy = Utility.getPreferredSort(getActivity());
        if(sortBy.equals("fav")) {
            MovieListAdapter.clear();
            Griditem movieItem;
            Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,MOVIE_COLUMNS,null,null,null);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        String posterStr= cursor.getString(COL_POSTER);
                        String movieId = cursor.getString(COL_MOVIE_ID);
                        String movieName = cursor.getString(COL_MOVIE_NAME);
                        String fav = cursor.getString(COL_FAVORITE);
                        String synopsis = cursor.getString(COL_SYNOPSIS);
                        String releaseDate = cursor.getString(COL_RELEASE_DATE);
                        String rating = cursor.getString(COL_RATING);
                        if (fav.equals("Y")) {
                        movieItem = new Griditem();
                        movieItem.setId(movieId);
                        movieItem.setPoster_path("http://image.tmdb.org/t/p/w185//" + posterStr);
                        movieItem.setOriginal_title(movieName);
                        movieItem.setOverview(synopsis);
                        movieItem.setRelease_date(releaseDate);
                        movieItem.setVote_average(rating);
                        movieItem.setFavorite("Y");
//                      if (List.optString(OWM_POSTER) != null && List.optString(OWM_POSTER)!= "")
                        movieListArray.add(movieItem);
                        cursor.moveToNext();
                    }
                }
                MovieListAdapter.setGridData(movieListArray);

            }
//            mPosition = GridView.INVALID_POSITION;
            OnInitialSelected();
        }
        else
        { FetchMovieTask movieTask = new FetchMovieTask(this);
            MovieListAdapter.clear();
            movieTask.execute(sortBy);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
//        UpdateMovieList();

    }

    @Override
    public void onResume() {
        super.onResume();
        movieGrid.smoothScrollToPosition(mPosition,1);
    }

    public void initializePosition() {
        mPosition = GridView.INVALID_POSITION;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_list, container, false);

        movieListArray = new ArrayList<Griditem>();
        MovieListAdapter = new GridViewAdapter(getActivity(),R.layout.movie_grid_item_layout,movieListArray);
        movieGrid= (GridView)v.findViewById(R.id.movieGridView);
        movieGrid.setAdapter(MovieListAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Griditem item = (Griditem) parent.getItemAtPosition(position);
                selectMovie(item,false);
//                Intent detailAct = new Intent(getActivity(), MovieDetail.class);
//                detailAct.putExtra(ID_INTENT_KEY, Integer.parseInt(item.getId()));
//                detailAct.putExtra(TITLE_INTENT_KEY, item.getOriginal_title());
//                detailAct.putExtra(OVERVIEW_INTENT_KEY, item.getOverview());
//                detailAct.putExtra(POSTERPATH_INTENT_KEY, item.getPoster_path());
//                detailAct.putExtra(USERRATING_INTENT_KEY, item.getVote_average());
//                detailAct.putExtra(RELEASEDATE_INTENT_KEY, item.getRelease_date());
//                startActivity(detailAct);
                mPosition = position;
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Callback)
            callback = (Callback) context;
        else
            throw new ClassCastException(context.toString()+"must implement CallBack");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback=null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition!=GridView.INVALID_POSITION)
            outState.putInt(SELECTED_KEY,mPosition);

        super.onSaveInstanceState(outState);
    }


    public void OnInitialSelected (){
        if(mPosition==GridView.INVALID_POSITION){
            if (movieListArray.size()>0) {
                Griditem item = (Griditem) movieListArray.get(0);
                selectMovie(item,true);
                mPosition = 0;
            }else
            selectMovie(null,true);
        }
    }
    private void selectMovie(Griditem item,boolean initial) {
        callback.onItemSelected(item,initial);
    }
}
