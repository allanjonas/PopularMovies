package com.example.allanjonas.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allanjonas on 2017-02-26.
 */

public class MovieAdapter extends
        RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    private ArrayList<Movie> movies = new ArrayList<Movie>();
    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public  class MovieHolder extends RecyclerView.ViewHolder implements OnClickListener{
        public final ImageView movie_poster;

        public MovieHolder(View itemView){
            super(itemView);
            movie_poster = (ImageView) itemView.findViewById(R.id.poster_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!movies.isEmpty()) {
                Movie movie = movies.get(adapterPosition);
                mClickHandler.onClick(movie);
            } else {
                Toast.makeText((MainActivity)mClickHandler, R.string.error_toast_click_movie,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler ) {
        mClickHandler = clickHandler;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovieData(List<Movie> data) {
        movies.addAll(data);
        notifyDataSetChanged();
    }

    public void resetMovieData(){
        movies = new ArrayList<Movie>();
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovieData() { return movies; }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.movie_item, viewGroup, false);
        return new MovieHolder(movieView);
    }

    @Override
    public void onBindViewHolder(MovieHolder movieViewHolder, int position) {
        Picasso.with((MainActivity)mClickHandler)
                .load(movies.get(position).getmPoster())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(movieViewHolder.movie_poster);
    }
}

