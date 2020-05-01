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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    RecyclerView bloglist;
    DatabaseReference mDatabase;


    FirebaseAuth mAuth;


    LinearLayoutManager mLinearLayoutManager;

    ProgressBar pb;

    static int totalcount;
    static int tc;
    TextView noposts;

    String chapid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        setTitle("Stream");
        //getblogposts();

        mAuth = FirebaseAuth.getInstance();

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapid = spchap.getString("chapterID", "tempid");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("ActivityStream");
        mDatabase.keepSynced(true);

        noposts = findViewById(R.id.nopost);

         bloglist = findViewById(R.id.blog_list);
        // bloglist.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        bloglist.setLayoutManager(mLinearLayoutManager);


        pb = findViewById(R.id.blgpb);

    }

    @Override
    protected void onStart() {
        super.onStart();

        pb.setVisibility(View.VISIBLE);

        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(mDatabase, Blog.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_row, parent, false);

                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final BlogViewHolder holder, int position, Blog model) {
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());
                if (model.getImageurl().equals("No image in this post")) {
                    holder.setImage(getApplicationContext(), "http://www.staticwhich.co.uk/static/images/products/no-image/no-image-available.png");
                } else {
                    holder.setImage(getApplicationContext(), model.getImageurl());
                }

                holder.setUsername(model.getUsername());
                holder.setTimestamp(model.getTimestamp());
                holder.setuserImage(getApplicationContext(), model.getUid());

                final String post_key = getRef(position).getKey();


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


                holder.list_comments.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });


                holder.setListView(post_key, chapid);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singintent = new Intent(MainActivity.this, PostSingleActivity.class);
                        singintent.putExtra("post_id", post_key);
                        startActivity(singintent);
                    }
                });

                holder.mcomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, CommentActivity.class);
                        i.putExtra("post_key", post_key);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_in_out);
                    }
                });

                holder.sendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, CommentActivity.class);
                        i.putExtra("post_key", post_key);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_in_out);
                    }
                });


            }
        };

        //  bloglist.smoothScrollToPosition(bloglist.getChildCount());
        bloglist.setAdapter(adapter);
        adapter.startListening();
        pb.setVisibility(View.GONE);

       /* if(tc==0){
           noposts.setVisibility(View.VISIBLE);
        }else{
            noposts.setVisibility(View.INVISIBLE);
        }*/

        // bloglist.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;
       ImageView sendbtn;
        DatabaseReference udatabase;
        FirebaseAuth mAuth;
        ImageView post_image;
        CircleImageView uimage;
        EditText mcomment;

        ListView list_comments;


        public BlogViewHolder(View itemView) {
            super(itemView);

            SharedPreferences spchap = itemView.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
            String chapid = spchap.getString("chapterID", "tempid");

            mView = itemView;
            mcomment = mView.findViewById(R.id.commentArea);
            udatabase = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("Users");
            mAuth = FirebaseAuth.getInstance();
            sendbtn = mView.findViewById(R.id.sendcomment);


            list_comments = mView.findViewById(R.id.list_comments);
        }

        public void setTitle(String title) {
            tc++;
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String imageurl) {
            post_image = (ImageView) mView.findViewById(R.id.post_image);
            Glide.with(ctx).load(imageurl).into(post_image);
            if (imageurl.equals("http://www.staticwhich.co.uk/static/images/products/no-image/no-image-available.png")) {
                post_image.setVisibility(View.GONE);
            }
        }

        public void setuserImage(final Context ctx, String uid) {

            uimage = (CircleImageView) mView.findViewById(R.id.userimage);
            udatabase.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("profpic")) {
                        String thing = dataSnapshot.child("profpic").getValue().toString();
                        if (thing.equals("nocustomimage")) {
                            Glide.with(ctx).load(R.drawable.defaultimg).into(uimage);
                        } else {
                            Glide.with(ctx).load(thing).into(uimage);
                        }
                    }
                    //the user does not have the filed profpic, menaing they didnt complete accouuntsetip
                    else {
                        Glide.with(ctx).load(R.drawable.defaultimg).into(uimage);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setUsername(String username) {
            TextView post_username = mView.findViewById(R.id.post_username);
            post_username.setText("Posted by: " + username);
        }

        public void setTimestamp(String timestamp) {
            TextView post_timestamp = mView.findViewById(R.id.post_timestamp);
            post_timestamp.setText(timestamp);
        }



        public void setListView(String post_key, String chapid) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                    child("Chapters").child(chapid).child("ActivityStream")
                    .child(post_key);

            final ArrayList<Comment> comments = new ArrayList<>();
            final CommentAdapter[] cadapter = new CommentAdapter[1];

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("Comments")) {
                        for (DataSnapshot snapshot :
                                dataSnapshot.child("Comments").getChildren()) {
                            String ct = snapshot.child("commenttext").getValue().toString();
                            String uid = snapshot.child("useruid").getValue().toString();
                            String time = snapshot.child("time").getValue().toString();

                            comments.add(new Comment(ct, uid, time));
                        }


                        cadapter[0] = new CommentAdapter(mView.getContext(), R.layout.comment_single_layout, comments);
                        list_comments.setAdapter(cadapter[0]);

                        list_comments.setOnItemClickListener(null);

                        if (comments.size() == 0) {
                            list_comments.setVisibility(View.GONE);
                        }

                        totalcount = comments.size();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String role = sp.getString(getString(R.string.role), "role");

        check(new MACallback() {
            @Override
            public void onCallback(Boolean canofficer, Boolean canadviser) {
                if ((role.equals("Officer") && canofficer) || (role.equals("Adviser") && canadviser)) {
                    getMenuInflater().inflate(R.menu.main_menu, menu);

                    for (int i = 0; i < menu.size(); i++) {
                        Drawable drawable = menu.getItem(i).getIcon();
                        if (drawable != null) {
                            drawable.mutate();
                            drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
                }
            }
        });

        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(MainActivity.this, FBLAHome.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MainActivity.this, FBLAHome.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public void check(final MACallback maCallback) {
        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        String chapid = spchap.getString("chapterID", "tempid");

        DatabaseReference calrolecheck = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                .child("Roles");
        calrolecheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean canofficer = false;
                boolean canadviser = false;

                if (dataSnapshot.child("OfficerRules").getValue().toString().contains("0")) {
                    canofficer = true;
                }

                if (dataSnapshot.child("AdviserRules").getValue().toString().contains("0")) {
                    canadviser = true;
                }
                maCallback.onCallback(canofficer, canadviser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
