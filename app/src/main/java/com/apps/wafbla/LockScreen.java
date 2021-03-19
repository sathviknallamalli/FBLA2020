package com.apps.wafbla;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class LockScreen extends AppCompatActivity {

    Button login;
    EditText email, password;

    private static final int RC_SIGN_IN = 1;

    ProgressBar pbl;

    private FirebaseAuth mAuth;

    String enteredemail, enteredpassword;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences sp = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        String id = sp.getString("chapterID", "tempid");
        if (!id.equals("tempid")) {
            updateUI(currentUser);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        mAuth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pbl = findViewById(R.id.pbls);
        // pbl.setVisibility(View.VISIBLE);


        login = findViewById(R.id.loginbtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbl.setVisibility(View.VISIBLE);
                enteredemail = email.getText().toString();
                enteredpassword = password.getText().toString();

                if (enteredemail.isEmpty() || enteredpassword.isEmpty()) {
                    Toast.makeText(LockScreen.this, "Missing field(s)", Toast.LENGTH_SHORT).show();
                    pbl.setVisibility(View.GONE);
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(enteredemail).matches()) {
                        Toast.makeText(LockScreen.this, "The email you entered is invalid", Toast.LENGTH_SHORT).show();
                        pbl.setVisibility(View.GONE);
                    } else {
                        login.setEnabled(false);

                        mAuth.signInWithEmailAndPassword(enteredemail, enteredpassword)
                                .addOnCompleteListener(LockScreen.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            final FirebaseUser user = mAuth.getCurrentUser();

                                            secondlevelauth(user);


                                        } else {
                                            if (!isNetworkConnected()) {
                                                Toast.makeText(LockScreen.this, "You are not connected to the Internet",
                                                        Toast.LENGTH_SHORT).show();
                                                updateUI(null);
                                                pbl.setVisibility(View.GONE);
                                                login.setEnabled(true);
                                            } else {
                                                Toast.makeText(LockScreen.this, "Login failed",
                                                        Toast.LENGTH_SHORT).show();
                                                updateUI(null);
                                                login.setEnabled(true);
                                                pbl.setVisibility(View.GONE);
                                            }

                                        }
                                    }
                                });
                    }
                }

            }
        });


        TextView fp = findViewById(R.id.forgotpassword);
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredemail = email.getText().toString();
                if (!TextUtils.isEmpty(enteredemail)) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(enteredemail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LockScreen.this, "Reset email has been sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LockScreen.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LockScreen.this, "Enter email", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void secondlevelauth(final FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LockScreen.this);
        builder.setTitle("Chapter ID");

        final EditText input = new EditText(LockScreen.this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setCancelable(false);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                isuserexists(user, input.getText().toString(), new MyCallback() {
                    @Override
                    public void onCallback(Boolean isExists, String who) {
                        if (isExists) {
                            SharedPreferences sp = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("chapterID", input.getText().toString());
                            editor.putString("who", who);
                            editor.apply();


                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            pbl.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(LockScreen.this, "Login failed",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            pbl.setVisibility(View.GONE);
                            login.setEnabled(true);
                        }
                    }

                    @Override
                    public void callbackGroups(ArrayList<String> groups) {

                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                pbl.setVisibility(View.GONE);
                login.setEnabled(true);
            }
        });

        builder.show();
    }


    public void isuserexists(final FirebaseUser user, final String enteredchapid, final MyCallback myCallback) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Chapters");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                if (snapshot.hasChild(enteredchapid)) {
                    rootRef.child(enteredchapid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Users").hasChild(user.getUid())) {
                                myCallback.onCallback(true, "member");
                            } else if (dataSnapshot.child("Advisers").hasChild(user.getUid())) {
                                myCallback.onCallback(true, "adviser");
                            } else {
                                myCallback.onCallback(false, "no one");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                } else {
                    Toast.makeText(LockScreen.this, "Login failed, invalid chapter ID",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                    pbl.setVisibility(View.GONE);
                    login.setEnabled(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateUI(final FirebaseUser user) {

        if (user != null) {


            SharedPreferences sp = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
            final String chapid = sp.getString("chapterID", "tempid");
            final String[] who = {sp.getString("who", "tempwho")};
            final DatabaseReference[] mDatabase = {null};

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("Chapters")
                    .child(chapid).child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        who[0] = "member";
                        mDatabase[0] = FirebaseDatabase.getInstance().getReference().child("Chapters")
                                .child(chapid).child("Users").child(user.getUid());
                    } else {
                        who[0] = "adviser";
                        mDatabase[0] = FirebaseDatabase.getInstance().getReference().child("Chapters")
                                .child(chapid).child("Advisers").child(user.getUid());
                    }

                    final DatabaseReference finalMDatabase = mDatabase[0];
                    mDatabase[0].addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                editor.putString(getString(R.string.fname), dataSnapshot.child("fname").getValue().toString());
                                editor.putString(getString(R.string.lname), dataSnapshot.child("lname").getValue().toString());
                                editor.putString(getString(R.string.email), dataSnapshot.child("email").getValue().toString());

                                editor.putString(getString(R.string.username), dataSnapshot.child("username").getValue().toString());

                                if (who[0].equals("adviser")) {
                                    editor.putString(getString(R.string.role), "Adviser");


                                } else {
                                    editor.putString(getString(R.string.grade), dataSnapshot.child("graduationyear").getValue().toString());
                                    editor.putString(getString(R.string.role), dataSnapshot.child("role").getValue().toString());
                                }

                                editor.putString(getString(R.string.uid), dataSnapshot.child("uid").getValue().toString());
                                editor.putString(getString(R.string.deviceToken), dataSnapshot.child("device_token").getValue().toString());

                                if (dataSnapshot.hasChild("profpic")) {
                                    editor.putString(getString(R.string.profpic), dataSnapshot.child("profpic").getValue().toString());
                                } else {
                                    editor.putString(getString(R.string.profpic), "nocustomimage");
                                }
                                editor.apply();

                                finalMDatabase.child("online").setValue(true);
                                Log.d("DARBAR", "fremove" + dataSnapshot.child("fname").getValue().toString());



                                Intent intent = new Intent(LockScreen.this, Hello.class);
                                pbl.setVisibility(View.GONE);
                                startActivity(intent);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    pbl.setVisibility(View.GONE);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

    }


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null; // return true =(connected),false=(not connected)
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(LockScreen.this, SplashScreen.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
