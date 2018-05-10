package com.piotrserafin.popularmovies1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.piotrserafin.popularmovies1.model.Movie;
import com.piotrserafin.popularmovies1.utils.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.detail_img)
    ImageView posterImageView;

    @BindView(R.id.movie_title)
    TextView movieTitleTextView;

    @BindView(R.id.release_date)
    TextView releaseDateTextView;

    @BindView(R.id.vote_average)
    TextView voteAverageTextView;

    @BindView(R.id.overview)
    TextView overviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("Movie");

        String title = movie.getTitle();
        String posterPath = movie.getPosterPath();
        String overview = movie.getOverview();
        String releaseDate = movie.getReleaseDate();
        float voteAverage = movie.getVoteAverage();

        movieTitleTextView.setText(title);
        overviewTextView.setText(overview);
        releaseDateTextView.setText(releaseDate);
        voteAverageTextView.setText(Float.toString(voteAverage) + getString(R.string.vote_average_max));


        Picasso.get()
                .load(Utils.preparePosterImagePath(posterPath))
                .placeholder(R.color.colorPrimaryDark)
                .into(posterImageView);
    }
}
