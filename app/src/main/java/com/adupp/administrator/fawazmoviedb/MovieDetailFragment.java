package com.adupp.administrator.fawazmoviedb;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.ShareActionProvider;
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
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class MovieDetailFragment extends Fragment  {
    private static final String TAG = MovieListActivityFragment.class.getSimpleName();
    private static final String TITLE_INTENT_KEY = "Original Title";
    private static final String ID_INTENT_KEY = "ID";
    private static final String OVERVIEW_INTENT_KEY = "Synopsis ";
    private static final String POSTERPATH_INTENT_KEY = "Poster";
    private static final String USERRATING_INTENT_KEY = "User Rating";
    private static final String RELEASEDATE_INTENT_KEY = "Release Date";
    private static final String API_URL = "http://api.themoviedb.org/3/";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String TRAILER_SHARE = "#TRAILER : ";
    private  String TRAILER_URl = null;

    ImageView mImageView;
    public Integer mID;
    public String mTitle;
    public String mOverView;
    public String mReleaseDate;
    public String mRating;
    public String mPosterPath;
    public String mFavorite;
    boolean initialLoad =true;
    public static String key = null;
    Call<Trailer> callTrailer;
    Call<Review> callReview;
    private favCallBack callbackfav;
    private ShareActionProvider shareActionProvider;

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

    public interface favCallBack {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void refreshList();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof favCallBack)
            callbackfav = (favCallBack) context;
        else
            throw new ClassCastException(context.toString()+"must implement CallBack");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbackfav=null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    public MovieDetailFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
       Bundle bundle = getArguments();
        ContentValues movieDetails = new ContentValues();
         String releaseDate= null;
        if (bundle != null) {
            initialLoad=false;
            mID = Integer.parseInt(bundle.getString(ID_INTENT_KEY));
            mTitle = bundle.getString(TITLE_INTENT_KEY);
            ((TextView) rootView.findViewById(R.id.title)).setText(mTitle);
            mOverView = bundle.getString(OVERVIEW_INTENT_KEY);
            ((TextView) rootView.findViewById(R.id.OverViewText)).setText(mOverView);
            mReleaseDate = bundle.getString(RELEASEDATE_INTENT_KEY);
            releaseDate = Utility.formatDate(mReleaseDate);
            ((TextView) rootView.findViewById(R.id.dateTextView)).setText(releaseDate);

            mRating = bundle.getString(USERRATING_INTENT_KEY);
            ((RatingBar) rootView.findViewById(R.id.movieRatingBar)).setRating(Float.parseFloat(mRating));
            ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(mRating + " /10");
            mImageView = (ImageView) rootView.findViewById(R.id.movie_item_image);
            mPosterPath = bundle.getString(POSTERPATH_INTENT_KEY);
            Picasso.with(getActivity()).load(mPosterPath).placeholder(R.mipmap.ic_launcher).error(R.drawable.connection_error).into(mImageView);
            movieDetails.put(MovieContract.MovieEntry.COLUMN_FAVORITE, "N");


            CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.favCheckBox);

            Uri inserted = MovieContract.MovieEntry.buildMovieUri(mID);
            Cursor cursor = getActivity().getContentResolver().query(inserted, MOVIE_COLUMNS, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mPosterPath = cursor.getString(COL_POSTER);
                mFavorite = cursor.getString(COL_FAVORITE);
//            mID = Integer.parseInt(cursor.getString(COL_MOVIE_ID));
//            mTitle = cursor.getString(COL_MOVIE_NAME);
//            mOverView = cursor.getString(COL_SYNOPSIS);
//            mReleaseDate = Utility.formatDate(cursor.getString(COL_RELEASE_DATE));
//            mRating = cursor.getString(COL_RATING);
                if (mFavorite.equals("Y")) {
                    checkBox.setChecked(true);
                    String idStr = mPosterPath.substring(mPosterPath.lastIndexOf('/') + 1);
                    File filepath = getActivity().getFileStreamPath(idStr);
                    mImageView.setImageDrawable(Drawable.createFromPath(filepath.toString()));
                } else {
                    checkBox.setChecked(false);
//                ((TextView) rootView.findViewById(R.id.title)).setText(mTitle);
//                ((TextView) rootView.findViewById(R.id.OverViewText)).setText(mOverView);
//                ((TextView) rootView.findViewById(R.id.dateTextView)).setText(mReleaseDate);
//                ((RatingBar) rootView.findViewById(R.id.movieRatingBar)).setRating(Float.parseFloat(mRating));
//                ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(mRating + " /10");
                    Picasso.with(getActivity()).load(mPosterPath).placeholder(R.mipmap.ic_launcher).error(R.drawable.connection_error).into(mImageView);
                }
            } else {
                movieDetails.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mID);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, mTitle);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, mOverView);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_RATING, mRating);
                movieDetails.put(MovieContract.MovieEntry.COLUMN_POSTER, mPosterPath);
                Uri inertUri = MovieContract.MovieEntry.CONTENT_URI;
                Uri i = getActivity().getContentResolver().insert(inertUri, movieDetails);
            }
            cursor.close();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create())
                    .build();
            IApiTrailer serviceTrailer = retrofit.create(IApiTrailer.class);
            callTrailer = serviceTrailer.getTrailer(mID, API_KEY);
            callTrailer.enqueue(new Callback<Trailer>() {
                @Override
                public void onResponse(Call<Trailer> call, Response<Trailer> response) {
                    if (response.isSuccess()) {
                        Uri builtUri = null;
                        if(shareActionProvider!=null)
                        {
                            shareActionProvider.setShareIntent(null);
                        }
                        ViewGroup trailerLayout = (ViewGroup) rootView.findViewById(R.id.trailerLayout);
                        Trailer trailer = response.body();
                        int i =0;
                        for (Trailer.Result results : trailer.results) {
                            ImageButton trailerImage = new ImageButton(getActivity());
                            key = results.key;
                            if(i==0)
                            {
                                TRAILER_URl="http://www.youtube.com/watch?v=" + key;
                                if(shareActionProvider!=null)
                                {
                                    shareActionProvider.setShareIntent(createShareTrailer());
                                }

                            }
                            i++;
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
                    if (isChecked) {
                        updateFav.put(MovieContract.MovieEntry.COLUMN_FAVORITE, "Y");
                        getActivity().getContentResolver().update(updateUri, updateFav, null, null);
                        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        File sFileDir = Environment.getExternalStorageDirectory();
                        FileOutputStream outputStream;
                        try {
                            //outputStream = new FileOutputStream(image);
                            String idStr = mPosterPath.substring(mPosterPath.lastIndexOf('/') + 1);
                            outputStream = getActivity().openFileOutput(idStr, Context.MODE_PRIVATE);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        updateFav.put(MovieContract.MovieEntry.COLUMN_FAVORITE, "N");
                        getActivity().getContentResolver().update(updateUri, updateFav, null, null);
                        callbackfav.refreshList();
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share,menu);
        MenuItem menuItem =menu.findItem(R.id.action_share);
         shareActionProvider =(ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id=item.getItemId();
//
//        if (id==R.id.action_settings)
//        {
//            startActivity(new Intent(getActivity(),SettingsActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onPause() {
        super.onPause();
        if(!initialLoad) {
            callReview.cancel();
            callTrailer.cancel();
        }
    }

    private Intent createShareTrailer()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,TRAILER_URl);
        return shareIntent;
    }
}
