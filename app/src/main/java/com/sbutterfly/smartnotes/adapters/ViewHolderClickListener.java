package com.sbutterfly.smartnotes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface ViewHolderClickListener {
     boolean onLongClick(RecyclerView.ViewHolder viewHolder, View view);
     void onClick(RecyclerView.ViewHolder viewHolder, View view);
}
