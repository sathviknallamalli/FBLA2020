package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterChapter extends AppCompatActivity {

    EditText newid, newzip, newchapter;
    Spinner listofstates;


    Button register;

    ArrayAdapter<CharSequence> adapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chapter);

        setTitle("Register Your Chapter");

        listofstates = findViewById(R.id.newstatechap);
        newid = findViewById(R.id.newid);
        newzip = findViewById(R.id.newzip);
        newchapter = findViewById(R.id.newchapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        LinearLayout regll = findViewById(R.id.regll);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(regll.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        adapter2 = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listofstates.setAdapter(adapter2);
        listofstates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                //parent.getItemAtPosition(position)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        listofstates.setSelection(0);

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newid.getText().toString().isEmpty() || newchapter.getText().toString().isEmpty() || newzip.getText().toString().isEmpty() ||
                        listofstates.getSelectedItem().toString().equals("Select your state chapter")) {
                    Toast.makeText(RegisterChapter.this, "Missing field(s)",
                            Toast.LENGTH_SHORT).show();
                } else {
                    final DatabaseReference chapters = FirebaseDatabase.getInstance().getReference().child("Chapters");
                    chapters.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(newid.getText().toString())) {
                                Toast.makeText(RegisterChapter.this, "This chapter ID already exists",
                                        Toast.LENGTH_SHORT).show();
                            } else {


                                chapters.child(newid.getText().toString()).child("Setup").child("ID").setValue(newid.getText().toString());
                                chapters.child(newid.getText().toString()).child("Setup").child("ChapterName").setValue(newchapter.getText().toString());
                                chapters.child(newid.getText().toString()).child("Setup").child("Zip").setValue(newzip.getText().toString());
                                chapters.child(newid.getText().toString()).child("Setup").child("State").setValue(listofstates.getSelectedItem().toString());

                                SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorchap = spchap.edit();

                                editorchap.putString("chapterID",newid.getText().toString());
                                editorchap.apply();

                                Intent intent = new Intent(getApplicationContext(), AdviserAccount.class);
                                intent.putExtra("chapterid", newid.getText().toString());
                                startActivity(intent);
                                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }
        });


    }


}
