package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by sathv on 6/1/2018.
 */

public class SocialMedia extends Fragment {

    public SocialMedia() {

    }

    WebView fwebView;

    ProgressBar pb;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.social_media, container, false);
        //set the title of the screen
        getActivity().setTitle("Social Media");

        String furl = "https://www.instagram.com/olyfbla/";

        pb = view.findViewById(R.id.pb);

        fwebView = view.findViewById(R.id.facebookpage);
        fwebView.setWebViewClient(new myWebclient());
        fwebView.getSettings().setJavaScriptEnabled(true);
        fwebView.loadUrl(furl);
        return view;
    }

    public class myWebclient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pb.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            fwebView.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
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