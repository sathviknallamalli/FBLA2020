package olyapps.sathv.fbla2020;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sathv on 6/4/2018.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {
    Context context;
    ArrayList<Comment> arraylistcheckedbooks = null;

    //checked books adapter contructor
    public CommentAdapter(Context context, int resource, ArrayList<Comment> arraylistcheckedbooks) {
        super(context, resource, arraylistcheckedbooks);
        this.context = context;
        this.arraylistcheckedbooks = arraylistcheckedbooks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Comment checkedBook = arraylistcheckedbooks.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_single_layout, parent, false);
        }

        //retrieve the fields
        final CircleImageView civ = convertView.findViewById(R.id.comment_profile_layout);
        final TextView textView = (TextView) convertView.findViewById(R.id.comment_text_layout);
        TextView timecomment = convertView.findViewById(R.id.comment_time);


        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime = Long.parseLong(checkedBook.timestemp);
        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, context);
        timecomment.setText(lastSeenTime);

        TextView comment_reply = convertView.findViewById(R.id.comment_reply);
        comment_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(checkedBook.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profpic").getValue().toString().equals("nocustomimage")) {
                    Glide.with(context).load(R.drawable.defaultimg).into(civ);
                } else {
                    String uri = dataSnapshot.child("profpic").getValue().toString();
                    Glide.with(context).load(uri).into(civ);
                }


                String full = dataSnapshot.child("fname").getValue().toString() + " " + dataSnapshot.child("lname").getValue().toString();

                String sourceString = "<b>" + full + "</b> " + "  " + checkedBook.text;
                textView.setText(Html.fromHtml(sourceString));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return convertView;
    }
}
