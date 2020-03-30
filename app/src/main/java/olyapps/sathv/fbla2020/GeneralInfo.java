package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.content.Intent;
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

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.core.content.ContextCompat;

/**
 * Created by sathv on 6/1/2018.
 */

public class GeneralInfo extends Fragment {

    public GeneralInfo() {

    }

    WebView webView;
    FirebaseAuth mAuth;

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


        return view;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");
            dr.child(mAuth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(view.getContext(), LockScreen.class);
            startActivity(i);
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