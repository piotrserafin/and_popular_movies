package com.piotrserafin.popularmovies1.api;

import com.piotrserafin.popularmovies1.BuildConfig;
import com.piotrserafin.popularmovies1.model.Movies;
import com.piotrserafin.popularmovies1.model.Reviews;
import com.piotrserafin.popularmovies1.model.Videos;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

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
        Call<Movies> getPopular();

        @GET("/3/movie/top_rated")
        Call<Movies> getTopRated();

        @GET("/3/movie/{movie_id}/videos")
        Call<Videos> getVideos(@Path("movie_id") long movieId);

        @GET("/3/movie/{movie_id}/reviews")
        Call<Reviews> getReviews(@Path("movie_id") long movieId);
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

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.addInterceptor(chain -> {
            Request interceptedRequest = chain.request();
            HttpUrl url = interceptedRequest.url();

            HttpUrl modifiedUrl = url.newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build();

            Request request = interceptedRequest
                    .newBuilder()
                    .url(modifiedUrl)
                    .build();

            return chain.proceed(request);
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build();

        api = retrofit.create(TmdbClient.Api.class);
    }

    public static TmdbClient getInstance() {
        if (instance == null) {
            instance = new TmdbClient();
        }
        return instance;
    }

    public Call<Videos> getVideos(long movieId) { return api.getVideos(movieId); }

    public Call<Reviews> getReviews(long movieId) {
        return api.getReviews(movieId);
    }

    public Call<Movies> fetch(Strategy strategy) {
        return strategy.execute();
    }

    private Call<Movies> getPopular() {
        return api.getPopular();
    }

    private Call<Movies> getTopRated() {
        return api.getTopRated();
    }
}
