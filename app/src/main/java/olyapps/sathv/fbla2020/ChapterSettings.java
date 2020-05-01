package olyapps.sathv.fbla2020;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by sathv on 6/1/2018.
 */

public class ChapterSettings extends AppCompatActivity implements View.OnClickListener {

    String chapterid;
    private static final int GALLEY_REQUEST = 1;


    //basic stuff
    ArrayAdapter<CharSequence> adapter2;
    EditText chapid, name, zip;
    Spinner state;
    ImageView logo;
    ImageButton camclick;

    //datesstuff
    EditText statec, fallc, winterc;
    private DatePickerDialog.OnDateSetListener scdl, wcdl, fcdl;

    //rolekey stuff
    EditText mk, ok, ak;

    //chapter officers and images
    EditText pset, vpset, sset, tset, aset, puset;
    ImageView pimg, vpimg, simg, timg, aimg, puimg, chapimg;
    private int clickImage;
    Uri mImageuri = null;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private ArrayList<String> taglist = new ArrayList<String>();

    //role permissions
    CheckBox op0, op1, op2, op3, op4, op5, op6, op7;
    ArrayList<CheckBox> officerchecks = new ArrayList<>();
    ArrayList<CheckBox> adviserchecks = new ArrayList<>();

    //social media stuff
    EditText insta, fb;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        setContentView(R.layout.chaptersettings);
        //set the title of the screen
        setTitle("Chapter Settings");

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chapid = findViewById(R.id.chapidsett);
        name = findViewById(R.id.chapnamesett);
        zip = findViewById(R.id.chapzipsett);
        logo = findViewById(R.id.chaplogosett);
        camclick = findViewById(R.id.camclicksett);
        camclick.setOnClickListener(this);

        statec = findViewById(R.id.stateconfdate);
        winterc = findViewById(R.id.winterconfdate);
        fallc = findViewById(R.id.fallconfdate);

        mk = findViewById(R.id.memberset);
        ok = findViewById(R.id.officerset);
        ak = findViewById(R.id.adviserset);

        pset = findViewById(R.id.presidentnamesett);
        vpset = findViewById(R.id.vpnamesett);
        sset = findViewById(R.id.secnamesett);
        tset = findViewById(R.id.tresnamesett);
        puset = findViewById(R.id.pubrelnamesett);
        aset = findViewById(R.id.advisernamesett);

        pimg = findViewById(R.id.presimgsett);
        vpimg = findViewById(R.id.vpimgsett);
        simg = findViewById(R.id.secimgsett);
        timg = findViewById(R.id.tresimgsett);
        puimg = findViewById(R.id.pubrelimgsett);
        aimg = findViewById(R.id.advimgsett);
        chapimg = findViewById(R.id.chaplogosett);

        pimg.setOnClickListener(this);
        vpimg.setOnClickListener(this);
        simg.setOnClickListener(this);
        timg.setOnClickListener(this);
        puimg.setOnClickListener(this);
        aimg.setOnClickListener(this);


        op0 = findViewById(R.id.op0sett);
        op1 = findViewById(R.id.op1sett);
        op2 = findViewById(R.id.op2sett);
        op3 = findViewById(R.id.op3sett);
        officerchecks.add(op0);
        officerchecks.add(op1);
        officerchecks.add(op2);
        officerchecks.add(op3);
        op4 = findViewById(R.id.op4sett);
        op5 = findViewById(R.id.op5sett);
        op6 = findViewById(R.id.op6sett);
        op7 = findViewById(R.id.op7sett);
        adviserchecks.add(op4);
        adviserchecks.add(op5);
        adviserchecks.add(op6);
        adviserchecks.add(op7);

        insta = findViewById(R.id.instaurl);
        fb = findViewById(R.id.fburl);

        state = findViewById(R.id.chapstatesett);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(adapter2);
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                //parent.getItemAtPosition(position)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("Setup").child("ChapterName").getValue().toString());
                zip.setText(dataSnapshot.child("Setup").child("Zip").getValue().toString());
                chapid.setText(dataSnapshot.child("Setup").child("ID").getValue().toString());
                state.setSelection(find(dataSnapshot.child("Setup").child("State").getValue().toString()));
                Glide.with(ChapterSettings.this).load(dataSnapshot.child("Images").child("ChapterLogo").getValue().toString()).into(logo);

                statec.setText(dataSnapshot.child("Setup").child("StateConfDate").getValue().toString());
                fallc.setText(dataSnapshot.child("Setup").child("FallConfDate").getValue().toString());
                winterc.setText(dataSnapshot.child("Setup").child("WinterConfDate").getValue().toString());

                mk.setText(dataSnapshot.child("JoinCodes").child("MemberCode").getValue().toString());
                ok.setText(dataSnapshot.child("JoinCodes").child("OfficerCode").getValue().toString());
                ak.setText(dataSnapshot.child("JoinCodes").child("AdviserCode").getValue().toString());

                pset.setText(dataSnapshot.child("ChapterOfficers").child("President").getValue().toString());
                vpset.setText(dataSnapshot.child("ChapterOfficers").child("VicePresident").getValue().toString());
                sset.setText(dataSnapshot.child("ChapterOfficers").child("Secretary").getValue().toString());
                tset.setText(dataSnapshot.child("ChapterOfficers").child("Treasurer").getValue().toString());
                puset.setText(dataSnapshot.child("ChapterOfficers").child("PublicRelations").getValue().toString());
                aset.setText(dataSnapshot.child("ChapterOfficers").child("Adviser").getValue().toString());

                fb.setText(dataSnapshot.child("SocialMedia").child("Facebook").getValue().toString());
                insta.setText(dataSnapshot.child("SocialMedia").child("Instagram").getValue().toString());

                Glide.with(ChapterSettings.this).load(dataSnapshot.child("Images").child("PresidentImg").getValue().toString()).into(pimg);
                Glide.with(ChapterSettings.this).load(dataSnapshot.child("Images").child("VicePresidentImg").getValue().toString()).into(vpimg);
                Glide.with(ChapterSettings.this).load(dataSnapshot.child("Images").child("SecretaryImg").getValue().toString()).into(simg);
                Glide.with(ChapterSettings.this).load(dataSnapshot.child("Images").child("TreasurerImg").getValue().toString()).into(timg);
                Glide.with(ChapterSettings.this).load(dataSnapshot.child("Images").child("PublicRelationsImg").getValue().toString()).into(puimg);
                Glide.with(ChapterSettings.this).load(dataSnapshot.child("Images").child("AdviserImg").getValue().toString()).into(aimg);

                String arules = dataSnapshot.child("Roles").child("AdviserRules").getValue().toString();
                String orules = dataSnapshot.child("Roles").child("OfficerRules").getValue().toString();

                if (arules.contains("0")) {
                    op4.setChecked(true);
                }
                if (arules.contains("1")) {
                    op5.setChecked(true);
                }
                if (arules.contains("2")) {
                    op6.setChecked(true);
                }
                if (arules.contains("3")) {
                    op7.setChecked(true);
                }

                if (orules.contains("0")) {
                    op0.setChecked(true);
                }
                if (orules.contains("1")) {
                    op1.setChecked(true);
                }
                if (orules.contains("2")) {
                    op2.setChecked(true);
                }
                if (orules.contains("3")) {
                    op3.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                statec.setText(sdf.format(myCalendar.getTime()));
            }

        };

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                winterc.setText(sdf.format(myCalendar.getTime()));
            }

        };

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                fallc.setText(sdf.format(myCalendar.getTime()));
            }

        };

        statec.setInputType(InputType.TYPE_NULL);
        statec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ChapterSettings.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        winterc.setInputType(InputType.TYPE_NULL);
        winterc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ChapterSettings.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fallc.setInputType(InputType.TYPE_NULL);
        fallc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ChapterSettings.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(ChapterSettings.this, FBLAHome.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if (item.getItemId() == R.id.check) {
            //save , , , , images, ,


            if (!state.getSelectedItem().toString().equals("Select your state chapter")) {
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);

                dr.child("Setup").child("ChapterName").setValue(name.getText().toString());
                dr.child("Setup").child("State").setValue(state.getSelectedItem().toString());
                dr.child("Setup").child("Zip").setValue(zip.getText().toString());

                dr.child("Setup").child("StateConfDate").setValue(statec.getText().toString());
                dr.child("Setup").child("WinterConfDate").setValue(winterc.getText().toString());
                dr.child("Setup").child("FallConfDate").setValue(fallc.getText().toString());

                dr.child("SocialMedia").child("Instagram").setValue(insta.getText().toString());
                dr.child("SocialMedia").child("Facebook").setValue(fb.getText().toString());

                dr.child("ChapterOfficers").child("President").setValue(pset.getText().toString());
                dr.child("ChapterOfficers").child("VicePresident").setValue(vpset.getText().toString());
                dr.child("ChapterOfficers").child("Secretary").setValue(sset.getText().toString());
                dr.child("ChapterOfficers").child("Treasurer").setValue(tset.getText().toString());
                dr.child("ChapterOfficers").child("PublicRelations").setValue(puset.getText().toString());
                dr.child("ChapterOfficers").child("Adviser").setValue(aset.getText().toString());

                String ocrules = "";
                String acrules = "";

                for (int i = 0; i < officerchecks.size(); i++) {
                    if (officerchecks.get(i).isChecked()) {
                        ocrules += i + ",";
                    }
                }

                for (int i = 0; i < adviserchecks.size(); i++) {
                    if (adviserchecks.get(i).isChecked()) {
                        acrules += i + ",";
                    }
                }

                dr.child("Roles").child("OfficerRules").setValue(ocrules);
                dr.child("Roles").child("AdviserRules").setValue(acrules);

                final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child(chapid + "ImageFolder");

                for (int uploads = 0; uploads < ImageList.size(); uploads++) {
                    Uri Image = ImageList.get(uploads);
                    final String tag = taglist.get(uploads);
                    final StorageReference imagename = ImageFolder.child("image/" + Image.getLastPathSegment());

                    final int finalUploads = uploads;
                    imagename.putFile(Image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String url = String.valueOf(uri);
                                    SendLink(url, tag);

                                    if (finalUploads == (ImageList.size() - 1)) {
                                        ImageList.clear();
                                        taglist.clear();
                                    }
                                }
                            });

                        }
                    });
                }
                Toast.makeText(ChapterSettings.this, "Updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChapterSettings.this, "Choose valid state", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }


        return super.onCreateOptionsMenu(menu);

    }

    // Function to find the index of an element in a primitive array in Java
    public static int find(String target) {
        String[] states = {"Select your state chapter",
                "Alabama",
                "Alaska",
                "Arizona",
                "Arkansas",
                "California",
                "Colorado",
                "Connecticut",
                "Delaware",
                "District of Columbia",
                "Florida",
                "Georgia",
                "Guam",
                "Hawaii",
                "Idaho",
                "Illinois",
                "Indiana",
                "Iowa",
                "Kansas",
                "Kentucky",
                "Louisiana",
                "Maine",
                "Maryland",
                "Massachusetts",
                "Michigan",
                "Minnesota",
                "Mississippi",
                "Missouri",
                "Montana",
                "Nebraska",
                "Nevada",
                "New Hampshire",
                "New Jersey",
                "New Mexico",
                "New York",
                "North Carolina",
                "North Dakota",
                "Northern Marianas Islands",
                "Ohio",
                "Oklahoma",
                "Oregon",
                "Pennsylvania",
                "Puerto Rico",
                "Rhode Island",
                "South Carolina",
                "South Dakota",
                "Tennessee",
                "Texas",
                "Utah",
                "Vermont",
                "Virginia",
                "Virgin Islands",
                "Washington",
                "West Virginia",
                "Wisconsin",
                "Wyoming"};
        for (int i = 0; i < states.length; i++)
            if (states[i].equals(target)) {
                return i;
            }
        return -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (clickImage) {

            case 1:
                galrequest(requestCode, resultCode, data, chapimg);
                break;
            case 2:
                galrequest(requestCode, resultCode, data, pimg);
                break;
            case 3:
                galrequest(requestCode, resultCode, data, vpimg);
                break;
            case 4:
                galrequest(requestCode, resultCode, data, simg);
                break;
            case 5:
                galrequest(requestCode, resultCode, data, timg);
                break;
            case 6:
                galrequest(requestCode, resultCode, data, puimg);
                break;
            case 7:
                galrequest(requestCode, resultCode, data, aimg);
                break;
        }


    }

    public void galrequest(int requestCode, int resultCode, Intent data, ImageView iv) {
        if (requestCode == GALLEY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setFixAspectRatio(true).setAspectRatio(1, 1).
                    setGuidelines(CropImageView.Guidelines.ON).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageuri = result.getUri();
                iv.setImageURI(mImageuri);
                ImageList.add(mImageuri);

                taglist.add(iv.getTag().toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void SendLink(String url, String tag) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(chapterid).child("Images");
        databaseReference.child(tag).setValue(url);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camclicksett) {
            clickImage = 1;
        } else if (v.getId() == R.id.presimgsett) {
            clickImage = 2;
        } else if (v.getId() == R.id.vpimgsett) {
            clickImage = 3;
        } else if (v.getId() == R.id.secimgsett) {
            clickImage = 4;
        } else if (v.getId() == R.id.tresimgsett) {
            clickImage = 5;
        } else if (v.getId() == R.id.pubrelimgsett) {
            clickImage = 6;
        } else if (v.getId() == R.id.advimgsett) {
            clickImage = 7;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLEY_REQUEST);
    }
}