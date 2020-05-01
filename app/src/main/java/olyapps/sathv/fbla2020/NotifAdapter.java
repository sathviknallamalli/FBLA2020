package olyapps.sathv.fbla2020;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

/**
 * Created by sathv on 6/4/2018.
 */

public class NotifAdapter extends ArrayAdapter<Notif> {
    Context context;
    ArrayList<Notif> notifsArrayList = null;

    //checked books adapter contructor
    public NotifAdapter(Context context, int resource, ArrayList<Notif> notifsArrayList) {
        super(context, resource, notifsArrayList);
        this.context = context;
        this.notifsArrayList = notifsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notif anotif = notifsArrayList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notif_item, parent, false);
        }

        //retrieve the fields
        TextView notiftitle = (TextView) convertView.findViewById(R.id.notiftitle);
        TextView notifmessage = (TextView) convertView.findViewById(R.id.notifmessage);
        TextView notiftimestamp = (TextView) convertView.findViewById(R.id.notiftimestamp);
        ImageView notificon = convertView.findViewById(R.id.notificon);



        //set the appropriate fields with the appropriate info
        notiftitle.setText(anotif.title);
        notifmessage.setText(anotif.message);
        notiftimestamp.setText(anotif.timestamp);

        if(anotif.title.equals("New Post")){
            notificon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.streamactive));
        } else if(anotif.title.equals("New Member")){
            notificon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.primaryuser));
        }else if(anotif.title.equals("Chapter Meeting")){
            notificon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_group));
        }else{
            notificon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notif));
        }




        return convertView;
    }
}
