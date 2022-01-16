package com.pearadox.yg_alliance;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PitCover_Activity extends AppCompatActivity {

    String TAG = "PitCover_Activity";        // This CLASS name
    static final ArrayList<HashMap<String, String>> draftList = new ArrayList<HashMap<String, String>>();
    ListView lstView_Teams;
    String tnum = "";
    String camera = "\uD83D\uDCF7";
    String thumb = "\uD83D\uDC4D";
    String pitData_pres = "";
    String Wt = "";
    String photo_pres = "   ✔ ";
    String Stud = "";
    String DatTim = "";
    String Mesg = "";

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
                new String[] {"team","Stats"},
                new int[] {R.id.TeamData, R.id.Stats}
//                new String[] {"team","BA","Stats"},
//                new int[] {R.id.TeamData,R.id.BA, R.id.Stats}
        );

        draftList.clear();
        String totalScore="";
        p_Firebase.teamsObj My_inst = new p_Firebase.teamsObj();
        Log.w(TAG, " Team array size = " + Pearadox.numTeams);
        pitData the_pits = new pitData();
        draftList.clear();

        if (Pearadox.numTeams > 0) {
            for (int i = 0; i < Pearadox.numTeams; i++) {    // load by sorted scores
                HashMap<String, String> temp = new HashMap<String, String>();
                My_inst = Pearadox.team_List.get(i);
                Log.w(TAG, " Team = " + My_inst.getTeam_num());
                temp.put("team",  My_inst.getTeam_num() + " - " + My_inst.getTeam_name());
                tnum = My_inst.getTeam_num();

                // Find Pit Data (if there)
                boolean found = false;
                for (int x = 0; x < Pearadox.num_Pits; x++) {
                    the_pits = Pearadox.Pit_Data.get(x);
//                            Log.w(TAG, " Team# = '" + tnum + "'  and '" + the_pits.getPit_team() + "'") ;
                    if (the_pits.getPit_team().matches(tnum)) {
                        found = true;
                        pitData_pres = " ✔ ";
                        Wt = String.format("%1$2s", the_pits.getPit_weight());
                        Stud = the_pits.getPit_scout();
                        DatTim = the_pits.getPit_dateTime();
//                                Log.w(TAG, "Ht=" + Ht + "  Scout=" + Stud);
                        String photoStatus = the_pits.getPit_photoURL();
                        Log.w(TAG, "%%%%%%%%% Status = " + photoStatus) ;
                        if (TextUtils.isEmpty(photoStatus)) {
                            photo_pres = "  ❌ ";
                        } else {
                            photo_pres = " ✔ ";
                        }
                    } // Endif
                } //End for #pits
                if (found) {
                    Mesg = "Pit " + thumb + " " + camera + photo_pres + "  Wt=" + Wt + "  @ " + DatTim + "    " + "Scout: " + Stud ;
                } else {
                    Mesg = "";
                }
                temp.put("Stats", Mesg );
//                temp.put("BA",  "     ");
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


    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pitcov, menu);
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
        if (id == R.id.action_refresh) {
            Log.w(TAG, "### Refesh requested ### \n" );
            loadTeams();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

