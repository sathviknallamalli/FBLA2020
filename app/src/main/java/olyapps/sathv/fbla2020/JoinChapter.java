package olyapps.sathv.fbla2020;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class JoinChapter extends AppCompatActivity {

    TextView error;
    Button continueid;
    TextView enteredchapid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_chapter);

        error=findViewById(R.id.errorid);
        continueid = findViewById(R.id.continueid);
        enteredchapid = findViewById(R.id.enteredchapid);

        continueid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DatabaseReference chapterID = FirebaseDatabase.getInstance().getReference().child("Chapters");
                chapterID.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(final com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(enteredchapid.getText().toString())) {
                            error.setVisibility(View.VISIBLE);
                            error.setText("This chapter ID does not exist. Please try again");
                        }else{
                            error.setVisibility(View.INVISIBLE);

                            AlertDialog moveon = new AlertDialog.Builder(JoinChapter.this)
                                    .setTitle("Confirm Chapter Details")
                                    .setMessage("Chapter ID: "+ dataSnapshot.child(enteredchapid.getText().toString()).child("ID").getValue()
                                    + "\nChapter Name: " + dataSnapshot.child(enteredchapid.getText().toString()).child("ChapterName").getValue()
                                            + "\nAdviser Email: " + dataSnapshot.child(enteredchapid.getText().toString()).child("AdviserEmail").getValue()
                                    + "\nState: " + dataSnapshot.child(enteredchapid.getText().toString()).child("State").getValue() +
                                            "\nZip code: " + dataSnapshot.child(enteredchapid.getText().toString()).child("Zip").getValue())

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {



                                            //CHANGE THIS TO INTENT STRINGS
                                            //GET CHAPTER ID



                                            Intent intent = new Intent(getApplicationContext(), MemberRole.class);
                                            intent.putExtra("membercode", dataSnapshot.child(enteredchapid.getText().toString()).child("MemberCode").getValue().toString());
                                            intent.putExtra("officercode", dataSnapshot.child(enteredchapid.getText().toString()).child("OfficerCode").getValue().toString());
                                            intent.putExtra("advisercode", dataSnapshot.child(enteredchapid.getText().toString()).child("AdviserCode").getValue().toString());
                                            intent.putExtra("chapterid", enteredchapid.getText().toString());

                                            startActivity(intent);
                                            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                                            finish();

                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();




                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });


    }
}
