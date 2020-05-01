package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Approvals extends AppCompatActivity {
    String yourname, role, firstname, lastname;
    FirebaseAuth mAuth;
    ListView approvallist;
    AnApprovalAdapter adapter;
    ArrayList<AnApproval> approvals;
    ArrayAdapter<CharSequence> spinnneradapter;

    ArrayList<String> names = new ArrayList<>();
String chapterid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvals);



        ListView list = findViewById(R.id.listofapprovals);

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        yourname = sp.getString(getString(R.string.fname), "") + " " + sp.getString(getString(R.string.lname), "");
        role = sp.getString(getString(R.string.role), "");
        firstname = sp.getString(getString(R.string.fname), "fname");
        lastname = sp.getString(getString(R.string.lname), "lname");

        DatabaseReference prema = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapterid).child("TeamEvents");

        setTitle("Approvals");
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        approvallist = findViewById(R.id.listofapprovals);

        spinnneradapter = ArrayAdapter.createFromResource(this, R.array.statusoptions, android.R.layout.simple_spinner_item);
        spinnneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mAuth = FirebaseAuth.getInstance();
        prema.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                approvals = new ArrayList<>();
                String val = "";
                String key = "";
                String dskey = "";

                names.clear();

                if(role.equals("Adviser")){
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String builder = "";
                        dskey = ds.getKey();
                        for (DataSnapshot right : ds.getChildren()) {
                            String name = right.getValue().toString();
                            String important = name.charAt(name.length() - 1) + "";
                            name = name.substring(0, name.length() - 1);


                            if (important.equals("0")) {
                                important = "Not approved";
                            } else {
                                important = "Approved";
                            }
                            if (!right.getKey().toString().equals("AdviserStatus")) {
                                builder = builder + name + "'s status: " + important + "\n\n";
                                names.add(name);
                            }else{
                                val = right.getValue().toString();
                            }
                        }

                        approvals.add(new AnApproval(ds.getKey().toString(),
                                "The Adviser status is " + ds.child("AdviserStatus").getValue().toString()
                                        + "\n\n" + builder + "Click here to edit your approval status"));

                    }
                }

                else{
                    Log.d("DARBAR", "else");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot second : ds.getChildren()) {
                            //this means its your events
                            dskey = ds.getKey();


                            if (second.getValue().toString().contains(yourname)) {

                                val = second.getValue().toString();
                                key = second.getKey().toString();


                                String builder = "";
                                for (DataSnapshot right : ds.getChildren()) {
                                    String name = right.getValue().toString();
                                    String important = name.charAt(name.length() - 1) + "";
                                    name = name.substring(0, name.length() - 1);


                                    if (important.equals("0")) {
                                        important = "Not approved";
                                    } else {
                                        important = "Approved";
                                    }
                                    if (!right.getKey().toString().equals("AdviserStatus")) {
                                        builder = builder + name + "'s status: " + important + "\n\n";
                                        names.add(name);
                                    }
                                }

                                approvals.add(new AnApproval(ds.getKey().toString(),
                                        "The Adviser status is " + ds.child("AdviserStatus").getValue().toString()
                                                + "\n\n" + builder + "Click here to edit your approval status"));


                            }


                        }
                    }
                }




                adapter = new AnApprovalAdapter(Approvals.this, R.layout.perapproval, approvals);
                approvallist.setAdapter(adapter);


                if(role.equals("Adviser")){
                    final String finalVal = val;
                    final String finalDskey = dskey;
                    final String finalKey = key;
                    approvallist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(Approvals.this);
                            builder.setTitle("Update Adviser Status");

                            LayoutInflater inflater = Approvals.this.getLayoutInflater();
                            final View v = inflater.inflate(R.layout.custom_alert, null);
                            builder.setView(v);

                            final Spinner statusspinner = v.findViewById(R.id.statusspinner);

                            statusspinner.setAdapter(spinnneradapter);

                            if (finalVal.contains("0")) {
                                statusspinner.setSelection(0);
                            } else if (finalVal.contains("1")) {
                                statusspinner.setSelection(1);
                            }

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final int pos = statusspinner.getSelectedItemPosition();
                                    DatabaseReference fd = FirebaseDatabase.getInstance().getReference().
                                            child("Chapters").child(chapterid).child("TeamEvents")
                                            .child(finalDskey).child("AdviserStatus");
                                    fd.setValue(pos+ "");

                                    //ADVISOR NOTIFICATION
                                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                                            child("Chapters").child(chapterid).child("Users");
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ArrayList<String> user_device_tokens = new ArrayList<>();
                                            user_device_tokens = userDts((Map<String, Object>) dataSnapshot.getValue(),
                                                    names);

                                            DatabaseReference databaseReference1 =
                                                    FirebaseDatabase.getInstance().getReference().child("notificationUpdateStatus");

                                            databaseReference1.child("a8042437-91e7-4e57-b240-55de8d33d213")
                                                    .child("DeviceTokens").setValue("Adviser" + "SEPERATOR" +
                                                    user_device_tokens.toString() + "SEPERATOR" + finalDskey + "SEPERATOR" + pos);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();

                        }
                    });
                }else{
                    Log.d("DARBAR", "elselcick");
                    final String finalVal = val;
                    final String finalDskey = dskey;
                    final String finalKey = key;
                    approvallist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(Approvals.this);
                            builder.setTitle("Update Your Status");

                            LayoutInflater inflater = Approvals.this.getLayoutInflater();
                            final View v = inflater.inflate(R.layout.custom_alert, null);
                            builder.setView(v);

                            final Spinner statusspinner = v.findViewById(R.id.statusspinner);

                            statusspinner.setAdapter(spinnneradapter);

                            if (finalVal.contains("0")) {
                                statusspinner.setSelection(0);
                            } else if (finalVal.contains("1")) {
                                statusspinner.setSelection(1);
                            }

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final int pos = statusspinner.getSelectedItemPosition();
                                    DatabaseReference fd = FirebaseDatabase.getInstance().getReference().
                                            child("Chapters").child(chapterid).child("TeamEvents")
                                            .child(finalDskey).child(finalKey);
                                    fd.setValue(yourname + "" + pos);


                                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                                            child("Chapters").child(chapterid).child("Users");
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ArrayList<String> user_device_tokens = new ArrayList<>();
                                            user_device_tokens = userDts((Map<String, Object>) dataSnapshot.getValue(),
                                                    names);

                                            DatabaseReference databaseReference1 =
                                                    FirebaseDatabase.getInstance().getReference().child("notificationUpdateStatus");

                                            databaseReference1.child("a8042437-91e7-4e57-b240-55de8d33d213")
                                                    .child("DeviceTokens").setValue(yourname + "SEPERATOR" +
                                                    user_device_tokens.toString() + "SEPERATOR" + finalDskey + "SEPERATOR" + pos);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStackImmediate();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStackImmediate();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }

    private ArrayList<String> userDts(Map<String, Object> users, ArrayList<String> names) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list


            String s = singleUser.get("fname").toString() + " " + singleUser.get("lname").toString();

            for (int i = 0; i < names.size(); i++) {
                if(names.get(i).equals(s)){
                    if(singleUser.get("device_token") != null){
                        information.add((String) singleUser.get("device_token").toString());
                    }
                }
            }

        }

        return information;
    }
}
