package com.piotrserafin.popularmovies.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.piotrserafin.popularmovies.R;
import com.piotrserafin.popularmovies.api.TmdbClient;
import com.piotrserafin.popularmovies.model.Movie;
import com.piotrserafin.popularmovies.model.Movies;
import com.piotrserafin.popularmovies.ui.CommandFactory;
import com.piotrserafin.popularmovies.ui.MovieSortType;
import com.piotrserafin.popularmovies.ui.adapters.MoviesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    public static final String TAG = MainActivity.class.getSimpleName();

    private MovieSortType sortType = MovieSortType.MOST_POPULAR;

    @BindView(R.id.movies_grid)
    RecyclerView moviesRecyclerView;

    private MoviesAdapter moviesAdapter;
    private final CommandFactory commandFactory = CommandFactory.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        moviesAdapter = new MoviesAdapter(this, this);
        moviesRecyclerView.setAdapter(moviesAdapter);

        commandFactory.addCommand(MovieSortType.MOST_POPULAR, this::fetchPopularMovies);
        commandFactory.addCommand(MovieSortType.TOP_RATED, this::fetchTopRatedMovies);
        commandFactory.addCommand(MovieSortType.FAVORITES, this::fetchFavorites);

        commandFactory.execute(sortType);
    }

    private void fetchPopularMovies() {

        Call<Movies> moviesCall = TmdbClient.getInstance().getPopular();
        Callback<Movies> moviesCallback = new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> moviesCall, Response<Movies> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Movie> movieList = response.body().getResults();

                if(movieList.isEmpty()) {
                    return;
                }
                moviesAdapter.setMovieList(movieList);
            }

            @Override
            public void onFailure(Call<Movies> moviesCall, Throwable t) {
            }
        };
        moviesCall.enqueue(moviesCallback);
    }

    private void fetchTopRatedMovies() {

        Call<Movies> moviesCall = TmdbClient.getInstance().getTopRated();
        Callback<Movies> moviesCallback = new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> moviesCall, Response<Movies> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Movie> movieList = response.body().getResults();

                if(movieList.isEmpty()) {
                    return;
                }
                moviesAdapter.setMovieList(movieList);
            }

            @Override
            public void onFailure(Call<Movies> moviesCall, Throwable t) {
            }
        };
        moviesCall.enqueue(moviesCallback);
    }

    private void fetchFavorites() {
        Log.d(TAG, "Not implemented");
    }

    @Override
    public void onClick(Movie movie) {
        Intent movieDetailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
        movieDetailsIntent.putExtra("Movie", movie);
        startActivity(movieDetailsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        switch (sortType) {
            case MOST_POPULAR:
                menu.findItem(R.id.action_popularity).setChecked(true);
                break;
            case TOP_RATED:
                menu.findItem(R.id.action_topRated).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (checkIfOptionChanged(item)) return true;

        switch (item.getItemId()) {

            case R.id.action_popularity: {
                sortType = MovieSortType.MOST_POPULAR;
                commandFactory.execute(sortType);
                item.setChecked(true);
                return true;
            }

            case R.id.action_topRated: {
                sortType = MovieSortType.TOP_RATED;
                commandFactory.execute(sortType);
                item.setChecked(true);
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean checkIfOptionChanged(MenuItem item) {
        return ((item.getItemId() == R.id.action_popularity &&
                sortType == MovieSortType.MOST_POPULAR) ||
                (item.getItemId() == R.id.action_topRated &&
                        sortType == MovieSortType.TOP_RATED));
    }
}
