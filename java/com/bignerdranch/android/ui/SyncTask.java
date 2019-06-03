package com.bignerdranch.android.ui;

import android.os.AsyncTask;

import java.net.URL;

import COMMUNICATION.LoginRequest;
import COMMUNICATION.RegisterRequest;
import model.Model;
import model.ServerProxy;

public class SyncTask extends AsyncTask<URL, Integer, Long>
{
    private Model model = Model.getSingleton();

    public interface Context {
        void onSyncComplete();
    }

    private SyncTask.Context context;

    public SyncTask(SyncTask.Context c) {
        context = c;
    }


    @Override
    protected Long doInBackground(URL... urls) {
        ServerProxy httpClient = new ServerProxy();
        httpClient.getEvents(model.getServerHost(), model.getServerPort());
        httpClient.getPersons(model.getServerHost(), model.getServerPort());
        return null;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        context.onSyncComplete();
    }
}
