package olyapps.sathv.fbla2020;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PostActivity extends AppCompatActivity {

    EditText titleedit, desc;
    ImageButton imageSelect;
    Button submit;

    Uri imageURI;

    private static final int GALLERY_REQUEST = 1;

    private ProgressDialog mProgress;

    FirebaseAuth mAuth;

    FirebaseUser currentuser;

    DatabaseReference mDatabase;

    DatabaseReference mDatabaseusers;

    DatabaseReference mNotification;

    StorageReference mStorage;

    DatabaseReference mLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setTitle("New Post");
        Firebase.setAndroidContext(this);

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseusers = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser.getUid());
        mNotification = FirebaseDatabase.getInstance().getReference().child("notificationsBlog");
        mLikes = FirebaseDatabase.getInstance().getReference().child("Likes");

        mProgress = new ProgressDialog(PostActivity.this);

        imageSelect = findViewById(R.id.imagelogo);
        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        titleedit = findViewById(R.id.titleField);
        desc = findViewById(R.id.descField);
        submit = findViewById(R.id.submitButton);

        titleedit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        desc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();

            }
        });
    }

    private void startPosting() {

        mProgress.setMessage("Posting to Blog...");

        final String title_val = titleedit.getText().toString().trim();
        final String desc_val = desc.getText().toString().trim();

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val)) {
            mProgress.show();
            if (imageURI != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Blog_Images");
                final StorageReference photoRef = storageReference.child(imageURI.getLastPathSegment());
                // Upload file to Firebase Storage

                Task<UploadTask.TaskSnapshot> uploadTask;
                uploadTask = photoRef.putFile(imageURI);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final Uri downloadUri = task.getResult();

                            final DatabaseReference newpost = mDatabase.push();

                            mDatabaseusers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy  hh:mm:ss");
                                    String format = simpleDateFormat.format(new Date());

                                    newpost.child("title").setValue(title_val);
                                    newpost.child("desc").setValue(desc_val);
                                    newpost.child("imageurl").setValue(downloadUri.toString());
                                    newpost.child("uid").setValue(currentuser.getUid());
                                    newpost.child("timestamp").setValue(format);

                                    mLikes.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String all = dataSnapshot.child("AllDeviceTokens").getValue().toString();

                                            SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

                                            all = all.replace(sp.getString(getString(R.string.deviceToken), "devt") +",","");

                                            newpost.child("device_token").setValue(all);


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    mNotification.child("add599ba-9aa8-11e8-9eb6-529269fb1459")
                                            .child("Postkey").setValue(newpost.getKey());


                                    newpost.child("username").setValue(dataSnapshot.child("fname").getValue().toString() + " " +
                                            dataSnapshot.child("lname").getValue().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(PostActivity.this, MainActivity.class));
                                            }
                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            mProgress.dismiss();

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
            //NO IMAGE URL
            else {
                mProgress.show();
                final DatabaseReference newpost = mDatabase.push();

                mDatabaseusers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  hh:mm:ss");
                        String format = simpleDateFormat.format(new Date());

                        newpost.child("title").setValue(title_val);
                        newpost.child("desc").setValue(desc_val);
                        newpost.child("imageurl").setValue("No image in this post");
                        newpost.child("uid").setValue(currentuser.getUid());
                        newpost.child("timestamp").setValue(format);
                        mLikes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String all = dataSnapshot.child("AllDeviceTokens").getValue().toString();

                                SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

                                all = all.replace(sp.getString(getString(R.string.deviceToken), "devt") +",","");

                                newpost.child("device_token").setValue(all);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        mNotification.child("add599ba-9aa8-11e8-9eb6-529269fb1459")
                                .child("Postkey").setValue(newpost.getKey());

                        newpost.child("username").setValue(dataSnapshot.child("fname").getValue().toString() + " " +
                                dataSnapshot.child("lname").getValue().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mProgress.dismiss();

            }


        } else {
            Toast.makeText(PostActivity.this, "Missing field(s)", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageURI = data.getData();
            imageSelect.setImageURI(imageURI);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(PostActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PostActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
