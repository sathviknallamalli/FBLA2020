package olyapps.sathv.fbla2020;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by sathv on 6/10/2018.
 */

public class ViewPagerAdapter extends PagerAdapter {
    int[] images;
    Activity activity;
    LayoutInflater inflater;
    String[] descriptions;

    public ViewPagerAdapter(int[] images, Activity activity, String[] descriptions) {
        this.images = images;
        this.activity = activity;
        this.descriptions = descriptions;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemview = inflater.inflate(R.layout.viewpageritem, container, false);
        ImageView imageView;
        TextView description;

        imageView = itemview.findViewById(R.id.vimage);
        description = itemview.findViewById(R.id.description);
        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
        int height = dis.heightPixels;
        int width = dis.widthPixels;
        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);

        description.setText(descriptions[position]);

        try{
            Picasso.with(activity.getApplicationContext()).load(images[position]).placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher).into(imageView);
        }catch (Exception e){

        }

        container.addView(itemview);
        return itemview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((View) object);
    }
}
