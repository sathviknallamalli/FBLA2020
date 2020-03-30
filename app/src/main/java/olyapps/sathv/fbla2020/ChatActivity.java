package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class ChatActivity extends AppCompatActivity {

    ArrayList<String> mSelectedItems = new ArrayList<>();
    ImageView sendButton;
    ImageView addchtbutotn;
    String message;
    EditText messageArea;
    DatabaseReference ref1, ref2, notifications, gn;
    FirebaseAuth mAuth;
    String already;
    FirebaseUser currentuser;
    DatabaseReference mRootRef;
    ImageButton viewmembercustom, leavecustom, addmembercustom;

    LinearLayout layout;

    ViewSwitcher custom_view;
    AvatarView custom_avatar;
    CircleImageView custom_circle;
    ImageView customonline;

    StorageReference mImageStorage;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private TextView mTitleView;
    private TextView mLastSeenView;

    private static final int GALLERY_PICK = 1;
    private static final int GALLERYGROUPPIC = 2;

    private Toolbar mChatToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();

        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.actionbarcolor), PorterDuff.Mode.SRC_ATOP);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(upArrow);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String yourusername = sp.getString(getString(R.string.fname), "urfn") +
                sp.getString(getString(R.string.lname), "urln");

        final String withspace = sp.getString(getString(R.string.fname), "urfn") +" " +
                sp.getString(getString(R.string.lname), "urln");

        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        custom_circle = (CircleImageView) findViewById(R.id.custom_bar_image);
        custom_view = findViewById(R.id.custom_view_switcher);
        custom_avatar = findViewById(R.id.custom_avatar);
        viewmembercustom = findViewById(R.id.viewmembercustom);
        leavecustom = findViewById(R.id.leavegroup);
        addmembercustom = findViewById(R.id.am);
        customonline = findViewById(R.id.custom_online);


        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        addchtbutotn = findViewById(R.id.addchatbutton);
        layout = findViewById(R.id.layout1);

        messageArea.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        mImageStorage = FirebaseStorage.getInstance().getReference();


        addchtbutotn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Members.isthisagroup || Members.isgroupcreated){
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERYGROUPPIC);
                }else{
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                }


            }
        });

        if (Members.isgroupcreated) {
            final ScrollView scrollView = findViewById(R.id.sv);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
            mTitleView.setText("Group: " + Members.groupname);
            mLastSeenView.setText("Group chat");
            custom_avatar.bind("Group: " + Members.groupname, null);
            customonline.setVisibility(View.INVISIBLE);


            viewmembercustom.setVisibility(View.VISIBLE);
            leavecustom.setVisibility(View.VISIBLE);
            addmembercustom.setVisibility(View.VISIBLE);
            addmembercustom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fa[] = LockScreen.getFnas();
                    String la[] = LockScreen.getLnas();
                    ArrayList<String> als = new ArrayList<>();
                    for (int i = 0; i < fa.length; i++) {
                        als.add(fa[i] + " " + la[i]);
                    }

                    String allofthem = Members.names;
                    allofthem = allofthem.substring(0, allofthem.length() - 1);
                    String[] commas = allofthem.split(",");

                    for (int i = 0; i < commas.length; i++) {
                        als.remove(commas[i]);
                    }

                    String adv[] = LockScreen.getAdviarray();
                    for (int i = 0; i < adv.length; i++) {
                        als.remove(adv[i]);
                    }

                    final CharSequence cs[] = new CharSequence[als.size()];

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Add member");
                    builder.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                            builder.setTitle("Confirm");
                            builder.setMessage("Are you sure you want to add this member?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, final int which) {
                                    final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("TeamEvents");
                                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            long count = dataSnapshot.getChildrenCount();
                                            dr.child("Member" + count+1).setValue(cs[which]);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if((snapshot.child("fname").getValue().toString() + " " +
                                                        snapshot.child("lname").getValue().toString()).equals(cs[which])){
                                                    String uid = snapshot.child("uid").getValue().toString();
                                                    if(snapshot.hasChild("groupspartof")){
                                                        String groupspartof = snapshot.child("groupspartof").getValue().toString();
                                                        groupspartof = groupspartof + Members.groupname + ",";
                                                        databaseReference.child(uid).child("groupspartof").setValue(groupspartof);
                                                    }

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("message", cs[which] + " added to the group by " + withspace);
                                    map.put("user", withspace);
                                    map.put("senderuid", mAuth.getCurrentUser().getUid());
                                    map.put("type", "default");
                                    // reference1.push().setValue(map);
                                    // reference2.push().setValue(map);

                                    String key = ref1.push().getKey();

                                    ref1.child(key).setValue(map);
                                    dialog.dismiss();

                                    gn = FirebaseDatabase.getInstance().getReference().child("notificationsGroupMessages");

                                    gn.child("f4289e07-57fc-4641-8b10-6674cf3f473e").child("GroupMessageInfo")
                                            .setValue(key + "SEPERATOR" + Members.groupname + "SEPERATOR" + yourusername + "SEPERATOR" +
                                                    Members.groupdts);
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });



                        }
                    });

                }
            });
            leavecustom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to leave the group");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            String muid = mAuth.getCurrentUser().getUid();
                            final DatabaseReference groups = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(muid);
                            groups.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String cureentgroups = dataSnapshot.child("groupspartof").getValue().toString();
                                    cureentgroups = cureentgroups.replace(Members.groupname + "," , "");
                                    groups.child("groupspartof").setValue(cureentgroups);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            final DatabaseReference removename = FirebaseDatabase.getInstance().getReference().child("ChatMessages")
                                    .child("Groups").child(Members.groupname);
                            removename.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                        if(!snapshot.getKey().equals("messages")){
                                            if(snapshot.getValue().toString().equals(withspace)){
                                                removename.child(snapshot.getKey()).removeValue();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            //addLine(withspace + " left the group");

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("message", withspace + " left the group");
                            map.put("user", withspace);
                            map.put("senderuid", mAuth.getCurrentUser().getUid());
                            map.put("type", "default");
                            // reference1.push().setValue(map);
                            // reference2.push().setValue(map);

                            String key = ref1.push().getKey();

                            ref1.child(key).setValue(map);
                            dialog.dismiss();

                            gn = FirebaseDatabase.getInstance().getReference().child("notificationsGroupMessages");

                            gn.child("f4289e07-57fc-4641-8b10-6674cf3f473e").child("GroupMessageInfo")
                                    .setValue(key + "SEPERATOR" + Members.groupname + "SEPERATOR" + yourusername + "SEPERATOR" +
                                            Members.groupdts);

                            getFragmentManager().popBackStackImmediate();
                            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                            finish();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            viewmembercustom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ofsef = "";
                    String afsef = "";
                    for (int i = 0; i < LockScreen.getOfficall().length; i++) {
                        ofsef = ofsef + LockScreen.getOfficall()[i] + ",";
                    }

                    for (int i = 0; i < LockScreen.getAdviarray().length; i++) {
                        afsef = afsef + LockScreen.getAdviarray()[i] + ",";
                    }

                    ofsef = ofsef.substring(0, ofsef.length() - 1);
                    afsef = afsef.substring(0, afsef.length() - 1);

                    String allofthem = Members.names;
                    allofthem = allofthem.substring(0, allofthem.length() - 1);
                    final String[] commas = allofthem.split(",");

                    for (int i = 0; i < commas.length; i++) {
                        if (ofsef.contains(commas[i])) {
                            commas[i] = commas[i] + " -- Officer";
                        } else if (afsef.contains(commas[i])) {
                            commas[i] = commas[i] + " -- Advisor";
                        }
                    }


                    final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("View Group Members")
                            .setItems(commas, null);

                    builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });


            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("ChatMessages").child("Groups").child(Members.groupname);


            for (int i = 0; i < Members.groupmembers.size(); i++) {
                String membername = "Member" + (i + 1);
                if (Members.groupmembersuid.get(i).equals(currentuser.getUid())) {
                    membername += "Creator";
                }
                ref3.child(membername).setValue(Members.groupmembers.get(i));
            }

            for (int i = 0; i < Members.groupmembersuid.size(); i++) {
                final DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference().child("Users").
                        child(Members.groupmembersuid.get(i));
                ref4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("groupspartof")) {
                            already = dataSnapshot.child("groupspartof").getValue().toString();

                            already = already + Members.groupname + ",";
                            ref4.child("groupspartof").setValue(already);
                        } else {
                            ref4.child("groupspartof").setValue(Members.groupname + ",");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


            ref1 = FirebaseDatabase.getInstance().getReference().child("ChatMessages").child("Groups").child(Members.groupname).child("messages");

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageText = messageArea.getText().toString();

                    if (!messageText.equals("")) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", messageText);
                        map.put("user", withspace);
                        map.put("senderuid", mAuth.getCurrentUser().getUid());
                        map.put("type", "text");


                        String key = ref1.push().getKey();
                        ref1.child(key).setValue(map);
                        messageArea.setText("");

                        gn = FirebaseDatabase.getInstance().getReference().child("notificationsGroupMessages");

                        gn.child("f4289e07-57fc-4641-8b10-6674cf3f473e").child("GroupMessageInfo")
                                .setValue(key + "SEPERATOR" + Members.groupname + "SEPERATOR" + yourusername + "SEPERATOR" +
                                        Members.groupdts);

                    }
                    messageArea.setText("");

                    MediaPlayer mPlayer = MediaPlayer.create(ChatActivity.this, R.raw.ping);

                    try {
                        mPlayer.prepare();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    mPlayer.start();
                }
            });


            ref1.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Map<String, String> map = (Map) dataSnapshot.getValue();
                    String key = dataSnapshot.getKey();
                    String message = map.get("message").toString();
                    String userName = map.get("user").toString();
                    String senderuid = map.get("senderuid").toString();

                    String type = "";
                    if(map.get("type") != null){
                       type = map.get("type").toString();
                    }


                    if (userName.equals(withspace)) {
                        if(type.equals("default")){
                            addLine(message);
                        }else{
                            addMessageBox(message, mAuth.getCurrentUser().getUid(), 1, type);
                        }
                    } else {
                        if(type.equals("default")){
                            addLine(message);
                        }else{
                            addMessageBox(userName + ":\n" + message, senderuid, 2, type);
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (Members.isthisagroup) {
            final ScrollView scrollView = findViewById(R.id.sv);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
            mTitleView.setText("Group: " + Members.groupname);
            ref1 = FirebaseDatabase.getInstance().getReference().child("ChatMessages").child("Groups").child(Members.groupname).child("messages");

            //          String allofthem = Members.names;
//            allofthem = allofthem.substring(0, allofthem.length()-1);
            customonline.setVisibility(View.INVISIBLE);

            mLastSeenView.setText("");
            custom_avatar.bind("Group: " + Members.groupname, null);


            viewmembercustom.setVisibility(View.VISIBLE);
            leavecustom.setVisibility(View.VISIBLE);
            addmembercustom.setVisibility(View.VISIBLE);
            addmembercustom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fa[] = LockScreen.getFnas();
                    String la[] = LockScreen.getLnas();
                    ArrayList<String> als = new ArrayList<>();
                    for (int i = 0; i < fa.length; i++) {
                        als.add(fa[i] + " " + la[i]);
                    }

                    String allofthem = Members.names;
                    allofthem = allofthem.substring(0, allofthem.length() - 1);
                    String[] commas = allofthem.split(",");

                    for (int i = 0; i < commas.length; i++) {
                        als.remove(commas[i]);
                    }

                    String adv[] = LockScreen.getAdviarray();
                    for (int i = 0; i < adv.length; i++) {
                        als.remove(adv[i]);
                    }

                    final CharSequence cs[] = new CharSequence[als.size()];

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Add member");
                    builder.setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                            builder.setTitle("Confirm");
                            builder.setMessage("Are you sure you want to add this member?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, final int which) {
                                    final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("TeamEvents");
                                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            long count = dataSnapshot.getChildrenCount();
                                            dr.child("Member" + count+1).setValue(cs[which]);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if((snapshot.child("fname").getValue().toString() + " " +
                                                        snapshot.child("lname").getValue().toString()).equals(cs[which])){
                                                    String uid = snapshot.child("uid").getValue().toString();
                                                    if(snapshot.hasChild("groupspartof")){
                                                        String groupspartof = snapshot.child("groupspartof").getValue().toString();
                                                        groupspartof = groupspartof + Members.groupname + ",";
                                                        databaseReference.child(uid).child("groupspartof").setValue(groupspartof);
                                                    }

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("message", cs[which] + " added to the group by " + withspace);
                                    map.put("user", withspace);
                                    map.put("senderuid", mAuth.getCurrentUser().getUid());
                                    map.put("type", "default");
                                    // reference1.push().setValue(map);
                                    // reference2.push().setValue(map);

                                    String key = ref1.push().getKey();

                                    ref1.child(key).setValue(map);
                                    dialog.dismiss();

                                    gn = FirebaseDatabase.getInstance().getReference().child("notificationsGroupMessages");

                                    gn.child("f4289e07-57fc-4641-8b10-6674cf3f473e").child("GroupMessageInfo")
                                            .setValue(key + "SEPERATOR" + Members.groupname + "SEPERATOR" + yourusername + "SEPERATOR" +
                                                    Members.groupdts);
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                        }
                    });

                }
            });
            leavecustom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to leave the group");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            String muid = mAuth.getCurrentUser().getUid();
                            final DatabaseReference groups = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(muid);
                            groups.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String cureentgroups = dataSnapshot.child("groupspartof").getValue().toString();
                                    cureentgroups = cureentgroups.replace(Members.groupname + "," , "");
                                    groups.child("groupspartof").setValue(cureentgroups);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            final DatabaseReference removename = FirebaseDatabase.getInstance().getReference().child("ChatMessages")
                                    .child("Groups").child(Members.groupname);
                            removename.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                        if(!snapshot.getKey().equals("messages")){
                                            if(snapshot.getValue().toString().equals(withspace)){
                                                removename.child(snapshot.getKey()).removeValue();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            //addLine(withspace + " left the group");

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("message", withspace + " left the group");
                            map.put("user", withspace);
                            map.put("senderuid", mAuth.getCurrentUser().getUid());
                            map.put("type", "default");
                            // reference1.push().setValue(map);
                            // reference2.push().setValue(map);

                            String key = ref1.push().getKey();

                            ref1.child(key).setValue(map);
                            dialog.dismiss();

                            gn = FirebaseDatabase.getInstance().getReference().child("notificationsGroupMessages");

                            gn.child("f4289e07-57fc-4641-8b10-6674cf3f473e").child("GroupMessageInfo")
                                    .setValue(key + "SEPERATOR" + Members.groupname + "SEPERATOR" + yourusername + "SEPERATOR" +
                                            Members.groupdts);

                            getFragmentManager().popBackStackImmediate();
                            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                            finish();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            viewmembercustom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ofsef = "";
                    String afsef = "";
                    for (int i = 0; i < LockScreen.getOfficall().length; i++) {
                        ofsef = ofsef + LockScreen.getOfficall()[i] + ",";
                    }

                    for (int i = 0; i < LockScreen.getAdviarray().length; i++) {
                        afsef = afsef + LockScreen.getAdviarray()[i] + ",";
                    }

                    ofsef = ofsef.substring(0, ofsef.length() - 1);
                    afsef = afsef.substring(0, afsef.length() - 1);

                    String allofthem = Members.names;
                    allofthem = allofthem.substring(0, allofthem.length() - 1);
                    final String[] commas = allofthem.split(",");

                    for (int i = 0; i < commas.length; i++) {
                        if (ofsef.contains(commas[i])) {
                            commas[i] = commas[i] + " -- Officer";
                        } else if (afsef.contains(commas[i])) {
                            commas[i] = commas[i] + " -- Advisor";
                        }
                    }


                    final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("View Group Members")
                            .setItems(commas, null);

                    builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageText = messageArea.getText().toString();

                    if (!messageText.equals("")) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", messageText);
                        map.put("user", withspace);
                        map.put("senderuid", mAuth.getCurrentUser().getUid());
                        map.put("type", "text");
                        // reference1.push().setValue(map);
                        // reference2.push().setValue(map);

                        String key = ref1.push().getKey();

                        ref1.child(key).setValue(map);
                        messageArea.setText("");

                        gn = FirebaseDatabase.getInstance().getReference().child("notificationsGroupMessages");

                        gn.child("f4289e07-57fc-4641-8b10-6674cf3f473e").child("GroupMessageInfo")
                                .setValue(key + "SEPERATOR" + Members.groupname + "SEPERATOR" + yourusername + "SEPERATOR" +
                                        Members.groupdts);


                        final ScrollView scrollView = findViewById(R.id.sv);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });

                        MediaPlayer mPlayer = MediaPlayer.create(ChatActivity.this, R.raw.ping);

                        try {
                            mPlayer.prepare();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        mPlayer.start();

                    }
                    messageArea.setText("");
                }
            });


            ref1.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Map<String, String> map = (Map) dataSnapshot.getValue();
                    String key = dataSnapshot.getKey();
                    String message = map.get("message").toString();
                    String userName = map.get("user").toString();
                    String senderuid = map.get("senderuid").toString();


                    String type = map.get("type").toString();


                    if (userName.equals(withspace)) {
                        if(type.equals("default")){
                            addLine(message);
                        }else{
                            addMessageBox(message, mAuth.getCurrentUser().getUid(), 1, type);
                        }
                    } else {
                        if(type.equals("default")){
                            addLine(message);
                        }else{
                            addMessageBox(userName + ":\n" + message, senderuid, 2, type);
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {

            mTitleView.setText(UserDetails.fullname);
            viewmembercustom.setVisibility(View.INVISIBLE);
            leavecustom.setVisibility(View.INVISIBLE);

            mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.child("Users").child(UserDetails.chatuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    String image = dataSnapshot.child("profpic").getValue().toString();

                    if (image.equals("nocustomimage")) {
                        custom_avatar.bind(dataSnapshot.child("fname").getValue().toString() + " " + dataSnapshot.child("lname")
                                .getValue().toString(), null);
                    } else {
                        custom_view.showNext();
                        Glide.with(getApplicationContext()).load(image).into(custom_circle);
                    }

                    if (dataSnapshot.hasChild("online")) {
                        String online = dataSnapshot.child("online").getValue().toString();

                        if (online.equals("true")) {

                            mLastSeenView.setText("online");
                            customonline.setVisibility(View.VISIBLE);

                        } else {

                            GetTimeAgo getTimeAgo = new GetTimeAgo();

                            long lastTime = Long.parseLong(online);

                            String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, ChatActivity.this);

                            mLastSeenView.setText(lastSeenTime);
                            customonline.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        mLastSeenView.setText("not online");
                        customonline.setVisibility(View.INVISIBLE);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            ref1 = FirebaseDatabase.getInstance().getReference().child("ChatMessages").child(yourusername + "_" + UserDetails.chatWith);
            ref2 = FirebaseDatabase.getInstance().getReference().child("ChatMessages").child(UserDetails.chatWith + "_" + yourusername);

            notifications = FirebaseDatabase.getInstance().getReference().child("notificationsMessages");
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageText = messageArea.getText().toString();

                    if (!messageText.equals("")) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("message", messageText.toString());
                        map.put("user", withspace.toString());
                        map.put("recieverUid", UserDetails.chatuid.toString());
                        map.put("time", ServerValue.TIMESTAMP);
                        map.put("type", "text");


                        // reference1.push().setValue(map);
                        // reference2.push().setValue(map);

                        String key = ref1.push().getKey();

                        ref1.child(key).setValue(map);
                        ref2.push().setValue(map);
                        messageArea.setText("");

                        notifications.child("15d7c782-9b57-11e8-98d0-529269fb1459")
                                .child("Messagekey").setValue(key +
                                "SEPERATOR" + yourusername + "_" + UserDetails.chatWith);
                        final ScrollView scrollView = findViewById(R.id.sv);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });

                        MediaPlayer mPlayer = MediaPlayer.create(ChatActivity.this, R.raw.ping);

                        try {
                            mPlayer.prepare();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        mPlayer.start();


                    }
                    messageArea.setText("");
                }
            });


            ref1.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Map<String, String> map = (Map) dataSnapshot.getValue();
                    String key = dataSnapshot.getKey();
                    String message = map.get("message").toString();
                    String userName = map.get("user").toString();
                    //  String time = map.get("time").toString();
                    //String type = map.get("type").toString();

                    String type = "text";
                    if(map.get("type")!= null){
                        type = map.get("type").toString();
                    }


                    if (userName.equals(withspace)) {
                        addMessageBox(message, mAuth.getCurrentUser().getUid(), 1, type);

                    } else {
                        addMessageBox(message, UserDetails.chatuid, 2, type);

                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    public void addMessageBox(final String message, String uid, int type, String textorimage) {
        TextView textView = new TextView(this);
        textView.setTextSize(16);
        textView.setPadding(30, 30, 30, 30);
        textView.setMaxWidth(700);

        ImageView imageView = new ImageView(this);
       // imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, FullImage.class);
                i.putExtra("imageuri", message);
                startActivity(i);
            }
        });

        final CircleImageView profileImage = new CircleImageView(this);
        final AvatarView profileavatar = new AvatarView(this);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profpic").getValue().toString().equals("nocustomimage")) {
                    Glide.with(getApplicationContext()).load(R.drawable.defaultimg).into(profileImage);
                } else {
                    String uri = dataSnapshot.child("profpic").getValue().toString();
                    Glide.with(getApplicationContext()).load(uri).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams another = new LinearLayout.LayoutParams(76, 76);


        final ScrollView scrollView = findViewById(R.id.sv);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        if (type == 1) {

            if(textorimage.equals("text")){

                textView.setBackgroundResource(R.drawable.mymtb);
                textView.setText(message);
                textView.setTextColor(getResources().getColor(R.color.black));
                another.setMargins(0, 7, 20, 0);
                lp.setMargins(0, 7, 100, 0);
                another.gravity = Gravity.RIGHT;
                profileImage.setLayoutParams(another);

                lp.gravity = Gravity.RIGHT;
                textView.setLayoutParams(lp);

                layout.addView(profileImage);
                layout.addView(textView);

            }else if(textorimage.equals("image")){
                Glide.with(getApplicationContext()).load(message).into(imageView);

                //ANOTHER IS ONLY PROF PIC
                another.setMargins(0, 7, 20, 0);
                lp.setMargins(0, 7, 100, 0);
                another.gravity = Gravity.RIGHT;
                profileImage.setLayoutParams(another);

                lp.gravity = Gravity.RIGHT;
                imageView.setLayoutParams(lp);

                layout.addView(profileImage);
                layout.addView(imageView);
            }

        } else {
            if(textorimage.equals("text")){
                textView.setBackgroundResource(R.drawable.mtb);
                textView.setText(message);
                textView.setTextColor(getResources().getColor(R.color.white));
                another.setMargins(20, 7, 0, 0);
                lp.setMargins(100, 7, 0, 0);
                another.gravity = Gravity.LEFT;
                profileImage.setLayoutParams(another);

                lp.gravity = Gravity.LEFT;
                textView.setLayoutParams(lp);

                layout.addView(profileImage);
                layout.addView(textView);
            } else if(textorimage.equals("image")){
                Glide.with(getApplicationContext()).load(message).into(imageView);

                another.setMargins(20, 7, 0, 0);
                lp.setMargins(100, 7, 0, 0);
                another.gravity = Gravity.LEFT;
                profileImage.setLayoutParams(another);

                lp.gravity = Gravity.LEFT;
                imageView.setLayoutParams(lp);

                layout.addView(profileImage);
                layout.addView(imageView);


            }


        }




        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        };
        scrollView.post(runnable);

        scrollView.fullScroll(View.FOCUS_DOWN);
    }


    public void addLine(String message){
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);
        textView.setTextColor(getResources().getColor(R.color.gray));
        textView.setTextSize(12);
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        lp.gravity = Gravity.CENTER;
        textView.setLayoutParams(lp);

        layout.addView(textView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        getFragmentManager().popBackStackImmediate();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStackImmediate();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            final String yourusername = sp.getString(getString(R.string.fname), "urfn") +
                    sp.getString(getString(R.string.lname), "urln");

            ref1 = FirebaseDatabase.getInstance().getReference().child("ChatMessages").child(yourusername + "_" + UserDetails.chatWith);
            ref2 = FirebaseDatabase.getInstance().getReference().child("ChatMessages").child(UserDetails.chatWith + "_" + yourusername);


            final String key = ref1.push().getKey();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Message_images");
            final StorageReference photoRef = storageReference.child(key + ".jpg");



            Task<UploadTask.TaskSnapshot> uploadTask;
            uploadTask = photoRef.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("message", downloadUri.toString());
                        map.put("user", yourusername.toString());
                        map.put("recieverUid", UserDetails.chatuid.toString());
                        map.put("time", ServerValue.TIMESTAMP);
                        map.put("type", "image");

                        ref1.child(key).setValue(map);
                        ref2.push().setValue(map);
                        messageArea.setText("");

                        notifications.child("15d7c782-9b57-11e8-98d0-529269fb1459")
                                .child("Messagekey").setValue(key +
                                "SEPERATOR" + yourusername + "_" + UserDetails.chatWith);

                    }
                }
            });
        }else if(requestCode == GALLERYGROUPPIC && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            ref1 = FirebaseDatabase.getInstance().getReference().child("ChatMessages").child("Groups").child(Members.groupname).child("messages");

            final String key = ref1.push().getKey();

            SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            final String yourusername = sp.getString(getString(R.string.fname), "urfn") +
                    sp.getString(getString(R.string.lname), "urln");

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Message_images");
            final StorageReference photoRef = storageReference.child(key + ".jpg");

            Task<UploadTask.TaskSnapshot> uploadTask;
            uploadTask = photoRef.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", downloadUri.toString());
                        map.put("user", yourusername);
                        map.put("senderuid", mAuth.getCurrentUser().getUid());
                        map.put("type", "image");

                        ref1.child(key).setValue(map);
                        gn = FirebaseDatabase.getInstance().getReference().child("notificationsGroupMessages");

                        gn.child("f4289e07-57fc-4641-8b10-6674cf3f473e").child("GroupMessageInfo")
                                .setValue(key + "SEPERATOR" + Members.groupname + "SEPERATOR" + yourusername + "SEPERATOR" +
                                        Members.groupdts);


                    }
                }
            });

        }
    }
}
