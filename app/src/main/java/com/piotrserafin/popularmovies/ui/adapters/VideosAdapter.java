package com.piotrserafin.popularmovies.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piotrserafin.popularmovies.R;
import com.piotrserafin.popularmovies.model.Video;

import java.util.ArrayList;
import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {

    private final Context context;
    final private VideosAdapterOnClickHandler clickHandler;
    private ArrayList<Video> results;

    public interface VideosAdapterOnClickHandler {
        void onClick(Video video);
    }

    public VideosAdapter(Context context, VideosAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public VideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
        return new VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapterViewHolder holder, int position) {
        holder.videoType.setText(results.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return (results == null) ? 0 : results.size();
    }

    public void setVideosList(List<Video> results) {
        this.results = new ArrayList<>();
        this.results.addAll(results);
        notifyDataSetChanged();
    }

    public ArrayList<Video> getResults() {
        return results;
    }

    class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView videoType;

        VideosAdapterViewHolder(View view) {
            super(view);

            videoType = view.findViewById(R.id.video_type);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Video video = results.get(position);
            clickHandler.onClick(video);
        }
    }
}
