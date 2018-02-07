package com.pearadox.yg_alliance;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpjd.main.Settings;
import com.cpjd.main.TBA;
import com.cpjd.models.Event;
import com.cpjd.models.Match;
import com.cpjd.models.Team;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
// API-V3
//import main.Settings;
//import main.TBA;
//import main.CTBA;
//import models.other.events.EventOPR;
//import models.simple.SEvent;
//import models.simple.SMatch;
//import models.simple.STeam;
//import models.standard.Event;
//import models.standard.Match;
//import models.standard.Team;
// === DEBUG  ===


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";        // This CLASS name
    String Pearadox_Version = " ";      // initialize
    Boolean FB_logon = false;           // indicator for Firebase logon success
    Boolean BA_Data = false;
    Spinner spinner_Device, spinner_Event;
    TextView txt_EvntCod, txt_EvntDat, txt_EvntPlace;
    ArrayAdapter<String> adapter_Event;
    Button btn_Teams, btn_Match_Sched, btn_Spreadsheet;
    Team[] BAteams;
    public static int BAnumTeams = 0;                      // # of teams from Blue Alliance
    public String[] teamsRed;
    public String[] teamsBlue;
    private FirebaseDatabase pfDatabase;
    private DatabaseReference pfEvent_DBReference;
    private DatabaseReference pfMatchData_DBReference;
    private FirebaseAuth mAuth;
    matchData match_inst = new matchData();
    String destFile;
    String prevTeam ="";
    int startRow = 3; int lastRow = 0;
    BufferedWriter bW;
    String tmName=""; String tmRank=""; String tmWLT=""; String tmOPR=""; String tmKPa=""; String tmTPts="";
    Event BAe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w(TAG, "******* Starting Yellow-Green Alliance  *******");

        try {
            Pearadox_Version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        Toast toast = Toast.makeText(getBaseContext(), "Pearadox Yellow-Green Alliance App ©2018  Ver." + Pearadox_Version, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        mAuth = FirebaseAuth.getInstance();

        preReqs(); 				        // Check for pre-requisites

        Spinner spinner_Event = (Spinner) findViewById(R.id.spinner_Event);

        btn_Teams = (Button) findViewById(R.id.btn_Teams);
        btn_Match_Sched = (Button) findViewById(R.id.btn_Match_Sched);
        btn_Spreadsheet = (Button) findViewById(R.id.btn_Spreadsheet);
        btn_Teams.setEnabled(false);
        btn_Match_Sched.setEnabled(false);
        btn_Spreadsheet.setEnabled(false);
        txt_EvntCod = (TextView) findViewById(R.id.txt_EvntCod);
        txt_EvntDat = (TextView) findViewById(R.id.txt_EvntDat);
        txt_EvntPlace = (TextView) findViewById(R.id.txt_EvntPlace);
        txt_EvntCod.setText("");            // Event Code
        txt_EvntDat.setText("");            // Event Date
        txt_EvntPlace.setText("");          // Event Location

//        TBA.setAuthToken("xgqQi9cACRSUt4xanOto70jLPxhz4lR2Mf83e2iikyR2vhOmr1Kvg1rDBlAQcOJg");
        TBA.setID("Pearadox", "YG_Alliance", "V1");
        final TBA tba = new TBA();
        Settings.FIND_TEAM_RANKINGS = true;
        Settings.GET_EVENT_TEAMS = true;
        Settings.GET_EVENT_MATCHES = true;
//        Settings.GET_EVENT_ALLIANCES = true;
//        Settings.GET_EVENT_AWARDS = true;


/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Teams.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Teams setOnClickListener  " + Pearadox.FRC_ChampDiv);

                Team[] teams = tba.getTeams(Pearadox.FRC_ChampDiv, 2018);
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
                            String tnum = String.format("%1$4s", teams[i].team_number);
                            Log.w(TAG, " Team = " + tnum);
                            bW.write("    {    \"team_num\":\"" + tnum + "\", " + "\n");
                            bW.write("         \"team_name\":\"" + teams[i].nickname + "\", " + "\n");
                            bW.write("         \"team_loc\":\"" + teams[i].location + "\" " + "\n");

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
                }else {
                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                    Toast toast = Toast.makeText(getBaseContext(), "** There are _NO_ teams for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            }
        });

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        btn_Match_Sched.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.w(TAG, "  btn_Match_Sched setOnClickListener  ");
                Event event = new TBA().getEvent("2018" + Pearadox.FRC_ChampDiv);       // GLF 4/12
//                Event event = new TBA().getEvent("2017" + "txlu");       // **DEBUG testing **  hard coded yr/event
                Match[] matches = event.matches;
                Log.w(TAG, " Matches size = " + matches.length);

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
                    tg.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK);
                    Toast toast = Toast.makeText(getBaseContext(), "***  Data from the Blue Alliance is _NOT_ available this session  ***", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                //----------------------------------------
                int qm = 0;
                String mn, r1, r2, r3, b1, b2, b3;
                String matchFile = Pearadox.FRC_ChampDiv + "_Match-Sched" + ".json";
                if (matches.length > 0) {
                    // The comp level variable includes an indentifier for whether it's practice, qualifying, or playoff
                    try {
                        File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + matchFile);
                        BufferedWriter bW;
                        bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
                        bW.write("[" + "\n");
                        for (int i = 0; i < matches.length; i++) {
//                            Log.w(TAG, " Comp = " + matches[i].comp_level);
                            if (matches[i].comp_level.matches("qm")) {
                                qm ++;
                                bW.write(" {\"time\":\"" + matches[i].time_string + "\", ");
                                mn = String.valueOf(matches[i].match_number);
                                if (mn.length() < 2) {mn = "00" + mn;}      // make it at least 3-digits
                                if (mn.length() < 3) {mn = "0" + mn;}       // make it at least 3-digits
                                Log.w(TAG, " match# = " + mn);
                                bW.write("  \"mtype\":\"Qualifying\",  \"match\": \"Q" + mn + "\", ");
                                teamsRed = matches[i].redTeams.clone();
                                r1 = teamsRed[0].substring(3, teamsRed[0].length());
                                if (r1.length() < 4) {r1 = " " + r1;}
                                Log.w(TAG, " R1 = " + r1);
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
                                Log.e(TAG, "******* NOT 'qm' ********* " );
//                                System.out.println(matches[i].set_number);
//                                System.out.println(matches[i].event_key);
//                                System.out.println(matches[i].time_string);
//                                System.out.println(matches[i].key);
                            }
                        }  // end For # matches
                        //=====================================================================

                        bW.write("]" + "\n");
                        bW.write(" " + "\n");
                        bW.flush();
                        bW.close();
                        Log.w(TAG, qm + " *** '" + Pearadox.FRC_Event + "' Matches written to SD card ***");
                        Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event + "' Matches file written to SD card *** " + qm , Toast.LENGTH_LONG);
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
        Log.w(TAG, "  btn_Spreadsheet setOnClickListener  ");
        Log.e(TAG, "***** Matches # = "  + Pearadox.Matches_Data.size());   // Done in Event Click Listner
//        Toast toast1 = Toast.makeText(getBaseContext(), "FRC5414 ©2018  *** Match Data loaded = " + Pearadox.Matches_Data.size() + " ***" , Toast.LENGTH_LONG);
//        toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast1.show();
        String new_comm="";

        destFile = Pearadox.FRC_Event + "_MatchData" + ".csv";
        try {
            File prt = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + destFile);
            bW = new BufferedWriter(new FileWriter(prt, false));    // true = Append to existing file
            bW.write(Pearadox.FRC_Event.toUpperCase() + " - " + Pearadox.FRC_EventName +"  \n");
            // Write Excel/Spreadsheet Header for each column
            bW.write("Team,Match,Carry Cube,Start Pos,_NO_ Auto Mode,Crossed Baseline?,");
            bW.write("Cube Switch,Switch Attempted,Switch X-Over,Extra Cube,Cube Scale,Scale Attempted,Scale X-Over,Wrong Switch,Wrong Scale,");
            bW.write("Auto Comment,|,");

            bW.write("Cube Switch,Switch Attempted,Cube Scale,Scale Attempted,Their Switch,Their Attempted,");
            bW.write("Exchange,Portal,Power Zone,Our Floor,Their Floor,P/U Cubes,Place,Launch,");
            bW.write("On Platform,Climb Success?,Climb Attempt?,Rung,Side,Lift-1,Lift-2,Was Lifted,Tele Comment,|,");

            bW.write("Lost Parts?,Lost Comms?,Good Def?,Lane?,Blocking?,Switch Block?,");
            bW.write("Num Penalties,Date-Time Saved,Final Comment,||,Scout-Last,Scout-First");
            bW.write("Team,Rank,W-L-T,OPR,||");
            bW.write(",Weighted ALL,Weighted Last-3,Auto Switch ALL,Auto Switch Last-3,Auto Scale ALL,Auto Scale Last-3,Tele Switch ALL,Tele Switch Last-3,Tele Scale ALL,Tele Scale Last-3,");
            bW.write("Exchange,Climbs ALL,Climbs Last-3");

            bW.write(" " + "\n");
            prevTeam ="";
            //=====================================================================
            for (int i = 0; i < Pearadox.Matches_Data.size(); i++) {
                match_inst = Pearadox.Matches_Data.get(i);      // Get instance of Match Data
                if (!match_inst.getTeam_num().matches(prevTeam)) {      // Same team?
                    if (i > 0) {
//                        Log.w(TAG, "Prev: " + prevTeam + "  New: " + match_inst.getTeam_num() + "  Start: " + startRow + "  i=" + i);
                        wrtHdr();
                    }  else {
                        prevTeam = match_inst.getTeam_num();
                    }
                    lastRow = startRow - 1;
                }
                bW.write(match_inst.getTeam_num() + "," + match_inst.getMatch() + "," );
                //----- Auto -----
                bW.write(match_inst.isPre_cube() + "," + match_inst.getPre_startPos() + ","  + match_inst.isAuto_mode() + "," + match_inst.isAuto_baseline() + ",");
                bW.write(match_inst.isAuto_cube_switch() + "," + match_inst.isAuto_cube_switch_att() + "," + match_inst.isAuto_xover_switch()  + "," + match_inst.isAuto_switch_extra()  + "," );
                bW.write(match_inst.isAuto_cube_scale() + "," + match_inst.isAuto_cube_scale_att() + "," + match_inst.isAuto_xover_scale()  + "," + match_inst.isAuto_wrong_switch()  + ","  + match_inst.isAuto_wrong_scale()  + ",");
                new_comm = StringEscapeUtils.escapeCsv(match_inst.getAuto_comment());
                bW.write(new_comm + "," + "|" + ",");
                //----- Tele -----
                bW.write(match_inst.getTele_cube_switch() + "," + match_inst.getTele_switch_attempt() + "," + match_inst.getTele_cube_scale() + "," + match_inst.getTele_scale_attempt() + "," + match_inst.getTele_their_switch() + "," + match_inst.getTele_their_attempt() + ",");
                bW.write(match_inst.getTele_cube_exchange() + "," + match_inst.getTele_cube_portal() + "," + match_inst.getTele_cube_pwrzone() + "," + match_inst.getTele_cube_floor() + "," + match_inst.getTele_their_floor() + ","  + match_inst.isTele_cube_pickup() + "," + match_inst.isTele_placed_cube() + ","  + match_inst.isTele_launched_cube() + "," );
//                String y = match_inst.getTele_comment();
                new_comm = StringEscapeUtils.escapeCsv(match_inst.getTele_comment());
                bW.write(match_inst.isTele_on_platform() + "," + match_inst.isTele_climb_success() + "," + match_inst.isTele_climb_attempt() + "," + match_inst.isTele_grab_rung() + "," + match_inst.isTele_grab_side() + "," + match_inst.isTele_lift_one() + "," + match_inst.isTele_lift_two() + "," + match_inst.isTele_got_lift() + ","   + new_comm + "," + "|" + ",");
                //----- Final -----
                bW.write(match_inst.isFinal_lostParts() + "," + match_inst.isFinal_lostComms() + "," + match_inst.isFinal_defense_good() + "," + match_inst.isFinal_def_Lane() + "," + match_inst.isFinal_def_Block() + "," + match_inst.isFinal_def_BlockSwitch() + ",");
//                String x = match_inst.getFinal_comment();
                new_comm = StringEscapeUtils.escapeCsv(match_inst.getFinal_comment());
                bW.write(match_inst.getFinal_num_Penalties() + "," + match_inst.getFinal_dateTime() + "," + new_comm + "," + "||" + "," + match_inst.getFinal_studID() + ",|,,,,,,,|");
                //-----------------
                bW.write(" " + "\n");
                lastRow = lastRow + 1;
//                Log.w(TAG, match_inst.getTeam_num() + "  Last: " + lastRow);
                if (i == Pearadox.Matches_Data.size() -1) {       // Last one?
                    wrtHdr();
                }
            } // End For

            //=====================================================================
            bW.write(" " + "\n");
            bW.flush();
            bW.close();
            Toast toast = Toast.makeText(getBaseContext(), "*** '" + Pearadox.FRC_Event.toUpperCase() + "' Match Data Spreadsheet written to SD card ***" , Toast.LENGTH_LONG);
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
            bW.write(",'Bool% >>," + esc$E + "/" + ((lastRow-startRow)+1) + "," + esc$XT + "/" + ((lastRow-startRow)+1));  // crossed ÷ #matches
            String esc$G = StringEscapeUtils.escapeCsv("=(COUNTIF($G" + startRow + ":$G" + lastRow + ",TRUE))");
            String esc$H = StringEscapeUtils.escapeCsv("=(COUNTIF($H" + startRow + ":$H" + lastRow + ",TRUE))");
            String esc$I = StringEscapeUtils.escapeCsv("=(COUNTIF($I" + startRow + ":$I" + lastRow + ",TRUE))");
            String esc$J = StringEscapeUtils.escapeCsv("=(COUNTIF($J" + startRow + ":$J" + lastRow + ",TRUE))");
            String esc$K = StringEscapeUtils.escapeCsv("=(COUNTIF($K" + startRow + ":$K" + lastRow + ",TRUE))");
            String esc$L = StringEscapeUtils.escapeCsv("=(COUNTIF($L" + startRow + ":$L" + lastRow + ",TRUE))");
            String esc$M = StringEscapeUtils.escapeCsv("=(COUNTIF($M" + startRow + ":$M" + lastRow + ",TRUE))");
            bW.write("," + esc$G + "," + esc$H + "," + esc$I + "/" + ((lastRow-startRow)+1) +  "," + esc$J  + "/" + ((lastRow-startRow)+1)+  "," + esc$K+  "," + esc$L + "," + esc$M + "/" + ((lastRow-startRow)+1));
            String esc$N = StringEscapeUtils.escapeCsv("=(COUNTIF($N" + startRow + ":$N" + lastRow + ",TRUE))");
            String esc$O = StringEscapeUtils.escapeCsv("=(COUNTIF($O" + startRow + ":$O" + lastRow + ",TRUE))");
            bW.write("," + esc$N + "/" + ((lastRow-startRow)+1) +  "," + esc$O  + "/" + ((lastRow-startRow)+1)+  ",,|");

            /*  Tele  */
            String escR = StringEscapeUtils.escapeCsv("=IF(SUM($S" + startRow + ":$S" + lastRow +")>0,SUM($R" + startRow + ":$R" + lastRow + ")/SUM($S" + startRow + ":$S" + lastRow + "),0)");
            String escT = StringEscapeUtils.escapeCsv("=IF(SUM($U" + startRow + ":$U" + lastRow +")>0,SUM($T" + startRow + ":$T" + lastRow + ")/SUM($U" + startRow + ":$U" + lastRow + "),0)");
            String escV = StringEscapeUtils.escapeCsv("=IF(SUM($W" + startRow + ":$W" + lastRow +")>0,SUM($V" + startRow + ":$V" + lastRow + ")/SUM($W" + startRow + ":$W" + lastRow + "),0)");
            String escS = "";String escU = "";String escW= "";
            if (lastRow-startRow >= 2)  {
                escS = StringEscapeUtils.escapeCsv("=IF(SUM($S" + (lastRow - 2) + ":$S" + lastRow + ")>0,SUM($R" + (lastRow - 2) + ":$R" + lastRow + ")/SUM($S" + (lastRow - 2) + ":$S" + lastRow + "),0)");
                escU = StringEscapeUtils.escapeCsv("=IF(SUM($U" + (lastRow - 2) + ":$U" + lastRow + ")>0,SUM($T" + (lastRow - 2) + ":$T" + lastRow + ")/SUM($U" + (lastRow - 2) + ":$U" + lastRow + "),0)");
                escW = StringEscapeUtils.escapeCsv("=IF(SUM($W" + (lastRow - 2) + ":$W" + lastRow + ")>0,SUM($V" + (lastRow - 2) + ":$V" + lastRow + ")/SUM($W" + (lastRow - 2) + ":$W" + lastRow + "),0)");
            } else {
                escS = "0";
                escU = "0";
                escW = "0";
            }
            //            String escL3 = StringEscapeUtils.escapeCsv("=AVERAGE(OFFSET(INDIRECT(\"J\"&ROW()),-3,0,3,1))");
            bW.write("," + escR + "," + escS + "," + escT + "," + escU + "," + escV + "," + escW);
            bW.write("," + "=SUM($X" + startRow + ":$X" + lastRow + ")/" + ((lastRow-startRow)+1));
            bW.write("," + "=SUM($Y" + startRow + ":$Y" + lastRow + ")/" + ((lastRow-startRow)+1));
            bW.write("," + "=SUM($Z" + startRow + ":$Z" + lastRow + ")/" + ((lastRow-startRow)+1));
            bW.write("," + "=SUM($AA" + startRow + ":$AA" + lastRow + ")/" + ((lastRow-startRow)+1));
            bW.write("," + "=SUM($AB" + startRow + ":$AB" + lastRow + ")/" + ((lastRow-startRow)+1));
            String esc$AC = StringEscapeUtils.escapeCsv("=(COUNTIF($AC" + startRow + ":$AC" + lastRow + ",TRUE))");
            String esc$AD = StringEscapeUtils.escapeCsv("=(COUNTIF($AD" + startRow + ":$AD" + lastRow + ",TRUE))");
            bW.write("," + esc$AC + "/" + ((lastRow-startRow)+1) + "," + esc$AD + "/" + ((lastRow-startRow)+1));
            String esc$AE = StringEscapeUtils.escapeCsv("=(COUNTIF($AE" + startRow + ":$AE" + lastRow + ",TRUE))");
            String esc$AF = StringEscapeUtils.escapeCsv("=(COUNTIF($AF" + startRow + ":$AF" + lastRow + ",TRUE))");
            bW.write("," + esc$AE + "/" + ((lastRow-startRow)+1) + "," + esc$AF + "/" + ((lastRow-startRow)+1));
            String esc$Climb = StringEscapeUtils.escapeCsv("=IF((COUNTIF($AH" + startRow + ":$AH" + lastRow + ",TRUE))>0,COUNTIF($AG" + startRow + ":$AG" + lastRow + ",TRUE)/COUNTIF($AH" + startRow + ":$AH" + lastRow + ",TRUE),0)");
            String escAH = "";
            if (lastRow-startRow >= 2)  {
                escAH = StringEscapeUtils.escapeCsv("=IF((COUNTIF($AH" + (lastRow - 2) + ":$AH" + lastRow + ",TRUE))>0,COUNTIF($AG" + (lastRow - 2) + ":$AG" + lastRow + ",TRUE)/COUNTIF($AH" + (lastRow - 2) + ":$AH" + lastRow + ",TRUE),0)");
            } else {
                escAH = "0";
            }
            bW.write("," + esc$Climb + "," + escAH);
            String esc$AI = StringEscapeUtils.escapeCsv("=(COUNTIF($AI" + startRow + ":$AI" + lastRow + ",TRUE))");
            String esc$AJ = StringEscapeUtils.escapeCsv("=(COUNTIF($AJ" + startRow + ":$AJ" + lastRow + ",TRUE))");
            bW.write("," + esc$AI + "/" + ((lastRow-startRow)+1) + "," + esc$AJ  + "/" + ((lastRow-startRow)+1));
            String esc$AK = StringEscapeUtils.escapeCsv("=(COUNTIF($AK" + startRow + ":$AK" + lastRow + ",TRUE))");
            String esc$AL = StringEscapeUtils.escapeCsv("=(COUNTIF($AL" + startRow + ":$AL" + lastRow + ",TRUE))");
            String esc$AM = StringEscapeUtils.escapeCsv("=(COUNTIF($AM" + startRow + ":$AM" + lastRow + ",TRUE))");
            bW.write("," + esc$AK + "/" + ((lastRow-startRow)+1) + "," + esc$AL  + "/" + ((lastRow-startRow)+1) + "," + esc$AM  + "/" + ((lastRow-startRow)+1) + ",,|");

            // ToDo -  done up to this point   =IF((COUNTIF($AH3:$AH6,TRUE))>0,COUNTIF($AG3:$AG6,TRUE)/COUNTIF($AH3:$AH6,TRUE),0)
            bW.write(",,,,|,'TOTAL >,=SUM($W" + startRow + ":$W" + lastRow + "),=SUM($X" + startRow + ":$X" + lastRow + ")");
            bW.write(",'RATIO >,=$W" + (lastRow+1) + "/" + ((lastRow-startRow)+1) );
            bW.write(",'Last 3>,=Sum($W" + (lastRow-2) + ":$W" + (lastRow) + ")/3" );    // Tele Gears Last 3

//            String esc$AD = StringEscapeUtils.escapeCsv("=(COUNTIF($AD" + startRow + ":$AD" + lastRow + ",TRUE))");
//            String esc$AF = StringEscapeUtils.escapeCsv("=(COUNTIF($AF" + startRow + ":$AF" + lastRow + ",TRUE))");
            String escAD$AF = StringEscapeUtils.escapeCsv("=IF($AD" + (lastRow+1) +">0,$AE" + (lastRow+1) + "/" + ((lastRow-startRow)+1) + ",0)");
            String esc$AD3 = StringEscapeUtils.escapeCsv("=(COUNTIF($AF" + (lastRow-2) + ":$AF" + (lastRow) + ",TRUE))");
            bW.write(",'TOTAL >,"+ esc$AD + "," + esc$AF + "," + escAD$AF + "," + esc$AD3 + "/3,,,,,,,,,,,,||,,,|,");
            gatherBA(prevTeam);
            bW.write(tmName + "," + tmRank + ",'"+tmWLT + ","+tmOPR + ","+tmKPa + ","+tmTPts + ",|");

            // Todo - Fix All to use # of matches!!!
            bW.write(",=(($AF" + (lastRow+1) +"*2) + $Z" + (lastRow+1) + " + $O" + (lastRow+1) +") / 3");   // Weighted ALL
            bW.write(",=(($AG" + (lastRow+1) +"*2) + $AB" + (lastRow+1) + " + $Q" + (lastRow+1) +") / 3,");  // Weighted Last 3
            bW.write("=$O$" + (lastRow+1) + ",=$Q$" + (lastRow+1) + ",");          // Auto Gears (ALL & Last 3)
            bW.write("=$Z$" + (lastRow+1) + ",=$AB$" + (lastRow+1) + ",");         // Tele Gears (ALL & Last 3)
            bW.write("=$AF$" + (lastRow+1) + ",=$AG$" + (lastRow+1) + ",");        // Climbs (ALL & Last 3)
            String escL = StringEscapeUtils.escapeCsv("=COUNTIF($L$" + startRow  + ":$L$" + (lastRow) + ",\"2\")");
            bW.write(escL + ",");           // Auto Center Gears
            String escS1 = StringEscapeUtils.escapeCsv("=COUNTIF($L$" + startRow  + ":$L$" + (lastRow) + ",\"1\")");
            bW.write(escS1 + ",");           // Auto Left-Side Gears
            String escS3 = StringEscapeUtils.escapeCsv("=COUNTIF($L$" + startRow  + ":$L$" + (lastRow) + ",\"3\")");
            bW.write(escS3 + ",=$BM$" + (lastRow+1) + "+ $BN$" + (lastRow+1) + ",");      // TOTAL Auto Side Gears
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
//        Log.w(TAG, " gatherBA  " + teamNo);
        if (BA_Data) {
            for (int i = 0; i < BAnumTeams; i++) {
                if (BAe.teams[i].team_number == Long.parseLong(teamNo.trim())) {
                    tmName = BAe.teams[i].nickname;
                    tmRank = String.valueOf(BAe.teams[i].rank);
                    tmWLT = BAe.teams[i].record;
                    tmOPR = String.format("%3.3f", ((new TBA().fillOPR(BAe, BAe.teams[i]).opr)));
//                Log.w(TAG,"  OPR: " + BAe.teams[i].opr);
//                tmOPR = String.format("%3.3f",(BAe.teams[i].opr));
                    tmKPa = String.valueOf(BAe.teams[i].pressure);
                    tmTPts = String.valueOf(BAe.teams[i].touchpad);
//                System.out.println(tmName+" "+tmRank+" "+ tmWLT+" "+tmOPR+" "+tmKPa+" "+tmTPts + " \n");
                    break;      // exit For - found team
                }
            } // End For
        } else {
            tmName = "???";
            tmRank = "00";
            tmWLT = "*-*-*";

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
//        Toast toast = Toast.makeText(getBaseContext(), "FRC5414 ©2018  *** Files initialied ***" , Toast.LENGTH_LONG);
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
        Log.w(TAG, ">>>>> Event '" + Pearadox.FRC_EventName + "'");
        txt_EvntCod = (TextView) findViewById(R.id.txt_EvntCod);
        txt_EvntDat = (TextView) findViewById(R.id.txt_EvntDat);
        txt_EvntPlace = (TextView) findViewById(R.id.txt_EvntPlace);
        p_Firebase.eventObj event_inst = new p_Firebase.eventObj();
        for(int i=0 ; i < Pearadox.eventList.size() ; i++)
        {
            event_inst = Pearadox.eventList.get(i);
            if (event_inst.getcomp_name().equals(ev)) {
                Pearadox.FRC_Event = event_inst.getComp_code();
                Pearadox.FRC_ChampDiv = event_inst.getcomp_div();
                txt_EvntCod.setText(Pearadox.FRC_Event.toUpperCase());  // Event Code
                txt_EvntDat.setText(event_inst.getcomp_date());         // Event Date
                txt_EvntPlace.setText(event_inst.getcomp_place());                      // Event Location
            }
        }
        Log.w(TAG, "** Event code '" + Pearadox.FRC_Event + "' " + Pearadox.FRC_ChampDiv + "  \n ");

        Log.w(TAG, "*** TBA Event ***");
        Event e = new TBA().getEvent("2018" + Pearadox.FRC_Event);
        // Print general event info
        System.out.println(e.name);
        System.out.println(e.location);
        System.out.println(e.start_date);
        System.out.println("\n");
//        txt_EvntCod.setText(Pearadox.FRC_Event.toUpperCase());  // Event Code
//        txt_EvntDat.setText(e.start_date);                      // Event Date
//        txt_EvntPlace.setText(e.location);                      // Event Location

        btn_Teams.setEnabled(true);
        btn_Match_Sched.setEnabled(true);

        pfDatabase = FirebaseDatabase.getInstance();
        pfMatchData_DBReference = pfDatabase.getReference("match-data/" + Pearadox.FRC_Event);    // Match Data
        addMD_VE_Listener(pfMatchData_DBReference.orderByChild("team_num"));        // Load _ALL_ Matches in team order GLF 4/18

    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
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
                    Toast toast1 = Toast.makeText(getBaseContext(), "FRC5414 ©2018  *** Match Data loaded = " + Pearadox.Matches_Data.size() + " ***", Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.BOTTOM, 0, 0);
                    toast1.show();
                } else {
                    btn_Spreadsheet.setEnabled(false);
                }
// ----------  Blue Alliance  -----------
                Settings.GET_EVENT_STATS = false;
                TBA t = new TBA();
                BAe = new TBA().getEvent("2018" + Pearadox.FRC_ChampDiv);
                if (BAe.name == null) {
                    Log.e(TAG,"### Data for: '" + Pearadox.FRC_ChampDiv + "' is _NOT_ available yet  ###");
                    BA_Data = false;
                    Toast toast2 = Toast.makeText(getBaseContext(), "### Data for: '" + Pearadox.FRC_ChampDiv + "' is _NOT_ available yet  ###", Toast.LENGTH_LONG);
                    toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast2.show();
                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                    btn_Teams.setEnabled(false);
                    btn_Match_Sched.setEnabled(false);
                    btn_Spreadsheet.setEnabled(true);
                } else {
                    BA_Data = true;
                    BAteams = BAe.teams.clone();
                    BAnumTeams = BAteams.length;

                    btn_Teams.setEnabled(true);
                    btn_Match_Sched.setEnabled(true);
                    btn_Spreadsheet.setEnabled(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });
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
                    Log.w(TAG,"      " + event_inst.getcomp_name() + " - " + event_inst.getComp_code());
                    Pearadox.eventList.add(event_inst);
                }
                Log.w(TAG,"### Events ###  : " + Pearadox.eventList.size());
                Pearadox.num_Events = Pearadox.eventList.size() +1;     // account for 1st blank
                Pearadox.comp_List = new String[Pearadox.num_Events];  // Re-size for spinner
                Arrays.fill(Pearadox.comp_List, null );
                Pearadox.comp_List[0] = " ";       // make it so 1st Drop-Down entry is blank
                for(int i=0 ; i < Pearadox.eventList.size() ; i++)
                {
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
//            Log.e(TAG, "Peardox = '" + pw + "'");
        } catch (IOException e) {
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
            Toast toast = Toast.makeText(getBaseContext(), "Firebase authentication - Password required", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            e.printStackTrace();
        }
//        Log.e(TAG, "Sign-In " + eMail + "  '" + pw + "'");

        mAuth.signInWithEmailAndPassword(eMail, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
