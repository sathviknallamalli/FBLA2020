package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class FBLAHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;

    TextView fullname, emailandgrade;
    private FirebaseAuth mAuth;
    NavigationView navigationView;
    TextView notifs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblahome);
        Firebase.setAndroidContext(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        GeneralInfo fragmenttt = new GeneralInfo();
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragmenttt);
        fragmentTransaction.commit();


        // startActivity(new Intent(FBLAHome.this, MainActivity.class));


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View v = navigationView.getHeaderView(0);

        fullname = v.findViewById(R.id.fullname);
        emailandgrade = v.findViewById(R.id.emailandgrade);

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

        fullname.setText(sp.getString(getString(R.string.fname), "fname") + " " + sp.getString(getString(R.string.lname), "lname"));
        final String fnf = fullname.getText().toString();
        emailandgrade.setText(sp.getString(getString(R.string.email), "email"));

        String uri = sp.getString(getString(R.string.profpic), "profpic");

        final ViewSwitcher switcher = v.findViewById(R.id.viewSwitcher);

        CircleImageView civ = v.findViewById(R.id.profile_image);
        AvatarView userinitials = v.findViewById(R.id.profpicnav);

        if (uri.equals("nocustomimage")) {
            userinitials.bind(fullname.getText().toString(), null);
        } else {
            switcher.showNext();
            Glide.with(getApplicationContext()).load(uri).into(civ);

        }

        DatabaseReference getnumber = FirebaseDatabase.getInstance().getReference().child("Users");
        getnumber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long val = dataSnapshot.getChildrenCount();
                long total = val;


                TextView chaptertotal = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                        findItem(R.id.nav_chapmembers));
                initializeCountDrawer(chaptertotal, "Member Count: " + total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference geteventcount = FirebaseDatabase.getInstance().getReference().child("UserEvents")
                .child(sp.getString(getString(R.string.fname), "") + " " + sp.getString(getString(R.string.lname), ""));
        geteventcount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final long[] total = {0};
                if(dataSnapshot.exists()){
                    long val = dataSnapshot.getChildrenCount();
                    total[0] = val;
                }


                DatabaseReference teameventcount = FirebaseDatabase.getInstance().getReference().child("TeamEvents");
                teameventcount.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                for (DataSnapshot sp:snapshot.getChildren()) {
                                    if(sp.getValue().toString().contains(fnf)){
                                        total[0]++;
                                    }
                                }
                            }

                        }
                        TextView chaptertotal = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                                findItem(R.id.nav_myfbla));
                        initializeCountDrawer(chaptertotal, "Event Count: " + total[0]);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*Intent notifyIntent = getIntent();
        if (notifyIntent != null && notifyIntent.getExtras() != null) {
            String extras = getIntent().getExtras().get("KEY").toString();
            if (extras != null && extras.equals("YOUR VAL")) {
                Notifications fragmentt = new Notifications();
                android.app.FragmentTransaction fragmentTransactiont = getFragmentManager().beginTransaction();
                fragmentTransactiont.replace(R.id.frameLayout, fragmentt);
                fragmentTransactiont.commit();

            }
        }*/

        Intent notifyIntent = getIntent();
        if (notifyIntent != null && notifyIntent.getExtras() != null) {
            if(getIntent().getExtras().get("viewpage")==null){
                android.app.FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                fragmentTransaction1.replace(R.id.frameLayout, new GeneralInfo());
                fragmentTransaction1.commit();
            }else{
                String extras = getIntent().getExtras().get("viewpage").toString();
                if (extras != null && extras.equals("gotocombo")) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, new ComboFragments()).
                            addToBackStack(null).commit();
                }else if (extras != null && extras.equals("gotomyfbla")) {
                    android.app.FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                  //  fragmentTransaction1.replace(R.id.frameLayout, new MyFBLA());
                    fragmentTransaction1.commit();
                }else if (extras != null && extras.equals("gotocal")) {
                    android.app.FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.frameLayout, new Calendar());
                    fragmentTransaction1.commit();
                }
            }

        }


        ImageButton sharebutton = v.findViewById(R.id.sharebutton);
        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String sharebody = "Your body here";
                String sharesub = "Hey there! Download the OHS FBLA app to get updates on our club information";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
                myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
                startActivity(Intent.createChooser(myIntent, "Share OHSFBLA using"));
            }
        });

        ImageButton lgtbutton = v.findViewById(R.id.lgt);
        lgtbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");
                dr.child(mAuth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(v.getContext(), LockScreen.class);
                startActivity(i);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.fblahome, menu);
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
       FragmentManager fragmentManager = getSupportFragmentManager();



        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();


        if (id == R.id.nav_info) {
            fragmentTransaction.replace(R.id.frameLayout, new GeneralInfo());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_events) {
            fragmentTransaction.replace(R.id.frameLayout, new CompetitveEvents());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_welcome) {
            fragmentTransaction.replace(R.id.frameLayout, new Welcome());
            fragmentTransaction.commit();
        } /*else if (id == R.id.nav_budget) {
            startActivity(new Intent(FBLAHome.this, Head.class));
        } */else if (id == R.id.nav_social) {
            fragmentTransaction.replace(R.id.frameLayout, new SocialMedia());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_myfbla) {
           // fragmentTransaction.replace(R.id.frameLayout, new MyFBLA());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_notes) {
            fragmentTransaction.replace(R.id.frameLayout, new MeetingNotes());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_officers) {
            fragmentTransaction.replace(R.id.frameLayout, new Officers());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_board) {
            startActivity(new Intent(FBLAHome.this, MainActivity.class));
        }  if (id == R.id.nav_chapmembers) {
            fragmentManager.beginTransaction().replace(R.id.frameLayout, new ComboFragments()).
                    addToBackStack(null).commit();
        } /* else if (id == R.id.nav_conf) {
            fragmentTransaction.replace(R.id.frameLayout, new HowItWorks());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_ourinfo) {
            fragmentTransaction.replace(R.id.frameLayout, new OurInformation());
            fragmentTransaction.commit();

        }  else if (id == R.id.nav_calendar) {
            fragmentTransaction.replace(R.id.frameLayout, new Calendar());
            fragmentTransaction.commit();
        }else if (id == R.id.nav_club) {
            fragmentTransaction.replace(R.id.frameLayout, new Club());
            fragmentTransaction.commit();
        }*/else if (id == R.id.nav_privacy) {
            fragmentTransaction.replace(R.id.frameLayout, new PrivacyPolicy());
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initializeCountDrawer(TextView tv1, String text) {
        //Gravity property aligns the text
        tv1.setGravity(Gravity.CENTER_VERTICAL);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextColor(getResources().getColor(R.color.actionbarcolor));
        tv1.setText(text);
        tv1.setTextSize(12);
    }


}
