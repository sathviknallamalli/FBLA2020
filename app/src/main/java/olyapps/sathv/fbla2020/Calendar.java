package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by sathv on 6/1/2018.
 */

public class Calendar extends Fragment {

    public Calendar() {

    }


    TextView nocals;
    View view;
    CompactCalendarView compactCalendarView;
    SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
    SimpleDateFormat dateFormatday = new SimpleDateFormat("MMMM dd", Locale.getDefault());
    TextView monthdisplay;
    ListView eventlist;
    FloatingActionButton fab;
    static String clickedstring;
    final ArrayList<CalendarEvent> newcale = new ArrayList<>();
    String chapid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.calendar, container, false);
        //set the title of the screen
        setHasOptionsMenu(true);
        getActivity().setTitle("Calendar");

        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        monthdisplay = view.findViewById(R.id.monthdisplay);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        eventlist = view.findViewById(R.id.eventlist);
        fab = view.findViewById(R.id.floatingActionButton);
        nocals = view.findViewById(R.id.nocals);

        compactCalendarView.setFirstDayOfWeek(java.util.Calendar.SUNDAY);

        SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String role = sp.getString(getString(R.string.role), "role");

        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapid = spchap.getString("chapterID", "tempid");

        check(new CalendarCallback() {
            @Override
            public void onCallback(Boolean canofficer) {
                if ((canofficer && role.equals("Officer")) || role.equals("Adviser")) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), PopupActivity.class);
                i.putExtra("taginfo", "addnew");
                startActivity(i);
               // getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_in_out);
            }
        });

        String date = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        clickedstring = date;


        monthdisplay.setText(dateFormatMonth.format(java.util.Calendar.getInstance().getTime()));
        newcale.clear();
        compactCalendarView.removeAllEvents();


        String month = monthdisplay.getText().toString();
        month = month.replaceAll("\\d", "");
        month = month.replaceAll(",", "");
        month = month.replaceAll("\\s+", "");

        Log.d("DARBAR", month);

        setupcalendar(month);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");

                String fulldate = simpleDateFormat.format(dateClicked);
                clickedstring = fulldate;


            }

            @Override
            public void onMonthScroll(final Date firstDayOfNewMonth) {
                monthdisplay.setText(dateFormatMonth.format(firstDayOfNewMonth));
                newcale.clear();
                compactCalendarView.removeAllEvents();

                String month = monthdisplay.getText().toString();
                month = month.replaceAll("\\d", "");
                month = month.replaceAll(",", "");
                month = month.replaceAll("\\s+", "");

                setupcalendar(month);
            }
        });

        return view;

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String role = sp.getString(getString(R.string.role), "role");


        check(new CalendarCallback() {
            @Override
            public void onCallback(Boolean canofficer) {
                if ((canofficer && role.equals("Officer")) || role.equals("Adviser")) {
                    inflater.inflate(R.menu.calendarofficer, menu);

                    for (int i = 0; i < menu.size(); i++) {
                        Drawable drawable = menu.getItem(i).getIcon();
                        if (drawable != null) {
                            drawable.mutate();
                            drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
                } else {
                    inflater.inflate(R.menu.calendarother, menu);

                    for (int i = 0; i < menu.size(); i++) {
                        Drawable drawable = menu.getItem(i).getIcon();
                        if (drawable != null) {
                            drawable.mutate();
                            drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.calendarnote) {
            Intent newintent = new Intent(view.getContext(), ANote.class);
            newintent.putExtra("notename", "aboutcalendar");
            startActivity(newintent);
        }

        if (item.getItemId() == R.id.deleteevent) {
            final AlertDialog.Builder anotherbuilder = new AlertDialog.Builder(view.getContext());
            anotherbuilder.setTitle("Delete event");

            final ArrayList<String> mSelectedItems = new ArrayList<>();
            mSelectedItems.clear();
            final String[] names = new String[newcale.size()];
            final int[] whichd = new int[1];

            for (int i = 0; i < newcale.size(); i++) {
                names[i] = newcale.get(i).getCaltitle();
            }

            final String[] chosendelete = new String[1];

            if (names.length == 0) {
                Toast.makeText(view.getContext(), "No events in this month for you to delete", Toast.LENGTH_SHORT).show();
            } else {

                anotherbuilder.setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosendelete[0] = names[which];
                        whichd[0] = which;

                        String month = monthdisplay.getText().toString();
                        String months[] = month.split(",");

                        //REMOVE FROM FIREBASE
                        DatabaseReference deletevent =
                                FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                                        .child("CalendarEvents").child(months[0]).child(chosendelete[0]);

                        //DELTE FROM ARRAYLIST
                        deletevent.removeValue();
                        newcale.remove(whichd);

                        //RELODAD
                        newcale.clear();
                        compactCalendarView.removeAllEvents();

                        setupcalendar(months[0]);
                        dialog.cancel();
                    }
                });

                anotherbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                anotherbuilder.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> collectEventdata(Map<String, Object> users, String fieldName) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            information.add((String) singleUser.get(fieldName));

        }

        return information;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }

    public void check(final CalendarCallback calCallback) {
        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        String chapid = spchap.getString("chapterID", "tempid");

        DatabaseReference calrolecheck = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                .child("Roles");
        calrolecheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("OfficerRules").getValue().toString().contains("1")) {
                    calCallback.onCallback(true);
                } else {
                    calCallback.onCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setupcalendar(String month) {
        DatabaseReference getcalevents = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapid).child("CalendarEvents")
                .child(month);

        getcalevents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> enddates
                            = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "EndDate");

                    ArrayList<String> startdates
                            = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "StartDate");

                    ArrayList<String> endtimes
                            = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "EndTime");

                    ArrayList<String> starttimes
                            = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "StartTime");
                    final ArrayList<String> names
                            = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "Name");


                    for (int i = 0; i < enddates.size(); i++) {
                        String fulldate = "";
                        if (startdates.get(i).toString().equals("none")) {
                            fulldate = "All Day " + starttimes.get(i).toString() +
                                    " to " + endtimes.get(i).toString();
                        } else {
                            fulldate = startdates.get(i).toString() + " " + starttimes.get(i).toString() +
                                    " to " + enddates.get(i).toString() + " " + endtimes.get(i).toString();
                        }

                        newcale.add(new CalendarEvent(names.get(i).toString(), fulldate));

                        try {
                            String startdate = startdates.get(i).toString();
                            DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                            Date date = format.parse(startdate);
                            Long millisepoch = date.getTime();

                            final Event ev1 = new Event(Color.RED, millisepoch, names.get(i).toString());
                            compactCalendarView.addEvent(ev1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }


                    CalendarEventAdapter adapter = new CalendarEventAdapter(view.getContext(),
                            R.layout.percalevent, newcale);
                    eventlist.setAdapter(adapter);
                    nocals.setVisibility(View.INVISIBLE);
                    eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                            String monthname = monthdisplay.getText().toString().replaceAll(",", "");
                            monthname = monthname.replaceAll("\\d", "");
                            monthname = monthname.replaceAll("\\s+", "");


                            Intent i = new Intent(view.getContext(), PopupActivity.class);
                            i.putExtra("taginfo", "viewevent");
                            i.putExtra("eventname", newcale.get(position).getCaltitle());
                            i.putExtra("monthname", monthname);

                            if (newcale.get(position).getCaldate().contains("All Day")) {
                                i.putExtra("alldayornot", "ALLDAY");
                            } else {
                                i.putExtra("alldayornot", "NOT");
                            }

                            startActivity(i);


                            FragmentManager fm = getFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.addToBackStack(Calendar.class.getName()).commit();
                            fm.executePendingTransactions();
                        }
                    });


                } else {
                    nocals.setVisibility(View.VISIBLE);
                    newcale.clear();
                    compactCalendarView.removeAllEvents();
                    CalendarEventAdapter adapter = new CalendarEventAdapter(view.getContext(),
                            R.layout.percalevent, newcale);
                    eventlist.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}