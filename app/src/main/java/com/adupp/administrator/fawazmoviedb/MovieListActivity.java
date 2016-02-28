package com.adupp.administrator.fawazmoviedb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MovieListActivity extends AppCompatActivity implements MovieListActivityFragment.Callback,MovieDetailFragment.favCallBack{
    private final String LOG_TAG = MovieListActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String TITLE_INTENT_KEY = "Original Title";
    private static final String ID_INTENT_KEY = "ID";
    private static final String OVERVIEW_INTENT_KEY = "Synopsis ";
    private static final String POSTERPATH_INTENT_KEY = "Poster";
    private static final String USERRATING_INTENT_KEY = "User Rating";
    private static final String RELEASEDATE_INTENT_KEY = "Release Date";
    private  String mSortby = null;
    Bundle bundle = new Bundle();
    public boolean mTwoPane;
    public boolean mRefresh=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortby= Utility.getPreferredSort(this);
        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(findViewById(R.id.movie_detail_container)!=null)
        {mTwoPane=true;
            if(savedInstanceState==null)
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,new MovieDetailFragment(),DETAILFRAGMENT_TAG).commitAllowingStateLoss();
        }else {
            mTwoPane = false;
//            getSupportActionBar().setElevation(0f);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initialize(){
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
//        movieDetailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, movieDetailFragment, DETAILFRAGMENT_TAG).commit();
    }


    @Override
    protected void onStart() {
        super.onStart();
        MovieListActivityFragment movieListActivityFragment = ((MovieListActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_list));
        if(mTwoPane) {
            String sortBy = Utility.getPreferredSort(this);
            if (sortBy != null && !sortBy.equals(mSortby)) {
                mRefresh=false;
                 movieListActivityFragment.initializePosition();
            }
            mSortby = sortBy;
        }

        movieListActivityFragment.UpdateMovieList();
        }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTwoPane && !bundle.isEmpty() && mRefresh)
        { MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, movieDetailFragment, DETAILFRAGMENT_TAG).commitAllowingStateLoss();
        }
    }

    @Override
    public void onItemSelected(Griditem item,boolean initial) {

        if (mTwoPane ) {
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            if(item!= null) {
                bundle.putString(ID_INTENT_KEY, item.getId());
                bundle.putString(TITLE_INTENT_KEY, item.getOriginal_title());
                bundle.putString(OVERVIEW_INTENT_KEY, item.getOverview());
                bundle.putString(POSTERPATH_INTENT_KEY, item.getPoster_path());
                bundle.putString(USERRATING_INTENT_KEY, item.getVote_average());
                bundle.putString(RELEASEDATE_INTENT_KEY, item.getRelease_date());
                movieDetailFragment.setArguments(bundle);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,movieDetailFragment,DETAILFRAGMENT_TAG).commitAllowingStateLoss();
        }
        else
        {
            if(!initial) {
                Intent intent = new Intent(this, MovieDetail.class);
                intent.putExtra(ID_INTENT_KEY, item.getId());
                intent.putExtra(TITLE_INTENT_KEY, item.getOriginal_title());
                intent.putExtra(OVERVIEW_INTENT_KEY, item.getOverview());
                intent.putExtra(POSTERPATH_INTENT_KEY, item.getPoster_path());
                intent.putExtra(USERRATING_INTENT_KEY, item.getVote_average());
                intent.putExtra(RELEASEDATE_INTENT_KEY, item.getRelease_date());
                startActivity(intent);
            }
        }
    }

    @Override
    public void refreshList() {
        if (mTwoPane)
        {  String sortBy = Utility.getPreferredSort(this);
            if(sortBy.equals("fav")) {
                MovieListActivityFragment movieListActivityFragment = ((MovieListActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_list));
                movieListActivityFragment.initializePosition();
                movieListActivityFragment.UpdateMovieList();
            }

        }
    }
}
