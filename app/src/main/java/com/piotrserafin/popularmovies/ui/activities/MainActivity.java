package com.piotrserafin.popularmovies.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.piotrserafin.popularmovies.R;
import com.piotrserafin.popularmovies.api.TmdbClient;
import com.piotrserafin.popularmovies.data.MovieContract;
import com.piotrserafin.popularmovies.model.Movie;
import com.piotrserafin.popularmovies.model.Movies;
import com.piotrserafin.popularmovies.ui.adapters.MoviesAdapter;
import com.piotrserafin.popularmovies.utils.CommandFactory;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MainActivity.class.getSimpleName();

    //The answer to the ultimate question of life, the universe and everything
    private static final int ID_FAVORITE_MOVIES_LOADER = 42;

    public static final String MOVIES = "MOVIES";

    @Retention(SOURCE)
    @StringDef({
            MOST_POPULAR,
            TOP_RATED,
            FAVORITES
    })
    public @interface sortType {}
    public static final String MOST_POPULAR = "most_popular";
    public static final String TOP_RATED = "top_rated";
    public static final String FAVORITES = "favorites";

    private MoviesAdapter moviesAdapter;
    private final CommandFactory commandFactory = CommandFactory.getInstance();

    @BindView(R.id.movies_grid)
    RecyclerView moviesRecyclerView;

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.main_acitvity_name);

        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        moviesAdapter = new MoviesAdapter(this, this, new ArrayList<>());
        moviesRecyclerView.setAdapter(moviesAdapter);

        commandFactory.addCommand(MOST_POPULAR, this::fetchPopularMovies);
        commandFactory.addCommand(TOP_RATED, this::fetchTopRatedMovies);
        commandFactory.addCommand(FAVORITES, this::fetchFavorites);

        if(savedInstanceState != null) {
            String sortType = getSortBySharedPreference();

            if (sortType.equals(FAVORITES)) {
                getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIES_LOADER, null, this);
            } else {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIES);
                moviesAdapter.setMovieList(movies);
                updateLayout();
            }
        } else {
            fetchMovies();
        }
    }

    private void fetchMovies() {
        commandFactory.execute(getSortBySharedPreference());
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
                updateLayout();
            }

            @Override
            public void onFailure(Call<Movies> moviesCall, Throwable t) {
                updateLayout();
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
                updateLayout();
            }

            @Override
            public void onFailure(Call<Movies> moviesCall, Throwable t) {
                updateLayout();
            }
        };
        moviesCall.enqueue(moviesCallback);
    }

    private void fetchFavorites() {
        startLoaderIfSortOrderEqualsFavorites();
    }

    private void updateLayout() {
        if (moviesAdapter.getItemCount() == 0) {
            String sortType = getSortBySharedPreference();
            if (sortType.equals(FAVORITES)) {
                findViewById(R.id.no_connection).setVisibility(View.GONE);
                findViewById(R.id.empty_favorites).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.no_connection).setVisibility(View.VISIBLE);
                findViewById(R.id.empty_favorites).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.no_connection).setVisibility(View.GONE);
            findViewById(R.id.empty_favorites).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Movie> movies = moviesAdapter.getResults();

        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(MOVIES, movies);
        }

        String sortType = getSortBySharedPreference();
        if (!sortType.equals(FAVORITES)) {
            getSupportLoaderManager().destroyLoader(ID_FAVORITE_MOVIES_LOADER);
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent movieDetailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
        movieDetailsIntent.putExtra(DetailsActivity.MOVIE, movie);
        startActivity(movieDetailsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        String sortType = getSortBySharedPreference();
        switch (sortType) {
            case MOST_POPULAR:
                menu.findItem(R.id.action_popularity).setChecked(true);
                break;
            case TOP_RATED:
                menu.findItem(R.id.action_topRated).setChecked(true);
                break;
            case FAVORITES:
                menu.findItem(R.id.action_favorites).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_popularity: {
                stopLoaderIfSortOrderEqualsFavorites();
                setSortBySharedPreference(MOST_POPULAR);
                fetchMovies();
                item.setChecked(true);
                return true;
            }

            case R.id.action_topRated: {
                stopLoaderIfSortOrderEqualsFavorites();
                setSortBySharedPreference(TOP_RATED);
                fetchMovies();
                item.setChecked(true);
                return true;
            }

            case R.id.action_favorites: {
                setSortBySharedPreference(FAVORITES);
                fetchMovies();
                item.setChecked(true);
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void startLoaderIfSortOrderEqualsFavorites() {
        String sortType = getSortBySharedPreference();
        if (sortType.equals(FAVORITES)) {
            getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIES_LOADER, null, this);
        }
    }

    private void stopLoaderIfSortOrderEqualsFavorites() {
        String sortType = getSortBySharedPreference();
        if (sortType.equals(FAVORITES)) {
            getSupportLoaderManager().destroyLoader(ID_FAVORITE_MOVIES_LOADER);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {

            case ID_FAVORITE_MOVIES_LOADER:

                return new CursorLoader(this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.MOVIE_COLUMNS_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        moviesAdapter.setMovieList(data);
        updateLayout();
}

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        moviesAdapter.setMovieList((Cursor)null);
        updateLayout();
    }

    private @sortType String getSortBySharedPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getString(R.string.sort_by_key), MOST_POPULAR);
    }

    private void setSortBySharedPreference(@sortType String sortBy) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.sort_by_key), sortBy);
        editor.apply();
    }
}
