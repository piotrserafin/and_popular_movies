package com.piotrserafin.popularmovies1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.piotrserafin.popularmovies1.api.TmdbClient;
import com.piotrserafin.popularmovies1.model.Movie;
import com.piotrserafin.popularmovies1.model.Movies;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler{

    public static final String TAG = MainActivity.class.getSimpleName();

    private TmdbClient.Strategy sortOrder = TmdbClient.Strategy.MOST_POPULAR;

    @BindView(R.id.movies_grid)
    RecyclerView moviesRecyclerView;

    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        moviesAdapter = new MoviesAdapter(this, this);
        moviesRecyclerView.setAdapter(moviesAdapter);

        fetchTmdbData();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");
    }

    private void fetchTmdbData() {

        Call<Movies> moviesCall = TmdbClient.getInstance().fetch(sortOrder);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        switch (sortOrder) {
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
                sortOrder = TmdbClient.Strategy.MOST_POPULAR;
                fetchTmdbData();
                item.setChecked(true);
                return true;
            }

            case R.id.action_topRated: {
                sortOrder = TmdbClient.Strategy.TOP_RATED;
                fetchTmdbData();
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
                sortOrder == TmdbClient.Strategy.MOST_POPULAR) ||
                (item.getItemId() == R.id.action_topRated &&
                        sortOrder == TmdbClient.Strategy.TOP_RATED));
    }
}
