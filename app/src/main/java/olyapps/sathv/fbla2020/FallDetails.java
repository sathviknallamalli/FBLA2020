package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FallDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_details);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(FallConference.fname + " " + FallConference.lname + " Fall Details");

        TextView fnamefd = findViewById(R.id.fnamefd);
        TextView lnamefd = findViewById(R.id.lnamefd);
        TextView fallconffd = findViewById(R.id.fallconffd);
        TextView fallpermifd = findViewById(R.id.fallpermifd);
        TextView gradyearfd = findViewById(R.id.gradyearfd);


        fnamefd.setText(FallConference.fname);
        lnamefd.setText(FallConference.lname);
        fallconffd.setText("Fall Conference Dues: " + FallConference.fallcnf);
        fallpermifd.setText("Fall Conference Permission: " + FallConference.fallperm);
        gradyearfd.setText("Grad Year: " + FallConference.gradyear);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(FallDetails.this, Head.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if (item.getItemId() == R.id.paymentnote) {
            Intent newintent = new Intent(FallDetails.this, ANote.class);
            newintent.putExtra("notename", "aboutFallDetails");
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
        Intent i = new Intent(FallDetails.this, Head.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }


}
