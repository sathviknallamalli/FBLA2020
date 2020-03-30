package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemberRole extends AppCompatActivity {

    Button create;
    Spinner roles;
    EditText keyentered;
    String desiredrole;

    ArrayAdapter<CharSequence> adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_role);

        Intent intent = getIntent();
        final String id = intent.getExtras().getString("chapterid");
        final String memberkey =  intent.getExtras().getString("membercode");
        final String officerkey = intent.getExtras().getString("officercode");
        final String adviserkey = intent.getExtras().getString("advisercode");

        setTitle("Join Chapter: " + id);

        roles = findViewById(R.id.roles);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roles.setAdapter(adapter2);
        roles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                //parent.getItemAtPosition(position)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        roles.setSelection(0);

        keyentered = findViewById(R.id.rolekey);


        create = findViewById(R.id.gotocreate);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                desiredrole = roles.getSelectedItem().toString();
                boolean ismatch = false;

                if (desiredrole.equals("Please select your role")) {
                    Toast.makeText(MemberRole.this, "Select a valid role", Toast.LENGTH_SHORT).show();
                } else {

                    if (desiredrole.equals("Member") && keyentered.getText().toString().equals(memberkey)) {
                        ismatch = true;
                    } else if (desiredrole.equals("Officer") && keyentered.getText().toString().equals(officerkey)) {
                        ismatch = true;
                    } else if (desiredrole.equals("Adviser") && keyentered.getText().toString().equals(adviserkey)) {
                        ismatch = true;
                    }

                    if (ismatch) {




                        Intent intent = new Intent(getApplicationContext(), CreateFBLAAccount.class);
                        intent.putExtra("role", desiredrole);
                        intent.putExtra("chapterid", id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        Toast.makeText(MemberRole.this, "Incorrect key entered", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }
}