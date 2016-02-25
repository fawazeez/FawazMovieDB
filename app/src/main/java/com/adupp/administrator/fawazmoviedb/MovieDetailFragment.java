package com.adupp.administrator.fawazmoviedb;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adupp.administrator.fawazmoviedb.api.IApiReview;
import com.adupp.administrator.fawazmoviedb.api.IApiTrailer;
import com.adupp.administrator.fawazmoviedb.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    ImageView mImageView;
    public Integer mID;
    public String mTitle;
    public String mOverView;
    public String mReleaseDate;
    public String mRating;
    public String mPosterPath;
    public static String key = null;
    Call<Trailer> callTrailer;
    Call<Review> callReview;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        ContentValues movieDetails = new ContentValues();

        mID = intent.getIntExtra(ID_INTENT_KEY,0);
        movieDetails.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mID);
        String releaseDate= null;
        if (intent != null && intent.hasExtra(TITLE_INTENT_KEY)) {
            {
                mTitle = intent.getStringExtra(TITLE_INTENT_KEY);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, mTitle);
                ((TextView) rootView.findViewById(R.id.title)).setText(intent.getStringExtra(TITLE_INTENT_KEY));
            }
            if ( intent.hasExtra(OVERVIEW_INTENT_KEY))
            {
                mOverView = intent.getStringExtra(OVERVIEW_INTENT_KEY);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, mOverView);
                ((TextView) rootView.findViewById(R.id.OverViewText)).setText(intent.getStringExtra(OVERVIEW_INTENT_KEY));
            }
            if ( intent.hasExtra(RELEASEDATE_INTENT_KEY)) {
                mReleaseDate = intent.getStringExtra(RELEASEDATE_INTENT_KEY);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
                releaseDate = Utility.formatDate(mReleaseDate);
                ((TextView) rootView.findViewById(R.id.dateTextView)).setText(releaseDate);
            }
            if ( intent.hasExtra(USERRATING_INTENT_KEY)) {
                mRating = intent.getStringExtra(USERRATING_INTENT_KEY);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_RATING, mRating);
                ((RatingBar) rootView.findViewById(R.id.movieRatingBar)).setRating(Float.parseFloat(mRating));
                ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(mRating+" /10");
            }
            mImageView =(ImageView)rootView.findViewById(R.id.movie_item_image);
            if (intent.hasExtra(POSTERPATH_INTENT_KEY)) {
                mPosterPath = intent.getStringExtra(POSTERPATH_INTENT_KEY);
                Picasso.with(getActivity()).load(mPosterPath).placeholder(R.mipmap.ic_launcher).error(R.drawable.connection_error).into(mImageView);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_POSTER, mPosterPath);
            }
            movieDetails.put(MovieContract.MovieEntry.COLUMN_FAVORITE,"N");

        }
        CheckBox checkBox = (CheckBox)rootView.findViewById(R.id.favCheckBox);

        Uri inserted = MovieContract.MovieEntry.buildMovieUri(mID);
        Cursor cursor = getActivity().getContentResolver().query(inserted,MOVIE_COLUMNS,null,null,null);
        if(cursor.getCount()>0) {
            int idColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
            int favColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
            ImageView test = (ImageView)rootView.findViewById(R.id.test);
            cursor.moveToFirst();
            String posterStr= cursor.getString(idColumn);
            String fav = cursor.getString(favColumn);
            if (fav.equals("Y")) {
                checkBox.setChecked(true);
                String idStr = posterStr.substring(posterStr.lastIndexOf('/') + 1);
                File filepath = getActivity().getFileStreamPath(idStr);
                test.setImageDrawable(Drawable.createFromPath(filepath.toString()));
            }
            else
            {
                checkBox.setChecked(false);
            }
        }
        else
        {

            inserted = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieDetails);
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        IApiTrailer serviceTrailer = retrofit.create(IApiTrailer.class);
        callTrailer = serviceTrailer.getTrailer(mID, API_KEY);
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
                            youtubeUri.appendPath("hqdefault.jpg");
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
       callReview = serviceReview.getReview(mID, API_KEY);
        callReview.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccess()) {
                    Review review = response.body();
                    ViewGroup reviewLayout = (ViewGroup) rootView.findViewById(R.id.reviewLayout);
                    for (Review.Result results : review.results) {
                        TextView reviewAuthorTextView = new TextView(getActivity());
                        TextView reviewTextView = new TextView(getActivity());
                        reviewAuthorTextView.setText(getString(R.string.author_label) +
                                "-" + results.author);
                        reviewAuthorTextView.setTypeface(null, Typeface.BOLD);
                        reviewTextView.setText("\t" + results.content + "\n");
                        reviewLayout.addView(reviewAuthorTextView);
                        reviewLayout.addView(reviewTextView);
                    }
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               ContentValues updateFav = new ContentValues();
               Uri updateUri = MovieContract.MovieEntry.buildMovieUri(mID);
               if (isChecked)
               {
                   updateFav.put(MovieContract.MovieEntry.COLUMN_FAVORITE,"Y");
                   getActivity().getContentResolver().update(updateUri,updateFav,null,null);
                   Resources res = getActivity().getResources();
                   BitmapDrawable drawable = (BitmapDrawable)mImageView.getDrawable();
                   Bitmap bitmap = drawable.getBitmap();
                   File sFileDir = Environment.getExternalStorageDirectory();
                   File image = new File(sFileDir,mPosterPath);
                   FileOutputStream outputStream;
                   try {
                       //outputStream = new FileOutputStream(image);
                       String idStr = mPosterPath.substring(mPosterPath.lastIndexOf('/')+1);
                       outputStream = getActivity().openFileOutput(idStr, Context.MODE_PRIVATE);
                       bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                       outputStream.flush();
                       outputStream.close();
                   } catch (FileNotFoundException e) {
                       e.printStackTrace();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               else
               {
                   updateFav.put(MovieContract.MovieEntry.COLUMN_FAVORITE,"N");
                   getActivity().getContentResolver().update(updateUri,updateFav,null,null);
               }
           }
       });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        callReview.cancel();
        callTrailer.cancel();
    }
}
