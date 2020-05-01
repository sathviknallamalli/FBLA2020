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

public class ContactUs extends Fragment {

    public ContactUs() {

    }


    View view;

    Button contactsend;
    EditText contactname, contactemail, contactmessage;

    String chapterid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.contactus, container, false);
        //set the title of the screen
        getActivity().setTitle("Contact Us");

        contactsend = view.findViewById(R.id.contactsend);
        contactname = view.findViewById(R.id.contactname);
        contactemail = view.findViewById(R.id.contactemail);
        contactmessage = view.findViewById(R.id.contactmessage);

        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        contactsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactemail.getText().toString().isEmpty()||contactmessage.getText().toString().isEmpty()
                ||contactname.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(),"Missing field(s)", Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> emails = collectemails((Map<String, Object>) dataSnapshot.child("Advisers").getValue(), "email");

                            for (int i = 0; i < emails.size(); i++) {
                                String subject = "New Contact Message";
                                String message = "New contact message from the FBLA Chapters App.\n\nName: " + contactname.getText().toString()
                                        + "\n\nEmail: " + contactemail.getText().toString() + "\n\nMessage: " + contactmessage.getText().toString();
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