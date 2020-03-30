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
import olyapps.sathv.fbla2020.adapter.StateAdapter;
import olyapps.sathv.fbla2020.model.StateDataModel;
import olyapps.sathv.fbla2020.parser.StateParser;
import olyapps.sathv.fbla2020.util.InternetConnection;
import olyapps.sathv.fbla2020.util.StateKeys;

/**
 * Created by sathv on 6/1/2018.
 */

public class State extends Fragment {

    public State() {

    }

    ListView listView;
    ArrayList<StateDataModel> list;
    StateAdapter adapter;
    View view;

    static String lname;
    static String fname;
    static String female;
    static String male;
    static String permission;
    static String nurse;
    static String balance;
    static String nonrefund;
    static String finalpay;
    static String sponsormon;
    static String eventuno;
    static String eventdos;
    static String eventtres;
    static String eventcuatro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.statecon, container, false);
        //set the title of the screen
        /**
         * Array List for Binding Data from JSON to this List
         */

        list = new ArrayList<>();

        adapter = new StateAdapter(view.getContext(), list);


        listView = view.findViewById(R.id.listviewinstate);
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
                female = list.get(position).getFemale();
                male = list.get(position).getMale();
                permission = list.get(position).getStatepermission();
                nurse = list.get(position).getNurse();
                balance = list.get(position).getBalance();
                nonrefund = list.get(position).getNonrefund();
                finalpay = list.get(position).getFinalpayment();
                sponsormon = list.get(position).getSponsormoney();
                eventuno = list.get(position).getEventuno();
                eventdos = list.get(position).getEventdos();
                eventtres = list.get(position).getEventtres();
                eventcuatro = list.get(position).getEventcuatro();

                if (female.equals("1")) {
                    female = "Female";
                    male = "";
                }
                if (male.equals("1")) {
                    male = "Male";
                    female = "";
                }
                if (permission.equals("x")) {
                    permission = "Yes";
                } else {
                    permission = "No";
                }
                if (nurse.equals("x")) {
                    nurse = "Yes";
                } else {
                    nurse = "No";
                }
                if (balance.equals("")) {
                    balance = "Unavailable";
                }
                if (nonrefund.equals("")) {
                    nonrefund = "Unavailable";
                }
                if (finalpay.equals("")) {
                    finalpay = "Unavailable";
                }
                if (sponsormon.equals("")) {
                    sponsormon = "Unavailable";
                }
                if (eventuno.equals("")) {
                    eventuno = "Unavailable";
                }
                if (eventdos.equals("")) {
                    eventdos = "Unavailable";
                }
                if (eventtres.equals("")) {
                    eventtres = "Unavailable";
                }
                if (eventcuatro.equals("")) {
                    eventcuatro = "Unavailable";
                }

                Intent intent = new Intent(view.getContext(), StateDetails.class);
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
            JSONObject jsonObject = StateParser.getDataFromWeb();

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
                        JSONArray array = jsonObject.getJSONArray(StateKeys.KEY_CONTACTS);

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
                                StateDataModel model = new StateDataModel();

                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String firstname = innerObject.getString(StateKeys.KEY_FIRSTNAME);
                                String lastname = innerObject.getString(StateKeys.KEY_LASTNAME);
                                String femalestr = innerObject.getString(StateKeys.KEY_FEMALE);
                                String malestr = innerObject.getString(StateKeys.KEY_MALE);
                                String permissionstr = innerObject.getString(StateKeys.KEY_SPERMISSION);
                                String nursestr = innerObject.getString(StateKeys.KEY_NURSE);
                                String balancestr = innerObject.getString(StateKeys.KEY_BALANCE);
                                String nonrefundstr = innerObject.getString(StateKeys.KEY_NONREFUND);
                                String finalpaystr = innerObject.getString(StateKeys.KEY_FINALPAYMENT);
                                String sponsormonstr = innerObject.getString(StateKeys.KEY_SPONSORMONEY);
                                String eventunostr = innerObject.getString(StateKeys.KEY_EVENT1);
                                String eventdosstr = innerObject.getString(StateKeys.KEY_EVENT2);
                                String eventtresstr = innerObject.getString(StateKeys.KEY_EVENT3);
                                String eventcuatrostr = innerObject.getString(StateKeys.KEY_EVENT4);

                                /**
                                 * Getting Object from Object "phone"
                                 */
                                //JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
                                //String phone = phoneObject.getString(Keys.KEY_MOBILE);

                                model.setFirstname(firstname);
                                model.setLastname(lastname);
                                model.setFemale(femalestr);
                                model.setMale(malestr);
                                model.setStatepermission(permissionstr);
                                model.setNurse(nursestr);
                                model.setBalance(balancestr);
                                model.setNonrefund(nonrefundstr);
                                model.setFinalpayment(finalpaystr);
                                model.setSponsormoney(sponsormonstr);
                                model.setEventuno(eventunostr);
                                model.setEventdos(eventdosstr);
                                model.setEventtres(eventtresstr);
                                model.setEventcuatro(eventcuatrostr);

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