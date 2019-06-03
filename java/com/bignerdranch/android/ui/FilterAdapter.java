package com.bignerdranch.android.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import MODELS.Person;
import model.Model;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> objects;
    private Context context;
    private Model model = Model.getSingleton();

    public FilterAdapter(List<Object> objects, Context context) {
        this.objects = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View v = View.inflate(context, R.layout.search_person_viewholder, null);
        final FilterViewHolder f = new FilterViewHolder(v);
        //f.onCheckedChanged(new CompoundButton().setOnCheckedChangeListener(), f.mFilterSwitch.isChecked());
        return f;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        FilterAdapter.FilterViewHolder f = (FilterAdapter.FilterViewHolder)viewHolder;
        String eventType = (String)objects.get(i);
        f.mFirstTextView.setText(eventType);
        f.mSecondTextView.setText("FILTER BY " + eventType.toUpperCase());
        f.mFilterSwitch.setChecked(true);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder implements Switch.OnCheckedChangeListener
    {
        public TextView mFirstTextView;
        public TextView mSecondTextView;
        public Switch mFilterSwitch;


        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            mFirstTextView = itemView.findViewById(R.id.filter_first_text_view);
            mSecondTextView = itemView.findViewById(R.id.filter_second_text_view);
            mFilterSwitch = itemView.findViewById(R.id.filter_switch);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        }
    }
}
