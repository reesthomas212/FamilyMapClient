package model;

import android.app.Activity;
import android.widget.Spinner;

import com.bignerdranch.android.ui.MainActivity;
import com.bignerdranch.android.ui.familyMapFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import COMMUNICATION.EventsResult;
import COMMUNICATION.LoginResult;
import COMMUNICATION.PersonsResult;
import COMMUNICATION.RegisterResult;
import MODELS.AuthToken;
import MODELS.Event;
import MODELS.Person;
import MODELS.User;

public class Model
{

    // static variable single_instance of type Model
    private static Model singleton = null;

    //map fragment pointer
    private familyMapFragment mainFamilyMapFragment;
    private MainActivity mainActivity;


    // For login input from user
    private String serverHost;
    private String serverPort;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;

    //Data members
    private HashMap<String, Person> people;
    private HashMap<String, Event> events;
    public HashMap<String, Event> filteredEvents;
    private HashMap<Marker, Event> mapEvents;
    private Map<Person, ArrayList<Event>> personEvents;
    private Settings settings = new Settings("red", "green", "blue", "normal");
    private Filter filter = new Filter();
    private Search search = new Search();
    private Event selectedEvent;
    private Event mainEvent;
    private boolean familyExpanded = true;
    private boolean lifeEventsExpanded = true;
    private int storySpinnerPosition = 0;
    private int ancestorSpinnerPosition = 1;
    private int spouseSpinnerPosition = 2;
    private int mapTypeSpinnerPosition = 0;
    private HashMap<String, Boolean> filterEventTypes = new HashMap<>();

    //Current user's info
    private AuthToken currAuthToken;
    private User currUser;
    private Person currUsersPerson;

    //Server Result objects
    private RegisterResult registerResult;
    private LoginResult loginResult;
    private EventsResult eventsResult;
    private PersonsResult personsResult;

    // private constructor restricted to this class itself
    private Model() {}

    // static method to create instance of Singleton class
    public static Model getSingleton()
    {
        if (singleton == null)
            singleton = new Model();

        return singleton;
    }

    public void createMaps()
    {
        //initialize maps for both person and event objects

        //start with arrays given from server
        Event[] E = eventsResult.getEventArray();
        Person[] P = personsResult.getPersonArray();

        for (int i = 0; i < E.length; i++)
        {
            E[i].setEventType(E[i].getEventType().toLowerCase());
        }

        //put data into events map
        events = new HashMap<>();
        for (int i = 0; i < E.length; i++)
        {
            events.put(E[i].getEventID(), E[i]);
        }

        //put data into people map
        people = new HashMap<>();
        for (int i = 0; i < P.length; i++)
        {
            people.put(P[i].getPersonID(), P[i]);
        }
    }

    public HashMap<String, Event> getFilteredEvents()
    {
        filteredEvents = new HashMap<>();
        filteredEvents.putAll(events);

        Set<String> eventTypes = filterEventTypes.keySet();
        for (String eType : eventTypes)
        {
            if (!filterEventTypes.get(eType))
            {
                Iterator<String> it = filteredEvents.keySet().iterator();

                while (it.hasNext()) {
                    String key = it.next();
                    Event event = filteredEvents.get(key);
                    if (event.getEventType().equals(eType))
                    {
                        it.remove();
                    }
                }
            }
        }

        if (!filter.isFilterMales())
        {
            Iterator<String> it = filteredEvents.keySet().iterator();

            while (it.hasNext()) {
                String key = it.next();
                Event event = filteredEvents.get(key);
                Person person = people.get(event.getPerson());
                if (person.getGender().equals("m"))
                {
                    it.remove();
                }
            }
        }

        if (!filter.isFilterFemales())
        {
            Iterator<String> it = filteredEvents.keySet().iterator();

            while (it.hasNext()) {
                String key = it.next();
                Event event = filteredEvents.get(key);
                Person person = people.get(event.getPerson());
                if (person.getGender().equals("f"))
                {
                    it.remove();
                }
            }
        }

        if (!filter.isFilterDadSide())
        {
            String fatherID = currUsersPerson.getFather();
            removeAncestorEvents(fatherID);
        }

        if (!filter.isFilterMomSide())
        {
            String motherID = currUsersPerson.getMother();
            removeAncestorEvents(motherID);
        }
        return filteredEvents;
    }

    private void removeAncestorEvents(String parentID)
    {
        if (parentID != null)
        {
            Iterator<String> it = filteredEvents.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Event event = filteredEvents.get(key);
                if (event.getPerson().equals(parentID))
                {
                    it.remove();
                }
            }

            Person parent = people.get(parentID);
            removeAncestorEvents(parent.getFather());
            removeAncestorEvents(parent.getMother());
        }
    }

    public Event getEarliestEvent(String personID)
    {
        ArrayList<Event> newPersonEvents = new ArrayList<>();

        Set<String> IDs = getFilteredEvents().keySet();
        for (String ID : IDs)
        {
            Event e = getFilteredEvents().get(ID);
            if (e.getPerson().equals(personID))
            {
                newPersonEvents.add(e);
            }
        }

        if (newPersonEvents.size() != 0)
        {
            Collections.sort(newPersonEvents);
            return newPersonEvents.get(0);
        }
        else
        {
            return null;
        }
    }

    public Marker getMarker(Event event)
    {
        if (mapEvents != null)
        {
            for (Map.Entry<Marker, Event> entry : mapEvents.entrySet()) {
                if (Objects.equals(event, entry.getValue())) {
                    return entry.getKey();
                }
            }
            return null;
        }
        else
        {
            return null;
        }
    }

    public Event getPersonEventByType(String eventType, Person p)
    {
        Set<String> keys = filteredEvents.keySet();
        for (String key : keys)
        {
            if      (filteredEvents.get(key).getEventType().equals(eventType) &&
                     filteredEvents.get(key).getPerson().equals(p.getPersonID()))
            {
                return filteredEvents.get(key);
            }
        }

        return null;
    }

    public Person getSelectedPerson()
    {
        Set<Person> key = personEvents.keySet();
        for (Person p : key)
        {
            return p;
        }
        return null;
    }

    public String getRelation(Person relative)
    {
        Person selectedPerson = getSelectedPerson();

        if (selectedPerson != null && relative != null)
        {
            if (selectedPerson.getFather() != null && selectedPerson.getFather().equals(relative.getPersonID()))
            {
                return "Father";
            }
            else if (selectedPerson.getMother() != null && selectedPerson.getMother().equals(relative.getPersonID()))
            {
                return "Mother";
            }
            else if (selectedPerson.getSpouse() != null && selectedPerson.getSpouse().equals(relative.getPersonID()))
            {
                return "Spouse";
            }
            else
            {
                if ((relative.getFather() != null && relative.getFather().equals(selectedPerson.getPersonID())) ||
                        (relative.getMother() != null && relative.getMother().equals(selectedPerson.getPersonID())))
                {
                    return "Child";
                }
                else
                {
                    return null;
                }
            }
        }
        return null;
    }

    public Person getChild()
    {
        Person parent = getSelectedPerson();
        Person child = null;

        Set<String> keys = getPeople().keySet();
        for (String ID : keys)
        {
            Person potentialChild = getPeople().get(ID);
            if (potentialChild.getFather() != null)
            {
                if (potentialChild.getFather().equals(parent.getPersonID()))
                {
                    child = potentialChild;
                }
            }
            if (potentialChild.getMother() != null)
            {
                if (potentialChild.getMother().equals(parent.getPersonID()))
                {
                    child = potentialChild;
                }
            }
        }
        return child;
    }

    public ArrayList<Event> addPersonEvents(Person p)
    {
        ArrayList<Event> newPersonEvents = new ArrayList<>();

        Set<String> IDs = getFilteredEvents().keySet();
        for (String ID : IDs)
        {
            Event e = getFilteredEvents().get(ID);
            if (e.getPerson().equals(p.getPersonID()))
            {
                newPersonEvents.add(e);
            }
        }

        Collections.sort(newPersonEvents);
        return newPersonEvents;
    }

    public int getColor(String color)
    {
        int COLOR;
        color = color.toLowerCase();
        switch (color) {
            case "red":
                COLOR = settings.getRed();
                break;
            case "green":
                COLOR = settings.getGreen();
                break;
            case "blue":
                COLOR = settings.getBlue();
                break;
            case "black":
                COLOR = settings.getBlack();
                break;
            case "cyan":
                COLOR = settings.getCyan();
                break;
            case "yellow":
                COLOR = settings.getYellow();
                break;
            case "white":
                COLOR = settings.getWhite();
                break;
            case "magenta":
                COLOR = settings.getMagenta();
                break;
            case "gray":
                COLOR = settings.getGray();
                break;
            default:
                COLOR = 0;
        }
        return COLOR;
    }

    public int getMapType(String mapType)
    {
        int type;
        mapType = mapType.toLowerCase();
        switch (mapType) {
            case "normal":
                type = settings.getNormal();
                break;
            case "satellite":
                type = settings.getSatellite();
                break;
            case "terrain":
                type = settings.getTerrain();
                break;
            case "hybrid":
                type = settings.getHybrid();
                break;
            default:
                type = 1;
        }
        return type;
    }

    public void reset()
    {
        this.singleton = null;
    }

    public List<Object> getSearchFilteredObjects(String query)
    {
        List<Object> objects = new ArrayList<>();

        getPeople().put(currUsersPerson.getPersonID(), currUsersPerson);
        HashMap<String, Person> filteredPeople = getFilteredPeople();
       // "fastfood".indexOf("food")

        int index = -1;
        Set<String> peopleKeys = filteredPeople.keySet();
        for (String key : peopleKeys)
        {
            String first_Name = filteredPeople.get(key).getFirstName().toLowerCase();
            index = first_Name.indexOf(query);
            if (index != -1)
            {
                objects.add(filteredPeople.get(key));
            }
            String last_Name = filteredPeople.get(key).getLastName().toLowerCase();
            index = last_Name.indexOf(query);
            if (index != -1)
            {
                if (!objects.contains(filteredPeople.get(key)))
                {
                    objects.add(filteredPeople.get(key));
                }
            }
        }

        Set<String> eventKeys = filteredEvents.keySet();
        for (String key : eventKeys)
        {
            String country = filteredEvents.get(key).getCountry().toLowerCase();
            index = country.indexOf(query);
            if (index != -1)
            {
                objects.add(filteredEvents.get(key));
            }
            String city = filteredEvents.get(key).getCity().toLowerCase();
            index = city.indexOf(query);
            if (index != -1)
            {
                if (!objects.contains(filteredEvents.get(key)))
                {
                    objects.add(filteredEvents.get(key));
                }
            }
            String eventType = filteredEvents.get(key).getEventType().toLowerCase();
            index = eventType.indexOf(query);
            if (index != -1)
            {
                if (!objects.contains(filteredEvents.get(key)))
                {
                    objects.add(filteredEvents.get(key));
                }
            }
            String year = filteredEvents.get(key).getYear();
            index = year.indexOf(query);
            if (index != -1)
            {
                if (!objects.contains(filteredEvents.get(key)))
                {
                    objects.add(filteredEvents.get(key));
                }
            }
        }

        return objects;
    }

    private HashMap<String,Person> getFilteredPeople()
    {
        HashMap<String, Person> filteredPeople = new HashMap<>();

        Set<String> keys = getPeople().keySet();
        for (String key : keys)
        {
            Person p = getPeople().get(key);
            Set<String> events = filteredEvents.keySet();
            for (String eventID: events)
            {
                if (filteredEvents.get(eventID).getPerson().equals(p.getPersonID()))
                {
                    filteredPeople.put(p.getPersonID(), p);
                }
            }
        }

        return filteredPeople;
    }

    public HashMap<String, Float> getFilteredEventTypes()
    {
        MarkerColor markerColors = new MarkerColor();
        HashMap<String, Float> eventColors = new HashMap<>();
        Set<String> keys = getFilteredEvents().keySet();
        int i = 0;
        for (String key : keys)
        {
            Event event = getEvents().get(key);
            if (!eventColors.containsKey(event.getEventType()))
            {
                eventColors.put(event.getEventType(), markerColors.colors.get(i).floatValue());
                i++;
            }
        }

        return eventColors;
    }







    public Map<String, Person> getPeople() {
        return people;
    }

    public void setPeople(HashMap<String, Person> people) {
        this.people = people;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(HashMap<String, Event> events) {
        this.events = events;
    }

    public Map<Person, ArrayList<Event>> getPersonEvents() {
        return personEvents;
    }

    public void setPersonEvents(Map<Person, ArrayList<Event>> personEvents) {
        this.personEvents = personEvents;
    }

    public AuthToken getCurrAuthToken() {
        return currAuthToken;
    }

    public void setCurrAuthToken(AuthToken currAuthToken) {
        this.currAuthToken = currAuthToken;
    }

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User currUser) {
        this.currUser = currUser;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Person getCurrUsersPerson() {
        return currUsersPerson;
    }

    public void setCurrUsersPerson(Person currUsersPerson) {
        this.currUsersPerson = currUsersPerson;
    }

    public RegisterResult getRegisterResult() {
        return registerResult;
    }

    public void setRegisterResult(RegisterResult registerResult) {
        this.registerResult = registerResult;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public EventsResult getEventsResult() {
        return eventsResult;
    }

    public void setEventsResult(EventsResult eventsResult) {
        this.eventsResult = eventsResult;
    }

    public PersonsResult getPersonsResult() {
        return personsResult;
    }

    public void setPersonsResult(PersonsResult personsResult) {
        this.personsResult = personsResult;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public HashMap<Marker, Event> getMapEvents() {
        return mapEvents;
    }

    public void setMapEvents(HashMap<Marker, Event> mapEvents) {
        this.mapEvents = mapEvents;
    }

    public familyMapFragment getMainFamilyMapFragment() {
        return mainFamilyMapFragment;
    }

    public void setMainFamilyMapFragment(familyMapFragment mapFragment) {
        this.mainFamilyMapFragment = mapFragment;
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public boolean isFamilyExpanded() {
        return familyExpanded;
    }

    public void setFamilyExpanded(boolean familyExpanded) {
        this.familyExpanded = familyExpanded;
    }

    public boolean isLifeEventsExpanded() {
        return lifeEventsExpanded;
    }

    public void setLifeEventsExpanded(boolean lifeEventsExpanded) {
        this.lifeEventsExpanded = lifeEventsExpanded;
    }

    public Event getMainEvent() {
        return mainEvent;
    }

    public void setMainEvent(Event firstEvent) {
        this.mainEvent = firstEvent;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public int getStorySpinnerPosition() {
        return storySpinnerPosition;
    }

    public void setStorySpinnerPosition(int storySpinnerPosition) {
        this.storySpinnerPosition = storySpinnerPosition;
    }

    public int getAncestorSpinnerPosition() {
        return ancestorSpinnerPosition;
    }

    public void setAncestorSpinnerPosition(int ancestorSpinnerPosition) {
        this.ancestorSpinnerPosition = ancestorSpinnerPosition;
    }

    public int getSpouseSpinnerPosition() {
        return spouseSpinnerPosition;
    }

    public void setSpouseSpinnerPosition(int spouseSpinnerPosition) {
        this.spouseSpinnerPosition = spouseSpinnerPosition;
    }

    public int getMapTypeSpinnerPosition() {
        return mapTypeSpinnerPosition;
    }

    public void setMapTypeSpinnerPosition(int mapTypeSpinnerPosition) {
        this.mapTypeSpinnerPosition = mapTypeSpinnerPosition;
    }

    public HashMap<String, Boolean> getFilterEventTypes() {
        return filterEventTypes;
    }

    public void setFilterEventTypes(HashMap<String, Boolean> filterEventTypes) {
        this.filterEventTypes = filterEventTypes;
    }
}
