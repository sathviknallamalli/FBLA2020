package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class Details extends AppCompatActivity {

    TextView bigname, bigtype, bigcategory;
    WebView eventinfo;
    ProgressBar pb;
        FirebaseAuth mAuth;

        Intent i;

        String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        i = getIntent();



        setTitle(i.getExtras().getString("name"));
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
mAuth = FirebaseAuth.getInstance();

        bigname = findViewById(R.id.bigname);
        bigcategory = findViewById(R.id.bigcategory);
        bigtype = findViewById(R.id.bigtype);

        pb = findViewById(R.id.pbe);

        bigname.setText(i.getExtras().getString("name"));
        bigcategory.setText(i.getExtras().getString("category"));
        bigtype.setText(i.getExtras().getString("type"));

        String name = i.getExtras().getString("name");
        name = name.toLowerCase();

        name = name.replaceAll(" &", "");
        name = name.replaceAll("(FBLA)", "fbla");
        name = name.replaceAll("\\s+", "-");

        String urlbasic = "https://www.fbla-pbl.org/competitive-event/";

        url = urlbasic + name + "/";

        eventinfo = findViewById(R.id.eventinfowv);
        eventinfo.setWebViewClient(new myWebclient());
        eventinfo.getSettings().setJavaScriptEnabled(true);
        eventinfo.loadUrl(url);


        Button rating = findViewById(R.id.ratingsheet);
        final String finalName = name;
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i.getExtras().getString("category").equals("Objective Test")) {
                    Toast.makeText(Details.this, "Testing events have no rating sheet", Toast.LENGTH_SHORT).show();
                } else if (i.getExtras().getString("category").equals("Team Performance")) {
                    Toast.makeText(Details.this, "Team Performance have no rating sheet", Toast.LENGTH_SHORT).show();
                } else if (i.getExtras().getString("category").equals("Spreadsheet Applications")) {
                    Toast.makeText(Details.this, "No rating sheet available", Toast.LENGTH_SHORT).show();
                } else if (i.getExtras().getString("category").equals("Computer Applications")) {
                    Toast.makeText(Details.this, "No rating sheet available", Toast.LENGTH_SHORT).show();
                } else if (i.getExtras().getString("category").equals("Word Processing")) {
                    Toast.makeText(Details.this, "No rating sheet available", Toast.LENGTH_SHORT).show();
                } else if (i.getExtras().getString("category").equals("Database Design & Application")) {
                    Toast.makeText(Details.this, "No rating sheet available", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), RatingSheet.class);
                    intent.putExtra("name", finalName);
                    intent.putExtra("category",i.getExtras().getString("category"));
                    intent.putExtra("type",i.getExtras().getString("type"));
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_share) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String sharesub = "Check out " + i.getExtras().getString("name");
            String sharebody = "Go to the FBLA website or FBLAChapters to check out more about this event.\n" +
                    i.getExtras().getString("name") + "\nHeres the link: " + url;
            myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
            myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
            startActivity(Intent.createChooser(myIntent, "Share with"));
        }
        if (item.getItemId() == R.id.take_note) {
            //Launch take note activity and make it with corner x
            //save it with ifreb ase or intent extra

            Intent newintent = new Intent(Details.this, ANote.class);
            //aboutHospitalityManagement
            newintent.putExtra("notename", "about" + i.getExtras().getString("name"));
            startActivity(newintent);
        }

        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStackImmediate();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStackImmediate();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    public class myWebclient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pb.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            eventinfo.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}