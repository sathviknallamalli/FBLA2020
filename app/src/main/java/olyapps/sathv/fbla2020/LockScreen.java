package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import olyapps.sathv.fbla2020.adapter.MyArrayAdapter;
import olyapps.sathv.fbla2020.model.MyDataModel;
import olyapps.sathv.fbla2020.parser.JSONParser;
import olyapps.sathv.fbla2020.util.InternetConnection;
import olyapps.sathv.fbla2020.util.Keys;


public class LockScreen extends AppCompatActivity {

    Button login, create;
    EditText email, password;

    ProgressBar pbl;
    static ArrayList<MyDataModel> staticlist;

    private FirebaseAuth mAuth;

    String enteredemail, enteredpassword;
    MyArrayAdapter adapter;

    static int color;

    public static int getColor() {
        return color;
    }

    public static void setColor(int color) {
        LockScreen.color = color;
    }

    ArrayList<String> officeral = new ArrayList<>();
    ArrayList<String> advisoral = new ArrayList<>();
    ArrayList<String> on = new ArrayList<>();


    ArrayList<String> dts = new ArrayList<>();


    String[] dtsarray;

    public String[] getDtsarray() {
        return dtsarray;
    }

    public void setDtsarray(String[] dtsarray) {
        this.dtsarray = dtsarray;
    }

    static String[] adviarray;

    public static String[] getAdviarray() {
        return adviarray;
    }

    public static void setAdviarray(String[] adviarray) {
        LockScreen.adviarray = adviarray;
    }

    static String[] officall;

    public static String[] getOfficall() {
        return officall;
    }

    public static void setOfficall(String[] officall) {
        LockScreen.officall = officall;
    }

    static String[] gms;

    public static String[] getGms() {
        return gms;
    }

    public static void setGms(String[] gms) {
        LockScreen.gms = gms;
    }


    ArrayList<String> gs = new ArrayList<>();

    ArrayList<String> ts = new ArrayList<>();
    ArrayList<String> aus = new ArrayList<>();
    ArrayList<String> cas = new ArrayList<>();

    ArrayList<String> fn = new ArrayList<>();
    ArrayList<String> ln = new ArrayList<>();

    ArrayList<String> uidall = new ArrayList<>();

    static String[] tils, auths, cass;

    static String[] gsarray;

    public static String[] getGsarray() {
        return gsarray;
    }

    public static void setGsarray(String[] gsarray) {
        LockScreen.gsarray = gsarray;
    }

    public static String[] getTils() {
        return tils;
    }

    public static void setTils(String[] tils) {
        LockScreen.tils = tils;
    }

    public static String[] getAuths() {
        return auths;
    }

    public static void setAuths(String[] auths) {
        LockScreen.auths = auths;
    }

    public static String[] getCass() {
        return cass;
    }

    public static void setCass(String[] cass) {
        LockScreen.cass = cass;
    }

    static String[] fnas, lnas;

    static String[] uidasall;

    public static String[] getUidasall() {
        return uidasall;
    }

    public static void setUidasall(String[] uidasall) {
        LockScreen.uidasall = uidasall;
    }

    public static String[] getFnas() {
        return fnas;
    }

    public static void setFnas(String[] fnas) {
        LockScreen.fnas = fnas;
    }

    public static String[] getLnas() {
        return lnas;
    }

    public static void setLnas(String[] lnas) {
        LockScreen.lnas = lnas;
    }

    DatabaseReference mDatabase, mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        mAuth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);

        pbl = findViewById(R.id.pbls);
        pbl.setVisibility(View.VISIBLE);


        login = findViewById(R.id.loginbtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        create = findViewById(R.id.createaccount);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("hi");
        dr.setValue("bye");

        staticlist = new ArrayList<>();
        adapter = new MyArrayAdapter(LockScreen.this, staticlist);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            pbl.setVisibility(View.GONE);
        } else {
            updateUI(currentUser);
        }

        final HomeWatcher mHomeWatcher = new HomeWatcher(this);

        mHomeWatcher.stopWatch();


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
                        Log.d("PANDAGA", "login button press");

                        mAuth.signInWithEmailAndPassword(enteredemail, enteredpassword)
                                .addOnCompleteListener(LockScreen.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            updateUI(user);
                                            pbl.setVisibility(View.GONE);
                                            Log.d("PANDAGA", "login success");

                                        } else {
                                            if (!isNetworkConnected()) {
                                                Toast.makeText(LockScreen.this, "You are not connected to the Internet",
                                                        Toast.LENGTH_SHORT).show();
                                                updateUI(null);
                                                pbl.setVisibility(View.GONE);
                                                login.setEnabled(true);
                                            } else {
                                                Toast.makeText(LockScreen.this, "Login incorrect",
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

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateFBLAAccount.class);
                startActivity(intent);
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


        //validate email first:


    }


    private void updateUI(final FirebaseUser user) {
        if (user != null) {
            getallevents();
            getGroups();
            if (InternetConnection.checkConnection(LockScreen.this)) {
                new GetDataTask().execute();
            }
            // SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);


            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

            mDatabase.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());

            mDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString(getString(R.string.fname), dataSnapshot.child("fname").getValue().toString());
                        editor.putString(getString(R.string.lname), dataSnapshot.child("lname").getValue().toString());
                        editor.putString(getString(R.string.email), dataSnapshot.child("email").getValue().toString());
                        editor.putString(getString(R.string.password), dataSnapshot.child("password").getValue().toString());
                        editor.putString(getString(R.string.username), dataSnapshot.child("username").getValue().toString());
                        editor.putString(getString(R.string.grade), dataSnapshot.child("gradyear").getValue().toString());
                        editor.putString(getString(R.string.role), dataSnapshot.child("role").getValue().toString());
                        editor.putString(getString(R.string.uid), dataSnapshot.child("uid").getValue().toString());
                        editor.putString(getString(R.string.deviceToken), dataSnapshot.child("device_token").getValue().toString());

                        mDatabase.child("online").setValue(true);

                        if (dataSnapshot.hasChild("profpic")) {
                            editor.putString(getString(R.string.profpic), dataSnapshot.child("profpic").getValue().toString());
                        } else {
                            editor.putString(getString(R.string.profpic), "nocustomimage");
                        }
                        editor.apply();

                        if (dataSnapshot.child("role").getValue().toString().equals("Officer")) {
                            FirebaseMessaging.getInstance().subscribeToTopic("Officers")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            }
                                        }
                                    });
                        } else if (dataSnapshot.child("role").getValue().toString().equals("Member")) {
                            FirebaseMessaging.getInstance().subscribeToTopic("Members")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String msg = getString(R.string.msg_subscribed);
                                            if (!task.isSuccessful()) {
                                                msg = getString(R.string.msg_subscribe_failed);
                                            }
                                        }
                                    });
                        } else if (dataSnapshot.child("role").getValue().toString().equals("Advisor")) {
                            FirebaseMessaging.getInstance().subscribeToTopic("Advisors")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            }
                                        }
                                    });
                        }


                        getallpeeps(dataSnapshot.child("fname").getValue().toString(),
                                dataSnapshot.child("lname").getValue().toString(),
                                dataSnapshot.child("email").getValue().toString());

                        if (dataSnapshot.child("status").exists()) {

                            String status = dataSnapshot.child("status").getValue().toString();

                            if (status.equals("0")) {
                                Toast.makeText(LockScreen.this, "Your account has not been approved yet. Please wait", Toast.LENGTH_SHORT).show();
                            } else if (status.equals("1")) {
                                Intent intent = new Intent(LockScreen.this, Hello.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        //testing purpose
                        else {
                            Intent intent = new Intent(LockScreen.this, Hello.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            pbl.setVisibility(View.GONE);

        }

    }


    public void getallpeeps(final String fremove, final String lremove, final String email) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        databaseReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {

                fn = collectdelete((Map<String, Object>) dataSnapshot.getValue(), "fname", email);
                ln = collectdelete((Map<String, Object>) dataSnapshot.getValue(), "lname", email);
                uidall = collectdelete((Map<String, Object>) dataSnapshot.getValue(), "uid", email);

                for (int i = 0; i < uidall.size(); i++) {
                }


                ArrayList<String> ftemp =
                        collectcertainpeople((Map<String, Object>) dataSnapshot.getValue(), "fname", "Officer");

                ArrayList<String> ltemp =
                        collectcertainpeople((Map<String, Object>) dataSnapshot.getValue(), "lname", "Officer");

                ArrayList<String> ftempa =
                        collectcertainpeople((Map<String, Object>) dataSnapshot.getValue(), "fname", "Advisor");

                ArrayList<String> ltempa =
                        collectcertainpeople((Map<String, Object>) dataSnapshot.getValue(), "lname", "Advisor");

                for (int i = 0; i < ftemp.size(); i++) {
                    if (ftemp.get(i).toString().equals(fremove) && ltemp.get(i).toString().equals(lremove)) {

                    } else {
                        officeral.add(ftemp.get(i).toString() + " " + ltemp.get(i).toString());
                    }
                }

                for (int i = 0; i < ftempa.size(); i++) {
                    if (ftempa.get(i).toString().equals(fremove) && ltempa.get(i).toString().equals(lremove)) {

                    } else {
                        advisoral.add(ftempa.get(i).toString() + " " + ltempa.get(i).toString());
                    }

                }

                setOfficall(officeral.toArray(new String[officeral.size()]));
                setAdviarray(advisoral.toArray(new String[advisoral.size()]));
                setUidasall(uidall.toArray(new String[uidall.size()]));

                //convert the arraylist to array and use setmethod
                setFnas(fn.toArray(new String[fn.size()]));
                setLnas(ln.toArray(new String[ln.size()]));

                dts = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "device_token");

                setDtsarray(dts.toArray(new String[dts.size()]));

                String all = "";

                for (int i = 0; i < dts.size(); i++) {
                    if (dts.get(i) != null) {
                        all = all + dts.get(i).toString() + ",";
                    }
                }


                mStore = FirebaseDatabase.getInstance().getReference().child("Likes");
                mStore.child("AllDeviceTokens").setValue(all);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void getGroups() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("groupspartof")) {
                    String thing = dataSnapshot.child("groupspartof").getValue().toString();
                    thing = thing.substring(0, thing.length() - 1);
                    String[] another = thing.split(",");
                    for (int i = 0; i < another.length; i++) {
                        gs.add(another[i]);
                    }
                    setGsarray(gs.toArray(new String[gs.size()]));
                } else {
                    gs.add("none");
                    setGsarray(gs.toArray(new String[gs.size()]));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getallevents() {
        //retrieve reference

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Events");

        databaseReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                ts = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "eventname");
                aus = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "eventtype");
                cas = collectEventdata((Map<String, Object>) dataSnapshot.getValue(), "eventcategory");


                //convert the arraylist to array and use setmethod
                setTils(ts.toArray(new String[ts.size()]));
                setAuths(aus.toArray(new String[aus.size()]));
                setCass(cas.toArray(new String[cas.size()]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private ArrayList<String> collectEventdata(Map<String, Object> users, String fieldName) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();

            if (singleUser != null) {
                information.add((String) singleUser.get(fieldName));
            }

            //Get phone field and append to list

        }

        return information;
    }

    private ArrayList<String> collectOnlineData(Map<String, Object> users, String fieldName) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();

            if (singleUser.get(fieldName) != null) {

                String thing = (String) singleUser.get(fieldName).toString();
                if (!thing.equals("true")) {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    long lastTime = Long.parseLong(thing);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, LockScreen.this);
                    information.add(lastSeenTime);

                } else {
                    information.add(thing);
                }


            } else {
                information.add("not online");
            }

            //Get phone field and append to list

        }

        return information;
    }

    private ArrayList<String> collectdelete(Map<String, Object> users, String fieldName, String seperator) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list

            if (singleUser.get("email").toString().equals(seperator)) {

            } else {
                information.add((String) singleUser.get(fieldName));
            }
        }

        return information;
    }

    private ArrayList<String> sameanother(Map<String, Object> users, String fieldName, String seperator) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list

            if (singleUser.get("email").toString().equals(seperator)) {

            } else {
                uidall.add((String) singleUser.get("uid"));
                information.add((String) singleUser.get(fieldName));
            }
        }

        return information;
    }

    private ArrayList<String> collectcertainpeople(Map<String, Object> users, String fieldName, String seperator) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list

            if (singleUser.get("role").toString().equals(seperator)) {
                information.add((String) singleUser.get(fieldName));
            } else {
            }
        }

        return information;
    }


    class GetDataTask extends AsyncTask<Void, Void, Void> {

        int jIndex;
        int x;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */

            x = staticlist.size();

            if (x == 0)
                jIndex = 0;
            else
                jIndex = x;

            pbl.setVisibility(View.VISIBLE);
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
                                staticlist.add(model);
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
            pbl.setVisibility(View.GONE);
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            if (staticlist.size() > 0) {
                adapter.notifyDataSetChanged();
            }
        }


    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null; // return true =(connected),false=(not connected)
    }


}
