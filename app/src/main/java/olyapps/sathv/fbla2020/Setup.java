package olyapps.sathv.fbla2020;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

    private static final int GALLEY_REQUEST = 1;

    FirebaseAuth mAuth;
    FirebaseUser user;

    Uri mImageuri = null;
    StorageReference mstorageimage;



    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Intent i = getIntent();
        final String logintype = i.getExtras().getString("logintype");

        final Spinner grad = findViewById(R.id.setupgrad);
        final EditText username = findViewById(R.id.setupusername);

        mstorageimage = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        imageButton = findViewById(R.id.profimage);
        done = findViewById(R.id.donebutton);

        if(logintype.equals("GMAIL")){
            grad.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.GONE);
        } else if(logintype.equals("LOCAL")){
            grad.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gradyear, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grad.setAdapter(adapter);
        grad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                //parent.getItemAtPosition(position)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        grad.setSelection(0);

        mProgress = new ProgressDialog(this);



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

                if(logintype.equals("LOCAL")){
                    startaccountsetup();
                } else if(logintype.equals("GMAIL")){

                    if(username.getText().toString().isEmpty() || grad.getSelectedItem().toString().equals("Select your graduation year")){
                        Toast.makeText(Setup.this, "Missing field(s)", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent i = getIntent();
                        String chapid = i.getExtras().getString("chapid");

                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("Users");
                        dr.child(mAuth.getCurrentUser().getUid()).child("username").setValue(username.getText().toString());
                        dr.child(mAuth.getCurrentUser().getUid()).child("graduationyear").setValue(grad.getSelectedItem().toString());

                        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString(getString(R.string.username), username.getText().toString());
                        editor.putString(getString(R.string.grade), grad.getSelectedItem().toString());

                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), Instructions.class);
                        startActivity(intent);
                        finish();
                    }


                }

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
       final String userid = mAuth.getCurrentUser().getUid();
        mProgress.setMessage("Finishing setup...");
        mProgress.show();
        if (mImageuri == null) {

            Intent i = getIntent();
            String chapid = i.getExtras().getString("chapid");

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("Users");
            dr.child(userid).child("profpic").setValue("nocustomimage");

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

                        Intent i = getIntent();
                        String chapid = i.getExtras().getString("chapid");

                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("Users");
                        dr.child(userid).child("profpic").setValue(downloadUri.toString());

                        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(getString(R.string.profpic), downloadUri.toString());
                        editor.apply();

                        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editorchap = spchap.edit();
                        editorchap.putString("chapterID", chapid);
                        editorchap.apply();

                        Intent intent = new Intent(getApplicationContext(), Instructions.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });


        }
    }
}
