package com.piotrserafin.popularmovies1.api;

import com.piotrserafin.popularmovies1.model.Movies;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by pserafin on 21.03.2018.
 */

public class TmdbClient {

    private final static String BASE_URL = "http://api.themoviedb.org/";

    private static final String API_KEY = "[TMDB API KEY HERE!!!]";

    private static TmdbClient instance = null;

    private Retrofit retrofit;
    private TmdbClient.Api tmdbApi;

    public interface Api {
        @GET("/3/discover/movie")
        Call<Movies> getMovies(@Query("api_key") String apiKey);
    }

    private TmdbClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        tmdbApi = retrofit.create(TmdbClient.Api.class);
    }

    public static TmdbClient getInstance() {
        if (instance == null) {
            instance = new TmdbClient();
        }
        return instance;
    }

    public Call<Movies> fetchMovies() {
        return tmdbApi.getMovies(API_KEY);
    }

}
