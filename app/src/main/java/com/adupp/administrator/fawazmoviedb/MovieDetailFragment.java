package com.adupp.administrator.fawazmoviedb;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adupp.administrator.fawazmoviedb.api.IApiReview;
import com.adupp.administrator.fawazmoviedb.api.IApiTrailer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {
    private static final String TAG = MovieListActivityFragment.class.getSimpleName();
    private static final String TITLE_INTENT_KEY = "Original Title";
    private static final String ID_INTENT_KEY = "ID";
    private static final String OVERVIEW_INTENT_KEY = "Synopsis ";
    private static final String POSTERPATH_INTENT_KEY = "Poster";
    private static final String USERRATING_INTENT_KEY = "User Rating";
    private static final String RELEASEDATE_INTENT_KEY = "Release Date";
    private static final String API_URL = "http://api.themoviedb.org/3/";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    public Integer mID;
    public static String key = null;


    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        mID = intent.getIntExtra(ID_INTENT_KEY,0);
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

        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        IApiTrailer serviceTrailer = retrofit.create(IApiTrailer.class);
        Call<Trailer> callTrailer = serviceTrailer.getTrailer(mID, API_KEY);
        callTrailer.enqueue(new Callback<Trailer>() {
            @Override
            public void onResponse(Call<Trailer> call, Response<Trailer> response) {
                if (response.isSuccess()) {
                    Uri builtUri= null;
                    ViewGroup trailerLayout = (ViewGroup)rootView.findViewById(R.id.trailerLayout);
                    int i=0;
                    Trailer trailer = response.body();
                    for (Trailer.Result results : trailer.results) {
                        ++i;
                        if(i<3) {
                            ImageButton trailerImage = new ImageButton(getActivity());
                            key = results.key;
                            Uri.Builder youtubeUri = new Uri.Builder();
                            youtubeUri.scheme("http");
                            youtubeUri.authority("img.youtube.com");
                            youtubeUri.appendPath("vi");
                            youtubeUri.appendPath(key);
                            youtubeUri.appendPath("default.jpg");
                            builtUri = youtubeUri.build();
                            Picasso.with(getActivity()).load(builtUri).placeholder(R.mipmap.ic_launcher).error(R.drawable.connection_error).into(trailerImage);
                            trailerImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException ex) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
                                        startActivity(intent);
                                    }
                                }
                            });
//                        builtUri = Uri.parse("http://img.youtube.com/vi/"+key+"/default.jpg").buildUpon().build();
                            trailerLayout.addView(trailerImage);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<Trailer> call, Throwable t) {

            }
        });

        IApiReview serviceReview = retrofit.create(IApiReview.class);
        Call<Review> callReview = serviceReview.getReview(mID, API_KEY);
        callReview.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccess()) {
                    Review review = response.body();
                    ViewGroup reviewLayout = (ViewGroup)rootView.findViewById(R.id.reviewLayout);
                    for (Review.Result results : review.results) {
                        TextView reviewAuthorTextView =new TextView(getActivity());
                        TextView reviewTextView =new TextView(getActivity());
                        reviewAuthorTextView.setText(getString(R.string.author_label )+
                                "-" + results.author );
                        reviewAuthorTextView.setTypeface(null, Typeface.BOLD);
                        reviewTextView.setText("\t" + results.content +"\n");
                        reviewLayout.addView(reviewAuthorTextView);
                        reviewLayout.addView(reviewTextView);
                    }
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {

            }
        });
        return rootView;
    }

}
