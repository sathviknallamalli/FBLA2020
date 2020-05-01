package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Created by sathv on 6/1/2018.
 */

public class ViewPageAbout extends Fragment {

    public ViewPageAbout() {

    }

    TextView aboutemail, aboutgradyear, aboutusername;

    View view;
    String chapterid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.viewpagesabout, container, false);
        //set the title of the screen

        aboutemail = view.findViewById(R.id.aboutemail);
        aboutgradyear = view.findViewById(R.id.aboutgradyear);
        aboutusername = view.findViewById(R.id.aboutusername);

        String uid = UserDetails.opuid;

        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        DatabaseReference data = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapterid).child("Users").child(uid);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aboutemail.setText(Html.fromHtml("<b>Contact email:</b> " +dataSnapshot.child("email").getValue().toString()));
                aboutgradyear.setText(Html.fromHtml("<b>Graduation Year:</b> " + dataSnapshot.child("graduationyear").getValue().toString()));
                aboutusername.setText(Html.fromHtml("<b>Username:</b> " + dataSnapshot.child("username").getValue().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



       return view;
    }




}