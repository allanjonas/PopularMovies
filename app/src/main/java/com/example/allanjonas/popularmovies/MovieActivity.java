package com.example.allanjonas.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allanjonas.popularmovies.data.MovieContract;
import com.example.allanjonas.popularmovies.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by allanjonas on 2017-02-28.
 */

public class MovieActivity extends AppCompatActivity {

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private Movie mMovie;
    private boolean isFavorite = false;
    @BindView(R.id.poster_iv)
    ImageView mPoster;
    @BindView(R.id.overview_tv)
    TextView mOverView;
    @BindView(R.id.user_rating_tv)
    TextView mUserRating;
    @BindView(R.id.release_date_tv)
    TextView mReleaseDate;
    @BindView(R.id.trailer_rv)
    RecyclerView mTrailerView;
    @BindView(R.id.review_rv)
    RecyclerView mReviewView;
    @BindView(R.id.favorite_button)
    ImageButton mFavoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");
            if (movie == null) {
                mReleaseDate.setText(getString(R.string.error_movie_activity));
            } else {
                getSupportActionBar().setTitle(movie.getmTitle());
                Picasso.with(this)
                        .load(movie.getmPoster())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(mPoster);
                mMovie = movie;
                setupFavoriteButton();
                mOverView.setText(movie.getmOverview());
                mUserRating.setText(getString(R.string.avr_vote_movie_activity) + Double.toString(movie.getmVoteAverage()) + getString(R.string.out_of_10));
                mReleaseDate.setText(getString(R.string.release_date_movie_activity) + movie.getmReleaseDate());
                setupTrailerView(movie);
                setupReviewView(movie);

            }
        }
    }

    private void setupFavoriteButton(){
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(mMovie.getmId()).build();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if(cursor.getCount() != 0) {
            isFavorite = true;
        }
        if(isFavorite) {
            mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_off);

        }
    }

    public void editFavorite(View view) {

        if(isFavorite){
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(mMovie.getmId()).build();
            getContentResolver().delete(uri, null, null);

        } else {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry._ID, Integer.parseInt(mMovie.getmId()));
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getmTitle());
            values.put(MovieContract.MovieEntry.COLUMN_POSTER, mMovie.getmPoster());
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getmOverview());
            values.put(MovieContract.MovieEntry.COLUMN_VOTE, mMovie.getmVoteAverage());
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getmReleaseDate());
            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        }
        isFavorite = !isFavorite;
        setupFavoriteButton();

    }

    private void setupTrailerView(Movie movie) {
        new FetchTrailerTask().execute(movie.getmId());

        mTrailerView.setNestedScrollingEnabled(false);
        final GridLayoutManager gridLayout = new GridLayoutManager(this,
                MainActivity.calculateNoOfColumns(this, 200));
        mTrailerView.setLayoutManager(gridLayout);
        mTrailerView.setHasFixedSize(true);

        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerView.setAdapter(mTrailerAdapter);
    }


    private void setupReviewView(Movie movie) {
        new FetchReviewTask().execute(movie.getmId());

        mReviewView.setNestedScrollingEnabled(false);
        final LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        mReviewView.setLayoutManager(linearLayout);
        mReviewView.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter();
        mReviewView.setAdapter(mReviewAdapter);
    }


    private class FetchTrailerTask extends AsyncTask<String, Void, List<String[]>> {

        @Override
        protected List<String[]> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String movieId = params[0];
            URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(movieId);
            try {
                ArrayList<String[]> trailers = new ArrayList<String[]>();
                String jsonTrailerResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequestUrl);
                JSONObject trailerResponse = new JSONObject(jsonTrailerResponse);
                JSONArray movieArray = trailerResponse.getJSONArray("results");
                for (int i = 0; i < movieArray.length() && i < 6; i++) {
                    JSONObject jsonTrailer = movieArray.getJSONObject(i);
                    String name = jsonTrailer.getString("name");
                    String key = jsonTrailer.getString("key");
                    String[] trailer = {name, key};
                    trailers.add(trailer);
                }
                return trailers;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String[]> trailers) {
            if (trailers != null) {
                mTrailerAdapter.setTrailerData(trailers);
            }
        }
    }

    private class FetchReviewTask extends AsyncTask<String, Void, List<String[]>> {

        @Override
        protected List<String[]> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String movieId = params[0];
            URL reviewRequestUrl = NetworkUtils.buildReviewUrl(movieId);
            try {
                ArrayList<String[]> reviews = new ArrayList<String[]>();
                String jsonReviewResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestUrl);
                JSONObject reviewResponse = new JSONObject(jsonReviewResponse);
                JSONArray movieArray = reviewResponse.getJSONArray("results");
                for (int i = 0; i < movieArray.length() && i < 6; i++) {
                    JSONObject jsonReview = movieArray.getJSONObject(i);
                    String name = jsonReview.getString("author");
                    String key = jsonReview.getString("content");
                    String[] review = {name, key};
                    reviews.add(review);
                }

                return reviews;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String[]> reviews) {
            if (reviews != null) {
                mReviewAdapter.setReviewData(reviews);
            }
        }
    }
}
