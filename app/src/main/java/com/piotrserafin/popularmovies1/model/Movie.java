package com.piotrserafin.popularmovies1.model;

/**
 * Created by pserafin on 21.03.2018.
 */

public class Movie {
    private int id;
    private String posterThumbnail;
    private String overview;
    private String rating;
    private String releaseDate;

    public int getId() {
        return id;
    }

    public String getPosterThumbnail() {
        return posterThumbnail;
    }

    public String getOverview() {
        return overview;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
