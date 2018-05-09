package com.piotrserafin.popularmovies1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.piotrserafin.popularmovies1.api.TmdbClient;
import com.piotrserafin.popularmovies1.api.TmdbStrategy;
import com.piotrserafin.popularmovies1.model.Movie;
import com.piotrserafin.popularmovies1.model.Movies;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.debugTv)
    TextView debutTv;

    private TmdbStrategy sortOrder = TmdbStrategy.MOST_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fetchTmdbData();
    }

    private void fetchTmdbData() {

        Call<Movies> moviesCall = TmdbClient.getInstance().perform(sortOrder);
        Callback<Movies> moviesCallback = new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> moviesCall, Response<Movies> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Movie> movieList = response.body().getResults();

                debutTv.setText("");

                for(Movie movie : movieList) {
                    debutTv.append(movie.getOverview() + "\n\n\n");
                }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (checkIfOptionChanged(item)) return true;

        switch (item.getItemId()) {

            case R.id.action_popularity: {
                sortOrder = TmdbStrategy.MOST_POPULAR;
                fetchTmdbData();
                return true;
            }

            case R.id.action_topRated: {
                sortOrder = TmdbStrategy.TOP_RATED;
                fetchTmdbData();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean checkIfOptionChanged(MenuItem item) {
        return ((item.getItemId() == R.id.action_popularity &&
                sortOrder == TmdbStrategy.MOST_POPULAR) ||
                (item.getItemId() == R.id.action_topRated &&
                        sortOrder == TmdbStrategy.TOP_RATED));
    }
}
