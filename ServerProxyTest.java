package com.bignerdranch.android.familymap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import COMMUNICATION.EventsRequest;
import COMMUNICATION.EventsResult;
import COMMUNICATION.LoginRequest;
import COMMUNICATION.LoginResult;
import COMMUNICATION.PersonsResult;
import COMMUNICATION.RegisterRequest;
import COMMUNICATION.RegisterResult;
import MODELS.AuthToken;
import model.Model;
import model.ServerProxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ServerProxyTest
{
    private Model model = Model.getSingleton();
    @Rule
    public RunInThreadRule runInThread = new RunInThreadRule();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface RunInThread {}

    @Before
    public void setUp()
    {

    }

    @After
    public void tearDown()
    {

    }

    @Test
    @RunInThread
    public void testLogin()
    {
        LoginRequest r = new LoginRequest("sheila", "parker");
        String serverHost = "10.0.2.2";
        String serverPort = "8080";

        new ServerProxy().login(r, serverHost, serverPort);

        LoginResult s = model.getLoginResult();
        assertNotEquals(null, s.getAuthToken());
        assertNotEquals(null, s.getUsername());
        assertNotEquals(null, s.getPersonID());
    }

    @Test
    @RunInThread
    public void testRegister()
    {
        RegisterRequest r = new RegisterRequest(UUID.randomUUID().toString(), "password", "yay@byu.edu", "Matt", "Anderson","m");
        String serverHost = "10.0.2.2";
        String serverPort = "8080";

        new ServerProxy().register(r, serverHost, serverPort);

        RegisterResult s = model.getRegisterResult();
        assertNotEquals(null, s.getAuthToken());
        assertNotEquals(null, s.getUsername());
        assertNotEquals(null, s.getPersonID());

        r = new RegisterRequest("sheila", "parker", "ya@byu.edu", "Sheila", "Parker", "f");
        new ServerProxy().register(r, serverHost, serverPort);
        s = model.getRegisterResult();
        assertEquals(null, s.getAuthToken());
        assertEquals(null, s.getUsername());
        assertEquals(null, s.getPersonID());
        assertNotEquals(null, s.getErrorMessage());
    }

    @Test
    public void testGetEvents()
    {
        String serverHost = "10.0.2.2";
        String serverPort = "8080";
        new ServerProxy().getEvents(serverHost, serverPort);

        EventsResult s = model.getEventsResult();
        assertNotEquals(null, s.data);
        assertEquals(null, s.getErrorMessage());
    }

    @Test
    public void testGetPersons()
    {
        String serverHost = "10.0.2.2";
        String serverPort = "8080";
        new ServerProxy().getPersons(serverHost, serverPort);

        PersonsResult s = model.getPersonsResult();
        assertNotEquals(null, s.data);
        assertEquals(null, s.getErrorMessage());
    }

    public class RunInThreadRule implements TestRule {

        @Override
        public Statement apply(Statement base, Description description ) {
            Statement result = base;
            RunInThread annotation = description.getAnnotation( RunInThread.class );
            if( annotation != null ) {
                result = new RunInThreadStatement( base );
            }
            return result;
        }
    }

    class RunInThreadStatement extends Statement {

        private final Statement baseStatement;
        private Future<?> future;
        private volatile Throwable throwable;

        RunInThreadStatement( Statement baseStatement ) {
            this.baseStatement = baseStatement;
        }

        @Override
        public void evaluate() throws Throwable {
            ExecutorService executorService = runInThread();
            try {
                waitTillFinished();
            } finally {
                executorService.shutdown();
            }
            rethrowAssertionsAndErrors();
        }

        private ExecutorService runInThread() {
            ExecutorService result = Executors.newSingleThreadExecutor();
            future = result.submit( new Runnable() {
                @Override
                public void run() {
                    try {
                        baseStatement.evaluate();
                    } catch( Throwable throwable ) {
                        RunInThreadStatement.this.throwable = throwable;
                    }
                }
            } );
            return result;
        }

        private void waitTillFinished() {
            try {
                future.get();
            } catch( ExecutionException shouldNotHappen ) {
                throw new IllegalStateException( shouldNotHappen );
            } catch( InterruptedException shouldNotHappen ) {
                throw new IllegalStateException( shouldNotHappen );
            }
        }

        private void rethrowAssertionsAndErrors() throws Throwable {
            if( throwable != null ) {
                throw throwable;
            }
        }
    }
}
