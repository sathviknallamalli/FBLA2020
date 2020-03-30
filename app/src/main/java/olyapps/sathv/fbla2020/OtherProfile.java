package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.fahmisdk6.avatarview.AvatarView;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class OtherProfile extends AppCompatActivity {


    TabLayout tl;
    ViewPager vp;

    TextView otherprofilename;
    TextView otherprofilerole;

    ViewSwitcher otherprofilevs;
    CircleImageView otherprofileciv;
    AvatarView otherprofileav;

    ImageView imageview19;

    static String namewithspace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tl = findViewById(R.id.other_profile_tabs);
        vp = findViewById(R.id.other_profile_viewpager);

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        adapter.addfragment(new ViewPageAbout(), "About");
        adapter.addfragment(new ViewPageConversation(), "Chat Conversation");

        vp.setAdapter(adapter);

        tl.setupWithViewPager(vp);

        otherprofilename = findViewById(R.id.other_profile_name);
        otherprofilerole = findViewById(R.id.other_profile_role);
        otherprofilevs = findViewById(R.id.other_profile_vs);
        otherprofileav = findViewById(R.id.other_profile_av);
        otherprofileciv = findViewById(R.id.other_profile_civ);
        imageview19 = findViewById(R.id.imageView19);

        final Intent i = getIntent();

        setTitle(i.getStringExtra("name"));

        otherprofilename.setText(i.getStringExtra("name"));

        namewithspace = i.getStringExtra("name");

        String uid = i.getStringExtra("uid");
        UserDetails.opuid = uid;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                otherprofilerole.setText(dataSnapshot.child("role").getValue().toString());

                final String profpic = dataSnapshot.child("profpic").getValue().toString();

                if(profpic.equals("nocustomimage")){
                    otherprofileav.bind(i.getStringExtra("name"), null);
                }
                else{
                    otherprofilevs.showNext();
                    Glide.with(getApplicationContext()).load(profpic).into(otherprofileciv);

                    imageview19.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(getApplicationContext()).load(profpic)
                                    .apply(bitmapTransform(new BlurTransformation(25)))
                                    .into(imageview19);
                        }
                    }, 5);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
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
}
