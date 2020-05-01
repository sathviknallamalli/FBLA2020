package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdviserAccount extends AppCompatActivity {

    EditText adviserfn, adviserln, adviserun, adviseremail, adviserpd, advisercpd;
    Button advisersubmit;
    ProgressBar apbc;

    Pattern p;
    Matcher m;

    FirebaseAuth mAuth;

    Pattern numberp;
    Matcher numberm;

    ImageView adviserprofpic;
    Uri mImageuri = null;
    private static final int GALLEY_REQUEST = 1;

    StorageReference mstorageimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adviser_account);

        mAuth = FirebaseAuth.getInstance();

        adviserfn = findViewById(R.id.afn);
        adviserln = findViewById(R.id.aln);
        adviserun = findViewById(R.id.aun);
        adviseremail = findViewById(R.id.aemail);
        adviserpd = findViewById(R.id.apd);
        advisercpd = findViewById(R.id.acpd);

        advisersubmit = findViewById(R.id.asubmit);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        LinearLayout regll = findViewById(R.id.linlayoutaa);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(regll.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        Intent intent = getIntent();
        final String id = intent.getExtras().getString("chapterid");

        apbc = findViewById(R.id.apbc);
        apbc.setVisibility(View.INVISIBLE);


        adviserprofpic = findViewById(R.id.adviserprofpic);
        adviserprofpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLEY_REQUEST);
            }
        });

        mstorageimage = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        advisersubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                p = Pattern.compile("[^A-Za-z0-9]");
                m = p.matcher(adviserpd.getText().toString());

                numberp = Pattern.compile("([0-9])");
                numberm = numberp.matcher(adviserpd.getText().toString());

                if (adviserfn.getText().toString().isEmpty() || adviserln.getText().toString().isEmpty() ||
                        adviserun.getText().toString().isEmpty() || adviseremail.getText().toString().isEmpty() ||
                        adviserpd.getText().toString().isEmpty() || advisercpd.getText().toString().isEmpty()) {
                    Toast.makeText(AdviserAccount.this, "Missing field(s)", Toast.LENGTH_SHORT).show();
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(adviseremail.getText().toString()).matches()) {
                        Toast.makeText(AdviserAccount.this, "The email you entered is invalid", Toast.LENGTH_SHORT).show();
                    } else if (!adviserpd.getText().toString().equals(advisercpd.getText().toString())) {
                        Toast.makeText(AdviserAccount.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    } else if (adviserun.getText().toString().equals(adviserfn.getText().toString()) || adviserun.getText().toString().equals(adviserln.getText().toString()) ) {
                        Toast.makeText(AdviserAccount.this, "Invalid username", Toast.LENGTH_SHORT).show();
                    } else if (adviserfn.getText().toString().contains(" ") || adviserfn.getText().toString().contains("\\s+") || adviserln.getText().toString().contains(" ") || adviserln.getText().toString().contains("\\s+")) {
                        Toast.makeText(AdviserAccount.this, "Names cannot contain spaces", Toast.LENGTH_SHORT).show();
                    } else if (!m.find()) {
                        Toast.makeText(getApplicationContext(), "Password must contain a special character", Toast.LENGTH_SHORT).show();
                    } else if (!numberm.find()) {
                        Toast.makeText(getApplicationContext(), "Password must contain a number", Toast.LENGTH_SHORT).show();
                    } else if (adviserfn.getText().toString().contains("SEPERATOR") || adviserln.getText().toString().contains("SEPERATOR")) {
                        Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
                    } else if (adviserfn.getText().toString().contains("THEUID") || adviserln.getText().toString().contains("THEUID")) {
                        Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
                    } else if (adviserfn.getText().toString().matches(".*\\d+.*") || adviserln.getText().toString().matches(".*\\d+.*")) {
                        Toast.makeText(getApplicationContext(), "Names cannot contain numbers", Toast.LENGTH_SHORT).show();
                    } else {



                        AlertDialog.Builder builder = new AlertDialog.Builder(AdviserAccount.this);
                        builder.setCancelable(false);
                        builder.setTitle("Privacy Policy");
                        builder.setMessage("This app utilizes the Firebase Services. It includes utilizing the Firebase Database and Authentication and Notification Services. It will collect personal information such as name, email, username, password, and device ID to complete the necessary actions. If you agree to these terms, click Agree below to proceed.");
                        builder.setPositiveButton("Agree",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        apbc.setVisibility(View.VISIBLE);
                                        mAuth.createUserWithEmailAndPassword(adviseremail.getText().toString(), adviserpd.getText().toString())
                                                .addOnCompleteListener(AdviserAccount.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            updateUI(user);

                                                            SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editorchap = spchap.edit();

                                                            editorchap.putString("chapterID",id);
                                                            editorchap.apply();

                                                            Intent intent = new Intent(getApplicationContext(), RolesStep.class);
                                                            intent.putExtra("chapterid", id);
                                                            intent.putExtra("adviseremail", adviseremail.getText().toString());
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                                                            finish();


                                                        } else {
                                                            Toast.makeText(AdviserAccount.this, "Signup failed",
                                                                    Toast.LENGTH_SHORT).show();
                                                            updateUI(null);
                                                        }

                                                        // ...
                                                    }
                                                });

                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();

                    }
                }


            }
        });

    }

    private void updateUI(final FirebaseUser user) {
        Intent intent = getIntent();
        final String id = intent.getExtras().getString("chapterid");

        final DatabaseReference chapter = FirebaseDatabase.getInstance().getReference().child("Chapters").child(id);

        chapter.child("Advisers").child(user.getUid()).child("uid").setValue(user.getUid());
        chapter.child("Advisers").child(user.getUid()).child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());
        chapter.child("Advisers").child(user.getUid()).child("fname").setValue(adviserfn.getText().toString());
        chapter.child("Advisers").child(user.getUid()).child("lname").setValue(adviserln.getText().toString());
        chapter.child("Advisers").child(user.getUid()).child("email").setValue(adviseremail.getText().toString());
        chapter.child("Advisers").child(user.getUid()).child("username").setValue(adviserun.getText().toString());
        chapter.child("Advisers").child(user.getUid()).child("password").setValue(adviserpd.getText().toString());
        chapter.child("Advisers").child(user.getUid()).child("accounttype").setValue("LOCAL");

        final DatabaseReference devtokens = FirebaseDatabase.getInstance().getReference().child("Chapters").child(id).child("Advisers");
        devtokens.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> adevtoks = collectemails((Map<String, Object>) dataSnapshot.getValue(), "device_token");
                String add = "";
                for (int i = 0; i < adevtoks.size(); i++) {
                    add += adevtoks.get(i) + ",";
                }
                devtokens.child("device_tokens").setValue(add);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        FirebaseMessaging.getInstance().subscribeToTopic(id + "Advisers")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                    }
                });



        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            Toast.makeText(AdviserAccount.this, "A verification email has been sent," +
                                    " please check the entered email and verify your account", Toast.LENGTH_LONG).show();
                          } else {
                            // email not sent, so display list_item and restart the activity or do whatever you wish to do

                        }
                    }
                });

        if(mImageuri!=null){
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

                        chapter.child("Advisers").child(user.getUid()).child("profpic").setValue(downloadUri.toString());
                    }
                }
            });
        }else{
            chapter.child("Advisers").child(user.getUid()).child("profpic").setValue("nocustomimage");
        }


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
                adviserprofpic.setImageURI(mImageuri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private ArrayList<String> collectemails(Map<String, Object> users, String whatyouwant) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            if(!entry.getKey().toString().equals("device_tokens")){
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list

                if (singleUser != null) {
                    information.add((String) singleUser.get(whatyouwant));
                }
            }
            //Get user map

        }

        return information;
    }
}
