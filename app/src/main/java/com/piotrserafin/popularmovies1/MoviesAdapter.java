package com.piotrserafin.popularmovies1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.piotrserafin.popularmovies1.model.Movie;
import com.piotrserafin.popularmovies1.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private final Context context;
    final private MoviesAdapterOnClickHandler clickHandler;
    private List<Movie> results;

    public interface MoviesAdapterOnClickHandler {
        void onClick(View view);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
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
                .placeholder(R.color.colorAccent)
                .into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return (results == null) ? 0 : results.size();
    }

    public void setMovieList(List<Movie> results) {
        this.results = new ArrayList<>();
        this.results.addAll(results);
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
            Log.d("MoviesAdapterViewHolder", "onClick()");
        }
    }
}
