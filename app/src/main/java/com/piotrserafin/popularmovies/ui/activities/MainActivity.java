package com.piotrserafin.popularmovies.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.piotrserafin.popularmovies.utils.MovieSortType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int SPAN_COUNT = 2;

    //The answer to the ultimate question of life, the universe and everything
    private static final int ID_FAVORITE_MOVIES_LOADER = 42;

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SORT_TYPE = "EXTRA_SORT_TYPE";

    private final String RECYCLER_POSITION_KEY = "recycler_position";
    private int recyclerPosition = RecyclerView.NO_POSITION;

    private static Bundle bundleActivityState;

    private MoviesAdapter moviesAdapter;
    private MovieSortType sortType = MovieSortType.MOST_POPULAR;
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

        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        moviesRecyclerView.setHasFixedSize(true);

        moviesAdapter = new MoviesAdapter(this, this, new ArrayList<>());
        moviesRecyclerView.setAdapter(moviesAdapter);

        commandFactory.addCommand(MovieSortType.MOST_POPULAR, this::fetchPopularMovies);
        commandFactory.addCommand(MovieSortType.TOP_RATED, this::fetchTopRatedMovies);
        commandFactory.addCommand(MovieSortType.FAVORITES, this::fetchFavorites);

        if (savedInstanceState != null) {
            sortType = savedInstanceState.getParcelable(EXTRA_SORT_TYPE);
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                moviesAdapter.setMovieList(movies);
                startLoaderIfSortOrderEqualsFavorites();
            }
            updateLayout();
        } else {
            fetchMovies();
        }
    }

    private void fetchMovies() {
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
                moviesAdapter.notifyDataSetChanged();
                updateLayout();
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
                moviesAdapter.notifyDataSetChanged();
                updateLayout();
            }

            @Override
            public void onFailure(Call<Movies> moviesCall, Throwable t) {

            }
        };
        moviesCall.enqueue(moviesCallback);
    }

    private void fetchFavorites() {
        getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIES_LOADER, null, this);
    }

    private void updateLayout() {
        if (moviesAdapter.getItemCount() == 0) {
            if (sortType.equals(MovieSortType.FAVORITES)) {
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

        outState.putParcelable(RECYCLER_POSITION_KEY,  moviesRecyclerView.getLayoutManager().onSaveInstanceState());

        ArrayList<Movie> movies = moviesAdapter.getResults();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_MOVIES, movies);
        }
        outState.putParcelable(EXTRA_SORT_TYPE, sortType);

        stopLoaderIfSortOrderEqualsFavorites();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(EXTRA_SORT_TYPE)) {
            sortType = savedInstanceState.getParcelable(EXTRA_SORT_TYPE);
        }

        if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
            List<Movie> movies = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
            moviesAdapter.setMovieList(movies);
            updateLayout();
        }

        if (savedInstanceState.containsKey(RECYCLER_POSITION_KEY)) {
            Parcelable listState = savedInstanceState.getParcelable(RECYCLER_POSITION_KEY);
            moviesRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        bundleActivityState = new Bundle();
        Parcelable rvState = moviesRecyclerView.getLayoutManager().onSaveInstanceState();
        bundleActivityState.putParcelable(RECYCLER_POSITION_KEY, rvState);

        ArrayList<Movie> movies = moviesAdapter.getResults();
        if (movies != null && !movies.isEmpty()) {
            bundleActivityState.putParcelableArrayList(EXTRA_MOVIES, movies);
        }
        bundleActivityState.putParcelable(EXTRA_SORT_TYPE, sortType);

        stopLoaderIfSortOrderEqualsFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bundleActivityState != null) {
            if (bundleActivityState.containsKey(EXTRA_SORT_TYPE)) {
                sortType = bundleActivityState.getParcelable(EXTRA_SORT_TYPE);
            }

            if (bundleActivityState.containsKey(EXTRA_MOVIES)) {
                List<Movie> movies = bundleActivityState.getParcelableArrayList(EXTRA_MOVIES);
                moviesAdapter.setMovieList(movies);
                startLoaderIfSortOrderEqualsFavorites();
                updateLayout();
            }

            if (bundleActivityState.containsKey(RECYCLER_POSITION_KEY)) {
                Parcelable listState = bundleActivityState.getParcelable(RECYCLER_POSITION_KEY);
                moviesRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }
        }

        startLoaderIfSortOrderEqualsFavorites();
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
                sortType = MovieSortType.MOST_POPULAR;
                fetchMovies();
                item.setChecked(true);
                return true;
            }

            case R.id.action_topRated: {
                stopLoaderIfSortOrderEqualsFavorites();
                sortType = MovieSortType.TOP_RATED;
                fetchMovies();
                item.setChecked(true);
                return true;
            }

            case R.id.action_favorites: {
                sortType = MovieSortType.FAVORITES;
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
        if (sortType.equals(MovieSortType.FAVORITES)) {
            getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIES_LOADER, null, this);
        }
    }

    private void stopLoaderIfSortOrderEqualsFavorites() {
        if (sortType.equals(MovieSortType.FAVORITES)) {
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
        moviesAdapter.notifyDataSetChanged();
        updateLayout();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        moviesAdapter.setMovieList((Cursor)null);
    }
}
