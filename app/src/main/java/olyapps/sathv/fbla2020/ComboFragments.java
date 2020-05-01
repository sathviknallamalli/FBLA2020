package olyapps.sathv.fbla2020;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by sathv on 6/1/2018.
 */


public class ComboFragments extends Fragment {

    public ComboFragments() {

    }


    View view;
    TabLayout tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.combofragments, container, false);
        //set the title of the screen


        setHasOptionsMenu(true);

        tabLayout = (TabLayout) view.findViewById(R.id.result_tabs);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpagerresult);
        ViewPageAdapter adapter = new ViewPageAdapter(getChildFragmentManager());
        adapter.addfragment(new MessagesInbox(), "Messages Inbox");
        adapter.addfragment(new ChapterMembers(), "Members");


        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);


        if (tabLayout.getSelectedTabPosition() == 0) {
            getActivity().setTitle("Inbox");
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            getActivity().setTitle("Chapter Members");
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    getActivity().setTitle("Inbox");
                } else if (position == 1) {
                    getActivity().setTitle("Chapter Members");
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }

}