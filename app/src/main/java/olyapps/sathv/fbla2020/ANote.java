package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ANote extends AppCompatActivity {

    EditText notetext;
    FirebaseAuth mauth;

    String chapterid, role;
    String notename = "";
    String fname,lname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anote);


        Intent intent = getIntent();
        notename = intent.getExtras().getString("notename");

        if(notename.equals("sendNotif")){
            setTitle("Send Notification");
        }else{
            setTitle("New Note");
        }

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");
        fname = sp.getString(getString(R.string.fname), "fname");
        lname = sp.getString(getString(R.string.lname), "lname");

        notetext = findViewById(R.id.notetext);
        notetext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        notetext.setSingleLine(false);
        notetext.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        mauth = FirebaseAuth.getInstance();


        String rolechild = "";
        if (role.equals("Adviser")) {
            rolechild = "Advisers";
        } else if (role.equals("Member") || role.equals("Officer")) {
            rolechild = "Users";
        }

        DatabaseReference getnote = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapterid).child(rolechild).child(mauth.getCurrentUser().getUid())
                .child("Notes");

        getnote.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(notename).exists()) {
                    String text = dataSnapshot.child(notename).getValue().toString();
                    notetext.setText(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sharenote) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String sharebody = "Here is the note I wrote up: \n" +
                    notetext.getText().toString();
            String sharesub = "Take a look at this note!";
            myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
            myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
            startActivity(Intent.createChooser(myIntent, "Share note with"));
        } else if (item.getItemId() == R.id.deletenote) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want delete this note");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    String rolechild = "";
                    if (role.equals("Adviser")) {
                        rolechild = "Advisers";
                    } else if (role.equals("Member") || role.equals("Officer")) {
                        rolechild = "Users";
                    }
                    DatabaseReference fdr = FirebaseDatabase.getInstance().getReference().
                            child("Chapters").child(chapterid).child(rolechild).child(mauth.getCurrentUser().getUid());
                    Intent intent = getIntent();
                    String notename = intent.getExtras().getString("notename");
                    fdr.child("Notes").child(notename).removeValue();

                    getFragmentManager().popBackStackImmediate();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();

        } else if (item.getItemId() == R.id.savenote) {
            if(notename.equals("sendNotif")){
                final String note = notetext.getText().toString();

                final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy  hh:mm:ss");
                        String format = simpleDateFormat.format(new Date());
                        //get dts and send
                        ArrayList<String>
                                dts = collectEventdata((Map<String, Object>) dataSnapshot.child("Users").getValue(), "device_token");

                        String adts = "";

                        for (int i = 0; i < dts.size(); i++) {
                            adts += dts.get(i) + ",";
                        }

                        adts += dataSnapshot.child("Advisers").child("device_tokens").getValue().toString();


                        String yourdt = FirebaseInstanceId.getInstance().getToken();


                        adts = adts.replace(yourdt + ",AAA", "");
                        DatabaseReference ds = dr.child("MassMessages").push();
                        ds.child("todevice_tokens").setValue(adts);
                        ds.child("Title").setValue("Mass Message from " + fname);
                        ds.child("Message").setValue(note);
                        ds.child("Timestamp").setValue(format);

                        //create notification in each uid
                        ArrayList<String>
                                uids = collectEventdata((Map<String, Object>) dataSnapshot.child("Users").getValue(), "uid");
                        ArrayList<String>
                                auids = collectEventdata((Map<String, Object>) dataSnapshot.child("Advisers").getValue(), "uid");

                        if(role.equals("Officer")){
                            uids.remove(mauth.getCurrentUser().getUid());
                        }else if(role.equals("Adviser")){
                            auids.remove(mauth.getCurrentUser().getUid());
                        }

                        if(auids.size()!=0){
                            for (int i = 0; i < auids.size(); i++) {
                                DatabaseReference notifdr = dr.child("Advisers").child(auids.get(i)).child("Notifications")
                                        .push();
                                notifdr.child("Title").setValue("Mass Message from " + fname);
                                notifdr.child("Message").setValue(note);
                                notifdr.child("Timestamp").setValue(format);


                            }
                        }

                        for (int i = 0; i < uids.size(); i++) {
                            DatabaseReference notifdr = dr.child("Users").child(uids.get(i)).child("Notifications")
                                    .push();
                            notifdr.child("Title").setValue("Mass Message from " + fname);
                            notifdr.child("Message").setValue(note);
                            notifdr.child("Timestamp").setValue(format);

                        }

                        getFragmentManager().popBackStackImmediate();
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }else{
                String rolechild = "";
                if (role.equals("Adviser")) {
                    rolechild = "Advisers";
                } else if (role.equals("Member") || role.equals("Officer")) {
                    rolechild = "Users";
                }

                DatabaseReference fdr = FirebaseDatabase.getInstance().getReference().
                        child("Chapters").child(chapterid).child(rolechild).child(mauth.getCurrentUser().getUid());
                Intent intent = getIntent();
                String notename = intent.getExtras().getString("notename");

                fdr.child("Notes").child(notename).setValue(notetext.getText().toString());

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            }

        }
        if (item.getItemId() == android.R.id.home) {
            // finish();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.note, menu);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }

        if(notename.equals("sendNotif")){
           menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setTitle("Send");
        }


        return true;
    }

    private ArrayList<String> collectEventdata(Map<String, Object> users, String fieldName) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            if (!entry.getKey().toString().equals("device_tokens")) {
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list

                if (singleUser != null) {
                    information.add((String) singleUser.get(fieldName));
                }
            }

            //Get phone field and append to list

        }

        return information;
    }

}
