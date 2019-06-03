package com.bignerdranch.android.ui;

import android.app.ListActivity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import MODELS.Event;
import MODELS.User;
import model.Model;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private Model model = Model.getSingleton();
    private RecyclerView searchRecycler;
    private List<Object> objects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Family Map: Search");

        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                objects = model.getSearchFilteredObjects(s.toLowerCase());
                searchRecycler.setAdapter(new SearchAdapter( objects, SearchActivity.this));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        searchRecycler = findViewById(R.id.search_recycler_view);
        searchRecycler.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
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
