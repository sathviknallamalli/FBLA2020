package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import olyapps.sathv.fbla2020.adapter.MyArrayAdapter;
import olyapps.sathv.fbla2020.model.MyDataModel;
import olyapps.sathv.fbla2020.parser.JSONParser;
import olyapps.sathv.fbla2020.util.Keys;


public class CreateFBLAAccount extends AppCompatActivity {

    private static final int RC_SIGN_IN =1 ;
    EditText fname, lname, email, username, password, cpd;

    String firstname, lastname, enteredemail, enteredusername, enteredpassword, enteredgradyear;

    String theirrole;

    private FirebaseAuth mAuth;

    Spinner gradyear;

    Button submit;

    Pattern p;
    Matcher m;

    ProgressBar pbc;

    Pattern numberp;
    Matcher numberm;

    ArrayAdapter<CharSequence> adapter;
    MyArrayAdapter adaptera;
    CallbackManager mCallbackManager;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("DARBAR", "Google sign in failed", e);
                // ...
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_fblaaccount);
        mAuth = FirebaseAuth.getInstance();
        setTitle("Create an Account");
        submit = findViewById(R.id.submitted);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ImageView chapterlogo = findViewById(R.id.cchapterlogo);
        Intent i = getIntent();
        String imageurl = i.getExtras().getString("chapterlogourl");
        theirrole = i.getExtras().getString("role");

        Glide.with(getApplicationContext()).load(imageurl).into(chapterlogo);


        LinearLayout linlayout = findViewById(R.id.linlayout);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(linlayout.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        adaptera = new MyArrayAdapter(CreateFBLAAccount.this, LockScreen.staticlist);
        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.lastname);
        email = findViewById(R.id.emailincreate);
        username = findViewById(R.id.username);
        password = findViewById(R.id.passwordincreate);
        cpd = findViewById(R.id.confirmpassword);
        gradyear = findViewById(R.id.gradyear);

        fname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        lname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        pbc = findViewById(R.id.pbc);
        pbc.setVisibility(View.INVISIBLE);

        adapter = ArrayAdapter.createFromResource(this, R.array.gradyear, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradyear.setAdapter(adapter);
        gradyear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                //parent.getItemAtPosition(position)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        gradyear.setSelection(0);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("368403097563-9dal8aj1oodelcnghrvlpv83qqrvr4oh.apps.googleusercontent.com")
                .requestEmail()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton createwithg = findViewById(R.id.createwithg);
        createwithg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.createwithfb);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("DARBAR", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("DARBAR", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("DARBAR", "facebook:onError", error);
                // ...
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECK DUPLICATE NAME
                firstname = fname.getText().toString();
                lastname = lname.getText().toString();
                enteredemail = email.getText().toString();
                enteredusername = username.getText().toString();
                enteredpassword = password.getText().toString();
                enteredgradyear = gradyear.getSelectedItem().toString();

                p = Pattern.compile("[^A-Za-z0-9]");
                m = p.matcher(enteredpassword);

                numberp = Pattern.compile("([0-9])");
                numberm = numberp.matcher(enteredpassword);

                if (firstname.isEmpty() || lastname.isEmpty() || enteredemail.isEmpty() || enteredusername.isEmpty() ||
                        enteredpassword.isEmpty() || enteredgradyear.equals("Select your graduation year")) {
                    Toast.makeText(CreateFBLAAccount.this, "Missing field(s)", Toast.LENGTH_SHORT).show();
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(enteredemail).matches()) {
                        Toast.makeText(CreateFBLAAccount.this, "The email you entered is invalid", Toast.LENGTH_SHORT).show();
                    } else if (!enteredpassword.equals(cpd.getText().toString())) {
                        Toast.makeText(CreateFBLAAccount.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    } else if (enteredusername.equals(firstname) || enteredusername.equals(lastname)) {
                        Toast.makeText(CreateFBLAAccount.this, "Invalid username", Toast.LENGTH_SHORT).show();
                    } else if (firstname.contains(" ") || firstname.contains("\\s+") || lastname.contains(" ") || lastname.contains("\\s+")) {
                        Toast.makeText(CreateFBLAAccount.this, "Names cannot contain spaces", Toast.LENGTH_SHORT).show();
                    } else if (!m.find()) {
                        Toast.makeText(getApplicationContext(), "Password must contain a special character", Toast.LENGTH_SHORT).show();
                    } else if (!numberm.find()) {
                        Toast.makeText(getApplicationContext(), "Password must contain a number", Toast.LENGTH_SHORT).show();
                    } else if (firstname.contains("SEPERATOR") || lastname.contains("SEPERATOR")) {
                        Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
                    } else if (firstname.contains("THEUID") || lastname.contains("THEUID")) {
                        Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
                    } else if (firstname.matches(".*\\d+.*") || lastname.matches(".*\\d+.*")) {
                        Toast.makeText(getApplicationContext(), "Names cannot contain numbers", Toast.LENGTH_SHORT).show();
                    } else {


                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateFBLAAccount.this);
                        builder.setCancelable(false);
                        builder.setTitle("Privacy Policy");
                        builder.setMessage("This app utilizes the Firebase Services. It includes utilizing the Firebase Database and Authentication and Notification Services. It will collect personal information such as name, email, username, password, and device ID to complete the necessary actions. If you agree to these terms, click Agree below to proceed.");
                        builder.setPositiveButton("Agree",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pbc.setVisibility(View.VISIBLE);
                                        mAuth.createUserWithEmailAndPassword(enteredemail, enteredpassword)
                                                .addOnCompleteListener(CreateFBLAAccount.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            updateUI(user, null, null);
                                                            sendEmailtoAdvisers();
                                                        } else {
                                                            Toast.makeText(CreateFBLAAccount.this, "Signup failed",
                                                                    Toast.LENGTH_SHORT).show();
                                                            updateUI(null, null, null);
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

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("DARBAR", "firebaseAuthWithGoogle:" + acct.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DARBAR", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, acct, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("DARBAR", "signInWithCredential:failure", task.getException());
                            updateUI(null, null, null);
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d("DARBAR", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DARBAR", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, null, token);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("DARBAR", "signInWithCredential:failure", task.getException());

                            updateUI(null, null, null);
                        }

                        // ...
                    }
                });
    }


    public void sendEmailtoAdvisers() {
        Intent i = getIntent();
        String chapid = i.getExtras().getString("chapterid");

        DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("Advisers");

        d.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> emails = collectCertainField((Map<String, Object>) dataSnapshot.getValue(), "email");
                for (int i = 0; i < emails.size(); i++) {
                    String subject = "New Member";
                    String message = "A new member is joining your FBLA Chapter" +
                            "Here is the student's information.\n\nFirst name: " + firstname + "\nLast name: " + lastname +
                            "\nEmail: " + enteredemail + "\nUser ID: " + mAuth.getUid() + "\nTheir role: " + theirrole +
                            "\nTo approve this member, go to the Approvals page in the app and change their status";
                    SendMail sm = new SendMail(CreateFBLAAccount.this, emails.get(i), subject, message);
                    sm.execute();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<String> collectCertainField(Map<String, Object> users, String whatyouwant) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list

            if (singleUser != null) {
                information.add((String) singleUser.get(whatyouwant));
            }

        }

        return information;
    }



    private void updateUI(final FirebaseUser user, GoogleSignInAccount account, AccessToken token) {
        if (user != null) {

            String userid = user.getUid();

            //Get the role that the user already confirmed in the MemberRole class.
            Intent intent = getIntent();
            String enteredrole = intent.getExtras().getString("role");
            final String chapterid = intent.getExtras().getString("chapterid");

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid).child("Users")
                    .child(userid);

            String FULLNAME = "";
            String logintype = "";

            if(account!=null & token==null){
                //GMAIL USER
                logintype = "GMAIL";
                FULLNAME = account.getDisplayName();
                dr.child("fname").setValue(account.getGivenName());
                dr.child("lname").setValue(account.getFamilyName());
                dr.child("email").setValue(account.getEmail());
                dr.child("profpic").setValue(account.getPhotoUrl());
                //GET USERNAME AND GRAD YEAR FROM SETUP ACTIVITY
                dr.child("accounttype").setValue("GMAIL");
                dr.child("role").setValue(enteredrole);
                dr.child("uid").setValue(userid);
                dr.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());

                SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString(getString(R.string.fname), account.getGivenName());
                editor.putString(getString(R.string.lname), account.getDisplayName());
                editor.putString(getString(R.string.email), account.getEmail());
                //GET USERNAME AND GRAD YEAR INTO SAVED PREFS OF SETUP ACTIVITY
                editor.putString(getString(R.string.role), enteredrole);
                editor.putString(getString(R.string.uid), userid);
                editor.putString(getString(R.string.deviceToken), FirebaseInstanceId.getInstance().getToken());

                editor.apply();

            }else if(account==null && token!=null){
              //FACEBOOK USER
                logintype = "FACEBOOK";
            } else if (account==null && token==null){
                //LOCAL USER
                logintype = "LOCAL";
                FULLNAME = firstname + " " + lastname;
                dr.child("fname").setValue(firstname);
                dr.child("lname").setValue(lastname);
                dr.child("email").setValue(enteredemail);
                dr.child("password").setValue(enteredpassword);
                dr.child("username").setValue(enteredusername);
                dr.child("graduationyear").setValue(enteredgradyear);
                dr.child("accounttype").setValue("LOCAL");
                dr.child("role").setValue(enteredrole);
                dr.child("uid").setValue(userid);
                dr.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());

                SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString(getString(R.string.fname), firstname);
                editor.putString(getString(R.string.lname), lastname);
                editor.putString(getString(R.string.email), enteredemail);
                editor.putString(getString(R.string.password), enteredpassword);
                editor.putString(getString(R.string.username), enteredusername);
                editor.putString(getString(R.string.role), enteredrole);
                editor.putString(getString(R.string.grade), enteredgradyear);
                editor.putString(getString(R.string.uid), userid);
                editor.putString(getString(R.string.deviceToken), FirebaseInstanceId.getInstance().getToken());

                editor.apply();
            }




            DatabaseReference devtokens = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid).child("Advisers");
            final String finalFULLNAME = FULLNAME;
            devtokens.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> adevtoks = collectCertainField((Map<String, Object>) dataSnapshot.getValue(), "uid");

                    for (int i = 0; i < adevtoks.size(); i++) {
                        FirebaseDatabase.getInstance().getReference().child("Chapters").child("5791").child("Advisers").child(adevtoks.get(i))
                                .child("Notifications").push().child("Title").setValue("New Member");
                        FirebaseDatabase.getInstance().getReference().child("Chapters").child("5791").child("Advisers").child(adevtoks.get(i))
                                .child("Notifications").push().child("Message").setValue("You have a new user joining your chapter. Member name: " + finalFULLNAME);

                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





           /* LockScreen ls = new LockScreen();
            ls.getallevents();
            ls.getallpeeps(firstname, lastname, enteredemail);
            ls.getGroups();
            if (InternetConnection.checkConnection(CreateFBLAAccount.this)) {
                new GetDataTask().execute();
            }*/


            final String finalLogintype = logintype;
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // email sent
                                Intent intent = new Intent(getApplicationContext(), Setup.class);
                                intent.putExtra("chapid", chapterid);
                                intent.putExtra("logintype", finalLogintype);
                                startActivity(intent);
                                Toast.makeText(CreateFBLAAccount.this, "A verification email has been sent," +
                                        " please check the entered email and verify your account", Toast.LENGTH_LONG).show();
                                pbc.setVisibility(View.GONE);
                                finish();
                            } else {
                                // email not sent, so display list_item and restart the activity or do whatever you wish to do

                            }
                        }
                    });
        } else {
            pbc.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(CreateFBLAAccount.this, MemberRole.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(CreateFBLAAccount.this, MemberRole.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        int jIndex;
        int x;

        // LockScreen.staticlist = new ArrayList<MyDataModel>;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */
            x = LockScreen.staticlist.size();

            if (x == 0)
                jIndex = 0;
            else
                jIndex = x;

            pbc.setVisibility(View.VISIBLE);
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            /**
             * Getting JSON Object from Web Using okHttp
             */
            JSONObject jsonObject = JSONParser.getDataFromWeb();

            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonObject != null) {
                    /**
                     * Check Length...
                     */
                    if (jsonObject.length() > 0) {
                        /**
                         * Getting Array named "contacts" From MAIN Json Object
                         */
                        JSONArray array = jsonObject.getJSONArray(Keys.KEY_CONTACTS);

                        /**
                         * Check Length of Array...
                         */


                        int lenArray = array.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {

                                /**
                                 * Creating Every time New Object
                                 * and
                                 * Adding into List
                                 */
                                MyDataModel model = new MyDataModel();

                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String firstname = innerObject.getString(Keys.KEY_FIRSTNAME);
                                String lastname = innerObject.getString(Keys.KEY_LASTNAME);
                                String graduation = innerObject.getString(Keys.KEY_GRADYEAR);
                                String shirtsize = innerObject.getString(Keys.KEY_SHIRTSIZE);
                                String clubdues = innerObject.getString(Keys.KEY_CLUBDUE);
                                String fallc = innerObject.getString(Keys.KEY_FALLC);
                                String winterc = innerObject.getString(Keys.KEY_WINTERC);
                                String winterp = innerObject.getString(Keys.KEY_WINTERP);

                                /**
                                 * Getting Object from Object "phone"
                                 */
                                //JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
                                //String phone = phoneObject.getString(Keys.KEY_MOBILE);

                                model.setFirstname(firstname);
                                model.setLastname(lastname);
                                model.setGraduation(graduation);
                                model.setShirtsize(shirtsize);
                                model.setClubdues(clubdues);
                                model.setFallc(fallc);
                                model.setWinterc(winterc);
                                model.setWinterpermission(winterp);
                                /**
                                 * Adding yourname and phone concatenation in List...
                                 */
                                LockScreen.staticlist.add(model);
                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbc.setVisibility(View.GONE);
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            if (LockScreen.staticlist.size() > 0) {
                adaptera.notifyDataSetChanged();
            }
        }
    }



}
