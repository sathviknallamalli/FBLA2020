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
import olyapps.sathv.fbla2020.adapter.SponsorAdapter;
import olyapps.sathv.fbla2020.model.SponsorDataModel;
import olyapps.sathv.fbla2020.parser.SponsorParser;
import olyapps.sathv.fbla2020.util.InternetConnection;
import olyapps.sathv.fbla2020.util.SponsorsKeys;

/**
 * Created by sathv on 6/1/2018.
 */

public class Sponsors extends Fragment {

    public Sponsors() {

    }

    ListView listView;
    ArrayList<SponsorDataModel> list;
    SponsorAdapter adapter;
    View view;

    static String lname;
    static String fname;
    static String spon1, cn1, amt1;
    static String spon2, cn2, amt2;
    static String spon3, cn3, amt3;
    static String spon4, cn4, amt4;
    static String total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.sponsorslist, container, false);
        //set the title of the screen
        /**
         * Array List for Binding Data from JSON to this List
         */

        list = new ArrayList<>();

        adapter = new SponsorAdapter(view.getContext(), list);


        listView = view.findViewById(R.id.listviewinsponsor);
        listView.setAdapter(adapter);

        if (InternetConnection.checkConnection(view.getContext())) {
            new GetDataTask().execute();
        } else {
            Snackbar.make(view, "Internet Connection Not Available", Snackbar.LENGTH_LONG).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lname = list.get(position).getLast();
                fname = list.get(position).getFirst();
                spon1 = list.get(position).getSponsor1();
                cn1 = list.get(position).getCn1();
                amt1 = list.get(position).getAmt1();
                spon2 = list.get(position).getSponsor2();
                cn2 = list.get(position).getCn2();
                amt2 = list.get(position).getAmt2();
                spon3 = list.get(position).getSponsor3();
                cn3 = list.get(position).getCn3();
                amt3 = list.get(position).getAmt3();
                spon4 = list.get(position).getSponsor4();
                cn4 = list.get(position).getCn4();
                amt4 = list.get(position).getAmt4();
                total = list.get(position).getTotal();

                if (spon1.equals("")) {
                    spon1 = "Unavailable";
                }
                if (spon2.equals("")) {
                    spon2 = "Unavailable";
                }
                if (spon3.equals("")) {
                    spon3 = "Unavailable";
                }
                if (spon4.equals("")) {
                    spon4 = "Unavailable";
                }
                if (cn1.equals("")) {
                    cn1 = "Unavailable";
                }
                if (cn2.equals("")) {
                    cn2 = "Unavailable";
                }
                if (cn3.equals("")) {
                    cn3 = "Unavailable";
                }
                if (cn4.equals("")) {
                    cn4 = "Unavailable";
                }
                if (amt1.equals("")) {
                    amt1 = "Unavailable";
                }
                if (amt2.equals("")) {
                    amt2 = "Unavailable";
                }
                if (amt3.equals("")) {
                    amt3 = "Unavailable";
                }
                if (amt4.equals("")) {
                    amt4 = "Unavailable";
                }

                Intent intent = new Intent(view.getContext(), SponsorDetails.class);
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
            JSONObject jsonObject = SponsorParser.getDataFromWeb();

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
                        JSONArray array = jsonObject.getJSONArray(SponsorsKeys.KEY_CONTACTS);

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
                                SponsorDataModel model = new SponsorDataModel();

                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                String firstname = innerObject.getString(SponsorsKeys.KEY_FIRST);
                                String lastname = innerObject.getString(SponsorsKeys.KEY_LAST);
                                String spon1str = innerObject.getString(SponsorsKeys.KEY_SPONSOR1);
                                String spon2str = innerObject.getString(SponsorsKeys.KEY_SPONSOR2);
                                String spon3str = innerObject.getString(SponsorsKeys.KEY_SPONSOR3);
                                String spon4str = innerObject.getString(SponsorsKeys.KEY_SPONSOR4);
                                String cn1str = innerObject.getString(SponsorsKeys.KEY_CN1);
                                String cn2str = innerObject.getString(SponsorsKeys.KEY_CN2);
                                String cn3str = innerObject.getString(SponsorsKeys.KEY_CN3);
                                String cn4str = innerObject.getString(SponsorsKeys.KEY_CN4);
                                String am1str = innerObject.getString(SponsorsKeys.KEY_1AMT);
                                String am2str = innerObject.getString(SponsorsKeys.KEY_2AMT);
                                String am3str = innerObject.getString(SponsorsKeys.KEY_3AMT);
                                String am4str = innerObject.getString(SponsorsKeys.KEY_4AMT);
                                String totalstr = innerObject.getString(SponsorsKeys.KEY_TOTAL);

                                /**
                                 * Getting Object from Object "phone"
                                 */
                                //JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
                                //String phone = phoneObject.getString(Keys.KEY_MOBILE);

                                model.setFirst(firstname);
                                model.setLast(lastname);
                                model.setSponsor1(spon1str);
                                model.setSponsor2(spon2str);
                                model.setSponsor3(spon3str);
                                model.setSponsor4(spon4str);
                                model.setCn1(cn1str);
                                model.setCn2(cn2str);
                                model.setCn3(cn3str);
                                model.setCn4(cn4str);
                                model.setAmt1(am1str);
                                model.setAmt2(am2str);
                                model.setAmt3(am3str);
                                model.setAmt4(am4str);
                                model.setTotal(totalstr);

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