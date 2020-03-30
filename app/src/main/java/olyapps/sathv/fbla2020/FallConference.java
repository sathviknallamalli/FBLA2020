package olyapps.sathv.fbla2020;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import olyapps.sathv.fbla2020.adapter.FallConfAdapter;
import olyapps.sathv.fbla2020.model.FallConfDataModel;
import olyapps.sathv.fbla2020.parser.FallConferenceParser;
import olyapps.sathv.fbla2020.util.FallConferenceKeys;
import olyapps.sathv.fbla2020.util.InternetConnection;


/**
 * Created by sathv on 6/1/2018.
 */

public class FallConference extends Fragment {

    public FallConference() {

    }

    ListView listView;
    ArrayList<FallConfDataModel> list;
    FallConfAdapter adapter;
    View view;

    static String lname;
    static String fname;
    static String gradyear;
    static String fallcnf;
    static String fallperm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.fallc, container, false);
        //set the title of the screen
        /**
         * Array List for Binding Data from JSON to this List
         */

        list = new ArrayList<>();

        adapter = new FallConfAdapter(view.getContext(), list);



        listView = view.findViewById(R.id.listviewinfallc);
        listView.setAdapter(adapter);

        if (InternetConnection.checkConnection(view.getContext())) {
            new GetDataTask().execute();
        } else {
            Snackbar.make(view, "Internet Connection Not Available", Snackbar.LENGTH_LONG).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lname = list.get(position).getLastname();
                fname = list.get(position).getFirstname();
                gradyear = list.get(position).getGradyear();
                fallcnf = list.get(position).getFallconf();
                fallperm = list.get(position).getFallpermi();

                if(fallperm.equals("x")){
                    fallperm = "x : Yes";
                }else{
                    fallperm = "No";
                }

                Intent intent = new Intent(view.getContext(), FallDetails.class);
                startActivity(intent);
            }
        });


        return view;
    }

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int jIndex;
        int x;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */

            x = list.size();

            if (x == 0)
                jIndex = 0;
            else
                jIndex = x;

            dialog = new ProgressDialog(view.getContext());
            dialog.setTitle("Hey Wait Please...");
            dialog.setMessage("Loading data...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            /**
             * Getting JSON Object from Web Using okHttp
             */
            JSONObject jsonObject = FallConferenceParser.getDataFromWeb();

            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonObject != null) {
                    /**
                     * Check Length...
                     */
                    if (jsonObject.length() > 0) {
                        /**
                         * Getting Array named "contacts" From MAIN Json Object
                         */
                        JSONArray array = jsonObject.getJSONArray(FallConferenceKeys.KEY_CONTACTS);

                        /**
                         * Check Length of Array...
                         */


                        int lenArray = array.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {

                                /**
                                 * Creating Every time New Object
                                 * and
                                 * Adding into List
                                 */
                                FallConfDataModel model = new FallConfDataModel();

                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String firstname = innerObject.getString(FallConferenceKeys.KEY_FIRSTNAME);
                                String lastname = innerObject.getString(FallConferenceKeys.KEY_LASTNAME);
                                String graduation = innerObject.getString(FallConferenceKeys.KEY_GRADYEAR);
                                String fallconf = innerObject.getString(FallConferenceKeys.KEY_FALLCONFERENCE);
                                String fallperm = innerObject.getString(FallConferenceKeys.KEY_FALLPERMISSION);

                                /**
                                 * Getting Object from Object "phone"
                                 */
                                //JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
                                //String phone = phoneObject.getString(Keys.KEY_MOBILE);

                                model.setFirstname(firstname);
                                model.setLastname(lastname);
                                model.setGradyear(graduation);
                                model.setFallconf(fallconf);
                                model.setFallpermi(fallperm);
                                /**
                                 * Adding yourname and phone concatenation in List...
                                 */
                                list.add(model);
                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            if (list.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Snackbar.make(view.findViewById(R.id.parentLayout), "Currently, no Member data is loaded", Snackbar.LENGTH_LONG).show();
            }
        }
    }

}