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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

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

    TextView fullname, emailandgrade, chapterheader;
    private FirebaseAuth mAuth;
    NavigationView navigationView;
    String role;

    boolean canuser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblahome);
        Firebase.setAndroidContext(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        GeneralInfo fragmenttt = new GeneralInfo();
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragmenttt);
        fragmentTransaction.commit();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View v = navigationView.getHeaderView(0);

        fullname = v.findViewById(R.id.fullnameheader);
        emailandgrade = v.findViewById(R.id.emailheader);
        chapterheader = v.findViewById(R.id.chapterheader);

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editorchap = spchap.edit();

        final String chapid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        fullname.setText(sp.getString(getString(R.string.fname), "fname") + " " + sp.getString(getString(R.string.lname), "lname"));
        final String fnf = fullname.getText().toString();
        emailandgrade.setText(sp.getString(getString(R.string.email), "email"));

        role = sp.getString(getString(R.string.role), "temprole");

        FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterheader.setText(dataSnapshot.child("Setup").child("ChapterName").getValue().toString() + " FBLA");

                editorchap.putString("chaptername",dataSnapshot.child("Setup").child("ChapterName").getValue().toString());
                editorchap.putString("chapterlogo",dataSnapshot.child("Images").child("ChapterLogo").getValue().toString());
                editorchap.apply();


                final ViewSwitcher switcher = v.findViewById(R.id.viewSwitcher);

                CircleImageView civ = v.findViewById(R.id.profile_image);
                AvatarView userinitials = v.findViewById(R.id.profpicnav);

                String child="";
                if(role.equals("Officer")||role.equals("Member")){
                    child="Users";
                }else if(role.equals("Adviser")){
                    child="Advisers";
                }

                if (dataSnapshot.child(child).child(mAuth.getCurrentUser().getUid()).child("profpic").getValue().toString().equals("nocustomimage")) {
                    userinitials.bind(fullname.getText().toString(), null);
                } else {
                    switcher.showNext();
                    Glide.with(getApplicationContext()).load(dataSnapshot.child(child).child(mAuth.getCurrentUser().getUid()).child("profpic").getValue().toString()).into(civ);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        if(role.equals("Adviser")){
            Menu menu = navigationView.getMenu();
            MenuItem nav_myfbla = menu.findItem(R.id.nav_myfbla);
            nav_myfbla.setTitle("Approvals");
        }


        DatabaseReference getnumber = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(chapid);
        getnumber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long val = dataSnapshot.child("Users").getChildrenCount();
                long total = val;

                TextView chaptertotal = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                        findItem(R.id.nav_chapmembers));
                initializeCountDrawer(chaptertotal, "Member Count: " + total);

                if(role.equals("Adviser")||role.equals("Officer")) {
                    if (dataSnapshot.child("Roles").child(role + "Rules").getValue().toString().contains("3")) {
                        canuser = true;
                        Menu menu = navigationView.getMenu();
                        MenuItem nav_meetings = menu.findItem(R.id.nav_notes);
                        nav_meetings.setTitle("Manage Meetings");
                    }
                }

                if(!canuser){
                    Menu menu = navigationView.getMenu();
                    MenuItem nav_meetings = menu.findItem(R.id.nav_notes);
                    nav_meetings.setTitle("Check In to Meeting");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //update adviser devicetokens
        if(role.equals("Adviser")) {
            final DatabaseReference devtokens = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("Advisers");
            devtokens.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> adevtoks = collectemails((Map<String, Object>) dataSnapshot.getValue(), "device_token");
                    String add = "";
                    for (int i = 0; i < adevtoks.size(); i++) {
                        add += adevtoks.get(i) + ",";
                    }
                    devtokens.child("device_tokens").setValue(add);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_chapsetts).setVisible(true);

            nav_Menu.findItem(R.id.nav_contact).setVisible(false);

            nav_Menu.findItem(R.id.nav_join).setVisible(false);
        }else{
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_chapsetts).setVisible(false);
        }


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
                String sharesub = "Hey there! Download the FBLA Chapters to get updates on our chapter.";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
                myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
                startActivity(Intent.createChooser(myIntent, "Share FBLA Chapters using"));
            }
        });

        ImageButton lgtbutton = v.findViewById(R.id.lgt);
        lgtbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
                final String chapid = spchap.getString("chapterID", "tempid");

                SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                final String role = sp.getString(getString(R.string.role), "role");
                DatabaseReference dr;

                if(role.equals("Adviser")){
                    dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                            .child("Advisers");
                }else{
                    dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                            .child("Users");
                }

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


       if (id == R.id.nav_board) {
            startActivity(new Intent(FBLAHome.this, MainActivity.class));
        } else if (id == R.id.nav_calendar) {
           fragmentTransaction.replace(R.id.frameLayout, new Calendar());
           fragmentTransaction.commit();
       }else if (id == R.id.nav_info) {
            fragmentTransaction.replace(R.id.frameLayout, new GeneralInfo());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_welcome) {
            fragmentTransaction.replace(R.id.frameLayout, new Welcome());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_chapmembers) {
            fragmentManager.beginTransaction().replace(R.id.frameLayout, new ComboFragments()).
                    addToBackStack(null).commit();
        } else if (id == R.id.nav_myfbla) {

            if(role.equals("Adviser")){
                //aprovals page
                startActivity(new Intent(FBLAHome.this, Approvals.class));
            }else{
                fragmentTransaction.replace(R.id.frameLayout, new MyFBLA());
                fragmentTransaction.commit();
            }

       } else if (id == R.id.nav_events) {
           fragmentTransaction.replace(R.id.frameLayout, new CompetitveEvents());
           fragmentTransaction.commit();
       }else if (id == R.id.nav_choose) {
           fragmentTransaction.replace(R.id.frameLayout, new ChooseEvent());
           fragmentTransaction.commit();
       }else if (id == R.id.nav_stats) {
           fragmentTransaction.replace(R.id.frameLayout, new Stats());
           fragmentTransaction.commit();
       }

       else if (id == R.id.nav_join) {
           fragmentTransaction.replace(R.id.frameLayout, new JoinFBLA());
           fragmentTransaction.commit();
       }
       else if (id == R.id.nav_notes) {
           if(canuser){
               fragmentTransaction.replace(R.id.frameLayout, new StartMeeting());
               fragmentTransaction.commit();
           }else{
               fragmentTransaction.replace(R.id.frameLayout, new CheckInMeeting());
               fragmentTransaction.commit();
           }

       }

       else if (id == R.id.nav_officers) {
           fragmentTransaction.replace(R.id.frameLayout, new Officers());
           fragmentTransaction.commit();
       } else if (id == R.id.nav_social) {

           fragmentManager.beginTransaction().replace(R.id.frameLayout, new SocialCombo()).
                   addToBackStack(null).commit();
        }

       else if (id == R.id.nav_notifs) {
           fragmentTransaction.replace(R.id.frameLayout, new Notifications());
           fragmentTransaction.commit();
       }


       else if (id == R.id.nav_chapsetts) {
           startActivity(new Intent(FBLAHome.this, ChapterSettings.class));
         //  fragmentTransaction.replace(R.id.frameLayout, new ChapterSettings());
           //fragmentTransaction.commit();
       }
       else if (id == R.id.nav_faq) {
           fragmentTransaction.replace(R.id.frameLayout, new FAQ());
           fragmentTransaction.commit();
       }
       else if (id == R.id.nav_contact) {
           //INCLUDE QA
           fragmentTransaction.replace(R.id.frameLayout, new ContactUs());
           fragmentTransaction.commit();
       }
       else if (id == R.id.nav_privacy) {
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
    private ArrayList<String> collectemails(Map<String, Object> users, String whatyouwant) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            if(!entry.getKey().toString().equals("device_tokens")){
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list

                if (singleUser != null) {
                    information.add((String) singleUser.get(whatyouwant));
                }
            }
            //Get user map

        }

        return information;
    }

}
