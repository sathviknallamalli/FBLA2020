package olyapps.sathv.fbla2020;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.bumptech.glide.request.target.ViewTarget;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by sathv on 6/1/2018.
 */

public class FireApp extends Application {
    FirebaseAuth mAuth;
    DatabaseReference mUserDatabase;

    String chapterid,role;

    @Override
    public void onCreate() {
        super.onCreate();

        ViewTarget.setTagId(R.id.glide_tag);
        Firebase.setAndroidContext(this);

       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        if (!isNetworkConnected()) {
            Toast.makeText(this, "You are not connected to the Internet",
                    Toast.LENGTH_SHORT).show();
            System.exit(0);
        }




        mAuth = FirebaseAuth.getInstance();





    }
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null; // return true =(connected),false=(not connected)
    }
}