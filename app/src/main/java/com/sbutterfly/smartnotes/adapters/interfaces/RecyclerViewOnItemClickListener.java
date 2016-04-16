package com.sbutterfly.smartnotes.adapters.interfaces;

import android.view.View;

public interface RecyclerViewOnItemClickListener {
    void onItemClick(View clickedView, int position);
    void onItemLongClick(View clickedView, int position);
}