package com.crimefighter.crimefighter.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crimefighter.crimefighter.R;
import com.crimefighter.crimefighter.activities.FoundActivity;
import com.crimefighter.crimefighter.activities.MainActivity;
import com.crimefighter.crimefighter.activities.ReportActivity;
import com.crimefighter.crimefighter.activities.StolenActivity;
import com.crimefighter.crimefighter.activities.WaitActivity;
import com.crimefighter.crimefighter.activities.WitnessActivity;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView personName;
        TextView personAge;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            personName = (TextView) itemView.findViewById(R.id.item_name);
            personName.setTypeface(MainActivity.mTypeface);
            personName.setTextColor(Color.BLACK);
            personAge = (TextView) itemView.findViewById(R.id.item_dist);
            personAge.setTypeface(MainActivity.mTypeface);
            personAge.setTextColor(Color.BLACK);
        }
    }

    List<Item> persons;
    Context context;

    public RVAdapter(List<Item> persons, Context con) {
        this.context = con;
        this.persons = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void clear() {
        persons.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Item> list) {
        persons.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, int i) {
        final int jj = i;
        personViewHolder.personName.setText(persons.get(i).itemName);
        personViewHolder.personAge.setText(persons.get(i).distance);
        personViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WaitActivity.class);
                intent.putExtra("id", Integer.toString(persons.get(jj).id));
                intent.putExtra("name", (persons.get(jj).itemName));
                intent.putExtra("distance", (persons.get(jj).distance));
                intent.putExtra("description", (persons.get(jj).description));
                intent.putExtra("location", new String[]{Double.toString(persons.get(jj).dd.getLatitude()), Double.toString(persons.get(jj).dd.getLongitude())});
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
}