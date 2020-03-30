package olyapps.sathv.fbla2020;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ANote extends AppCompatActivity {

    EditText notetext;
    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anote);
        setTitle("New Note");
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notetext = findViewById(R.id.notetext);
        notetext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        notetext.setSingleLine(false);
        notetext.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        mauth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        final String notename = intent.getExtras().getString("notename");

        DatabaseReference getnote = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mauth.getCurrentUser().getUid());

        getnote.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(notename).exists()) {
                    String text = dataSnapshot.child(notename).getValue().toString();
                    notetext.setText(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sharenote) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String sharebody = "Here is the note I wrote up: \n" +
                    notetext.getText().toString();
            String sharesub = "Take a look at this note!";
            myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
            myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
            startActivity(Intent.createChooser(myIntent, "Share note with"));
        } else if (item.getItemId() == R.id.deletenote) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you really want delete this note");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    DatabaseReference fdr = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
                    Intent intent = getIntent();
                    String notename = intent.getExtras().getString("notename");
                    fdr.child(notename).removeValue();

                    getFragmentManager().popBackStackImmediate();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();

        } else if (item.getItemId() == R.id.savenote) {
            DatabaseReference fdr = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
            Intent intent = getIntent();
            String notename = intent.getExtras().getString("notename");

            fdr.child(notename).setValue(notetext.getText().toString());

            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == android.R.id.home) {
            // finish();

            getFragmentManager().popBackStackImmediate();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        getFragmentManager().popBackStackImmediate();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

}
