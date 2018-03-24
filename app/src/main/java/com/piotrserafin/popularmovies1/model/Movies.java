package com.piotrserafin.popularmovies1.model;

import java.util.List;

/**
 * Created by pserafin on 24.03.2018.
 */

public class Movies {
    private int page;
    private List<Movie> results;
    private int total_pages;
    private long total_results;

    public int getPage() {
        return page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public long getTotal_results() {
        return total_results;
    }
}
