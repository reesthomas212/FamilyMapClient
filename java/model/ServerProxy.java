package model;


import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import COMMUNICATION.EventsResult;
import COMMUNICATION.LoginRequest;
import COMMUNICATION.LoginResult;
import COMMUNICATION.PersonsResult;
import COMMUNICATION.RegisterRequest;
import COMMUNICATION.RegisterResult;

public class ServerProxy {

    private Model model = Model.getSingleton();

    public void login(LoginRequest r, String serverHost, String serverPort)
    {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(r);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();
            LoginResult s;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                s = gson.fromJson(reader, LoginResult.class);
                System.out.println("LoginResult created");
            }
            else {
                s = new LoginResult(null,null,null);
                s.setErrorMessage("Login failed!");
            }
            model.setLoginResult(s);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(RegisterRequest r, String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(r);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();
            RegisterResult s;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                s = gson.fromJson(reader, RegisterResult.class);
                System.out.println("RegisterResult created");
            } else {
                s = new RegisterResult(null,null,null);
                s.setErrorMessage("Register Failed!");
            }
            model.setRegisterResult(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getEvents(String serverHost, String serverPort)
    {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event/");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", model.getCurrAuthToken().getAuthToken());
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                EventsResult s = gson.fromJson(reader, EventsResult.class);
                System.out.println("EventsResult object created");
                model.setEventsResult(s);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getPersons(String serverHost, String serverPort)
    {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person/");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", model.getCurrAuthToken().getAuthToken());
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(http.getInputStream());
                PersonsResult s = gson.fromJson(reader, PersonsResult.class);
                System.out.println("PersonsResult object created");
                model.setPersonsResult(s);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
