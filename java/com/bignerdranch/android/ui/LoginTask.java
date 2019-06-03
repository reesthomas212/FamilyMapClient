package com.bignerdranch.android.ui;

import android.os.AsyncTask;

import java.net.URL;

import COMMUNICATION.LoginRequest;
import COMMUNICATION.RegisterRequest;
import model.Model;
import model.ServerProxy;

public class LoginTask extends AsyncTask<URL, Integer, Long>
{
    private Model model = Model.getSingleton();
    private int check = 0;

    public interface Context {
        void onLoginButtonComplete();
        void onRegisterButtonComplete();
        void onGetGenealogyComplete();
    }

    private Context context;

    public LoginTask(Context c) {
        context = c;
    }


    @Override
    protected Long doInBackground(URL... urls) {
        ServerProxy httpClient = new ServerProxy();
        String[] split_url = urls[0].toString().split("/");
        if (split_url[split_url.length-1].equals("login"))
        {
            LoginRequest r = new LoginRequest(model.getUserName(), model.getPassword());
            httpClient.login(r, model.getServerHost(), model.getServerPort());
            check = 1;
        }
        else if (split_url[split_url.length-1].equals("register"))
        {
            RegisterRequest r = new RegisterRequest(model.getUserName(), model.getPassword(),
                                                    model.getEmail(), model.getFirstName(),
                                                    model.getLastName(), model.getGender());
            httpClient.register(r, model.getServerHost(), model.getServerPort());
            check = 2;
        }
        else if (split_url[split_url.length-1].equals("event"))
        {
            httpClient.getEvents(model.getServerHost(), model.getServerPort());
            httpClient.getPersons(model.getServerHost(), model.getServerPort());
            check = 3;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        if (check == 1)
            context.onLoginButtonComplete();
        else if (check == 2)
            context.onRegisterButtonComplete();
        else if (check == 3)
            context.onGetGenealogyComplete();
    }
}
