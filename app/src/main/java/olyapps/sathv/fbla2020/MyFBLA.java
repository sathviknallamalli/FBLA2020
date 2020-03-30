package olyapps.sathv.fbla2020;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;
import olyapps.sathv.fbla2020.adapter.MyArrayAdapter;
import olyapps.sathv.fbla2020.model.MyDataModel;
import olyapps.sathv.fbla2020.parser.JSONParser;
import olyapps.sathv.fbla2020.util.InternetConnection;
import olyapps.sathv.fbla2020.util.Keys;

/**
 * Created by sathv on 6/1/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MyFBLA extends Fragment {

    public MyFBLA() {

    }

    String toadd;
    AvatarView user;
    ListView currentevents, currenteamevents, onlyifuradvisor;
    ArrayList<CurrentEvent> ces, tes;

    TextView title;
    int totale;

    DatabaseReference notifications;

    CurrentEventAdapter adapter;
    Button approvals;
    CurrentEventAdapter teamadapter;
    Button open;
    View overallview;

    public static final int REQUEST_PERM_WRITE_STORAGE = 102;
    public static final int REQUEST_PERM_READ_STORAGE = 103;


    boolean paidornot = false;
    Button downloadpdfbutton;

    String name;

    FirebaseAuth mAuth;

    CircleImageView circleImageView;


    String urrole;
    MyArrayAdapter adaptera;

    ArrayList<MyDataModel> staticlist;
    private static String FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            + "/HelloWorld.pdf";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        overallview = inflater.inflate(R.layout.myfbla, container, false);
        //set the title of the screen
        getActivity().setTitle("MyFBLA");
        ces = new ArrayList<>();
        tes = new ArrayList<>();

        setHasOptionsMenu(true);
        staticlist = new ArrayList<MyDataModel>();

        adaptera = new MyArrayAdapter(overallview.getContext(), LockScreen.staticlist);
        Button addteamevent = overallview.findViewById(R.id.addteamevent);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sp = overallview.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        user = overallview.findViewById(R.id.profpic);
        urrole = sp.getString(getString(R.string.role), "");

        TextView fullname = overallview.findViewById(R.id.fullname);
        fullname.setText(sp.getString(getString(R.string.fname), "") + " " + sp.getString(getString(R.string.lname), ""));
        circleImageView = overallview.findViewById(R.id.circleprofpic);

        TextView fullgradyear = overallview.findViewById(R.id.fullgradyear);
        fullgradyear.setText(sp.getString(getString(R.string.grade), "gy"));

        TextView fullemail = overallview.findViewById(R.id.fullemail);
        fullemail.setText(sp.getString(getString(R.string.email), "email"));

        TextView fullusername = overallview.findViewById(R.id.fullusername);
        fullusername.setText(sp.getString(getString(R.string.username), "username"));

        ImageView iv17 = overallview.findViewById(R.id.imageView17);
        iv17.setOnClickListener(null);

        name = sp.getString(getString(R.string.fname), "") + " " + sp.getString(getString(R.string.lname), "");

        final ViewSwitcher switcher = overallview.findViewById(R.id.viewSwitcher);
        String picurl = sp.getString(getString(R.string.profpic), "");
        if (picurl.equals("nocustomimage")) {
            user.bind(name, null);
        } else {
            switcher.showNext();
            Glide.with(overallview.getContext()).load(picurl).into(circleImageView);
        }

        final Button addanevent = overallview.findViewById(R.id.addevent);
        currentevents = overallview.findViewById(R.id.currentevents);
        currenteamevents = overallview.findViewById(R.id.teameventslist);
        ces.clear();
        tes.clear();

        if (InternetConnection.checkConnection(overallview.getContext())) {
            new GetDataTask().execute();
        }

        try {
            if (new SimpleDateFormat("MM/dd/yyyy").parse("012/01/2018").before(new Date())) {
                addanevent.setEnabled(false);
                addanevent.setClickable(false);

                addteamevent.setEnabled(false);
                addteamevent.setClickable(false);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        downloadpdfbutton = overallview.findViewById(R.id.regitseredevents);

        downloadpdfbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(overallview.getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERM_WRITE_STORAGE);
                } else {
                   //createPdf();
                    String allces = "Individual Events\n\n";
                    if(ces.size()==0){
                        allces += "No individual events added\n\n";
                    } else{
                        for (int i = 0; i < ces.size(); i++) {
                            allces += "Event " + i+1 + ":     "+ ces.get(i).getRownameevent() + "\n";
                        }
                    }


                    String alltes = "\nTeam Events\n\n";
                    if(tes.size()==0){
                        alltes +="No team events added";
                    }else{
                        for (int i = 0; i < tes.size(); i++) {
                            alltes += "Team Event " + i+1 + ":     "+ tes.get(i).getRownameevent() + "\n";
                        }
                    }

                    writeFileOnInternalStorage(overallview.getContext() ,allces + alltes);
                }

            }
        });

        open = overallview.findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(overallview.getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERM_READ_STORAGE);
                } else {
                    openPdf();
                }
            }
        });

        title = overallview.findViewById(R.id.fullname5);

        //GET INDIVIDUAL EVENTS
        DatabaseReference first = FirebaseDatabase.getInstance().getReference().child("UserEvents").
                child(sp.getString(getString(R.string.fname), "") + " " + sp.getString(getString(R.string.lname), ""));
        first.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<CurrentEvent> nondup = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String full = postSnapshot.getValue().toString();
                    ces.add(new CurrentEvent(full, false));
                }

                for (int i = 0; i < ces.size(); i++) {
                    String dupword = ces.get(i).getRownameevent();
                    if (!nondup.contains(dupword)) {
                        nondup.add(new CurrentEvent(dupword, false));
                    }
                }

                if (!urrole.equals("Advisor")) {
                    if (ces.size() == 0) {
                        title.setText("No individual events ");
                    }
                    totale += ces.size();
                }


                adapter = new CurrentEventAdapter(overallview.getContext(), R.layout.currenteventitem, nondup);
                currentevents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //GET ALL TEAM EVENTS
        final DatabaseReference second = FirebaseDatabase.getInstance().getReference().child("TeamEvents");
        second.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<CurrentEvent> nondup = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //postsnapshot goes to team event names
                    for (DataSnapshot secondsnapshot : postSnapshot.getChildren()) {
                        //second snapshot goes to TEAMMATES

                        String key = postSnapshot.getKey();
                        String another = secondsnapshot.getValue().toString();
                        another = another.substring(0, another.length() - 1);
                        if (another.equals(name)) {
                            tes.add(new CurrentEvent(key, false));
                        }
                    }
                }

                for (int i = 0; i < tes.size(); i++) {
                    String dupword = tes.get(i).getRownameevent();
                    if (!nondup.contains(dupword)) {
                        nondup.add(new CurrentEvent(dupword, true));
                    }
                }

                if (!urrole.equals("Advisor")) {
                    if (tes.size() == 0) {
                        title.setText(title.getText() + " No team events");
                    }
                    totale += tes.size();
                }


                teamadapter = new CurrentEventAdapter(overallview.getContext(), R.layout.currenteventitem, nondup);
                currenteamevents.setAdapter(teamadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        approvals = overallview.findViewById(R.id.approvals);
        onlyifuradvisor = overallview.findViewById(R.id.onlyifuradvisor);

        if (urrole.equals("Advisor")) {
            approvals.setVisibility(View.GONE);
            onlyifuradvisor.setVisibility(View.VISIBLE);
            addanevent.setVisibility(View.INVISIBLE);
            addteamevent.setVisibility(View.INVISIBLE);
            currenteamevents.setVisibility(View.INVISIBLE);
            currentevents.setVisibility(View.INVISIBLE);
            open.setVisibility(View.INVISIBLE);
            downloadpdfbutton.setVisibility(View.INVISIBLE);


            title.setText("As an advisor, you will be responsible for approving any new team events that are created by the members" +
                    "in your chapter. To do so, whenever team events are created, they will show in a list below, and you may click to view information " +
                    "such as Members and Approvals.");
            title.setGravity(Gravity.CENTER);

            final ArrayAdapter<CharSequence> spinnneradapter;

            final ArrayList<String> names = new ArrayList<>();

            spinnneradapter = ArrayAdapter.createFromResource(overallview.getContext(), R.array.statusoptions, android.R.layout.simple_spinner_item);
            spinnneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            DatabaseReference prema = FirebaseDatabase.getInstance().getReference().child("TeamEvents");

            prema.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<AnApproval> approvalsal = new ArrayList<>();
                    AnApprovalAdapter adapter;
                    String val = "";
                    String key = "";
                    String dskey = "";

                    names.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String builder = "";
                        dskey = ds.getKey();
                        for (DataSnapshot right : ds.getChildren()) {
                            String name = right.getValue().toString();
                            String important = name.charAt(name.length() - 1) + "";
                            name = name.substring(0, name.length() - 1);


                            if (important.equals("0")) {
                                important = "Not approved";
                            } else {
                                important = "Approved";
                            }
                            if (!right.getKey().toString().equals("AdvisorStatus")) {
                                builder = builder + name + "'s status: " + important + "\n\n";
                                names.add(name);
                            } else {
                                val = right.getValue().toString();
                            }
                        }

                        approvalsal.add(new AnApproval(ds.getKey().toString(),
                                "The Advisor status is " + ds.child("AdvisorStatus").getValue().toString()
                                        + "\n\n" + builder + "Click here to edit your approval status"));

                    }


                    adapter = new AnApprovalAdapter(overallview.getContext(), R.layout.perapproval, approvalsal);
                    onlyifuradvisor.setAdapter(adapter);


                    final String finalVal = val;
                    final String finalDskey = dskey;
                    final String finalKey = key;
                    onlyifuradvisor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(overallview.getContext());
                            builder.setTitle("Update Advisor Status");

                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            final View v = inflater.inflate(R.layout.custom_alert, null);
                            builder.setView(v);

                            final Spinner statusspinner = v.findViewById(R.id.statusspinner);

                            statusspinner.setAdapter(spinnneradapter);

                            if (finalVal.contains("0")) {
                                statusspinner.setSelection(0);
                            } else if (finalVal.contains("1")) {
                                statusspinner.setSelection(1);
                            }

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final int pos = statusspinner.getSelectedItemPosition();
                                    DatabaseReference fd = FirebaseDatabase.getInstance().getReference().child("TeamEvents")
                                            .child(finalDskey).child("AdvisorStatus");
                                    fd.setValue(pos + "");

                                    //ADVISOR NOTIFICATION
                                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ArrayList<String> user_device_tokens = new ArrayList<>();
                                            user_device_tokens = userDts((Map<String, Object>) dataSnapshot.getValue(),
                                                    names);

                                            DatabaseReference databaseReference1 =
                                                    FirebaseDatabase.getInstance().getReference().child("notificationUpdateStatus");

                                            databaseReference1.child("a8042437-91e7-4e57-b240-55de8d33d213")
                                                    .child("DeviceTokens").setValue("Advisor" + "SEPERATOR" +
                                                    user_device_tokens.toString() + "SEPERATOR" + finalDskey + "SEPERATOR" + pos);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        approvals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newintent = new Intent(overallview.getContext(), Approvals.class);
                startActivity(newintent);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(MyFBLA.class.getName()).commit();
                fm.executePendingTransactions();
            }
        });


        addanevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (paidornot) {

                    if (totale >= 4) {
                        Toast.makeText(overallview.getContext(), "You can only have 4 events", Toast.LENGTH_SHORT).show();
                    } else {
                        final Item events[] = new Item[]{
                                new Item("Accounting I", R.drawable.info),
                                new Item("Accounting II", R.drawable.info),
                                new Item("Advertising", R.drawable.info),
                                new Item("Agribusiness", R.drawable.info),
                                new Item("American Enterprise Project", R.drawable.info),
                                new Item("Business Calculations", R.drawable.info),
                                new Item("Business Communication (FBLA)", R.drawable.info),
                                new Item("Business Law (FBLA)", R.drawable.info),
                                new Item("Client Service (FBLA)", R.drawable.info),
                                new Item("Coding & Programming", R.drawable.info),
                                new Item("Community Service Project (FBLA)", R.drawable.info),
                                new Item("Computer Applications (FBLA)", R.drawable.info),
                                new Item("Computer Problem Solving", R.drawable.info),
                                new Item("Cyber Security (FBLA)", R.drawable.info),
                                new Item("Database Design & Application", R.drawable.info),
                                new Item("Economics", R.drawable.info),
                                new Item("Electronic Career Portfolio", R.drawable.info),
                                new Item("Future Business Leader", R.drawable.info),
                                new Item("Health Care Administration", R.drawable.info),
                                new Item("Help Desk (FBLA)", R.drawable.info),
                                new Item("Impromptu Speaking (FBLA)", R.drawable.info),
                                new Item("Insurance & Risk Management", R.drawable.info),
                                new Item("Introduction to Business 9th & 10th Grade Events", R.drawable.info),
                                new Item("Introduction to Business Communication 9th & 10th Grade Events", R.drawable.info),
                                new Item("Introduction to Business Procedures 9th & 10th Grade Events", R.drawable.info),
                                new Item("Introduction to FBLA 9th & 10th Grade Events", R.drawable.info),
                                new Item("Introduction to Financial Math 9th & 10th Grade Events", R.drawable.info),
                                new Item("Introduction to Information Technology 9th & 10th Grade Events", R.drawable.info),
                                new Item("Introduction to Parliamentary Procedure 9th & 10th Grade Events", R.drawable.info),
                                new Item("Introduction to Public Speaking 9th & 10th Grade Events", R.drawable.info),
                                new Item("Job Interview (FBLA)", R.drawable.info),
                                new Item("Journalism", R.drawable.info),
                                new Item("Local Chapter Annual Business Report (FBLA)", R.drawable.info),
                                new Item("Networking Concepts (FBLA)", R.drawable.info),
                                new Item("Organizational Leadership", R.drawable.info),
                                new Item("Partnership with Business Project", R.drawable.info),
                                new Item("Personal Finance (FBLA)", R.drawable.info),
                                new Item("Political Science", R.drawable.info),
                                new Item("Public Speaking (FBLA)", R.drawable.info),
                                new Item("Sales Presentation (FBLA)", R.drawable.info),
                                new Item("Securities & Investments", R.drawable.info),
                                new Item("Spreadsheet Applications", R.drawable.info),
                                new Item("Word Processing", R.drawable.info)};

                        ListAdapter adapterla = new ArrayAdapter<Item>(
                                view.getContext(),
                                android.R.layout.select_dialog_item,
                                android.R.id.text1,
                                events) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                //Use super class to create the View
                                View v = super.getView(position, convertView, parent);
                                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                                tv.setTextSize(16);

                                //Put the image on the TextView
                                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, events[position].icon, 0);


                                //Add margin between image and text (support various screen densities)
                                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                                tv.setCompoundDrawablePadding(dp5);

                                return v;
                            }
                        };


                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Choose event");
                        builder.setAdapter(adapterla, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);

                                String str = sp.getString(getString(R.string.fname), "fname") + " " + sp.getString(getString(R.string.lname), "lname");

                                toadd = events[which].toString();
                                ces.add(new CurrentEvent(toadd, false));
                                adapter = new CurrentEventAdapter(overallview.getContext(), R.layout.currenteventitem, ces);
                                currentevents.setAdapter(adapter);

                                //ADDING
                                final DatabaseReference update = FirebaseDatabase.getInstance().getReference().child("UserEvents")
                                        .child(str);

                                update.push().setValue(toadd);

                                final Snackbar snackbar = Snackbar.make(overallview, "Event added. Scroll down the list", Snackbar.LENGTH_LONG);
                                snackbar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snackbar.dismiss();
                                    }
                                });
                                snackbar.show();

                                if (ces.size() == 1) {
                                    title.setText("Your Events");
                                }
                            }
                        });
                        builder.show();
                    }
                } else {
                    Toast.makeText(overallview.getContext(), "Must pay club dues before registering for events", Toast.LENGTH_LONG).show();
                }
            }
        });


        addteamevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paidornot) {
                    final String teamevents[] = new String[]{"3-D Animation",
                            "Banking & Financial Systems",
                            "Broadcast Journalism",
                            "Business Ethics (FBLA)",
                            "Business Financial Plan",
                            "Business Plan",
                            "Computer Game & Simulation Programming",
                            "Digital Video Production",
                            "E-business",
                            "Emerging Business Issues (FBLA)",
                            "Entrepreneurship",
                            "Global Business",
                            "Graphic Design",
                            "Hospitality Management (FBLA)",
                            "Introduction to Business Presentation  9th & 10th Grade Event",
                            "LifeSmarts",
                            "Management Decision Making",
                            "Management Information Systems",
                            "Marketing",
                            "Mobile Application Development (FBLA)",
                            "Network Design (FBLA)",
                            "Parliamentary Procedure (FBLA)",
                            "Public Service Announcement",
                            "Publication Design",
                            "Social Media Campaign",
                            "Sports & Entertainment Management",
                            "Virtual Business Finance Challenge",
                            "Virtual Business Management Challenge",
                            "Website Design (FBLA)"};

                    final String[] fnsall = LockScreen.getFnas();
                    final String[] lnsall = LockScreen.getLnas();
                    String[] uidstemp = LockScreen.getUidasall();

                    String alluidsall[] = new String[uidstemp.length];
                    for (int i = 0; i < alluidsall.length; i++) {
                        alluidsall[i] = uidstemp[i];

                    }

                    final ArrayList<String> mSelectedItems = new ArrayList<>();
                    mSelectedItems.clear();
                    final ArrayList<String> mSelectedUids = new ArrayList<>();
                    mSelectedUids.clear();

                    String names[] = new String[fnsall.length];

                    for (int i = 0; i < fnsall.length; i++) {
                        names[i] = fnsall[i] + " " + lnsall[i];
                    }

                    String allad = "";

                    for (int i = 0; i < LockScreen.getAdviarray().length; i++) {
                        allad = allad + LockScreen.getAdviarray()[i] + ",";
                    }

                    allad = allad.substring(0, allad.length() - 1);

                    ArrayList<String> self = new ArrayList<>();
                    for (int i = 0; i < names.length; i++) {
                        self.add(names[i]);
                    }

                    ArrayList<String> usaf = new ArrayList<>();
                    for (int i = 0; i < alluidsall.length; i++) {
                        usaf.add(alluidsall[i]);
                    }

                    for (int i = 0; i < names.length; i++) {
                        if (allad.contains(names[i])) {
                            self.remove(names[i]);
                            usaf.remove(alluidsall[i]);
                        }
                    }


                    String namesf[] = new String[self.size()];
                    String alluidsallf[] = new String[usaf.size()];

                    for (int i = 0; i < self.size(); i++) {
                        namesf[i] = self.get(i).toString();
                        alluidsallf[i] = usaf.get(i).toString();
                    }

                    final int[] count = {0};
                    AlertDialog.Builder builder = new AlertDialog.Builder(overallview.getContext());
                    builder.setTitle("Choose team event");
                    final String[] finalNames = namesf;
                    final String[] finalAlluidsall = alluidsallf;
                    builder.setItems(teamevents, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String teameventname = teamevents[which];
                            AlertDialog.Builder anotherbuilder = new AlertDialog.Builder(overallview.getContext());
                            anotherbuilder.setTitle("Select team members");

                            anotherbuilder.setMultiChoiceItems(finalNames, null,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                            String chosenname = finalNames[which].toString();
                                            String whichuid = finalAlluidsall[which].toString();
                                            int index = mSelectedItems.indexOf(chosenname.toString());

                                            if (isChecked) {
                                                if (count[0] < 3) {
                                                    mSelectedItems.add(chosenname);
                                                    mSelectedUids.add(whichuid);
                                                    count[0]++;
                                                }

                                            } else if (mSelectedItems.contains(chosenname)) {
                                                // Else, if the item is already in the array, remove it
                                                // write your code when user Uchecked the checkbox
                                                mSelectedItems.remove(index);
                                                mSelectedUids.remove(index);
                                                count[0]--;
                                            }

                                        }
                                    });
                            final String finalTeameventname = teameventname;
                            anotherbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mSelectedItems.add(name);
                                    if (!mSelectedItems.isEmpty() || mSelectedItems.size() != 0) {


                                        final DatabaseReference fd = FirebaseDatabase.getInstance().getReference().child("TeamEvents");

                                        fd.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int count = 1;
                                                if (dataSnapshot.child(finalTeameventname).exists()) {
                                                    boolean exists = true;
                                                    String checkname = "";

                                                    while (exists) {
                                                        checkname = finalTeameventname + " " + count;

                                                        if (dataSnapshot.child(checkname).exists()) {
                                                            count++;
                                                        } else {
                                                            exists = false;
                                                        }
                                                    }

                                                    for (int i = 0; i < mSelectedItems.size(); i++) {
                                                        fd.child(checkname).child("Teammate" + (i + 1))
                                                                .setValue(mSelectedItems.get(i).toString());
                                                    }

                                                    fd.child(checkname).child("AdvisorStatus").setValue("0");


                                                    tes.add(new CurrentEvent(checkname, true));
                                                    if (tes.size() == 1) {
                                                        title.setText("Your Events");
                                                    }
                                                    teamadapter = new CurrentEventAdapter(overallview.getContext(), R.layout.currenteventitem, tes);
                                                    currenteamevents.setAdapter(teamadapter);

                                                    String temp = checkname;
                                                    temp = temp.replaceAll("\\s+", "");
                                                    temp = temp.replaceAll("(FBLA)", "");
                                                    temp = temp.replaceAll("[()]", "");
                                                    FirebaseMessaging.getInstance().subscribeToTopic(temp)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                    }
                                                                }
                                                            });

                                                    DatabaseReference getdts = FirebaseDatabase.getInstance().getReference().child("Users");
                                                    final ArrayList<String> alldts = new ArrayList<>();
                                                    final ArrayList<String> adviemail = new ArrayList<>();

                                                    final String finalCheckname = checkname;
                                                    getdts.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (int i = 0; i < mSelectedUids.size(); i++) {
                                                                if (dataSnapshot.child(mSelectedUids.get(i)).child("device_token").exists()) {
                                                                    String dt = dataSnapshot.child(mSelectedUids.get(i)).child("device_token")
                                                                            .getValue().toString();
                                                                    alldts.add(dt);
                                                                }
                                                            }

                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                                if (snapshot.child("role").getValue().toString().equals("Advisor")) {
                                                                    adviemail.add(snapshot.child("email").getValue().toString());
                                                                }
                                                            }

                                                            for (int i = 0; i < adviemail.size(); i++) {
                                                                String subject = "Team Event Approval";
                                                                String message = "A new team is trying to register. The team information is below.\n" + "\nEvent yourname: " +
                                                                        finalTeameventname + "\nThe member who created it: " + name;

                                                                for (int j = 0; j < mSelectedItems.size(); j++) {
                                                                    message = message + "\nTeam Member: " + mSelectedItems.get(j).toString();
                                                                }

                                                                message = message + "\n\nIf you approve of this team or need more information about the Team, go to the Firebase Console and view this team's info. If you approve," +
                                                                        " please change the AdvisorStatus from 0 to 1. If not, leave it at 0";
                                                                SendMail sm = new SendMail(overallview.getContext(), adviemail.get(i), subject, message);
                                                                sm.execute();

                                                            }

                                                            String notif = "";
                                                            for (int i = 0; i < alldts.size(); i++) {
                                                                notif = notif + alldts.get(i) + ",";
                                                            }

                                                            notifications = FirebaseDatabase.getInstance().getReference().child("notificationsTeamEvent");
                                                            notifications.child("a8d8fbbd-147a-41af-8d58-c24f8278b895")
                                                                    .child("TeamInfo").setValue(finalCheckname +
                                                                    "SEPERATOR" + name + "SEPERATOR" + notif);

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                    final Snackbar snackbar = Snackbar.make(overallview, "An approval message to all members and advisors has been sent to verify your event", Snackbar.LENGTH_LONG);
                                                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            snackbar.dismiss();
                                                        }
                                                    });
                                                    snackbar.show();

                                                } else {

                                                    //CHANGE EVERYTHING FOR DUPLICATE CONDITION ABOVE


                                                    for (int i = 0; i < mSelectedItems.size(); i++) {
                                                        if (mSelectedItems.get(i).equals(name)) {
                                                            fd.child(finalTeameventname).child("Teammate" + (i + 1))
                                                                    .setValue(mSelectedItems.get(i).toString() + "1");
                                                        } else {
                                                            fd.child(finalTeameventname).child("Teammate" + (i + 1))
                                                                    .setValue(mSelectedItems.get(i).toString() + "0");
                                                        }

                                                    }

                                                    fd.child(finalTeameventname).child("AdvisorStatus").setValue("0");

                                                    tes.add(new CurrentEvent(finalTeameventname, true));
                                                    teamadapter = new CurrentEventAdapter(overallview.getContext(), R.layout.currenteventitem, tes);
                                                    currenteamevents.setAdapter(teamadapter);

                                                    String temp = finalTeameventname;
                                                    temp = temp.replaceAll("\\s+", "");
                                                    temp = temp.replaceAll("(FBLA)", "");
                                                    temp = temp.replaceAll("[()]", "");
                                                    FirebaseMessaging.getInstance().subscribeToTopic(temp)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("APPLE", "successful");
                                                                    }
                                                                }
                                                            });


                                                    DatabaseReference getdts = FirebaseDatabase.getInstance().getReference().child("Users");
                                                    final ArrayList<String> alldts = new ArrayList<>();
                                                    final ArrayList<String> adviemail = new ArrayList<>();


                                                    getdts.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (int i = 0; i < mSelectedUids.size(); i++) {
                                                                if (dataSnapshot.child(mSelectedUids.get(i)).child("device_token").exists()) {
                                                                    String dt = dataSnapshot.child(mSelectedUids.get(i)).child("device_token")
                                                                            .getValue().toString();
                                                                    alldts.add(dt);
                                                                }
                                                            }
                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                                if (snapshot.child("role").getValue().toString().equals("Advisor")) {
                                                                    adviemail.add(snapshot.child("email").getValue().toString());
                                                                }
                                                            }

                                                            for (int i = 0; i < adviemail.size(); i++) {
                                                                String subject = "Team Event Approval";
                                                                String message = "A new team is trying to register. The team information is below.\n" + "\nEvent yourname: " +
                                                                        finalTeameventname + "\nThe member who created it: " + name;

                                                                for (int j = 0; j < mSelectedItems.size(); j++) {
                                                                    message = message + "\nTeam Member: " + mSelectedItems.get(j).toString();
                                                                }

                                                                message = message + "\n\nIf you approve of this team or need more information about the Team, go to the Firebase Console and view this team's info. If you approve," +
                                                                        " please change the AdvisorStatus from 0 to 1. If not, leave it at 0";
                                                                SendMail sm = new SendMail(overallview.getContext(), adviemail.get(i), subject, message);
                                                                sm.execute();

                                                            }

                                                            String notif = "";
                                                            for (int i = 0; i < alldts.size(); i++) {
                                                                notif = notif + alldts.get(i) + ",";
                                                            }

                                                            // fd.child(finalTeameventname).child("memberdts").setValue(notif);

                                                            notifications = FirebaseDatabase.getInstance().getReference().child("notificationsTeamEvent");
                                                            notifications.child("a8d8fbbd-147a-41af-8d58-c24f8278b895")
                                                                    .child("TeamInfo").setValue(finalTeameventname +
                                                                    "SEPERATOR" + name + "SEPERATOR" + notif);

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });


                                                    final Snackbar snackbar = Snackbar.make(overallview, "An approval message to all members and advisors has been sent to verify your event", Snackbar.LENGTH_LONG);
                                                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            snackbar.dismiss();
                                                        }
                                                    });
                                                    snackbar.show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else {
                                        Toast.makeText(overallview.getContext(), "Must choose members", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            anotherbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            anotherbuilder.show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(overallview.getContext(), "Must pay club dues before registering for events", Toast.LENGTH_LONG).show();
                }
            }
        });

        return overallview;
    }
    public void writeFileOnInternalStorage(Context mcoContext, String sBody){

        try{
            File root = new File(Environment.getExternalStorageDirectory().toString());
            File gpxfile = new File(root, "YourEvents.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(overallview.getContext(), "Saved", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        int jIndex;
        int x;
        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            x = staticlist.size();

            if (x == 0)
                jIndex = 0;
            else
                jIndex = x;

            dialog = new ProgressDialog(overallview.getContext());
            dialog.setTitle("Loading..");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
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

                        SharedPreferences sp = overallview.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                        String firstnamestr = sp.getString(getString(R.string.fname), "");
                        String lastnamestr = sp.getString(getString(R.string.lname), "");

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

                                if (firstname.equals(firstnamestr) && lastname.equals(lastnamestr)) {
                                    if (clubdues.equals("$30.00") || clubdues.equals("30") || !clubdues.isEmpty()) {

                                        paidornot = true;
                                    }
                                }
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
            dialog.dismiss();
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            if (staticlist.size() > 0) {
                adaptera.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        overallview.setFocusableInTouchMode(true);
        overallview.requestFocus();
        overallview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fblamenu, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.take_note) {
            Intent newintent = new Intent(overallview.getContext(), ANote.class);
            newintent.putExtra("notename", "aboutmyfbla");
            startActivity(newintent);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(MyFBLA.class.getName()).commit();
            fm.executePendingTransactions();
        }
        return super.onOptionsItemSelected(item);
    }

    void openPdf() {
       String url =Environment.getExternalStorageDirectory().toString() + "/YourEvents.txt";
        File file = new File( Environment.getExternalStorageDirectory(),"YourEvents.txt");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        intent.setDataAndType(Uri.fromFile(file), mimetype);

        Intent intent1 = Intent.createChooser(intent, "Open with");
        startActivity(intent1);

    }

    private ArrayList<String> userDts(Map<String, Object> users, ArrayList<String> names) {
        ArrayList<String> information = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list


            String s = singleUser.get("fname").toString() + " " + singleUser.get("lname").toString();

            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).equals(s)) {
                    if (singleUser.get("device_token") != null) {
                        information.add((String) singleUser.get("device_token").toString());
                    }
                }
            }

        }

        return information;
    }




}