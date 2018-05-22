package com.piotrserafin.popularmovies.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.piotrserafin.popularmovies.R;
import com.piotrserafin.popularmovies.data.MovieContract;
import com.piotrserafin.popularmovies.model.Movie;
import com.piotrserafin.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private final Context context;
    final private MoviesAdapterOnClickHandler clickHandler;
    private List<Movie> results;

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler, List<Movie> results) {
        this.context = context;
        this.clickHandler = clickHandler;
        this.results = results;
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_row_item, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder holder, int position) {

        Movie movie = results.get(position);

        Picasso.get()
                .load(Utils.preparePosterImagePath(movie.getPosterPath()))
                .into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return (results == null) ? 0 : results.size();
    }

    public void setMovieList(List<Movie> movies) {
        results.clear();
        results.addAll(movies);
        notifyDataSetChanged();
    }

    public void setMovieList(Cursor data) {
        results.clear();
        if (data != null && data.moveToFirst()) {
            do {

                Movie movie = new Movie(
                        data.getLong(MovieContract.MovieEntry.INDEX_MOVIE_ID),
                        data.getString(MovieContract.MovieEntry.INDEX_TITLE),
                        data.getString(MovieContract.MovieEntry.INDEX_POSTER_PATH),
                        data.getString(MovieContract.MovieEntry.INDEX_BACKDROP_PATH),
                        data.getFloat(MovieContract.MovieEntry.INDEX_VOTE_AVG),
                        data.getString(MovieContract.MovieEntry.INDEX_RELEASE_DATE),
                        data.getString(MovieContract.MovieEntry.INDEX_OVERVIEW));

                results.add(movie);

            } while (data.moveToNext());
        }
        notifyDataSetChanged();
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView posterImage;

        public MoviesAdapterViewHolder(View view) {
            super(view);

            posterImage = view.findViewById(R.id.poster_img);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movie = results.get(position);
            clickHandler.onClick(movie);
        }
    }
}
