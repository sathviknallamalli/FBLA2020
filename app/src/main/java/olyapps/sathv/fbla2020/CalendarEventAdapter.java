package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Created by sathv on 6/4/2018.
 */

public class CalendarEventAdapter extends ArrayAdapter<CalendarEvent> {
    Context context;
    ArrayList<CalendarEvent> arraylistcheckedbooks = null;

    //checked books adapter contructor
    public CalendarEventAdapter(Context context, int resource, ArrayList<CalendarEvent> arraylistcheckedbooks) {
        super(context, resource, arraylistcheckedbooks);
        this.context = context;
        this.arraylistcheckedbooks = arraylistcheckedbooks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CalendarEvent checkedBook = arraylistcheckedbooks.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.percalevent, parent, false);
        }


        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String role = sp.getString(context.getString(R.string.role), "role");

        //retrieve the fields
        TextView bookTitle = (TextView) convertView.findViewById(R.id.caleventdate);
        TextView dateinchecked = (TextView) convertView.findViewById(R.id.caleventname);
        Button sendrmeinder = convertView.findViewById(R.id.sendreminder);
        sendrmeinder.setTag(position);

        if(role.equals("Officer") || role.equals("Advisor")){
            sendrmeinder.setVisibility(View.VISIBLE);
        }else{
            sendrmeinder.setVisibility(View.INVISIBLE);
        }

        sendrmeinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                DatabaseReference mLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
                mLikes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String all = dataSnapshot.child("AllDeviceTokens").getValue().toString();

                        databaseReference.child("notificationsReminder").child("3ff290a3-f1d0-460a-b8ac-24a7d370bf48")
                                .child("ReminderInfo").setValue(all + "SEPERATOR" + checkedBook.caltitle
                        + "SEPERATOR" + checkedBook.caldate);

                        Toast.makeText(context, "Reminder sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        //set the appropriate fields with the appropriate info
        bookTitle.setText(checkedBook.caldate);
        dateinchecked.setText(checkedBook.caltitle);

        return convertView;
    }
}
