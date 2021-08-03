package com.example.onceuponabook.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onceuponabook.R;
import com.example.onceuponabook.holders.HomeViewHolder;
import com.example.onceuponabook.holders.SingleItemViewHolder;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Book;
import com.example.onceuponabook.models.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<SingleItemViewHolder> {
    private List<Category> categories;
    private Context context;
    private static final String TAG = "CategoriesAdapter";
    private OnItemListener mOnItemListener;

    public CategoryAdapter(List<Category> categories, OnItemListener onItemListener) {
        this.categories = categories;
        this.mOnItemListener = onItemListener;
    }
    @NonNull
    @Override
    public SingleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //извлича контекста където се намира RecyclerView
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Извлича персонализиран изглед
        View contactView = inflater.inflate(R.layout.single_item_layout, parent, false);

        // Създава viewHolder обект
        SingleItemViewHolder viewHolder = new SingleItemViewHolder(contactView, mOnItemListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemViewHolder holder, int position) {
        // Взима текущия елемент от колекцията
        Category category = categories.get(position);

        // Поставя стоиност на TextView за заглавие
        holder.setTvSingleItem(category.getCategory());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
