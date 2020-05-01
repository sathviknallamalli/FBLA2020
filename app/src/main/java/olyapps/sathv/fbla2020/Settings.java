package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class Settings extends AppCompatActivity {

    EditText f, l, position, email, phone, website;

    ImageButton camclick;
    private static final int GALLEY_REQUEST = 1;

    Uri mImageuri = null;

    StorageReference mstorageimage;
    ViewSwitcher vs;
    DatabaseReference dr;
    CircleImageView civ;
    FirebaseAuth mAuth;
    FirebaseUser user;
    AvatarView userinitials;

    String uri;

    boolean ischange;

    Button logout;

    String role;

    String chapterid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        setTitle("Edit settings");
        final Drawable upArrow = getResources().getDrawable(R.drawable.exit);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mstorageimage = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");

        String childid="";
        if(role.equals("Adviser")){
            childid = "Advisers";
        }else if(role.equals("Member")||role.equals("Officer")){
            childid = "Users";
        }

        dr = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapterid).child(childid).child(mAuth.getCurrentUser().getUid());
        f = findViewById(R.id.f);
        l = findViewById(R.id.l);
        position = findViewById(R.id.position);

        f.setText(sp.getString(getString(R.string.fname), "fname"));
        l.setText(sp.getString(getString(R.string.lname), "lname"));
        position.setText(sp.getString(getString(R.string.role), "role"));
        position.setEnabled(false);
        position.setKeyListener(null);

        camclick = findViewById(R.id.camclick);

        camclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLEY_REQUEST);
            }
        });

        vs = findViewById(R.id.vsetting);
        userinitials = findViewById(R.id.profpicset);
        civ = findViewById(R.id.circset);

        uri = sp.getString(getString(R.string.profpic), "profpic");
        if (uri.equals("nocustomimage")) {
            userinitials.bind(f.getText().toString() + " " + l.getText().toString(), null);
        } else {
            vs.showNext();
            Glide.with(getApplicationContext()).load(uri).into(civ);
        }

        logout = findViewById(R.id.lo);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dr.child("online").setValue(ServerValue.TIMESTAMP);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Settings.this, LockScreen.class));
                finish();
            }
        });

        email = findViewById(R.id.em);
        email.setText(sp.getString(getString(R.string.email), "email"));
        email.setEnabled(false);
        email.setKeyListener(null);

        phone = findViewById(R.id.phone);
        website = findViewById(R.id.web);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            Intent i = new Intent(Settings.this, Profile.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if(item.getItemId() == R.id.check){
            final String userid = mAuth.getCurrentUser().getUid();
            if(ischange){
                final StorageReference filepath = mstorageimage.child(mImageuri.getLastPathSegment());

                SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.profpic), mImageuri.toString());
                editor.apply();

                Task<UploadTask.TaskSnapshot> uploadTask;
                uploadTask = filepath.putFile(mImageuri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final Uri downloadUri = task.getResult();

                            dr.child("profpic").setValue(downloadUri.toString());
                        }
                    }
                });
            }

            dr.child("fname").setValue(f.getText().toString());
            dr.child("lname").setValue(l.getText().toString());

            Intent i = new Intent(Settings.this, Profile.class);
            startActivity(i);
            //overridePendingTransition(R.anim.slide_in_out, R.anim.slide_in_up);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setFixAspectRatio(true).setAspectRatio(1, 1).
                    setGuidelines(CropImageView.Guidelines.ON).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageuri = result.getUri();

                if(uri.equals("nocustomimage")){
                    vs.showNext();
                    Glide.with(getApplicationContext()).load(mImageuri).into(civ);
                }else{
                    Glide.with(getApplicationContext()).load(mImageuri).into(civ);
                }


                ischange = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
