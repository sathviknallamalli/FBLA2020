package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ChooseEvent extends Fragment {

    public ChooseEvent() {

    }

    TextView thequestion;
    Button yes,no,results;

    int count = 0;
    View view;
    boolean q1, q2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.activity_choose_event, container, false);
        //set the title of the screen
        getActivity().setTitle("Choose an Event");

        thequestion = view.findViewById(R.id.question);
        no = view.findViewById(R.id.no);
        yes = view.findViewById(R.id.yes);
        results = view.findViewById(R.id.results);

        thequestion.setText("Do you feel comfortable speaking in front of a small audience?");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(thequestion.getText().equals("Do you feel comfortable speaking in front of a small audience?")){
                    thequestion.setText("Would you like to work on a project, presentation, or report that showcases your business or technical knowledge?");
                }else if(thequestion.getText().equals("Would you like to work on a project, presentation, or report that showcases your business or technical knowledge?")){
                    showresults("Chapter Events\n" +
                            "American Enterprise Project\n" +
                            "Community Service Project\n" +
                            "Local Chapter Annual Business Report\n" +
                            "Partnership with Business Project\n" +
                            "Individual Event\n" +
                            "Coding & Programming\n" +
                            "Individual or Team Events\n" +
                            "3-D Animation\n" +
                            "Broadcast Journalism\n" +
                            "Business Ethics\n" +
                            "Business Financial Plan\n" +
                            "Business Plan\n" +
                            "Computer Game & Simulation Programming\n" +
                            "Digital Video Production\n" +
                            "E-business\n" +
                            "Emerging Business Issues\n" +
                            "Graphic Design\n" +
                            "Mobile App Development\n" +
                            "Public Service Announcement\n" +
                            "Publication Design\n" +
                            "Social Media Campaign\n" +
                            "Website Design");
                } else if(thequestion.getText().equals("Would you like to work as part of a team?")){
                    showresults("Banking & Financial Systems\n" +
                            "Entrepreneurship\n" +
                            "Global Business\n" +
                            "Hospitality Management\n" +
                            "Management Decision Making\n" +
                            "Management Information Systems\n" +
                            "Marketing\n" +
                            "Network Design\n" +
                            "Parliamentary Procedure\n" +
                            "Sports & Entertainment Management");
                } else if(thequestion.getText().equals("Are you in 9th or 10th grade?")){
                    showresults("9th & 10th Grade Speaking\n" +
                            "or Presentation Event\n" +
                            "Introduction to Business Presentation\n" +
                            "Introduction to Public Speaking");
                }else if(thequestion.getText().equals("Do you use one or more pieces of office application software well (Microsoft Office—Word, Excel, PowerPoint, Access, etc.; OpenOffice—Writer, Calc, Impress)?")){
                    showresults("Try\n" +
                            "Computer Applications\n" +
                            "Database Design & Applications\n" +
                            "Spreadsheet Applications\n" +
                            "Word Processing");
                } else if(thequestion.getText().equals("Are you in 9th or 10th grade?")){
                    showresults("9th & 10th Grade Objective Test\n" +
                            "Events\n" +
                            "Introduction to FBLA\n" +
                            "Introduction to Business\n" +
                            "Introduction to Business Communication\n" +
                            "Introduction to Business Procedures\n" +
                            "Introduction to Financial Math\n" +
                            "Introduction to Information Technology\n" +
                            "Introduction to Parliamentary Procedure");
                }

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thequestion.getText().equals("Would you like to work on a project, presentation, or report that showcases your business or technical knowledge?")){
                    thequestion.setText("Would you like to work as part of a team?");
                }else if(thequestion.getText().equals("Would you like to work as part of a team?")){
                    thequestion.setText("Are you in 9th or 10th grade?");
                }else if(thequestion.getText().equals("Are you in 9th or 10th grade?")){
                    showresults("Speaking Events\n" +
                            "Impromptu Speaking\n" +
                            "Public Speaking\n" +
                            "Interview Events\n" +
                            "Future Business Leader (test also)\n" +
                            "Job Interview\n" +
                            "Interactive Role Play Events\n" +
                            "Client Service\n" +
                            "Help Desk (test also)\n" +
                            "YES\n" +
                            "YES\n" +
                            "YES\n" +
                            "Presentation Events\n" +
                            "Electronic Career Portfolio\n" +
                            "Sales Presentation");
                } else if(thequestion.getText().equals("Do you feel comfortable speaking in front of a small audience?")){
                    thequestion.setText("Do you use one or more pieces of office application software well (Microsoft Office—Word, Excel, PowerPoint, Access, etc.; OpenOffice—Writer, Calc, Impress)?");
                } else if(thequestion.getText().equals("Do you use one or more pieces of office application software well (Microsoft Office—Word, Excel, PowerPoint, Access, etc.; OpenOffice—Writer, Calc, Impress)?")){
                    thequestion.setText("Are you in 9th or 10th grade?");
                } else if(thequestion.getText().equals("Are you in 9th or 10th grade?")){
                    showresults("Objective test events open\n" +
                            "to any grade level covering\n" +
                            "a variety of topics.\n" +
                            "Accounting I\n" +
                            "Accounting II\n" +
                            "Advertising\n" +
                            "Agribusiness\n" +
                            "Business Calculations\n" +
                            "Business Communication\n" +
                            "Business Law\n" +
                            "Computer Problem Solving\n" +
                            "Cyber Security\n" +
                            "Economics\n" +
                            "Health Care Admininistration\n" +
                            "Insurance & Risk Management\n" +
                            "Journalism\n" +
                            "Networking Concepts\n" +
                            "Organizational Leadership\n" +
                            "Personal Finance\n" +
                            "Securities and Investments");
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }


    public void showresults(String events){
        thequestion.setText("The following events would be perfect for you to compete in: ");
        yes.setVisibility(View.INVISIBLE);
        no.setVisibility(View.INVISIBLE);
        results.setVisibility(View.VISIBLE);
        results.setText(events);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_share) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String sharesub = "Check out the choose event feature";
            String sharebody = "It shows you how you can choose the right events for you!";
            myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
            myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
            startActivity(Intent.createChooser(myIntent, "Share with"));
        }
        if (item.getItemId() == R.id.take_note) {
            //Launch take note activity and make it with corner x
            //save it with ifreb ase or intent extra

            Intent newintent = new Intent(view.getContext(), ANote.class);
            //aboutHospitalityManagement
            newintent.putExtra("notename", "aboutchooseevent");
            startActivity(newintent);
        }

        return super.onOptionsItemSelected(item);
    }
}
