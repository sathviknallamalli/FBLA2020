package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SponsorDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor_details);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView last = findViewById(R.id.last);
        TextView first = findViewById(R.id.first);

        last.setText(Sponsors.lname);
        first.setText(Sponsors.fname);

        TextView spon1 = findViewById(R.id.spon1name);
        TextView spon2 = findViewById(R.id.spon1name2);
        TextView spon3 = findViewById(R.id.spon1name3);
        TextView spon4 = findViewById(R.id.spon1name4);

        spon1.setText(Sponsors.spon1);
        spon2.setText(Sponsors.spon2);
        spon3.setText(Sponsors.spon3);
        spon4.setText(Sponsors.spon4);

        TextView contname1 = findViewById(R.id.contname1);
        TextView contname2 = findViewById(R.id.contname2);
        TextView contname3 = findViewById(R.id.contname3);
        TextView contname4 = findViewById(R.id.contname4);

        contname1.setText(Sponsors.cn1);
        contname2.setText(Sponsors.cn2);
        contname3.setText(Sponsors.cn3);
        contname4.setText(Sponsors.cn4);

        TextView amt1 = findViewById(R.id.amount1);
        TextView amt2 = findViewById(R.id.amount2);
        TextView amt3 = findViewById(R.id.amount3);
        TextView amt4 = findViewById(R.id.amount4);

        if (!Sponsors.amt1.equals("Unavailable")) {
            amt1.setText("$" + Double.parseDouble(Sponsors.amt1));
        } else {
            amt1.setText(Sponsors.amt1);
        }

        if (!Sponsors.amt2.equals("Unavailable")) {
            amt2.setText("$" + Double.parseDouble(Sponsors.amt2));
        } else {
            amt2.setText(Sponsors.amt2);
        }

        if (!Sponsors.amt3.equals("Unavailable")) {
            amt3.setText("$" + Double.parseDouble(Sponsors.amt3));
        } else {
            amt3.setText(Sponsors.amt3);
        }

        if (!Sponsors.amt4.equals("Unavailable")) {
            amt4.setText("$" + Double.parseDouble(Sponsors.amt4));
        } else {
            amt4.setText(Sponsors.amt4);
        }

        TextView total = findViewById(R.id.total);
        total.setText("Total Sponsorship: " + Sponsors.total);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(SponsorDetails.this, Head.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if (item.getItemId() == R.id.paymentnote) {
            Intent newintent = new Intent(SponsorDetails.this, ANote.class);
            newintent.putExtra("notename", "aboutSponsorDetails");
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
        Intent i = new Intent(SponsorDetails.this, Head.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
