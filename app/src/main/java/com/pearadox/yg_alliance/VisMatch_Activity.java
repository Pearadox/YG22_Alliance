package com.pearadox.yg_alliance;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.StackedBarModel;

import java.io.File;
import java.io.FileOutputStream;

import androidx.appcompat.app.AppCompatActivity;

public class VisMatch_Activity extends AppCompatActivity {
    String TAG = "VisMatch_Activity";        // This CLASS name
    String tnum = "";
    String tname = "";
    int start = 0;          // Start Position for matches (0=ALL)
    int numObjects = 0; int numProcessed = 0;
    Spinner spinner_numMatches;
    String underScore = new String(new char[60]).replace("\0", "_");  // string of 'x' underscores
    String matches = "";  String match_id = "";
    TextView txt_team, txt_teamName, txt_NumMatches, txt_Matches;
    TextView txt_auto_left, txt_StartingBalls, txt_noAuton;
    TextView txt_Auto_CargoScored, txt_Auto_CargoMissed, txt_Auto_Human;
    TextView txt_Tele_CargoScored, txt_Tele_CargoMissed;
    TextView txt_Auto_LowerPercnt, txt_Auto_UpperPercnt, txt_Tele_LowerPercnt, txt_Tele_UpperPercnt;
    String A_LowPercent = ""; String A_HiPercent = ""; String T_LowPercent = ""; String T_HiPercent = "";
    TextView txt_HangLevel;
    TextView txt_AutonFloor, txt_AutonTerminal, txt_AutonAcquired;
    TextView txt_TeleFloor, txt_TeleTerminal, txt_TeleAcquired;
    TextView txt_TeleClimb;
    TextView txt_final_Tipped;
    /* Comment Boxes */     TextView txt_AutoComments, txt_TeleComments, txt_FinalComments;
    TextView txt_StartPositions;
    TextView txt_Pos;
    public static String[] numMatch = new String[]             // Num. of Matches to process
            {"ALL","Last","Last 2","Last 3","Last 4","Last 5"};
    StackedBarChart teleBarChart; StackedBarChart autonBarChart;
    //----------------------------------
    int numleftSectorLine = 0; int noAuton = 0; int precellsCarried = 0; int precell_0 = 0; int precell_1 = 0;
    int auto_B1 = 0; int auto_B2 = 0; int auto_B3 = 0; int auto_B4 = 0; int auto_B5 = 0; int auto_B6 = 0;  int auto_B7 = 0;
    // NOTE: _ALL_ external mentions of Playere Sta. (PS) were changed to Loading Sta. (LS) so as to NOT be confused with Player Control Sta. (Driver)
    int auto_Ps1 = 0; int auto_Ps2 = 0; int auto_Ps3 = 0;
    int auton_Floor= 0; int auton_Terminal = 0; int auton_Acquire =0; int auton_Human = 0;
    int tele_CargoFloor = 0; int tele_CargoTerminal = 0; int tele_Acquire =0;
    int tele_Climb = 0;
    int climbH0= 0; int climbH1 = 0; int climbH2 = 0; int climbH3 = 0;  int climbH4 = 0;
    int auto_Low = 0; int auto_High = 0; int auto_LowMiss = 0; int Auto_HighMiss = 0;
    int Tele_Low = 0; int Tele_High = 0; int Tele_LowMiss = 0; int Tele_HighMiss = 0;
    int numMatches = 0;
    String auto_Comments = "";
    //----------------------------------
    int numTeleClimbSuccess = 0;
    String tele_Comments = "";
    //----------------------------------
    int final_LostComm = 0; int final_LostParts = 0; int final_Tipped =0; int final_NumPen = 0;
    int final_DefBad = 0;  int final_DefAvg = 0; int final_DefGood = 0; int final_DefNA = 0;
    TextView txt_final_LostComm, txt_final_LostParts, txt_final_Defenses, txt_final_NumPen;
    String final_Comments = "";
    //----------------------------------

    matchData match_inst = new matchData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_match);
        Log.i(TAG, "@@@@  VisMatch_Activity started  @@@@");
        Bundle bundle = this.getIntent().getExtras();
        String param1 = bundle.getString("team");
        String param2 = bundle.getString("name");
//        Log.w(TAG, param1);      // ** DEBUG **
        tnum = param1;      // Save Team #
//        Log.w(TAG, param2);      // ** DEBUG **
        tname = param2;      // Save Team #


        txt_Matches = (TextView) findViewById(R.id.txt_Matches);
        txt_team = (TextView) findViewById(R.id.txt_team);
        txt_teamName = (TextView) findViewById(R.id.txt_teamName);
        txt_NumMatches = (TextView) findViewById(R.id.txt_NumMatches);
        Spinner spinner_numMatches = (Spinner) findViewById(R.id.spinner_numMatches);
        ArrayAdapter adapter_Matches = new ArrayAdapter<String>(this, R.layout.robonum_list_layout, numMatch);
        adapter_Matches.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_numMatches.setAdapter(adapter_Matches);
        spinner_numMatches.setSelection(0, false);
        spinner_numMatches.setOnItemSelectedListener(new numMatches_OnItemSelectedListener());
        /*  Auto  */
        txt_auto_left = (TextView) findViewById(R.id.txt_auto_left);
        txt_HangLevel = (TextView) findViewById(R.id.txt_HangLevel);
        txt_noAuton = (TextView) findViewById(R.id.txt_noAuton);
        txt_StartingBalls = (TextView) findViewById(R.id.txt_StartingBalls);
        txt_Auto_CargoScored = (TextView) findViewById(R.id.txt_Auto_CargoScored);
        txt_Auto_CargoMissed = (TextView) findViewById(R.id.txt_Auto_CargoMissed);
        txt_Auto_LowerPercnt = (TextView) findViewById(R.id.txt_Auto_LowerPercnt);
        txt_Auto_UpperPercnt = (TextView) findViewById(R.id.txt_Auto_UpperPercnt);
        txt_Auto_Human = (TextView) findViewById(R.id.txt_Auto_Human);
        txt_AutonFloor = (TextView) findViewById(R.id.txt_AutonFloor);
        txt_AutonTerminal = (TextView) findViewById(R.id.txt_AutonTerminal);
        txt_AutonAcquired = (TextView) findViewById(R.id.txt_AutonAcquired);

        txt_StartPositions = (TextView) findViewById(R.id.txt_StartPositions);
        txt_Pos = (TextView) findViewById(R.id.txt_Pos);
        autonBarChart = (StackedBarChart) findViewById(R.id.autonBarChart);
        txt_AutoComments = (TextView) findViewById(R.id.txt_AutoComments);
        txt_AutoComments.setTextSize(12);       // normal
        txt_AutoComments.setMovementMethod(new ScrollingMovementMethod());
        /*  Tele  */
        txt_Tele_CargoScored = (TextView) findViewById(R.id.txt_Tele_CargoScored);
        txt_Tele_CargoMissed = (TextView) findViewById(R.id.txt_Tele_CargoMissed);
        txt_Tele_LowerPercnt = (TextView) findViewById(R.id.txt_Tele_LowerPercnt);
        txt_Tele_UpperPercnt = (TextView) findViewById(R.id.txt_Tele_UpperPercnt);
        txt_TeleFloor = (TextView) findViewById(R.id.txt_TeleFloor);
        txt_TeleClimb = (TextView) findViewById(R.id.txt_TeleClimb);
        txt_TeleAcquired = (TextView) findViewById(R.id.txt_TeleAcquired);
        teleBarChart = (StackedBarChart) findViewById(R.id.teleBarChart);
        txt_TeleComments = (TextView) findViewById(R.id.txt_TeleComments);
        txt_TeleComments.setMovementMethod(new ScrollingMovementMethod());

        /*  Final  */
        txt_FinalComments = (TextView) findViewById(R.id.txt_FinalComments);
        txt_FinalComments.setMovementMethod(new ScrollingMovementMethod());

        txt_final_LostComm = (TextView) findViewById(R.id.txt_final_LostComm);
        txt_final_LostParts = (TextView) findViewById(R.id.txt_final_LostParts);
        txt_final_Tipped = (TextView) findViewById(R.id.txt_final_Tipped);
        txt_final_Defenses = (TextView) findViewById(R.id.txt_final_Defenses);
        txt_final_NumPen = (TextView) findViewById(R.id.txt_final_NumPen);

        txt_team.setText(tnum);
        txt_teamName.setText(tname);    // Get real

        numObjects = Pearadox.Matches_Data.size();
//        Log.w(TAG, "Objects = " + numObjects);
        txt_NumMatches.setText(String.valueOf(numObjects));

        init_Values();
        numProcessed = numObjects;
        start = 0;
        getMatch_Data();
    }
    // ================================================================
    private void getMatch_Data() {
        for (int i = start; i < numObjects; i++) {
//            Log.w(TAG, "In for loop!   " + i);
            match_inst = Pearadox.Matches_Data.get(i);      // Get instance of Match Data
            match_id = match_inst.getMatch();
            matches = matches + match_inst.getMatch() + "  ";   // cumulative list of matches

            if (match_inst.isAuto_mode()) {
                noAuton++;
            }
            if (match_inst.isAuto_leftTarmac()) {
                numleftSectorLine++;
            }


            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            // Pre-Start
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            String pos = match_inst.getPre_startPos().trim();
//            Log.w(TAG, "Start Pos. " + pos);
            switch (pos) {
                case "1":
                    auto_B1++;
                    break;
                case ("2"):
                    auto_B2++;
                    break;
                case "3":
                    auto_B3++;
                    break;
                case "4":
                    auto_B4++;
                    break;
                case "5":
                    auto_B5++;
                    break;
                case "6":
                    auto_B6++;
                    break;
                case "No Show":
                    auto_B7++;
                    break;
                default:                //
                    Log.e(TAG, "***  Invalid Start Position!!!  ***" );
            }

            int PlayerStat = match_inst.getPre_PlayerSta();
//            Log.w(TAG, "Player Station. " + PlayerStat);
            switch (PlayerStat) {
                case 1:
                    auto_Ps1++;
                    break;
                case 2:
                    auto_Ps2++;
                    break;
                case 3:
                    auto_Ps3++;
                    break;
                default:                //
                    Log.e(TAG, "***  Invalid Player Station!!!  ***" );
            }

            int BallStart = match_inst.getPre_cargo_carried();
//            Log.w(TAG, "Pre-Cells." + BallStart);
            switch (BallStart) {
                case 0:
                    precell_0++;
                case 1:
                    precell_1++;
            }

            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            //@@@@@@@@@@@@@@@@@@@@@@  Autonomous  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            if (match_inst.isAuto_CollectFloor()) {
                auton_Floor++;
            }
            if (match_inst.isAuto_CollectTerm()) {
                auton_Terminal++;
            }
            if (match_inst.isAuto_aquCargo()) {
                auton_Acquire++;
            }
            if (match_inst.isAuto_Human()) {
                auton_Human++;
            }

            auto_Low = auto_Low + match_inst.getAuto_Low();
            int thisMatchAutoLow = match_inst.getAuto_Low();
            auto_High = auto_High + match_inst.getAuto_High();
            int thisMatchAutoHigh = match_inst.getAuto_High();
            AutoStackBar(i+1, match_id, (float)thisMatchAutoLow , (float)thisMatchAutoHigh);
            auto_LowMiss = auto_LowMiss + match_inst.getAuto_MissedLow();
            Auto_HighMiss = Auto_HighMiss + match_inst.getAuto_MissedHigh();
            Float BatAvg;
            Log.e(TAG, "AutoLow   Score" + auto_Low  +"  Miss" +auto_LowMiss);
            if ((auto_Low + auto_LowMiss) > 0) {
                BatAvg = (float)auto_Low / (auto_Low + auto_LowMiss);  // Made ÷ Attempts
                Log.e(TAG, "Low%= " + BatAvg);
                if (BatAvg == 1.0f) {
                    A_LowPercent = "1.000";
                } else {
                    A_LowPercent = String.format("%.3f", BatAvg);
                }
            } else {
                Log.e(TAG, "BatAvg=.000");
                A_LowPercent = "0.000";
            }
            if ((auto_High + Auto_HighMiss) > 0) {
                BatAvg = (float)auto_High / (auto_High + Auto_HighMiss);  // Made ÷ Attempts
                if (BatAvg == 1.0f) {
                    A_HiPercent = "1.000";
                } else {
                    A_HiPercent =String.format("%.3f", BatAvg);
                }
            } else {
                A_HiPercent = "0.000";
            }

            if (match_inst.getAuto_comment().length() > 1) {
                auto_Comments = auto_Comments + match_inst.getMatch() + "-" + match_inst.getAuto_comment() + "\n" + underScore  + "\n" ;
            }

            // *************************************************
            // ******************** TeleOps ********************
            // *************************************************
            if (match_inst.isTele_Cargo_floor()){
                tele_CargoFloor++;
            }
            if (match_inst.isTele_Cargo_term()) {
                tele_CargoTerminal++;
            }
            if (match_inst.isTele_aquCargo()) {
                tele_Acquire++;
            }

            if (match_inst.isTele_Climbed()) {
                tele_Climb++;
            }
            String Hang = match_inst.getTele_HangarLevel();
            switch (Hang) {
                case "None":
                    climbH0++;
                    break;
                case "Low":
                    climbH1++;
                    break;
                case ("Mid"):
                    climbH2++;
                    break;
                case "High":
                    climbH3++;
                    break;
                case "Traversal":
                    climbH4++;
                    break;
                default:                //
                    Log.e(TAG, "***  Invalid Hangar Level!!!  ***" );
            }

            Tele_Low = Tele_Low + match_inst.getTele_Low();
            int thisMatchTeleLow = match_inst.getTele_Low();
            Tele_High = Tele_High + match_inst.getTele_High();
            int thisMatchTeleHigh = match_inst.getTele_High();
            Tele_LowMiss = Tele_LowMiss + match_inst.getTele_MissedLow();
            Tele_HighMiss = Tele_HighMiss + match_inst.getTele_MissedHigh();
            if ((Tele_Low + Tele_LowMiss) > 0) {        // avoid divide by 0
                BatAvg = (float)Tele_Low / (Tele_Low + Tele_LowMiss);  // Made ÷ Attempts
                if (BatAvg == 1.0f) {       // all this to get 3 digits!!
                    T_LowPercent = "1.00";
                } else {
                    Log.e(TAG, "TeleLow%= " + BatAvg +" L" + Tele_Low  +"  M" +Tele_LowMiss);
                    T_LowPercent = String.format("%.3f", BatAvg);
                }
            } else {
                Log.e(TAG, "Default TeleLow = .000");
                T_LowPercent = "0.000";
            }
            if ((Tele_High + Tele_HighMiss) > 0) {
                BatAvg = (float)Tele_High / (Tele_High + Tele_HighMiss);    // Made ÷ Attempts
                if (BatAvg == 1.0f) {
                    T_HiPercent = "1.000";
                } else {
                    Log.e(TAG, "TeleHigh%= " + BatAvg+" L" + Tele_High  +"  M" +Tele_HighMiss);
                    T_HiPercent = String.format("%.3f", BatAvg);
                }
            } else {
                T_HiPercent = "0.000";
            }

            TeleStackBar(i+1, match_id, (float)thisMatchTeleLow , (float)thisMatchTeleHigh);

            if (match_inst.getTele_comment().length() > 1) {
                tele_Comments = tele_Comments + match_inst.getMatch() + "-" + match_inst.getTele_comment() + "\n" + underScore  + "\n" ;
            }

            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2
            // Final elements
            if (match_inst.isFinal_lostComms()) {
                final_LostComm++;
            }
            if (match_inst.isFinal_lostParts()) {
                final_LostParts++;
            }
            if (match_inst.isFinal_tipped()) {
                final_Tipped++;
            }
            String defend = (match_inst.getFinal_defense());
            switch (defend) {
                case "Bad":
                    final_DefBad++;
                    break;
                case "Avg":
                    final_DefAvg++;
                    break;
                case "Good":
                    final_DefGood++;
                    break;
                case "Not Observed (None)":
                    final_DefNA++;
                    break;
                default:                //
                    Log.e(TAG, "***  Invalid Defense!!!  ***" );
            }

            if (match_inst.getTele_num_Penalties() > 0) {
                final_NumPen = final_NumPen + match_inst.getTele_num_Penalties();
            }

//            Log.w(TAG, "Final Comment = " + match_inst.getFinal_comment() + "  " + match_inst.getFinal_comment().length());
            if (match_inst.getFinal_comment().length() > 1) {
                final_Comments = final_Comments + match_inst.getMatch() + "-" + match_inst.getFinal_comment() + "\n" + underScore  + "\n" ;
            }
        } //end For


// ================================================================
// ======  Now start displaying all the data we collected  ========
// ================================================================
        txt_auto_left = (TextView) findViewById(R.id.txt_auto_left);
        txt_HangLevel = (TextView) findViewById(R.id.txt_HangLevel);
        txt_noAuton = (TextView) findViewById(R.id.txt_noAuton);
        txt_StartingBalls = (TextView) findViewById(R.id.txt_StartingBalls);
        txt_Auto_CargoScored = (TextView) findViewById(R.id.txt_Auto_CargoScored);
        txt_Tele_CargoScored = (TextView) findViewById(R.id.txt_Tele_CargoScored);
        txt_Auto_Human = (TextView) findViewById(R.id.txt_Auto_Human);
        txt_AutonFloor = (TextView) findViewById(R.id.txt_AutonFloor);
        txt_AutonTerminal = (TextView) findViewById(R.id.txt_AutonTerminal);
        txt_AutonAcquired = (TextView) findViewById(R.id.txt_AutonAcquired);
        txt_TeleFloor = (TextView) findViewById(R.id.txt_TeleFloor);
        txt_TeleTerminal = (TextView) findViewById(R.id.txt_TeleTerminal);
        txt_TeleClimb = (TextView) findViewById(R.id.txt_TeleClimb);
        txt_StartPositions = (TextView) findViewById(R.id.txt_StartPositions);
        txt_Pos = (TextView) findViewById(R.id.txt_Pos);
        txt_Matches.setText(matches);
        txt_auto_left.setText(String.valueOf(numleftSectorLine));
        txt_noAuton.setText(String.valueOf(noAuton));
        String carScored = "⚪L" + String.format("%-3s", auto_Low) + " U" + String.format("%-3s", auto_High) ;
        txt_Auto_CargoScored.setText(carScored);
        txt_Auto_CargoMissed.setText("⊗"+ "L" + auto_LowMiss + " U" + Auto_HighMiss);
        txt_Auto_LowerPercnt.setText(A_LowPercent);
        txt_Auto_UpperPercnt.setText(A_HiPercent);
        String startingBalls = "⁰" + String.format("%-3s", precell_0) + " ¹" + String.format("%-3s", precell_1);
        txt_StartingBalls.setText(startingBalls);
        txt_AutonFloor.setText(String.valueOf(auton_Floor));
        txt_AutonTerminal.setText(String.valueOf(auton_Terminal));
        txt_AutonAcquired.setText(String.valueOf(auton_Acquire));
        txt_Auto_Human.setText(Integer.toString(auton_Human));
        String StartPositions = String.format("%-2s", Integer.toString(auto_B1)) + "  " + String.format("%-2s", Integer.toString(auto_B2)) + "  " + String.format("%-2s", Integer.toString(auto_B3)) + "   " + String.format("%-2s", Integer.toString(auto_B4)) + "   " + String.format("%-2s", Integer.toString(auto_B5)) + "  " + String.format("%-2s", Integer.toString(auto_B6)) + "  " + String.format("%-2s", Integer.toString(auto_B7));
        txt_StartPositions.setText(String.valueOf(StartPositions));
        txt_Pos.setText(String.format("%-3s", Integer.toString(auto_Ps1)) + "  " + String.format("%-3s", Integer.toString(auto_Ps2)) + "  " + String.format("%-3s", Integer.toString(auto_Ps3)));
        txt_AutoComments.setText(auto_Comments);

        // ==============================================
        // Display Tele elements
        String telePowerCell = "⚪L" + String.format("%-3s", Tele_Low) + " U" + String.format("%-3s", Tele_High);
        txt_Tele_CargoScored.setText(telePowerCell);
        txt_Tele_CargoMissed.setText("⊗L"+ Tele_LowMiss + " U" + Tele_HighMiss);
        txt_Tele_LowerPercnt.setText(T_LowPercent);
        txt_Tele_UpperPercnt.setText(T_HiPercent);
        String HabEnd = "⁰"+ String.valueOf(climbH0) + " ¹" + String.valueOf(climbH1) + " ²" + String.valueOf(climbH2) + " ³" + String.valueOf(climbH3)+ " ⁴" + String.valueOf(climbH4);
        txt_HangLevel.setText(HabEnd);
        txt_TeleFloor.setText(String.valueOf(tele_CargoFloor));
        txt_TeleAcquired.setText(String.valueOf(tele_Acquire));
        txt_TeleTerminal.setText(String.valueOf(tele_CargoTerminal));
        txt_TeleClimb.setText(String.valueOf(tele_Climb));
        txt_TeleComments.setText(tele_Comments);

        // ==============================================
        // Display Final elements
        txt_final_LostComm.setText(String.valueOf(final_LostComm));
        txt_final_LostParts.setText(String.valueOf(final_LostParts));
        txt_final_Tipped.setText(String.valueOf(final_Tipped));
        txt_final_Defenses.setText("B=" + String.valueOf(final_DefBad) + " A=" + String.valueOf(final_DefAvg) + " G=" + String.valueOf(final_DefGood) + " NO=" + String.valueOf(final_DefNA) );
        txt_final_NumPen.setText(String.valueOf(final_NumPen));
        txt_FinalComments.setText(final_Comments);

        autonBarChart.startAnimation();
        teleBarChart.startAnimation();

    }

    //******************************
    private void init_Values() {
        noAuton = 0;
        numleftSectorLine = 0;
        precell_0 = 0; precell_1 = 0;
        auto_Ps1 = 0;
        auto_Ps2 = 0;
        auto_Ps3 = 0;
        auto_Low = 0; auto_High = 0; auto_LowMiss = 0; Auto_HighMiss = 0;
        Tele_Low = 0; Tele_High = 0; Tele_LowMiss = 0; Tele_HighMiss = 0;
        auton_Floor= 0; auton_Terminal = 0; auton_Acquire =0; auton_Human = 0;
        tele_CargoFloor = 0; tele_CargoTerminal = 0; tele_Acquire = 0;
        tele_Climb = 0;
        numTeleClimbSuccess = 0;
        auto_B1 = 0; auto_B2 = 0; auto_B3 = 0; auto_B4 = 0; auto_B5 = 0; auto_B6 = 0;   int auto_B7 = 0;
        auto_Ps1 = 0; auto_Ps2 = 0; auto_Ps3 = 0;
        climbH0 = 0; climbH1 = 0; climbH2 = 0; climbH3 = 0;  int climbH4 = 0;
        auto_Comments = "";
        tele_Comments = "";
        final_Comments = "";
        matches = "";
        final_LostComm = 0;
        final_LostParts = 0;
        final_Tipped =0;
        final_DefBad = 0;  final_DefAvg = 0; final_DefGood = 0; final_DefNA = 0;
        final_NumPen = 0;
        autonBarChart.clearChart();
        teleBarChart.clearChart();
    }


    //===========================================================================================
    public class numMatches_OnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String num = " ";
            num = parent.getItemAtPosition(pos).toString();
            Log.w(TAG, ">>>>> NumMatches '" + num + "'");
            switch (num) {
                case "Last":
                    start = numObjects - 1;     //
                    numProcessed = 1;
                    break;
                case "Last 2":
                    if (numObjects > 2) {
                        start = numObjects - 2;     //
                        numProcessed = 2;
                        break;
                    } else {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                        Toast toast = Toast.makeText(getBaseContext(), "***  This team only has " + numObjects +  " match(s) ***", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                case "Last 3":
                    if (numObjects > 2) {
                        start = numObjects - 3;     //
                        numProcessed = 3;
                        break;
                    } else {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                        Toast toast = Toast.makeText(getBaseContext(), "***  This team only has " + numObjects +  " match(s) ***", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                case "Last 4":
                    if (numObjects > 3) {
                        start = numObjects - 4;     //
                        numProcessed = 4;
                        break;
                    } else {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                        Toast toast = Toast.makeText(getBaseContext(), "***  This team only has " + numObjects +  " match(s) ***", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                case "Last 5":
                    if (numObjects > 4) {
                        start = numObjects - 5;     //
                        numProcessed = 5;
                        break;
                    } else {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                        Toast toast = Toast.makeText(getBaseContext(), "***  This team only has " + numObjects +  " match(s) ***", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                case "ALL":
                    start = 0;                  // Start at beginning
                    numProcessed = numObjects;
                    break;
                default:                //
                    Log.e(TAG, "Invalid Sort - " + start);
            }
//            Log.w(TAG, "Start = " + num );
            init_Values();
            getMatch_Data();
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        Log.e(TAG, "@@@  Options  @@@ " );
//        Log.w(TAG, " \n  \n");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_screen) {
            String filNam = Pearadox.FRC_Event.toUpperCase() + "-VizMatch"  + "_" + tnum.trim() + ".JPG";
//            Log.w(TAG, "File='" + filNam + "'");
            try {
                File imageFile = new File(Environment.getExternalStorageDirectory() + "/download/FRC5414/" + filNam);
                View v1 = getWindow().getDecorView().getRootView();             // **\
                v1.setDrawingCacheEnabled(true);                                // ** \Capture screen
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());      // ** /  as bitmap
                v1.setDrawingCacheEnabled(false);                               // **/
                FileOutputStream fos = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                fos.flush();
                fos.close();
                bitmap.recycle();           //release memory
                MediaActionSound sound = new MediaActionSound();
                sound.play(MediaActionSound.SHUTTER_CLICK);
                Toast toast = Toast.makeText(getBaseContext(), "☢☢  Screen captured in Download/FRC5414  ☢☢", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } catch (Throwable e) {
                // Several error may come out with file handling or DOM
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void sayLeaving() {
        txt_AutoComments = (TextView) findViewById(R.id.txt_AutoComments);
        txt_AutoComments.setTextSize(20);
        txt_AutoComments.setText("**** Exiting " + TAG + " ****" );
    }


    private void AutoStackBar(int num, String match, float low, float upper) {
        Log.d(TAG, "@@@  AutoStackBar  @@@ " + num + " " + match + " " + low + " " + upper );
        StackedBarChart autonBarChart = (StackedBarChart) findViewById(R.id.autonBarChart);

        switch (num) {
            case 1:
                StackedBarModel s1 = new StackedBarModel(match);
                s1.addBar(new BarModel(low, 0xFFff0000));    // Low
                s1.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s1);
                break;
            case 2:
                StackedBarModel s2 = new StackedBarModel(match);
                s2.addBar(new BarModel(low, 0xFFff0000));    // Low
                s2.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s2);
                break;
            case 3:
                StackedBarModel s3 = new StackedBarModel(match);
                s3.addBar(new BarModel(low, 0xFFff0000));    // Low
                s3.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s3);
                break;
            case 4:
                StackedBarModel s4 = new StackedBarModel(match);
                s4.addBar(new BarModel(low, 0xFFff0000));    // Low
                s4.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s4);
                break;
            case 5:
                StackedBarModel s5 = new StackedBarModel(match);
                s5.addBar(new BarModel(low, 0xFFff0000));    // Low
                s5.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s5);
                break;

            case 6:
                StackedBarModel s6 = new StackedBarModel(match);
                s6.addBar(new BarModel(low, 0xFFff0000));    // Low
                s6.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s6);
                break;

            case 7:
                StackedBarModel s7 = new StackedBarModel(match);
                s7.addBar(new BarModel(low, 0xFFff0000));    // Low
                s7.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s7);
                break;

            case 8:
                StackedBarModel s8 = new StackedBarModel(match);
                s8.addBar(new BarModel(low, 0xFFff0000));    // Low
                s8.addBar(new BarModel(upper, 0xFF0000ff));    // upper
               autonBarChart.addBar(s8);
                break;

            case 9:
                StackedBarModel s9 = new StackedBarModel(match);
                s9.addBar(new BarModel(low, 0xFFff0000));    // Low
                s9.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s9);
                break;

            case 10:
                StackedBarModel s10 = new StackedBarModel(match);
                s10.addBar(new BarModel(low, 0xFFff0000));    // Low
                s10.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s10);
                break;

            case 11:
                StackedBarModel s11 = new StackedBarModel(match);
                s11.addBar(new BarModel(low, 0xFFff0000));    // Low
                s11.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s11);
                break;

            case 12:
                StackedBarModel s12 = new StackedBarModel(match);
                s12.addBar(new BarModel(low, 0xFFff0000));    // Low
                s12.addBar(new BarModel(upper, 0xFF0000ff));    // upper
               autonBarChart.addBar(s12);
                break;

            case 13:
                StackedBarModel s13 = new StackedBarModel(match);
                s13.addBar(new BarModel(low, 0xFFff0000));    // Low
                s13.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s13);
                break;

            case 14:
                StackedBarModel s14 = new StackedBarModel(match);
                s14.addBar(new BarModel(low, 0xFFff0000));    // Low
                s14.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s14);
                break;

            case 15:
                StackedBarModel s15 = new StackedBarModel(match);
                s15.addBar(new BarModel(low, 0xFFff0000));    // Low
                s15.addBar(new BarModel(upper, 0xFF0000ff));    // upper
                autonBarChart.addBar(s15);
                break;

            default:                //
                Log.e(TAG, "Chart can only handle 15 - " + num);

        }}

    private void TeleStackBar(int num, String match, float low, float under) {
        StackedBarChart teleBarChart = (StackedBarChart) findViewById(R.id.teleBarChart);

        switch (num) {
            case 1:
                StackedBarModel s1 = new StackedBarModel(match);
                s1.addBar(new BarModel(low, 0xFFff0000));    // Low
                s1.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s1.addBar(new BarModel(line, 0xFF006400));    // Line
//                s1.addBar(new BarModel(front, 0xFF800080));    // Front
//                s1.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s1);
                break;
            case 2:
                StackedBarModel s2 = new StackedBarModel(match);
                s2.addBar(new BarModel(low, 0xFFff0000));    // Low
                s2.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s2.addBar(new BarModel(line, 0xFF006400));   // Line
//                s2.addBar(new BarModel(front, 0xFF800080));    // Front
//                s2.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s2);
                break;
            case 3:
                StackedBarModel s3 = new StackedBarModel(match);
                s3.addBar(new BarModel(low, 0xFFff0000));    // Low
                s3.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s3.addBar(new BarModel(line, 0xFF006400));    // Line
//                s3.addBar(new BarModel(front, 0xFF800080));    // Front
//                s3.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s3);
                break;
            case 4:
                StackedBarModel s4 = new StackedBarModel(match);
                s4.addBar(new BarModel(low, 0xFFff0000));    // Low
                s4.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s4.addBar(new BarModel(line, 0xFF006400));    // Line
//                s4.addBar(new BarModel(front, 0xFF800080));    // Front
//                s4.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s4);
                break;
            case 5:
                StackedBarModel s5 = new StackedBarModel(match);
                s5.addBar(new BarModel(low, 0xFFff0000));    // Low
                s5.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s5.addBar(new BarModel(line, 0xFF006400));    // Line
//                s5.addBar(new BarModel(front, 0xFF800080));    // Front
//                s5.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s5);
                break;
            case 6:
                StackedBarModel s6 = new StackedBarModel(match);
                s6.addBar(new BarModel(low, 0xFFff0000));    // Low
                s6.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s6.addBar(new BarModel(line, 0xFF006400));    // Line
//                s6.addBar(new BarModel(front, 0xFF800080));    // Front
//                s6.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s6);
                break;
            case 7:
                StackedBarModel s7 = new StackedBarModel(match);
                s7.addBar(new BarModel(low, 0xFFff0000));    // Low
                s7.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s7.addBar(new BarModel(line, 0xFF006400));   // Line
//                s7.addBar(new BarModel(front, 0xFF800080));    // Front
//                s7.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s7);
                break;
            case 8:
                StackedBarModel s8 = new StackedBarModel(match);
                s8.addBar(new BarModel(low, 0xFFff0000));    // Low
                s8.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s8.addBar(new BarModel(line, 0xFF006400));    // Line
//                s8.addBar(new BarModel(front, 0xFF800080));    // Front
//                s8.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s8);
                break;
            case 9:
                StackedBarModel s9 = new StackedBarModel(match);
                s9.addBar(new BarModel(low, 0xFFff0000));    // Low
                s9.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s9.addBar(new BarModel(line, 0xFF006400));    // Line
//                s9.addBar(new BarModel(front, 0xFF800080));    // Front
//                s9.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s9);
                break;
            case 10:
                StackedBarModel s10 = new StackedBarModel(match);
                s10.addBar(new BarModel(low, 0xFFff0000));    // Low
                s10.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s10.addBar(new BarModel(line, 0xFF006400));    // Line
//                s10.addBar(new BarModel(front, 0xFF800080));    // Front
//                s10.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s10);
                break;
            case 11:
                StackedBarModel s11 = new StackedBarModel(match);
                s11.addBar(new BarModel(low, 0xFFff0000));    // Low
                s11.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s11.addBar(new BarModel(line, 0xFF006400));    // Line
//                s11.addBar(new BarModel(front, 0xFF800080));    // Front
//                s11.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s11);
                break;
            case 12:
                StackedBarModel s12 = new StackedBarModel(match);
                s12.addBar(new BarModel(low, 0xFFff0000));    // Low
                s12.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s12.addBar(new BarModel(line, 0xFF006400));    // Line
//                s12.addBar(new BarModel(front, 0xFF800080));    // Front
//                s12.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s12);
                break;
            case 13:
                StackedBarModel s13 = new StackedBarModel(match);
                s13.addBar(new BarModel(low, 0xFFff0000));    // Low
                s13.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s13.addBar(new BarModel(line, 0xFF006400));    // Line
//                s13.addBar(new BarModel(front, 0xFF800080));    // Front
//                s13.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s13);
                break;
            case 14:
                StackedBarModel s14 = new StackedBarModel(match);
                s14.addBar(new BarModel(low, 0xFFff0000));    // Low
                s14.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s14.addBar(new BarModel(line, 0xFF006400));    // Line
//                s14.addBar(new BarModel(front, 0xFF800080));    // Front
//                s14.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s14);
                break;
            case 15:
                StackedBarModel s15 = new StackedBarModel(match);
                s15.addBar(new BarModel(low, 0xFFff0000));    // Low
                s15.addBar(new BarModel(under, 0xFF0000ff));    // Under
//                s15.addBar(new BarModel(line, 0xFF006400));    // Line
//                s15.addBar(new BarModel(front, 0xFF800080));    // Front
//                s15.addBar(new BarModel(back, 0xFFff8300));    // Back
                teleBarChart.addBar(s15);
                break;

            default:                //
                Log.e(TAG, "Chart can only handle 15 - " + num);
        }
    }


    //###################################################################
//###################################################################
//###################################################################
    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
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
        sayLeaving();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "OnDestroy");
    }

}
// This is a test 2
