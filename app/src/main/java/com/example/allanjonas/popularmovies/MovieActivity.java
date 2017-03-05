package com.example.allanjonas.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by allanjonas on 2017-02-28.
 */

public class MovieActivity extends AppCompatActivity {

    @BindView(R.id.poster_iv) ImageView mPoster;
    @BindView(R.id.overview_tv)TextView mOverView;
    @BindView(R.id.user_rating_tv)TextView mUserRating;
    @BindView(R.id.release_date_tv)TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if(intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");
            if(movie == null) {
                mReleaseDate.setText(getString(R.string.error_movie_activity));
            } else {
                getSupportActionBar().setTitle(movie.getmTitle());
                Picasso.with(this)
                        .load(movie.getmPoster())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(mPoster);
                mOverView.setText(movie.getmOverview());
                mUserRating.setText(getString(R.string.avr_vote_movie_activity) + Double.toString(movie.getmVoteAverage()) + getString(R.string.out_of_10));
                mReleaseDate.setText(getString(R.string.release_date_movie_activity) + movie.getmReleaseDate());

            }
        }


    }
}
