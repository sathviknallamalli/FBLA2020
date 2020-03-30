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
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import olyapps.sathv.fbla2020.adapter.MyArrayAdapter;
import olyapps.sathv.fbla2020.model.MyDataModel;
import olyapps.sathv.fbla2020.parser.JSONParser;
import olyapps.sathv.fbla2020.util.InternetConnection;
import olyapps.sathv.fbla2020.util.Keys;

/**
 * Created by sathv on 6/1/2018.
 */

public class Budget extends Fragment {

    public Budget() {

    }

    ListView listView;
    ArrayList<MyDataModel> list;
    MyArrayAdapter adapter;
    View view;

    static String lname;
    static String fname;
    static String gradyear;
    static String shirtsize;
    static String clubdue;
    static String fallconference;
    static String winterconference;
    static String winterpermission;

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.budget, container, false);
        //set the title of the screen
        /**
         * Array List for Binding Data from JSON to this List
         */
        list = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        adapter = new MyArrayAdapter(view.getContext(), list);

        listView = view.findViewById(R.id.listViewinbudget);
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
                gradyear = list.get(position).getGraduation();
                shirtsize = list.get(position).getShirtsize();
                clubdue = list.get(position).getClubdues();
                fallconference = list.get(position).getFallc();
                winterconference = list.get(position).getWinterc();
                winterpermission = list.get(position).getWinterpermission();

                if (winterpermission.equals("X")) {
                    winterpermission = "Yes";
                } else {
                    winterpermission = "No";
                }


                if (fallconference.equals("") || fallconference.isEmpty()) {
                    fallconference = "Not Available";
                }
                if (clubdue.equals("") || clubdue.isEmpty()) {
                    clubdue = "Not Available";
                }
                if (winterconference.equals("") || winterconference.isEmpty()) {
                    winterconference = "Not Available";
                }

                Intent intent = new Intent(view.getContext(), PaymentInformation.class);
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
            JSONObject jsonObject = JSONParser.getDataFromWeb();

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
                        JSONArray array = jsonObject.getJSONArray(Keys.KEY_CONTACTS);


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
                                MyDataModel model = new MyDataModel();

                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String firstname = innerObject.getString(Keys.KEY_FIRSTNAME);
                                String lastname = innerObject.getString(Keys.KEY_LASTNAME);
                                String graduation = innerObject.getString(Keys.KEY_GRADYEAR);
                                String shirtsize = innerObject.getString(Keys.KEY_SHIRTSIZE);
                                String clubdues = innerObject.getString(Keys.KEY_CLUBDUE);
                                String fallc = innerObject.getString(Keys.KEY_FALLC);
                                String winterc = innerObject.getString(Keys.KEY_WINTERC);
                                String winterp = innerObject.getString(Keys.KEY_WINTERP);

                                /**
                                 * Getting Object from Object "phone"
                                 */
                                //JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
                                //String phone = phoneObject.getString(Keys.KEY_MOBILE);

                                model.setFirstname(firstname);
                                model.setLastname(lastname);
                                model.setGraduation(graduation);
                                model.setShirtsize(shirtsize);
                                model.setClubdues(clubdues);
                                model.setFallc(fallc);
                                model.setWinterc(winterc);
                                model.setWinterpermission(winterp);
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