package com.example.allanjonas.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allanjonas on 2017-04-22.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {
    private ArrayList<String[]> trailers = new ArrayList<>();
    private final Context mContext;

    public class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final Button trailer_button;

        public TrailerHolder(View itemView){
            super(itemView);
            trailer_button = (Button) itemView.findViewById(R.id.trailer_button);
            trailer_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!trailers.isEmpty()) {
                String[] trailer = trailers.get(adapterPosition);
                watchYoutubeVideo(trailer[1]);
            } else {
                Toast.makeText(mContext, R.string.error_toast_click_movie,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            mContext.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(webIntent);
        }
    }

    public void setTrailerData(List<String[]> data) {
        trailers.addAll(data);
        notifyDataSetChanged();
    }

    public TrailerAdapter(Context context ) {
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View trailerView = inflater.inflate(R.layout.trailer_item, viewGroup, false);
        return new TrailerAdapter.TrailerHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        holder.trailer_button.setText(holder.trailer_button.getText()+Integer.toString(position+1));
    }
}
