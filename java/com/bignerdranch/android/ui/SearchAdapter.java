package com.bignerdranch.android.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Object> objects;
    private Context context;
    private Model model = Model.getSingleton();

    private static final int ITEM_TYPE_PERSON = 0;
    private static final int ITEM_TYPE_EVENT = 1;

    public SearchAdapter(List<Object> objects, Context context)
    {
        this.objects = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ITEM_TYPE_PERSON)
        {
            final View v = View.inflate(context, R.layout.search_person_viewholder, null);
            final PersonViewHolder p = new PersonViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    p.onClick(v);
                }
            });
            return p;
        }
        else
        {
            final View v = View.inflate(context, R.layout.search_event_viewholder, null);
            final EventViewHolder e = new EventViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    e.onClick(v);
                }
            });
            return e;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == ITEM_TYPE_PERSON)
        {
            PersonViewHolder p = (PersonViewHolder)viewHolder;
            Person person = (Person)objects.get(i);
            if (person.getGender().equals("m"))
            {
                p.mPersonImageView.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_male).
                        colorRes(R.color.maleBlue).sizeDp(30));
            }
            else
            {
                p.mPersonImageView.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_female).
                        colorRes(R.color.femalePink).sizeDp(30));
            }

            p.mPersonTextView.setText(person.getFirstName() + " " + person.getLastName());
        }
        else if (viewHolder.getItemViewType() == ITEM_TYPE_EVENT)
        {
            EventViewHolder e = (EventViewHolder)viewHolder;

            e.mEventImageView.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.darkGrey).sizeDp(30));

            Object o = objects.get(i);
            Event event = (Event) o;
            Person person = model.getPeople().get(event.getPerson());

            e.mFirstLine.setText(event.getEventType() + ": " +
                    event.getCity() + ", " +
                    event.getCountry() + " (" +
                    event.getYear() + ")");
            e.mLastLine.setText(person.getFirstName() + " " + person.getLastName());
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
            return ITEM_TYPE_PERSON;
        }
        else
        {
            return ITEM_TYPE_EVENT;
        }
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ImageView mPersonImageView;
        public TextView mPersonTextView;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            mPersonImageView = itemView.findViewById(R.id.search_person_image);
            mPersonTextView = itemView.findViewById(R.id.search_person_name);
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
        private TextView mFirstLine;
        private TextView mLastLine;

        public EventViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mEventImageView = itemView.findViewById(R.id.search_event_image);
            mFirstLine = itemView.findViewById(R.id.search_event_info);
            mLastLine = itemView.findViewById(R.id.search_event_person);
        }

        @Override
        public void onClick(View view)
        {
            Event e = (Event) objects.get(getAdapterPosition());
            model.setSelectedEvent(e);

            Intent intent = new Intent(context, EventActivity.class);
            context.startActivity(intent);
        }
    }
}
