package olyapps.sathv.fbla2020;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sathv on 6/4/2018.
 */

public class AnApprovalAdapter extends ArrayAdapter<AnApproval> {
    Context context;
    ArrayList<AnApproval> arraylistcheckedbooks = null;

    //checked books adapter contructor
    public AnApprovalAdapter(Context context, int resource, ArrayList<AnApproval> arraylistcheckedbooks) {
        super(context, resource, arraylistcheckedbooks);
        this.context = context;
        this.arraylistcheckedbooks = arraylistcheckedbooks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AnApproval checkedBook = arraylistcheckedbooks.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.perapproval, parent, false);
        }

        //retrieve the fields
        TextView bookTitle = (TextView) convertView.findViewById(R.id.appname);
        TextView dateinchecked = (TextView) convertView.findViewById(R.id.appinfo);

        //set the appropriate fields with the appropriate info
        bookTitle.setText(checkedBook.approvalevent);
        dateinchecked.setText(checkedBook.approvalinfo);

        return convertView;
    }
}
