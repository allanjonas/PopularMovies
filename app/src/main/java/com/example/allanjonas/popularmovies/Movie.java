package com.example.allanjonas.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by allanjonas on 2017-02-26.
 */

public class Movie implements Parcelable {
    private String mId;
    private String mTitle;
    private String mPoster;
    private String mOverview;
    private double mVoteAverage;
    private String mReleaseDate;

    public Movie(String id, String title, String poster, String overview,
                 double voteAverage, String releaseDate) {
        mId = id;
        mTitle = title;
        mPoster = poster;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
    }

    public String getmId() {
        return mId;
    }
    public String getmTitle() {
        return mTitle;
    }

    public String getmPoster() {
        return mPoster;
    }

    public String getmOverview() {
        return mOverview;
    }

    public double getmVoteAverage() {
        return mVoteAverage;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    protected Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mPoster = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readDouble();
        mReleaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mPoster);
        dest.writeString(mOverview);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mReleaseDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}