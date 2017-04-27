package com.example.allanjonas.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.allanjonas.popularmovies.data.MovieContract;
import com.example.allanjonas.popularmovies.data.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{
    private static String CHOICE_POPULAR = "popular/";
    private static String CHOICE_TOP_RATED = "top_rated/";
    private static String CHOICE_FAVORITES = "favorites";
    private static String BUNDLE_LAYOUT = "mainLayout";
    private static String BUNDLE_MOVIELIST = "movieList";
    private static String BUNDLE_NEXTPAGE = "nextPage";
    private static String BUNDLE_SORTING = "sort";
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private String sortChoice = CHOICE_POPULAR;
    private int nextPage = 1;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(BUNDLE_MOVIELIST, mMovieAdapter.getMovieData());
        outState.putInt(BUNDLE_NEXTPAGE, nextPage);
        outState.putString(BUNDLE_SORTING, sortChoice);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView)findViewById(R.id.movie_rv);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        final GridLayoutManager gridLayout = new GridLayoutManager(MainActivity.this, calculateNoOfColumns(this, getResources().getInteger(R.integer.movie_poster_divider)));
        mRecyclerView.setLayoutManager(gridLayout);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if(savedInstanceState == null){
            loadMovieData(nextPage, sortChoice);
        }else {
            Parcelable savedRecyclerViewState = savedInstanceState.getBundle(BUNDLE_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
            mMovieAdapter.setMovieData(savedInstanceState.<Movie>getParcelableArrayList(BUNDLE_MOVIELIST));
            nextPage = savedInstanceState.getInt(BUNDLE_NEXTPAGE);
            sortChoice = savedInstanceState.getString(BUNDLE_SORTING);
        }

        // For dynamic scrolling of content
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = gridLayout.getItemCount();
                firstVisibleItem = gridLayout.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold) &&
                        sortChoice != CHOICE_FAVORITES) {

                    loadMovieData(nextPage, sortChoice);
                    loading = true;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sortChoice == CHOICE_FAVORITES) {
            resetMovieData();
            loadFavorites();
        }
    }

    public static int calculateNoOfColumns(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / width);
        return noOfColumns;
    }

    /**
     * Load additional movie data.
     * @param page Page to loaded from the OpenMovie API.
     * @param sort Sorting order of loaded data.
     */
    private void loadMovieData(Integer page, String sort) {
        showMovieDataView();
        new FetchMovieTask().execute(sort, page.toString());
        nextPage++;
    }

    private void loadFavorites() {
        showMovieDataView();
        ArrayList<Movie> movieList = new ArrayList<>();
        Uri movies = MovieContract.MovieEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(movies, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),
                        Double.parseDouble(cursor.getString(4)), cursor.getString(5));
                movieList.add(movie);
            } while (cursor.moveToNext());
            mMovieAdapter.setMovieData(movieList);
        }
    }

    /**
     * Empty and reset all loaded movie data.
     */
    private void resetMovieData(){
        mMovieAdapter.resetMovieData();
        nextPage = 1;
    }

    /**
     * Reset the scroll for the app.
     */
    private void resetScroll(){
        previousTotal = 0;
        loading = true;
        visibleThreshold = 5;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
    }

    /**
     * Start a new Activity for detailed info on a movie
     * @param movie
     */
    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = MovieActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);

    }

    /**
     * Displays the MovieDataView
     */
    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Displays an error message if we can't fetch movie data
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String sortMethod = params[0];
            String page = params[1];
            URL movieRequestUrl = NetworkUtils.buildSortUrl(sortMethod, page);
            try {
                ArrayList<Movie> movies = new ArrayList<Movie>();
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);
                JSONObject moviesResponse = new JSONObject(jsonMovieResponse);
                JSONArray movieArray = moviesResponse.getJSONArray("results");
                for(int i = 0; i < movieArray.length(); i++ ) {
                    JSONObject jsonMovie= movieArray.getJSONObject(i);
                    String title = jsonMovie.getString("title");
                    String poster = "http://image.tmdb.org/t/p/w342/" +
                            jsonMovie.getString("poster_path");
                    String overview = jsonMovie.getString("overview");
                    double voteAverage = jsonMovie.getDouble("vote_average");
                    String releaseDate = jsonMovie.getString("release_date");
                    int id = jsonMovie.getInt("id");
                    movies.add(new Movie(Integer.toString(id),title, poster, overview, voteAverage, releaseDate));

                }

                return movies;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        resetMovieData();
        resetScroll();

        if (id == R.id.action_popularity) {
            sortChoice = CHOICE_POPULAR;
            loadMovieData(nextPage, sortChoice);
            return true;
        }
        if(id == R.id.action_toprated) {
            sortChoice = CHOICE_TOP_RATED;
            loadMovieData(nextPage, sortChoice);
        }
        if(id == R.id.action_favorites) {
            sortChoice = CHOICE_FAVORITES;
            loadFavorites();
        }
        return super.onOptionsItemSelected(item);
    }
}
