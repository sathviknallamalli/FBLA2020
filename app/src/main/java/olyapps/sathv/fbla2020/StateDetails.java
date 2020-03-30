package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StateDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_details);
        setTitle(State.fname + " " + State.lname + " State Details");
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView fnamestate = findViewById(R.id.fnamestate);
        TextView lnamestate = findViewById(R.id.lnamestate);
        TextView genderstate = findViewById(R.id.gender);

        fnamestate.setText(State.fname);
        lnamestate.setText(State.lname);

        String gender;

        if (State.male.equals("")) {
            gender = "Female";
        } else {
            gender = "Male";
        }
        genderstate.setText("Gender: " + gender);

        TextView permission = findViewById(R.id.permission);
        permission.setText("State Permission: " + State.permission);

        TextView nurse = findViewById(R.id.nurse);
        nurse.setText("Nurse Permission: " + State.nurse);

        TextView balance = findViewById(R.id.balance);
        TextView nonrefund = findViewById(R.id.nonrefund);
        TextView finalpay = findViewById(R.id.finalpay);
        TextView sponsor = findViewById(R.id.sponsor);

        if (!State.balance.matches(".*\\d+.*")) {
            balance.setText("$" + Double.parseDouble(State.balance));
        } else {
            balance.setText(State.balance);
        }

        if (!State.nonrefund.matches(".*\\d+.*")) {
            nonrefund.setText("$" + Double.parseDouble(State.nonrefund));
        } else {
            nonrefund.setText(State.nonrefund);
        }

        if (!State.finalpay.matches(".*\\d+.*")) {
            finalpay.setText("$" + Double.parseDouble(State.finalpay));
        } else {
            finalpay.setText(State.finalpay);
        }

        if (!!State.sponsormon.matches(".*\\d+.*")) {
            sponsor.setText("$" + Double.parseDouble(State.sponsormon));
        } else {
            sponsor.setText(State.sponsormon);
        }


        TextView event1 = findViewById(R.id.event1state);
        TextView event2 = findViewById(R.id.event2state);
        TextView event3 = findViewById(R.id.event3state);
        TextView event4 = findViewById(R.id.event4state);


        event1.setText(State.eventuno);
        event2.setText(State.eventdos);
        event3.setText(State.eventtres);
        event4.setText(State.eventcuatro);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(StateDetails.this, Head.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if (item.getItemId() == R.id.paymentnote) {
            Intent newintent = new Intent(StateDetails.this, ANote.class);
            newintent.putExtra("notename", "aboutStateDetails");
            startActivity(newintent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.justanote, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(StateDetails.this, Head.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
