package com.bignerdranch.android.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import model.Model;


public class MainActivity extends AppCompatActivity
{
    private LoginFragment loginFragment;
    private familyMapFragment FamMapFragment;
    private Model model = Model.getSingleton();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model.setMainActivity(this);
        Iconify.with(new FontAwesomeModule());
        if (model.getMainFamilyMapFragment() == null)
        {
            FragmentManager fm = getFragmentManager();
            loginFragment = LoginFragment.newInstance("");
            fm.beginTransaction()
                    .add(R.id.my_frameLayout, loginFragment)
                    .commit();
        }
        else
        {
            FragmentManager fm = getFragmentManager();
            fm.executePendingTransactions();
            FamMapFragment = model.getMainFamilyMapFragment();
            fm.beginTransaction()
                    .replace(R.id.my_frameLayout, FamMapFragment)
                    .commit();
        }
    }

    public void onLoginComplete()
    {
        FamMapFragment = familyMapFragment.newInstance(null);
        model.setMainFamilyMapFragment(FamMapFragment);
        FamMapFragment.setArguments(null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction().replace(R.id.my_frameLayout, FamMapFragment).commit();
            }
        });
    }
}
