package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by sathv on 6/1/2018.
 */

public class MessagesInbox extends Fragment {

    public MessagesInbox() {

    }

    ListView allmembers;
    ArrayList<Person> memebers;

    String[] finalTempud;
    PersonAdapter adapter;
    PersonAdapter filteredvaluesadapter;

    ImageButton addbg;
    SearchView sb;

    static String names;
    static int val;

    public static int getVal() {
        return val;
    }

    public static void setVal(int val) {
        MessagesInbox.val = val;
    }


    String[] fns = LockScreen.getFnas();
    String[] lns = LockScreen.getLnas();
    String[] tempud = LockScreen.getUidasall();


    static ArrayList<String> groupmembers = new ArrayList<>();
    static ArrayList<String> groupmembersuid = new ArrayList<>();
    static String groupname;
    static String groupdts;
    ArrayList<String> mSelectedItems = new ArrayList<>();
    ArrayList<String> mSelectedUids = new ArrayList<>();
    static boolean isgroupcreated = false;
    static boolean isthisagroup = false;

    ArrayList<String> adviseruids;

    FirebaseAuth mAuth;

    View view;
    String[] fullnames = new String[fns.length];

    String chapid;

    String role;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.members, container, false);
        //set the title of the screen
        //  getActivity().setTitle("MemInbox");
        Firebase.setAndroidContext(view.getContext());
        setHasOptionsMenu(true);

        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");




        sb = view.findViewById(R.id.sb);
        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addbg.setVisibility(View.INVISIBLE);
            }
        });
        sb.onActionViewCollapsed();
        sb.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                addbg.setVisibility(View.VISIBLE);
                return true;
            }
        });

        sb.setIconifiedByDefault(false);

        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                resetSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    //if the search bar is empty, load the original listview using the resetsearch method that is defined below
                    resetSearch();
                    return false;
                } else {

                    //filtered values of book based on the search
                    final ArrayList<Person> filteredValues = new ArrayList<Person>(memebers);

                    for (int i = 0; i < memebers.size(); i++) {

                        //if the title of each book does not contaain the string from the search bar, then delete it from the listview
                        //and remove from the filtered values arraylist
                        if (!(memebers.get(i).getPersonname().toLowerCase()).contains(newText.toLowerCase())) {

                            //remove each field
                            filteredValues.remove(memebers.get(i));
                            filteredvaluesadapter = new PersonAdapter(getActivity().getApplicationContext(), R.layout.perperson, filteredValues);
                        }
                    }

                    allmembers.setAdapter(filteredvaluesadapter);

                    //then declare the onclick listener if a book is clicked after they searched
                    allmembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int position, long val) {

                            FragmentManager fragmentManager = getFragmentManager();


                            if (filteredValues.get(position).getPersonname().contains("Group: ")) {
                                isthisagroup = true;
                                isgroupcreated = false;
                                groupname = filteredValues.get(position).getPersonname().replace("Group: ", "");


                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                UserDetails.isadviser = false;
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                                fm.executePendingTransactions();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                                        child("Chapters").child(chapid).child("ChatMessages")
                                        .child("Groups").child(groupname);

                                SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                                final String urname = sp.getString(getString(R.string.fname), "urfn") + " " +
                                        sp.getString(getString(R.string.lname), "urln");

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final ArrayList<String> membernames = new ArrayList<>();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            if (!postSnapshot.getValue().toString().equals(urname)) {
                                                membernames.add(postSnapshot.getValue().toString());
                                            }
                                        }

                                        String them = "";

                                        for (int i = 0; i < membernames.size(); i++) {
                                            them = them + membernames.get(i).toString() + ",";
                                        }

                                        names = them;

                                        DatabaseReference userinfo = FirebaseDatabase.getInstance().getReference().
                                                child("Chapters").child(chapid).child("Users");
                                        final ArrayList<String> dts = new ArrayList<>();

                                        userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    for (int i = 0; i < membernames.size(); i++) {
                                                        String[] split = membernames.get(i).split("\\s+");
                                                        if (postSnapshot.child("fname").getValue().toString().equals(split[0]) && postSnapshot.child("lname").getValue().toString().equals(split[1])) {
                                                            if (postSnapshot.child("device_token").exists()) {
                                                                dts.add(postSnapshot.child("device_token").getValue().toString());
                                                            }
                                                        }
                                                    }
                                                }

                                                String them = "";

                                                for (int i = 0; i < dts.size(); i++) {
                                                    them = them + dts.get(i).toString() + ",";
                                                }

                                                groupdts = them;

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                String sm = filteredValues.get(position).getPersonname();
                                int delete = spacechar(sm);
                                sm = sm.replace(sm.charAt(delete) + "", "");

                                UserDetails.chatWith = sm;
                                isgroupcreated = false;
                                isthisagroup = false;
                                UserDetails.fullname = filteredValues.get(position).getPersonname();

                                UserDetails.chatuid = filteredValues.get(position).getUid();

                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                UserDetails.isadviser = false;
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                                fm.executePendingTransactions();


                            }


                        }
                    });
                    return false;
                }
            }
        };

        //set the appropriate listener and hint for searchbar
        sb.setOnQueryTextListener(listener);
        sb.setQueryHint("Search by member or group");

        mAuth = FirebaseAuth.getInstance();
        final SharedPreferences groups = view.getContext().getSharedPreferences("groupstuff", Context.MODE_PRIVATE);
        groups.edit().clear();
        final String urname = sp.getString(getString(R.string.fname), "urfn") + " " +
                sp.getString(getString(R.string.lname), "urln");
        final String nospace = sp.getString(getString(R.string.fname), "urfn") +
                sp.getString(getString(R.string.lname), "urln");

        allmembers = view.findViewById(R.id.allmembers);
        memebers = new ArrayList<Person>();

        Button talktoofficer = view.findViewById(R.id.talktoofficer);
        Button talktoadvisor = view.findViewById(R.id.talktoadvisor);



        for (int i = 0; i < fns.length; i++) {
            fullnames[i] = fns[i] + " " + lns[i];
        }

        fullnames = sortItemsM(fullnames, tempud);


        addbg = view.findViewById(R.id.addbg);
        addbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSelectedUids.clear();
                mSelectedItems.clear();

                SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                final String urname = sp.getString(getString(R.string.fname), "urfn") + " " +
                        sp.getString(getString(R.string.lname), "urln");

                final CharSequence names[] = new CharSequence[fullnames.length + 1];
                final CharSequence alluidsall[] = new CharSequence[tempud.length + 1];

                for (int i = 0; i < names.length; i++) {
                    if (i == names.length - 1) {
                        names[i] = urname;
                        alluidsall[i] = sp.getString(getString(R.string.uid), "uid");
                    } else {
                        names[i] = fullnames[i];
                        alluidsall[i] = tempud[i];
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Choose GROUP MEMBERS")
                        .setMultiChoiceItems(names, null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int item, boolean isChecked) {

                                        String which = names[item].toString();
                                        String whichuid = alluidsall[item].toString();
                                        int index = mSelectedItems.indexOf(which.toString());

                                        if (isChecked) {
                                            // If the user checked the item, add it to the selected items
                                            // write your code when user checked the checkbox
                                            mSelectedItems.add(which);
                                            mSelectedUids.add(whichuid);
                                        } else if (mSelectedItems.contains(which)) {
                                            // Else, if the item is already in the array, remove it
                                            // write your code when user Uchecked the checkbox
                                            mSelectedItems.remove(index);
                                            mSelectedUids.remove(index);
                                        }
                                    }
                                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int size = mSelectedItems.size();

                        boolean canproceed = true;

                        if (!mSelectedItems.contains(urname)) {
                            canproceed = false;
                            Toast.makeText(view.getContext(), "You must be in the group to create it", Toast.LENGTH_SHORT).show();
                        }
                        if (!mSelectedItems.contains(urname) && size == 1) {
                            canproceed = false;
                            Toast.makeText(view.getContext(), "Group must contain multiple people", Toast.LENGTH_SHORT).show();
                        }
                        if (mSelectedItems.contains(urname) && size == 1) {
                            canproceed = false;
                            Toast.makeText(view.getContext(), "You cannot text yourself!!!", Toast.LENGTH_SHORT).show();
                        }
                        if (mSelectedItems.contains(urname) && size == 2) {
                            canproceed = false;
                            Toast.makeText(view.getContext(), "Text them personally!", Toast.LENGTH_SHORT).show();
                        }
                        if (canproceed) {
                            AlertDialog.Builder alert2 = new AlertDialog.Builder(view.getContext());
                            alert2.setTitle("Enter group name - REQUIRED");

                            final EditText input = new EditText(view.getContext());
                            input.setHint("Group name");
                            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                            alert2.setView(input);

                            alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                                            child("Chapters").child(chapid).child("ChatMessages")
                                            .child("Groups");

                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            boolean moveone = true;
                                            if (dataSnapshot.exists() && dataSnapshot != null) {
                                                for (DataSnapshot snapshor :
                                                        dataSnapshot.getChildren()) {
                                                    if (snapshor.getKey().toLowerCase().equals(input.getText().toString().toLowerCase())) {
                                                        Toast.makeText(view.getContext(), "Choose different name", Toast.LENGTH_SHORT).show();
                                                        moveone = false;
                                                    }
                                                }
                                            } else {
                                                moveone = true;
                                            }

                                            if (moveone) {

                                                Log.d("DARBAR", "newg");
                                                final DatabaseReference dr =
                                                        FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("notificationsNewGroup");
                                                DatabaseReference newdr =
                                                        FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid);

                                                groupmembersuid = mSelectedUids;
                                                groupmembers = mSelectedItems;
                                                groupname = input.getText().toString();
                                                isgroupcreated = true;
                                                isthisagroup = false;

                                                String them = "";

                                                for (int i = 0; i < groupmembers.size(); i++) {
                                                    them = them + groupmembers.get(i).toString() + ",";
                                                }

                                                newdr.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String them = "";
                                                        for (int j = 0; j < mSelectedUids.size(); j++) {
                                                            if (dataSnapshot.child("Users").hasChild(mSelectedUids.get(j))) {
                                                                them = them + dataSnapshot.child("Users").child(mSelectedUids.get(j)).child("device_token")
                                                                        .getValue().toString() + ",";
                                                            } else if (dataSnapshot.child("Advisers").hasChild(mSelectedUids.get(j))) {
                                                                them = them + dataSnapshot.child("Advisers").child(mSelectedUids.get(j)).child("device_token")
                                                                        .getValue().toString() + ",";
                                                            }
                                                        }
                                                        them = them.replace(FirebaseInstanceId.getInstance().getToken() + ",", "");

                                                        dr.child("ddd1331f-63f0-4070-a43e-5efef80e7a39").child("MemberDts").setValue(them);
                                                        groupdts = them;

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                                String gn = groupname;
                                                gn = gn.replaceAll("\\s+", "");

                                                FirebaseMessaging.getInstance().subscribeToTopic("Group_" + gn)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {

                                                                }
                                                            }
                                                        });

                                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                                UserDetails.isadviser = false;
                                                startActivity(i);

                                                FragmentManager fm = getFragmentManager();
                                                fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                                                fm.executePendingTransactions();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                            alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            alert2.show();
                        }


                    }
                });

                //alert dialog negative cancel button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                builder.show();
            }
        });


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapid);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < fns.length; i++) {

                    for (DataSnapshot snapshot :
                            dataSnapshot.child("Users").getChildren()) {
                        if ((snapshot.child("fname").getValue().toString() + " " + snapshot.child("lname").getValue().toString()).equals(fullnames[i])) {
                            memebers.add(new Person(fullnames[i], "false", snapshot.child("uid").getValue().toString()));
                            tempud[i] = snapshot.child("uid").getValue().toString();
                            finalTempud[i] = snapshot.child("uid").getValue().toString();
                        }
                    }


                }
                adviseruids =
                        collectEventdata((Map<String, Object>) dataSnapshot.child("Advisers").getValue(), "uid");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        getGroups(new MyCallback() {


            @Override
            public void onCallback(Boolean isExists, String who) {

            }

            @Override
            public void callbackGroups(ArrayList<String> groups) {

                final ArrayList<String>[] gs = new ArrayList[]{new ArrayList<>()};

                gs[0] = groups;

                if(gs[0].size() !=0){
                    if (gs[0].get(0).equals("none")) {
                        //not part of any groups
                    } else {
                        for (int i = 0; i < gs[0].size(); i++) {
                            Log.d("DARBAR", "added");
                            memebers.add(new Person("Group: " + gs[0].get(i), "true", "nocustomimage"));
                        }

                    }
                }
            }
        });





        adapter = new PersonAdapter(getActivity().getApplicationContext(), R.layout.perperson, memebers);
        allmembers.setAdapter(adapter);

        finalTempud = tempud;
        final String[] finalFullnames = fullnames;

        talktoofficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] alltheofficers = LockScreen.getOfficall();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Choose officer")
                        .setItems(alltheofficers, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int val = 0;
                                String chosen = alltheofficers[which];

                                for (int i = 0; i < fns.length; i++) {
                                    if (finalFullnames[i].equals(chosen)) {

                                        val = i;
                                    }
                                }

                                String tempstr = chosen;
                                int delete = spacechar(tempstr);
                                tempstr = tempstr.replace(tempstr.charAt(delete) + "", "");

                                UserDetails.chatWith = tempstr;
                                UserDetails.fullname = chosen;
                                UserDetails.chatuid = finalTempud[val];
                                UserDetails.isadviser = false;
                                isgroupcreated = false;
                                isthisagroup = false;

                                Intent i = new Intent(view.getContext(), ChatActivity.class);

                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                                fm.executePendingTransactions();
                            }
                        });

                builder.show();
            }
        });

        talktoadvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] alltheadvisors = LockScreen.getAdviarray();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Choose adviser")
                        .setItems(alltheadvisors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int val = 0;
                                String chosen = alltheadvisors[which];
                                Log.d("DARBAR", chosen);

                                for (int i = 0; i < fns.length; i++) {
                                    if (finalFullnames[i].equals(chosen)) {

                                        val = i;
                                    }
                                }

                                String tempstr = chosen;
                                int delete = spacechar(chosen);
                                chosen = chosen.replace(chosen.charAt(delete) + "", "");

                                UserDetails.chatWith = chosen;
                                UserDetails.fullname = tempstr;
                                UserDetails.isadviser = true;
                                UserDetails.chatuid = adviseruids.get(val);
                                isgroupcreated = false;
                                isthisagroup = false;


                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                                fm.executePendingTransactions();


                            }
                        });

                builder.show();
            }
        });


        allmembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                FragmentManager fragmentManager = getFragmentManager();

                setVal(position);


                if (memebers.get(position).getPersonname().contains("Group: ")) {

                    groupname = memebers.get(position).getPersonname().replace("Group: ", "");
                    //get groupview member names
                    //go through each user and check if their yourname matches, if it does, store their Device token

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                            child("Chapters").child(chapid).child("ChatMessages")
                            .child("Groups").child(groupname);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ArrayList<String> membernames = new ArrayList<>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (!postSnapshot.getValue().toString().equals(urname)
                                        && postSnapshot.getKey().contains("Member")) {
                                    membernames.add(postSnapshot.getValue().toString());
                                }
                            }

                            String them = "";

                            for (int i = 0; i < membernames.size(); i++) {
                                them = them + membernames.get(i).toString() + ",";
                            }

                            names = them;

                            DatabaseReference userinfo = FirebaseDatabase.getInstance().getReference().
                                    child("Chapters").child(chapid).child("Users");
                            final ArrayList<String> dts = new ArrayList<>();

                            userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        for (int i = 0; i < membernames.size(); i++) {
                                            String[] split = membernames.get(i).split("\\s+");
                                            if (postSnapshot.child("fname").getValue().toString().equals(split[0]) && postSnapshot.child("lname").getValue().toString().equals(split[1])) {
                                                if (postSnapshot.child("device_token").exists()) {
                                                    dts.add(postSnapshot.child("device_token").getValue().toString());
                                                }
                                            }
                                        }
                                    }

                                    String them = "";

                                    for (int i = 0; i < dts.size(); i++) {
                                        them = them + dts.get(i).toString() + ",";
                                    }

                                    groupdts = them;

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    isthisagroup = true;
                    UserDetails.isadviser = false;
                    isgroupcreated = false;

                    Intent i = new Intent(view.getContext(), ChatActivity.class);
                    startActivity(i);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                    fm.executePendingTransactions();

                } else {

                    String tempstr = finalFullnames[position];
                    int delete = spacechar(tempstr);
                    tempstr = tempstr.replace(tempstr.charAt(delete) + "", "");

                    UserDetails.chatWith = tempstr;
                    UserDetails.chatuid = finalTempud[position];
                    UserDetails.fullname = finalFullnames[position];
                    UserDetails.isadviser = false;
                    isgroupcreated = false;
                    isthisagroup = false;

                    Intent i = new Intent(view.getContext(), ChatActivity.class);
                    startActivity(i);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                    fm.executePendingTransactions();
                }
            }
        });


        return view;
    }



   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.messaging, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }

        //resetSearch();
        //inflater.inflate(R.menu.activities, menu);
        MenuItem searchItem = menu.findItem(R.id.searchmessages);
        android.support.v7.widget.SearchView searchView = (SearchView) searchItem.getActionView();

        ImageView icon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        icon.setColorFilter(R.color.colorPrimary);
        //the listener
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                resetSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    //if the search bar is empty, load the original listview using the resetsearch method that is defined below
                    resetSearch();
                    return false;
                } else {

                    //filtered values of book based on the search
                    final ArrayList<Person> filteredValues = new ArrayList<Person>(memebers);

                    for (int i = 0; i < memebers.size(); i++) {

                        //if the title of each book does not contaain the string from the search bar, then delete it from the listview
                        //and remove from the filtered values arraylist
                        if (!(memebers.get(i).getPersonname().toLowerCase()).contains(newText.toLowerCase())) {

                            //remove each field
                            filteredValues.remove(memebers.get(i));
                            filteredvaluesadapter = new PersonAdapter(getActivity().getApplicationContext(), R.layout.perperson, filteredValues);
                        }
                    }

                    allmembers.setAdapter(filteredvaluesadapter);

                    //then declare the onclick listener if a book is clicked after they searched
                    allmembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int position, long val) {

                            FragmentManager fragmentManager = getFragmentManager();


                            if (filteredValues.get(position).getPersonname().contains("Group: ")) {
                                isthisagroup = true;
                                isgroupcreated = false;
                                groupname = filteredValues.get(position).getPersonname().replace("Group: ", "");


                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(Members.class.getName()).commit();
                                fm.executePendingTransactions();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ChatMessages")
                                        .child("Groups").child(groupname);

                                SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                                final String urname = sp.getString(getString(R.string.fname), "urfn") + " " +
                                        sp.getString(getString(R.string.lname), "urln");

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final ArrayList<String> membernames = new ArrayList<>();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            if (!postSnapshot.getValue().toString().equals(urname)) {
                                                membernames.add(postSnapshot.getValue().toString());
                                            }
                                        }

                                        String them = "";

                                        for (int i =w.getContext());
                            input.setHint("Group name");
                            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                            alert2.setView(input);

                            alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ChatMessages")
                                            .child("Groups");

                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            boolean moveone = true;
                                            if (dataSnapshot.exists() && dataSnapshot != null) {
                                                for (DataSnapshot snapshor :
                                                        dataSnapshot.getChildren()) {
                                                    if (snapshor.getKey().toLowerCase().equals(input.getText().toString().toLowerCase())) {
                                                        Toast.makeText(view.getContext(), "Choose different name", Toast.LENGTH_SHORT).show();
                                                        moveone = false;
                                                    }
                                                }
                                            } else {
                                                moveone = true;
                                            }

                                            if (moveone) {

                                                final DatabaseReference dr =
                                                        FirebaseDatabase.getInstance().getReference().child("notificationsNewGroup");
                                                DatabaseReference newdr =
                                                        FirebaseDatabase.getInstance().getReference().child("Users");

                                                groupmembersuid = mSelectedUids;
                                                groupmembers = mSelectedItems;
                                                groupname = input.getText().toString();
                                                isgroupcreated = true;
                                                isthisagroup = false;

                                                String them = "";

                                                for (int i = 0; i < groupmembers.size(); i++) {
                                                    them = them + groupmembers.get(i).toString() + ",";
                                                }

                                                newdr.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String them = "";
                                                        for (int j = 0; j < mSelectedUids.size(); j++) {
                                                            if (dataSnapshot.child(mSelectedUids.get(j).toString()).child("device_token").exists()) {
                                                                them = them + dataSnapshot.child(mSelectedUids.get(j).toString()).child("device_token")
                                                                        .getValue().toString() + ",";
                                                            }
                                                        }

                                                        dr.child("ddd1331f-63f0-4070-a43e-5efef80e7a39").child("MemberDts").setValue(them);
                                                        groupdts = them;

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                                String gn = groupname;
                                                gn = gn.replaceAll("\\s+", "");

                                                FirebaseMessaging.getInstance().subscribeToTopic("Group_" + gn)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {

                                                                }
                                                            }
                                                        });

                                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                                startActivity(i);

                                                FragmentManager fm = getFragmentManager();
                                                fm.beginTransaction().addToBackStack(Members.class.getName()).commit();
                                                fm.executePendingTransactions();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                            alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            alert2.show();
                        }


                    }
                });

                //alert dialog negative cancel button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                builder.show();
            }
        });


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < fns.length; i++) {

                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        if ((snapshot.child("fname").getValue().toString() + " " + snapshot.child("lname").getValue().toString()).equals(fullnames[i])) {
                            memebers.add(new Person(fullnames[i], "false", snapshot.child("uid").getValue().toString()));
                            tempud[i] = snapshot.child("uid").getValue().toString();
                            finalTempud[i] = snapshot.child("uid").getValue().toString();
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (gs[0].equals("none")) {
            //not part of any groups
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (int i = 0; i < gs.length; i++) {
                        memebers.add(new Person("Group: " + gs[i], "true", "nocustomimage"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        adapter = new PersonAdapter(getActivity().getApplicationContext(), R.layout.perperson, memebers);
        allmembers.setAdapter(adapter);

        finalTempud = tempud;
        final String[] finalFullnames = fullnames;

        talktoofficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] alltheofficers = LockScreen.getOfficall();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Choose officer")
                        .setItems(alltheofficers, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int val = 0;
                                String chosen = alltheofficers[which];

                                for (int i = 0; i < fns.length; i++) {
                                    if (finalFullnames[i].equals(chosen)) {

                                        val = i;
                                    }
                                }

                                String tempstr = chosen;
                                int delete = spacechar(tempstr);
                                tempstr = tempstr.replace(tempstr.charAt(delete) + "", "");

                                UserDetails.chatWith = tempstr;
                                UserDetails.fullname = chosen;
                                UserDetails.chatuid = finalTempud[val];
                                isgroupcreated = false;
                                isthisagroup = false;

                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(Members.class.getName()).commit();
                                fm.executePendingTransactions();
                            }
                        });

                builder.show();
            }
        });

        talktoadvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] alltheadvisors = LockScreen.getAdviarray();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Choose advisor")
                        .setItems(alltheadvisors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int val = 0;
                                String chosen = alltheadvisors[which];

                                for (int i = 0; i < fns.length; i++) {
                                    if (finalFullnames[i].equals(chosen)) {

                                        val = i;
                                    }
                                }

                                String tempstr = chosen;
                                int delete = spacechar(tempstr);
                                tempstr = tempstr.replace(tempstr.charAt(delete) + "", "");

                                UserDetails.chatWith = tempstr;
                                UserDetails.fullname = chosen;
                                UserDetails.chatuid = finalTempud[val];
                                isgroupcreated = false;
                                isthisagroup = false;

                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(Members.class.getName()).commit();
                                fm.executePendingTransactions();


                            }
                        });

                builder.show();
            }
        });


        allmembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                FragmentManager fragmentManager = getFragmentManager();

                setVal(position);


                if (memebers.get(position).getPersonname().contains("Group: ")) {

                    groupname = memebers.get(position).getPersonname().replace("Group: ", "");
                    //get groupview member names
                    //go through each user and check if their yourname matches, if it does, store their Device token

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ChatMessages")
                            .child("Groups").child(groupname);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ArrayList<String> membernames = new ArrayList<>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (!postSnapshot.getValue().toString().equals(urname)
                                        && postSnapshot.getKey().contains("Member")) {
                                    membernames.add(postSnapshot.getValue().toString());
                                }
                            }

                            String them = "";

                            for (int i = 0; i < membernames.size(); i++) {
                                them = them + membernames.get(i).toString() + ",";
                            }

                            names = them;

                            DatabaseReference userinfo = FirebaseDatabase.getInstance().getReference().child("Users");
                            final ArrayList<String> dts = new ArrayList<>();

                            userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        for (int i = 0; i < membernames.size(); i++) {
                                            String[] split = membernames.get(i).split("\\s+");
                                            if (postSnapshot.child("fname").getValue().toString().equals(split[0]) && postSnapshot.child("lname").getValue().toString().equals(split[1])) {
                                                if (postSnapshot.child("device_token").exists()) {
                                                    dts.add(postSnapshot.child("device_token").getValue().toString());
                                                }
                                            }
                                        }
                                    }

                                    String them = "";

                                    for (int i = 0; i < dts.size(); i++) {
                                        them = them + dts.get(i).toString() + ",";
                                    }

                                    groupdts = them;

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    isthisagroup = true;
                    isgroupcreated = false;

                    Intent i = new Intent(view.getContext(), ChatActivity.class);
                    startActivity(i);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().addToBackStack(Members.class.getName()).commit();
                    fm.executePendingTransactions();

                } else {

                    String tempstr = finalFullnames[position];
                    int delete = spacechar(tempstr);
                    tempstr = tempstr.replace(tempstr.charAt(delete) + "", "");

                    UserDetails.chatWith = tempstr;
                    UserDetails.chatuid = finalTempud[position];
                    UserDetails.fullname = finalFullnames[position];

                    isgroupcreated = false;
                    isthisagroup = false;

                    Intent i = new Intent(view.getContext(), ChatActivity.class);
                    startActivity(i);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().addToBackStack(Members.class.getName()).commit();
                    fm.executePendingTransactions();
                }
            }
        });


        return view;
    }



   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.messaging, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }

        //resetSearch();
        //inflater.inflate(R.menu.activities, menu);
        MenuItem searchItem = menu.findItem(R.id.searchmessages);
        android.support.v7.widget.SearchView searchView = (SearchView) searchItem.getActionView();

        ImageView icon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        icon.setColorFilter(R.color.colorPrimary);
        //the listener
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                resetSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    //if the search bar is empty, load the original listview using the resetsearch method that is defined below
                    resetSearch();
                    return false;
                } else {

                    //filtered values of book based on the search
                    final ArrayList<Person> filteredValues = new ArrayList<Person>(memebers);

                    for (int i = 0; i < memebers.size(); i++) {

                        //if the title of each book does not contaain the string from the search bar, then delete it from the listview
                        //and remove from the filtered values arraylist
                        if (!(memebers.get(i).getPersonname().toLowerCase()).contains(newText.toLowerCase())) {

                            //remove each field
                            filteredValues.remove(memebers.get(i));
                            filteredvaluesadapter = new PersonAdapter(getActivity().getApplicationContext(), R.layout.perperson, filteredValues);
                        }
                    }

                    allmembers.setAdapter(filteredvaluesadapter);

                    //then declare the onclick listener if a book is clicked after they searched
                    allmembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int position, long val) {

                            FragmentManager fragmentManager = getFragmentManager();


                            if (filteredValues.get(position).getPersonname().contains("Group: ")) {
                                isthisagroup = true;
                                isgroupcreated = false;
                                groupname = filteredValues.get(position).getPersonname().replace("Group: ", "");


                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(Members.class.getName()).commit();
                                fm.executePendingTransactions();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ChatMessages")
                                        .child("Groups").child(groupname);

                                SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                                final String urname = sp.getString(getString(R.string.fname), "urfn") + " " +
                                        sp.getString(getString(R.string.lname), "urln");

                                databaseReference.addListenerForSin 0; i < membernames.size(); i++) {
                                            them = them + membernames.get(i).toString() + ",";
                                        }

                                        names = them;

                                        DatabaseReference userinfo = FirebaseDatabase.getInstance().getReference().child("Users");
                                        final ArrayList<String> dts = new ArrayList<>();

                                        userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    for (int i = 0; i < membernames.size(); i++) {
                                                        String[] split = membernames.get(i).split("\\s+");
                                                        if (postSnapshot.child("fname").getValue().toString().equals(split[0]) && postSnapshot.child("lname").getValue().toString().equals(split[1])) {
                                                            if (postSnapshot.child("device_token").exists()) {
                                                                dts.add(postSnapshot.child("device_token").getValue().toString());
                                                            }
                                                        }
                                                    }
                                                }

                                                String them = "";

                                                for (int i = 0; i < dts.size(); i++) {
                                                    them = them + dts.get(i).toString() + ",";
                                                }

                                                groupdts = them;
                                                Log.d("HATHA", groupdts);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                String sm = filteredValues.get(position).getPersonname();
                                int delete = spacechar(sm);
                                sm = sm.replace(sm.charAt(delete) + "", "");

                                UserDetails.chatWith = sm;
                                isgroupcreated = false;
                                isthisagroup = false;
                                UserDetails.fullname = filteredValues.get(position).getPersonname();

                                UserDetails.chatuid = filteredValues.get(position).getUid();

                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(Members.class.getName()).commit();
                                fm.executePendingTransactions();


                            }


                        }
                    });
                    return false;
                }
            }
        };

        //set the appropriate listener and hint for searchbar
        searchView.setOnQueryTextListener(listener);
        searchView.setQueryHint("Search conversation by member or groupview yourname");
    }*/

    //reset search method used when the search bar is empty and the originnal list view is set with orig arrays
    public void resetSearch() {
        //books two was originally set with orig arrays
        adapter = new PersonAdapter(getActivity().getApplicationContext(), R.layout.perperson, memebers);
        allmembers.setAdapter(adapter);

        //onclick listener set similarly to open the book info class
        allmembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long val) {

                FragmentManager fragmentManager = getFragmentManager();

                if (memebers.get(position).getPersonname().contains("Group: ")) {
                    groupname = memebers.get(position).getPersonname().replace("Group: ", "");

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                            child("Chapters").child(chapid).child("ChatMessages")
                            .child("Groups").child(groupname);

                    SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                    final String urname = sp.getString(getString(R.string.fname), "urfn") + " " +
                            sp.getString(getString(R.string.lname), "urln");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ArrayList<String> membernames = new ArrayList<>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (!postSnapshot.getValue().toString().equals(urname)) {
                                    membernames.add(postSnapshot.getValue().toString());
                                }
                            }

                            String them = "";

                            for (int i = 0; i < membernames.size(); i++) {
                                them = them + membernames.get(i).toString() + ",";
                            }

                            names = them;

                            DatabaseReference userinfo = FirebaseDatabase.getInstance().getReference().
                                    child("Chapters").child(chapid).child("Users");
                            final ArrayList<String> dts = new ArrayList<>();

                            userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        for (int i = 0; i < membernames.size(); i++) {
                                            String[] split = membernames.get(i).split("\\s+");
                                            if (postSnapshot.child("fname").getValue().toString().equals(split[0]) && postSnapshot.child("lname").getValue().toString().equals(split[1])) {
                                                if (postSnapshot.child("device_token").exists()) {
                                                    dts.add(postSnapshot.child("device_token").getValue().toString());
                                                }
                                            }
                                        }
                                    }

                                    String them = "";

                                    for (int i = 0; i < dts.size(); i++) {
                                        them = them + dts.get(i).toString() + ",";
                                    }

                                    groupdts = them;

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    isthisagroup = true;
                    isgroupcreated = false;
                    UserDetails.isadviser = false;
                    Intent i = new Intent(view.getContext(), ChatActivity.class);
                    startActivity(i);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                    fm.executePendingTransactions();
                } else {

                    String tempstr = fullnames[position];
                    int delete = spacechar(tempstr);
                    tempstr = tempstr.replace(tempstr.charAt(delete) + "", "");

                    UserDetails.chatWith = tempstr;
                    UserDetails.chatuid = finalTempud[position];
                    UserDetails.fullname = fullnames[position];

                    isgroupcreated = false;
                    isthisagroup = false;
                    UserDetails.isadviser = false;

                    Intent i = new Intent(view.getContext(), ChatActivity.class);
                    startActivity(i);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().addToBackStack(MessagesInbox.class.getName()).commit();
                    fm.executePendingTransactions();

                }

            }
        });


    }


    public static String[] removeElements(String[] input, String deleteMe) {
        List result = new LinkedList();

        for (String item : input)
            if (!deleteMe.equals(item))
                result.add(item);
        String temp[] = (String[]) result.toArray(input);

        return temp;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
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

    public String[] sortItemsM(String cargohold[], String[] uid) {
        String temp, tempu;
        boolean wasSwap = true;
        for (int index1 = 0; index1 < cargohold.length - 1 && wasSwap; ++index1) {
            wasSwap = false;
            for (int index2 = 0; index2 < cargohold.length - index1 - 1; ++index2) {
                if (cargohold[index2].compareToIgnoreCase(cargohold[index2 + 1]) > 0) {
                    temp = cargohold[index2];
                    cargohold[index2] = cargohold[index2 + 1];
                    cargohold[index2 + 1] = temp;

                    tempu = uid[index2];
                    uid[index2] = uid[index2 + 1];
                    uid[index2 + 1] = tempu;

                    wasSwap = true;
                }
            }
        }
        return cargohold;
    }

    public int spacechar(String input) {
        for (int index = 0; index < input.length(); index++) {
            if (Character.isWhitespace(input.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    public void getGroups(final MyCallback myCallback) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        final ArrayList<String> gs = new ArrayList<>();

        String childid = "";
        if(role.equals("Adviser")){
           childid = "Advisers";
        }else if(role.equals("Member") || role.equals("Officer")){
            childid =  "Users";
        }

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapid).child(childid)
                .child(currentUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("groupspartof")) {
                    Log.d("DARBAR", "do have");
                    String thing = dataSnapshot.child("groupspartof").getValue().toString();
                    if(thing.length()!=0){
                        thing = thing.substring(0, thing.length() - 1);
                        String[] another = thing.split(",");
                        for (int i = 0; i < another.length; i++) {
                            gs.add(another[i]);
                        }
                        myCallback.callbackGroups(gs);
                    } else {
                        gs.add("none");
                        myCallback.callbackGroups(gs);
                    }

                } else {
                    gs.add("none");
                    myCallback.callbackGroups(gs);
                }


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