package com.piotrserafin.popularmovies1.utils;

public class Utils {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";

    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    public static String preparePosterImagePath(String posterPath) {
        return new StringBuilder(POSTER_BASE_URL).append(POSTER_SIZE).append(posterPath).toString();
    }

    public static String prepareYoutubeUrl(String videoKey) {
        return new StringBuilder(YOUTUBE_BASE_URL).append(videoKey).toString();
    }
}
