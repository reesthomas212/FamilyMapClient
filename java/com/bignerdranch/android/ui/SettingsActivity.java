package com.bignerdranch.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import model.Model;

public class SettingsActivity extends AppCompatActivity implements SyncTask.Context{

    private Model model = Model.getSingleton();

    private Spinner mStoryLineColor;
    private Spinner mAncestorLineColor;
    private Spinner mSpouseLineColor;
    private Spinner mMapType;
    private Switch mStoryLineActive;
    private Switch mAncestorLineActive;
    private Switch mSpouseLineActive;
    private LinearLayout mReSyncLayout;
    private LinearLayout mLogoutLayout;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Family Map: Settings");

        initializeWidgets();

        setListeners();
    }

    private void initializeWidgets() {
        mStoryLineColor = (Spinner) findViewById(R.id.story_lines_spinner);
        mStoryLineColor.setSelection(model.getStorySpinnerPosition());
        mAncestorLineColor = (Spinner) findViewById(R.id.ancestor_lines_spinner);
        mAncestorLineColor.setSelection(model.getAncestorSpinnerPosition());
        mSpouseLineColor = (Spinner) findViewById(R.id.spouse_line_spinner);
        mSpouseLineColor.setSelection(model.getSpouseSpinnerPosition());
        mMapType = (Spinner) findViewById(R.id.map_type_spinner);
        mMapType.setSelection(model.getMapTypeSpinnerPosition());
        mStoryLineActive = (Switch) findViewById(R.id.story_lines_switch);
        mAncestorLineActive = (Switch) findViewById(R.id.ancestor_lines_switch);
        mSpouseLineActive = (Switch) findViewById(R.id.spouse_line_switch);
        mReSyncLayout = (LinearLayout) findViewById(R.id.sync_layout);
        mLogoutLayout = (LinearLayout) findViewById(R.id.logout_layout);
    }

    private void setListeners() {
        mStoryLineColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                String value = mStoryLineColor.getSelectedItem().toString();
                model.getSettings().setStoryLineColor(value);
                model.setStorySpinnerPosition(mStoryLineColor.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mAncestorLineColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = mAncestorLineColor.getSelectedItem().toString();
                model.getSettings().setAncestorLineColor(value);
                model.setAncestorSpinnerPosition(mAncestorLineColor.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSpouseLineColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = mSpouseLineColor.getSelectedItem().toString();
                model.getSettings().setSpouseLineColor(value);
                model.setSpouseSpinnerPosition(mSpouseLineColor.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = mMapType.getSelectedItem().toString();
                model.getSettings().setMapType(value);
                model.setMapTypeSpinnerPosition(mMapType.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (model.getSettings().isStoryLineActive()) {mStoryLineActive.setChecked(true);}
        else {mStoryLineActive.setChecked(false);}
        mStoryLineActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {model.getSettings().setStoryLineActive(true);}
                else {model.getSettings().setStoryLineActive(false);}
            }
        });

        if (model.getSettings().isAncestorLineActive()) {mAncestorLineActive.setChecked(true);}
        else {mAncestorLineActive.setChecked(false);}
        mAncestorLineActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {model.getSettings().setAncestorLineActive(true);}
                else {model.getSettings().setAncestorLineActive(false);}
            }
        });

        if (model.getSettings().isSpouseLineActive()) {mSpouseLineActive.setChecked(true);}
        else {mSpouseLineActive.setChecked(false);}
        mSpouseLineActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {model.getSettings().setSpouseLineActive(true);}
                else {model.getSettings().setSpouseLineActive(false);}
            }
        });

        mReSyncLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SyncTask task = new SyncTask(SettingsActivity.this);

                    task.execute(new URL("http://" + model.getServerHost() + ":" + model.getServerPort() + "/event/"));
                }
                catch (MalformedURLException e)
                {
                    mToast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLogoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.reset();
                startTopActivity(model.getMainActivity(), true);
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

    @Override
    public void onSyncComplete() {
        model.setSelectedEvent(null);
        startTopActivity(this, false);
    }
}
