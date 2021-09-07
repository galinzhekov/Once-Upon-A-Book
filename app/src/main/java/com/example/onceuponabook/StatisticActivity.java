package com.example.onceuponabook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.onceuponabook.adapters.HomeAdapter;
import com.example.onceuponabook.adapters.StatisticBooksAdapter;
import com.example.onceuponabook.fragments.MenuFragmentDialog;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Author;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.models.BooksBought;
import com.example.onceuponabook.models.Category;
import com.example.onceuponabook.models.User;
import com.example.onceuponabook.util.ApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticActivity extends AppCompatActivity implements OnItemListener, View.OnClickListener {
    private static final String TAG = "StatisticActivity";
    Toolbar toolbar;
    RecyclerView rvStatistic;
    private HomeAdapter adapter;
    private StatisticBooksAdapter statisticBooksAdapter;
    private List<Book> mBooks, mSelectedBooks;
    private List<BooksBought> mBooksBought, mUniqueBooksBought;
    Call<List<Book>> callBooks;
    Call<List<BooksBought>> callBoughtBooks;
    private OnItemListener mOnItemListener, mOTL2;
    private ImageButton mBackArrow;
    private Category mCategory;
    private Author mAuthor;
    User mUser;
    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        toolbar = findViewById(R.id.toolbar);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mBackArrow.setOnClickListener(this);
        setSupportActionBar(toolbar);

        mOnItemListener = this;
        rvStatistic = findViewById(R.id.rvStatistic);
        Log.v(TAG,"here");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvStatistic.setLayoutManager(linearLayoutManager);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        callBooks = apiInterface.getBooks(
                ApiClient.PASSWORD,
                "read_book"
        );
        callBooks.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {

            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {

            }
        });

        if (getIntent().hasExtra("bought")) {
            callBoughtBooks = apiInterface.getBooksBought(
                    ApiClient.PASSWORD,
                    "read_books_bought"
            );
            callBoughtBooks.enqueue(new Callback<List<BooksBought>>() {
                @Override
                public void onResponse(Call<List<BooksBought>> call, Response<List<BooksBought>> response) {
                    mBooksBought = response.body();
                    mUniqueBooksBought = new ArrayList<>();
                    if (mBooksBought != null) {
                        for (BooksBought booksBought : mBooksBought) {
                            boolean is_added = false;
                            for (BooksBought uniqueBooksBought : mUniqueBooksBought) {
                                if (uniqueBooksBought.getBook_id() == booksBought.getBook_id()) {
                                    is_added = true;
                                }
                            }
                            if (! is_added) {
                                mUniqueBooksBought.add(booksBought);
                            }
                        }
                        List<Integer> frequency, backgroundPercentage;
                        frequency = new ArrayList<>();
                        backgroundPercentage = new ArrayList<>();
                        int count = 0, size = mBooksBought.size();
                        for (BooksBought uniqueBooksBought : mUniqueBooksBought) {
                            count = 0;
                            for (BooksBought booksBought : mBooksBought) {
                                if (booksBought.getBook_id() == uniqueBooksBought.getBook_id()) {
                                    count++;
                                }
                            }
                            frequency.add(count);
                        }
                        int x = 100 / frequency.get(0);
                        Log.v(TAG, String.valueOf(frequency));
                        for (Integer i : frequency) {
                            int sum = x * i * 100;
                            backgroundPercentage.add(sum);
                        }
                        statisticBooksAdapter = new StatisticBooksAdapter(mUniqueBooksBought, mOnItemListener, backgroundPercentage);
                        rvStatistic.setAdapter(statisticBooksAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<BooksBought>> call, Throwable t) {

                }
            });

//            callBooksBought.enqueue(new Callback<List<BooksBought>>() {
//                @Override
//                public void onResponse(Call<List<BooksBought>> call, Response<List<BooksBought>> response) {
//                    mBooksBought = response.body();
//                    mUniqueBooksBought = mBooksBought.stream().distinct().collect(Collectors.toList());
//                    List<Integer> frequency, backgroundPercentage;
//                    frequency = new ArrayList<>();
//                    backgroundPercentage = new ArrayList<>();
//                    int count = 0, size = mBooksBought.size();
//                    for (BooksBought uniqueBooksBought : mUniqueBooksBought) {
//                        count = 0;
//                        for (BooksBought booksBought : mBooksBought) {
//                            if (booksBought.getBook_id() == uniqueBooksBought.getBook_id()) {
//                                count++;
//                            } else {
//                                frequency.add(count);
//                            }
//                        }
//                    }
//                    int x = 100 / frequency.get(0);
//                    for (Integer i : frequency) {
//                        int sum = x * i * 100;
//                        backgroundPercentage.add(sum);
//                    }
//                    statisticBooksAdapter = new StatisticBooksAdapter(mUniqueBooksBought, mOnItemListener, backgroundPercentage);
//                    rvStatistic.setAdapter(statisticBooksAdapter);
//                }
//
//                @Override
//                public void onFailure(Call<List<BooksBought>> call, Throwable t) {
//
//                }
//            });



        }
//        else {
//            call = apiInterface.getBooks();
//            call.enqueue(new Callback<List<Book>>() {
//                @Override
//                public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
//
//                    mBooks = response.body();
//                    if (getIntent().hasExtra("selected_category")) {
//                        mCategory = getIntent().getParcelableExtra("selected_category");
//                        mSelectedBooks = mBooks.stream()
//                                .filter(item -> item.getCategory()
//                                        .equals(mCategory.getCategory()))
//                                .collect(Collectors.toList());
//                        adapter = new HomeAdapter(mSelectedBooks, mOnItemListener);
//                    } else if (getIntent().hasExtra("selected_author")) {
//                        mAuthor = getIntent().getParcelableExtra("selected_author");
//                        mSelectedBooks = mBooks.stream()
//                                .filter(item -> item.getAuthor()
//                                        .equals(mAuthor.getAuthor()))
//                                .collect(Collectors.toList());
//                        adapter = new HomeAdapter(mSelectedBooks, mOnItemListener);
//                    } else if (getIntent().hasExtra("search_query")) {
//                        String searchElement = getIntent().getStringExtra("search_query");
//                        Log.v(TAG, "searchElement:" + searchElement);
//                        mSelectedBooks = mBooks.stream()
//                                .filter(item -> item.getName().contains(searchElement) || item.getAuthor().contains(searchElement))
//                                .collect(Collectors.toList());
//                        adapter = new HomeAdapter(mSelectedBooks, mOnItemListener);
//                    } else {
//                        Log.v(TAG, "asd" + mBooks.toString());
//                        adapter = new HomeAdapter(mBooks, mOnItemListener);
//                    }
//                    rvDefault.setAdapter(adapter);
//
//                }
//
//                @Override
//                public void onFailure(Call<List<Book>> call, Throwable t) {
//                    Log.v(TAG, "asd" + t.toString());
//                }
//            });
//        }
    }



    @Override
    public void onItemClick(View v, int iPosition) {
//        Intent intent = new Intent(this, BookActivity.class);
//        intent.putExtra("selected_book", mBooks.get(iPosition));
//        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back_arrow: {
                finish();
                break;
            }
        }
    }
}