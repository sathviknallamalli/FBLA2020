package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;


/**
 * Created by sathv on 6/1/2018.
 */

public class CompetitveEvents extends Fragment {

    public CompetitveEvents() {

    }


    ArrayList<Events> events;

    EventAdapter adapter;

    ListView listofevents;
    View view;

    String[] eventnames = LockScreen.getTils();
    String[] eventtype = LockScreen.getAuths();
    String[] eventcategory = LockScreen.getCass();

    private EventAdapter filteredvaluesadapter;
    static String name, type, category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.competitve_events, container, false);
        //set the title of the screen
        getActivity().setTitle("Events");
        setHasOptionsMenu(true);

        listofevents = (ListView) view.findViewById(R.id.listofevents);
        events = new ArrayList<Events>();


        for (int i = 0; i < eventnames.length; i++) {

            //add checked book to arraylist
            events.add(new Events(eventnames[i], eventcategory[i], eventtype[i]));

        }
        //set adapter
        adapter = new EventAdapter(getActivity().getApplicationContext(), R.layout.event_item, events);
        listofevents.setAdapter(adapter);

        listofevents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View listview, int position, long val) {

                int i = position;

                //based on what book is

                name = eventnames[i];
                type = eventtype[i];
                category = eventcategory[i];


                Intent appInfo = new Intent(view.getContext(), Details.class);
                startActivity(appInfo);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(CompetitveEvents.class.getName()).commit();
                fm.executePendingTransactions();

            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_share) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String sharebody = "Hey, check out this event!";
            String sharesub = "Your subject";
            myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
            myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
            startActivity(Intent.createChooser(myIntent, "Share with"));

        } else if (item.getItemId() == R.id.take_note) {
            Intent newintent = new Intent(view.getContext(), ANote.class);
            newintent.putExtra("notename", "aboutcompetitiveevents");
            startActivity(newintent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.compevents, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }

        resetSearch();
        //inflater.inflate(R.menu.activities, menu);
        MenuItem searchItem = menu.findItem(R.id.searchevents);
        SearchView searchView = (SearchView) searchItem.getActionView();

        ImageView icon = searchView.findViewById(R.id.search_button);
        icon.setColorFilter(R.color.colorPrimary);
        //the listener
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //resetSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    //if the search bar is empty, load the original listview using the resetsearch method that is defined below
                    resetSearch();
                    return false;
                } else {

                    //filtered values of book based on the search
                    final ArrayList<Events> filteredValues = new ArrayList<Events>(events);

                    for (int i = 0; i < events.size(); i++) {

                        //if the title of each book does not contaain the string from the search bar, then delete it from the listview
                        //and remove from the filtered values arraylist
                        if (!(events.get(i).getEventname().toLowerCase()).contains(newText.toLowerCase())) {

                            //remove each field
                            filteredValues.remove(events.get(i));
                            filteredvaluesadapter = new EventAdapter(getActivity().getApplicationContext(), R.layout.event_item, filteredValues);
                        }
                    }

                    listofevents.setAdapter(filteredvaluesadapter);

                    //then declare the onclick listener if a book is clicked after they searched
                    listofevents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int position, long val) {

                            int i = position;

                            //based on what book is

                            name = filteredValues.get(i).getEventname();
                            type = filteredValues.get(i).getEventtype();
                            category = filteredValues.get(i).getEventcategory();


                            Intent appInfo = new Intent(view.getContext(), Details.class);
                            startActivity(appInfo);


                        }
                    });
                    return false;
                }
            }
        };

        //set the appropriate listener and hint for searchbar
        searchView.setOnQueryTextListener(listener);
        searchView.setQueryHint("Search event by yourname");
    }

    //reset search method used when the search bar is empty and the originnal list view is set with orig arrays
    public void resetSearch() {
        //books two was originally set with orig arrays
        adapter = new EventAdapter(getActivity().getApplicationContext(), R.layout.event_item, events);
        listofevents.setAdapter(adapter);

        //onclick listener set similarly to open the book info class
        listofevents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long val) {

                int i = position;

                //based on what book is

                name = eventnames[i];
                type = eventtype[i];
                category = eventcategory[i];


                Intent appInfo = new Intent(view.getContext(), Details.class);
                startActivity(appInfo);

            }
        });


    }
    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }
}


