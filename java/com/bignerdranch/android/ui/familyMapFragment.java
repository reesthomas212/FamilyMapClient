package com.bignerdranch.android.ui;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import MODELS.Event;
import MODELS.Person;
import model.Model;


public class familyMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Model model = Model.getSingleton();
    HashMap<String, Event> filtEvents = new HashMap<>();
    private LinkedList<Polyline> polylines;
    private int lineWidth = 15;

    private Drawable searchIcon;
    private Drawable filterIcon;
    private Drawable settingsIcon;
    private Drawable androidIcon;
    private ImageView detailsIcon;
    private Marker mMarker;
    private TextView firstLine;
    private TextView lastLine;
    private Drawable maleIcon;
    private Drawable femaleIcon;
    private LinearLayout displayLayout;
    private MapFragment mapFragment;
    private HashMap<String, Float> eventMarkerColors;


    public static familyMapFragment newInstance(String eventID) {

        Bundle args = new Bundle();
        args.putString("eventID", eventID);

        familyMapFragment fragment = new familyMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mapFragment = MapFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.mapFrameLayout, mapFragment).commit();
        model.createMaps();

        if (model.getSelectedEvent() != null)
        {
            if (getActivity() instanceof MainActivity)
            {
                model.setSelectedEvent(model.getMainEvent());
                setHasOptionsMenu(true);
            }
            else
            {
                setHasOptionsMenu(false);
            }
        }
        else
        {
            setHasOptionsMenu(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        androidIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android)
                .colorRes(R.color.androidGreen).sizeDp(60);
        maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                .colorRes(R.color.maleBlue).sizeDp(50);
        femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                .colorRes(R.color.femalePink).sizeDp(50);

        detailsIcon = v.findViewById(R.id.icon_view_mapFragment);
        detailsIcon.setImageDrawable(androidIcon);

        firstLine = v.findViewById(R.id.top_text_view);
        lastLine = v.findViewById(R.id.bottom_text_view);

        displayLayout = v.findViewById(R.id.display_layout);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);

        searchIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                .colorRes(R.color.white).sizeDp(40);
        settingsIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                .colorRes(R.color.white).sizeDp(40);
        filterIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_filter)
                .colorRes(R.color.white).sizeDp(40);

        menu.findItem(R.id.search_icon).setIcon(searchIcon);
        menu.findItem(R.id.filter_icon).setIcon(filterIcon);
        menu.findItem(R.id.settings_icon).setIcon(settingsIcon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int temp = item.getItemId();
        if (temp == R.id.search_icon)
        {
            //mToast.makeText(getActivity(), "Search!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        }
        else if (temp == R.id.filter_icon)
        {
            //mToast3.makeText(getActivity(), "Filter!", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(getActivity(), FilterActivity.class);
            startActivity(intent2);
        }
        else if (temp == R.id.settings_icon)
        {
            //mToast2.makeText(getActivity(), "Settings!", Toast.LENGTH_SHORT).show();
            Intent intent3 = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent3);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        polylines = new LinkedList<>();
        mMap = googleMap;
        mMap.clear();
        mMap.setOnMarkerClickListener(new MapMarker());
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(model.getMapType(model.getSettings().getMapType()));

        filtEvents = model.getFilteredEvents();
        eventMarkerColors = model.getFilteredEventTypes();
        model.setMapEvents(new HashMap<Marker, Event>());

        Set<String> keys = filtEvents.keySet();
        for (String key : keys)
        {
            Event event = model.getEvents().get(key);
            createMarkerForEvent(event);
        }
        //determine what to do if selectedEvent is null or not null
        if (model.getSelectedEvent() != null)
        {
            //check if selectedEvent is in filtEvents
            Event e = model.getSelectedEvent();
            if (filtEvents.containsValue(e))
            {
                connectLines(e);
                LatLng center = new LatLng(e.getLatitude(), e.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
                updateMarkerInfo(e);
            }
            else
            {
                model.setSelectedEvent(null);
                displayDefaultWidgets();
            }
        }
        else
        {
            displayDefaultWidgets();
        }

        //determine which markers should be visible
        Set<Marker> markers = model.getMapEvents().keySet();
        for (Marker m : markers)
        {
            Event event = model.getMapEvents().get(m);
            if (filtEvents.containsValue(event))
            {
                m.setVisible(true);
            }
            else
            {
                m.setVisible(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
    }

    private void createMarkerForEvent(Event event)
    {
        Person p = model.getPeople().get(event.getPerson());
        LatLng location = new LatLng(event.getLatitude(), event.getLongitude());

        mMarker = mMap.addMarker(new MarkerOptions().position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(eventMarkerColors.get(event.getEventType())))
                .title(p.getFirstName() + " " + p.getLastName()));
        model.getMapEvents().put(mMarker, event);
    }

    public class MapMarker extends android.support.v4.app.FragmentActivity
            implements GoogleMap.OnMarkerClickListener
    {

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (model.getMapEvents() != null)
            {
                final Event event = model.getMapEvents().get(marker);
                model.setSelectedEvent(event);
                if (getActivity() instanceof MainActivity)
                {
                    model.setMainEvent(event);
                }
                if (event != null)
                {
                    updateMarkerInfo(event);
                }
                connectLines(event);
                displayLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startPersonActivity(event);
                    }
                });
            }
            return false;
        }
    }

    private void connectLines(Event e)
    {
        clearLines();
        if (model.getSettings().isStoryLineActive())
        {
            connectStoryLines(e);
        }
        if (model.getSettings().isAncestorLineActive())
        {
            connectAncestorLines(e);
        }
        if (model.getSettings().isSpouseLineActive())
        {
            connectSpouseLine(e);
        }
    }

    private void connectStoryLines(Event markerEvent)
    {
        Person selectedPerson = model.getPeople().get(markerEvent.getPerson());

        ArrayList<Event> personEvents = model.addPersonEvents(selectedPerson);

        for (int i = 0; i < personEvents.size()-1; i++)
        {
            Marker m1 = model.getMarker(personEvents.get(i));
            Marker m2 = model.getMarker(personEvents.get(i+1));

            drawLine(m1, m2, model.getColor(model.getSettings().getStoryLineColor()));
        }
    }

    private void connectAncestorLines(Event selectedEvent)
    {
        Person selectedPerson = model.getPeople().get(selectedEvent.getPerson());
        Event fatherEvent = model.getEarliestEvent(selectedPerson.getFather());
        Event motherEvent = model.getEarliestEvent(selectedPerson.getMother());

        lineWidth = lineWidth - 3;

        if (fatherEvent != null)
        {
            Marker m1 = model.getMarker(selectedEvent);
            Marker m2 = model.getMarker(fatherEvent);

            drawLine(m1, m2, model.getColor(model.getSettings().getAncestorLineColor()));
            connectAncestorLines(fatherEvent);
        }

        if (motherEvent != null)
        {
            Marker m1 = model.getMarker(selectedEvent);
            Marker m2 = model.getMarker(motherEvent);

            drawLine(m1, m2, model.getColor(model.getSettings().getAncestorLineColor()));
            connectAncestorLines(motherEvent);
        }
        lineWidth = lineWidth + 3;
    }

    private void connectSpouseLine(Event markerEvent)
    {
        Person selectedPerson = model.getPeople().get(markerEvent.getPerson());
        if (selectedPerson.getSpouse() != null)
        {
            Person spouse = model.getPeople().get(selectedPerson.getSpouse());
            Event connectingEvent = model.getEarliestEvent(spouse.getPersonID());

            if (connectingEvent != null)
            {
                Marker m1 = model.getMarker(markerEvent);
                Marker m2 = model.getMarker(connectingEvent);

                drawLine(m1, m2, model.getColor(model.getSettings().getSpouseLineColor()));
            }
        }
    }

    private void drawLine(Marker m1, Marker m2, int color)
    {
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .add(m1.getPosition(), m2.getPosition())
                .width(lineWidth)
                .color(color));

        polylines.add(polyline);
    }

    private void clearLines()
    {
        int size = polylines.size();
        for (int i = 0; i < size; i++)
        {
            polylines.get(0).remove();
            polylines.removeFirst();
        }

        polylines = new LinkedList<>();
    }

    private void startPersonActivity(Event event)
    {
        Person p = model.getPeople().get(event.getPerson());
        ArrayList<Event> personEvents = model.addPersonEvents(p);
        Collections.sort(personEvents);

        HashMap<Person, ArrayList<Event>> newPersonEventMap = new HashMap<>();
        newPersonEventMap.put(p, personEvents);
        model.setPersonEvents(newPersonEventMap);

        Intent intent = new Intent(getActivity(), PersonActivity.class);
        startActivity(intent);
    }

    private void updateMarkerInfo(Event event)
    {
        Person p = model.getPeople().get(event.getPerson());
        firstLine.setText(p.getFirstName() + " " + p.getLastName());

        lastLine.setText(event.getEventType() + ": " +
                event.getCity() + ", " +
                event.getCountry() + " (" +
                event.getYear() + ")");

        if (p.getGender().equals("m"))
        {
            detailsIcon.setImageDrawable(maleIcon);
        }
        else if (p.getGender().equals("f"))
        {
            detailsIcon.setImageDrawable(femaleIcon);
        }
    }

    private void displayDefaultWidgets()
    {
        detailsIcon.setImageDrawable(androidIcon);
        firstLine.setText("Click on a marker");
        lastLine.setText("to see event details");
    }
}