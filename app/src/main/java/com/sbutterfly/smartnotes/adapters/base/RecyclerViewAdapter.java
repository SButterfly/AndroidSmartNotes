/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package com.sbutterfly.smartnotes.adapters.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extension to standard RecyclerView.Adapter that also keep state of selected/activated items and add footer view;
 *
 * @param <T> Type of the class in this adapter
 * @param <H> ViewHolder type
 */
public abstract class RecyclerViewAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_TYPE = 776;

    // First param is item, second is selected
    private ArrayList<Tuple<T, Boolean>> items;
    private int selectedCount = 0;

    private final int footerId;

    public RecyclerViewAdapter(Collection<T> items, int footerId) {
        if (footerId < 0) {
            throw new IllegalArgumentException("Footer must be not negative value");
        }
        this.footerId = footerId;
        this.items = new ArrayList<>(items.size());
        for (T item: items) {
            this.items.add(new Tuple<>(item, false));
        }
    }

    public void onBindModelViewHolder(H viewHolder, int position) {
        viewHolder.itemView.setActivated(items.get(position).y);
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (FOOTER_TYPE == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(footerId, parent, false);
            return new FooterViewHolder(view);
        } else {
            return onCreateModelViewHolder(parent, viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= getModelItemCount()){
            return FOOTER_TYPE;
        }
        return super.getItemViewType(position);
    }

    public abstract H onCreateModelViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position < items.size()) {
            onBindModelViewHolder((H) viewHolder, position);
        }
    }

    public T getItem(int position) {
        return items.get(position).x;
    }

    @Override
    public int getItemCount() {
        return getModelItemCount()
                + (hasFooter() ? 1 : 0);
    }

    public int getModelItemCount() {
        return items.size();
    }

    public void deleteItem(int position) {
        selectedCount += items.get(position).y ? -1 : 0;
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void setItem(int position, T note) {
        items.set(position, new Tuple<>(note, items.get(position).y));
        notifyItemChanged(position);
    }

    public void insertItem(int position, T item) {
        items.add(position, new Tuple<>(item, false));
        notifyItemInserted(position);
    }

    public void toggleSelection(int position) {
        // change to opposite
        items.get(position).y ^= true;
        selectedCount += items.get(position).y ? 1 : -1;
        notifyItemChanged(position);
    }

    public void setSelected(int position) {
        if (!items.get(position).y) {
            items.get(position).y = true;
            selectedCount++;
            notifyItemChanged(position);
        }
    }

    public void clearSelection(int position) {
        if (items.get(position).y) {
            items.get(position).y = false;
            selectedCount--;
            notifyItemChanged(position);
        }
    }

    public void clearSelections() {
        if (selectedCount > 0) {
            for (Tuple<T, Boolean> tuple : this.items) {
                tuple.y = false;
            }
            selectedCount = 0;
            notifyDataSetChanged();
        }
    }

    public int getSelectedItemsCount() {
        return selectedCount;
    }

    public List<Integer> getSelectedItemsPositions() {
        List<Integer> indexes = new ArrayList<>(selectedCount);
        for (int i = 0; i < this.items.size(); i++) {
            if (items.get(i).y) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    public List<T> getSelectedItems() {
        List<T> result = new ArrayList<>(selectedCount);
        for (int i = 0; i < this.items.size(); i++) {
            if (items.get(i).y) {
                result.add(items.get(i).x);
            }
        }
        return result;
    }

    public boolean hasFooter() {
        return footerId != 0;
    }

    private class Tuple<X, Y> {
        public X x;
        public Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
