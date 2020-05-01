package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

/**
 * Created by sathv on 6/1/2018.
 */

public class Officers extends Fragment {

    public Officers() {

    }

    View view;

    String chapterid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.officerteam, container, false);
        //set the title of the screen
        getActivity().setTitle("Chapter Officers");
        setHasOptionsMenu(true);

        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        final ImageView pres = view.findViewById(R.id.pres);
        final ImageView vicepres = view.findViewById(R.id.vicepres);
        final ImageView tres = view.findViewById(R.id.tres);
        final ImageView sec = view.findViewById(R.id.sec);
        final ImageView pubrel = view.findViewById(R.id.pubrel);
        final ImageView advi = view.findViewById(R.id.advi);

        final TextView namepres  = view.findViewById(R.id.namepres);
        final TextView namevp  = view.findViewById(R.id.namevp);
        final TextView nametres  = view.findViewById(R.id.nametres);
        final TextView namesec  = view.findViewById(R.id.namesec);
        final TextView namepubrel  = view.findViewById(R.id.namepubrel);
        final TextView nameadvi  = view.findViewById(R.id.nameadvi);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(view.getContext()).load(dataSnapshot.child("Images").child("AdviserImg").getValue().toString()).into(advi);
                Glide.with(view.getContext()).load(dataSnapshot.child("Images").child("PresidentImg").getValue().toString()).into(pres);
                Glide.with(view.getContext()).load(dataSnapshot.child("Images").child("VicePresidentImg").getValue().toString()).into(vicepres);
                Glide.with(view.getContext()).load(dataSnapshot.child("Images").child("TreasurerImg").getValue().toString()).into(tres);
                Glide.with(view.getContext()).load(dataSnapshot.child("Images").child("SecretaryImg").getValue().toString()).into(sec);
                Glide.with(view.getContext()).load(dataSnapshot.child("Images").child("PublicRelationsImg").getValue().toString()).into(pubrel);

                namepres.setText("President:\n" + dataSnapshot.child("ChapterOfficers").child("President").getValue().toString());
                namevp.setText("Vice President:\n" + dataSnapshot.child("ChapterOfficers").child("VicePresident").getValue().toString());
                nametres.setText("Treasurer:\n"+dataSnapshot.child("ChapterOfficers").child("Treasurer").getValue().toString());
                namesec.setText("Secretary:\n"+dataSnapshot.child("ChapterOfficers").child("Secretary").getValue().toString());
                namepubrel.setText("Public Relations:\n"+dataSnapshot.child("ChapterOfficers").child("PublicRelations").getValue().toString());
                nameadvi.setText("Adviser:\n"+dataSnapshot.child("ChapterOfficers").child("Adviser").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.officer_takenote) {
            //Launch take note activity and make it with corner x
            //save it with ifreb ase or intent extra

            Intent newintent = new Intent(view.getContext(), ANote.class);
            newintent.putExtra("notename", "aboutofficers");
            startActivity(newintent);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(Officers.class.getName()).commit();
            fm.executePendingTransactions();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.officerdetails, menu);


        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }
}
