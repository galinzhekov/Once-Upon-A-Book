package com.example.onceuponabook.models;

import com.google.gson.annotations.SerializedName;

public class BooksBought {
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

    @SerializedName("date_bought")
    private String dateBought;

    @SerializedName("price_bought")
    private String priceBought;

    public BooksBought(int id, int book_id, int user_id, String name, String email, String dateBought, String priceBought) {
        this.id = id;
        this.book_id = book_id;
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.dateBought = dateBought;
        this.priceBought = priceBought;
    }

    public int getId() {
        return id;
    }

    public int getBook_id() {
        return book_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDateBought() {
        return dateBought;
    }

    public String getPriceBought() {
        return priceBought;
    }
}
