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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**
 * Created by sathv on 6/1/2018.
 */

public class PrivacyPolicy extends Fragment {

    public PrivacyPolicy() {

    }

    WebView webView;
    View view;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.privacypolicy, container, false);
        //set the title of the screen
        getActivity().setTitle("Privacy Policy");
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();

        String flow = "https://myfbla-0.flycricket.io/privacy.html";
        webView = view.findViewById(R.id.pripol);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(flow);



        return view;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout){

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
           // finish();
        } if (item.getItemId() == R.id.item_userprofile) {
            Intent newintent = new Intent(view.getContext(), Profile.class);
            startActivity(newintent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fblahome, menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
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