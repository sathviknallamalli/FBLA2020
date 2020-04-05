package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class Instructions extends AppCompatActivity {

    ViewPager pager;
    ViewPagerAdapter adapter;

    int[] images = {R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4,
            R.drawable.s5, R.drawable.s6, R.drawable.s8,
            R.drawable.s9};

    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    String descriptions[] = {"Find the event you are competing in and you can get access to the rating sheet. Use it to " +
            "evaluate your performance and adjust to get the best score you can. Make sure you understand what each requirement" +
            " means and what the expectations are.", "Get all the information about all the events and find the best that " +
            "fits you. Make sure to read all the guidelines and create the best presentation", "Be able to message with members" +
            " in your chapter. Communicate with members in your groupview with the groupview chat feature.", "Check the Instagram and other" +
            " social media to view photos and moments from chapter events and conferences.",
            "Admins and those who can post will be able to post and all the members can view them. Things such as" +
                    "dates and important reminders will be posted", "Adding a new post is very simple and you can also add a picture",
            "Creating an account will require this information and will change based on this personal information", "You will be" +
            "able to view all your FBLA image such as events and you have the freedom to edit as well"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        pager = findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(images, Instructions.this, descriptions);
        pager.setAdapter(adapter);

        sliderDotspanel = findViewById(R.id.SliderDots);

        dotscount = adapter.getCount();
        dots = new ImageView[dotscount];
        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(Instructions.this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(Instructions.this, R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                    .LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

                if (position == pager.getAdapter().getCount() - 1) {
                    //start next Activity here with activity_next.xml layout


                            Intent intent = new Intent(Instructions.this, FBLAHome.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                            finish();





                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
