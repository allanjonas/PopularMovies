package com.example.allanjonas.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allanjonas on 2017-04-22.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    private ArrayList<String[]> reviews = new ArrayList<>();


    public class ReviewHolder extends RecyclerView.ViewHolder {
        private ImageView mProfilePicture;
        private TextView mAuthor;
        private TextView mReviewText;

        public ReviewHolder(View itemView){
            super(itemView);
            mProfilePicture = (ImageView) itemView.findViewById(R.id.review_picture);
            mAuthor = (TextView) itemView.findViewById(R.id.author_tv);
            mReviewText = (TextView) itemView.findViewById(R.id.review_tv);
        }
    }

    public void setReviewData(List<String[]> data) {
        reviews.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @Override
    public ReviewAdapter.ReviewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View trailerView = inflater.inflate(R.layout.review_item, viewGroup, false);
        return new ReviewAdapter.ReviewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewHolder holder, int position) {
        holder.mAuthor.setText(reviews.get(position)[0]);
        holder.mReviewText.setText(reviews.get(position)[1]);
    }
}
