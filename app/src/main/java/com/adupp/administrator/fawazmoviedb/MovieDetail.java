package com.adupp.administrator.fawazmoviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MovieDetail
        extends AppCompatActivity implements MovieDetailFragment.favCallBack {
    private static final String TITLE_INTENT_KEY = "Original Title";
    private static final String ID_INTENT_KEY = "ID";
    private static final String OVERVIEW_INTENT_KEY = "Synopsis ";
    private static final String POSTERPATH_INTENT_KEY = "Poster";
    private static final String USERRATING_INTENT_KEY = "User Rating";
    private static final String RELEASEDATE_INTENT_KEY = "Release Date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
if (savedInstanceState == null)
{
    Bundle bundle = new Bundle();
    String id= getIntent().getStringExtra(ID_INTENT_KEY);
    bundle.putString(ID_INTENT_KEY,getIntent().getStringExtra(ID_INTENT_KEY));
    bundle.putString(TITLE_INTENT_KEY,getIntent().getStringExtra(TITLE_INTENT_KEY));
    bundle.putString(OVERVIEW_INTENT_KEY, getIntent().getStringExtra(OVERVIEW_INTENT_KEY));
    bundle.putString(POSTERPATH_INTENT_KEY, getIntent().getStringExtra(POSTERPATH_INTENT_KEY));
    bundle.putString(USERRATING_INTENT_KEY, getIntent().getStringExtra(USERRATING_INTENT_KEY));
    bundle.putString(RELEASEDATE_INTENT_KEY, getIntent().getStringExtra(RELEASEDATE_INTENT_KEY));
    MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
    movieDetailFragment.setArguments(bundle);
    getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container,movieDetailFragment).commit();
}
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if (id==R.id.action_settings)
        {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refreshList() {

    }
}
