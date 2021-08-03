package com.example.onceuponabook.adapters;

import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.onceuponabook.fragments.AuthorsFragment;
import com.example.onceuponabook.fragments.BrowseAllFragment;
import com.example.onceuponabook.fragments.CategoriesFragment;
import com.example.onceuponabook.fragments.HomeFragment;
import com.example.onceuponabook.fragments.StudentFragment;
import com.example.onceuponabook.models.Book;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "Position";

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Log.v(TAG, "asd" + position);
        switch (position) {
            case 1:
                return new CategoriesFragment();
            case 2:
                return new AuthorsFragment();
            case 3:
                return new BrowseAllFragment();
            case 4:
                return new StudentFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

}

