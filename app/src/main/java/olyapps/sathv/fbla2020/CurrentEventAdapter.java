package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by sathv on 6/4/2018.
 */

public class CurrentEventAdapter extends ArrayAdapter<CurrentEvent> {
    Context context;
    ArrayList<CurrentEvent> arraylistcheckedbooks = null;

    //checked books adapter contructor
    public CurrentEventAdapter(Context context, int resource, ArrayList<CurrentEvent> arraylistcheckedbooks) {
        super(context, resource, arraylistcheckedbooks);
        this.context = context;
        this.arraylistcheckedbooks = arraylistcheckedbooks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CurrentEvent checkedBook = arraylistcheckedbooks.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.currenteventitem, parent, false);
        }

        //retrieve the fields
        final TextView bookTitle = (TextView) convertView.findViewById(R.id.rowname);

        //lastmessage.setText();

        final int[] currentm = new int[1];

        bookTitle.setText(checkedBook.rownameevent);
        Button remove = (Button) convertView.findViewById(R.id.delete);
        remove.setTag(position);

        Button viewteam = (Button) convertView.findViewById(R.id.viewteam);
        viewteam.setTag(position);

        Button approvalstatus = (Button) convertView.findViewById(R.id.approvalstatus);
        approvalstatus.setTag(position);

        Button addmember = convertView.findViewById(R.id.addmember);
        addmember.setTag(position);


        try {
            if (new SimpleDateFormat("MM/dd/yyyy").parse("012/01/2018").before(new Date())) {

                remove.setClickable(false);
                remove.setEnabled(false);

                addmember.setClickable(false);
                addmember.setEnabled(false);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (checkedBook.isTeam) {
            viewteam.setVisibility(View.VISIBLE);
            approvalstatus.setVisibility(View.VISIBLE);
            addmember.setVisibility(View.VISIBLE);

            DatabaseReference prema = FirebaseDatabase.getInstance().getReference().child("TeamEvents")
                    .child(checkedBook.rownameevent);
            prema.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("AdvisorStatus").getValue().toString().equals("0")) {
                        bookTitle.setTextColor(context.getResources().getColor(R.color.red));
                    } else {

                        boolean isoragne = false;
                        for (DataSnapshot snapshot :
                                dataSnapshot.getChildren()) {
                            if (snapshot.getValue().toString().contains("1")) {
                                isoragne = true;
                            } else {
                                isoragne = false;
                            }
                        }
                        if (isoragne) {
                            bookTitle.setTextColor(context.getResources().getColor(R.color.orange));
                        } else {
                            bookTitle.setTextColor(context.getResources().getColor(R.color.mygreen));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            viewteam.setVisibility(View.GONE);
            approvalstatus.setVisibility(View.GONE);
            addmember.setVisibility(View.GONE);
        }

        SharedPreferences sp = convertView.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String fullname =
                sp.getString(context.getString(R.string.fname), "") + " " + sp.getString(context.getString(R.string.lname), "");
        final String role =
                sp.getString(context.getString(R.string.role), "");


        approvalstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference prema = FirebaseDatabase.getInstance().getReference().child("TeamEvents")
                        .child(checkedBook.rownameevent);

                prema.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> statusvalues = new ArrayList<>();
                        ArrayList<String> vals = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals("AdvisorStatus")) {

                                statusvalues.add(snapshot.getValue().toString());
                                vals.add("Advisor");
                            } else {
                                String val = snapshot.getValue().toString();
                                String statuschar = val.charAt(val.length() - 1) + "";
                                String status = val.substring(0, val.length() - 1);
                                statusvalues.add(statuschar);
                                vals.add(status);
                            }
                        }

                        final String[] commas = new String[statusvalues.size()];
                        final ColourItem[] colors = new ColourItem[statusvalues.size()];

                        for (int i = 0; i < commas.length; i++) {
                            //advisor status handling
                            if (i == 0) {
                                String temp = statusvalues.get(i).toString();
                                if (temp.equals("0")) {
                                    commas[i] = "Advisor: Not approved: " + temp;
                                    colors[i] = new ColourItem(R.color.red);
                                } else {
                                    commas[i] = "Advisor: Approved: " + temp;
                                    colors[i] = new ColourItem(R.color.mygreen);
                                }
                            }
                            //other members handling
                            else {

                                String temp = statusvalues.get(i).toString();
                                if (temp.equals("0")) {

                                    commas[i] = vals.get(i) + ": Not Approved: " + temp;
                                    colors[i] = new ColourItem(R.color.red);
                                } else {
                                    commas[i] = vals.get(i) + ": Approved: " + temp;
                                    colors[i] = new ColourItem(R.color.mygreen);
                                }
                            }
                        }

                        ListAdapter adapter = new ArrayAdapter<ColourItem>(
                                context,
                                android.R.layout.select_dialog_item,
                                android.R.id.text1, colors) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);
                                TextView tv = (TextView) v.findViewById(android.R.id.text1);

                                int thing = colors[position].getColour();
                                tv.setTextColor(context.getResources().getColor(thing));
                                tv.setText(commas[position]);
                                tv.setTextSize(14);

                                return v;
                            }
                        };


                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Event Status")
                                .setAdapter(adapter, null);

                        builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        viewteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference prema = FirebaseDatabase.getInstance().getReference().child("TeamEvents")
                        .child(checkedBook.rownameevent);

                prema.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> teammembernames = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!snapshot.getKey().equals("AdvisorStatus")) {

                                teammembernames.add(snapshot.getValue().toString());
                            }
                        }

                        String[] commas = new String[teammembernames.size()];

                        for (int i = 0; i < commas.length; i++) {
                            String temp = teammembernames.get(i).toString();
                            temp = temp.substring(0, temp.length() - 1);
                            commas[i] = temp;
                        }

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("View Team Members")
                                .setItems(commas, null);

                        builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        if (checkedBook.isTeam) {
            remove.setText("Drop out");
        }

        if (role.equals("Advisor")) {
            remove.setVisibility(View.INVISIBLE);
            addmember.setVisibility(View.INVISIBLE);
        }

        addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference memberc = FirebaseDatabase.getInstance().getReference().child("TeamEvents")
                        .child(checkedBook.rownameevent);
                memberc.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        ArrayList<String> mSelectedItems = new ArrayList<>();
                        final ArrayList<String> mNames = new ArrayList<>();
                        final ArrayList<String> mAllnames = new ArrayList<>();
                        mSelectedItems.clear();
                        String f[] = LockScreen.getFnas();
                        String l[] = LockScreen.getLnas();
                        String[] advis = LockScreen.getAdviarray();

                        for (int i = 0; i < f.length; i++) {
                            mAllnames.add(f[i] + " " + l[i]);
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            mNames.add(snapshot.getValue().toString().substring(0, snapshot.getValue().toString().length()-1));
                        }

                        for (int i = 0; i < mNames.size(); i++) {

                          mAllnames.remove(mNames.get(i));

                        }

                        for (int i = 0; i < advis.length; i++) {
                            mAllnames.remove(advis[i]);
                        }

                        final CharSequence names[] = new CharSequence[mAllnames.size()];

                        for (int i = 0; i < mAllnames.size(); i++) {
                            names[i] = mAllnames.get(i);
                        }

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Add member")
                                .setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentm[0] = which;
                                        long count = dataSnapshot.getChildrenCount();
                                        memberc.child("Teammate" + count).setValue(mAllnames.get(currentm[0]) + "0");

                                        //notification to current guys and new person

                                        final ArrayList<String> adviemail = new ArrayList<>();
                                        final ArrayList<String> dte = new ArrayList<>();
                                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");
                                        dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String dt="";
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                    if (snapshot.child("role").getValue().toString().equals("Advisor")) {
                                                        adviemail.add(snapshot.child("email").getValue().toString());
                                                    }
                                                    for (int i = 0; i < mNames.size(); i++) {
                                                        if((snapshot.child("fname").getValue().toString()
                                                                + " " + snapshot.child("lname").getValue().toString())
                                                                .equals(mNames.get(i))){
                                                            if(snapshot.hasChild("device_token")){
                                                                dte.add(snapshot.child("device_token").getValue().toString());
                                                            }
                                                        }
                                                    }
                                                    if((snapshot.child("fname").getValue().toString() + " " + snapshot.child("lname").getValue().toString())
                                                            .equals(mAllnames.get( currentm[0]))){
                                                        if(snapshot.hasChild("device_token")){
                                                            dt = snapshot.child("device_token").getValue().toString();
                                                            dte.add(dt);
                                                        }
                                                    }
                                                }

                                                //SEMD EMAILA
                                                for (int i = 0; i < adviemail.size(); i++) {
                                                    String subject = "Member added to " + checkedBook.rownameevent;

                                                    String message = "A new member was added to the team event: " + checkedBook.rownameevent
                                                            + "The name of the member is " + mAllnames.get( currentm[0]);
                                                    SendMail sm = new SendMail(context, adviemail.get(i), subject, message);
                                                    sm.execute();

                                                }

                                                DatabaseReference newmember = FirebaseDatabase.getInstance().getReference().child("notificationAddMember");
                                                newmember.child("0883f53d-00d1-429b-8de6-200e91c1942a")
                                                        .child("AddInfo").setValue(mAllnames.get( currentm[0]) + "SEPERATOR"
                                                        + checkedBook.rownameevent + "SEPERATOR" + fullname + "SEPERATOR" + dte.toString());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



        final View finalConvertView = convertView;
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer index = (Integer) view.getTag();
                arraylistcheckedbooks.remove(index);
                notifyDataSetChanged();

                if (checkedBook.isTeam == false) {
                    final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("UserEvents")
                            .child(fullname);
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> keys = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                keys.add(snapshot.getKey().toString());
                            }
                            String deletingkey = keys.get(index);

                            dr.child(deletingkey).removeValue();

                            final Snackbar snackbar = Snackbar.make(finalConvertView, "Deleted. Reload page", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("TeamEvents")
                            .child(checkedBook.rownameevent);

                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ArrayList<String> fullnames = new ArrayList<>();
                            final ArrayList<String> dts = new ArrayList<>();
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {


                                fullnames.add(snapshot.getValue().toString().substring(0, snapshot.getValue().toString().length()-1));

                                if (snapshot.getValue().toString().substring(0, snapshot.getValue().toString().length() - 1).equals(fullname)) {
                                    String key = snapshot.getKey();
                                    dr.child(key).removeValue();

                                    final Snackbar snackbar = Snackbar.make(finalConvertView, "Dropped. Reload page", Snackbar.LENGTH_LONG);
                                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();


                                    final ArrayList<String> adviemail = new ArrayList<>();
                                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");
                                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                if (snapshot.child("role").getValue().toString().equals("Advisor")) {
                                                    adviemail.add(snapshot.child("email").getValue().toString());
                                                }
                                            }

                                            for (int i = 0; i < adviemail.size(); i++) {
                                                String subject = checkedBook.rownameevent + " Member Drop";

                                                String message = "A member dropped out from " + checkedBook.rownameevent + " Event " +
                                                        "\n" + fullname + " dropped. This email is an update of this team's status";
                                                SendMail sm = new SendMail(context, adviemail.get(i), subject, message);
                                                sm.execute();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                    DatabaseReference dru = FirebaseDatabase.getInstance().getReference().child("Users");
                                    dru.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot uidsnap : dataSnapshot.getChildren()) {
                                                for (int i = 0; i < fullnames.size(); i++) {
                                                    if(fullnames.get(i).equals((uidsnap.child("fname").getValue().toString() + " " + uidsnap.child("lname").getValue().toString()))
                                                            && !fullnames.get(i).equals(fullname)){

                                                        if(uidsnap.hasChild("device_token")){
                                                            dts.add(uidsnap.child("device_token").getValue().toString());
                                                        }
                                                    }
                                                }
                                            }
                                            DatabaseReference notifications = FirebaseDatabase.getInstance().getReference().child("notificationsDrop");
                                            notifications.child("5d3da0f6-e43f-4948-8bfe-658938104ddc")
                                                    .child("DeleteInfo").setValue(checkedBook.rownameevent + "SEPERATOR" + fullname + "SEPERATOR" + dts.toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //  dr.child(checkedBook.rownameevent).removeValue();
                }


            }
        });


        return convertView;
    }
}
