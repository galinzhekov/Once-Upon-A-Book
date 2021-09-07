package com.example.onceuponabook.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.onceuponabook.AccountActivity;
import com.example.onceuponabook.AdminActivity;
import com.example.onceuponabook.ApiInterface;
import com.example.onceuponabook.DefaultActivity;
import com.example.onceuponabook.R;
import com.example.onceuponabook.StartActivity;
import com.example.onceuponabook.UploadBookActivity;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.models.BooksBought;
import com.example.onceuponabook.models.User;
import com.example.onceuponabook.util.ApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragmentDialog extends DialogFragment implements View.OnClickListener {

    private TextView tvLogOut, tvWelcomeText, tvAdminPanel, tvManageAccount, tvHomePage, tvLibrary, upload_file;
    GoogleSignInClient mGoogleSignInClient;
    Context mContext;
    Call<List<User>> callUsers;
    Call<List<Book>> callBooks;
    Call<List<BooksBought>> callBooksBought;
    List<User> users;
    List<Book> mBooks, mSelectedBooks;
    List<BooksBought> mBooksBought;
    User mUser;
    ApiInterface apiInterface;
    private static final String TAG = "DialogFragment";

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GALLERY = 200;
    private long downloadId;

    TextView file_name;
    String file_path=null;
    Button upload;

    public MenuFragmentDialog() {
        // Required empty public constructor
    }

    public static MenuFragmentDialog newInstance() {
        return new MenuFragmentDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        tvWelcomeText = view.findViewById(R.id.welcomeText);
        tvLogOut = view.findViewById(R.id.logOut);
        tvAdminPanel = view.findViewById(R.id.tvAdminPanel);
        tvAdminPanel.setVisibility(View.GONE);
        tvManageAccount = view.findViewById(R.id.manageAccount);
        tvHomePage = view.findViewById(R.id.homePage);
        tvLibrary = view.findViewById(R.id.library);
        upload_file = view.findViewById(R.id.tvUploadFile);
        upload_file.setVisibility(View.GONE);

        mSelectedBooks = new ArrayList<>();

        if(account != null) {
            tvWelcomeText.setText("Hello, " + account.getGivenName() + "!");

            apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

            callUsers = apiInterface.getUsers(
                    ApiClient.PASSWORD,
                    "read_users"
            );
            callUsers.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    users = response.body();
                    if (users != null) {
                        for (User user : users) {
                            if (user.getEmail().equals(account.getEmail())) {
                                mUser = user;
                                break;
                            }
                        }

                        if (mUser.getPosition_id() == 1) {
                            tvAdminPanel.setVisibility(View.VISIBLE);
                        }
                        if (mUser.getPosition_id() == 1 || mUser.getPosition_id() == 3) {
                            upload_file.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Log.v(TAG, String.valueOf(t));
                }
            });
        }

        tvLogOut.setOnClickListener(this);
        tvManageAccount.setOnClickListener(this);
        tvHomePage.setOnClickListener(this);
        tvLibrary.setOnClickListener(this);
        tvAdminPanel.setOnClickListener(this);
        upload_file.setOnClickListener(this);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logOut:
                signOut();
                break;
            case R.id.manageAccount:
                Intent intent = new Intent(mContext, AccountActivity.class);
                Log.v(TAG, String.valueOf(mUser));
                intent.putExtra("selected_user", mUser);
                startActivity(intent);
                break;
            case R.id.homePage:
                dismiss();
                Intent intent1 = new Intent(mContext, StartActivity.class);
                startActivity(intent1);
                break;
            case R.id.library:
                dismiss();
                Intent intent2 = new Intent(mContext, DefaultActivity.class);
                intent2.putExtra("selected_user", mUser);
                startActivity(intent2);
                break;
            case R.id.tvAdminPanel:
                dismiss();
                Intent intent3 = new Intent(mContext, AdminActivity.class);
                startActivity(intent3);
                break;
            case R.id.tvUploadFile:
                dismiss();
                Intent intent4 = new Intent(mContext, UploadBookActivity.class);
                intent4.putExtra("userId", mUser.getUser_id());
                startActivity(intent4);
                break;
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
            startActivity(new Intent(mContext, StartActivity.class));
        });
    }
}
