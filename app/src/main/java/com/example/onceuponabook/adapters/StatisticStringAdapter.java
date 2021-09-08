package com.example.onceuponabook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onceuponabook.R;
import com.example.onceuponabook.holders.StatisticViewHolder;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Book;

import java.util.List;

public class StatisticStringAdapter extends RecyclerView.Adapter<StatisticViewHolder> {
    private List<String> mBooks;
    private List<Integer> mFrequency;
    private Context context;
    private static final String TAG = "CategoriesAdapter";
    private OnItemListener mOnItemListener;

    public StatisticStringAdapter(List<String> books, OnItemListener onItemListener, List<Integer> frequency) {
        this.mBooks = books;
        this.mOnItemListener = onItemListener;
        this.mFrequency = frequency;
    }
    @NonNull
    @Override
    public StatisticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //извлича контекста където се намира RecyclerView
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Извлича персонализиран изглед
        View contactView = inflater.inflate(R.layout.single_item_layout, parent, false);

        // Създава viewHolder обект
        StatisticViewHolder viewHolder = new StatisticViewHolder(contactView, mOnItemListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticViewHolder holder, int position) {
        // Взима текущия елемент от колекцията
        String book = mBooks.get(position);
        Integer i = mFrequency.get(position);

        // Поставя стоиност на TextView за заглавие
        holder.setTvSingleItem(book);
        holder.setBackgroundColorFilled(i);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }
}