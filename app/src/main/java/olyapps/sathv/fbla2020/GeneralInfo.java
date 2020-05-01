package olyapps.sathv.fbla2020;

import android.app.Fragment;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sathv on 6/1/2018.
 */

public class GeneralInfo extends Fragment {

    public GeneralInfo() {

    }

    WebView webView;
    FirebaseAuth mAuth;

    TextView chaptertitle, datestv;

    ImageView iv5;

    CircleImageView chapterlogo;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.general_info, container, false);
        //set the title of the screen
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("General Info");
        getActivity().setTitleColor(ContextCompat.getColor(view.getContext(), R.color.orange));


        String url = "http://docs.google.com/gview?embedded=true&url=http://www.fbla-pbl.org/media/New-National-Dress-Code.pdf";

        webView = view.findViewById(R.id.dresscode);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        iv5 = view.findViewById(R.id.imageView5);
        iv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //practice toast


            }
        });
        chaptertitle = view.findViewById(R.id.chaptertitle);
        datestv = view.findViewById(R.id.datestv);
        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").
                child(spchap.getString("chapterID", "tempid"));
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chaptertitle.setText(dataSnapshot.child("Setup").child("ChapterName").getValue().toString() + " FBLA");

                datestv.setText("National Leadership Conference:\n\n2020 NLC - Salt Lake City, Utah" +
                        "\n\nFBLA: June 29 - July 2\n\nState Business Leadership Conference" +
                        "\n\n" + dataSnapshot.child("Setup").child("StateConfDate").getValue().toString()
                        + "\n\nFall Conference:\n\n" + dataSnapshot.child("Setup").child("FallConfDate").getValue().toString() +
                        "\n\nWinter Conference: \n\n" + dataSnapshot.child("Setup").child("WinterConfDate").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        chapterlogo = view.findViewById(R.id.chapterlogo);
        Glide.with(view.getContext()).load(spchap.getString("chapterlogo", "tempuri")).into(chapterlogo);

        return view;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {

           /* DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");
            dr.child(mAuth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);*/


            SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
            final String chapid = spchap.getString("chapterID", "tempid");

            SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            final String role = sp.getString(getString(R.string.role), "role");
            DatabaseReference dr;

            if(role.equals("Adviser")){
                dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                        .child("Advisers");
            }else{
                dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                        .child("Users");
            }

            dr.child(mAuth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(view.getContext(), LockScreen.class);
            startActivity(i);
        //    finish();
        }
        if (item.getItemId() == R.id.item_userprofile) {
            Intent newintent = new Intent(view.getContext(), Profile.class);
            startActivity(newintent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.fblahome, menu);


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


}