package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import androidx.annotation.NonNull;


/**
 * Created by sathv on 6/1/2018.
 */

public class Stats extends Fragment {

    public Stats() {

    }


    View view;

    TextView totaltotal, count1, count2, count3, count4;
    TextView onlymember, onlyofficer, onlyadviser;
    TextView totalpost, totalindiv, totalteam;

    String chapterid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.chapstats, container, false);
        //set the title of the screen
        getActivity().setTitle("Chapter Statistics");
        setHasOptionsMenu(true);

        totaltotal = view.findViewById(R.id.total);
        count1 = view.findViewById(R.id.count1);
        count2 = view.findViewById(R.id.count2);
        count3 = view.findViewById(R.id.count3);
        count4 = view.findViewById(R.id.count4);

        onlymember = view.findViewById(R.id.onlymember);
        onlyofficer = view.findViewById(R.id.onlyofficer);
        onlyadviser = view.findViewById(R.id.onlyadviser);

        totalpost = view.findViewById(R.id.totalpost);
        totalindiv = view.findViewById(R.id.totalindiv);
        totalteam = view.findViewById(R.id.totalteam);

        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long total = dataSnapshot.child("Advisers").getChildrenCount() + dataSnapshot.child("Users").getChildrenCount() - 1;
                totaltotal.setText(total + " users");


                int c1 = collectcertainpeople((Map<String, Object>) dataSnapshot.child("Users").getValue(), "graduationyear", "2023");
                count1.setText(c1 + " members");

                int c2 = collectcertainpeople((Map<String, Object>) dataSnapshot.child("Users").getValue(), "graduationyear", "2022");
                count2.setText(c2 + " members");
                int c3 = collectcertainpeople((Map<String, Object>) dataSnapshot.child("Users").getValue(), "graduationyear", "2021");
                count3.setText(c3 + " members");
                int c4 = collectcertainpeople((Map<String, Object>) dataSnapshot.child("Users").getValue(), "graduationyear", "2020");
                count4.setText(c4 + " members");

                int m1 = collectcertainpeople((Map<String, Object>) dataSnapshot.child("Users").getValue(), "role", "Member");
                onlymember.setText(m1 + " users");

                int o1 = collectcertainpeople((Map<String, Object>) dataSnapshot.child("Users").getValue(), "role", "Officer");
                onlyofficer.setText(o1 + " users");

                int a1 = collectcertainpeople((Map<String, Object>) dataSnapshot.child("Users").getValue(), "role", "Adviser");
                onlyadviser.setText(a1 + " users");

                long post = dataSnapshot.child("ActivityStream").getChildrenCount();
                totalpost.setText(post + " posts");

                long uevents = dataSnapshot.child("UserEvents").getChildrenCount();
                totalindiv.setText(uevents + " events");

                long tevents = dataSnapshot.child("TeamEvents").getChildrenCount();
                totalteam.setText(tevents + " events");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_share) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String sharebody = "Hey, check out our stats!";
            String sharesub = "Your subject";
            myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
            myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
            startActivity(Intent.createChooser(myIntent, "Share with"));

        } else if (item.getItemId() == R.id.take_note) {
            Intent newintent = new Intent(view.getContext(), ANote.class);
            newintent.putExtra("notename", "aboutstatistics");
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


    }

    //reset search method used when the search bar is empty and the originnal list view is set with orig arrays

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

    private int collectcertainpeople(Map<String, Object> users, String fieldName, String seperator) {
        int count = 0;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list

            if (singleUser.get(fieldName).toString().equals(seperator)) {
                count++;
            }
        }

        return count;
    }

}


