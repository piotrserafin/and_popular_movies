package com.piotrserafin.popularmovies1.api;

import com.piotrserafin.popularmovies1.BuildConfig;
import com.piotrserafin.popularmovies1.model.Movies;
import com.piotrserafin.popularmovies1.model.Reviews;
import com.piotrserafin.popularmovies1.model.Videos;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pserafin on 21.03.2018.
 */

public class TmdbClient {

    private static final String TAG = TmdbClient.class.getSimpleName();

    private static final String BASE_URL = "http://api.themoviedb.org/";
    private static final String API_KEY = BuildConfig.API_KEY;

    private static TmdbClient instance = null;

    private TmdbClient.Api api;

    public interface Api {
        @GET("/3/movie/popular")
        Call<Movies> getPopular(@Query("api_key") String apiKey);

        @GET("/3/movie/top_rated")
        Call<Movies> getTopRated(@Query("api_key") String apiKey);

        @GET("/3/movie/{movie_id}/videos")
        Call<Videos> getVideos(@Path("movie_id") long movieId, @Query("api_key") String apiKey);

        @GET("/3/movie/{movie_id}/reviews")
        Call<Reviews> getReviews(@Path("movie_id") long movieId, @Query("api_key") String apiKey);
    }

    // Enum based strategy pattern for sort order selection
    public enum Strategy {

        MOST_POPULAR {
            @Override
            Call<Movies> execute() {
                return getInstance().getPopular();
            }
        },

        TOP_RATED {
            @Override
            Call<Movies> execute() {
                return getInstance().getTopRated();
            }
        };

        abstract Call<Movies> execute();
    }

    private TmdbClient() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        api = retrofit.create(TmdbClient.Api.class);
    }

    public static TmdbClient getInstance() {
        if (instance == null) {
            instance = new TmdbClient();
        }
        return instance;
    }

    public Call<Videos> getVideos(long movieId) { return api.getVideos(movieId, API_KEY); }

    public Call<Reviews> getReviews(long movieId) {
        return api.getReviews(movieId, API_KEY);
    }

    public Call<Movies> fetch(Strategy strategy) {
        return strategy.execute();
    }

    private Call<Movies> getPopular() {
        return api.getPopular(API_KEY);
    }

    private Call<Movies> getTopRated() {
        return api.getTopRated(API_KEY);
    }
}
