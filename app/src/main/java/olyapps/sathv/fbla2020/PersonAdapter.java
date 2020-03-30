package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sathv on 6/4/2018.
 */

public class PersonAdapter extends ArrayAdapter<Person> {
    Context context;
    ArrayList<Person> arraylistcheckedbooks = null;
    String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //checked books adapter contructor
    public PersonAdapter(Context context, int resource, ArrayList<Person> arraylistcheckedbooks) {
        super(context, resource, arraylistcheckedbooks);
        this.context = context;
        this.arraylistcheckedbooks = arraylistcheckedbooks;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Person checkedBook = arraylistcheckedbooks.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.perperson, parent, false);
        }
        Firebase.setAndroidContext(context);

        final TextView lastmessage = (TextView) convertView.findViewById(R.id.messagebody);


        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);

        final String yourusername = sp.getString(context.getString(R.string.fname), "fname") + " " + sp.getString(context.getString(R.string.lname), "fname");
        String nospace = sp.getString(context.getString(R.string.fname), "fname") + sp.getString(context.getString(R.string.lname), "lname");

        String fn = checkedBook.personname;

        int delete = spacechar(fn);
        fn = fn.replace(fn.charAt(delete) + "", "");


        if (checkedBook.isGroup.equals("chap")) {
            lastmessage.setText("");
        }

        if (checkedBook.isGroup.equals("false")) {
            DatabaseReference getlast = FirebaseDatabase.getInstance().getReference().child("ChatMessages")
                    .child(fn + "_" + nospace);

            Query lastQuery = getlast.orderByKey().limitToLast(1);
            lastQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String message = child.child("message").getValue().toString();
                            // viewHolder.setUserStatus(message);

                            if (message.contains("https://firebasestorage.googleapis.com")) {
                                lastmessage.setText("Image");
                            } else {
                                lastmessage.setText(message);
                            }

                        }
                    } else {
                        lastmessage.setText("Tap to start conversation");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        if (checkedBook.isGroup.equals("true")) {

            String groupname = checkedBook.personname;
            groupname = groupname.replace("Group: ", "");

            DatabaseReference getlast = FirebaseDatabase.getInstance().getReference().child("ChatMessages")
                    .child("Groups").child(groupname).child("messages");

            Query lastQuery = getlast.orderByKey().limitToLast(1);
            lastQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String message = child.child("message").getValue().toString();
                            // viewHolder.setUserStatus(message);
                            if (message.contains("https://firebasestorage.googleapis.com")) {
                                lastmessage.setText("Image");
                            } else {
                                lastmessage.setText(message);
                            }
                        }
                    } else {
                        lastmessage.setText("Tap to start conversation");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        //retrieve the fields
        final TextView bookTitle = (TextView) convertView.findViewById(R.id.messagetitle);
        TextView name = (TextView) convertView.findViewById(R.id.name2);
        final CircleImageView cmiv = convertView.findViewById(R.id.userperimage);


        final TextView messageonline = convertView.findViewById(R.id.messagelastseen);

        if (yourusername.equals(checkedBook.personname)) {
            String picurl = sp.getString(context.getString(R.string.profpic), "fname");
            if (picurl.equals("nocustomimage")) {
                Glide.with(context).load(R.drawable.primaryuser).into(cmiv);
            } else {
                Glide.with(context).load(picurl).into(cmiv);

            }
            messageonline.setText("online");
        } else {

            if (!checkedBook.uid.equals("nocustomimage")) {

                DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users").child(checkedBook.uid);
                dr.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("profpic")){
                            String imageurl = dataSnapshot.child("profpic").getValue().toString();


                            if (imageurl.equals("nocustomimage")) {
                                Glide.with(context).load(R.drawable.primaryuser).into(cmiv);
                                // Glide.with(context).load("https://upload.wikimedia.org/wikipedia/commons/thumb/9/93/Default_profile_picture_%28male%29_on_Facebook.jpg/600px-Default_profile_picture_%28male%29_on_Facebook.jpg").into(cmiv);
                            } else {
                                Glide.with(context).load(imageurl).into(cmiv);
                            }
                        }



                        if (dataSnapshot.hasChild("online")) {
                            String string = dataSnapshot.child("online").getValue().toString();

                            if (string.equals("true")) {
                                messageonline.setText("online");
                            } else {
                                GetTimeAgo getTimeAgo = new GetTimeAgo();

                                long lastTime = Long.parseLong(string);

                                String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, context);

                                messageonline.setText(lastSeenTime);
                                bookTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            }
                        } else {
                            bookTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            messageonline.setText("not available");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            } else {

                //GROUP
                // userinitials.bind(checkedBook.personname, null);

                Glide.with(context).load(R.drawable.primarygroup).into(cmiv);
                messageonline.setText("");
                bookTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }


        }


        //String url2 = url + allkeys.get()

        name.setText("");

        //lastmessage.setText(message);
        //set the appropriate fields with the appropriate info
        bookTitle.setText(checkedBook.personname);


        return convertView;
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
