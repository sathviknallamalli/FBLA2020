package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sathv on 6/1/2018.
 */

public class Club extends Fragment {

    public Club() {

    }


    FirebaseAuth mAuth;

    View view;
    WebView webView;
    VideoView videoview;
    MediaController mc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.theclub, container, false);
        //set the title of the screen
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("The Club");

       /* webView = view.findViewById(R.id.player_view);
        String url = "https://www.youtube.com/watch?v=A-jvd0ye2ms&pbjreload=10";

        webView = view.findViewById(R.id.dresscode);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);*/


        videoview = view.findViewById(R.id.player_view);
        String videopath = "rtsp://r5---sn-a5mekn7r.googlevideo.com/Cj0LENy73wIaNAlr2p5Md-_oAxMYDSANFC3hYIdbMOCoAUIASARg2qiBmffHvaBYigELOXY2MlBRT0pmeEkM/362B33281D6AA996CC53E6864A33CB30A4466641.0DD6072AB4E6689C10FC79B67DF79CB2D332F602/yt6/1/video.3gp";
        Uri uri = Uri.parse(videopath);
        videoview.setVideoURI(uri);
        mc = new MediaController(view.getContext());
        videoview.setMediaController(mc);
        mc.setAnchorView(videoview);
       // videoview.requestFocus();
      //  videoview.start();

        Button ourinfo = view.findViewById(R.id.ourinfo);
        ourinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, new OurInformation()).commit();
            }
        });




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