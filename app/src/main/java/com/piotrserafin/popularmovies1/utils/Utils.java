package com.piotrserafin.popularmovies1.utils;

public class Utils {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";

    public static String preparePosterImagePath(String posterPath) {
        return new StringBuilder(POSTER_BASE_URL).append(POSTER_SIZE).append(posterPath).toString();
    }


}
