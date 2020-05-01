package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class JoinChapter extends AppCompatActivity {

    TextView error;
    Button continueid;
    EditText enteredchapid;

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

                if(!enteredchapid.getText().toString().isEmpty()){
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
                                        .setMessage("Chapter ID: "+ dataSnapshot.child(enteredchapid.getText().toString()).child("Setup").child("ID").getValue()
                                                + "\nChapter Name: " + dataSnapshot.child(enteredchapid.getText().toString()).child("Setup").child("ChapterName").getValue()
                                                + "\nState: " + dataSnapshot.child(enteredchapid.getText().toString()).child("Setup").child("State").getValue() +
                                                "\nZip code: " + dataSnapshot.child(enteredchapid.getText().toString()).child("Setup").child("Zip").getValue())

                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {



                                                //CHANGE THIS TO INTENT STRINGS
                                                //GET CHAPTER ID

                                                SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editorchap = spchap.edit();

                                                editorchap.putString("chapterID",enteredchapid.getText().toString());
                                                editorchap.apply();

                                                Intent intent = new Intent(getApplicationContext(), MemberRole.class);
                                                intent.putExtra("membercode", dataSnapshot.child(enteredchapid.getText().toString()).child("JoinCodes").child("MemberCode").getValue().toString());
                                                intent.putExtra("officercode", dataSnapshot.child(enteredchapid.getText().toString()).child("JoinCodes").child("OfficerCode").getValue().toString());
                                                intent.putExtra("advisercode", dataSnapshot.child(enteredchapid.getText().toString()).child("JoinCodes").child("AdviserCode").getValue().toString());
                                                intent.putExtra("chapterid", enteredchapid.getText().toString());
                                                intent.putExtra("chapterlogourl", dataSnapshot.child(enteredchapid.getText().toString()).child("Images").child("ChapterLogo").getValue().toString());

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
                }else{
                    Toast.makeText(getApplicationContext(), "Missing field", Toast.LENGTH_SHORT).show();
                }






            }
        });


    }
}
