package com.example.onceuponabook.holders;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onceuponabook.R;
import com.example.onceuponabook.listeners.OnItemListener;

public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public void setTvHomeItemPrice(String tvHomeItemPrice) {
        this.tvHomeItemPrice.setText(tvHomeItemPrice);
    }

    public void setTvHomeItemName(String tvHomeItemName) {
        this.tvHomeItemName.setText(tvHomeItemName);
    }

    public ImageView getTvHomeItemImage() {
        return tvHomeItemImage;
    }

    private TextView tvHomeItemPrice, tvHomeItemName;
    private ImageView tvHomeItemImage;
    private OnItemListener mOnItemListener;

    public HomeViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
        super(itemView);

        tvHomeItemName = itemView.findViewById(R.id.tvHomeItemName);
        tvHomeItemPrice = itemView.findViewById(R.id.tvHomeItemPrice);
        tvHomeItemImage = itemView.findViewById(R.id.tvHomeItemImage);
        mOnItemListener = onItemListener;
        itemView.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        mOnItemListener.onItemClick(v, getAdapterPosition());
    }
}
