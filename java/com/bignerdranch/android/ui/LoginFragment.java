package com.bignerdranch.android.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import MODELS.AuthToken;
import MODELS.User;
import model.Model;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements LoginTask.Context {

    private static final String ARG_TITLE = "title";
    private String title;

    private Model model = Model.getSingleton();

    private EditText mServerHost;
    private EditText mServerPort;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private RadioGroup mRadioGroup;
    private RadioButton mMaleButton;
    private RadioButton mFemaleButton;
    private Button mLoginButton;
    private Button mRegisterButton;

    private Toast mLoginToast;

    private TextWatcher mLoginWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkLoginFields();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private void checkLoginFields()
    {
        String s1 = mServerHost.getText().toString();
        String s2 = mServerPort.getText().toString();
        String s3 = mUserName.getText().toString();
        String s4 = mPassword.getText().toString();

        if(s1.equals("")||
            s2.equals("")||
            s3.equals("")||
            s4.equals(""))
        {
            mLoginButton.setEnabled(false);
        }
        else
        {
            mLoginButton.setEnabled(true);
        }
    }

    private TextWatcher mRegisterWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkRegisterFields();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private void checkRegisterFields()
    {
        String s1 = mServerHost.getText().toString();
        String s2 = mServerPort.getText().toString();
        String s3 = mUserName.getText().toString();
        String s4 = mPassword.getText().toString();
        String s5 = mFirstName.getText().toString();
        String s6 = mLastName.getText().toString();
        String s7 = mEmail.getText().toString();

        if(s1.equals("")||
            s2.equals("")||
            s3.equals("")||
            s4.equals("")||
            s5.equals("")||
            s6.equals("")||
            s7.equals(""))
        {
            mRegisterButton.setEnabled(false);
        }
        else
        {
            if(mMaleButton.isChecked())
            {
                mRegisterButton.setEnabled(true);
            }
            else if(mFemaleButton.isChecked())
            {
                mRegisterButton.setEnabled(true);
            }
            else
            {
                mRegisterButton.setEnabled(false);
            }
        }
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String title) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        initializeWidgets(v);

        setListeners();

        return v;
    }

    private void initializeWidgets(View v)
    {
        mServerHost = v.findViewById(R.id.edit_server_host_id);
        mServerPort = v.findViewById(R.id.edit_server_port_id);
        mUserName = v.findViewById(R.id.edit_username_id);
        mPassword = v.findViewById(R.id.edit_password_id);
        mFirstName = v.findViewById(R.id.edit_first_name_id);
        mLastName = v.findViewById(R.id.edit_last_name_id);
        mEmail = v.findViewById(R.id.edit_email_id);

        mRadioGroup = v.findViewById(R.id.my_radioGroup);
        mRadioGroup.setEnabled(false);
        mMaleButton = v.findViewById(R.id.male_button);
        mFemaleButton = v.findViewById(R.id.female_button);

        mLoginButton = v.findViewById(R.id.login_button);
        mLoginButton.setEnabled(false);

        mRegisterButton = v.findViewById(R.id.register_button);
        mRegisterButton.setEnabled(false);
    }

    private void setListeners()
    {
        mServerHost.addTextChangedListener(mLoginWatcher);
        mServerPort.addTextChangedListener(mLoginWatcher);
        mUserName.addTextChangedListener(mLoginWatcher);
        mPassword.addTextChangedListener(mLoginWatcher);

        mServerHost.addTextChangedListener(mRegisterWatcher);
        mServerPort.addTextChangedListener(mRegisterWatcher);
        mUserName.addTextChangedListener(mRegisterWatcher);
        mPassword.addTextChangedListener(mRegisterWatcher);
        mFirstName.addTextChangedListener(mRegisterWatcher);
        mLastName.addTextChangedListener(mRegisterWatcher);
        mEmail.addTextChangedListener(mRegisterWatcher);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mRadioGroup.setEnabled(true);
                checkRegisterFields();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginButtonClick();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterButtonClick();
            }
        });
    }

    public void onLoginButtonClick()
    {
        try {
            model.setServerHost(mServerHost.getText().toString());
            model.setServerPort(mServerPort.getText().toString());
            model.setUserName(mUserName.getText().toString());
            model.setPassword(mPassword.getText().toString());

            LoginTask task = new LoginTask(this);

            task.execute(new URL("http://" + mServerHost.toString() + ":" + mServerPort.toString() + "/user/login"));
        }
        catch (MalformedURLException e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
    }

    public void onRegisterButtonClick()
    {
        try {
            model.setServerHost(mServerHost.getText().toString());
            model.setServerPort(mServerPort.getText().toString());
            model.setUserName(mUserName.getText().toString());
            model.setPassword(mPassword.getText().toString());
            model.setFirstName(mFirstName.getText().toString());
            model.setLastName(mLastName.getText().toString());
            model.setEmail(mEmail.getText().toString());
            if (mMaleButton.isChecked()) {model.setGender("m");}
            else if (mFemaleButton.isChecked()) {model.setGender("f");}

            LoginTask task = new LoginTask(this);

            task.execute(new URL("http://" + mServerHost.getText().toString() + ":" + mServerPort.getText().toString() + "/user/register"));
        }
        catch (MalformedURLException e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
    }

    @Override
    public void onLoginButtonComplete() {
        if (model.getLoginResult() == null)
        {
            mLoginToast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
        }
        else if (model.getLoginResult().getErrorMessage() != null)
        {
            mLoginToast.makeText(getActivity(), model.getLoginResult().getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            setUserInfo(model.getLoginResult().getPersonID());
            AuthToken a = new AuthToken(model.getLoginResult().getAuthToken(), model.getLoginResult().getUsername());
            model.setCurrAuthToken(a);
            //mLoginToast.makeText(getContext(),(model.getFirstName() + " " + model.getLastName()), Toast.LENGTH_SHORT).show();
            getGenealogyInfo();
        }
    }

    @Override
    public void onRegisterButtonComplete() {
        if (model.getRegisterResult() == null)
        {
            mLoginToast.makeText(getActivity(), "Register Failed!", Toast.LENGTH_SHORT).show();
        }
        else if (model.getRegisterResult().getErrorMessage() != null)
        {
            mLoginToast.makeText(getActivity(), model.getRegisterResult().getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            setUserInfo(model.getRegisterResult().getPersonID());
            AuthToken a = new AuthToken(model.getRegisterResult().getAuthToken(), model.getRegisterResult().getUsername());
            model.setCurrAuthToken(a);
            //mLoginToast.makeText(getContext(),(model.getFirstName() + " " + model.getLastName()), Toast.LENGTH_SHORT).show();
            getGenealogyInfo();
        }
    }

    private void setUserInfo(String personID)
    {
        User user = new User(model.getUserName(),
                             model.getPassword(),
                             model.getEmail(),
                             model.getFirstName(),
                             model.getLastName(),
                             model.getGender(),
                             personID);

        model.setCurrUser(user);
    }

    @Override
    public void onGetGenealogyComplete()
    {
        model.setCurrUsersPerson(model.getPersonsResult().getPersonArray()[0]);

        if (model.getCurrUser().getFirstName() == null)
        {model.getCurrUser().setFirstName(model.getCurrUsersPerson().getFirstName());}

        if (model.getCurrUser().getLastName() == null)
        {model.getCurrUser().setLastName(model.getCurrUsersPerson().getLastName());}

        if (model.getCurrUser().getGender() == null)
        {model.getCurrUser().setGender(model.getCurrUsersPerson().getGender());}

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onLoginComplete();
    }

    private void getGenealogyInfo()
    {
        try {
            LoginTask task = new LoginTask(this);

            task.execute(new URL("http://" + mServerHost.getText().toString() + ":" + mServerPort.getText().toString() + "/user/event/"),
                         new URL("http://" + mServerHost.getText().toString() + ":" + mServerPort.getText().toString() + "/user/person/"));
        }
        catch (MalformedURLException e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
    }
}



