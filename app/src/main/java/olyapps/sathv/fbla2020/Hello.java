package olyapps.sathv.fbla2020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class Hello extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        ViewSwitcher vshello = findViewById(R.id.vshello);
        AvatarView av = findViewById(R.id.profpichello);
        CircleImageView cv = findViewById(R.id.profile_imagehello);

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String uri = sp.getString(getString(R.string.profpic), "profpic");
        String fullname = (sp.getString(getString(R.string.fname), "fname") + " " + sp.getString(getString(R.string.lname), "lname"));

        mAuth = FirebaseAuth.getInstance();


        if (uri.equals("nocustomimage")) {
            av.bind(fullname, null);
        } else {
            vshello.showNext();
            Glide.with(getApplicationContext()).load(uri).into(cv);
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    //1.5 seconds
                    sleep(1500);
                    //then start the next activity
                    Intent intent = new Intent(getApplicationContext(), FBLAHome.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

}
