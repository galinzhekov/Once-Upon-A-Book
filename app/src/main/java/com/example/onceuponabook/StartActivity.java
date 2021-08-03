package com.example.onceuponabook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onceuponabook.adapters.ViewPagerAdapter;
import com.example.onceuponabook.fragments.AuthorsFragment;
import com.example.onceuponabook.fragments.BrowseAllFragment;
import com.example.onceuponabook.fragments.CategoriesFragment;
import com.example.onceuponabook.fragments.HomeFragment;
import com.example.onceuponabook.fragments.MenuFragmentDialog;
import com.example.onceuponabook.fragments.StudentFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class StartActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MyActivity";
    private GoogleSignInAccount account;
    MenuItem option;
    private ImageButton mCheck, mBackArrow;
    Toolbar toolbar;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        Log.d(TAG, String.valueOf(account));

        //< get elements >
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        //</ get elements >

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);
        viewPager2.setUserInputEnabled(false);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mBackArrow.setVisibility(View.GONE);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        option = menu.findItem(R.id.option);
        TextView welcomeText = findViewById(R.id.welcomeText);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(StartActivity.this, DefaultActivity.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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

            Toast.makeText(StartActivity.this, "Google ID: " + account.getId(), Toast.LENGTH_LONG).show();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(StartActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option) {
            if (account == null) {
                signIn();
            } else {
                FragmentManager fm = getSupportFragmentManager();
                MenuFragmentDialog optionMenu = MenuFragmentDialog.newInstance();
                optionMenu.show(fm, "");
            }
        }
        return super.onOptionsItemSelected(item);
    }
}