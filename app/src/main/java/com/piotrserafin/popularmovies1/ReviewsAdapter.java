package com.piotrserafin.popularmovies1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piotrserafin.popularmovies1.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private final Context context;
    private List<Review> results;

    public ReviewsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder holder, int position) {
        holder.review.setText(results.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return (results == null) ? 0 : results.size();
    }

    public void setReviewsList(List<Review> results) {
        this.results = new ArrayList<>();
        this.results.addAll(results);
        notifyDataSetChanged();
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView review;

        ReviewsAdapterViewHolder(View view) {
            super(view);
            review = view.findViewById(R.id.review);
        }

    }
}
