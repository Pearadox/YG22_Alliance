package com.pearadox.yg_alliance;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PitCover_Activity extends AppCompatActivity {

    String TAG = "PitCover_Activity";        // This CLASS name
    static final ArrayList<HashMap<String, String>> draftList = new ArrayList<HashMap<String, String>>();
    ListView lstView_Teams;

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_cover);
        Log.w(TAG, "******* Starting PitCover_Activity  *******");
        lstView_Teams = (ListView) findViewById(R.id.lstView_Teams);

        loadTeams();
    }

    // =============================================================================
    private void loadTeams() {
        Log.w(TAG, "@@@@  loadTeams started  @@@@  ");

        SimpleAdapter adaptTeams = new SimpleAdapter(
                this,
                draftList,
                R.layout.pit_list_layout,
                new String[] {"team","BA","Stats"},
                new int[] {R.id.TeamData,R.id.BA, R.id.Stats}
        );

        draftList.clear();
        String totalScore="";
        p_Firebase.teamsObj My_inst = new p_Firebase.teamsObj();
        Log.w(TAG, " Team array size = " + Pearadox.numTeams);
        draftList.clear();

        if (Pearadox.numTeams > 0) {
            for (int i = 0; i < Pearadox.numTeams; i++) {    // load by sorted scores
                HashMap<String, String> temp = new HashMap<String, String>();
                My_inst = Pearadox.team_List.get(i);
                Log.w(TAG, " Team = " + My_inst.getTeam_num());

                temp.put("team",  My_inst.getTeam_num() + " - " + My_inst.getTeam_name());
                temp.put("Stats", "Line1  \uD83D\uDCF7     ");
                temp.put("BA",  "    \uD83D\uDC4D ");
                draftList.add(temp);

            } // end For # teams
            Log.w(TAG, "### Teams ###  : " + draftList.size());
            lstView_Teams.setAdapter(adaptTeams);
            adaptTeams.notifyDataSetChanged();

        } else {
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
            Toast toast = Toast.makeText(getBaseContext(), "** There are _NO_ teams for '" + Pearadox.FRC_ChampDiv + "' **", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }

    }
}

