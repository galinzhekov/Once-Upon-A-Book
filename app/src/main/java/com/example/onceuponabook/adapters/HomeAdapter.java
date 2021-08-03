package com.example.onceuponabook.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onceuponabook.R;
import com.example.onceuponabook.holders.HomeViewHolder;
import com.example.onceuponabook.listeners.OnItemListener;
import com.example.onceuponabook.models.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> implements Filterable {
    private List<Book> books, booksFull;
    private Context context;
    private static final String TAG = "HomeAdapter";
    private OnItemListener mOnItemListener;

    public HomeAdapter(List<Book> books, OnItemListener onItemListener) {
        this.books = books;
        this.booksFull = new ArrayList<>(books);
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //извлича контекста където се намира RecyclerView
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Извлича персонализиран изглед
        View contactView = inflater.inflate(R.layout.homepage_item_layout, parent, false);

        // Създава viewHolder обект
        HomeViewHolder viewHolder = new HomeViewHolder(contactView, mOnItemListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        // Взима текущия елемент от колекцията
        Book book = books.get(position);

        // Поставя стоиност на TextView за заглавие
        holder.setTvHomeItemName(book.getName());
        holder.setTvHomeItemPrice(String.valueOf(book.getPrice()));

        Log.v(TAG, book.getCategory());

        Picasso.get().load(book.getImage()).into(holder.getTvHomeItemImage());
        // Поставя слушател за натискане на елемент от списъка
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Book> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(booksFull);
            } else {
                String filteredPattern = constraint.toString().toLowerCase().trim();

                for (Book book : booksFull) {
                    if (book.getName().toLowerCase().contains(filteredPattern) || book.getAuthor().toLowerCase().contains(filteredPattern)) {
                        filteredList.add(book);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            books.clear();
            books.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
