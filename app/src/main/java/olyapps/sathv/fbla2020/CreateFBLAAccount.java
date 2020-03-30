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
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

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
import olyapps.sathv.fbla2020.util.InternetConnection;
import olyapps.sathv.fbla2020.util.Keys;


public class CreateFBLAAccount extends AppCompatActivity {

    EditText fname, lname, email, username, password, cpd;

    String firstname, lastname, enteredemail, enteredusername, enteredpassword, enteredgradyear;

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

    DatabaseReference mDevTokens;

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
                        enteredpassword.isEmpty() || enteredgradyear.equals("Please select your highschool gradyear")) {
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
                                                            updateUI(user);
                                                           // sendemailtoallofficers();
                                                        } else {
                                                            Toast.makeText(CreateFBLAAccount.this, "Signup failed",
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


                    }
                }


            }
        });
    }

    public void sendemailtoallofficers() {
        DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("Users");

        d.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> emails = collectemails((Map<String, Object>) dataSnapshot.getValue());
                for (int i = 0; i < emails.size(); i++) {
                    String subject = "New User Approval";
                    String message = "A new user is trying to create an account to signup for the app, OHS FBLA.\n" +
                            "Here is the student's information.\n\nFirst yourname: " + firstname + "\nLast yourname: " + lastname +
                            "\nEmail: " + enteredemail + "\nUID: " + mAuth.getUid() + "\nTheir role: "  + "\nIf you approve of this person, go to the Firebase Console and " +
                            "change their STATUS from 0 to 1.\nIf you dont approve, keep the STATUS at 0.";
                    SendMail sm = new SendMail(CreateFBLAAccount.this, emails.get(i), subject, message);
                    sm.execute();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<String> collectemails(Map<String, Object> users) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list

            if (singleUser != null) {
                if (singleUser.get("role").toString().equals("Officer")) {
                    information.add((String) singleUser.get("email"));
                } else {

                }
            }

        }

        return information;
    }

    private void updateUI(final FirebaseUser user) {
        if (user != null) {

            String userid = user.getUid();

            //Get the role that the user already confirmed in the MemberRole class.
            Intent intent = getIntent();
            String enteredrole = intent.getExtras().getString("role");
            String chapterid = intent.getExtras().getString("chapterid");

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid).child("Users")
                    .child(userid);

            //ADD new user to chapter database
            dr.child("fname").setValue(firstname);
            dr.child("lname").setValue(lastname);
            dr.child("email").setValue(enteredemail);
            dr.child("password").setValue(enteredpassword);
            dr.child("username").setValue(enteredusername);
            dr.child("graduationyear").setValue(enteredgradyear);
            dr.child("role").setValue(enteredrole);
            dr.child("uid").setValue(userid);
            dr.child("status").setValue("0");
            dr.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());


            //SEND NOTIFICATION TO ADVISER ABOUT NEW USER
            /*DatabaseReference drip = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference update = FirebaseDatabase.getInstance().getReference().child("notificationsNewUser");


            drip.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> roll = officerdevicetokens((Map<String, Object>) dataSnapshot.getValue());
                    String add = "";
                    for (int i = 0; i < roll.size(); i++) {
                        add = add + roll.get(i).toString() + ",";
                    }
                    update.child("e2045442-9c2e-11e8-98d0-529269fb1459").child("officerdevicetokens").setValue(add);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/


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


            LockScreen ls = new LockScreen();
            ls.getallevents();
            ls.getallpeeps(firstname, lastname, enteredemail);
            ls.getGroups();
            if (InternetConnection.checkConnection(CreateFBLAAccount.this)) {
                new GetDataTask().execute();
            }

            firstname = firstname.replace("\\s+", "");
            lastname = lastname.replace("\\s+", "");
            firstname = firstname.replace(" ", "");
            lastname = lastname.replace(" ", "");
            FirebaseMessaging.getInstance().subscribeToTopic(firstname + lastname)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            }
                        }
                    });

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // email sent
                                Intent intent = new Intent(getApplicationContext(), Setup.class);
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

    private ArrayList<String> officerdevicetokens(Map<String, Object> users) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list

            if (singleUser.get("role").toString().equals("Officer")) {
                if (singleUser.get("device_token") != null && !singleUser.get("device_token").toString().isEmpty()) {
                    information.add(singleUser.get("device_token").toString());
                }
            }
        }

        return information;
    }

}
