package com.example.onceuponabook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Book implements Parcelable {
    @SerializedName("book_id")
    private int id;

    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    @SerializedName("author")
    private String author;

    @SerializedName("category")
    private String category;

    @SerializedName("date_added")
    private String date_added;

    @SerializedName("price")
    private double price;

    @SerializedName("description")
    private String description;

    @SerializedName("product")
    private String product;

    public Book(int id, String image, String name, String author, String category, String date_added, double price, String description, String product) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.author = author;
        this.category = category;
        this.date_added = date_added;
        this.price = price;
        this.description = description;
        this.product = product;
    }

    protected Book(Parcel in) {
        id = in.readInt();
        image = in.readString();
        name = in.readString();
        author = in.readString();
        category = in.readString();
        date_added = in.readString();
        price = in.readDouble();
        description = in.readString();
        product = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getDate_added() {
        return date_added;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getProduct() {
        return product;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(category);
        dest.writeString(date_added);
        dest.writeDouble(price);
        dest.writeString(description);
        dest.writeString(product);
    }
}
