package com.crimefighter.crimefighter.utils;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

import java.util.List;

/**
 * Created by anees on 3/25/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder>{

    List<Item> items;

    public RVAdapter(List<Item> items){
        this.items = items;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView itemName;
        TextView itemDist;

        ItemViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            itemName = (TextView)itemView.findViewById(R.id.item_name);
            itemDist = (TextView)itemView.findViewById(R.id.item_dist);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_dashboard, viewGroup, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.itemName.setText(items.get(i).itemName);
        itemViewHolder.itemDist.setText(items.get(i).distance);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
