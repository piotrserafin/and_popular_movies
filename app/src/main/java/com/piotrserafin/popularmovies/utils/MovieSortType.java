package com.piotrserafin.popularmovies.utils;

import android.os.Parcel;
import android.os.Parcelable;

public enum MovieSortType implements Parcelable{
    MOST_POPULAR,
    TOP_RATED,
    FAVORITES;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieSortType> CREATOR = new Creator<MovieSortType>() {
        @Override
        public MovieSortType createFromParcel(Parcel in) {
            return MovieSortType.values()[in.readInt()];
        }

        @Override
        public MovieSortType[] newArray(int size) {
            return new MovieSortType[size];
        }
    };
}
