package olyapps.sathv.fbla2020;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;

public class FullImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Intent intent = getIntent();
        String notename = intent.getExtras().getString("imageuri");

        ImageView iv = findViewById(R.id.fullimage);
        Glide.with(getApplicationContext()).load(notename).into(iv);

        ImageView backfull = findViewById(R.id.backfull);
        backfull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newintent = new Intent(FullImage.this, ChatActivity.class);
                startActivity(newintent);
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent newintent = new Intent(FullImage.this, ChatActivity.class);
        startActivity(newintent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
