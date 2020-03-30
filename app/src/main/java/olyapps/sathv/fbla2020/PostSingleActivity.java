package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostSingleActivity extends AppCompatActivity {
    String post_key = null;
    DatabaseReference mDatabase;
    TextView postun, posttitle, postdesc;
    CircleImageView circlprof;
    ImageView postimg;
    FirebaseAuth mAuth;

    ListView singlecomments;

    ArrayList<Comment> comments;
    CommentAdapter cadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_single);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        post_key = getIntent().getExtras().getString("post_id");

        postun = findViewById(R.id.singleusername);
        posttitle = findViewById(R.id.singleblogtitle);
        postdesc = findViewById(R.id.singleblogdesc);
        circlprof = findViewById(R.id.singunimage);
        postimg = findViewById(R.id.sinlgepostimage);
        singlecomments = findViewById(R.id.single_comments);

        singlecomments.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        comments = new ArrayList<>();

        mDatabase.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String post_title = dataSnapshot.child("title").getValue().toString();
                String post_desc = dataSnapshot.child("desc").getValue().toString();
                String imageurl = dataSnapshot.child("imageurl").getValue().toString();
                String post_username = dataSnapshot.child("username").getValue().toString();
                String postuid = dataSnapshot.child("uid").getValue().toString();

                DatabaseReference udatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                udatabase.child(postuid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("profpic")) {
                            String thing = dataSnapshot.child("profpic").getValue().toString();
                            if (thing.equals("nocustomimage")) {
                                Glide.with(getApplicationContext()).load(R.drawable.defaultimg).into(circlprof);
                            } else {
                                Glide.with(getApplicationContext()).load(thing).into(circlprof);
                            }
                        }
                        //the user does not have the filed profpic, menaing they didnt complete accouuntsetip
                        else {
                            Glide.with(getApplicationContext()).load(R.drawable.defaultimg).into(circlprof);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                setTitle(post_title);


                postun.setText("Posted by: " + post_username);
                posttitle.setText(post_title);
                postdesc.setText(post_desc);

                if (imageurl.equals("No image in this post")) {
                    Glide.with(getApplicationContext()).load("http://www.staticwhich.co.uk/static/images/products/no-image/no-image-available.png").
                            into(postimg);

                } else {
                    Glide.with(getApplicationContext()).load(imageurl).into(postimg);
                }

                //SETT COMMENTS
                if (dataSnapshot.hasChild("Comments")) {
                    for (DataSnapshot snapshot :
                            dataSnapshot.child("Comments").getChildren()) {
                        String ct = snapshot.child("commenttext").getValue().toString();
                        String uid = snapshot.child("useruid").getValue().toString();
                        String time = snapshot.child("time").getValue().toString();

                        comments.add(new Comment(ct, uid, time));
                    }
                }
                cadapter = new CommentAdapter(getApplicationContext(), R.layout.comment_single_layout, comments);
                singlecomments.setAdapter(cadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(PostSingleActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PostSingleActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
