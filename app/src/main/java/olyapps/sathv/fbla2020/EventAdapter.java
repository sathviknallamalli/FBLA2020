package olyapps.sathv.fbla2020;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sathv on 6/4/2018.
 */

public class EventAdapter extends ArrayAdapter<Events> {
    Context context;
    ArrayList<Events> arraylistcheckedbooks = null;

    //checked books adapter contructor
    public EventAdapter(Context context, int resource, ArrayList<Events> arraylistcheckedbooks) {
        super(context, resource, arraylistcheckedbooks);
        this.context = context;
        this.arraylistcheckedbooks = arraylistcheckedbooks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Events checkedBook = arraylistcheckedbooks.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        }

        //retrieve the fields
        TextView bookTitle = (TextView) convertView.findViewById(R.id.event_title);
        TextView dateinchecked = (TextView) convertView.findViewById(R.id.event_category);
        TextView bookImage = (TextView) convertView.findViewById(R.id.event_type);

        if(checkedBook.eventname.contains("9th & 10th Grade Events")){
           bookTitle.setTextColor(Color.parseColor("#000080"));
        }

        //set the appropriate fields with the appropriate info
        bookTitle.setText(checkedBook.eventname);
        dateinchecked.setText(checkedBook.eventcategory);
        bookImage.setText(checkedBook.eventtype);


        return convertView;
    }
}
