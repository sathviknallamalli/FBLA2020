package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;


/**
 * Created by sathv on 6/1/2018.
 */

public class JoinFBLA extends Fragment {

    public JoinFBLA() {

    }


    View view;

    Button joinjoin;
    EditText joinname, joinemail, joinhear, joinwhy;

    String chapterid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.joinfbla, container, false);
        //set the title of the screen
        getActivity().setTitle("Join FBLA");
        setHasOptionsMenu(true);

        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        joinjoin = view.findViewById(R.id.joinjoin);
        joinname = view.findViewById(R.id.joinname);
        joinemail = view.findViewById(R.id.joinemail);
        joinwhy = view.findViewById(R.id.joinwhy);
        joinhear = view.findViewById(R.id.joinhear);

        joinjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joinemail.getText().toString().isEmpty()||joinemail.getText().toString().isEmpty()
                || joinwhy.getText().toString().isEmpty() || joinhear.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(), "Missing field(s)", Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> emails = collectemails((Map<String, Object>) dataSnapshot.child("Advisers").getValue(), "email");

                            for (int i = 0; i < emails.size(); i++) {
                                String subject = "New Join FBLA Request";
                                String message = "New join request from the FBLA Chapters App.\n\nName: " + joinname.getText().toString()
                                        + "\n\nEmail: " + joinemail.getText().toString() + "\n\nInterest: " + joinwhy.getText().toString()
                                        + "\n\nHow you heard: " + joinhear.getText().toString();
                                SendMail sm = new SendMail(view.getContext(), emails.get(i), subject, message);
                                sm.execute();
                            }
                            Toast.makeText(view.getContext(), "Your message was sent!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


        return view;
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

    private ArrayList<String> collectemails(Map<String, Object> users, String whatyouwant) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            if(!entry.getKey().toString().equals("device_tokens")){
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list

                if (singleUser != null) {
                    information.add((String) singleUser.get(whatyouwant));
                }
            }
            //Get user map

        }

        return information;
    }

}


