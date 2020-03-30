package olyapps.sathv.fbla2020;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class Setup extends AppCompatActivity {

    CircleImageView imageButton;
    Button done;
    TextView fnamesetup, lnamesetup, emailsetup;

    private static final int GALLEY_REQUEST = 1;

    FirebaseAuth mAuth;
    FirebaseUser user;

    Uri mImageuri = null;
    StorageReference mstorageimage;

    DatabaseReference dr;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mstorageimage = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        imageButton = findViewById(R.id.profimage);
        done = findViewById(R.id.donebutton);
        fnamesetup = findViewById(R.id.fnamesetup);
        lnamesetup = findViewById(R.id.lnamesetup);
        emailsetup = findViewById(R.id.emailsetup);
        mProgress = new ProgressDialog(this);

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String firstnae = sp.getString(getString(R.string.fname), "fname");
        String lastname = sp.getString(getString(R.string.lname), "lname");
        String emailname = sp.getString(getString(R.string.email), "email");


        dr = FirebaseDatabase.getInstance().getReference().child("Users");

        fnamesetup.setText(firstnae);
        lnamesetup.setText(lastname);
        emailsetup.setText(emailname);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLEY_REQUEST);

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startaccountsetup();
            }
        });


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
                imageButton.setImageURI(mImageuri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void startaccountsetup() {
        String replace;
        final String userid = mAuth.getCurrentUser().getUid();
        mProgress.setMessage("Finishing setup...");
        mProgress.show();
        if (mImageuri == null) {
            replace = "nocustomimage";
            dr.child(userid).child("profpic").setValue(replace);

            SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(getString(R.string.profpic), "nocustomimage");

            mProgress.dismiss();
            Intent intent = new Intent(getApplicationContext(), Instructions.class);
            startActivity(intent);
            finish();
        } else {

            final StorageReference filepath = mstorageimage.child(mImageuri.getLastPathSegment());


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

                        mProgress.dismiss();

                        dr.child(userid).child("profpic").setValue(downloadUri.toString());

                        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(getString(R.string.profpic), downloadUri.toString());

                        Intent intent = new Intent(getApplicationContext(), Instructions.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });


        }
    }
}
