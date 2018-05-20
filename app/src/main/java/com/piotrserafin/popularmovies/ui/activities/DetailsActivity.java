package com.piotrserafin.popularmovies.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.piotrserafin.popularmovies.R;
import com.piotrserafin.popularmovies.api.TmdbClient;
import com.piotrserafin.popularmovies.model.Movie;
import com.piotrserafin.popularmovies.model.Review;
import com.piotrserafin.popularmovies.model.Reviews;
import com.piotrserafin.popularmovies.model.Video;
import com.piotrserafin.popularmovies.model.Videos;
import com.piotrserafin.popularmovies.ui.adapters.ReviewsAdapter;
import com.piotrserafin.popularmovies.ui.adapters.VideosAdapter;
import com.piotrserafin.popularmovies.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity
        implements VideosAdapter.VideosAdapterOnClickHandler {

    public static final String TAG = DetailsActivity.class.getSimpleName();

    private Movie movie;

    @BindView(R.id.backdrop_img)
    ImageView backdropImageView;

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

    @BindView(R.id.videos_divider)
    View videosDivider;

    @BindView(R.id.videos_label)
    TextView videosLabelTextView;

    @BindView(R.id.videos_list)
    RecyclerView videosRecyclerView;

    @BindView(R.id.reviews_divider)
    View reviewsDivider;

    @BindView(R.id.reviews_label)
    TextView reviewsLabelTextView;

    @BindView(R.id.reviews_list)
    RecyclerView reviewsRecyclerView;

    @BindView(R.id.details_toolbar)
    Toolbar toolbar;

    private VideosAdapter videosAdapter;
    private ReviewsAdapter reviewsAdapter;

    private FavioriteAsyncTask favioriteAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.details_acitvity_name);

        Intent intent = getIntent();
        Movie parcelableMovie = intent.getParcelableExtra("Movie");

        movie = new Movie(
                parcelableMovie.getId(),
                parcelableMovie.getTitle(),
                parcelableMovie.getPosterPath(),
                parcelableMovie.getBackdropPath(),
                parcelableMovie.getVoteAverage(),
                parcelableMovie.getReleaseDate(),
                parcelableMovie.getOverview());

        favioriteAsyncTask = new FavioriteAsyncTask();
        favioriteAsyncTask.setListener(() -> Log.d(TAG, "AsyncTaskFinished()"));
        favioriteAsyncTask.execute();

        populateUi();
        createVideosList();
        createReviewsList();

        //TODO: Good place to use RxJava to chain multiple Retrofit requests
        fetchVideos();
        fetchReviews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_favorite: {
                toogleFavorite();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void toogleFavorite() {
    }

    private void populateUi() {

        videosLabelTextView.setVisibility(View.INVISIBLE);
        videosDivider.setVisibility(View.INVISIBLE);

        reviewsDivider.setVisibility(View.INVISIBLE);
        reviewsLabelTextView.setVisibility(View.INVISIBLE);

        movieTitleTextView.setText(movie.getTitle());
        overviewTextView.setText(movie.getOverview());
        releaseDateTextView.setText(movie.getReleaseDate());

        String voteAvg = String.format(Locale.US,
                "%f/%s",
                movie.getVoteAverage(),
                getString(R.string.vote_average_max));

        voteAverageTextView.setText(voteAvg);

        Picasso.get()
                .load(Utils.prepareBackdropImagePath(movie.getBackdropPath()))
                .into(backdropImageView);

        Picasso.get()
                .load(Utils.preparePosterImagePath(movie.getPosterPath()))
                .into(posterImageView);
    }

    private void createVideosList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());

        videosRecyclerView.setLayoutManager(linearLayoutManager);
        videosRecyclerView.addItemDecoration(itemDecoration);

        videosAdapter = new VideosAdapter(this, this);
        videosRecyclerView.setAdapter(videosAdapter);
    }

    private void createReviewsList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());

        reviewsRecyclerView.setLayoutManager(linearLayoutManager);
        reviewsRecyclerView.addItemDecoration(itemDecoration);

        reviewsAdapter = new ReviewsAdapter(this);
        reviewsRecyclerView.setAdapter(reviewsAdapter);
    }

    private void fetchVideos() {

        Call<Videos> videosCall = TmdbClient.getInstance().getVideos(movie.getId());
        Callback<Videos> videosCallback = new Callback<Videos>() {
            @Override
            public void onResponse(Call<Videos> videosCall, Response<Videos> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Video> videos = response.body().getResults();

                if(videos.isEmpty()) {
                    return;
                }

                videosDivider.setVisibility(View.VISIBLE);
                videosLabelTextView.setVisibility(View.VISIBLE);
                videosAdapter.setVideosList(videos);
            }

            @Override
            public void onFailure(Call<Videos> videosCall, Throwable t) {
            }
        };
        videosCall.enqueue(videosCallback);
    }

    private void fetchReviews() {

        Call<Reviews> reviewsCall = TmdbClient.getInstance().getReviews(movie.getId());
        Callback<Reviews> reviewsCallback = new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> reviewsCall, Response<Reviews> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Review> reviews = response.body().getResults();

                if(reviews.isEmpty()) {
                    return;
                }

                reviewsDivider.setVisibility(View.VISIBLE);
                reviewsLabelTextView.setVisibility(View.VISIBLE);
                reviewsAdapter.setReviewsList(reviews);
            }

            @Override
            public void onFailure(Call<Reviews> reviewsCall, Throwable t) {
            }
        };
        reviewsCall.enqueue(reviewsCallback);
    }

    @Override
    public void onClick(Video video) {
        playVideo(video.getKey());
    }

    public void playVideo(String key){

        Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));

        // If there is no YT app, start in browser
        if (youTubeIntent.resolveActivity(getPackageManager()) == null) {
            youTubeIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Utils.prepareYoutubeUrl(key)));
        }
        startActivity(youTubeIntent);
    }



    static class FavioriteAsyncTask extends AsyncTask<Void, Void, Void> {
        private FavoriteAsyncTaskListener listener;

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener != null) {
                listener.onFavoriteAsyncTaskFinished();
            }
        }

        public void setListener(FavoriteAsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface FavoriteAsyncTaskListener {
            void onFavoriteAsyncTaskFinished();
        }
    }
}
