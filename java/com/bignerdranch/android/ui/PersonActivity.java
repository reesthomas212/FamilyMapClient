package com.bignerdranch.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import MODELS.Event;
import MODELS.Person;
import model.Model;

public class PersonActivity extends AppCompatActivity {

    private Model model = Model.getSingleton();
    private Person person;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mGender;
    private RecyclerView mRecyclerView;
    private List<Object> objects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        model.setLifeEventsExpanded(true);
        model.setFamilyExpanded(true);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Family Map: Person Details");

        mFirstName = findViewById(R.id.person_first_name);
        mLastName = findViewById(R.id.person_last_name);
        mGender = findViewById(R.id.person_gender);
        setPersonInfo();

        initializeObjectList();
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new PersonAdapter( objects, this));
    }

    private void initializeObjectList()
    {
        objects = new ArrayList<>();
        objects.add("LIFE_EVENTS"); //first list's title
        if (model.isLifeEventsExpanded())
        {
            objects.addAll(model.getPersonEvents().get(person));
        }
        objects.add("FAMILY");
        if (model.isFamilyExpanded())
        {
            if (person.getFather() != null)
            {
                objects.add(model.getPeople().get(person.getFather()));
            }
            if (person.getMother() != null)
            {
                objects.add(model.getPeople().get(person.getMother()));
            }
            if (person.getSpouse() != null)
            {
                objects.add(model.getPeople().get(person.getSpouse()));
            }
            if (model.getChild() != null)
            {
                objects.add(model.getChild());
            }
        }
    }

    private void setPersonInfo()
    {
        person = model.getSelectedPerson();
        mFirstName.setText(person.getFirstName());
        mLastName.setText(person.getLastName());
        if (person.getGender().equals("m")) { mGender.setText("Male"); }
        else { mGender.setText("Female"); }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startTopActivity(this,false);
        return super.onOptionsItemSelected(item);
    }

    private void startTopActivity(Context context, boolean newInstance)
    {
        Intent intent = new Intent(context, MainActivity.class);
        if (newInstance) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }

    public void onArrowClick(int i)
    {
        initializeObjectList();
        mRecyclerView.setAdapter(new PersonAdapter( objects, this));
    }
}
