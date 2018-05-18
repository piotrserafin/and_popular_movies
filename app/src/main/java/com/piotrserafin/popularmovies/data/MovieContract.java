package com.piotrserafin.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.piotrserafin.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     *     content://com.piotrserafin.popularmovies/movie/
     *     [           BASE_CONTENT_URI           ][ PATH_MOVIE ]
     */

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID= "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_VOTE_AVG = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] MOVIE_COLUMNS_PROJECTION = {
                COLUMN_MOVIE_ID,
                COLUMN_TITLE,
                COLUMN_POSTER_PATH,
                COLUMN_BACKDROP_PATH,
                COLUMN_VOTE_AVG,
                COLUMN_RELEASE_DATE,
                COLUMN_OVERVIEW
        };

        public static final int INDEX_MOVIE_ID = 0;
        public static final int INDEX_TITLE = 1;
        public static final int INDEX_POSTER_PATH = 2;
        public static final int INDEX_BACKDROP_PATH = 3;
        public static final int INDEX_VOTE_AVG = 4;
        public static final int INDEX_RELEASE_DATE = 5;
        public static final int INDEX_OVERVIEW = 6;
    }
}
