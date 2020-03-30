package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

/**
 * Created by sathv on 6/1/2018.
 */

public class ViewPageStream extends Fragment {

    public ViewPageStream() {

    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.viewpagestream, container, false);
        //set the title of the screen

        Button messagebutton = view.findViewById(R.id.streambutton);
        messagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), MainActivity.class);
                startActivity(i);
            }
        });


       return view;
    }




}