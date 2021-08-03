package com.example.onceuponabook;

import com.example.onceuponabook.models.Author;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.models.BooksBought;
import com.example.onceuponabook.models.BooksRated;
import com.example.onceuponabook.models.Category;
import com.example.onceuponabook.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("read_books.php")
    Call<List<Book>> getBooks();

    @POST("read_categories.php")
    Call<List<Category>> getCategories();

    @POST("read_authors.php")
    Call<List<Author>> getAuthors();

    @POST("current_month_popular_books.php")
    Call<List<Book>> getPopularBooksByMonth();

    @FormUrlEncoded
    @POST("recommended_books.php")
    Call<List<Book>> getRecommendedBooks(
            @Field("email") String email
    );

    @POST("read_new_books.php")
    Call<List<Book>> getNewBooks();

    @POST("read_books_rated.php")
    Call<List<BooksRated>> getBooksRated();

    @POST("read_books_bought.php")
    Call<List<BooksBought>> getBooksBought();

    @POST("most_bought_books.php")
    Call<List<BooksBought>> getMostBoughtBooks();

    @FormUrlEncoded
    @POST("update_books_rated.php")
    Call<List<BooksRated>> updateBooksRated(
            @Field("book_id") int book_id,
            @Field("user_id") int user_id,
            @Field("rating") double rating,
            @Field("query_action") String query_action
    );

    @POST("read_users.php")
    Call<List<User>> getUsers();

    @FormUrlEncoded
    @POST("update_wallet.php")
    Call<List<User>> updateWallet(
            @Field("wallet") double wallet,
            @Field("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("add_books_bought.php")
    Call<Void> addBooksBought(
            @Field("book_id") int book_id,
            @Field("user_id") int user_id,
            @Field("price") double price
    );

    @FormUrlEncoded
    @POST("update_users_info.php")
    Call<Void> updateUsersInfo(
            @Field("user_id") int user_id,
            @Field("wallet") double wallet,
            @Field("location") String location,
            @Field("age") int age
    );
}
