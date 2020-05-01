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
import android.util.Log;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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


    StorageReference mStorage;

    String chapid;

    String role,fname,lname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setTitle("New Post");
        Firebase.setAndroidContext(this);

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");
        fname = sp.getString(getString(R.string.fname), "fname");
        lname = sp.getString(getString(R.string.lname), "lname");


        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("ActivityStream");
        mDatabaseusers = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid);


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

        mProgress.setMessage("Posting to Activity Stream...");

        final String title_val = titleedit.getText().toString().trim();
        final String desc_val = desc.getText().toString().trim();

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val)) {
            mProgress.show();
            if (imageURI != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ActivityStream_Images");
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
                            todatabase(title_val, desc_val, downloadUri, true,chapid,role,false,fname,lname);
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
                todatabase(title_val, desc_val, null, false, chapid,role,false,fname,lname);

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

    public void todatabase(final String title_val, final String desc_val, final Uri downloadUri, final boolean iasImage, String chapid
    , final String role, final boolean isMeeting, final String fname, final String lname) {


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("ActivityStream");
        mDatabaseusers = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid);


       final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final DatabaseReference newpost = mDatabase.push();

        //chapter->id->users
        mDatabaseusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy  hh:mm:ss");
                String format = simpleDateFormat.format(new Date());

                newpost.child("title").setValue(title_val);
                newpost.child("desc").setValue(desc_val);
                if (iasImage) {
                    newpost.child("imageurl").setValue(downloadUri.toString());
                } else {
                    newpost.child("imageurl").setValue("No image in this post");
                }

                newpost.child("uid").setValue(mAuth.getCurrentUser().getUid());
                newpost.child("timestamp").setValue(format);

                //get device tokens of all users
                //get device token of advisers

                ArrayList<String>
                        dts = collectEventdata((Map<String, Object>) dataSnapshot.child("Users").getValue(), "device_token");

                String adts = "";

                for (int i = 0; i < dts.size(); i++) {
                    adts += dts.get(i) + ",";
                }

                adts += dataSnapshot.child("Advisers").child("device_tokens").getValue().toString();


                String yourdt = FirebaseInstanceId.getInstance().getToken();


                adts = adts.replace(yourdt + ",AAA", "");
                newpost.child("todevice_tokens" +
                        "").setValue(adts);

                //Create Notification child in each user
                ArrayList<String>
                        uids = collectEventdata((Map<String, Object>) dataSnapshot.child("Users").getValue(), "uid");
                ArrayList<String>
                        auids = collectEventdata((Map<String, Object>) dataSnapshot.child("Advisers").getValue(), "uid");

                if(role.equals("Officer")){
                    uids.remove(mAuth.getCurrentUser().getUid());
                }else if(role.equals("Adviser")){
                    auids.remove(mAuth.getCurrentUser().getUid());
                }

                Log.d("DARBAR", uids.toString());
                if(auids.size()!=0){
                    for (int i = 0; i < auids.size(); i++) {
                        DatabaseReference notifdr = mDatabaseusers.child("Advisers").child(auids.get(i)).child("Notifications")
                                .child(newpost.getKey());
                        if(isMeeting){
                            notifdr.child("Title").setValue("Chapter Meeting");
                        }else{
                            notifdr.child("Title").setValue("New Post");
                        }


                            notifdr.child("Message").setValue("You have a new post from " + fname + " " + lname);


                        notifdr.child("Timestamp").setValue(format);
                    }
                }

                for (int i = 0; i < uids.size(); i++) {
                    DatabaseReference notifdr = mDatabaseusers.child("Users").child(uids.get(i)).child("Notifications")
                            .child(newpost.getKey());
                    if(isMeeting){
                        notifdr.child("Title").setValue("Chapter Meeting");
                    }else{
                        notifdr.child("Title").setValue("New Post");
                    }
                    notifdr.child("Message").setValue("You have a new post from " + fname + " " + lname);
                    notifdr.child("Timestamp").setValue(format);
                }

                newpost.child("username").setValue(fname + " " +
                        lname).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if(!isMeeting){
                                startActivity(new Intent(PostActivity.this, MainActivity.class));
                            }

                        }
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<String> collectEventdata(Map<String, Object> users, String fieldName) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            if (!entry.getKey().toString().equals("device_tokens")) {
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list

                if (singleUser != null) {
                    information.add((String) singleUser.get(fieldName));
                }
            }

            //Get phone field and append to list

        }

        return information;
    }
}
