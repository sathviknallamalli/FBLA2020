package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class Profile extends AppCompatActivity {

    TabLayout tl;
    ViewPager vp;

    private int[] navIcons = {
            R.drawable.inbox,
            R.drawable.board,
            R.drawable.person
    };
    private String[] navLabels = {
            "MESSAGES",
            "STREAM",
            "MYFBLA"
    };
    // another resouces array for active state for the icon
    private int[] navIconsActive = {
            R.drawable.messageactive,
            R.drawable.streamactive,
            R.drawable.personactive
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Profile.this, FBLAHome.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    String role;
    String chapterid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile Information");

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView menuname = findViewById(R.id.profmenuname);

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String firstnae = sp.getString(getString(R.string.fname), "fname");
        String lastname = sp.getString(getString(R.string.lname), "lname");

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");


        final String fn = firstnae + " " + lastname;

        final AvatarView av = findViewById(R.id.circlenav);
        final CircleImageView civ = findViewById(R.id.circleiv);
        final ViewSwitcher vs = findViewById(R.id.vs);

        role = sp.getString(getString(R.string.role), "role");

        String child="";
        if(role.equals("Adviser")){
            child="Advisers";
        }else if(role.equals("Officer") || role.equals("Member")){
            child="Users";
        }
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid)
                .child(child).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("profpic").getValue().toString().equals("nocustomimage")){
                        av.bind(fn, null);
                }else{
                    vs.showNext();
                    Glide.with(getApplicationContext()).load(dataSnapshot.child("profpic").getValue().toString()).into(civ);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        menuname.setText(fn);


        tl = findViewById(R.id.tabs);
        vp = findViewById(R.id.viewpager_id);

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        adapter.addfragment(new ViewPageMessage(), "Messages");
        adapter.addfragment(new ViewPageStream(), "Stream");
        adapter.addfragment(new ViewPageMyFBLA(), "MyFBLA");

        vp.setAdapter(adapter);

        tl.setupWithViewPager(vp);

        // loop through all navigation tabs
        for (int i = 0; i < tl.getTabCount(); i++) {
            // inflate the Parent LinearLayout Container for the tab
            // from the layout nav_tab.xml file that we created 'R.layout.nav_tab
            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tabitem, null);

            // get child TextView and ImageView from this layout for the icon and label
            TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);
            ImageView tab_icon = (ImageView) tab.findViewById(R.id.nav_icon);

            // set the label text by getting the actual string value by its id
            // by getting the actual resource value `getResources().getString(string_id)`
            tab_label.setText(navLabels[i]);

            // set the home to be active at first
            if(i == 0) {
                tab_label.setTextColor(getResources().getColor(R.color.colorPrimary));
                tab_icon.setImageResource(navIconsActive[i]);
            } else {
                tab_icon.setImageResource(navIcons[i]);
                tab_label.setTextColor(getResources().getColor(R.color.black));
            }

            // finally publish this custom view to navigation tab
            tl.getTabAt(i).setCustomView(tab);
        }


        tl.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(vp) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);

                        // 1. get the custom View you've added
                        View tabView = tab.getCustomView();

                        // get inflated children Views the icon and the label by their id
                        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                        ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);

                        // change the label color, by getting the color resource value
                        tab_label.setTextColor(getResources().getColor(R.color.colorPrimary));
                        // change the image Resource
                        // i defined all icons in an array ordered in order of tabs appearances
                        // call tab.getPosition() to get active tab index.
                        tab_icon.setImageResource(navIconsActive[tab.getPosition()]);
                    }

                    // do as the above the opposite way to reset tab when state is changed
                    // as it not the active one any more
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        View tabView = tab.getCustomView();
                        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                        ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);

                        // back to the black color
                        tab_label.setTextColor(getResources().getColor(R.color.black));
                        // and the icon resouce to the old black image
                        // also via array that holds the icon resources in order
                        // and get the one of this tab's position
                        tab_icon.setImageResource(navIcons[tab.getPosition()]);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );

      /*  new DownloadWebpageTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                processJson(object);
            }
        }).execute("https://spreadsheets.google.com/tq?key=17Rz6dWiKBKN6O-Mjun0DQmL6GVrb1dhpYThKqd7-Yw4");*/

       /* SheetsQuickstart sq = new SheetsQuickstart();
        try {
            sq.method();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            Intent i = new Intent(Profile.this, FBLAHome.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if(item.getItemId() == R.id.changesettings){
            Intent i = new Intent(Profile.this, Settings.class);
            startActivity(i);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profilemenu, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

}
