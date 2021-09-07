package com.example.onceuponabook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onceuponabook.fragments.MenuFragmentDialog;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Author;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.models.BooksBought;
import com.example.onceuponabook.models.BooksRated;
import com.example.onceuponabook.models.Category;
import com.example.onceuponabook.models.ServerResponse;
import com.example.onceuponabook.models.User;
import com.example.onceuponabook.util.ApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookActivity extends AppCompatActivity implements View.OnClickListener {
    int RC_SIGN_IN = 0, totalNumberRatings = 0, mUserId = 0, downloads;
    float ratings = 0, avgRatings = 0;
    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "BookActivity";
    private GoogleSignInAccount account;
    MenuItem option;
    Toolbar toolbar;
    Call<List<BooksRated>> callBooksRated;
    Call<Void> callUpdateBooksRated;
    Call<List<BooksBought>> callBooksBought;
    Call<ServerResponse> callAddBooksBought;
    Call<List<User>> callUser;
    Call<ServerResponse> callUpdateWallet;
    private List<BooksRated> mBooksRated;
    private List<BooksBought> mBooksBought;
    private List<User> mUsers;
    private OnItemListener mOnItemListener;
    private ImageButton mBackArrow;
    private Category mCategory;
    private Author mAuthor;
    private Book mBook;
    private User mUser;
    private TextView tvBookAuthor, tvBookName, tvBookDescription, tvBookRating, tvBookReviews, tvBookDownloads;
    private ImageView ivBookCover;
    private RatingBar rbBookScore;
    Button btnBookBuy;
    private boolean bIsBookBought, bIsBookRated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        tvBookAuthor = findViewById(R.id.tvBookAuthor);
        tvBookName = findViewById(R.id.tvBookName);
        tvBookDescription = findViewById(R.id.tvBookDescription);
        ivBookCover = findViewById(R.id.ivBookCover);
        tvBookRating = findViewById(R.id.tvBookRating);
        tvBookReviews = findViewById(R.id.tvBookReviews);
        tvBookDownloads = findViewById(R.id.tvBookDownloads);
        btnBookBuy = findViewById(R.id.btnBookBuy);
        toolbar = findViewById(R.id.toolbar);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        rbBookScore = findViewById(R.id.rbBookScore);
        downloads = 0;

        DecimalFormat df = new DecimalFormat("#.##");
        bIsBookBought = false;
        bIsBookRated = false;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        mBackArrow.setOnClickListener(this);
        setSupportActionBar(toolbar);

        mBook = getIntent().getParcelableExtra("selected_book");
        tvBookAuthor.setText(mBook.getAuthor());
        tvBookDescription.setText(mBook.getDescription());
        tvBookName.setText(mBook.getName());
        tvBookDescription.setMovementMethod(new ScrollingMovementMethod());
        Picasso.get().load(mBook.getImage()).into(ivBookCover);
        btnBookBuy.setText("EUR " + String.valueOf(mBook.getPrice()));

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        callBooksRated = apiInterface.getBooksRated(
                ApiClient.PASSWORD,
                "read_books_rated"
        );
        callBooksRated.enqueue(new Callback<List<BooksRated>>() {
            @Override
            public void onResponse(Call<List<BooksRated>> call, Response<List<BooksRated>> response) {
                mBooksRated = response.body();
                for (BooksRated booksRated : mBooksRated) {
                    Log.v(TAG, String.valueOf(booksRated.getRating()));
                    if (mBook.getId() == booksRated.getBook_id()) {
                        totalNumberRatings++;
                        ratings += Double.parseDouble(booksRated.getRating());
                        avgRatings = ratings / totalNumberRatings;
                        tvBookRating.setText(df.format(avgRatings));
                        tvBookReviews.setText(String.valueOf(totalNumberRatings) + " reviews");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BooksRated>> call, Throwable t) {
                Log.v(TAG, "Failure: " + t.toString());
            }
        });

        if (account == null) {
            Log.v(TAG, "Rating: " + totalNumberRatings);
            btnBookBuy.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "You must be logged in in order to open the product.", Toast.LENGTH_SHORT).show());
        } else {

            callBooksBought = apiInterface.getBooksBought(
                    ApiClient.PASSWORD,
                    "read_books_bought"
            );
            callUser = apiInterface.getUsers(
                    ApiClient.PASSWORD,
                    "read_users"
            );

            callUser.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    mUsers = response.body();

                    for (User user : mUsers) {
                        if (user.getEmail().equals(account.getEmail())) {
                            mUser = user;


                            callBooksBought.enqueue(new Callback<List<BooksBought>>() {
                                @Override
                                public void onResponse(Call<List<BooksBought>> call, Response<List<BooksBought>> response) {
                                    mBooksBought = response.body();

                                    if (mBooksBought != null) {
                                        for (BooksBought booksBought : mBooksBought) {
                                            if (booksBought.getBook_id() == mBook.getId() && booksBought.getEmail().equals(account.getEmail())) {
                                                btnBookBuy.setText("Open File");
                                                bIsBookBought = true;
                                                break;
                                            }
                                        }

                                        for (BooksBought booksBought : mBooksBought) {
                                            if (booksBought.getBook_id() == mBook.getId()) {
                                                downloads++;
                                            }
                                        }
                                        tvBookDownloads.setText(String.valueOf(downloads));

                                    }

                                    Log.v(TAG, "mBooksBought " + String.valueOf(mBooksBought));
                                    if (mBooksRated != null) {
                                        for (BooksRated booksRated : mBooksRated) {
                                            if (mBook.getId() == booksRated.getBook_id() && account.getEmail().equals(booksRated.getEmail())) {
                                                rbBookScore.setRating(Float.parseFloat(booksRated.getRating()));
                                                bIsBookRated = true;
                                                break;
                                            }
                                        }
                                    }

                                    if (bIsBookBought) {
                                        if (bIsBookRated) {
                                            rbBookScore.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                                int bookID = mBook.getId(), userID = mUser.getUser_id();
                                                double dRating = 0;

                                                @Override
                                                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                                    dRating = rating;
                                                    callUpdateBooksRated = apiInterface.updateBooksRated(
                                                            ApiClient.PASSWORD,
                                                            "update_books_rated",
                                                            bookID,
                                                            userID,
                                                            dRating
                                                    );
                                                    callUpdateBooksRated.enqueue(new Callback<Void>() {
                                                        @Override
                                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                                            Log.v(TAG,String.valueOf(dRating));
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Void> call, Throwable t) {
                                                            Log.v(TAG,String.valueOf(t));
                                                        }
                                                    });
                                                    totalNumberRatings = 0;
                                                    ratings = 0;
                                                    callBooksRated = apiInterface.getBooksRated(
                                                            ApiClient.PASSWORD,
                                                            "read_books_rated"
                                                    );
                                                    callBooksRated.enqueue(new Callback<List<BooksRated>>() {
                                                        @Override
                                                        public void onResponse(Call<List<BooksRated>> call, Response<List<BooksRated>> response) {
                                                            mBooksRated = response.body();
                                                            for (BooksRated booksRated : mBooksRated) {
                                                                if (mBook.getId() == booksRated.getBook_id()) {
                                                                    totalNumberRatings++;
                                                                    double current_rating = Double.parseDouble(booksRated.getRating());
                                                                    if (mUser.getUser_id() == booksRated.getUser_id()) {
                                                                        current_rating = dRating;
                                                                    }
                                                                    ratings += current_rating;
                                                                    avgRatings = ratings / totalNumberRatings;
                                                                    tvBookRating.setText(df.format(avgRatings));
                                                                    tvBookReviews.setText(String.valueOf(totalNumberRatings) + " reviews");
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<List<BooksRated>> call, Throwable t) {
                                                            Log.v(TAG, "Failure: " + t.toString());
                                                        }
                                                    });


                                                    ratingBar.setRating(rating);
                                                }

                                            });
                                        } else {
                                            rbBookScore.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                                                callUpdateBooksRated = apiInterface.updateBooksRated(
                                                        ApiClient.PASSWORD,
                                                        "insert_books_rated",
                                                        mBook.getId(),
                                                        mUser.getUser_id(),
                                                        rating
                                                );
                                                callUpdateBooksRated.enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> call1, Response<Void> response1) {

                                                    }

                                                    @Override
                                                    public void onFailure(Call<Void> call1, Throwable t) {

                                                    }
                                                });
                                                Intent intent = new Intent(getApplicationContext(), BookActivity.class);
                                                intent.putExtra("selected_book", mBook);
                                                startActivity(intent);
                                            });
                                        }

                                        btnBookBuy.setOnClickListener(v -> {
                                            Intent intent = new Intent(getApplicationContext(), PdfActivity.class);
                                            intent.putExtra("selected_book", mBook);
                                            startActivity(intent);
                                        });
                                    } else {
                                        btnBookBuy.setOnClickListener(v -> {
                                            double difference = mUser.getWallet() - mBook.getPrice();
                                            if (difference < 0) {
                                                Toast.makeText(getApplicationContext(), "You don't have enough money", Toast.LENGTH_SHORT).show();
                                            } else {
                                                callAddBooksBought = apiInterface.addBooksBought(
                                                        ApiClient.PASSWORD,
                                                        "add_books_bought",
                                                        mBook.getId(),
                                                        mUser.getUser_id(),
                                                        mBook.getPrice()
                                                );
                                                callAddBooksBought.enqueue(new Callback<ServerResponse>() {
                                                    @Override
                                                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                                        ServerResponse serverResponse = response.body();
                                                        if (serverResponse.getMessage() != null) {
                                                            Toast.makeText(BookActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(BookActivity.this, "The book wasn't bought.", Toast.LENGTH_SHORT).show();
                                                        }
                                                        Intent intent = new Intent(getApplicationContext(), BookActivity.class);
                                                        intent.putExtra("selected_book", mBook);
                                                        startActivity(intent);
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                                                        Log.v(TAG, "Failure: " + t.toString());
                                                        Toast.makeText(BookActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                callUpdateWallet = apiInterface.updateWallet(
                                                        ApiClient.PASSWORD,
                                                        "update_wallet",
                                                        difference,
                                                        mUser.getUser_id()
                                                );
                                                callUpdateWallet.enqueue(new Callback<ServerResponse>() {
                                                    @Override
                                                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                                        ServerResponse serverResponse = response.body();
                                                        if (serverResponse.getMessage() != null) {
                                                            Toast.makeText(BookActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            Toast.makeText(BookActivity.this, "The user wasn't registered.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                                                        Log.v(TAG,String.valueOf(t));
                                                        Toast.makeText(BookActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

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

                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        option = menu.findItem(R.id.option);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(800);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(BookActivity.this, DefaultActivity.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            option.setOnMenuItemClickListener(item1 -> {
                FragmentManager fm = getSupportFragmentManager();
                MenuFragmentDialog optionMenu = MenuFragmentDialog.newInstance();
                optionMenu.show(fm, "");
                return false;
            });

            Toast.makeText(BookActivity.this, "Google ID: " + account.getId(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), BookActivity.class);
            intent.putExtra("selected_book", mBook);
            startActivity(intent);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(BookActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.option: {
                if (account == null) {
                    signIn();
                } else {
                    FragmentManager fm = getSupportFragmentManager();
                    MenuFragmentDialog optionMenu = MenuFragmentDialog.newInstance();
                    optionMenu.show(fm, "");
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}