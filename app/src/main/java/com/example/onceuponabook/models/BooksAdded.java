package com.example.onceuponabook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class BooksAdded implements Parcelable {
    @SerializedName("books_added_id")
    private int id;

    @SerializedName("books_id")
    private int booksId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("email")
    private String email;

    @SerializedName("date_added")
    private String date;


    protected BooksAdded(Parcel in) {
        id = in.readInt();
        booksId = in.readInt();
        userId = in.readInt();
        email = in.readString();
        date = in.readString();
    }

    public static final Creator<BooksAdded> CREATOR = new Creator<BooksAdded>() {
        @Override
        public BooksAdded createFromParcel(Parcel in) {
            return new BooksAdded(in);
        }

        @Override
        public BooksAdded[] newArray(int size) {
            return new BooksAdded[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(booksId);
        dest.writeInt(userId);
        dest.writeString(email);
        dest.writeString(date);
    }

    public int getId() {
        return id;
    }

    public int getBooksId() {
        return booksId;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }
}
