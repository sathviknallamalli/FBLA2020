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


/**
 * Created by sathv on 6/1/2018.
 */

public class Welcome extends Fragment {

    public Welcome() {

    }

    WebView webView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.welcome, container, false);
        //set the title of the screen
        getActivity().setTitle("About FBLA");

        String url = "https://www.fbla-pbl.org/";


        webView = view.findViewById(R.id.webviewwelcome);
        webView.setWebViewClient(new myWebclient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        return view;
    }

    public class myWebclient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
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