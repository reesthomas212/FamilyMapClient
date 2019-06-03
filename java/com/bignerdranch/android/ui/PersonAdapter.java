package com.bignerdranch.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import MODELS.Event;
import MODELS.Person;
import model.Model;

public class PersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Object> objects;
    private Context context;
    private Model model = Model.getSingleton();

    private static final int ITEM_TYPE_LIST_TITLE = 0;
    private static final int ITEM_TYPE_RELATIVE = 1;
    private static final int ITEM_TYPE_EVENT = 2;

    public PersonAdapter(List<Object> objects, Context context)
    {
        this.objects = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ITEM_TYPE_RELATIVE)
        {
            final View v = View.inflate(context, R.layout.person_viewholder, null);
            final PersonViewHolder p = new PersonViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    p.onClick(v);
                }
            });
            return p;
        }
        else if (i == ITEM_TYPE_EVENT)
        {
            final View v = View.inflate(context, R.layout.event_viewholder, null);
            final EventViewHolder e = new EventViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    e.onClick(v);
                }
            });
            return e;
        }
        else
        {
            final View v = View.inflate(context, R.layout.person_list_title, null);
            final TitleViewHolder t = new TitleViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    t.onClick(v);
                }
            });
            return t;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == ITEM_TYPE_RELATIVE)
        {
            PersonViewHolder p = (PersonViewHolder)viewHolder;
            Person relative = (Person)objects.get(i);
            if (relative.getGender().equals("m"))
            {
                p.mPersonImageView.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_male).
                        colorRes(R.color.maleBlue).sizeDp(30));
            }
            else
            {
                p.mPersonImageView.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_female).
                        colorRes(R.color.femalePink).sizeDp(30));
            }

            p.mPersonTextView1.setText(relative.getFirstName() + " " + relative.getLastName());
            p.mPersonTextView2.setText(model.getRelation(relative));
        }
        else if (viewHolder.getItemViewType() == ITEM_TYPE_EVENT)
        {
            EventViewHolder e = (EventViewHolder)viewHolder;

            e.mEventImageView.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_map_marker).
            colorRes(R.color.darkGrey).sizeDp(30));

            Object o = objects.get(i);
            Event event = (Event) o;
            Person person = model.getPeople().get(event.getPerson());

            e.mEventTextView1.setText(event.getEventType() + ": " +
                    event.getCity() + ", " +
                    event.getCountry() + " (" +
                    event.getYear() + ")");
            e.mEventTextView2.setText(person.getFirstName() + " " + person.getLastName());
        }
        else
        {
            TitleViewHolder t = (TitleViewHolder)viewHolder;

            if (i == 0)
            {
                t.mTitleTextView.setText("LIFE EVENTS");
            }
            else
            {
                t.mTitleTextView.setText("FAMILY");
            }
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = objects.get(position);

        if (o instanceof Person)
        {
            return ITEM_TYPE_RELATIVE;
        }
        else if (o instanceof Event)
        {
            return ITEM_TYPE_EVENT;
        }
        else
        {
            return ITEM_TYPE_LIST_TITLE;
        }
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView mPersonImageView;
        private TextView mPersonTextView1;
        private TextView mPersonTextView2;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            mPersonImageView = itemView.findViewById(R.id.person_image);
            mPersonTextView1 = itemView.findViewById(R.id.relative_name);
            mPersonTextView2 = itemView.findViewById(R.id.person_relationship);
        }

        @Override
        public void onClick(View view)
        {
            Person p = (Person) objects.get(getAdapterPosition());
            ArrayList<Event> personEvents = model.addPersonEvents(p);
            Collections.sort(personEvents);

            HashMap<Person, ArrayList<Event>> newPersonEventMap = new HashMap<>();
            newPersonEventMap.put(p, personEvents);
            model.setPersonEvents(newPersonEventMap);

            Intent intent = new Intent(context, PersonActivity.class);
            context.startActivity(intent);
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView mEventImageView;
        private TextView mEventTextView1;
        private TextView mEventTextView2;

        public EventViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mEventImageView = itemView.findViewById(R.id.event_image);
            mEventTextView1 = itemView.findViewById(R.id.event_info);
            mEventTextView2 = itemView.findViewById(R.id.event_person);
        }

        @Override
        public void onClick(View view)
        {
            Event e = (Event) objects.get(getPosition());
            model.setSelectedEvent(e);

            Intent intent = new Intent(context, EventActivity.class);
            context.startActivity(intent);
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView mTitleImageView;
        private TextView mTitleTextView;
        public TitleViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mTitleImageView = itemView.findViewById(R.id.person_list_image);
            mTitleTextView = itemView.findViewById(R.id.person_list_title);
            mTitleTextView.setTextSize(20);
            mTitleImageView.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_arrow_down).
                    colorRes(R.color.darkGrey).sizeDp(30));
        }

        @Override
        public void onClick(View view)
        {
            if (getLayoutPosition() == 0)
            {
                if (model.isLifeEventsExpanded())
                {
                    model.setLifeEventsExpanded(false);
                }
                else
                {
                    model.setLifeEventsExpanded(true);
                }
            }
            else
            {
                if (model.isFamilyExpanded())
                {
                    model.setFamilyExpanded(false);
                }
                else
                {
                    model.setFamilyExpanded(true);
                }
            }
            PersonActivity myActivity = (PersonActivity)context;
            myActivity.onArrowClick(getLayoutPosition());
        }
    }
}


