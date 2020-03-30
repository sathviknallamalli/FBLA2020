package olyapps.sathv.fbla2020.util;

import android.content.Context;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;

/**
 * Created by sathv on 6/9/2018.
 */

public class InternetConnection {
    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(@NonNull Context context) {
        return  ((ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
