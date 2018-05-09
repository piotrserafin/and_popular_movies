package com.piotrserafin.popularmovies1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.piotrserafin.popularmovies1.api.TmdbClient;
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

    TmdbClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Call<Movies> moviesCall = TmdbClient.getInstance().getPopular();
        Callback<Movies> moviesCallback = new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> moviesCall, Response<Movies> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Movie> movieList = response.body().getResults();
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
}
