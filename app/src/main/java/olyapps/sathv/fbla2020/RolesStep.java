package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class RolesStep extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roles);

        final Button finishroles = findViewById(R.id.finishroles);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        LinearLayout regll = findViewById(R.id.rolelayout);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(regll.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        final ArrayList<CheckBox> officerchecks = new ArrayList<>();
        final ArrayList<CheckBox> adviserchecks = new ArrayList<>();

        //officer options
        CheckBox op0 = findViewById(R.id.op0);
        CheckBox op1 = findViewById(R.id.op1);
        CheckBox op2 = findViewById(R.id.op2);
        CheckBox op3 = findViewById(R.id.op3);

        officerchecks.add(op0);
        officerchecks.add(op1);
        officerchecks.add(op2);
        officerchecks.add(op3);

        //adviser options
        CheckBox op4 = findViewById(R.id.op4);
        CheckBox op5 = findViewById(R.id.op5);
        CheckBox op6 = findViewById(R.id.op6);
        CheckBox op7 = findViewById(R.id.op7);

        adviserchecks.add(op4);
        adviserchecks.add(op5);
        adviserchecks.add(op6);
        adviserchecks.add(op7);

        final TextView membercode = findViewById(R.id.membercode);
        final TextView officercode = findViewById(R.id.officercode);
        final TextView advisercode = findViewById(R.id.advisercode);

        final Button moveon = findViewById(R.id.moveon);

        moveon.setVisibility(View.INVISIBLE);
        membercode.setVisibility(View.INVISIBLE);
        officercode.setVisibility(View.INVISIBLE);
        advisercode.setVisibility(View.INVISIBLE);

        finishroles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String chapid = intent.getExtras().getString("chapterid");
                String email = intent.getExtras().getString("adviseremail");

                String mc = getSaltString();
                String oc = getSaltString();
                String ac = getSaltString();

                membercode.setText("Member code: " + mc);
                officercode.setText("Officer code: " + oc);
                advisercode.setText("Adviser code: " + ac);

                DatabaseReference codes = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid);
                codes.child("JoinCodes").child("MemberCode").setValue(mc);
                codes.child("JoinCodes").child("OfficerCode").setValue(oc);
                codes.child("JoinCodes").child("AdviserCode").setValue(ac);

                String ocrules = "";
                String acrules = "";

                for (int i = 0; i < officerchecks.size(); i++) {
                    if (officerchecks.get(i).isChecked()) {
                        ocrules += i + ",";
                    }
                }

                for (int i = 0; i < adviserchecks.size(); i++) {
                    if (adviserchecks.get(i).isChecked()) {
                        acrules += i + ",";
                    }
                }

                codes.child("Roles").child("OfficerRules").setValue(ocrules);
                codes.child("Roles").child("AdviserRules").setValue(acrules);

                sendconfirmemail(email, mc, oc ,ac);

                finishroles.setEnabled(false);
                moveon.setVisibility(View.VISIBLE);
                membercode.setVisibility(View.VISIBLE);
                officercode.setVisibility(View.VISIBLE);
                advisercode.setVisibility(View.VISIBLE);

            }
        });

        moveon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String chapid = intent.getExtras().getString("chapterid");

                SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorchap = spchap.edit();

                editorchap.putString("chapterID",chapid);
                editorchap.apply();

                Intent i = new Intent(getApplicationContext(), LastStep.class);
                i.putExtra("chapterid", chapid);
                startActivity(i);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                finish();
            }
        });
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

    public void sendconfirmemail(String email, String mc, String oc, String ac) {
        Intent intent = getIntent();
        String chapid = intent.getExtras().getString("chapterid");


        String subject = "Chapter Registration " + chapid;
        String message = "Below are the codes for each role in your chapter. Only share the proper ones to those members " +
                "that need it.\n\nMember role: " + mc + "\nOfficer role: " + oc + "\nAdviser role: " + ac;
        SendMail sm = new SendMail(RolesStep.this, email, subject, message);
        sm.execute();

    }
}
