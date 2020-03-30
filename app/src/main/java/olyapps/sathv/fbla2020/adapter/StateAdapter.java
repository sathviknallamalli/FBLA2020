package olyapps.sathv.fbla2020.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import olyapps.sathv.fbla2020.model.StateDataModel;

/**
 * Created by sathv on 6/9/2018.
 */

public class StateAdapter extends ArrayAdapter<StateDataModel> {

    List<StateDataModel> modelList;
    private LayoutInflater mInflater;

    // Constructors
    public StateAdapter(Context context, List<StateDataModel> objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        modelList = objects;
    }

    @Override
    public StateDataModel getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(olyapps.sathv.fbla2020.R.layout.layout_row_view, parent, false);
            vh = ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        StateDataModel item = getItem(position);

        vh.lastName.setText(item.getLastname());
        vh.firstName.setText(item.getFirstname());

        return vh.rootView;
    }

    /**
     * ViewHolder class for layout.<br />
     * <br />
     * Auto-created on 2016-01-05 00:50:26 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private static class ViewHolder {
        public final RelativeLayout rootView;

        public final TextView lastName;
        public final TextView firstName;

        private ViewHolder(RelativeLayout rootView, TextView lastName, TextView firstName) {
            this.rootView = rootView;
            this.lastName = lastName;
            this.firstName = firstName;
        }

        public static ViewHolder create(RelativeLayout rootView) {
            TextView lastName = (TextView) rootView.findViewById(olyapps.sathv.fbla2020.R.id.lastNamebud);
            TextView firstName = (TextView) rootView.findViewById(olyapps.sathv.fbla2020.R.id.firstNamebud);
            return new ViewHolder(rootView, lastName, firstName);
        }
    }
}
