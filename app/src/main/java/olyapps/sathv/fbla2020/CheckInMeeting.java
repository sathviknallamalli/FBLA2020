package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

/**
 * Created by sathv on 6/1/2018.
 */

public class CheckInMeeting extends Fragment {

    public CheckInMeeting() {

    }

    View view;

    String chapterid, role;

    Button checkinmeeting;
    EditText uniqueid;
    TextView errorid;

    FirebaseAuth mauth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.checkinmeeting, container, false);
        //set the title of the screen
        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");

        mauth = FirebaseAuth.getInstance();

        getActivity().setTitle("Join Meeting");

        uniqueid = view.findViewById(R.id.meetingid);
        checkinmeeting = view.findViewById(R.id.checkmeetingid);
        uniqueid.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        errorid = view.findViewById(R.id.errorid);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
        dr.child("Meetings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("Attendees").hasChild(mauth.getCurrentUser().getUid())){
                        checkinmeeting.setEnabled(false);
                        uniqueid.setText(dataSnapshot.child("ID").getValue().toString());
                        uniqueid.setEnabled(false);
                        errorid.setText("You already checked in!");
                        errorid.setVisibility(View.VISIBLE);
                        Toast.makeText(view.getContext(), "You already checked in!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkinmeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkinmeeting.isEnabled()) {
                    Toast.makeText(view.getContext(), "You already checked in!", Toast.LENGTH_SHORT).show();
                } else {
                    final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
                    dr.child("Meetings").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if (uniqueid.getText().toString().equals(dataSnapshot.child("ID").getValue().toString())) {
                                    Toast.makeText(view.getContext(), "Checked in!", Toast.LENGTH_SHORT).show();

                                    errorid.setText("Checked In!");
                                    errorid.setVisibility(View.VISIBLE);
                                    String val = dataSnapshot.child("Attendance").getValue().toString();
                                    int count = Integer.parseInt(val) + 1;

                                    dr.child("Meetings").child("Attendance").setValue(count);
                                    dr.child("Meetings").child("Attendees").child(mauth.getCurrentUser().getUid()).setValue(true);
                                    checkinmeeting.setEnabled(false);
                                    uniqueid.setEnabled(false);
                                } else {
                                    errorid.setVisibility(View.VISIBLE);
                                }
                            }else{
                                errorid.setVisibility(View.VISIBLE);
                            }

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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
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
}