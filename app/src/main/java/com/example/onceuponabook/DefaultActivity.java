package com.example.onceuponabook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onceuponabook.adapters.HomeAdapter;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefaultActivity extends AppCompatActivity implements OnItemListener, View.OnClickListener {

    int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MyActivity";
    private GoogleSignInAccount account;
    MenuItem option;
    Toolbar toolbar;
    RecyclerView rvDefault;
    private HomeAdapter adapter;
    private List<Book> mBooks, mSelectedBooks;
    Call<List<Book>> call, callBooks;
    private OnItemListener mOnItemListener, mOTL2;
    private ImageButton mBackArrow;
    private Category mCategory;
    private Author mAuthor;
    List<BooksBought> mBooksBought;
    User mUser;
    ApiInterface apiInterface;
    Call<List<BooksBought>> callBooksBought;
    List<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        toolbar = findViewById(R.id.toolbar);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mBackArrow.setOnClickListener(this);
        setSupportActionBar(toolbar);

        mOnItemListener = this;
        rvDefault = findViewById(R.id.rvDefault);

        rvDefault.setLayoutManager(new GridLayoutManager(this, 3));
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        if (getIntent().hasExtra("selected_user")) {
            callBooks = apiInterface.getBooks(
                    ApiClient.PASSWORD,
                    "read_book"
            );
            callBooks.enqueue(new Callback<List<Book>>() {
                @Override
                public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                    mBooks = response.body();
                    mUser = getIntent().getParcelableExtra("selected_user");
                    mSelectedBooks = new ArrayList<>();
                    callBooksBought = apiInterface.getBooksBought(
                            ApiClient.PASSWORD,
                            "read_books_bought"
                    );
                    callBooksBought.enqueue(new Callback<List<BooksBought>>() {
                        @Override
                        public void onResponse(Call<List<BooksBought>> call, Response<List<BooksBought>> response) {
                            mBooksBought = response.body();
                            if (mBooks != null && mBooksBought != null) {
                                for (Book book : mBooks) {
                                    for (BooksBought booksBought : mBooksBought) {
                                        if (booksBought.getBook_id() == book.getId() && booksBought.getUser_id() == mUser.getUser_id()) {
                                            mSelectedBooks.add(book);
                                        }
                                    }
                                    mOTL2 = (v, iPosition) -> {
                                        Intent intent = new Intent(getApplicationContext(), BookActivity.class);
                                        intent.putExtra("selected_book", mSelectedBooks.get(iPosition));
                                        startActivity(intent);
                                    };
                                }
                                adapter = new HomeAdapter(mSelectedBooks, mOTL2);
                                rvDefault.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<BooksBought>> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<List<Book>> call, Throwable t) {

                }
            });



        } else {
            call = apiInterface.getBooks(
                    ApiClient.PASSWORD,
                    "read_book"
            );
            call.enqueue(new Callback<List<Book>>() {
                @Override
                public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {

                    mBooks = response.body();
                    if (getIntent().hasExtra("selected_category")) {
                        mCategory = getIntent().getParcelableExtra("selected_category");
                        mSelectedBooks = mBooks.stream()
                                .filter(item -> item.getCategory()
                                        .equals(mCategory.getCategory()))
                                .collect(Collectors.toList());
                        adapter = new HomeAdapter(mSelectedBooks, mOnItemListener);
                    } else if (getIntent().hasExtra("selected_author")) {
                        mAuthor = getIntent().getParcelableExtra("selected_author");
                        mSelectedBooks = mBooks.stream()
                                .filter(item -> item.getAuthor()
                                        .equals(mAuthor.getAuthor()))
                                .collect(Collectors.toList());
                        adapter = new HomeAdapter(mSelectedBooks, mOnItemListener);
                    } else if (getIntent().hasExtra("search_query")) {
                        String searchElement = getIntent().getStringExtra("search_query");
                        Log.v(TAG, "searchElement:" + searchElement);
                        mSelectedBooks = mBooks.stream()
                                .filter(item -> item.getName().toLowerCase().contains(searchElement) || item.getAuthor().toLowerCase().contains(searchElement))
                                .collect(Collectors.toList());
                        adapter = new HomeAdapter(mSelectedBooks, mOnItemListener);
                    } else {
                        Log.v(TAG, "asd" + mBooks.toString());
                        adapter = new HomeAdapter(mBooks, mOnItemListener);
                    }
                    rvDefault.setAdapter(adapter);

                }

                @Override
                public void onFailure(Call<List<Book>> call, Throwable t) {
                    Log.v(TAG, "asd" + t.toString());
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
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

            Toast.makeText(DefaultActivity.this, "Google ID: " + account.getId(), Toast.LENGTH_LONG).show();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(DefaultActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onItemClick(View v, int iPosition) {
        Intent intent = new Intent(this, BookActivity.class);
        intent.putExtra("selected_book", mBooks.get(iPosition));
        startActivity(intent);
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