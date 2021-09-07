package com.example.onceuponabook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.onceuponabook.adapters.HomeAdapter;
import com.example.onceuponabook.fragments.MenuFragmentDialog;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Author;
import com.example.onceuponabook.models.Book;
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

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity implements OnItemListener, View.OnClickListener {
    int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "AccountActivity";
    private GoogleSignInAccount account;
    MenuItem option;
    Toolbar toolbar;
    private List<User> mBooks, mSelectedBooks;
    Call<ServerResponse> call;
    private User mUser;
    private ImageButton mBackArrow;
    private EditText etAccountLocation, etAccountAge, etAccountWallet;
    private Button btnAccountUpdate;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        etAccountLocation = findViewById(R.id.etAccountLocation);
        etAccountAge = findViewById(R.id.etAccountAge);
        etAccountWallet = findViewById(R.id.etAccountWallet);
        btnAccountUpdate = findViewById(R.id.btnAccountUpdate);
        toolbar = findViewById(R.id.toolbar);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mBackArrow.setOnClickListener(this);
        setSupportActionBar(toolbar);


        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);


        mUser = getIntent().getParcelableExtra("selected_user");
        Log.v(TAG, String.valueOf(mUser.getAge()));
        Log.v(TAG, String.valueOf(mUser.getWallet()));
        Log.v(TAG, String.valueOf(mUser.getLocation()));

        etAccountAge.setText(String.valueOf(mUser.getAge()));
        etAccountLocation.setText(mUser.getLocation());
        etAccountWallet.setText(String.valueOf(mUser.getWallet()));
        btnAccountUpdate.setOnClickListener(this);
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
                Intent intent = new Intent(AccountActivity.this, DefaultActivity.class);
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

            Toast.makeText(AccountActivity.this, "Google ID: " + account.getId(), Toast.LENGTH_LONG).show();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(AccountActivity.this, "Failed", Toast.LENGTH_LONG).show();
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
            case R.id.btnAccountUpdate: {
                call = apiInterface.updateUsersInfo(
                        ApiClient.PASSWORD,
                        "update_user_info",
                        mUser.getUser_id(),
                        Double.parseDouble(etAccountWallet.getText().toString()),
                        etAccountLocation.getText().toString(),
                        Integer.parseInt(etAccountAge.getText().toString())
                );
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {

                    }
                });
                Toast.makeText(getApplicationContext(),"Updated Info", Toast.LENGTH_SHORT).show();
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