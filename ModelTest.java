package com.bignerdranch.android.familymap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import COMMUNICATION.EventsRequest;
import COMMUNICATION.EventsResult;
import DAO.AuthTokenDAO;
import MODELS.AuthToken;
import MODELS.Event;
import MODELS.Person;
import SERVICES.Database;
import model.Filter;
import model.Model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ModelTest {
    private Model model = Model.getSingleton();
    private Event event;
    private Event event2;
    private Event event3;
    private Event event4;
    private Event event5;
    private Person person;
    private Person father;
    private Person mother;
    private HashMap<Person, ArrayList<Event>> personEvents;

    @Before
    public void setUp()
    {
        event = new Event("eventID", "descendant", "personID", 12.0, 12.0, "USA", "Provo", "eventType", "1993");
        event2 = new Event("eventID2", "descendant", "personID", 10.0, 10.0, "USA", "Provo", "birth", "1992");
        event3 = new Event("eventID3", "descendant", "personID", 8.0, 8.0, "USA", "Provo", "death", "1991");
        event4 = new Event("eventID4", "descendant", "fatherID", 6.0, 6.0, "USA", "Provo", "marriage", "1987");
        event5 = new Event("eventID5", "descendant", "motherID", 6.0, 6.0, "USA", "Provo", "marriage", "1987");
        person = new Person("personID", "descendant", "firstName", "lastName", "m", "fatherID", "motherID", "spouseID");
        father = new Person("fatherID", "descendant", "Mr.", "Father", "m", null, null, "fatherID");
        mother = new Person("motherID", "descendant", "Mrs.", "Mother", "f", null, null, "motherID");

        HashMap<String, Event> events = new HashMap<>();
        events.put(event.eventID, event);
        model.setEvents(events);

        Filter f = new Filter();
        f.setFilterMales(true);
        f.setFilterFemales(true);
        f.setFilterMomSide(true);
        f.setFilterDadSide(true);
        model.setFilter(f);

        model.setPeople(new HashMap<String, Person>());
        model.getPeople().put(person.getPersonID(), person);

        personEvents = new HashMap<>();
        ArrayList<Event> events2 = new ArrayList<>();
        events2.add(event);
        events2.add(event2);
        events2.add(event3);
        personEvents.put(person, events2);
    }

    @After
    public void tearDown()
    {

    }

    @Test
    public void testGetFilteredEvents_Exists() {
        model.getFilteredEvents();

        assertEquals(event, model.getFilteredEvents().get("eventID"));
        assertEquals(person.getPersonID(), model.getFilteredEvents().get("eventID").getPerson());
    }

    @Test
    public void testGetFilteredEvents_NotExists() {
        model.getFilter().setFilterMales(false);
        model.getFilteredEvents();

        assertEquals(null, model.getFilteredEvents().get("eventID"));
    }

    @Test
    public void testGetEarliestEvent() {
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);
        events.add(event3);
        events.add(event2);

        Collections.sort(events);

        assertEquals(event2, events.get(0));
        assertEquals(event3, events.get(events.size()-1));
    }

    @Test
    public void testGetPersonEventByType() {
        HashMap<String, Event> filtEvents = new HashMap<>();
        filtEvents.put("eventID", event);
        model.filteredEvents = filtEvents;
        Event e = model.getPersonEventByType(event.getEventType(), person);

        assertNotEquals(null, e);
    }

    @Test
    public void testGetSelectedPerson() {
        model.setPersonEvents(personEvents);
        assertEquals(person, model.getSelectedPerson());
    }

    @Test
    public void testGetRelation()
    {
        model.setPersonEvents(personEvents);
        String relation = model.getRelation(father);
        assertEquals("Father", relation);

        relation = model.getRelation(mother);
        assertEquals("Mother", relation);

        personEvents = new HashMap<>();
        ArrayList<Event> events = new ArrayList<>();
        events.add(event4);
        personEvents.put(father, events);
        model.setPersonEvents(personEvents);
        relation = model.getRelation(person);
        assertEquals("Child", relation);
    }

    @Test
    public void testGetColor()
    {
        int RED = model.getColor("red");
        int BLACK = model.getColor("black");
        int CYAN = model.getColor("CYAN");

        assertEquals(-65536, RED);
        assertEquals(-16777216, BLACK);
        assertEquals(-16711681,  CYAN);
    }
}