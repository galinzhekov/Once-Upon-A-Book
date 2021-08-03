package com.example.onceuponabook.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onceuponabook.R;
import com.example.onceuponabook.listeners.OnItemListener;

public class StatisticViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView mTvSingleItem;
    private OnItemListener mOnItemListener;

    public void setTvSingleItem(String tvSingleItem) {
        this.mTvSingleItem.setText(tvSingleItem);
    }
    public void setBackgroundColorFilled(int range) {
        this.mTvSingleItem.getBackground().setLevel(range);
    }

    public StatisticViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
        super(itemView);
        mOnItemListener = onItemListener;
        mTvSingleItem = itemView.findViewById(R.id.tvSingleItem);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mOnItemListener.onItemClick(v, getAdapterPosition());
    }
}
