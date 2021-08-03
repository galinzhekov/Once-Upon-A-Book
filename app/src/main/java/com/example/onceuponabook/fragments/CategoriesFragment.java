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
import com.example.onceuponabook.adapters.CategoryAdapter;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Category;
import com.example.onceuponabook.util.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment implements OnItemListener {

    private Context mContext;
    private RecyclerView rvCategories;
    Call<List<Category>> call;
    private OnItemListener mOnItemListener;
    private CategoryAdapter adapter;
    private List<Category> mCategories;
    private static final String TAG = "Categories";

    public CategoriesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        mOnItemListener = this;
        rvCategories = view.findViewById(R.id.rvCategories);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvCategories.setLayoutManager(linearLayoutManager);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        call = apiInterface.getCategories();


        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                mCategories = response.body();
                adapter = new CategoryAdapter(mCategories, mOnItemListener);
                rvCategories.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.v(TAG, "asd" + t.toString());
            }
        });
        return view;
    }

    @Override
    public void onItemClick(View v, int iPosition) {
        Intent intent = new Intent(mContext, DefaultActivity.class);
        intent.putExtra("selected_category", mCategories.get(iPosition));
        startActivity(intent);
    }
}