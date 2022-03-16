package com.pearadox.yg_alliance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cpjd.main.TBA;
import com.cpjd.models.events.Event;
import com.cpjd.models.events.EventOPR;
import com.cpjd.models.events.EventRanking;
import com.cpjd.models.matches.Match;
import com.cpjd.models.teams.Team;

import com.cpjd.requests.EventRequest;
import com.cpjd.utils.exceptions.DataNotFoundException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.util.Log.i;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";        // This CLASS name
    String Pearadox_Version = " ";      // initialize
    Long Pearadox_Date;
    String TBA_AuthToken = "xgqQi9cACRSUt4xanOto70jLPxhz4lR2Mf83e2iikyR2vhOmr1Kvg1rDBlAQcOJg";
    int BAyear = 2022;  // Current Yesr for B.A. calls
    Boolean FB_logon = false;           // indicator for Firebase logon success
    Spinner spinner_Event;
    TextView txt_EvntCod, txt_EvntDat, txt_EvntPlace, txt_Time, txt_Counter;
    ArrayAdapter<String> adapter_Event;
    Button btn_Events, btn_Teams, btn_Match_Sched, btn_Rank, btn_Pit, btn_PitScout, btn_Visualizer;
    Switch switch_CyclicRank;
    String model = Build.MODEL;

    final String[] URL = {""};
    final String[] photStat = {""};
    String DatTim = "";
    String teamNumber = "";
    Team[] teams;
    public static int BAnumTeams = 0;                       // # of teams from Blue Alliance
    CountDownTimer countDownTimer;

    //    public String[] teamsRed;
//    public String[] teamsBlue;
    private FirebaseDatabase pfDatabase;
    private DatabaseReference pfEvent_DBReference;
    private DatabaseReference pfMatch_DBReference;
    private DatabaseReference pfMatchData_DBReference;
    private DatabaseReference pfTeam_DBReference;
    private DatabaseReference pfPitData_DBReference;
    private DatabaseReference pfRank_DBReference;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;

    matchData match_inst = new matchData();
    String destFile;
    String prevTeam = "";
    String Wt = "";
    String Stud = "";
    String Result = "";
    String pitData_pres = "";
    String photo_pres = "   ✔ ";
    String tnum = "";

    BufferedWriter bW;
    String tmRank = "";
    String tmRScore = "";
    String tmWLT = "";
    String tmOPR = "";
    public static String timeStamp = " ";
    public static String timeCount = " ";

//    final TBA tba = null;

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
        Toast toast = Toast.makeText(getBaseContext(), "Pearadox Yellow-Green Alliance App ©2022  Ver." + Pearadox_Version, Toast.LENGTH_LONG);
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
        btn_Visualizer = (Button) findViewById(R.id.btn_Visualizer);
        btn_Rank = (Button) findViewById(R.id.btn_Rank);
        btn_Pit = (Button) findViewById(R.id.btn_Pit);
        btn_PitScout = (Button) findViewById(R.id.btn_PitScout);
        btn_Teams.setEnabled(false);
        btn_Match_Sched.setEnabled(false);
        btn_Rank.setEnabled(false);
        btn_Pit.setEnabled(false);
        btn_Visualizer.setEnabled(false);
        txt_EvntCod = (TextView) findViewById(R.id.txt_EvntCod);
        txt_EvntDat = (TextView) findViewById(R.id.txt_EvntDat);
        txt_EvntPlace = (TextView) findViewById(R.id.txt_EvntPlace);
        txt_Time = (TextView) findViewById(R.id.txt_Time);
        Switch switch_CyclicRank = (Switch) findViewById(R.id.switch_CyclicRank);
        txt_Counter = (TextView) findViewById(R.id.txt_Counter);
        txt_EvntCod.setText("");            // Event Code
        txt_EvntDat.setText("");            // Event Date
        txt_EvntPlace.setText("");          // Event Location
        txt_Time.setText("");
        txt_Counter.setText("");

        TBA.setAuthToken(TBA_AuthToken);
        final TBA tba = new TBA();


        /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Events.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Log.w(TAG, "  btn_Events setOnClickListener  " );
            pfDatabase = FirebaseDatabase.getInstance();
            pfEvent_DBReference = pfDatabase.getReference("competitions");
            p_Firebase.eventObj new_event = new p_Firebase.eventObj();
            Log.w(TAG, " Firebase eventObj ");
            Event[] our_events = tba.getEvents(5414, BAyear);
            Log.w(TAG, " #Events = " + our_events.length);

            for (int i = 0; i < our_events.length; i++) {
                new_event.setComp_name(our_events[i].getName());
                new_event.setComp_code(our_events[i].getEventCode());
                // ToDo - How is World's Division set on B.A.??????
                new_event.setComp_div(our_events[i].getEventCode());
                new_event.setComp_date(our_events[i].getStartDate());
                new_event.setComp_place(our_events[i].getLocationName());
                new_event.setComp_city(our_events[i].getCity() + ", " + our_events[i].getStateProv());
                String keyID = new_event.getComp_code();
                pfEvent_DBReference.child(keyID).setValue(new_event);
                // ToDo Add new directories for each event  (match-data, matches,pit-data, teams)
                setUp_Rank(keyID);      // Add each new event to B.A. Rank

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
                Team[] teams = tba.getEventTeams("2022" + Pearadox.FRC_ChampDiv);
                Log.w(TAG, " Team array size = " + teams.length);
                if (teams.length > 0) {
//                    String destFile = Pearadox.FRC_ChampDiv + "_Teams" + ".json";
//                    Log.w(TAG, " filename = " + destFile);
//                    try {
//                        File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
//                        Log.e(TAG, " path = " + prt);
//                        BufferedWriter bW;
//                        bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
//                        bW.write("[" + "\n");
                        for (int i = 0; i < teams.length; i++) {
                            String tnum = String.format("%1$4s", teams[i].getTeamNumber());
                            Log.w(TAG, " Team = " + tnum);
//                            bW.write("    {    \"team_num\":\"" + tnum + "\", " + "\n");
//                            bW.write("         \"team_name\":\"" + teams[i].getNickname() + "\", " + "\n");
//                            bW.write("         \"team_loc\":\"" + (teams[i].getCity() + ", " + teams[i].getStateProv() + "  " + teams[i].getPostalCode()) + "\" " + "\n");
                            new_team.setTeam_num(tnum);
                            new_team.setTeam_name(teams[i].getNickname());
                            if (teams[i].getNickname().length() > 36) {         // for Alliance Picks list
                                Toast toast = Toast.makeText(getBaseContext(), "\n♦♦♦♦ '" + tnum + "' Team name is too long (" + teams[i].getNickname().length()  + ").  Update in Firebase  ♦♦♦♦\n", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }
                            new_team.setTeam_loc(teams[i].getCity() + ", " + teams[i].getStateProv() + "  " + teams[i].getPostalCode());
                            String keyID = String.format("%04d", teams[i].getTeamNumber());     // Make it 4 digit (only for KEY)
                            new_team.setTeam_OPR("");
                            new_team.setTeam_rank("");
                            new_team.setTeam_rScore("");
                            new_team.setTeam_WLT("");
                            pfTeam_DBReference.child(keyID).setValue(new_team);  // Add to firebase

//                            if (i == teams.length - 1) {       // Last one?
//                                bW.write("    } " + "\n");
//                            } else {
//                                bW.write("    }," + "\n");
//                            }
                        } // end For # teams
                        //=====================================================================

//                        bW.write("]" + "\n");
//                        bW.write(" " + "\n");
//                        bW.flush();
//                        bW.close();
                        Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Teams file (" + teams.length + " teams) written to Firebase ***", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
//                    } catch (FileNotFoundException ex) {
//                        System.out.println(ex.getMessage() + " not found in the specified directory.");
//                        System.exit(0);
//                    } catch (IOException e) {
//                        System.out.println(e.getMessage());
//                    }
                } else {
//                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
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
            timeStamp = new SimpleDateFormat("yyyy.MM.dd   hh:mm:ss a").format(new Date());
            Log.w(TAG, timeStamp);

            try {
                EventOPR[] opr = tba.getOprs("2022" + Pearadox.FRC_ChampDiv);
                Log.w(TAG, " OPR array size = " + opr.length);
                for (int i = 0; i < opr.length; i++) {
                    String teamKey = opr[i].getTeamKey().substring(3);
                    teamNumber = ("0000" + teamKey).substring(teamKey.length());  // leading zero(s)
                    tmOPR = String.format("%8.3f", opr[i].getOpr());
                    Log.w(TAG, "Team='" + teamNumber + "'  OPR='" + tmOPR + "'");
                    updateOPR(teamNumber);
                }  // end For # OPRs
                // ToDo - add complete child to get Event code
                txt_Time.setText(timeStamp);
                pfRank_DBReference.child(Pearadox.FRC_ChampDiv).child("last").setValue(timeStamp);
//            } catch (NullPointerException e) {
//                Log.e(TAG, " >>>> ERROR - NPE  <<<<<  " + e);
//                Toast toast = Toast.makeText(getBaseContext(), "** There is _NO_ Blue Alliance OPR data for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                toast.show();
//            }
            } catch (DataNotFoundException dnf) {
                Log.e(TAG, " >>>> ERROR <<<<<  " + dnf);
                Toast toast = Toast.makeText(getBaseContext(), "** There is _NO_ Blue Alliance OPR data for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }

            try {
                EventRanking[] rankings = new EventRequest().getEventRankings("2022" + Pearadox.FRC_ChampDiv);
//            EventRanking[] rankings = new EventRequest().getEventRankings("2018code");  // Event _MUST_ be lower case!!
                Log.w(TAG, " Rank array size = " + rankings.length);
                for (int i = 0; i < rankings.length; i++) {
                    String teamKey  = rankings[i].getTeamKey().substring(3);
                    teamNumber = ("0000" + teamKey).substring(teamKey.length());  // leading zero(s)
                    tmWLT = String.valueOf(rankings[i].getWins()) + "-" + String.valueOf(rankings[i].getLosses()) + "-" + String.valueOf(rankings[i].getTies());
                    tmRank = String.valueOf(rankings[i].getRank());
                    double[] sortOrd = rankings[i].getSortOrders();
                    tmRScore = String.valueOf(sortOrd[0]);
                    Log.w(TAG, teamNumber + "  Rank: " + tmRank + "  Score:" + tmRScore + "  WLT: " + tmWLT  );
                    updateRank(teamNumber);
                }
//           btn_Rank.setEnabled(false);         // Turn off Button
            } catch (DataNotFoundException e) {
                Log.e(TAG, " >>>> ERROR <<<<<  " + e);
//                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//                tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                Toast toast = Toast.makeText(getBaseContext(), "** There is _NO_ Blue Alliance Ranking data for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
            catch (NullPointerException npe) {
                Log.e(TAG, " >>>> ERROR <<<<<  " + npe);
//            tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                Toast toast = Toast.makeText(getBaseContext(), "** There is _NO_ Blue Alliance Ranking data for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        }
    });


        /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Visualizer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Visualizer setOnClickListener  " + Pearadox.FRC_ChampDiv);
                Intent viz_intent = new Intent(MainActivity.this, Visualizer_Activity.class);
                Bundle VZbundle = new Bundle();
                VZbundle.putString("dev", "ScoutMaster");               // Pass data
                VZbundle.putString("stud", "Lead Scout");               //  to activity
                viz_intent.putExtras(VZbundle);
                startActivity(viz_intent);                        // Start Visualizer
            }
        });


        switch_CyclicRank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
            Log.w(TAG, "*** switch_CyclicRank - setOnCheckedChangeListener *** ");
            if (bChecked) {
                txt_Counter.setText("ON");
                usingCountDownTimer();
            } else {
                txt_Counter.setText("OFF");
                try {
                    Log.d(TAG, "*** Stopping Counter ***");
                    countDownTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    });


/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Pit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Pit setOnClickListener  " + Pearadox.FRC_ChampDiv);
                pitData the_pits = new pitData();
                p_Firebase.teamsObj My_inst = new p_Firebase.teamsObj();
                Log.w(TAG, " Team array size = " + Pearadox.numTeams);
// #######################  Don't Write file to SD card  #############################
//                if (Pearadox.numTeams > 0) {
//                    String destFile = Pearadox.FRC_ChampDiv + "_PitData" + ".txt";
//                    Log.w(TAG, " filename = " + destFile);
//                    try {
//                        File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
//                        Log.e(TAG, " path = " + prt);
//                        BufferedWriter bW;
//                        bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
//                        bW.write("TEAM" + "  " + "PIT  PHOTO  _____Date/Time_____       SCOUT" + "\n");
//
//                        for (int i = 0; i < Pearadox.numTeams; i++) {
//                            pitData_pres = " -  ";       // Not there
//                            photo_pres = "  - ";
//                            Wt = "  ";
//                            Stud = "";
//                            DatTim = "";
//                            My_inst = Pearadox.team_List.get(i);
//                            tnum = My_inst.getTeam_num();
////                        Log.w(TAG, " Team# = '" + tnum + "'  Pit=" + num_Pits) ;
//
//                            // Find Pit Data (if there)
//                            for (int x = 0; x < Pearadox.num_Pits; x++) {
//                                the_pits = Pearadox.Pit_Data.get(x);
////                            Log.w(TAG, " Team# = '" + tnum + "'  and '" + the_pits.getPit_team() + "'") ;
//
//                                if (the_pits.getPit_team().matches(tnum)) {
//                                    pitData_pres = " ✔";
//                                    Wt = String.format("%1$2s", the_pits.getPit_weight());
//                                    DatTim = the_pits.getPit_dateTime();
//                                    Stud = the_pits.getPit_scout();
////                                Log.w(TAG, "Ht=" + Ht + "  Scout=" + Stud);
//                                    String photoStatus = the_pits.getPit_photoURL();
//                                    Log.w(TAG, "%%%%%%%%% Status = " + photoStatus);
//                                    if (TextUtils.isEmpty(photoStatus)) {
//                                        photo_pres = "❌";
//                                    } else {
//                                        photo_pres = " ✔";
//                                    }
//                                } // Endif
//                            } //End for #pits
//                            Result = tnum + "   " + pitData_pres + "    " + photo_pres + "  Wt=" + Wt + "  " + DatTim + "  " + Stud;
//
//                            bW.write(Result + "\n");
//                        } // end For # teams
//                        //=====================================================================
//
//                        bW.write(" " + "\n");
//                        bW.flush();
//                        bW.close();
//                        Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Pit Data Coverage file (" + Pearadox.numTeams + " teams) written to SD card ***", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                        toast.show();
//                    } catch (FileNotFoundException ex) {
//                        System.out.println(ex.getMessage() + " not found in the specified directory.");
//                        System.exit(0);
//                    } catch (IOException e) {
//                        System.out.println(e.getMessage());
//                    }
//                } else {
////                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
////                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
//                    Toast toast = Toast.makeText(getBaseContext(), "** There are _NO_ teams for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                    toast.show();
//                }
                btn_Pit.setEnabled(false);         // Turn off Button

                Intent pit_intent = new Intent(MainActivity.this, PitCover_Activity.class);
                startActivity(pit_intent);
            }
        });


        /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_PitScout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Log.w(TAG, "  btn_PitScout setOnClickListener  " + Pearadox.FRC_ChampDiv);
                p_Firebase.teamsObj My_inst = new p_Firebase.teamsObj();
                String tnumB1=""; String tnumB2=""; String tnumB3=""; String tnumR1=""; String tnumR2=""; String tnumR3="";
                String tnamB1=""; String tnamB2=""; String tnamB3=""; String tnamR1=""; String tnamR2=""; String tnamR3="";
                Log.w(TAG, " Team array size = " + Pearadox.numTeams);
                if (Pearadox.numTeams > 0) {
                String destFile = Pearadox.FRC_ChampDiv.toUpperCase() + "_PitScout_Assign" + ".csv";
                Log.w(TAG, " filename = " + destFile);
                try {
                    File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
                    Log.e(TAG, " path = " + prt);
                    BufferedWriter bW;
                    bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
                    bW.write("Blue 1" + "," + "Blue 2" + "," + "Blue 3" + "," +"Red 1"  + ","+ "Red 2" + "," + "Red 3" + "\n");   // Header
                    int loop = Pearadox.numTeams / 6;
                    int rem = Pearadox.numTeams % 6;
                    Log.w(TAG, " loop = " + loop + "Rem=" + rem);
                    for (int i = 0; i < Pearadox.numTeams; i=i+6) {
                        Log.w(TAG, "In For   i=" + i);
                        if (i + 6 <= Pearadox.numTeams) {       // protect against NOT multiple of 6
                            My_inst = Pearadox.team_List.get(i);     tnumB1 = My_inst.getTeam_num(); tnamB1 = My_inst.getTeam_name();
                            My_inst = Pearadox.team_List.get(i + 1); tnumB2 = My_inst.getTeam_num(); tnamB2 = My_inst.getTeam_name();
                            My_inst = Pearadox.team_List.get(i + 2); tnumB3 = My_inst.getTeam_num(); tnamB3 = My_inst.getTeam_name();
                            My_inst = Pearadox.team_List.get(i + 3); tnumR1 = My_inst.getTeam_num(); tnamR1 = My_inst.getTeam_name();
                            My_inst = Pearadox.team_List.get(i + 4); tnumR2 = My_inst.getTeam_num(); tnamR2 = My_inst.getTeam_name();
                            My_inst = Pearadox.team_List.get(i + 5); tnumR3 = My_inst.getTeam_num(); tnamR3 = My_inst.getTeam_name();
                            bW.write(tnumB1 + "-" + tnamB1 + "," + tnumB2 + "-" + tnamB2 + "," + tnumB3 + "-" + tnamB3 + ",");
                            bW.write(tnumR1 + "-" + tnamR1 + "," + tnumR2 + "-" + tnamR2 + "," + tnumR3 + "-" + tnamR3 + "\n");
                        } else {
                            switch(rem) {
                                case 1 :
                                    My_inst = Pearadox.team_List.get(i);     tnumB1 = My_inst.getTeam_num(); tnamB1 = My_inst.getTeam_name();
                                    bW.write(tnumB1 + "-" + tnamB1 + "," + " " + "," + " " + ",");
                                    bW.write(" " + "," + " " + "," + " " + "\n");
                                    break;
                                case 2 :
                                    My_inst = Pearadox.team_List.get(i);     tnumB1 = My_inst.getTeam_num(); tnamB1 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 1); tnumB2 = My_inst.getTeam_num(); tnamB2 = My_inst.getTeam_name();
                                    bW.write(tnumB1 + "-" + tnamB1 + "," + tnumB2 + "-" + tnamB2 + "," + " " + ",");
                                    bW.write(" " + "," + " " + "," + " " + "\n");
                                    break;
                                case 3 :
                                    My_inst = Pearadox.team_List.get(i);     tnumB1 = My_inst.getTeam_num(); tnamB1 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 1); tnumB2 = My_inst.getTeam_num(); tnamB2 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 2); tnumB3 = My_inst.getTeam_num(); tnamB3 = My_inst.getTeam_name();
                                    bW.write(tnumB1 + "-" + tnamB1 + "," + tnumB2 + "-" + tnamB2 + "," + tnumB3 + "-" + tnamB3 + ",");
                                    bW.write(" " + "," + " " + "," + " " + "\n");
                                    break;
                                case 4 :
                                    My_inst = Pearadox.team_List.get(i);     tnumB1 = My_inst.getTeam_num(); tnamB1 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 1); tnumB2 = My_inst.getTeam_num(); tnamB2 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 2); tnumB3 = My_inst.getTeam_num(); tnamB3 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 3); tnumR1 = My_inst.getTeam_num(); tnamR1 = My_inst.getTeam_name();
                                    bW.write(tnumB1 + "-" + tnamB1 + "," + tnumB2 + "-" + tnamB2 + "," + tnumB3 + "-" + tnamB3 + ",");
                                    bW.write(tnumR1 + "-" + tnamR1 + "," + " " + "," + " " + "\n");
                                    break;
                                case 5 :
                                    My_inst = Pearadox.team_List.get(i);     tnumB1 = My_inst.getTeam_num(); tnamB1 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 1); tnumB2 = My_inst.getTeam_num(); tnamB2 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 2); tnumB3 = My_inst.getTeam_num(); tnamB3 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 3); tnumR1 = My_inst.getTeam_num(); tnamR1 = My_inst.getTeam_name();
                                    My_inst = Pearadox.team_List.get(i + 4); tnumR2 = My_inst.getTeam_num(); tnamR2 = My_inst.getTeam_name();
                                    bW.write(tnumB1 + "-" + tnamB1 + "," + tnumB2 + "-" + tnamB2 + "," + tnumB3 + "-" + tnamB3 + ",");
                                    bW.write(tnumR1 + "-" + tnamR1 + "," + tnumR2 + "-" + tnamR2 + "," + " " + "\n");
                                    break;
                                default :
                                    Log.e(TAG, "<<<<<<<  ERROR!!  >>>>>>> = " + "Rem=" + rem);
                            }
                        }
                    } // end For # teams
                    //=====================================================================

                    bW.write(" " + "\n");
                    bW.flush();
                    bW.close();
                    Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Pit Data Assignment CSV file (" + Pearadox.numTeams + " teams) written to SD card ***", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage() + " not found in the specified directory.");
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
//                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//                tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                Toast toast = Toast.makeText(getBaseContext(), "** There are _NO_ teams for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
            btn_PitScout.setEnabled(false);         // Turn off Button
        }
    });


/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Match_Sched.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Match_Sched setOnClickListener  ");
                try {
                    Match[] matchSched = tba.getMatches("2022" + Pearadox.FRC_ChampDiv);
    //                Match[] matchSched = tba.getMatches("2018code");          // ***DEBUG***
                    Log.w(TAG, " Matches size = " + matchSched.length);
                    pfMatch_DBReference = pfDatabase.getReference("matches/" + Pearadox.FRC_Event);   // Matches data from Firebase D/B


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
                            Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Matches file written to Firebase & SD card *** " + qm, Toast.LENGTH_LONG);
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
                } catch (DataNotFoundException e) {
                    Log.e(TAG, " >>>> ERROR <<<<<  " + e);
                    Toast toast = Toast.makeText(getBaseContext(), "** There is _NO_ Blue Alliance data for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
        });


    }   // end OnCreate

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

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
//        Toast toast = Toast.makeText(getBaseContext(), "FRC5414 ©2022  *** Files initialied ***" , Toast.LENGTH_LONG);
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
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    private void updateOPR(String teamNumber) {
        Log.w(TAG, "  updateOPR  " + Pearadox.FRC_ChampDiv + "  " + teamNumber);
        pfTeam_DBReference = pfDatabase.getReference("teams/" + Pearadox.FRC_Event);   // Team data from Firebase D/B
        String keyID = teamNumber;
        pfTeam_DBReference.child(keyID).child("team_OPR").setValue(tmOPR);
    }

    private void updateRank(String teamNumber) {
        Log.w(TAG, "  updateRank  " + Pearadox.FRC_ChampDiv + "  " + teamNumber);
        pfTeam_DBReference = pfDatabase.getReference("teams/" + Pearadox.FRC_Event);   // Team data from Firebase D/B
        String keyID = teamNumber;
        pfTeam_DBReference.child(keyID).child("team_rank").setValue(tmRank);
        pfTeam_DBReference.child(keyID).child("team_rScore").setValue(tmRScore);
        pfTeam_DBReference.child(keyID).child("team_WLT").setValue(tmWLT);
    }

    private void setUp_Rank(String event) {
        Log.w(TAG, "  setUp_Rank  " + event);
        pfRank_DBReference = pfDatabase.getReference("rank");   // B.A. Rank Timestamp Data
        String keyID = event;
        pfRank_DBReference.child(keyID).child("event").setValue(event);
        pfRank_DBReference.child(keyID).child("last").setValue("2022.01.01   00:00:01 AM");
    }


    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
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
                if (event_inst.getComp_name().equals(ev)) {
                    Pearadox.FRC_Event = event_inst.getComp_code();
                    Pearadox.FRC_ChampDiv = event_inst.getComp_div();
                    txt_EvntCod.setText(Pearadox.FRC_Event.toUpperCase());  // Event Code
                    txt_EvntDat.setText(event_inst.getComp_date());         // Event Date
                    txt_EvntPlace.setText(event_inst.getComp_place());      // Event Location

                }
            }
            Log.w(TAG, "** Event code '" + Pearadox.FRC_Event + "' " + Pearadox.FRC_ChampDiv + "  \n ");

            btn_Teams.setEnabled(true);
            btn_Match_Sched.setEnabled(true);
            btn_PitScout.setEnabled(true);
            btn_Rank.setEnabled(true);
            if (model.equals("K88")) {
                btn_Visualizer.setEnabled(true);
            } else {
                btn_Visualizer.setText("Not on Phone");
                btn_Visualizer.setEnabled(false);
            }

            pfDatabase = FirebaseDatabase.getInstance();
            pfMatchData_DBReference = pfDatabase.getReference("match-data/" + Pearadox.FRC_Event);    // Match Data
//            addMD_VE_Listener(pfMatchData_DBReference.orderByChild("team_num"));        // Load _ALL_ Matches in team order for spreadsheet  [GLF 4/18]

            pfTeam_DBReference = pfDatabase.getReference("teams/" + Pearadox.FRC_Event);   // Team data from Firebase D/B
            addTeam_VE_Listener(pfTeam_DBReference.orderByChild("team_num"));               // Load Teams since we now know event

            pfPitData_DBReference = pfDatabase.getReference("pit-data/" + Pearadox.FRC_Event); // Pit Scout Data
            addPitData_VE_Listener(pfPitData_DBReference.orderByChild("pit_team"));             // Load Pit Data since we now know event

            pfRank_DBReference = pfDatabase.getReference("rank"); // Rank Timestamp Data
            String child = "event";
            String key = Pearadox.FRC_Event;
            Query query = pfRank_DBReference.orderByChild(child).equalTo(key);
            final ChildEventListener childEventListener = query.addChildEventListener(new ChildEventListener() {
                private DataSnapshot dataSnapshot;

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    this.dataSnapshot = dataSnapshot;
                    Log.w(TAG, "%%%  ChildAdded");
                    p_Firebase.rankObj rank = dataSnapshot.getValue(p_Firebase.rankObj.class);
                    System.out.println(dataSnapshot.getValue());
                    txt_Time.setText(rank.getLast());           // Last B.A. Rank execution

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.w(TAG, "%%%  ChildChanged");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.w(TAG, "%%%  ChildRemoved");
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.w(TAG, "%%%  ChildMoved");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

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
//                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
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
                    Toast toast1 = Toast.makeText(getBaseContext(), "FRC5414 ©2022  *** Match Data loaded = " + Pearadox.Matches_Data.size() + " ***", Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.BOTTOM, 0, 0);
                    toast1.show();
                } else {
//                    btn_Spreadsheet.setEnabled(false);
                }
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

        if (id == R.id.action_about) {
            AboutDialog about = new AboutDialog(this);
            about.setTitle("YG_Alliance   Ver " + Pearadox_Version);
            about.show();
            return true;
        }
        if (id == R.id.action_ver) {
            Toast toast = Toast.makeText(getBaseContext(), "Pearadox YG_Alliance App ©2022  Ver." + Pearadox_Version, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
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
                    Log.w(TAG, "      " + event_inst.getComp_name() + " - " + event_inst.getComp_code());
                    Pearadox.eventList.add(event_inst);
                }
                Log.w(TAG, "### Events ###  : " + Pearadox.eventList.size());
                Pearadox.num_Events = Pearadox.eventList.size() + 1;     // account for 1st blank
                Pearadox.comp_List = new String[Pearadox.num_Events];  // Re-size for spinner
                Arrays.fill(Pearadox.comp_List, null);
                Pearadox.comp_List[0] = " ";       // make it so 1st Drop-Down entry is blank
                for (int i = 0; i < Pearadox.eventList.size(); i++) {
                    event_inst = Pearadox.eventList.get(i);
                    Pearadox.comp_List[i + 1] = event_inst.getComp_name();
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


    public void usingCountDownTimer() {
            countDownTimer = new CountDownTimer(Long.MAX_VALUE, 600000) {       // 10 min
//            countDownTimer = new CountDownTimer(Long.MAX_VALUE, 300000) {       // 5 min
//            countDownTimer = new CountDownTimer(Long.MAX_VALUE, 15000) {       // 15 sec

            // This is called after every 10 sec interval.
            public void onTick(long millisUntilFinished) {
                btn_Rank.performClick();    // run the Rank requests
                txt_Counter = (TextView) findViewById(R.id.txt_Counter);
                timeCount = new SimpleDateFormat("hh:mm:ss a").format(new Date());
                txt_Counter.setText(timeCount);
            }

            public void onFinish() {
                start();
            }
        }.start();
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
            Log.d(TAG, "stringBuffer = " +stringBuffer.length());
//           pw = (stringBuffer.toString());
//           pw = pw.substring(0,11);    //Remove CR/LF
            pw = "pear@5414%$";  // **DEBUG** hardcode for now
        } catch (IOException e) {
//            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
//            Toast toast = Toast.makeText(getBaseContext(), "Firebase authentication - Password required", Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//            toast.show();
            Log.e(TAG, "*** Firebase Authorization failed - No Password supplied *** ");

        }
        Log.e(TAG, "Sign-In " + eMail + "  '" + pw + "'");

        mAuth.signInWithEmailAndPassword(eMail, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "$$$  SignIn  $$$  " + eMail );
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success ");
                            FB_logon = true;    // show success
//                        FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure (" + eMail + ") ", task.getException());
//                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//                            tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
//                            Toast toast = Toast.makeText(getBaseContext(), "Firebase authenticSign-In Authorizationation failed for:  " + eMail , Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                            toast.show();
                            Log.e(TAG, "*** Firebase Sign-In Authorization failed *** ");
                        }
                    }
                });
        loadEvents();

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

//###################################################################
//###################################################################
//###################################################################
@Override
public void onStart() {
    super.onStart();
    Log.v(TAG, ">>>>  yg_alliance onStart  <<<<");
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
        // Permission is not granted
        Log.w(TAG, "*** Not authorized for SD card ***");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                99);
    }
    FirebaseApp.initializeApp(this);
    Fb_Auth();      // Authenticate with Firebase
//    loadEvents();
}

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
     }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        try {
//            Log.d(TAG, "*** Stopping Counter ***");
//            countDownTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
