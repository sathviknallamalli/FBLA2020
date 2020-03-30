package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by sathv on 6/1/2018.
 */

public class ViewPageConversation extends Fragment {

    public ViewPageConversation() {

    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.viewpageconversation, container, false);
        //set the title of the screen

        final String uid = UserDetails.opuid;

        Button messagebutton = view.findViewById(R.id.streambutton);
        messagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(view.getContext(), ChatActivity.class);

                UserDetails.chatuid = uid;
                UserDetails.fullname = OtherProfile.namewithspace;

                int delete = spacechar(OtherProfile.namewithspace);
                UserDetails.chatWith = OtherProfile.namewithspace.replace(OtherProfile.namewithspace.charAt(delete) + "", "");

                Members.isgroupcreated = false;
                Members.isthisagroup = false;

                startActivity(i);

                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().addToBackStack(ViewPageConversation.class.getName()).commit();
                fm.executePendingTransactions();

            }
        });


       return view;
    }

    public int spacechar(String input) {
        for (int index = 0; index < input.length(); index++) {
            if (Character.isWhitespace(input.charAt(index))) {
                return index;
            }
        }
        return -1;
    }


}