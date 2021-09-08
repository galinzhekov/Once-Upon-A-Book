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
import com.example.onceuponabook.adapters.StatisticModeratorAdapter;
import com.example.onceuponabook.adapters.StatisticRecentBooksAdapter;
import com.example.onceuponabook.adapters.StatisticStringAdapter;
import com.example.onceuponabook.fragments.MenuFragmentDialog;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Author;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.models.BooksAdded;
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
    private StatisticStringAdapter statisticStringAdapter;
    private StatisticBooksAdapter statisticBooksAdapter;
    private StatisticRecentBooksAdapter statisticRecentBooksAdapter;
    private StatisticModeratorAdapter statisticModeratorAdapter;
    private List<Book> mBooks, mSelectedBooks;
    private List<BooksBought> mBooksBought, mUniqueBooksBought;
    private List<BooksAdded> mBooksAdded, mUniqueBooksAdded;
    Call<List<Book>> callBooks, callNewBooks;
    Call<List<BooksBought>> callBoughtBooks;
    Call<List<BooksAdded>> callBooksAdded;
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


        } else if (getIntent().hasExtra("currentYearModerator")) {
            callBooksAdded = apiInterface.getModerators(
                    ApiClient.PASSWORD,
                    "most_active_moderators_for_this_year"
            );
            callBooksAdded.enqueue(new Callback<List<BooksAdded>>() {
                @Override
                public void onResponse(Call<List<BooksAdded>> call, Response<List<BooksAdded>> response) {
                    mBooksAdded = response.body();
                    mUniqueBooksAdded = new ArrayList<>();
                    if (mBooksAdded != null) {
                        for (BooksAdded booksBought : mBooksAdded) {
                            boolean is_added = false;
                            for (BooksAdded uniqueBooksBought : mUniqueBooksAdded) {
                                if (uniqueBooksBought.getUserId() == booksBought.getUserId()) {
                                    Log.v(TAG, String.valueOf(booksBought.getEmail()));
                                    is_added = true;
                                }
                            }
                            if (! is_added) {
                                Log.v(TAG, String.valueOf(1));
                                mUniqueBooksAdded.add(booksBought);
                            }
                        }
                        List<Integer> frequency, backgroundPercentage;
                        frequency = new ArrayList<>();
                        backgroundPercentage = new ArrayList<>();
                        int count = 0, size = mBooksAdded.size();
                        for (BooksAdded uniqueBooksBought : mUniqueBooksAdded) {
                            count = 0;
                            for (BooksAdded booksBought : mBooksAdded) {
                                if (booksBought.getUserId() == uniqueBooksBought.getUserId()) {
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
                        statisticModeratorAdapter = new StatisticModeratorAdapter(mUniqueBooksAdded, mOnItemListener, backgroundPercentage);
                        rvStatistic.setAdapter(statisticModeratorAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<BooksAdded>> call, Throwable t) {
                }
            });

        } else if (getIntent().hasExtra("currentMonthModerator")) {
            callBooksAdded = apiInterface.getModerators(
                    ApiClient.PASSWORD,
                    "most_active_moderators_for_this_month"
            );
            callBooksAdded.enqueue(new Callback<List<BooksAdded>>() {
                @Override
                public void onResponse(Call<List<BooksAdded>> call, Response<List<BooksAdded>> response) {
                    mBooksAdded = response.body();
                    mUniqueBooksAdded = new ArrayList<>();
                    if (mBooksAdded != null) {
                        for (BooksAdded booksBought : mBooksAdded) {
                            boolean is_added = false;
                            for (BooksAdded uniqueBooksBought : mUniqueBooksAdded) {
                                if (uniqueBooksBought.getUserId() == booksBought.getUserId()) {
                                    Log.v(TAG, String.valueOf(booksBought.getEmail()));
                                    is_added = true;
                                }
                            }
                            if (! is_added) {
                                Log.v(TAG, String.valueOf(1));
                                mUniqueBooksAdded.add(booksBought);
                            }
                        }
                        List<Integer> frequency, backgroundPercentage;
                        frequency = new ArrayList<>();
                        backgroundPercentage = new ArrayList<>();
                        int count = 0, size = mBooksAdded.size();
                        for (BooksAdded uniqueBooksBought : mUniqueBooksAdded) {
                            count = 0;
                            for (BooksAdded booksBought : mBooksAdded) {
                                if (booksBought.getUserId() == uniqueBooksBought.getUserId()) {
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
                        statisticModeratorAdapter = new StatisticModeratorAdapter(mUniqueBooksAdded, mOnItemListener, backgroundPercentage);
                        rvStatistic.setAdapter(statisticModeratorAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<BooksAdded>> call, Throwable t) {
                }
            });

        } else if (getIntent().hasExtra("added")) {
            callNewBooks = apiInterface.getBooks(
                    ApiClient.PASSWORD,
                    "read_new_books"
            );
            callNewBooks.enqueue(new Callback<List<Book>>() {
                @Override
                public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                    mBooks = response.body();
                    if (mBooks != null) {
                        List<Integer> frequency, backgroundPercentage;
                        frequency = new ArrayList<>();
                        backgroundPercentage = new ArrayList<>();
                        int count = 0;
                        count = mBooks.size();
                        for (Book uniqueBooksBought : mBooks) {
                            frequency.add(count);
                            count--;
                        }
                        int x = 100 / frequency.get(0);
                        Log.v(TAG, String.valueOf(frequency));
                        for (Integer i : frequency) {
                            int sum = x * i * 100;
                            backgroundPercentage.add(sum);
                        }
                        statisticRecentBooksAdapter = new StatisticRecentBooksAdapter(mBooks, mOnItemListener, backgroundPercentage);
                        rvStatistic.setAdapter(statisticRecentBooksAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Book>> call, Throwable t) {

                }
            });
        } else if (getIntent().hasExtra("mostBoughtCategory")) {
            callBoughtBooks = apiInterface.getBooksBought(
                    ApiClient.PASSWORD,
                    "most_bought_books"
            );
            callBoughtBooks.enqueue(new Callback<List<BooksBought>>() {
                @Override
                public void onResponse(Call<List<BooksBought>> call, Response<List<BooksBought>> response) {
                    mBooksBought = response.body();
                    mUniqueBooksBought = new ArrayList<>();
                    if (mBooksBought != null) {
                        callBooks = apiInterface.getBooks(
                                ApiClient.PASSWORD,
                                "read_book"
                        );
                        callBooks.enqueue(new Callback<List<Book>>() {
                            @Override
                            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                                mBooks = response.body();
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
                                List<String> categories= new ArrayList<>();
                                for (BooksBought uniqueBooksBought : mUniqueBooksBought) {
                                    count = 0;
                                    for (BooksBought booksBought : mBooksBought) {
                                        if (booksBought.getBook_id() == uniqueBooksBought.getBook_id()) {
                                            count++;
                                        }
                                    }
                                    frequency.add(count);
                                    for (Book oBook : mBooks) {
                                        if (uniqueBooksBought.getBook_id() == oBook.getId()) {
                                            boolean is_added = false;
                                            for (String oCategory : categories) {
                                                if (oBook.getCategory().equals(oCategory)) {
                                                    is_added = true;
                                                }
                                            }
                                            if (! is_added) {
                                                categories.add(oBook.getCategory());
                                            }
                                        }
                                    }
                                }



                                int x = 100 / frequency.get(0);
                                Log.v(TAG, String.valueOf(frequency));
                                for (Integer i : frequency) {
                                    int sum = x * i * 100;
                                    backgroundPercentage.add(sum);
                                }
                                statisticStringAdapter = new StatisticStringAdapter(categories, mOnItemListener, backgroundPercentage);
                                rvStatistic.setAdapter(statisticStringAdapter);
                            }

                            @Override
                            public void onFailure(Call<List<Book>> call, Throwable t) {

                            }
                        });

                    }
                }

                @Override
                public void onFailure(Call<List<BooksBought>> call, Throwable t) {

                }
            });
        }else if (getIntent().hasExtra("mostBoughtCategory")) {
            callBoughtBooks = apiInterface.getBooksBought(
                    ApiClient.PASSWORD,
                    "most_bought_books"
            );
            callBoughtBooks.enqueue(new Callback<List<BooksBought>>() {
                @Override
                public void onResponse(Call<List<BooksBought>> call, Response<List<BooksBought>> response) {
                    mBooksBought = response.body();
                    mUniqueBooksBought = new ArrayList<>();
                    if (mBooksBought != null) {
                        callBooks = apiInterface.getBooks(
                                ApiClient.PASSWORD,
                                "read_book"
                        );
                        callBooks.enqueue(new Callback<List<Book>>() {
                            @Override
                            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                                mBooks = response.body();
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
                                List<String> categories= new ArrayList<>();
                                for (BooksBought uniqueBooksBought : mUniqueBooksBought) {
                                    count = 0;
                                    for (BooksBought booksBought : mBooksBought) {
                                        if (booksBought.getBook_id() == uniqueBooksBought.getBook_id()) {
                                            count++;
                                        }
                                    }
                                    frequency.add(count);
                                    for (Book oBook : mBooks) {
                                        if (uniqueBooksBought.getBook_id() == oBook.getId()) {
                                            boolean is_added = false;
                                            for (String oCategory : categories) {
                                                if (oBook.getAuthor().equals(oCategory)) {
                                                    is_added = true;
                                                }
                                            }
                                            if (! is_added) {
                                                categories.add(oBook.getCategory());
                                            }
                                        }
                                    }
                                }



                                int x = 100 / frequency.get(0);
                                Log.v(TAG, String.valueOf(frequency));
                                for (Integer i : frequency) {
                                    int sum = x * i * 100;
                                    backgroundPercentage.add(sum);
                                }
                                statisticStringAdapter = new StatisticStringAdapter(categories, mOnItemListener, backgroundPercentage);
                                rvStatistic.setAdapter(statisticStringAdapter);
                            }

                            @Override
                            public void onFailure(Call<List<Book>> call, Throwable t) {

                            }
                        });

                    }
                }

                @Override
                public void onFailure(Call<List<BooksBought>> call, Throwable t) {

                }
            });
        }
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