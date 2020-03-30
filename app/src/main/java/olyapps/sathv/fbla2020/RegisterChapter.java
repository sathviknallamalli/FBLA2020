package olyapps.sathv.fbla2020;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterChapter extends AppCompatActivity {

    EditText newid, newzip, newchapter, newadviemail;
    Spinner listofstates, roleop1, roleop2, roleop3;


    ArrayAdapter<CharSequence> adapter2, roleadapter;

    Button register, addmore;

    private TableLayout mLayout;

    int clickcount = 0;

    ArrayList<Spinner> addedones;
    ArrayList<String> roleselections;
    ArrayList<Boolean> checkselections;
    ArrayList<CheckBox> checkboxes;

    CheckBox check1,check2,check3,check4,check5,check6,check7,check8,check9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chapter);

        setTitle("Register Your Chapter");

        listofstates = findViewById(R.id.newstatechap);

        addedones = new ArrayList<Spinner>();
        roleselections = new ArrayList<String>();
        checkselections = new ArrayList<Boolean>();
        checkboxes = new ArrayList<CheckBox>();

        check1 = findViewById(R.id.checkBox);
        check2 = findViewById(R.id.checkBox2);
        check3 = findViewById(R.id.checkBox3);
        check4 = findViewById(R.id.checkBox4);
        check5 = findViewById(R.id.checkBox5);
        check6 = findViewById(R.id.checkBox6);
        check7 = findViewById(R.id.checkBox7);
        check8 = findViewById(R.id.checkBox8);
        check9 = findViewById(R.id.checkBox9);

        newid = findViewById(R.id.newid);
        newzip = findViewById(R.id.newzip);
        newchapter = findViewById(R.id.newchapter);
        newadviemail = findViewById(R.id.newadviemail);

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

        roleop1 = findViewById(R.id.roleop1);
        roleop2 = findViewById(R.id.roleop2);
        roleop3 = findViewById(R.id.roleop3);

        roleadapter = ArrayAdapter.createFromResource(this, R.array.setuproles, android.R.layout.simple_spinner_item);
        roleadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleop1.setAdapter(roleadapter);
        roleop2.setAdapter(roleadapter);
        roleop3.setAdapter(roleadapter);
        roleop1.setSelection(0);
        roleop2.setSelection(0);
        roleop3.setSelection(0);

        addmore = findViewById(R.id.addmore);
        addmore.setOnClickListener(onClick());

        mLayout = findViewById(R.id.rolelayout);

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addedones.clear();
                roleselections.clear();
                checkselections.clear();
                checkboxes.clear();

                roleselections.add(roleop1.getSelectedItem().toString());
                roleselections.add(roleop2.getSelectedItem().toString());
                roleselections.add(roleop3.getSelectedItem().toString());
                checkselections.add(check1.isSelected());
                checkselections.add(check2.isSelected());
                checkselections.add(check3.isSelected());
                checkselections.add(check4.isSelected());
                checkselections.add(check5.isSelected());
                checkselections.add(check6.isSelected());
                checkselections.add(check7.isSelected());
                checkselections.add(check8.isSelected());
                checkselections.add(check9.isSelected());


                if(addedones.size() != 0){
                    for (int i = 0; i < addedones.size(); i++) {
                        roleselections.add(addedones.get(i).getSelectedItem().toString());
                    }
                    for (int i = 0; i <checkboxes.size(); i++) {
                        checkselections.add(checkboxes.get(i).isSelected());
                    }
                }
                //All the roles are in the list and the checkboxes in the list


                if(roleselections.contains("Please select your role")){
                    Toast.makeText(RegisterChapter.this, "Complete selecting each role", Toast.LENGTH_SHORT).show();
                }
                //remove the empty ones, and remove the corresponding checks
                else{
                    for (int i = 0; i < roleselections.size(); i++) {
                        if(roleselections.get(i).equals("Empty")){
                            roleselections.remove(i);
                            checkselections.remove((i*3));
                            checkselections.remove((i*3)+1);
                            checkselections.remove((i*3)+2);
                        }
                    }
                }

                boolean isdup = false;

                for (int i = 0; i < roleselections.size(); i++) {
                    for (int j = i + 1 ; j < roleselections.size(); j++) {
                        if (roleselections.get(i).equals(roleselections.get(j))) {
                            isdup = true;
                        }
                    }
                }

                if(isdup){
                    Toast.makeText(RegisterChapter.this, "Remove any duplicate roles", Toast.LENGTH_SHORT).show();
                }


                DatabaseReference registernewchap = FirebaseDatabase.getInstance().getReference().child("Chapters").child(newid.getText().toString());


                for (int i = 0; i < roleselections.size(); i++) {

                    if(roleselections.get(i).equals("Member")){
                        registernewchap.child("MemberKey").setValue(getSaltString());
                        registernewchap.child("MemberPrefs").setValue(checkselections.get(i*3)+","
                        + checkselections.get((i*3) +1)+"," + checkselections.get((i*3) +2));
                    }
                    else if(roleselections.get(i).equals("Adviser")){
                        registernewchap.child("AdviserKey").setValue(getSaltString());
                        registernewchap.child("AdviserPrefs").setValue(checkselections.get(i*3)+","
                                + checkselections.get((i*3) +1)+"," + checkselections.get((i*3) +2));
                    }
                    else if(roleselections.get(i).equals("Officer")){
                        registernewchap.child("OfficerKey").setValue(getSaltString());
                        registernewchap.child("OfficerPrefs").setValue(checkselections.get(i*3)+","
                                + checkselections.get((i*3) +1)+"," + checkselections.get((i*3) +2));
                    }
                    else if(roleselections.get(i).equals("State Officer")){
                        registernewchap.child("StateOfficerKey").setValue(getSaltString());
                        registernewchap.child("StateOfficerPrefs").setValue(checkselections.get(i*3)+","
                                + checkselections.get((i*3) +1)+"," + checkselections.get((i*3) +2));
                    }
                    else if(roleselections.get(i).equals("National Officer")){
                        registernewchap.child("NationalOfficerKey").setValue(getSaltString());
                        registernewchap.child("NationalOfficerPrefs").setValue(checkselections.get(i*3)+","
                                + checkselections.get((i*3) +1)+"," + checkselections.get((i*3) +2));
                    }
                }


                registernewchap.child("ID").setValue(newid.getText().toString());
                registernewchap.child("Name").setValue(newchapter.getText().toString());
                registernewchap.child("State").setValue(listofstates.getSelectedItem().toString());
                registernewchap.child("Zip").setValue(newzip.getText().toString());
                registernewchap.child("AdviserEmail").setValue(newadviemail.getText().toString());




            }
        });

    }

    private View.OnClickListener onClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                clickcount++;

                if(mLayout.getChildCount()<17){
                    addfirstcheck("Post on activity stream", clickcount);
                    mLayout.addView(addnext("Approve new members"));
                    mLayout.addView(addnext("Edit calendar events"));

                    Log.d("DARBAR", mLayout.getChildCount() + "");
                }else{
                    Toast.makeText(RegisterChapter.this, "Max roles reached", Toast.LENGTH_SHORT).show();
                }


            }
        };
    }

    private void addfirstcheck(String text, int clickcount) {
        final TableLayout.LayoutParams lparams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        final CheckBox check1 = new CheckBox(this);

        final TableLayout.LayoutParams normparams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        lparams.setMargins(0, 16, 0, 0); // left, top, right, bottom
        check1.setLayoutParams(normparams);

        check1.setText(text);


        Spinner newrole = new Spinner(this);

        ArrayAdapter<CharSequence> adapter  = ArrayAdapter.createFromResource(this, R.array.setuproles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newrole.setAdapter(adapter);
        newrole.setLayoutParams(lparams);
        newrole.setId(clickcount);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        addedones.add(newrole);
        checkboxes.add(check1);

        mLayout.addView(newrole);
         mLayout.addView(check1);
       // mLayout.addView(newrole);
        //mLayout.addView(check1);
        //return tr;


    }

    private CheckBox addnext(String text){
        final TableLayout.LayoutParams normparams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        final CheckBox check2 = new CheckBox(this);
        check2.setLayoutParams(normparams);
        check2.setText(text);

        checkboxes.add(check2);

        return check2;
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
