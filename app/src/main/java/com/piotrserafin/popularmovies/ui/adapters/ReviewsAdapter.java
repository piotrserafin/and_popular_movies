package com.piotrserafin.popularmovies.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piotrserafin.popularmovies.R;
import com.piotrserafin.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private final Context context;
    private ArrayList<Review> results;

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
        Review review = results.get(position);
        holder.reviewAuthor.setText(review.getAuthor());
        holder.reviewContent.setText(review.getContent());
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

    public ArrayList<Review> getResults() {
        return results;
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_author)
        TextView reviewAuthor;

        @BindView(R.id.review_content)
        TextView reviewContent;

        ReviewsAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
