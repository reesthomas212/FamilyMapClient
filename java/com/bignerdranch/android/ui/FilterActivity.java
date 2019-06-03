package com.bignerdranch.android.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import model.Filter;
import model.Model;

public class FilterActivity extends AppCompatActivity {

    private Switch mMaleSwitch;
    private Switch mFemaleSwitch;
    private Switch mFatherSwitch;
    private Switch mMotherSwitch;
    private RecyclerView filterRecycler;
    private List<Object> objects = new ArrayList<>();

    private Filter filter = Model.getSingleton().getFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Family Map: Filter");

        //objects = ???

        filterRecycler = findViewById(R.id.filter_recycler);
        filterRecycler.setLayoutManager(new LinearLayoutManager(FilterActivity.this));
        filterRecycler.setAdapter(new FilterAdapter(objects, this));


        initializeSwitches();

        setListeners();
    }

    private void initializeSwitches()
    {
        mMaleSwitch = (Switch) findViewById(R.id.male_switch);
        mFemaleSwitch = (Switch) findViewById(R.id.female_switch);
        mFatherSwitch = (Switch) findViewById(R.id.father_switch);
        mMotherSwitch = (Switch) findViewById(R.id.mother_switch);
    }

    private void setListeners()
    {
        setMaleListener();
        setFemaleListener();
        setFatherSideListener();
        setMotherSideListener();
    }

    private void setMaleListener()
    {
        if (filter.isFilterMales()) {mMaleSwitch.setChecked(true);}
        else {mMaleSwitch.setChecked(false);}

        mMaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mMaleSwitch.isChecked()) {filter.setFilterMales(true);}
                else {filter.setFilterMales(false);}
            }
        });
    }

    private void setFemaleListener()
    {
        if (filter.isFilterFemales()) {mFemaleSwitch.setChecked(true);}
        else {mFemaleSwitch.setChecked(false);}

        mFemaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mFemaleSwitch.isChecked()) {filter.setFilterFemales(true);}
                else {filter.setFilterFemales(false);}
            }
        });
    }

    private void setFatherSideListener()
    {
        if (filter.isFilterDadSide()) {mFatherSwitch.setChecked(true);}
        else {mFatherSwitch.setChecked(false);}

        mFatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mFatherSwitch.isChecked()) {filter.setFilterDadSide(true);}
                else {filter.setFilterDadSide(false);}
            }
        });
    }

    private void setMotherSideListener()
    {
        if (filter.isFilterMomSide()) {mMotherSwitch.setChecked(true);}
        else {mMotherSwitch.setChecked(false);}

        mMotherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mMotherSwitch.isChecked()) {filter.setFilterMomSide(true);}
                else {filter.setFilterMomSide(false);}
            }
        });
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
}
