package com.adupp.administrator.fawazmoviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MovieListActivity extends AppCompatActivity {
    private final String LOG_TAG = MovieListActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_list);
        if(findViewById(R.id.movie_detail_container)!=null)
        {mTwoPane=true;
            if(savedInstanceState!=null)
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,new MovieDetailFragment(),DETAILFRAGMENT_TAG).commit();
        }else
        mTwoPane =false;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
}
