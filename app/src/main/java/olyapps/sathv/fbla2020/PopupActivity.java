package olyapps.sathv.fbla2020;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PopupActivity extends AppCompatActivity {


    Button cancel;
    Spinner reminderspinner;

    ArrayAdapter<CharSequence> adapter;
    TextView uremail;
    TextView starttime, startdate, enddate, endttime;

    int hour_x;
    int minute_y;

    Switch aSwitch;

    Button addnote, cancelnote;

    EditText title, notes;

    ImageButton backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to CALENDAR
            }
        });

        reminderspinner = findViewById(R.id.reminderspinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.reminderoptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderspinner.setAdapter(adapter);
        reminderspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                //parent.getItemAtPosition(position)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        reminderspinner.setSelection(2);

        uremail = findViewById(R.id.uremail);
        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String email = sp.getString(getString(R.string.email), "email");
        uremail.setText(email);

        starttime = findViewById(R.id.starttime);
        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

        startdate = findViewById(R.id.startdate);
        startdate.setText(Calendar.clickedstring);

        enddate = findViewById(R.id.enddate);
        enddate.setText(Calendar.clickedstring);


        endttime = findViewById(R.id.endtime);
        endttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });

        aSwitch = findViewById(R.id.switch1);

        final boolean[] allday = {false};

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    endttime.setText("All day " + Calendar.clickedstring);
                    starttime.setText("All day " + Calendar.clickedstring);

                    startdate.setVisibility(View.INVISIBLE);
                    enddate.setVisibility(View.INVISIBLE);

                    allday[0] = true;
                } else {
                    startdate.setVisibility(View.VISIBLE);
                    enddate.setVisibility(View.VISIBLE);
                    startdate.setText(Calendar.clickedstring);
                    enddate.setText(Calendar.clickedstring);

                    endttime.setText("9:00 AM");
                    starttime.setText("8:00 AM");

                    allday[0] = false;

                }
            }
        });

        title = findViewById(R.id.title);
        notes = findViewById(R.id.customnotes);

        addnote = findViewById(R.id.addevent);
        addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText().toString())) {
                    Toast.makeText(PopupActivity.this, "Event needs title", Toast.LENGTH_SHORT).show();
                }else if (title.getText().toString().contains("SEPERATOR")) {
                    Toast.makeText(PopupActivity.this, "Invalid title name", Toast.LENGTH_SHORT).show();
                } else {

                    if(allday[0]==false){
                        String startdatestr = startdate.getText().toString();
                        startdatestr = startdatestr.replaceAll("\\d", "");
                        startdatestr = startdatestr.replaceAll("\\s+", "");
                        startdatestr = startdatestr.replaceAll(",", "");

                        String titles = title.getText().toString();

                        DatabaseReference addtime = FirebaseDatabase.getInstance().getReference().child("CalendarEvents").
                                child(startdatestr);
                        addtime.child(titles).child("StartDate").setValue(startdate.getText().toString());
                        addtime.child(titles).child("EndDate").setValue(enddate.getText().toString());
                        addtime.child(titles).child("StartTime").setValue(starttime.getText().toString());
                        addtime.child(titles).child("EndTime").setValue(endttime.getText().toString());
                        addtime.child(titles).child("Name").setValue(titles);
                        addtime.child(titles).child("Email").setValue(uremail.getText().toString());
                    }else{
                        String startdatestr = startdate.getText().toString();
                        startdatestr = startdatestr.replaceAll("\\d", "");
                        startdatestr = startdatestr.replaceAll("\\s+", "");
                        startdatestr = startdatestr.replaceAll(",", "");

                        String titles = title.getText().toString();

                        DatabaseReference addtime = FirebaseDatabase.getInstance().getReference().child("CalendarEvents").
                                child(startdatestr);
                        addtime.child(titles).child("StartDate").setValue("none");
                        addtime.child(titles).child("EndDate").setValue("none");
                        addtime.child(titles).child("StartTime").setValue(starttime.getText().toString());
                        addtime.child(titles).child("EndTime").setValue(endttime.getText().toString());
                        addtime.child(titles).child("Name").setValue(titles);
                        addtime.child(titles).child("Email").setValue(uremail.getText().toString());
                    }


                    getFragmentManager().popBackStackImmediate();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    finish();
                }

                if (!TextUtils.isEmpty(notes.getText().toString())) {

                    String startdatestr = startdate.getText().toString();
                    startdatestr = startdatestr.replaceAll("\\d", "");

                    DatabaseReference addtime = FirebaseDatabase.getInstance().getReference().child("CalendarEvents")
                            .child(startdatestr);


                    String titles = title.getText().toString();
                    addtime.child(titles).child("Notes").setValue(notes.getText().toString());
                }
            }
        });


        backbutton = findViewById(R.id.backbutton);

        ImageView ig15 = findViewById(R.id.imageView15);

        Intent intent = getIntent();
        String notename = intent.getExtras().getString("taginfo");
       String titletext = intent.getExtras().getString("eventname");
        String monthname = intent.getExtras().getString("monthname");
        String allornot = intent.getExtras().getString("alldayornot");

        if (notename.equals("viewevent")) {

            endttime.setOnClickListener(null);
            starttime.setOnClickListener(null);

            backbutton.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.INVISIBLE);
            addnote.setVisibility(View.INVISIBLE);

            reminderspinner.setVisibility(View.INVISIBLE);
            reminderspinner.setEnabled(false);
            ig15.setVisibility(View.INVISIBLE);

            title.setEnabled(false);
            title.setKeyListener(null);

            title.setText(titletext);

            if(allornot.equals("ALLDAY")){
                aSwitch.setChecked(true);
                aSwitch.setEnabled(false);

                startdate.setVisibility(View.INVISIBLE);
                enddate.setVisibility(View.INVISIBLE);

                DatabaseReference calinfo = FirebaseDatabase.getInstance().getReference().child("CalendarEvents")
                        .child(monthname).child(titletext);

                calinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String starttimestr = dataSnapshot.child("StartTime").getValue().toString();
                        starttime.setText(starttimestr);

                        String endtimestr = dataSnapshot.child("EndTime").getValue().toString();
                        endttime.setText(endtimestr);

                        String uremaailstr = dataSnapshot.child("Email").getValue().toString();
                        uremail.setText(uremaailstr);

                        if(dataSnapshot.child("Notes").exists()){
                            String notestr = dataSnapshot.child("Notes").getValue().toString();
                            notes.setText(notestr);

                            notes.setEnabled(false);
                            notes.setKeyListener(null);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }else{
                aSwitch.setChecked(false);
                aSwitch.setEnabled(false);


                DatabaseReference calinfo = FirebaseDatabase.getInstance().getReference().child("CalendarEvents")
                        .child(monthname).child(titletext);

                calinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String startdatestr = dataSnapshot.child("StartDate").getValue().toString();
                        startdate.setText(startdatestr);

                        String enddatestr = dataSnapshot.child("EndDate").getValue().toString();
                        enddate.setText(enddatestr);

                        String starttimestr = dataSnapshot.child("StartTime").getValue().toString();
                        starttime.setText(starttimestr);

                        String endtimestr = dataSnapshot.child("EndTime").getValue().toString();
                        endttime.setText(endtimestr);

                        String uremaailstr = dataSnapshot.child("Email").getValue().toString();
                        uremail.setText(uremaailstr);

                        if(dataSnapshot.child("Notes").exists()){
                            String notestr = dataSnapshot.child("Notes").getValue().toString();
                            notes.setText(notestr);

                            notes.setEnabled(false);
                            notes.setKeyListener(null);
                        }else{
                            notes.setVisibility(View.INVISIBLE);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        } else {
            backbutton.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.VISIBLE);
            addnote.setVisibility(View.VISIBLE);

            reminderspinner.setVisibility(View.VISIBLE);
            reminderspinner.setEnabled(true);
            ig15.setVisibility(View.VISIBLE);

            title.setEnabled(true);

            notes.setEnabled(true);
            notes.setVisibility(View.VISIBLE);

            startdate.setVisibility(View.VISIBLE);
            enddate.setVisibility(View.VISIBLE);

            aSwitch.setChecked(false);
            aSwitch.setEnabled(true);

            starttime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(0);
                }
            });
            endttime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(0);
                }
            });
        }

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
            }
        });

        cancelnote = findViewById(R.id.cancel);
        cancelnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            return new TimePickerDialog(PopupActivity.this, kTimePickerListener, hour_x, minute_y, false);
        } else if (id == 1) {
            return new TimePickerDialog(PopupActivity.this, mTimePickerListener, hour_x, minute_y, false);
        }
        return null;
    }

    protected TimePickerDialog.OnTimeSetListener kTimePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    hour_x = hourOfDay;
                    minute_y = minute;
                    String AM_PM = "";
                    String MIN = "";
                    if (hourOfDay < 12) {
                        AM_PM = "AM";
                    } else if (hourOfDay > 12) {
                        hour_x = hourOfDay - 12;
                        AM_PM = "PM";
                    } else {
                        hour_x = 12;
                        AM_PM = "PM";
                    }

                    if (minute < 10) {
                        MIN = "0" + minute;
                    } else {
                        MIN = minute + "";
                    }

                    starttime.setText(hour_x + ":" + MIN + " " + AM_PM);
                }
            };
    protected TimePickerDialog.OnTimeSetListener mTimePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    hour_x = hourOfDay;
                    minute_y = minute;
                    String AM_PM = "";
                    if (hourOfDay < 12) {
                        AM_PM = "AM";
                    } else if (hourOfDay > 12) {
                        hour_x = hourOfDay - 12;
                        AM_PM = "PM";
                    } else {
                        hour_x = 12;
                        AM_PM = "PM";
                    }
                    String MIN = "";
                    if (minute < 10) {
                        MIN = "0" + minute;
                    } else {
                        MIN = minute + "";
                    }

                    endttime.setText(hour_x + ":" + MIN + " " + AM_PM);
                }
            };
}
