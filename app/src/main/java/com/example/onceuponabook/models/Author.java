package com.example.onceuponabook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Author implements Parcelable {
    @SerializedName("author_id")
    private int id;

    @SerializedName("author")
    private String author;

    protected Author(Parcel in) {
        id = in.readInt();
        author = in.readString();
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(author);
    }

    public Author(int id, String author) {
        this.id = id;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }
}
