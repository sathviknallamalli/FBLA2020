package olyapps.sathv.fbla2020;

import java.util.ArrayList;

public interface MyCallback {
    void onCallback(Boolean isExists, String who);
    void callbackGroups(ArrayList<String> groups);

}
