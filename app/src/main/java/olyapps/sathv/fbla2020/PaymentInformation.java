package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_information);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(Budget.fname + " " + Budget.lname + " Payment Information");

        TextView lnamepi = findViewById(R.id.lnamepi);
        TextView fnamepi = findViewById(R.id.fnamepi);
        TextView gradyear = findViewById(R.id.gradyear);
        TextView ss = findViewById(R.id.ss);
        TextView cd = findViewById(R.id.clubdues);
        TextView fc = findViewById(R.id.fallconference);
        TextView wc = findViewById(R.id.winterconference);
        TextView wp = findViewById(R.id.winterpermission);

        lnamepi.setText(Budget.lname);
        fnamepi.setText(Budget.fname);
        gradyear.setText("Graduation Year: " + Budget.gradyear);
        ss.setText("Shirt Size: " + Budget.shirtsize);
        cd.setText("Club Dues: " + Budget.clubdue);
        fc.setText("Fall Conference Dues: " + Budget.fallconference);
        wc.setText("Winter Conference Dues: " + Budget.winterconference);
        wp.setText("Winter Permission Status: " + Budget.winterpermission);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(PaymentInformation.this, Head.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if (item.getItemId() == R.id.paymentnote) {
            Intent newintent = new Intent(PaymentInformation.this, ANote.class);
            newintent.putExtra("notename", "aboutPaymentInfo");
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
        Intent i = new Intent(PaymentInformation.this, Head.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
