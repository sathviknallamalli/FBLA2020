package olyapps.sathv.fbla2020;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import androidx.appcompat.app.AppCompatActivity;

public class LastStep extends AppCompatActivity {

    ImageView chapterlogo;
    Uri mImageuri = null;
    private static final int GALLEY_REQUEST = 1;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private ArrayList<String> taglist = new ArrayList<String>();

    ImageView presimg;
    ImageView vpimg;
    ImageView secimg;
    ImageView tresimg;
    ImageView pubrelimg;
    ImageView advimg;

    private int clickImage;
    String chapid;

    private DatePickerDialog.OnDateSetListener scdl, wcdl, fcdl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_step);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        LinearLayout regll = findViewById(R.id.lastlayout);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(regll.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        Intent intent = getIntent();
        chapid = intent.getExtras().getString("chapterid");

        final EditText sc = findViewById(R.id.scdate);
        final EditText wc = findViewById(R.id.wcdate);
        final EditText fc = findViewById(R.id.fcdate);

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

                sc.setText(sdf.format(myCalendar.getTime()));
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

                wc.setText(sdf.format(myCalendar.getTime()));
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

                fc.setText(sdf.format(myCalendar.getTime()));
            }

        };

        sc.setInputType(InputType.TYPE_NULL);
        sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LastStep.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        wc.setInputType(InputType.TYPE_NULL);
        wc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LastStep.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fc.setInputType(InputType.TYPE_NULL);
        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LastStep.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });




        final EditText pres = findViewById(R.id.presidentname);
        final EditText vp = findViewById(R.id.vpname);
        final EditText sec = findViewById(R.id.secname);
        final EditText tres = findViewById(R.id.tresname);
        final EditText pubrel = findViewById(R.id.pubrelname);
        final EditText advi = findViewById(R.id.advisername);

        presimg = findViewById(R.id.presimg);
        vpimg = findViewById(R.id.vpimg);
        secimg = findViewById(R.id.secimg);
        tresimg = findViewById(R.id.tresimg);
        pubrelimg = findViewById(R.id.pubrelimg);
        advimg = findViewById(R.id.advimg);

        final EditText instaurl = findViewById(R.id.instaurl);
        final EditText fburl = findViewById(R.id.fburl);

        chapterlogo = findViewById(R.id.chapterlogo);


        Button complete = findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sc.getText().toString().isEmpty() || wc.getText().toString().isEmpty() || fc.getText().toString().isEmpty()
                || ImageList.size()!=7 || taglist.size() != 7 || instaurl.getText().toString().isEmpty() ||
                fburl.getText().toString().isEmpty()){
                    Toast.makeText(LastStep.this, "Missing field(s)", Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseReference chap = FirebaseDatabase.getInstance().getReference().child("Chapters")
                            .child(chapid);

                    chap.child("Setup").child("StateConfDate").setValue(sc.getText().toString());
                    chap.child("Setup").child("WinterConfDate").setValue(wc.getText().toString());
                    chap.child("Setup").child("FallConfDate").setValue(fc.getText().toString());

                    chap.child("ChapterOfficers").child("President").setValue(pres.getText().toString());
                    chap.child("ChapterOfficers").child("VicePresident").setValue(vp.getText().toString());
                    chap.child("ChapterOfficers").child("Secretary").setValue(sec.getText().toString());
                    chap.child("ChapterOfficers").child("Treasurer").setValue(tres.getText().toString());
                    chap.child("ChapterOfficers").child("PublicRelations").setValue(pubrel.getText().toString());
                    chap.child("ChapterOfficers").child("Adviser").setValue(advi.getText().toString());

                    chap.child("SocialMedia").child("Instagram").setValue(instaurl.getText().toString());
                    chap.child("SocialMedia").child("Facebook").setValue(fburl.getText().toString());


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


                    Intent intent = new Intent(getApplicationContext(), PreFinish.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                    finish();
                }


            }
        });

    }

    private void SendLink(String url, String tag) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(chapid).child("Images");
        databaseReference.child(tag).setValue(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (clickImage) {

            case 1:
                galrequest(requestCode, resultCode, data, chapterlogo);
                break;
            case 2:
                galrequest(requestCode, resultCode, data, presimg);
                break;
            case 3:
                galrequest(requestCode, resultCode, data, vpimg);
                break;
            case 4:
                galrequest(requestCode, resultCode, data, secimg);
                break;
            case 5:
                galrequest(requestCode, resultCode, data, tresimg);
                break;
            case 6:
                galrequest(requestCode, resultCode, data, pubrelimg);
                break;
            case 7:
                galrequest(requestCode, resultCode, data, advimg);
                break;
        }


    }

    public void imgclick(View v) {
        if (v.getId() == R.id.chapterlogo) {
            clickImage = 1;
        } else if (v.getId() == R.id.presimg) {
            clickImage = 2;
        } else if (v.getId() == R.id.vpimg) {
            clickImage = 3;
        } else if (v.getId() == R.id.secimg) {
            clickImage = 4;
        } else if (v.getId() == R.id.tresimg) {
            clickImage = 5;
        } else if (v.getId() == R.id.pubrelimg) {
            clickImage = 6;
        } else if (v.getId() == R.id.advimg) {
            clickImage = 7;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLEY_REQUEST);
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


}
