package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CommentActivity extends AppCompatActivity {

    EditText commenttext;

    FirebaseAuth mAuth;

    ListView commentactivity;
    ArrayList<Comment> comments;
    CommentAdapter cadapter;

    String chapid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_close);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final LinearLayout.LayoutParams nocomment = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nocomment.setMargins(10, 21, 10, 0);


        commenttext = findViewById(R.id.edit_text_comment);
        commentactivity = findViewById(R.id.commentactivity);
        Intent i = getIntent();
        String post_key = i.getStringExtra("post_key");
        comments = new ArrayList<>();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapid = spchap.getString("chapterID", "tempid");


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapid).child("ActivityStream")
                .child(post_key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Comments")) {

                    commentactivity.setVisibility(View.VISIBLE);
                    commenttext.setEms(10);

                    for (DataSnapshot snapshot :
                            dataSnapshot.child("Comments").getChildren()) {
                        String ct = snapshot.child("commenttext").getValue().toString();
                        String uid = snapshot.child("useruid").getValue().toString();
                        String time = snapshot.child("time").getValue().toString();

                        comments.add(new Comment(ct, uid, time));
                    }

                  /*  ArrayList<String> texts = collectEventdata((Map<String, Object>) dataSnapshot.child("Comments").getValue(),
                            "commenttext");
                    ArrayList<String> useruids = collectEventdata((Map<String, Object>) dataSnapshot.child("Comments").getValue(),
                            "useruid");
                    ArrayList<String> times = collectEventdata((Map<String, Object>) dataSnapshot.child("Comments").getValue(),
                            "time");

                    for (int j = 0; j <texts.size(); j++) {


                    }*/


                    cadapter = new CommentAdapter(getApplicationContext(), R.layout.comment_single_layout, comments);
                    commentactivity.setAdapter(cadapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if (item.getItemId() == R.id.check) {


            if (!TextUtils.isEmpty(commenttext.getText().toString())) {
                String comment = commenttext.getText().toString();

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                        child("Chapters").child(chapid).child("ActivityStream");

                Intent i = getIntent();
                String post_key = i.getStringExtra("post_key");

                String key = databaseReference.child(post_key).child("Comments").push().getKey();


                databaseReference.child(post_key).child("Comments").child(key).child("commenttext").setValue(comment);
                databaseReference.child(post_key).child("Comments").child(key).child("useruid").setValue(mAuth.getCurrentUser().getUid());
                databaseReference.child(post_key).child("Comments").child(key).child("time").setValue(ServerValue.TIMESTAMP);

            }
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.settings, menu);

        return super.onCreateOptionsMenu(menu);
    }


}
