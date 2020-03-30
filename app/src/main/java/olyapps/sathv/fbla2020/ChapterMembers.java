package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ChapterMembers extends Fragment {
    ListView chapmembers;

    PersonAdapter adapter;
    ArrayList<Person> memebers;
    String[] fnstemp = LockScreen.getFnas();
    String[] lnstemp = LockScreen.getLnas();
    String[] uidstemp = LockScreen.getUidasall();


    public ChapterMembers() {
        // Required empty public constructor
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.membersmenu, menu);
    }*/

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.chaptermembers, container, false);

        //  getActivity().setTitle("ChapInbox");

        final String[] fns = new String[fnstemp.length + 1];
        final String[] lns = new String[lnstemp.length + 1];
        final String[] uids = new String[uidstemp.length + 1];

        SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String fname = sp.getString(getString(R.string.fname), "fname");
        final String lname = sp.getString(getString(R.string.lname), "lname");
        String uname = sp.getString(getString(R.string.uid), "uid");

        for (int i = 0; i < fns.length; i++) {
            if (i == fns.length - 1) {
                fns[i] = fname;
                lns[i] = lname;
                uids[i] = uname;
            } else {
                fns[i] = fnstemp[i];
                lns[i] = lnstemp[i];
                uids[i] = uidstemp[i];
            }
        }

        String[] fullnames = new String[fns.length];

        chapmembers = view.findViewById(R.id.chaptermembers);
        memebers = new ArrayList<Person>();


        for (int i = 0; i < fns.length; i++) {
            fullnames[i] = fns[i] + " " + lns[i];
        }

        fullnames = sortItems(fullnames, uids);
        final String[] finalFullnames = fullnames;

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < fns.length; i++) {

                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        String tempname =
                                snapshot.child("fname").getValue().toString() + " " + snapshot.child("lname").getValue().toString();
                        if (tempname.equals(finalFullnames[i])) {
                            String uid =
                                    snapshot.child("uid").getValue().toString();
                            uids[i] = uid;
                            memebers.add(new Person(finalFullnames[i], "chap", uid));
                        }
                    }

                }
                adapter = new PersonAdapter(view.getContext(), R.layout.perperson, memebers);
                chapmembers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        chapmembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                final CharSequence options[] = new CharSequence[]{"Open profile", "Send message"};
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Select option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!finalFullnames[position].equals(fname + " " + lname)) {
                            if (options[which].equals("Send message")) {

                                String str = finalFullnames[position];
                                int delete = spacechar(str);
                                str = str.replace(str.charAt(delete) + "", "");

                                UserDetails.chatWith = str;
                                UserDetails.chatuid = uids[position];
                                UserDetails.fullname = finalFullnames[position];

                                Members.isgroupcreated = false;
                                Members.isthisagroup = false;

                                Intent i = new Intent(view.getContext(), ChatActivity.class);
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(ChapterMembers.class.getName()).commit();
                                fm.executePendingTransactions();
                            } else if (options[which].equals("Open profile")) {
                                Intent i = new Intent(view.getContext(), OtherProfile.class);
                                i.putExtra("uid", uids[position]);
                                i.putExtra("name", finalFullnames[position]);
                                startActivity(i);

                                FragmentManager fm = getFragmentManager();
                                fm.beginTransaction().addToBackStack(ChapterMembers.class.getName()).commit();
                                fm.executePendingTransactions();


                            }
                        }


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

        return view;
    }

    private String[] sortItems(String cargohold[], String[] uids) {
        String temp, tempu;
        boolean wasSwap = true;
        for (int index1 = 0; index1 < cargohold.length - 1 && wasSwap; ++index1) {
            wasSwap = false;
            for (int index2 = 0; index2 < cargohold.length - index1 - 1; ++index2) {
                if (cargohold[index2].compareToIgnoreCase(cargohold[index2 + 1]) > 0) {
                    temp = cargohold[index2];
                    cargohold[index2] = cargohold[index2 + 1];
                    cargohold[index2 + 1] = temp;

                    tempu = uids[index2];
                    uids[index2] = uids[index2 + 1];
                    uids[index2 + 1] = tempu;

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

}