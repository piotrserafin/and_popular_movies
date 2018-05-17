package com.piotrserafin.popularmovies1.utils;

public class Utils {

    private static final String TMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w300";
    private static final String BACKDROP_SIZE = "w780";

    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    public static String preparePosterImagePath(String posterPath) {
        return new StringBuilder(TMDB_IMAGE_BASE_URL).append(POSTER_SIZE).append(posterPath).toString();
    }

    public static String prepareBackdropImagePath(String backdropPath) {
        return new StringBuilder(TMDB_IMAGE_BASE_URL).append(BACKDROP_SIZE).append(backdropPath).toString();
    }

    public static String prepareYoutubeUrl(String videoKey) {
        return new StringBuilder(YOUTUBE_BASE_URL).append(videoKey).toString();
    }
}
