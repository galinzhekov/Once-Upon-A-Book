package com.example.onceuponabook.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onceuponabook.ApiInterface;
import com.example.onceuponabook.DefaultActivity;
import com.example.onceuponabook.R;
import com.example.onceuponabook.adapters.AuthorAdapter;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Author;
import com.example.onceuponabook.util.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorsFragment extends Fragment implements OnItemListener {

    private Context mContext;
    private RecyclerView rvAuthors;
    Call<List<Author>> call;
    private OnItemListener mOnItemListener;
    private AuthorAdapter adapter;
    private List<Author> mAuthors;
    private static final String TAG = "Authors";

    public AuthorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authors, container, false);
        mOnItemListener = this;
        rvAuthors = view.findViewById(R.id.rvAuthors);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAuthors.setLayoutManager(linearLayoutManager);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        call = apiInterface.getAuthors();


        call.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {

                mAuthors = response.body();
                adapter = new AuthorAdapter(mAuthors, mOnItemListener);
                rvAuthors.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Log.v(TAG, "asd" + t.toString());
            }
        });
        return view;
    }

    @Override
    public void onItemClick(View v, int iPosition) {
        Intent intent = new Intent(mContext, DefaultActivity.class);
        intent.putExtra("selected_author", mAuthors.get(iPosition));
        startActivity(intent);
    }
}