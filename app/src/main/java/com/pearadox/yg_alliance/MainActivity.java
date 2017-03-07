package com.pearadox.yg_alliance;

import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
// === DEBUG  ===
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.cpjd.main.Settings;
import com.cpjd.main.TBA;
import com.cpjd.models.Event;
import com.cpjd.models.Match;
import com.cpjd.models.Team;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import static android.icu.lang.UCharacter.toUpperCase;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";        // This CLASS name
    Spinner spinner_Device, spinner_Event;
    TextView txt_EvntCod, txt_EvntDat, txt_EvntPlace;
    ArrayAdapter<String> adapter_Event;
    Button btn_Teams, btn_Match_Sched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "******* Starting Yellow-Green Alliance  *******");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        preReqs(); 				        // Check for pre-requisites

        Spinner spinner_Event = (Spinner) findViewById(R.id.spinner_Event);
        String[] events = getResources().getStringArray(R.array.event_array);
        adapter_Event = new ArrayAdapter<String>(this, R.layout.list_layout, events);
        adapter_Event.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Event.setAdapter(adapter_Event);
        spinner_Event.setSelection(0, false);
        spinner_Event.setOnItemSelectedListener(new event_OnItemSelectedListener());

        btn_Teams = (Button) findViewById(R.id.btn_Teams);
        btn_Match_Sched = (Button) findViewById(R.id.btn_Match_Sched);
        txt_EvntCod = (TextView) findViewById(R.id.txt_EvntCod);
        txt_EvntDat = (TextView) findViewById(R.id.txt_EvntDat);
        txt_EvntPlace = (TextView) findViewById(R.id.txt_EvntPlace);
        txt_EvntCod.setText("");   // Event Code
        txt_EvntDat.setText("");                      // Event Date
        txt_EvntPlace.setText("");                      // Event Location

        TBA.setID("Pearadox", "YG_Alliance", "V1");
        final TBA tba = new TBA();
        Settings.FIND_TEAM_RANKINGS = true;
        Settings.GET_EVENT_TEAMS = true;
        Settings.GET_EVENT_MATCHES = true;
        Settings.GET_EVENT_ALLIANCES = true;
        Settings.GET_EVENT_AWARDS = true;

        Event e = tba.getEvent("txwa", 2017);

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Teams.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "  btn_Teams setOnClickListener  ");

                // Display top three teams name + rank + score
//            for(int i = 0; i < 3; i++) {
//                System.out.println("Name: "+e.teams[i].name+" Rank: "+e.teams[i].rank+" Score: "+e.teams[i].rankingScore);
//            }
//            System.out.println("\n");

                Log.d(TAG, "*** Team ***");
                Team[] teams = tba.getTeams(Pearadox.FRC_Event, 2017);
                Log.d(TAG, " Team array size = " + teams.length);
                String destFile = Pearadox.FRC_Event + "_Teams" + ".json";
                try {
                    File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
                    BufferedWriter bW;
                    bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
                    bW.write("[" + "\n");
                    for (int i = 0; i < teams.length; i++) {
                        String tnum = String.format("%1$4s",teams[i].team_number);
                        Log.d(TAG, " Team = " + tnum);
                        bW.write("    {    \"team_num\":\"" +  tnum + "\", " + "\n");
                        bW.write("         \"team_name\":\"" +  teams[i].nickname + "\", " + "\n");
                        bW.write("         \"team_loc\":\"" +  teams[i].location + "\" " + "\n");

                        if (i == teams.length -1) {       // Last one?
                            bW.write("    } " + "\n");
                        }  else {
                            bW.write("    }," + "\n");
                        }
                    } // end For # teams
                    //=====================================================================

                    bW.write("]" + "\n");
                    bW.write(" " + "\n");
                    bW.flush();
                    bW.close();
                    Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Teams file written to SD card ***" , Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage() + " not found in the specified directory.");
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Match_Sched.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "  btn_Teams setOnClickListener  ");
                Toast.makeText(getBaseContext(), "*** Not QUITE implemented just yet  ***", Toast.LENGTH_LONG).show();

                Log.d(TAG, "*** Match ***");
                Event event = new TBA().getEvent("2017" + Pearadox.FRC_Event);
                Match[] matches = event.matches;
                Log.d(TAG, " array size = " + matches.length);
                if (matches.length > 0)
                    for (int i = 0; i < matches.length; i++) {
                        // The comp level variable should include an indentifier for whether it's practice, qualifying, or playoff, let me know if you need more help on this
                        // Just print some general information, you can add more variables if you want, just use matches[i].var
                        System.out.println("Match name: " + matches[i].comp_level + " Set number: " + matches[i].set_number + " Time (in ms): " + matches[i].time);
                    }
                }
        });

    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void preReqs() {
        boolean isSdPresent;
        isSdPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Log.d(TAG, "SD card: " + isSdPresent);
        if (isSdPresent) {        // Make sure FRC directory is there
            File extStore = Environment.getExternalStorageDirectory();
            File directFRC = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414");
            if (!directFRC.exists()) {
                if (directFRC.mkdir()) {
                }        //directory is created;
            }
            Log.i(TAG, "FRC files created");
//        Toast toast = Toast.makeText(getBaseContext(), "FRC5414 ©2017  *** Files initialied ***" , Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast.show();
        }  else {
            Toast.makeText(getBaseContext(), "There is no SD card available", Toast.LENGTH_LONG).show();
        }
    }

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
private class event_OnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent,
                               View view, int pos, long id) {
        String ev = parent.getItemAtPosition(pos).toString();
        Pearadox.FRC_EventName = ev;
        Log.d(TAG, ">>>>> Event '" + Pearadox.FRC_EventName + "'");
        switch (ev) {
            case "Brazos Valley Regional":          // txwa
                Pearadox.FRC_Event = "txwa";
                break;
            case ("Lone Star Central Regional"):    // txho
                Pearadox.FRC_Event = "txho";
                break;
            case ("Hub City Regional"):             // txlu
                Pearadox.FRC_Event = "txlu";
                break;
            default:                // ?????
                Toast.makeText(getBaseContext(), "Event code not recognized", Toast.LENGTH_LONG).show();
                Pearadox.FRC_Event = "zzzz";
        }
        Log.d(TAG, " Event code = '" + Pearadox.FRC_Event + "'");
        Log.d(TAG, "*** Event ***");
        Event e = new TBA().getEvent("2017" + Pearadox.FRC_Event);
        // Print general event info
        System.out.println(e.name);
        System.out.println(e.location);
        System.out.println(e.start_date);
        System.out.println("\n");
        txt_EvntCod = (TextView) findViewById(R.id.txt_EvntCod);
        txt_EvntDat = (TextView) findViewById(R.id.txt_EvntDat);
        txt_EvntPlace = (TextView) findViewById(R.id.txt_EvntPlace);
        txt_EvntCod.setText(Pearadox.FRC_Event.toUpperCase());  // Event Code
        txt_EvntDat.setText(e.start_date);                      // Event Date
        txt_EvntPlace.setText(e.location);                      // Event Location


    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
}


//###################################################################
//###################################################################
//###################################################################
    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
     }
    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "OnDestroy ");
    }

}
