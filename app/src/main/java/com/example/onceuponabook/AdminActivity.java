package com.example.onceuponabook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AdminActivity";
    Toolbar toolbar;
    private ImageButton mBackArrow;
    private TextView tvAdminAdded, tvAdminBought, tvAdminCurrentMonthModerator, tvAdminCurrentYearModerator, tvAdminMostBoughtCategory, tvAdminMostReadAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        toolbar = findViewById(R.id.toolbar);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        tvAdminAdded = findViewById(R.id.tvAdminAdded);
        tvAdminBought = findViewById(R.id.tvAdminBought);
        tvAdminCurrentMonthModerator = findViewById(R.id.tvAdminCurrentMonthModerator);
        tvAdminCurrentYearModerator = findViewById(R.id.tvAdminCurrentYearModerator);
        tvAdminMostBoughtCategory = findViewById(R.id.tvAdminMostBoughtCategory);
        tvAdminMostReadAuthor = findViewById(R.id.tvAdminMostReadAuthor);
        mBackArrow.setOnClickListener(this);
        tvAdminAdded.setOnClickListener(this);
        tvAdminBought.setOnClickListener(this);
        tvAdminCurrentMonthModerator.setOnClickListener(this);
        tvAdminCurrentYearModerator.setOnClickListener(this);
        tvAdminMostBoughtCategory.setOnClickListener(this);
        tvAdminMostReadAuthor.setOnClickListener(this);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back_arrow: {
                finish();
                break;
            }
            case R.id.tvAdminAdded: {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.putExtra("added", "a");
                startActivity(intent);
                break;
            }
            case R.id.tvAdminBought: {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.putExtra("bought", "a");
                startActivity(intent);
                break;
            }
            case R.id.tvAdminCurrentMonthModerator: {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.putExtra("currentMonthModerator", "a");
                startActivity(intent);
                break;
            }
            case R.id.tvAdminCurrentYearModerator: {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.putExtra("currentYearModerator", "a");
                startActivity(intent);
                break;
            }
            case R.id.tvAdminMostBoughtCategory: {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.putExtra("mostBoughtCategory", "a");
                startActivity(intent);
                break;
            }
            case R.id.tvAdminMostReadAuthor: {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.putExtra("mostReadAuthor", "a");
                startActivity(intent);
                break;
            }
        }
    }
}