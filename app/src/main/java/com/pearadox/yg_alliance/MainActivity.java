package com.pearadox.yg_alliance;

import android.media.AudioManager;
import android.media.ToneGenerator;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import static android.icu.lang.UCharacter.toUpperCase;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";        // This CLASS name
    Spinner spinner_Device, spinner_Event;
    TextView txt_EvntCod, txt_EvntDat, txt_EvntPlace;
    ArrayAdapter<String> adapter_Event;
    Button btn_Teams, btn_Match_Sched, btn_Spreadsheet;
    public String[] teamsRed;
    public String[] teamsBlue;
    private FirebaseDatabase pfDatabase;
    private DatabaseReference pfMatchData_DBReference;
    matchData match_inst = new matchData();

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
        btn_Spreadsheet = (Button) findViewById(R.id.btn_Spreadsheet);
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

        Event e = tba.getEvent("txlu", 2017);

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Teams.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "  btn_Teams setOnClickListener  ");

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
                    Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Teams file (" + teams.length + " teams) written to SD card ***" , Toast.LENGTH_LONG);
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
                Log.i(TAG, "  btn_Match_Sched setOnClickListener  ");
                Event event = new TBA().getEvent("2017" + Pearadox.FRC_Event);
                Match[] matches = event.matches;
                Log.d(TAG, " Matches size = " + matches.length);

                //----------------------------------------
                if (matches.length > 0) {
                    System.out.println("Match name: " + matches[0].comp_level + " Time: " + matches[0].time_string + " Time (long in ms): " + matches[0].time);
                    Date date1 = new Date(matches[0].time);
                    DateFormat formatter1 = new SimpleDateFormat("HH:mm:ss:SSS");
                    String dateFormatted = formatter1.format(date1);
                    Log.e(TAG, " Time = "  + dateFormatted);
                    System.out.println("Match name: "+matches[3].comp_level + " Time: "+matches[3].time_string + " Time (long in ms): " +matches[3].time);
                    Date date2 = new Date(matches[3].time);
                    DateFormat formatter2 = new SimpleDateFormat("HH:mm");
                    String dateFormatted2 = formatter2.format(date2);
                    Log.e(TAG, " Time = "  + dateFormatted2);
                }  else {
                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                    Toast toast = Toast.makeText(getBaseContext(), "***  Data from the Blue Alliance is _NOT_ available this session  ***", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                //----------------------------------------
                int qm;
                String mn, r1, r2, r3, b1, b2, b3;
                String matchFile = Pearadox.FRC_Event + "_Match-Sched" + ".json";
                if (matches.length > 0) {
                    // The comp level variable includes an indentifier for whether it's practice, qualifying, or playoff
                    try {
                        File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + matchFile);
                        BufferedWriter bW;
                        bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
                        bW.write("[" + "\n");
                        for (int i = 0; i < matches.length; i++) {
                            Log.d(TAG, " Comp = " + matches[i].comp_level);
                            if (matches[i].comp_level.matches("qm")) {
                                bW.write(" {\"time\":\"" + matches[i].time_string + "\", ");
                                mn = String.valueOf(matches[i].match_number);
                                if (mn.length() < 2) {mn = "0" + mn;}   // make it at least 2-digits
                                Log.d(TAG, " match# = " + mn);
                                bW.write("  \"mtype\":\"Qualifying\",  \"match\": \"Q" + mn + "\", ");
                                teamsRed = matches[i].redTeams.clone();
                                r1 = teamsRed[0].substring(3, teamsRed[0].length());
                                if (r1.length() < 4) {r1 = " " + r1;}
                                Log.d(TAG, " R1 = " + r1);
                                r2 = teamsRed[1].substring(3, teamsRed[1].length());
                                if (r2.length() < 4) {r2 = " " + r2;}
                                r3 = teamsRed[2].substring(3, teamsRed[2].length());
                                if (r3.length() < 4) {r3 = " " + r3;}
                                bW.write(" \"r1\":\"" + r1 + "\",  \"r2\": \"" + r2 + "\", \"r3\":\"" + r3 + "\",");
                                teamsBlue = matches[i].blueTeams.clone();
                                b1 = teamsBlue[0].substring(3, teamsBlue[0].length());
                                if (b1.length() < 4) {b1 = " " + b1;}
                                b2 = teamsBlue[1].substring(3, teamsBlue[1].length());
                                if (b2.length() < 4) {b2 = " " + b2;}
                                b3 = teamsBlue[2].substring(3, teamsBlue[2].length());
                                if (b3.length() < 4) {b3 = " " + b3;}
                                bW.write(" \"b1\":\"" + b1 + "\",  \"b2\": \"" + b2 + "\", \"b3\":\"" + b3 + "\"");

                                if (i == matches.length -1) {       // Last one?
                                    bW.write("} " + "\n");
                                }  else {
                                    bW.write("}," + "\n");
                                }
                            }  else {
                                Log.d(TAG, "******* NOT 'qm' ********* " );
                                System.out.println(matches[i].set_number);
                                System.out.println(matches[i].event_key);
                                System.out.println(matches[i].time_string);
                                System.out.println(matches[i].key);
                            }
                        }  // end For # matches
                        //=====================================================================

                        bW.write("]" + "\n");
                        bW.write(" " + "\n");
                        bW.flush();
                        bW.close();
                        Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Matches file written to SD card ***" , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    } catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage() + " not found in the specified directory.");
                        System.exit(0);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }  // end Try/Catch
                }  else {
                    Toast toast = Toast.makeText(getBaseContext(), "☆☆☆ No Match data exists for this event yet (too early!)  ☆☆☆" , Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
        });


/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    btn_Spreadsheet.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
        Log.i(TAG, "  btn_Spreadsheet setOnClickListener  ");
        Log.e(TAG, "***** Matches # = "  + Pearadox.Matches_Data.size());   // Done in Event Click Listner
        Toast toast1 = Toast.makeText(getBaseContext(), "FRC5414 ©2017  *** Match Data loaded = " + Pearadox.Matches_Data.size() + " ***" , Toast.LENGTH_LONG);
        toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast1.show();
        String new_comm="";

        String destFile = Pearadox.FRC_Event + "_MatchData" + ".csv";
        try {
            File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
            BufferedWriter bW;
            bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
            bW.write(Pearadox.FRC_Event.toUpperCase() + " - " + Pearadox.FRC_EventName +"  \n");
            // Write Excel/Spreadsheet Header for each column
            bW.write("Match,Team,Auto Mode,Rope?,Carry Fuel?,Fuel Amt,Carry Gear?,Start Pos,");
            bW.write("Cross Baseline?,Gears Placed,Gears Attempted,Gear Post,Shoot HG?,HG %,Shoot LG?,LG %,");
            bW.write("Act Hopper?,Fuel Collected,Stop Position,Auto Comment,|,");

            bW.write("Pickup Gears?,Gears Placed,Gears Attempted,# Cycles,Shoot HG?,HG %,Shoot LG?,LG %,");
            bW.write("Climb Attempt?,Touchpad Activated?,Climb Success?,Tele Comment,|,");

            bW.write("Lost Parts?,Lost Comms?,Good Def?,Lane?,Blocking?,Hopper Dump?,Gear Block?,");
            bW.write("Num Penalties,Date-Time Saved,Final Comment,||,Scout Name");

            bW.write(" " + "\n");
            //=====================================================================
            for (int i = 0; i < Pearadox.Matches_Data.size(); i++) {
                match_inst = Pearadox.Matches_Data.get(i);      // Get instance of Match Data
                bW.write(match_inst.getMatch() + "," + match_inst.getTeam_num() +",");
                //----- Auto -----
                bW.write(match_inst.isAuto_mode() + "," + match_inst.isAuto_rope() + "," + match_inst.isAuto_carry_fuel() + "," + match_inst.getAuto_fuel_amount() + "," + match_inst.isAuto_gear() + "," + match_inst.getAuto_start() + "," );
                bW.write(match_inst.isAuto_baseline() + "," + match_inst.getAuto_gears_placed() + "," + match_inst.getAuto_gears_attempt() + "," + match_inst.getAuto_gear_pos() + "," + match_inst.isAuto_hg() + "," + match_inst.getAuto_hg_percent() + "," + match_inst.isAuto_lg() + "," + match_inst.getAuto_lg_percent() + "," );
                new_comm = removeLine(match_inst.getAuto_comment());
                bW.write(match_inst.isAuto_act_hopper() + "," + match_inst.getAuto_fuel_collected() + "," + match_inst.getAuto_stop() + "," + new_comm + "," + "|" + "," );
                //----- Tele -----
                bW.write(match_inst.isTele_gear_pickup() + "," + match_inst.getTele_gears_placed() + "," + match_inst.getTele_gears_attempt() + "," + match_inst.getTele_cycles() + "," + match_inst.isTele_hg() + "," + match_inst.getTele_hg_percent() + "," + match_inst.isTele_lg() + "," + match_inst.getTele_lg_percent() +",");
                String y = match_inst.getTele_comment();
                new_comm = removeLine(match_inst.getTele_comment());
                bW.write(match_inst.isTele_climb_attempt() + "," + match_inst.isTele_touch_act() + "," + match_inst.isTele_climb_success() + "," + new_comm + "," + "|" + "," );
                //----- Final -----
                bW.write(match_inst.isFinal_lostParts() + "," + match_inst.isFinal_lostComms()+ "," + match_inst.isFinal_defense_good() + "," + match_inst.isFinal_def_Lane() + "," + match_inst.isFinal_def_Block() + "," + match_inst.isFinal_def_Hopper()+ "," + match_inst.isFinal_def_Gear() +",");
                String x = match_inst.getFinal_comment();
                new_comm = removeLine(match_inst.getFinal_comment());
                bW.write(match_inst.getFinal_num_Penalties() + "," + match_inst.getFinal_dateTime() + "," + new_comm + "," + "||" + "," + match_inst.getFinal_studID());
                //-----------------
                bW.write(" " + "\n");
            } // End For

            //=====================================================================
            bW.write(" " + "\n");
            bW.flush();
            bW.close();
            Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event.toUpperCase() + "' Match Data Spreadsheet written to SD card ***" , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " not found in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }  // end Try/Catch

        }
    });
}

    private String removeLine(String comment) {
        String x = "";
        if (comment.contains(",")) {
//            Log.w(TAG, " %$^&#!! COMMA  " + match_inst.getMatch() + "," + match_inst.getTeam_num() + "[" + comment + "]");
//            int Comma = comment.indexOf(",");
            x = comment.replaceAll(",", ";");
//            x = comment.substring(0, Comma) +"•"+  comment.substring(Comma + 1);
//            Log.w(TAG, "X: '" + x + "'");
        }
        return x;
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void preReqs() {
        boolean isSdPresent;
        isSdPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Log.w(TAG, "SD card: " + isSdPresent);
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
            case "FIRST Championship (Houston)":          // txwa
                Pearadox.FRC_Event = "cmptx";
                break;
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

        pfDatabase = FirebaseDatabase.getInstance();
        pfMatchData_DBReference = pfDatabase.getReference("match-data/" + Pearadox.FRC_Event);    // Match Data
        addMD_VE_Listener(pfMatchData_DBReference);        // Load _ALL_ Matches

    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
}


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void addMD_VE_Listener(final DatabaseReference pfMatchData_DBReference) {
        pfMatchData_DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "<<<< addMD_VE_Listener >>>>     Match Data ");
                Pearadox.Matches_Data.clear();
                matchData mdobj = new matchData();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    mdobj = iterator.next().getValue(matchData.class);
                    Pearadox.Matches_Data.add(mdobj);
                }
                Log.w(TAG, "***** Matches Loaded from Firebase. # = "  + Pearadox.Matches_Data.size());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });
    }


//###################################################################
//###################################################################
//###################################################################
@Override
public void onStart() {
    super.onStart();
    Log.v(TAG, ">>>>  yg_alliance onStart  <<<<");

}
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
