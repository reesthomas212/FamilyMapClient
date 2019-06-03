package com.bignerdranch.android.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import model.Model;

public class EventActivity extends AppCompatActivity {

    private Model model = Model.getSingleton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Family Map");

        familyMapFragment FamMapFragment = familyMapFragment.newInstance(null);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.event_frameLayout, FamMapFragment)
                .commit();
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
