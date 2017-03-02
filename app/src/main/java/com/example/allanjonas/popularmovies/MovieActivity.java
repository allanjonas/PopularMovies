package com.example.allanjonas.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by allanjonas on 2017-02-28.
 */

public class MovieActivity extends AppCompatActivity {

    private ImageView mPoster;
    private TextView mOverView;
    private TextView mUserRating;
    private TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        mPoster = (ImageView) findViewById(R.id.poster_iv);
        mOverView = (TextView) findViewById(R.id.overview_tv);
        mUserRating = (TextView) findViewById(R.id.user_rating_tv);
        mReleaseDate = (TextView) findViewById(R.id.release_date_tv);

        Intent intent = getIntent();

        if(intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");
            if(movie == null) {
                mReleaseDate.setText("Something went wrong try again");
            } else {
                getSupportActionBar().setTitle(movie.getmTitle());
                Picasso.with(this).load(movie.getmPoster()).into(mPoster);
                mOverView.setText(movie.getmOverview());
                mUserRating.setText("Average Vote: " + movie.getmVoteAverage() + "/10");
                mReleaseDate.setText("Release date: " + movie.getmReleaseDate());

            }
        }


    }
}
