package com.piotrserafin.popularmovies1.api;

import com.piotrserafin.popularmovies1.model.Movies;

import retrofit2.Call;

public enum TmdbStrategy {

    MOST_POPULAR {
        @Override
        Call<Movies> execute() {
            return TmdbClient.getInstance().getPopular();
        }
    },

    TOP_RATED {
        @Override
        Call<Movies> execute() {
            return TmdbClient.getInstance().getTopRated();
        }
    };

    abstract Call<Movies> execute();
}
