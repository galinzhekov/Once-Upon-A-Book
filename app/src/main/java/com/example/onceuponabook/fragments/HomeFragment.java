package com.example.onceuponabook.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.example.onceuponabook.ApiInterface;
import com.example.onceuponabook.BookActivity;
import com.example.onceuponabook.R;
import com.example.onceuponabook.adapters.HomeAdapter;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.util.ApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A fragment representing a list of Items.
 */
public class HomeFragment extends Fragment {

    RecyclerView rvPopularBooks, rvRecommendedForYou, rvNewBooks;
    Context mContext;
    private HomeAdapter adapter;
    private List<Book> mBooks, mAllBooks, mRecommendedBooks, mNewBooks;
    Call<List<Book>> call;
    private OnItemListener mOnItemListener, mBooksListener;
    GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    private static final String TAG = "Books";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecommendedBooks = new ArrayList<>();
        rvPopularBooks = view.findViewById(R.id.recyclerViewPopularBooks);
        rvRecommendedForYou = view.findViewById(R.id.recyclerViewRecommendedForYou);
        rvNewBooks = view.findViewById(R.id.recyclerViewNewBooks);

        LinearLayoutManager llmPopular = new LinearLayoutManager(mContext);
        llmPopular.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPopularBooks.setLayoutManager(llmPopular);

        LinearLayoutManager llmRecommended = new LinearLayoutManager(mContext);
        llmRecommended.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvRecommendedForYou.setLayoutManager(llmRecommended);

        LinearLayoutManager llmNewBooks = new LinearLayoutManager(mContext);
        llmNewBooks.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvNewBooks.setLayoutManager(llmNewBooks);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        call = apiInterface.getPopularBooksByMonth();

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                mBooks = response.body();
                mOnItemListener = (v, iPosition) -> {
                    Intent intent = new Intent(mContext, BookActivity.class);
                    intent.putExtra("selected_book", mBooks.get(iPosition));
                    startActivity(intent);
                };
                adapter = new HomeAdapter(mBooks, mOnItemListener);
                rvPopularBooks.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.v(TAG, "asd" + t.toString());
            }
        });

        call = apiInterface.getBooks();

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                mAllBooks = response.body();
                Log.v(TAG, "all:" + mAllBooks.toString());
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        account = GoogleSignIn.getLastSignedInAccount(mContext);

        if (account != null) {
            String email = account.getEmail();
            Log.v(TAG, "email: " + email);
            call = apiInterface.getRecommendedBooks(email);
            call.enqueue(new Callback<List<Book>>() {
                @Override
                public void onResponse(Call<List<Book>> call1, Response<List<Book>> response) {
                    mBooks = response.body();
                    boolean isInLibrary = false, isInThisCategory = false;
                    for (Book book : mAllBooks) {
                        for (Book userBook : mBooks) {
                            Log.v(TAG, "book: " + userBook.getName());
                            if (book.getId() == userBook.getId()) {
                                isInLibrary = true;

                            }
                            if (book.getCategory().contains(userBook.getCategory())) {
                                isInThisCategory = true;
                            }
                        }
                        if (!isInLibrary && isInThisCategory) {
                            mRecommendedBooks.add(book);
                        }
                        isInLibrary = false;
                        isInThisCategory = false;
                    }
                    mRecommendedBooks = mRecommendedBooks.stream().distinct().collect(Collectors.toList());

                    Log.v(TAG, "books: " + mRecommendedBooks);
                    mOnItemListener = (v, iPosition) -> {
                        Intent intent = new Intent(mContext, BookActivity.class);
                        intent.putExtra("selected_book", mRecommendedBooks.get(iPosition));
                        startActivity(intent);
                    };
                    adapter = new HomeAdapter(mRecommendedBooks, mOnItemListener);
                    rvRecommendedForYou.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<List<Book>> call1, Throwable t) {
                    Log.v(TAG, "asd" + t.toString());
                }
            });
        } else {
            String email = "";
            call = apiInterface.getRecommendedBooks(email);
            call.enqueue(new Callback<List<Book>>() {
                @Override
                public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                    mBooks = response.body();
                    mOnItemListener = (v, iPosition) -> {
                        Intent intent = new Intent(mContext, BookActivity.class);
                        intent.putExtra("selected_book", mBooks.get(iPosition));
                        startActivity(intent);
                    };
                    adapter = new HomeAdapter(mBooks, mOnItemListener);
                    rvRecommendedForYou.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<List<Book>> call, Throwable t) {
                    Log.v(TAG, "asd" + t.toString());
                }
            });
        }

        call = apiInterface.getNewBooks();

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                mNewBooks = response.body();
                mOnItemListener = (v, iPosition) -> {
                    Intent intent = new Intent(mContext, BookActivity.class);
                    intent.putExtra("selected_book", mNewBooks.get(iPosition));
                    startActivity(intent);
                };
                adapter = new HomeAdapter(mNewBooks, mOnItemListener);
                rvNewBooks.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.v(TAG, "asd" + t.toString());
            }
        });

        return view;
    }
}