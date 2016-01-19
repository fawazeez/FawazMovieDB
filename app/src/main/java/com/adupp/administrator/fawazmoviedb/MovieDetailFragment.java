package com.adupp.administrator.fawazmoviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {
    private static final String TAG = MovieListActivityFragment.class.getSimpleName();
    private static final String TITLE_INTENT_KEY = "Original Title";
    private static final String OVERVIEW_INTENT_KEY = "Synopsis ";
    private static final String POSTERPATH_INTENT_KEY = "Poter";
    private static final String USERRATING_INTENT_KEY = "User Rating";
    private static final String RELEASEDATE_INTENT_KEY = "Release Date";
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        String releaseDate= null;
        if (intent != null && intent.hasExtra(TITLE_INTENT_KEY)) {
            ((TextView) rootView.findViewById(R.id.title)).setText(intent.getStringExtra(TITLE_INTENT_KEY));
            if ( intent.hasExtra(OVERVIEW_INTENT_KEY))
                ((TextView) rootView.findViewById(R.id.OverViewText)).setText(intent.getStringExtra(OVERVIEW_INTENT_KEY));
            if ( intent.hasExtra(RELEASEDATE_INTENT_KEY)){
                try{
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = format.parse(intent.getStringExtra(RELEASEDATE_INTENT_KEY));
                format = new SimpleDateFormat("MMM dd,yyyy");
                releaseDate = format.format(newDate);
                }catch (ParseException e)
                {
                    Log.e(TAG, "Date Parsing Error", e);
                }
                ((TextView) rootView.findViewById(R.id.dateTextView)).setText(releaseDate);

            }

            if ( intent.hasExtra(USERRATING_INTENT_KEY)) {
                ((RatingBar) rootView.findViewById(R.id.movieRatingBar)).setRating(Float.parseFloat(intent.getStringExtra(USERRATING_INTENT_KEY)));
                ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(intent.getStringExtra(USERRATING_INTENT_KEY)+" /10");
            }
            if ( intent.hasExtra(POSTERPATH_INTENT_KEY))
                Picasso.with(getActivity()).load(intent.getStringExtra(POSTERPATH_INTENT_KEY)).into((ImageView)rootView.findViewById(R.id.movie_item_image));

        }return rootView;
    }
}
