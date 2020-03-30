package olyapps.sathv.fbla2020;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by sathv on 7/12/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
    }
}
