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

import com.example.onceuponabook.ApiInterface;
import com.example.onceuponabook.BookActivity;
import com.example.onceuponabook.DefaultActivity;
import com.example.onceuponabook.R;
import com.example.onceuponabook.adapters.CategoryAdapter;
import com.example.onceuponabook.adapters.HomeAdapter;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.models.Category;
import com.example.onceuponabook.util.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StudentFragment extends Fragment implements OnItemListener {

    private Context mContext;
    private RecyclerView rvStudents;
    Call<List<Book>> call;
    private OnItemListener mOnItemListener;
    private HomeAdapter adapter;
    private List<Book> mBooks, mSelectedBooks;
    private static final String TAG = "Student";

    public StudentFragment() {
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
        View view = inflater.inflate(R.layout.fragment_student, container, false);
        mOnItemListener = this;
        rvStudents = view.findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new GridLayoutManager(mContext, 3));
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        call = apiInterface.getBooks(
                ApiClient.PASSWORD,
                "read_book"
        );


        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {

                mBooks = response.body();
                mSelectedBooks = mBooks.stream()
                        .filter(item -> item.getCategory()
                                .equals("Document"))
                        .collect(Collectors.toList());
                adapter = new HomeAdapter(mSelectedBooks, mOnItemListener);
                rvStudents.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.v(TAG, "asd" + t.toString());
            }
        });
        return view;
    }

    @Override
    public void onItemClick(View v, int iPosition) {
        Intent intent = new Intent(mContext, BookActivity.class);
        intent.putExtra("selected_book", mBooks.get(iPosition));
        startActivity(intent);
    }
}