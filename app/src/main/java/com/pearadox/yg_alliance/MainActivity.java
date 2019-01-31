package com.pearadox.yg_alliance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

// API-V3
import com.cpjd.main.TBA;
import com.cpjd.main.CTBA;
import com.cpjd.models.other.events.EventOPR;
import com.cpjd.models.simple.SEvent;
import com.cpjd.models.simple.SMatch;
import com.cpjd.models.simple.STeam;
import com.cpjd.models.standard.Event;
import com.cpjd.models.standard.Match;
import com.cpjd.models.standard.Team;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

// === DEBUG  ===
import static android.util.Log.i;



public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";        // This CLASS name
    String Pearadox_Version = " ";      // initialize
    Long Pearadox_Date;
    String TBA_AuthToken = "xgqQi9cACRSUt4xanOto70jLPxhz4lR2Mf83e2iikyR2vhOmr1Kvg1rDBlAQcOJg";
    int BAyear = 2019;  // Current Yesr for B.A. calls
    Boolean FB_logon = false;           // indicator for Firebase logon success
    Boolean BA_Data = false;
    Spinner spinner_Device, spinner_Event;
    TextView txt_EvntCod, txt_EvntDat, txt_EvntPlace;
    ArrayAdapter<String> adapter_Event;
    Button btn_Events, btn_Teams, btn_Match_Sched, btn_Spreadsheet, btn_Rank, btn_Pit;
    final String[] URL = {""};
    final String[] photStat = {""};
    String teamNumber = "";
    Team[] teams;
    public static int BAnumTeams = 0;                       // # of teams from Blue Alliance
//    public String[] teamsRed;
//    public String[] teamsBlue;
    private FirebaseDatabase pfDatabase;
    private DatabaseReference pfEvent_DBReference;
    private DatabaseReference pfMatch_DBReference;
    private DatabaseReference pfMatchData_DBReference;
    private DatabaseReference pfTeam_DBReference;
    private DatabaseReference pfPitData_DBReference;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;

    matchData match_inst = new matchData();
    String destFile;
    String prevTeam = "";
    String Ht = "";
    String Stud = "";
    String Result = "";
    String pitData_pres = "";
    String photo_pres = "   ✔ ";
    String tnum = "";

    int startRow = 3;
    int lastRow = 0;
    BufferedWriter bW;
    String teamMumber = "";
    String tmName = "";
    String tmRank = "";
    String tmWLT = "";
    String tmOPR = "";
    String tmKPa = "";
    String tmTPts = "";
    Event BAe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w(TAG, "******* Starting Yellow-Green Alliance  *******");

        try {
            Pearadox_Version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            Pearadox_Date = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        Toast toast = Toast.makeText(getBaseContext(), "Pearadox Yellow-Green Alliance App ©2019  Ver." + Pearadox_Version, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        mAuth = FirebaseAuth.getInstance();

        preReqs();                        // Check for pre-requisites

        Spinner spinner_Event = (Spinner) findViewById(R.id.spinner_Event);

        btn_Events = (Button) findViewById(R.id.btn_Events);
        btn_Teams = (Button) findViewById(R.id.btn_Teams);
        btn_Match_Sched = (Button) findViewById(R.id.btn_Match_Sched);
        btn_Spreadsheet = (Button) findViewById(R.id.btn_Spreadsheet);
        btn_Rank = (Button) findViewById(R.id.btn_Rank);
        btn_Pit = (Button) findViewById(R.id.btn_Pit);
        btn_Teams.setEnabled(false);
        btn_Match_Sched.setEnabled(false);
        btn_Spreadsheet.setEnabled(false);
        btn_Rank.setEnabled(false);
        btn_Pit.setEnabled(false);
        txt_EvntCod = (TextView) findViewById(R.id.txt_EvntCod);
        txt_EvntDat = (TextView) findViewById(R.id.txt_EvntDat);
        txt_EvntPlace = (TextView) findViewById(R.id.txt_EvntPlace);
        txt_EvntCod.setText("");            // Event Code
        txt_EvntDat.setText("");            // Event Date
        txt_EvntPlace.setText("");          // Event Location

        TBA.setAuthToken(TBA_AuthToken);
        final TBA tba = new TBA();

        /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Events.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Log.w(TAG, "  btn_Events setOnClickListener  " );
            pfDatabase = FirebaseDatabase.getInstance();
            pfEvent_DBReference = pfDatabase.getReference("competitions");
            p_Firebase.eventObj new_event = new p_Firebase.eventObj();
            Event[] our_events = tba.getEvents(5414, BAyear);
            Log.w(TAG, " #Events = " + our_events.length);

            for (int i = 0; i < our_events.length; i++) {
                new_event.setcomp_name(our_events[i].getName());
                new_event.setComp_code(our_events[i].getEventCode());
                // ToDo - How is World's Division set on B.A.??????
                new_event.setcomp_div(our_events[i].getEventCode());
                new_event.setcomp_date(our_events[i].getStartDate());
                new_event.setcomp_place(our_events[i].getLocationName());
                new_event.setcomp_city(our_events[i].getCity() + ", " + our_events[i].getStateProv());
                String keyID = new_event.getComp_code();
                pfEvent_DBReference.child(keyID).setValue(new_event);
                // ToDo Add new directories for each event  (match-data, matches,pit-data, teams)

            } // for
            btn_Events.setEnabled(false);         // Turn off Button
            Toast toast = Toast.makeText(getBaseContext(), "*** Competitions (" + our_events.length + " events) written to Firebase ***", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            loadEvents();       // Reload with new events
            }
        });


/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Teams.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Teams setOnClickListener  " + Pearadox.FRC_ChampDiv);
                pfTeam_DBReference = pfDatabase.getReference("teams/" + Pearadox.FRC_Event);   // Team data from Firebase D/B
                p_Firebase.teamsObj new_team = new p_Firebase.teamsObj();
                Team[] teams = tba.getEventTeams("2019" + Pearadox.FRC_ChampDiv);
                Log.w(TAG, " Team array size = " + teams.length);
                if (teams.length > 0) {
                    String destFile = Pearadox.FRC_ChampDiv + "_Teams" + ".json";
                    Log.w(TAG, " filename = " + destFile);
                    try {
                        File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
                        Log.e(TAG, " path = " + prt);
                        BufferedWriter bW;
                        bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
                        bW.write("[" + "\n");
                        for (int i = 0; i < teams.length; i++) {
                            String tnum = String.format("%1$4s", teams[i].getTeamNumber());
                            Log.w(TAG, " Team = " + tnum);
                            bW.write("    {    \"team_num\":\"" + tnum + "\", " + "\n");
                            bW.write("         \"team_name\":\"" + teams[i].getNickname() + "\", " + "\n");
                            bW.write("         \"team_loc\":\"" + (teams[i].getCity() + ", " + teams[i].getStateProv() + "  " + teams[i].getPostalCode()) + "\" " + "\n");
                            new_team.setTeam_num(tnum);
                            new_team.setTeam_name(teams[i].getNickname());
                            new_team.setTeam_loc(teams[i].getCity() + ", " + teams[i].getStateProv() + "  " + teams[i].getPostalCode());
                            String keyID = String.format("%04d", teams[i].getTeamNumber());     // Make it 4 digit (only for KEY)
                            pfTeam_DBReference.child(keyID).setValue(new_team);  // Add to firebase

                            if (i == teams.length - 1) {       // Last one?
                                bW.write("    } " + "\n");
                            } else {
                                bW.write("    }," + "\n");
                            }
                        } // end For # teams
                        //=====================================================================

                        bW.write("]" + "\n");
                        bW.write(" " + "\n");
                        bW.flush();
                        bW.close();
                        Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Teams file (" + teams.length + " teams) written to SD card ***", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    } catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage() + " not found in the specified directory.");
                        System.exit(0);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                    Toast toast = Toast.makeText(getBaseContext(), "** There are _NO_ teams for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
        });


/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Rank.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Rank setOnClickListener  " + Pearadox.FRC_ChampDiv);

                Team[] teams = tba.getTeams(2018,1);
//                Team[] teams = tba.getTeams(Pearadox.FRC_ChampDiv, BAyear);
                Log.w(TAG, " Team array size = " + teams.length);
                if (teams.length > 0) {
                    for (int i = 0; i < teams.length; i++) {
//                        teamMumber = String.valueOf(teams[i].team_number);
//                        tmName = teams[i].nickname;
//                        tmRank = String.valueOf(teams[i].rank);
//                        tmWLT = teams[i].record;
//                        tmOPR = String.format("%3.3f", ((new TBA().fillOPR(BAe, BAe.teams[i]).opr)));
                        Log.w(TAG, teamMumber + "  OPR: " + tmOPR + "  WLT " + tmWLT + "  Rank=" + tmRank + "  " + tmName);
                    }
                } else {
                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                    Toast toast = Toast.makeText(getBaseContext(), "** There are _NO_ Blue Alliance teams for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                btn_Rank.setEnabled(false);         // Turn off Button
            }
        });

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Pit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Log.w(TAG, "  btn_Pit setOnClickListener  " + Pearadox.FRC_ChampDiv);
            pitData the_pits = new pitData();
            p_Firebase.teamsObj My_inst = new p_Firebase.teamsObj();
            Log.w(TAG, " Team array size = " + Pearadox.numTeams);
            if (Pearadox.numTeams > 0) {
                String destFile = Pearadox.FRC_ChampDiv + "_PitData" + ".txt";
                Log.w(TAG, " filename = " + destFile);
                try {
                    File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
                    Log.e(TAG, " path = " + prt);
                    BufferedWriter bW;
                    bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
                    bW.write("TEAM" + "  " + "PIT  PHOTO   HT   SCOUT" + "\n");

                    for (int i = 0; i < Pearadox.numTeams; i++) {
                        pitData_pres = " - ";       // Not there
                        photo_pres = " - ";
                        Ht = "  ";  Stud = "";
                        My_inst = Pearadox.team_List.get(i);
                        tnum = My_inst.getTeam_num();
//                        Log.w(TAG, " Team# = '" + tnum + "'  Pit=" + num_Pits) ;

                        // Find Pit Data (if there)
                        for (int x = 0; x < Pearadox.num_Pits; x++) {
                            the_pits = Pearadox.Pit_Data.get(x);
//                            Log.w(TAG, " Team# = '" + tnum + "'  and '" + the_pits.getPit_team() + "'") ;

                            if (the_pits.getPit_team().matches(tnum)) {
                                pitData_pres = " ✔ ";
                                Ht = String.format("%1$2s", the_pits.getPit_tall());
                                Stud = the_pits.getPit_scout();
//                                Log.w(TAG, "Ht=" + Ht + "  Scout=" + Stud);
                                String photoStatus = the_pits.getPit_photoURL();
                                Log.w(TAG, "%%%%%%%%% Status = " + photoStatus) ;
                                if (photoStatus == null) {
                                    photo_pres = " -  ";
                                } else {
                                    photo_pres = " ✔ ";
                                }
                            } // Endif
                        } //End for #pits
                        Result = tnum   + "    " + pitData_pres + "     " +  photo_pres + "     " + Ht + "   " + Stud;

                        bW.write(Result + "\n");
                    } // end For # teams
                    //=====================================================================

                    bW.write(" " + "\n");
                    bW.flush();
                    bW.close();
                    Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Pit Data Coverage file (" + Pearadox.numTeams + " teams) written to SD card ***", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage() + " not found in the specified directory.");
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                Toast toast = Toast.makeText(getBaseContext(), "** There are _NO_ teams for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
            btn_Pit.setEnabled(false);         // Turn off Button

            Intent pit_intent = new Intent(MainActivity.this, PitCover_Activity.class);
            startActivity(pit_intent);

        }
    });


/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Match_Sched.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Match_Sched setOnClickListener  ");
//                Match[] matchSched = tba.getMatches("2019" + Pearadox.FRC_ChampDiv);
                Match[] matchSched = tba.getMatches("2018code");
                Log.w(TAG, " Matches size = " + matchSched.length);
                pfMatch_DBReference = pfDatabase.getReference("matches/" + Pearadox.FRC_Event);   // Matches data from Firebase D/B

                //----------------------------------------
                if (matchSched.length > 0) {
//                    System.out.println("Match name: " + matchSched[0].getCompLevel() + " Time: " + matchSched[0].getTime() + " Time (long in ms): " + matchSched[0].getActualTime());
                } else {
                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK);
                    Toast toast = Toast.makeText(getBaseContext(), "***  Data from the Blue Alliance is _NOT_ available this session  ***", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                //----------------------------------------
                int qm = 0;
                String mn, r1, r2, r3, b1, b2, b3;
                p_Firebase.matchObj MO_inst = new p_Firebase.matchObj();
                String matchFile = Pearadox.FRC_ChampDiv + "_Match-Sched" + ".json";
                if (matchSched.length > 0) {
                    // The comp level variable includes an indentifier for whether it's practice, qualifying, or playoff
                    try {
                        File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + matchFile);
                        BufferedWriter bW;
                        bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
                        bW.write("[" + "\n");
                        for (int i = 0; i < matchSched.length; i++) {
                            Match m = matchSched[i];//                            Log.w(TAG, " Comp = " + matchSched[i].comp_level);
                            if (m.getCompLevel().matches("qm")) {
                                qm++;
                                MO_inst.setMtype("Qualifying");
//                                long millis = m.getPredictedTime();
                                long millis = m.getTime();
                                Date date = new Date(millis);
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                                String formatted = formatter.format(date);      // Pass date object
                                MO_inst.setTime(formatted);
                                bW.write(" {\"time\":\"" + formatted + "\", ");
                                mn = String.valueOf(m.getMatchNumber());
                                if (mn.length() < 2) {
                                    mn = "00" + mn;
                                }      // make it at least 3-digits
                                if (mn.length() < 3) {
                                    mn = "0" + mn;
                                }       // make it at least 3-digits
                                Log.w(TAG, " match# = " + mn);
                                MO_inst.setMatch(mn);
                                bW.write("  \"mtype\":\"Qualifying\",  \"match\": \"Q" + mn + "\", ");
                                String Red = m.getRed().getTeamKeys()[0];   // R1
                                r1 = Red.substring(3, Red.length());
                                if (r1.length() < 4) {
                                    r1 = " " + r1;
                                }
                                Log.w(TAG, " R1 = " + r1);
                                MO_inst.setR1(r1);
                                Red = m.getRed().getTeamKeys()[1];          // R2
                                r2 = Red.substring(3, Red.length());
                                if (r2.length() < 4) {
                                    r2 = " " + r2;
                                }
                                MO_inst.setR2(r2);
                                Red = m.getRed().getTeamKeys()[2];          // R3
                                r3 = Red.substring(3, Red.length());
                                if (r3.length() < 4) {
                                    r3 = " " + r3;
                                }
                                MO_inst.setR3(r3);
                                bW.write(" \"r1\":\"" + r1 + "\",  \"r2\": \"" + r2 + "\", \"r3\":\"" + r3 + "\",");
                                String Blue = m.getBlue().getTeamKeys()[0];  // B1
                                b1 = Blue.substring(3, Blue.length());
                                if (b1.length() < 4) {
                                    b1 = " " + b1;
                                }
                                MO_inst.setB1(b1);
                                Blue = m.getBlue().getTeamKeys()[1];         // B2
                                b2 = Blue.substring(3, Blue.length());
                                if (b2.length() < 4) {
                                    b2 = " " + b2;
                                }
                                MO_inst.setB2(b2);
                                Blue = m.getBlue().getTeamKeys()[2];         // B3
                                b3 = Blue.substring(3, Blue.length());
                                if (b3.length() < 4) {
                                    b3 = " " + b3;
                                }
                                MO_inst.setB3(b3);
                                bW.write(" \"b1\":\"" + b1 + "\",  \"b2\": \"" + b2 + "\", \"b3\":\"" + b3 + "\"");

                                pfMatch_DBReference.child(String.valueOf(qm)).setValue(MO_inst);  // Add to firebase

                                if (i == matchSched.length - 1) {       // Last one?
                                    bW.write("} " + "\n");
                                } else {
                                    bW.write("}," + "\n");
                                }
                            } else {
                                Log.e(TAG, "******* NOT 'qm' ********* ");
//                                System.out.println(matchSched[i].set_number);
//                                System.out.println(matchSched[i].event_key);
//                                System.out.println(matchSched[i].time_string);
//                                System.out.println(matchSched[i].key);
                            }
                        }  // end For # matchSched
                        //=====================================================================

                        bW.write("]" + "\n");
                        bW.write(" " + "\n");
                        bW.flush();
                        bW.close();
                        Log.w(TAG, qm + " *** '" + Pearadox.FRC_Event + "' Matches written to SD card ***");
                        Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Matches file written to SD card *** " + qm, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    } catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage() + " not found in the specified directory.");
                        System.exit(0);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }  // end Try/Catch
                } else {
                    Toast toast = Toast.makeText(getBaseContext(), "☆☆☆ No Match data exists for this event yet (too early!)  ☆☆☆", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
        });


/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Spreadsheet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Spreadsheet setOnClickListener  ");
                Log.e(TAG, "***** Matches # = " + Pearadox.Matches_Data.size());   // Done in Event Click Listner
//        Toast toast1 = Toast.makeText(getBaseContext(), "FRC5414 ©2019  *** Match Data loaded = " + Pearadox.Matches_Data.size() + " ***" , Toast.LENGTH_LONG);
//        toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast1.show();
                String new_comm = "";

                destFile = Pearadox.FRC_Event + "_MatchData" + ".csv";
                try {
                    File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
                    bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
                    bW.write(Pearadox.FRC_Event.toUpperCase() + " - " + Pearadox.FRC_EventName + "  \n");
                    // Write Excel/Spreadsheet Header for each column
                    bW.write("Team,Match,Carry Cube,Start Pos,_NO_ Auto Mode,Crossed Baseline?,");
                    bW.write("Cube Switch,Switch Attempted,Switch X-Over,Extra Cube,Cube Scale,Scale Attempted,Scale Extra,Scale X-Over,Wrong Switch,Wrong Scale,");
                    bW.write("Auto Comment,|,");

                    bW.write("Cube Switch,Switch Attempted,Cube Scale,Scale Attempted,Their Switch,Their Attempted,");
                    bW.write("Exchange,Portal,Power Zone,Our Floor,Their Floor,Random Floor,P/U Cubes,Place,Launch,");
                    bW.write("On Platform,Climb Success?,Climb Attempt?,Rung,Side,Lift-1,Lift-2,Was Lifted,Tele Comment,|,");

                    bW.write("Lost Parts?,Lost Comms?,Good Def?,Lane?,Blocking?,Switch Block?,");
                    bW.write("Num Penalties,Date-Time Saved,Final Comment,||,Scout-Last,Scout-First,|,");
                    bW.write("Team,Rank,W-L-T,OPR,||");
                    bW.write(",Weighted ALL,Weighted Last-3,Auto Switch ALL,Auto Switch Last-3,Auto Scale ALL,Auto Scale Last-3,Tele Switch ALL,Tele Switch Last-3,Tele Scale ALL,Tele Scale Last-3,");
                    bW.write("Exchange,Climbs ALL,Climbs Last-3,Side Out,Side In,Middle");

                    bW.write(" " + "\n");
                    prevTeam = "";
                    //=====================================================================
                    for (int i = 0; i < Pearadox.Matches_Data.size(); i++) {
                        match_inst = Pearadox.Matches_Data.get(i);      // Get instance of Match Data
                        if (!match_inst.getTeam_num().matches(prevTeam)) {      // Same team?
                            if (i > 0) {
//                        Log.w(TAG, "Prev: " + prevTeam + "  New: " + match_inst.getTeam_num() + "  Start: " + startRow + "  i=" + i);
                                wrtHdr();
                            } else {
                                prevTeam = match_inst.getTeam_num();
                            }
                            lastRow = startRow - 1;
                        }
                        bW.write(match_inst.getTeam_num() + "," + match_inst.getMatch() + ",");
                        //----- Auto -----
                        bW.write(match_inst.isPre_cube() + "," + match_inst.getPre_startPos() + "," + match_inst.isAuto_mode() + "," + match_inst.isAuto_baseline() + ",");
                        bW.write(match_inst.isAuto_cube_switch() + "," + match_inst.isAuto_cube_switch_att() + "," + match_inst.isAuto_xover_switch() + "," + match_inst.isAuto_switch_extra() + ",");
                        bW.write(match_inst.isAuto_cube_scale() + "," + match_inst.isAuto_cube_scale_att() + "," + match_inst.isAuto_scale_extra() + "," + match_inst.isAuto_xover_scale() + "," + match_inst.isAuto_wrong_switch() + "," + match_inst.isAuto_wrong_scale() + ",");
                        new_comm = StringEscapeUtils.escapeCsv(match_inst.getAuto_comment());
                        bW.write(new_comm + "," + "|" + ",");
                        //----- Tele -----
                        bW.write(match_inst.getTele_cube_switch() + "," + match_inst.getTele_switch_attempt() + "," + match_inst.getTele_cube_scale() + "," + match_inst.getTele_scale_attempt() + "," + match_inst.getTele_their_switch() + "," + match_inst.getTele_their_attempt() + ",");
                        bW.write(match_inst.getTele_cube_exchange() + "," + match_inst.getTele_cube_portal() + "," + match_inst.getTele_cube_pwrzone() + "," + match_inst.getTele_cube_floor() + "," + match_inst.getTele_their_floor() + "," + match_inst.getTele_random_floor() + "," + match_inst.isTele_cube_pickup() + "," + match_inst.isTele_placed_cube() + "," + match_inst.isTele_launched_cube() + ",");
//                String y = match_inst.getTele_comment();
                        new_comm = StringEscapeUtils.escapeCsv(match_inst.getTele_comment());
                        bW.write(match_inst.isTele_on_platform() + "," + match_inst.isTele_climb_success() + "," + match_inst.isTele_climb_attempt() + "," + match_inst.isTele_grab_rung() + "," + match_inst.isTele_grab_side() + "," + match_inst.isTele_lift_one() + "," + match_inst.isTele_lift_two() + "," + match_inst.isTele_got_lift() + "," + new_comm + "," + "|" + ",");
                        //----- Final -----
                        bW.write(match_inst.isFinal_lostParts() + "," + match_inst.isFinal_lostComms() + "," + match_inst.isFinal_defense_good() + "," + match_inst.isFinal_def_Lane() + "," + match_inst.isFinal_def_Block() + "," + match_inst.isFinal_def_BlockSwitch() + ",");
//                String x = match_inst.getFinal_comment();
                        new_comm = StringEscapeUtils.escapeCsv(match_inst.getFinal_comment());
                        bW.write(match_inst.getFinal_num_Penalties() + "," + match_inst.getFinal_dateTime() + "," + new_comm + "," + "||" + "," + match_inst.getFinal_studID() + ",|,,,,,||");
                        //-----------------
                        bW.write(" " + "\n");
                        lastRow = lastRow + 1;
//                Log.w(TAG, match_inst.getTeam_num() + "  Last: " + lastRow);
                        if (i == Pearadox.Matches_Data.size() - 1) {       // Last one?
                            wrtHdr();
                        }
                    } // End For

                    //=====================================================================
                    bW.write(" " + "\n");
                    bW.flush();
                    bW.close();
                    Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event.toUpperCase() + "' Match Data Spreadsheet written to SD card ***", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    btn_Spreadsheet.setEnabled(false);      // turn off button (done)
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage() + " not found in the specified directory.");
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }  // end Try/Catch

            }
        });
    }

    private void wrtHdr() {
//        Log.w(TAG, " wrtHdr  " + prevTeam);
        try {
            bW.write(prevTeam + ",'***,");
            /*  Auto */
            String esc$E = StringEscapeUtils.escapeCsv("=(COUNTIF($E" + startRow + ":$E" + lastRow + ",TRUE))");
            String esc$XT = StringEscapeUtils.escapeCsv("=(COUNTIF($F" + startRow + ":$F" + lastRow + ",TRUE))");
            bW.write(",'Bool% >>," + esc$E + "/" + ((lastRow - startRow) + 1) + "," + esc$XT + "/" + ((lastRow - startRow) + 1));  // crossed ÷ #matches
            String esc$G = StringEscapeUtils.escapeCsv("=IF((COUNTIF($H" + startRow + ":$H" + lastRow + ",TRUE))>0,COUNTIF($G" + startRow + ":$G" + lastRow + ",TRUE)/COUNTIF($H" + startRow + ":$H" + lastRow + ",TRUE),0)");
            String esc$H = "";
            if (lastRow - startRow >= 2) {
                esc$H = StringEscapeUtils.escapeCsv("=IF((COUNTIF($H" + (lastRow - 2) + ":$H" + lastRow + ",TRUE))>0,COUNTIF($G" + (lastRow - 2) + ":$G" + lastRow + ",TRUE)/COUNTIF($H" + (lastRow - 2) + ":$H" + lastRow + ",TRUE),0)");
            } else {
                esc$H = "0";
            }
            String esc$I = StringEscapeUtils.escapeCsv("=(COUNTIF($I" + startRow + ":$I" + lastRow + ",TRUE))");    // Switch Cross-over
            String esc$J = StringEscapeUtils.escapeCsv("=(COUNTIF($J" + startRow + ":$J" + lastRow + ",TRUE))");
            String esc$K = StringEscapeUtils.escapeCsv("=(COUNTIF($K" + startRow + ":$K" + lastRow + ",TRUE))");
            String esc$L = StringEscapeUtils.escapeCsv("=(COUNTIF($L" + startRow + ":$L" + lastRow + ",TRUE))");    // Scale Att
            String esc$M = StringEscapeUtils.escapeCsv("=(COUNTIF($M" + startRow + ":$M" + lastRow + ",TRUE))");    // Scale Extra
            String esc$N = StringEscapeUtils.escapeCsv("=(COUNTIF($M" + startRow + ":$M" + lastRow + ",TRUE))");
            bW.write("," + esc$G + "," + esc$H + "," + esc$I + "/" + ((lastRow - startRow) + 1) + "," + esc$J + "/" + ((lastRow - startRow) + 1) + "," + esc$K + "," + esc$L + "," + esc$M + "," + esc$N + "/" + ((lastRow - startRow) + 1));
            String esc$O = StringEscapeUtils.escapeCsv("=(COUNTIF($N" + startRow + ":$N" + lastRow + ",TRUE))");
            String esc$P = StringEscapeUtils.escapeCsv("=(COUNTIF($O" + startRow + ":$O" + lastRow + ",TRUE))");
            bW.write("," + esc$O + "/" + ((lastRow - startRow) + 1) + "," + esc$P + "/" + ((lastRow - startRow) + 1) + ",,|");

            /*  Tele  */
            String escS = StringEscapeUtils.escapeCsv("=IF(SUM($T" + startRow + ":$T" + lastRow + ")>0,SUM($S" + startRow + ":$S" + lastRow + ")/SUM($T" + startRow + ":$T" + lastRow + "),0)");
            String escU = StringEscapeUtils.escapeCsv("=IF(SUM($V" + startRow + ":$V" + lastRow + ")>0,SUM($U" + startRow + ":$U" + lastRow + ")/SUM($V" + startRow + ":$V" + lastRow + "),0)");
            String escW = StringEscapeUtils.escapeCsv("=IF(SUM($X" + startRow + ":$X" + lastRow + ")>0,SUM($W" + startRow + ":$W" + lastRow + ")/SUM($X" + startRow + ":$X" + lastRow + "),0)");
            String escT = "";
            String escV = "";
            String escX = "";
            if (lastRow - startRow >= 2) {
                escT = StringEscapeUtils.escapeCsv("=IF(SUM($T" + (lastRow - 2) + ":$T" + lastRow + ")>0,SUM($S" + (lastRow - 2) + ":$S" + lastRow + ")/SUM($T" + (lastRow - 2) + ":$T" + lastRow + "),0)");
                escV = StringEscapeUtils.escapeCsv("=IF(SUM($V" + (lastRow - 2) + ":$V" + lastRow + ")>0,SUM($U" + (lastRow - 2) + ":$U" + lastRow + ")/SUM($V" + (lastRow - 2) + ":$V" + lastRow + "),0)");
                escX = StringEscapeUtils.escapeCsv("=IF(SUM($X" + (lastRow - 2) + ":$X" + lastRow + ")>0,SUM($W" + (lastRow - 2) + ":$W" + lastRow + ")/SUM($X" + (lastRow - 2) + ":$X" + lastRow + "),0)");
            } else {
                escS = "0";
                escU = "0";
                escW = "0";
            }
            //            String escL3 = StringEscapeUtils.escapeCsv("=AVERAGE(OFFSET(INDIRECT(\"J\"&ROW()),-3,0,3,1))");
            bW.write("," + escS + "," + escT + "," + escU + "," + escV + "," + escW + "," + escX);
            bW.write("," + "=SUM($Y" + startRow + ":$Y" + lastRow + ")/" + ((lastRow - startRow) + 1));     // Exchange
            bW.write("," + "=SUM($Z" + startRow + ":$Z" + lastRow + ")/" + ((lastRow - startRow) + 1));     // Portal
            bW.write("," + "=SUM($AA" + startRow + ":$AA" + lastRow + ")/" + ((lastRow - startRow) + 1));   //Pwr Cube Zone
            bW.write("," + "=SUM($AB" + startRow + ":$AB" + lastRow + ")/" + ((lastRow - startRow) + 1));   // Our Floor
            bW.write("," + "=SUM($AC" + startRow + ":$AC" + lastRow + ")/" + ((lastRow - startRow) + 1));   // Their Floor
            bW.write("," + "=SUM($AD" + startRow + ":$AD" + lastRow + ")/" + ((lastRow - startRow) + 1));   // Random Floor
            String esc$AE = StringEscapeUtils.escapeCsv("=(COUNTIF($AE" + startRow + ":$AE" + lastRow + ",TRUE))");
            String esc$AF = StringEscapeUtils.escapeCsv("=(COUNTIF($AF" + startRow + ":$AF" + lastRow + ",TRUE))");
            bW.write("," + esc$AE + "/" + ((lastRow - startRow) + 1) + "," + esc$AF + "/" + ((lastRow - startRow) + 1));
            String esc$AG = StringEscapeUtils.escapeCsv("=(COUNTIF($AG" + startRow + ":$AG" + lastRow + ",TRUE))");     // On Platform
            String esc$AH = StringEscapeUtils.escapeCsv("=(COUNTIF($AH" + startRow + ":$AH" + lastRow + ",TRUE))");     // Climb Success
            bW.write("," + esc$AG + "/" + ((lastRow - startRow) + 1) + "," + esc$AH + "/" + ((lastRow - startRow) + 1));
            String esc$Climb = StringEscapeUtils.escapeCsv("=IF((COUNTIF($AJ" + startRow + ":$AJ" + lastRow + ",TRUE))>0,COUNTIF($AI" + startRow + ":$AI" + lastRow + ",TRUE)/COUNTIF($AJ" + startRow + ":$AJ" + lastRow + ",TRUE),0)");
            String escAJ = "";
            if (lastRow - startRow >= 2) {
                escAJ = StringEscapeUtils.escapeCsv("=IF((COUNTIF($AJ" + (lastRow - 2) + ":$AJ" + lastRow + ",TRUE))>0,COUNTIF($AH" + (lastRow - 2) + ":$AH" + lastRow + ",TRUE)/COUNTIF($AJ" + (lastRow - 2) + ":$AJ" + lastRow + ",TRUE),0)");
            } else {
                escAJ = "0";
            }
            bW.write("," + esc$Climb + "," + escAJ);
            String esc$AK = StringEscapeUtils.escapeCsv("=(COUNTIF($AK" + startRow + ":$AK" + lastRow + ",TRUE))");     // Rung
            String esc$AL = StringEscapeUtils.escapeCsv("=(COUNTIF($AL" + startRow + ":$AL" + lastRow + ",TRUE))");     // Side
            bW.write("," + esc$AK + "/" + ((lastRow - startRow) + 1) + "," + esc$AL + "/" + ((lastRow - startRow) + 1));
            String esc$AM = StringEscapeUtils.escapeCsv("=(COUNTIF($AM" + startRow + ":$AM" + lastRow + ",TRUE))");     // Lift-1
            String esc$AN = StringEscapeUtils.escapeCsv("=(COUNTIF($AN" + startRow + ":$AN" + lastRow + ",TRUE))");     // Lift-2
            String esc$AO = StringEscapeUtils.escapeCsv("=(COUNTIF($AO" + startRow + ":$AO" + lastRow + ",TRUE))");     // Was Lifted?
            bW.write("," + esc$AM + "/" + ((lastRow - startRow) + 1) + "," + esc$AN + "/" + ((lastRow - startRow) + 1) + "," + esc$AO + "/" + ((lastRow - startRow) + 1) + ",,|");

            /*  Final  */
            String esc$AR = StringEscapeUtils.escapeCsv("=(COUNTIF($AR" + startRow + ":$AR" + lastRow + ",TRUE))");     // Lost Parts
            String esc$AS = StringEscapeUtils.escapeCsv("=(COUNTIF($AS" + startRow + ":$AS" + lastRow + ",TRUE))");     // Lost Comm
            String esc$AT = StringEscapeUtils.escapeCsv("=(COUNTIF($AT" + startRow + ":$AT" + lastRow + ",TRUE))");     // Good Defense?
            bW.write("," + esc$AR + "/" + ((lastRow - startRow) + 1) + "," + esc$AS + "/" + ((lastRow - startRow) + 1) + "," + esc$AT + "/" + ((lastRow - startRow) + 1));
            String esc$AU = StringEscapeUtils.escapeCsv("=(COUNTIF($AU" + startRow + ":$AU" + lastRow + ",TRUE))");     // Lane Blk
            String esc$AV = StringEscapeUtils.escapeCsv("=(COUNTIF($AV" + startRow + ":$AV" + lastRow + ",TRUE))");     // Block
            String esc$AW = StringEscapeUtils.escapeCsv("=(COUNTIF($AW" + startRow + ":$AW" + lastRow + ",TRUE))");     // Switch Blk
            bW.write("," + esc$AU + "/" + ((lastRow - startRow) + 1) + "," + esc$AV + "/" + ((lastRow - startRow) + 1) + "," + esc$AW + "/" + ((lastRow - startRow) + 1));
            bW.write(",=SUM($AX" + startRow + ":$AX" + lastRow + ")/" + ((lastRow - startRow) + 1));                          // # Penalties
            bW.write(",,,||,,,|,");
            gatherBA(prevTeam);
            bW.write(tmName + "," + tmRank + ",'" + tmWLT + "," + tmOPR + ",||");

            bW.write(",=($G" + (lastRow + 1) + "+($K" + (lastRow + 1) + "*2) +($S" + (lastRow + 1) + "+($U" + (lastRow + 1) + "*2) + $AH" + (lastRow + 1) + " + $AL" + (lastRow + 1) + " + ($AM" + (lastRow + 1) + "*2)" + ") / 3)");   // Weighted ALL
            bW.write(",=($H" + (lastRow + 1) + "+($L" + (lastRow + 1) + "*2) +($T" + (lastRow + 1) + "+($V" + (lastRow + 1) + "*2) + $AI" + (lastRow + 1) + " + $AM" + (lastRow + 1) + " + ($AN" + (lastRow + 1) + "*2)" + ") / 3)");   // Weighted Last 3
            bW.write(",=$G$" + (lastRow + 1) + ",=$H$" + (lastRow + 1) + ",");          // Auto Switch (ALL & Last 3)
            bW.write("=$K$" + (lastRow + 1) + ",=$L$" + (lastRow + 1) + ",");           // Auto Scale (ALL & Last 3)
            bW.write("=$S$" + (lastRow + 1) + ",=$T$" + (lastRow + 1) + ",");           // Tele Switch (ALL & Last 3)
            bW.write("=$U$" + (lastRow + 1) + ",=$V$" + (lastRow + 1) + ",");           // Tele Switch (ALL & Last 3)
            bW.write("=$Y$" + (lastRow + 1) + ",");                                   // Exchange
            bW.write("=$AI$" + (lastRow + 1) + ",=$AJ$" + (lastRow + 1) + ",");         // Climbs (ALL & Last 3)
            String escOut = StringEscapeUtils.escapeCsv("=COUNTIF($D$" + startRow + ":$D$" + (lastRow) + ",\"Side (out)\")");
            bW.write(escOut + ",");           // Side (OUT)
            String escIn = StringEscapeUtils.escapeCsv("=COUNTIF($D$" + startRow + ":$D$" + (lastRow) + ",\"Side (in)\")");
            bW.write(escIn + ",");           // Side (IN)
            String escMid = StringEscapeUtils.escapeCsv("=COUNTIF($D$" + startRow + ":$D$" + (lastRow) + ",\"Middle\")");
            bW.write(escMid + ",");           // Middle
            //=============================
            bW.write(" " + "\n");   // End-of-Line
            prevTeam = match_inst.getTeam_num();
            startRow = (lastRow) + 2;              // Start row for new team
//            Log.w(TAG,"  Last: " + lastRow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gatherBA(String teamNo) {
        Log.w(TAG, " gatherBA  " + teamNo);
        if (BA_Data) {
            for (int i = 0; i < BAnumTeams; i++) {
//                if (BAe.teams[i].team_number == Long.parseLong(teamNo.trim())) {
//                    tmName = BAe.teams[i].nickname;
//                    tmRank = String.valueOf(BAe.teams[i].rank);
//                    tmWLT = BAe.teams[i].record;
//                    tmOPR = String.format("%3.3f", ((new TBA().fillOPR(BAe, BAe.teams[i]).opr)));
//                    Log.w(TAG, "  OPR: " + tmOPR + "  WLT " + tmWLT + "  Rank=" + tmRank + "  " + tmName);
////                    System.out.println(tmName+" "+tmRank+" "+ tmWLT+" "+tmOPR+" "+tmKPa+" "+tmTPts + " \n");
//                    break;      // exit For - found team
//                }
            } // End For
        } else {
            for (int x = 0; x < Pearadox.numTeams; x++) {
                p_Firebase.teamsObj My_inst = new p_Firebase.teamsObj();
                My_inst = Pearadox.team_List.get(x);
                if (teamNo.matches(My_inst.getTeam_num())) {
                    tmName = My_inst.getTeam_name();
                    break;
                }
            }
            tmRank = "00";
            tmWLT = "*-*-*";
            tmOPR = "000";
        }
    }

    private String removeLine(String comment) {
        String x = "";
//        ToDo - Carriage return
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
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
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
            Log.w(TAG, "FRC files created");
//        Toast toast = Toast.makeText(getBaseContext(), "FRC5414 ©2019  *** Files initialied ***" , Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast.show();
        } else {
            Toast.makeText(getBaseContext(), "There is no SD card available", Toast.LENGTH_LONG).show();
        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void findPhoto() {
        i(TAG, "###  findPhoto  ###  ");
        p_Firebase.teamsObj My_inst = new p_Firebase.teamsObj();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (int i = 0; i < Pearadox.numTeams; i++) {
            My_inst = Pearadox.team_List.get(i);
            tnum = My_inst.getTeam_num();
            teamNumber = tnum;
            StorageReference storageReference = storage.getReferenceFromUrl("gs://paradox-2017.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + tnum.trim() + ".png");
            Log.e(TAG, "gs://paradox-2017.appspot.com/images/" + Pearadox.FRC_Event + ".child(robot_" + tnum.trim() + ".png)");
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
//                    URL[0] = uri.toString();
                    photStat[0] = "Y";
//                    Log.w(TAG, "Got Photo. URL=" + URL[0]);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    photStat[0] = "N";
    //                                Log.e(TAG, "ERR= '" + exception + "'");
                }
            });
        } // end For # teams
        Log.w(TAG, "@@@@@ Photos done @@@@@  ");

    }

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    private class event_OnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String ev = parent.getItemAtPosition(pos).toString();
            Pearadox.FRC_EventName = ev;
            Log.w(TAG, ">>>>> Event '" + Pearadox.FRC_EventName + "'");
            txt_EvntCod = (TextView) findViewById(R.id.txt_EvntCod);
            txt_EvntDat = (TextView) findViewById(R.id.txt_EvntDat);
            txt_EvntPlace = (TextView) findViewById(R.id.txt_EvntPlace);
            p_Firebase.eventObj event_inst = new p_Firebase.eventObj();
            for (int i = 0; i < Pearadox.eventList.size(); i++) {
                event_inst = Pearadox.eventList.get(i);
                if (event_inst.getcomp_name().equals(ev)) {
                    Pearadox.FRC_Event = event_inst.getComp_code();
                    Pearadox.FRC_ChampDiv = event_inst.getcomp_div();
                    txt_EvntCod.setText(Pearadox.FRC_Event.toUpperCase());  // Event Code
                    txt_EvntDat.setText(event_inst.getcomp_date());         // Event Date
                    txt_EvntPlace.setText(event_inst.getcomp_place());      // Event Location
                }
            }
            Log.w(TAG, "** Event code '" + Pearadox.FRC_Event + "' " + Pearadox.FRC_ChampDiv + "  \n ");

//            Log.w(TAG, "*** TBA Event ***");
//            Event e = new TBA().getEvent("BAyear" + Pearadox.FRC_Event);
            // Print general event info
//            System.out.println(e.name);
//            System.out.println(e.location);
//            System.out.println(e.start_date);
//            System.out.println("\n");
//        txt_EvntCod.setText(Pearadox.FRC_Event.toUpperCase());  // Event Code
//        txt_EvntDat.setText(e.start_date);                      // Event Date
//        txt_EvntPlace.setText(e.location);                      // Event Location

            btn_Teams.setEnabled(true);
            btn_Match_Sched.setEnabled(true);
            btn_Rank.setEnabled(true);

            pfDatabase = FirebaseDatabase.getInstance();
            pfMatchData_DBReference = pfDatabase.getReference("match-data/" + Pearadox.FRC_Event);    // Match Data
            addMD_VE_Listener(pfMatchData_DBReference.orderByChild("team_num"));        // Load _ALL_ Matches in team order GLF 4/18

            pfTeam_DBReference = pfDatabase.getReference("teams/" + Pearadox.FRC_Event);   // Team data from Firebase D/B
            addTeam_VE_Listener(pfTeam_DBReference.orderByChild("team_num"));               // Load Teams since we now know event

            pfPitData_DBReference = pfDatabase.getReference("pit-data/" + Pearadox.FRC_Event); // Pit Scout Data
            addPitData_VE_Listener(pfPitData_DBReference.orderByChild("pit_team"));             // Load Pit Data since we now know event

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void addTeam_VE_Listener(final Query pfTeam_DBReference) {
        pfTeam_DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w(TAG, "<<<< getFB_Data >>>> Teams");
                Pearadox.team_List.clear();
                Pearadox.numTeams = 0;
                p_Firebase.teamsObj tmobj = new p_Firebase.teamsObj();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    tmobj = iterator.next().getValue(p_Firebase.teamsObj.class);
                    Pearadox.team_List.add(tmobj);
                    Pearadox.numTeams++;
                }
                if (Pearadox.numTeams == 0) {
                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                    Toast toast = Toast.makeText(getBaseContext(), "*** There are _NO_ teams loaded for '" + Pearadox.FRC_Event + "' ***", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } else {
                    Log.i(TAG, "***** Teams Loaded. # = " + Pearadox.numTeams + "  " + Pearadox.team_List.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void addMD_VE_Listener(final Query pfMatchData_DBReference) {
        pfMatchData_DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w(TAG, "<<<< addMD_VE_Listener >>>>     Match Data ");
                Pearadox.Matches_Data.clear();
                matchData mdobj = new matchData();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    mdobj = iterator.next().getValue(matchData.class);
                    Pearadox.Matches_Data.add(mdobj);
                }
                Log.w(TAG, "***** Matches Loaded from Firebase. # = " + Pearadox.Matches_Data.size());
                if (Pearadox.Matches_Data.size() > 0) {
                    Toast toast1 = Toast.makeText(getBaseContext(), "FRC5414 ©2019  *** Match Data loaded = " + Pearadox.Matches_Data.size() + " ***", Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.BOTTOM, 0, 0);
                    toast1.show();
                } else {
                    btn_Spreadsheet.setEnabled(false);
                }
                // ToDo - fix TBA code below
                // ----------  Blue Alliance  -----------
//                TBA t = new TBA();
//                BAe = new TBA().getEvent("BAyear" + Pearadox.FRC_ChampDiv);
                // TODO  is getname right
//                if (BAe.getName() == null) {
//                    Log.e(TAG, "### Data for: '" + Pearadox.FRC_ChampDiv + "' is _NOT_ available yet  ###");
//                    BA_Data = false;
//                    Toast toast2 = Toast.makeText(getBaseContext(), "### Data for: '" + Pearadox.FRC_ChampDiv + "' is _NOT_ available yet  ###", Toast.LENGTH_LONG);
//                    toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                    toast2.show();
//                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
//                    btn_Teams.setEnabled(false);
//                    btn_Match_Sched.setEnabled(false);
//                    btn_Spreadsheet.setEnabled(true);       // OK for Spreadsheet
//                    btn_Pit.setEnabled(true);               // OK for Pit
//                    btn_Rank.setEnabled(false);
//                } else {
//                    BA_Data = true;
//                    // TODO
////                    BAteams = BAe.teams.clone();
//                    BAnumTeams = BAteams.length;
//
//                    btn_Teams.setEnabled(true);
//                    btn_Match_Sched.setEnabled(true);
//                    btn_Spreadsheet.setEnabled(true);
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.action_about) {
            AboutDialog about = new AboutDialog(this);
            about.setTitle("YG_Alliance   Ver " + Pearadox_Version);
            about.show();
//            Toast toast = Toast.makeText(getBaseContext(), "Pearadox Scouting App ©2019  Ver." + Pearadox_Version, Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadEvents() {
        Log.w(TAG, "###  loadEvents  ###");

        pfDatabase = FirebaseDatabase.getInstance();
        pfEvent_DBReference = pfDatabase.getReference("competitions");      // Get list of Events/Competitions
        addEvents_VE_Listener(pfEvent_DBReference.orderByChild("comp-date"));
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void addEvents_VE_Listener(final Query pfEvent_DBReference) {
        pfEvent_DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w(TAG, "******* Firebase retrieve Competitions  *******");
                Pearadox.eventList.clear();
                Pearadox.num_Events = 0;
                p_Firebase.eventObj event_inst = new p_Firebase.eventObj();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    event_inst = iterator.next().getValue(p_Firebase.eventObj.class);
                    Log.w(TAG, "      " + event_inst.getcomp_name() + " - " + event_inst.getComp_code());
                    Pearadox.eventList.add(event_inst);
                }
                Log.w(TAG, "### Events ###  : " + Pearadox.eventList.size());
                Pearadox.num_Events = Pearadox.eventList.size() + 1;     // account for 1st blank
                Pearadox.comp_List = new String[Pearadox.num_Events];  // Re-size for spinner
                Arrays.fill(Pearadox.comp_List, null);
                Pearadox.comp_List[0] = " ";       // make it so 1st Drop-Down entry is blank
                for (int i = 0; i < Pearadox.eventList.size(); i++) {
                    event_inst = Pearadox.eventList.get(i);
                    Pearadox.comp_List[i + 1] = event_inst.getcomp_name();
                }
                Spinner spinner_Event = (Spinner) findViewById(R.id.spinner_Event);
                adapter_Event = new ArrayAdapter<String>(MainActivity.this, R.layout.list_layout, Pearadox.comp_List);
                adapter_Event.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_Event.setAdapter(adapter_Event);
                spinner_Event.setSelection(0, false);
                spinner_Event.setOnItemSelectedListener(new event_OnItemSelectedListener());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
                throw databaseError.toException();
            }
        });
    }


//========================================================
private void addPitData_VE_Listener(final Query pfPitData_DBReference) {
    pfPitData_DBReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
          Log.w(TAG, "******* Firebase get Pit Data  ******* ");
          Pearadox.Pit_Data.clear();
          Pearadox.num_Pits = 0;
          pitData pit_inst = new pitData();
          Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
          Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
          while (iterator.hasNext()) {
              pit_inst = iterator.next().getValue(pitData.class);
//              Log.w(TAG, "      " + pit_inst.getPit_team() + " - " + pit_inst.getPit_scout());
              Pearadox.Pit_Data.add(pit_inst);
              Pearadox.num_Pits ++;
          }
          Log.w(TAG, "### Pits ###  : " + Pearadox.Pit_Data.size());

//          findPhoto();      // See if Photos are there

          btn_Pit.setEnabled(true);
      }

        @Override
        public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            throw databaseError.toException();
        }
    });

                                                  }


    //______________________________________
    private void Fb_Auth() {
        Log.w(TAG, "===Fb_Auth===");
        FB_logon = false;
        String pw = " "; String eMail="scout.5414@gmail.com";
        try {
            File directFRC = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/Pearadox");
            FileReader fileReader = new FileReader(directFRC);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();
            pw = (stringBuffer.toString());
            pw = pw.substring(0,11);    //Remove CR/LF
//            Log.d(TAG, "Peardox = '" + pw + "'");
        } catch (IOException e) {
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
            Toast toast = Toast.makeText(getBaseContext(), "Firebase authentication - Password required", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            e.printStackTrace();
            Log.e(TAG, "*** Firebase Authorization failed - No Password supplied *** ");

        }
//        Log.e(TAG, "Sign-In " + eMail + "  '" + pw + "'");

        mAuth.signInWithEmailAndPassword(eMail, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "$$$  SignIn  $$$");
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success ");
                            FB_logon = true;    // show success
//                        FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                            Toast toast = Toast.makeText(getBaseContext(), "Firebase authentication failed.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                            Log.e(TAG, "*** Firebase SIgn-In Authorization failed *** ");
                        }
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
    Fb_Auth();      // Authenticate with Firebase
    loadEvents();
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
