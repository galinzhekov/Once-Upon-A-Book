package com.example.onceuponabook.models;

import com.google.gson.annotations.SerializedName;

public class BooksRated {
    @SerializedName("id")
    private int id;

    @SerializedName("book_id")
    private int book_id;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("rating")
    private String rating;

    public BooksRated(int id, int book_id, int user_id, String name, String email, String rating) {
        this.id = id;
        this.book_id = book_id;
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.rating = rating;
    }

    public int getBook_id() {
        return book_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRating() {
        return rating;
    }
}
