package com.example.onceuponabook;

import com.example.onceuponabook.models.Author;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.models.BooksAdded;
import com.example.onceuponabook.models.BooksBought;
import com.example.onceuponabook.models.BooksRated;
import com.example.onceuponabook.models.Category;
import com.example.onceuponabook.models.ServerResponse;
import com.example.onceuponabook.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("book.php")
    Call<List<Book>> getBooks(
            @Field("access") String password,
            @Field("selected_method") String classMethod
    );

    @POST("read_categories.php")
    Call<List<Category>> getCategories();

    @POST("read_authors.php")
    Call<List<Author>> getAuthors();

    @FormUrlEncoded
    @POST("book.php")
    Call<List<Book>> getPopularBooksByMonth(
            @Field("access") String password,
            @Field("selected_method") String classMethod
    );

    @FormUrlEncoded
    @POST("book.php")
    Call<List<Book>> getRecommendedBooks(
            @Field("access") String password,
            @Field("selected_method") String classMethod,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("book.php")
    Call<List<Book>> getNewBooks(
            @Field("access") String password,
            @Field("selected_method") String classMethod
    );

    @FormUrlEncoded
    @POST("read_books_rated.php")
    Call<List<BooksRated>> getBooksRated(
            @Field("access") String password,
            @Field("selected_method") String classMethod
    );

    @FormUrlEncoded
    @POST("books_bought.php")
    Call<List<BooksBought>> getBooksBought(
            @Field("access") String password,
            @Field("selected_method") String classMethod
    );

    @FormUrlEncoded
    @POST("bought_books.php")
    Call<List<BooksBought>> getMostBoughtBooks(
            @Field("access") String password,
            @Field("selected_method") String classMethod
    );

    @FormUrlEncoded
    @POST("books_rated.php")
    Call<Void> updateBooksRated(
            @Field("access") String password,
            @Field("selected_method") String classMethod,
            @Field("book_id") int book_id,
            @Field("user_id") int user_id,
            @Field("rating") double rating
    );

    @FormUrlEncoded
    @POST("user.php")
    Call<List<User>> getUsers(
            @Field("access") String password,
            @Field("selected_method") String classMethod
    );

    @FormUrlEncoded
    @POST("user.php")
    Call<List<BooksAdded>> getModerators(
            @Field("access") String password,
            @Field("selected_method") String classMethod
    );

    @FormUrlEncoded
    @POST("user.php")
    Call<ServerResponse> addNewUser(
            @Field("access") String password,
            @Field("selected_method") String classMethod,
            @Field("email") String email,
            @Field("name") String name
    );

    @FormUrlEncoded
    @POST("user.php")
    Call<ServerResponse> updateWallet(
            @Field("access") String password,
            @Field("selected_method") String classMethod,
            @Field("wallet") double wallet,
            @Field("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("books_bought.php")
    Call<ServerResponse> addBooksBought(
            @Field("access") String password,
            @Field("selected_method") String classMethod,
            @Field("book_id") int book_id,
            @Field("user_id") int user_id,
            @Field("price") double price
    );

    @FormUrlEncoded
    @POST("book.php")
    Call<ServerResponse> updateUsersInfo(
            @Field("access") String password,
            @Field("selected_method") String classMethod,
            @Field("user_id") int user_id,
            @Field("wallet") double wallet,
            @Field("location") String location,
            @Field("age") int age
    );

    @FormUrlEncoded
    @POST("book.php")
    Call<ServerResponse> addBook(
            @Field("access") String password,
            @Field("selected_method") String classMethod,
            @Field("server_path") String serverPath,
            @Field("user_id") int userId,
            @Field("encoded_file") String encodedFile,
            @Field("file_name") String fileName,
            @Field("file_extension") String fileExtension,
            @Field("encoded_image") String encodedImage,
            @Field("image_name") String imageName,
            @Field("image_extension") String imageExtension,
            @Field("title") String title,
            @Field("author") String author,
            @Field("category") String category,
            @Field("price") double price,
            @Field("description") String description
    );
}
