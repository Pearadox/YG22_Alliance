package com.pearadox.yg_alliance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.util.Log.e;
import static android.util.Log.i;
import static android.util.Log.v;
import static android.util.Log.w;


public class Visualizer_Activity extends AppCompatActivity {

    String TAG = "Visualizer_Activity";        // This CLASS name
    TextView txt_dev, txt_stud;
    public boolean launchViz = false;
     // -----------------------
    ArrayAdapter<String> adapter_typ;
    public String typSelected = " ";
    Spinner spinner_MatchType;
    Spinner spinner_MatchNum;
    ArrayAdapter<String> adapter_Num;
    public String NumSelected = " ";
    ListView listView_Matches;
    ArrayList<String> matchList = new ArrayList<String>();
    ArrayAdapter<String> adaptMatch;
    public int matchSelected = 0;
    public String matchID = "T00";          // Type + #
    public String next_Match = " ";         // List of next Matches for 5414
    TextView txt_EventName, txt_MatchID, txt_NextMatch;
    TextView txt_teamR1, txt_teamR2, txt_teamR3, txt_teamB1, txt_teamB2, txt_teamB3;
    TextView txt_teamR1_Name, txt_teamR2_Name, txt_teamR3_Name, txt_teamB1_Name, txt_teamB2_Name, txt_teamB3_Name;
    TextView tbl_teamR1, tbl_teamR2, tbl_teamR3, tbl_teamB1, tbl_teamB2, tbl_teamB3;
    TextView tbl_event1R1, tbl_event1R2, tbl_event1R3, tbl_event1B1, tbl_event1B2, tbl_event1B3;
    TextView tbl_rate1R1,tbl_rate1R2,tbl_rate1R3, tbl_rate1B1, tbl_rate1B2, tbl_rate1B3;
    TextView tbl_event2R1, tbl_event2R2, tbl_event2R3, tbl_event2B1, tbl_event2B2, tbl_event2B3;
    TextView tbl_rate2R1,tbl_rate2R2,tbl_rate2R3, tbl_rate2B1, tbl_rate2B2, tbl_rate2B3;
    TextView txt_MatchesR1, txt_MatchesR2, txt_MatchesR3, txt_MatchesB1, txt_MatchesB2, txt_MatchesB3;
    TextView txt_baR1, txt_baR2, txt_baR3, txt_baB1, txt_baB2, txt_baB3;

    Button button_View;
    Button btn_PitR1, btn_PitR2, btn_PitR3, btn_PitB1, btn_PitB2, btn_PitB3;
    String team_num, team_name, team_loc;
    String load_team, load_name;
    p_Firebase.teamsObj team_inst = new p_Firebase.teamsObj();
    private FirebaseDatabase pfDatabase;
//    private DatabaseReference pfStudent_DBReference;
//    private DatabaseReference pfDevice_DBReference;
//    private DatabaseReference pfTeam_DBReference;
    private DatabaseReference pfMatch_DBReference;
    private DatabaseReference pfMatchData_DBReference;
    private DatabaseReference pfPitData_DBReference;
//    private DatabaseReference pfCur_Match_DBReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<p_Firebase.teamsObj> Scout_teams = new ArrayList<p_Firebase.teamsObj>();
    ImageView tbl_robotR1, tbl_robotR2, tbl_robotR3, tbl_robotB1, tbl_robotB2, tbl_robotB3;
    String tnum = "";
    boolean pit = false;
    Bitmap img;
    FileOutputStream fos;
    String URL = "";
    String FB_teams[] = new String[]{"","","","","",""};
    String FB_url[] = new String[]{"","","","","",""};
    int FB_num;
    FirebaseStorage storage;
    StorageReference storageRef;
    ArrayList<matchData> Vis_MD = new ArrayList<>();
    matchData match_inst = new matchData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "@@@@  Visualizer_Activity started  @@@@");
        System.out.println(android.R.id.content);       // *** DEBUG ***
        setContentView(R.layout.activity_visualizer);
        Bundle bundle = this.getIntent().getExtras();
        String param1 = bundle.getString("dev");
        String param2 = bundle.getString("stud");
        w(TAG, param1 + " " + param2);      // ** DEBUG **

//      -----------------------------------------

        txt_dev = (TextView) findViewById(R.id.txt_Dev);
        txt_stud = (TextView) findViewById(R.id.txt_TeamName);
        txt_EventName = (TextView) findViewById(R.id.txt_EventName);
        txt_NextMatch = (TextView) findViewById(R.id.txt_NextMatch);
        txt_dev.setText(param1);
        txt_stud.setText(param2);
        txt_NextMatch.setText("");
        txt_EventName.setText(Pearadox.FRC_EventName);
        matchID = "";
        txt_MatchID = (TextView) findViewById(R.id.txt_MatchID);
        txt_MatchID.setText(" ");
        listView_Matches = (ListView) findViewById(R.id.listView_Matches);
        adaptMatch = new ArrayAdapter<String>(this, R.layout.match_list_layout, matchList);
        listView_Matches.setAdapter(adaptMatch);
        adaptMatch.notifyDataSetChanged();

        pfDatabase = FirebaseDatabase.getInstance();
        pfMatch_DBReference = pfDatabase.getReference("matches/" + Pearadox.FRC_Event); // List of Matches
        pfMatchData_DBReference = pfDatabase.getReference("match-data/" + Pearadox.FRC_Event);    // Match Data
        pfPitData_DBReference = pfDatabase.getReference("pit-data/" + Pearadox.FRC_Event); // Pit Scout Data

        clearTeams();
        Button button_View = (Button) findViewById(R.id.button_View);   // Listner defined in Layout XML
//        button_View.setOnClickListener(buttonView_Click);


// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        listView_Matches.setOnItemClickListener(new AdapterView.OnItemClickListener()	{
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {
                w(TAG,"*** listView_Matches ***   Item Selected: " + pos);
                matchSelected = pos;
                listView_Matches.setSelector(android.R.color.holo_blue_light);
        		/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
                int blnk = matchList.get(matchSelected).indexOf(" ");          // 1st blank after MatchID
                matchID = matchList.get(matchSelected).substring(0,blnk);      // GLF 4/20  (112 matches!!)  5/2 2 or 3 digit
                w(TAG,"   MatchID: " + matchID);
                txt_MatchID = (TextView) findViewById(R.id.txt_MatchID);
                txt_MatchID.setText(matchID);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }

    //===================================================================
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
//        Log.e(TAG, "@@@  Options  @@@ ");
//        Log.w(TAG, " \n  \n");
        int id = item.getItemId();
        txt_MatchID = (TextView) findViewById(R.id.txt_MatchID);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_screen) {
            String filNam = Pearadox.FRC_Event.toUpperCase() + "-Viz"  + "_" + txt_MatchID.getText() + ".JPG";
            Log.w(TAG, "File='" + filNam + "'");
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
                Toast toast = Toast.makeText(getBaseContext(), "✪✪✪  Screen captured in Download/FRC5414  ✪✪✪", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();

//                blueTooth_Xfer(imageFile);      // bluetooth transfer to paired Phones

            } catch (Throwable e) {
                // Several error may come out with file handling or DOM
//                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void blueTooth_Xfer(File imgFile) {
//        w(TAG,"*** blueTooth_Xfer ***   file: " + imgFile);
//        BluetoothAdapter BA;
//        Set<BluetoothDevice> pairedDevices;
//
//        BA = BluetoothAdapter.getDefaultAdapter();
//        if (!BA.isEnabled()) {
//            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(turnOn, 0);
//            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
//        } else {
////            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
//        }
//
//        pairedDevices = BA.getBondedDevices();
//
//        ArrayList list = new ArrayList();
//        for(BluetoothDevice bt : pairedDevices) {
//            list.add(bt.getName());
//            Log.v(TAG, "PairedDevices: " + bt.getName() + " " + bt.getAddress() + " |" + bt.getBluetoothClass()+ "| ");
//        }
////        Toast.makeText(getApplicationContext(), "Paired Devices = " + list.size(),Toast.LENGTH_SHORT).show();
//        try
//        {
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_SEND);
//            intent.setType("image/png");
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imgFile) );
//            startActivity(intent);
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(Visualizer_Activity.this, " " + e.getMessage(), Toast.LENGTH_LONG).show();
//
//        }
//
//    }

// =================================================================
    private void clearTeams() {
        i(TAG, "Clearing Team data");
        txt_teamR1 = (TextView) findViewById(R.id.txt_teamR1);
        txt_teamR2 = (TextView) findViewById(R.id.txt_teamR2);
        txt_teamR3 = (TextView) findViewById(R.id.txt_teamR3);
        txt_teamB1 = (TextView) findViewById(R.id.txt_teamB1);
        txt_teamB2 = (TextView) findViewById(R.id.txt_teamB2);
        txt_teamB3 = (TextView) findViewById(R.id.txt_teamB3);
        txt_teamR1_Name = (TextView) findViewById(R.id.txt_teamR1_Name);
        txt_teamR2_Name = (TextView) findViewById(R.id.txt_teamR2_Name);
        txt_teamR3_Name = (TextView) findViewById(R.id.txt_teamR3_Name);
        txt_teamB1_Name = (TextView) findViewById(R.id.txt_teamB1_Name);
        txt_teamB2_Name = (TextView) findViewById(R.id.txt_teamB2_Name);
        txt_teamB3_Name = (TextView) findViewById(R.id.txt_teamB3_Name);
        tbl_teamR1 = (TextView) findViewById(R.id.tbl_teamR1);
        tbl_teamR2 = (TextView) findViewById(R.id.tbl_teamR2);
        tbl_teamR3 = (TextView) findViewById(R.id.tbl_teamR3);
        tbl_teamB1 = (TextView) findViewById(R.id.tbl_teamB1);
        tbl_teamB2 = (TextView) findViewById(R.id.tbl_teamB2);
        tbl_teamB3 = (TextView) findViewById(R.id.tbl_teamB3);
        tbl_event1R1 = (TextView) findViewById(R.id.tbl_event1R1);
        tbl_event1R2 = (TextView) findViewById(R.id.tbl_event1R2);
        tbl_event1R3 = (TextView) findViewById(R.id.tbl_event1R3);
        tbl_event1B1 = (TextView) findViewById(R.id.tbl_event1B1);
        tbl_event1B2 = (TextView) findViewById(R.id.tbl_event1B2);
        tbl_event1B3 = (TextView) findViewById(R.id.tbl_event1B3);
        tbl_rate1R1 = (TextView) findViewById(R.id.tbl_rate1R1);
        tbl_rate1R2 = (TextView) findViewById(R.id.tbl_rate1R2);
        tbl_rate1R3 = (TextView) findViewById(R.id.tbl_rate1R3);
        tbl_rate1B1 = (TextView) findViewById(R.id.tbl_rate1B1);
        tbl_rate1B2 = (TextView) findViewById(R.id.tbl_rate1B2);
        tbl_rate1B3 = (TextView) findViewById(R.id.tbl_rate1B3);
        tbl_event2R1 = (TextView) findViewById(R.id.tbl_event2R1);
        tbl_event2R2 = (TextView) findViewById(R.id.tbl_event2R2);
        tbl_event2R3 = (TextView) findViewById(R.id.tbl_event2R3);
        tbl_event2B1 = (TextView) findViewById(R.id.tbl_event2B1);
        tbl_event2B2 = (TextView) findViewById(R.id.tbl_event2B2);
        tbl_event2B3 = (TextView) findViewById(R.id.tbl_event2B3);
        tbl_rate2R1 = (TextView) findViewById(R.id.tbl_rate2R1);
        tbl_rate2R2 = (TextView) findViewById(R.id.tbl_rate2R2);
        tbl_rate2R3 = (TextView) findViewById(R.id.tbl_rate2R3);
        tbl_rate2B1 = (TextView) findViewById(R.id.tbl_rate2B1);
        tbl_rate2B2 = (TextView) findViewById(R.id.tbl_rate2B2);
        tbl_rate2B3 = (TextView) findViewById(R.id.tbl_rate2B3);
        txt_MatchesR1 = (TextView) findViewById(R.id.txt_MatchesR1);
        txt_MatchesR2 = (TextView) findViewById(R.id.txt_MatchesR2);
        txt_MatchesR3 = (TextView) findViewById(R.id.txt_MatchesR3);
        txt_MatchesB1 = (TextView) findViewById(R.id.txt_MatchesB1);
        txt_MatchesB2 = (TextView) findViewById(R.id.txt_MatchesB2);
        txt_MatchesB3 = (TextView) findViewById(R.id.txt_MatchesB3);
        txt_baR1 = (TextView) findViewById(R.id.txt_baR1);
        txt_baR2 = (TextView) findViewById(R.id.txt_baR2);
        txt_baR3 = (TextView) findViewById(R.id.txt_baR3);
        txt_baB1 = (TextView) findViewById(R.id.txt_baB1);
        txt_baB2 = (TextView) findViewById(R.id.txt_baB2);
        txt_baB3 = (TextView) findViewById(R.id.txt_baB3);

        txt_teamR1.setText("");
        txt_teamR2.setText("");
        txt_teamR3.setText("");
        txt_teamB1.setText("");
        txt_teamB2.setText("");
        txt_teamB3.setText("");

        txt_teamR1_Name.setText("");
        txt_teamR2_Name.setText("");
        txt_teamR3_Name.setText("");
        txt_teamB1_Name.setText("");
        txt_teamB2_Name.setText("");
        txt_teamB3_Name.setText("");

        tbl_teamR1.setText("");
        tbl_teamR2.setText("");
        tbl_teamR3.setText("");
        tbl_teamB1.setText("");
        tbl_teamB2.setText("");
        tbl_teamB3.setText("");

        tbl_event1R1.setText("");
        tbl_event1R2.setText("");
        tbl_event1R3.setText("");
        tbl_event1B1.setText("");
        tbl_event1B2.setText("");
        tbl_event1B3.setText("");
        tbl_rate1R1.setText("");
        tbl_rate1R2.setText("");
        tbl_rate1R3.setText("");
        tbl_rate1B1.setText("");
        tbl_rate1B2.setText("");
        tbl_rate1B3.setText("");

        tbl_event2R1.setText("");
        tbl_event2R2.setText("");
        tbl_event2R3.setText("");
        tbl_event2B1.setText("");
        tbl_event2B2.setText("");
        tbl_event2B3.setText("");
        tbl_rate2R1.setText("");
        tbl_rate2R2.setText("");
        tbl_rate2R3.setText("");
        tbl_rate2B1.setText("");
        tbl_rate2B2.setText("");
        tbl_rate2B3.setText("");
        txt_MatchesR1.setText("");
        txt_MatchesR2.setText("");
        txt_MatchesR3.setText("");
        txt_MatchesB1.setText("");
        txt_MatchesB2.setText("");
        txt_MatchesB3.setText("");

        txt_baR1.setText("");
        txt_baR2.setText("");
        txt_baR3.setText("");
        txt_baB1.setText("");
        txt_baB2.setText("");
        txt_baB3.setText("");


        ImageView tbl_robotR1 = (ImageView) findViewById(R.id.tbl_robotR1);
        ImageView tbl_robotR2 = (ImageView) findViewById(R.id.tbl_robotR2);
        ImageView tbl_robotR3 = (ImageView) findViewById(R.id.tbl_robotR3);
        ImageView tbl_robotB1 = (ImageView) findViewById(R.id.tbl_robotB1);
        ImageView tbl_robotB2 = (ImageView) findViewById(R.id.tbl_robotB2);
        ImageView tbl_robotB3 = (ImageView) findViewById(R.id.tbl_robotB3);
        tbl_robotR1.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        tbl_robotR2.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        tbl_robotR3.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        tbl_robotB1.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        tbl_robotB2.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        tbl_robotB3.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));

    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void btn_PitR1_Click(View view) {
        i(TAG, " btn_PitR1_Click   ");
        txt_teamR1 = (TextView) findViewById(R.id.txt_teamR1);
        txt_teamR1_Name = (TextView) findViewById(R.id.txt_teamR1_Name);
        tnum = (String) txt_teamR1.getText();
        team_name = (String) txt_teamR1_Name.getText();
        w(TAG, "*** Team " + tnum + " " + team_name + "  URL ='" + FB_url[0] + "' ");
        launchVizPit(tnum, team_name, FB_url[0]);
    }
    public void btn_PitR2_Click(View view) {
        i(TAG, " btn_PitR2_Click   ");
        txt_teamR2 = (TextView) findViewById(R.id.txt_teamR2);
        txt_teamR2_Name = (TextView) findViewById(R.id.txt_teamR2_Name);
        tnum = (String) txt_teamR2.getText();
        team_name = (String) txt_teamR2_Name.getText();
        launchVizPit(tnum, team_name, FB_url[1]);
    }
    public void btn_PitR3_Click(View view) {
        i(TAG, " btn_PitR3_Click   ");
        txt_teamR3 = (TextView) findViewById(R.id.txt_teamR3);
        txt_teamR3_Name = (TextView) findViewById(R.id.txt_teamR3_Name);
        tnum = (String) txt_teamR3.getText();
        team_name = (String) txt_teamR3_Name.getText();
        launchVizPit(tnum, team_name, FB_url[2]);
    }
    public void btn_PitB1_Click(View view) {
        i(TAG, " btn_PitB1_Click   ");
        txt_teamB1 = (TextView) findViewById(R.id.txt_teamB1);
        txt_teamB1_Name = (TextView) findViewById(R.id.txt_teamB1_Name);
        tnum = (String) txt_teamB1.getText();
        team_name = (String) txt_teamB1_Name.getText();
        launchVizPit(tnum, team_name, FB_url[3]);
    }
    public void btn_PitB2_Click(View view) {
        i(TAG, " btn_PitB2_Click   ");
        txt_teamB2 = (TextView) findViewById(R.id.txt_teamB2);
        txt_teamB2_Name = (TextView) findViewById(R.id.txt_teamB2_Name);
        tnum = (String) txt_teamB2.getText();
        team_name = (String) txt_teamB2_Name.getText();
        launchVizPit(tnum, team_name, FB_url[4]);
    }
    public void btn_PitB3_Click(View view) {
        i(TAG, " btn_PitB3_Click   ");
        txt_teamB3 = (TextView) findViewById(R.id.txt_teamB3);
        txt_teamB3_Name = (TextView) findViewById(R.id.txt_teamB3_Name);
        tnum = (String) txt_teamB3.getText();
        team_name = (String) txt_teamB3_Name.getText();
        launchVizPit(tnum, team_name, FB_url[5]);
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void btn_MatchR1_Click(View view) {
        i(TAG, " btn_MatchR1_Click   ");
        txt_teamR1 = (TextView) findViewById(R.id.txt_teamR1);
        txt_teamR1_Name = (TextView) findViewById(R.id.txt_teamR1_Name);
        tnum = (String) txt_teamR1.getText();
        team_name = (String) txt_teamR1_Name.getText();
        w(TAG, "*** Team " + tnum + " " + team_name);
        launchVizMatch(tnum, team_name);
    }
    public void btn_MatchR2_Click(View view) {
        i(TAG, " btn_MatchR2_Click   ");
        txt_teamR2 = (TextView) findViewById(R.id.txt_teamR2);
        txt_teamR2_Name = (TextView) findViewById(R.id.txt_teamR2_Name);
        tnum = (String) txt_teamR2.getText();
        team_name = (String) txt_teamR2_Name.getText();
        launchVizMatch(tnum, team_name);
    }
    public void btn_MatchR3_Click(View view) {
        i(TAG, " btn_MatchR3_Click   ");
        txt_teamR3 = (TextView) findViewById(R.id.txt_teamR3);
        txt_teamR3_Name = (TextView) findViewById(R.id.txt_teamR3_Name);
        tnum = (String) txt_teamR3.getText();
        team_name = (String) txt_teamR3_Name.getText();
        launchVizMatch(tnum, team_name);
    }
    public void btn_MatchB1_Click(View view) {
        i(TAG, " btn_MatchB1_Click   ");
        txt_teamB1 = (TextView) findViewById(R.id.txt_teamB1);
        txt_teamB1_Name = (TextView) findViewById(R.id.txt_teamB1_Name);
        tnum = (String) txt_teamB1.getText();
        team_name = (String) txt_teamB1_Name.getText();
        launchVizMatch(tnum, team_name);
    }
    public void btn_MatchB2_Click(View view) {
        i(TAG, " btn_MatchB2_Click   ");
        txt_teamB2 = (TextView) findViewById(R.id.txt_teamB2);
        txt_teamB2_Name = (TextView) findViewById(R.id.txt_teamB2_Name);
        tnum = (String) txt_teamB2.getText();
        team_name = (String) txt_teamB2_Name.getText();
        launchVizMatch(tnum, team_name);
    }
    public void btn_MatchB3_Click(View view) {
        i(TAG, " btn_MatchB3_Click   ");
        txt_teamB3 = (TextView) findViewById(R.id.txt_teamB3);
        txt_teamB3_Name = (TextView) findViewById(R.id.txt_teamB3_Name);
        tnum = (String) txt_teamB3.getText();
        team_name = (String) txt_teamB3_Name.getText();
        launchVizMatch(tnum, team_name);
    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void launchVizMatch(String team, String name) {
        i(TAG, ">>>>> launchVizMatch   <<<<<"  + team + " " + name);

        launchViz = true;
        load_team = team;
        load_name = name;
        addMD_VE_Listener(pfMatchData_DBReference.orderByChild("match"));        // Load Matches
//        childTeamMD_Listner();

    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//    private void childTeamMD_Listner() {
    private void addMD_VE_Listener(final Query pfMatchData_DBReference) {
        i(TAG, "<<<< getFB_Data >>>> Match Data for team " + load_team);

            pfMatchData_DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.w(TAG, "@@@@ onChildAdded @@@@ Match Data for team " + load_team);
                Pearadox.Matches_Data.clear();
                Vis_MD.clear();
                matchData mdobj = new matchData();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
//                    w(TAG, " WHILE: "  + Pearadox.Matches_Data.size());
//                    System.out.println(dataSnapshot.getValue());
//                    System.out.println("  \n  \n");
                    mdobj = iterator.next().getValue(matchData.class);
                    Vis_MD.add(mdobj);      // add _ALL_ matches to this array
                    if (mdobj.getTeam_num().matches(load_team)) {
                        Log.w(TAG, "★★★★ Match: " + mdobj.getMatch() + " # "  + Pearadox.Matches_Data.size());
//                        System.out.println("  \n");
                        Pearadox.Matches_Data.add(mdobj);   // load data for team specified
                    }
                }
                Log.w(TAG, "***** Matches Loaded. # = " + Vis_MD.size() + "  Team# = " + Pearadox.Matches_Data.size());
                getMatchForTeams();     // Get Match Data for each team

                if(launchViz) {
                    if (Pearadox.Matches_Data.size() > 0) {
                        Intent pit_intent = new Intent(Visualizer_Activity.this, VisMatch_Activity.class);
                        Bundle VZbundle = new Bundle();
                        VZbundle.putString("team", load_team);        // Pass data to activity
                        VZbundle.putString("name", load_name);        // Pass data to activity
                        pit_intent.putExtras(VZbundle);
                        startActivity(pit_intent);               // Start Visualizer for Match Data
                    } else {
                        Toast toast = Toast.makeText(getBaseContext(), "★★★★  There is _NO_ Match Data for Team " + load_team + "  ★★★★", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();

                    }
                }
            }
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.w(TAG, "%%%  ChildChanged");
//            }
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.w(TAG, "%%%  ChildRemoved");
//            }
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                Log.w(TAG, "%%%  ChildMoved");
//            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void launchVizPit(String team, String name, String imgURL) {
        i(TAG,">>>>>>>>  launchVizPit " + team + " " + name + " " + imgURL);      // ** DEBUG **
        Intent pit_intent = new Intent(Visualizer_Activity.this, VisPit_Activity.class);
        Bundle VZbundle = new Bundle();
        VZbundle.putString("team", team);        // Pass data to activity
        VZbundle.putString("name", name);        // Pass data to activity
        VZbundle.putString("url", imgURL);       // Pass data to activity
        pit_intent.putExtras(VZbundle);
        startActivity(pit_intent);               // Start Visualizer for Pit Data

    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private class type_OnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            typSelected = parent.getItemAtPosition(pos).toString();
            w(TAG, ">>>>>  '" + typSelected + "'");
            switch (typSelected) {
                case "Practice":        // Practice round
                    matchID = "X";
                    break;
                case "Qualifying":        // Qualifying round
                    matchID = "Q";
                    break;
                case "Playoff":        // Playoff round
                    matchID = "P";
                    break;
                default:                // ????
                    e(TAG, "*** Error - bad TYPE indicator  ***");
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private class mNum_OnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            NumSelected = parent.getItemAtPosition(pos).toString();
            w(TAG, ">>>>>  '" + NumSelected + "'");
            matchID = matchID + NumSelected;
            w(TAG, ">>>>>  Match = '" + matchID + "'");
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void buttonView_Click(View view) {
        Log.w(TAG, " View Click  " + matchID);

        clearTeams();
        getTeams();             // Get the teams for match selected
    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void loadTblData () {
        i(TAG, "#### loadTblData  ####");
        // Start getting data for Table
        ImageView tbl_robotR1 = (ImageView) findViewById(R.id.tbl_robotR1);
        ImageView tbl_robotR2 = (ImageView) findViewById(R.id.tbl_robotR2);
        ImageView tbl_robotR3 = (ImageView) findViewById(R.id.tbl_robotR3);
        ImageView tbl_robotB1 = (ImageView) findViewById(R.id.tbl_robotB1);
        ImageView tbl_robotB2 = (ImageView) findViewById(R.id.tbl_robotB2);
        ImageView tbl_robotB3 = (ImageView) findViewById(R.id.tbl_robotB3);
        btn_PitR1 = (Button) findViewById(R.id.btn_PitR1);
        btn_PitR2 = (Button) findViewById(R.id.btn_PitR2);
        btn_PitR3 = (Button) findViewById(R.id.btn_PitR3);
        btn_PitB1 = (Button) findViewById(R.id.btn_PitB1);
        btn_PitB2 = (Button) findViewById(R.id.btn_PitB2);
        btn_PitB3 = (Button) findViewById(R.id.btn_PitB3);

        tnum = (String) txt_teamR1.getText();
//        FB_teams[0] = tnum;
        tbl_robotR1.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        btn_PitR1.setEnabled(false);
        getURL0(tnum);       // Get URLs for Robot photos (if they exist) and load them
        tnum = (String) txt_teamR2.getText();
        tbl_robotR2.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        btn_PitR2.setEnabled(false);
        getURL1(tnum);       // Get URLs for Robot photos (if they exist) and load them
        tnum = (String) txt_teamR3.getText();
        tbl_robotR3.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        btn_PitR3.setEnabled(false);
        getURL2(tnum);       // Get URLs for Robot photos (if they exist) and load them
        tnum = (String) txt_teamB1.getText();
        tbl_robotB1.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        btn_PitB1.setEnabled(false);
        getURL3(tnum);       // Get URLs for Robot photos (if they exist) and load them
        tnum = (String) txt_teamB2.getText();
        tbl_robotB2.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        btn_PitB2.setEnabled(false);
        getURL4(tnum);       // Get URLs for Robot photos (if they exist) and load them
        tnum = (String) txt_teamB3.getText();
        tbl_robotB3.setImageDrawable(getResources().getDrawable(R.drawable.photo_missing));
        btn_PitB3.setEnabled(false);
        getURL5(tnum);       // Get URLs for Robot photos (if they exist) and load them

    }

    private void getURL0(String team) {
        i(TAG, ">>>>>  getURL0: " + team);

//        ImageView tbl_robotR1 = (ImageView) findViewById(R.id.tbl_robotR1);
//        ImageView tbl_robotR2 = (ImageView) findViewById(R.id.tbl_robotR2);
//        ImageView tbl_robotR3 = (ImageView) findViewById(R.id.tbl_robotR3);
//        ImageView tbl_robotB1 = (ImageView) findViewById(R.id.tbl_robotB1);
//        ImageView tbl_robotB2 = (ImageView) findViewById(R.id.tbl_robotB2);
//        ImageView tbl_robotB3 = (ImageView) findViewById(R.id.tbl_robotB3);
        URL = "";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2022.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + team.trim() + ".png");
        e(TAG, "images/" + Pearadox.FRC_Event + "/robot_" + team.trim() + ".png" + "\n \n");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                e(TAG,  FB_num + "  uri: " + uri.toString());
                ImageView tbl_robotR1 = (ImageView) findViewById(R.id.tbl_robotR1);
                btn_PitR1 = (Button) findViewById(R.id.btn_PitR1);
                URL = uri.toString();
                FB_url[0] = URL;
                if (URL.length() > 0) {
                    Picasso.with(Visualizer_Activity.this).load(URL).into(tbl_robotR1);
                    btn_PitR1.setEnabled(true);
                } else {
                    // ToDo - GLF turn off Pit button
                    btn_PitR1.setEnabled(false);
                }
            }
        });

        //        Toast.makeText(getBaseContext(), "Robot images loaded", Toast.LENGTH_LONG).show();  //** DEBUG
    }
    private void getURL1(String team) {
        i(TAG, ">>>>>  getURL1: " + team);
        URL = "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2022.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + team.trim() + ".png");
        e(TAG, "images/" + Pearadox.FRC_Event + "/robot_" + team.trim() + ".png" + "\n \n");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                e(TAG,  FB_num + "  uri: " + uri.toString());
                ImageView tbl_robotR2 = (ImageView) findViewById(R.id.tbl_robotR2);
                btn_PitR2 = (Button) findViewById(R.id.btn_PitR2);
                URL = uri.toString();
                FB_url[1] = URL;
                if (URL.length() > 0) {
                    Picasso.with(Visualizer_Activity.this).load(URL).into(tbl_robotR2);
                    btn_PitR2.setEnabled(true);
                } else {
                }
            }
        });
    }
    private void getURL2(String team) {
        i(TAG, ">>>>>  getURL2: " + team);
        URL = "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2022.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + team.trim() + ".png");
        e(TAG, "images/" + Pearadox.FRC_Event + "/robot_" + team.trim() + ".png" + "\n \n");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                e(TAG,  FB_num + "  uri: " + uri.toString());
                ImageView tbl_robotR3 = (ImageView) findViewById(R.id.tbl_robotR3);
                btn_PitR3 = (Button) findViewById(R.id.btn_PitR3);
                URL = uri.toString();
                FB_url[2] = URL;
                if (URL.length() > 0) {
                    Picasso.with(Visualizer_Activity.this).load(URL).into(tbl_robotR3);
                    btn_PitR3.setEnabled(true);
                } else {
                    btn_PitR3.setEnabled(false);
                }
            }
        });
    }
    private void getURL3(String team) {
        i(TAG, ">>>>>  getURL3: " + team);
        URL = "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2022.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + team.trim() + ".png");
        e(TAG, "images/" + Pearadox.FRC_Event + "/robot_" + team.trim() + ".png" + "\n \n");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                e(TAG,  FB_num + "  uri: " + uri.toString());
                ImageView tbl_robotB1 = (ImageView) findViewById(R.id.tbl_robotB1);
                btn_PitB1 = (Button) findViewById(R.id.btn_PitB1);
                URL = uri.toString();
                FB_url[3] = URL;
                if (URL.length() > 0) {
                    Picasso.with(Visualizer_Activity.this).load(URL).into(tbl_robotB1);
                    btn_PitB1.setEnabled(true);
                } else {
                }
            }
        });
    }
    private void getURL4(String team) {
        i(TAG, ">>>>>  getURL4: " + team);
        URL = "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2022.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + team.trim() + ".png");
        e(TAG, "images/" + Pearadox.FRC_Event + "/robot_" + team.trim() + ".png" + "\n \n");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                e(TAG,  FB_num + "  uri: " + uri.toString());
                ImageView tbl_robotB2 = (ImageView) findViewById(R.id.tbl_robotB2);
                btn_PitB2 = (Button) findViewById(R.id.btn_PitB2);
                URL = uri.toString();
                FB_url[4] = URL;
                if (URL.length() > 0) {
                    Picasso.with(Visualizer_Activity.this).load(URL).into(tbl_robotB2);
                    btn_PitB2.setEnabled(true);
                } else {
                }
            }
        });
    }
    private void getURL5(String team) {
        i(TAG, ">>>>>  getURL5: " + team);
        URL = "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pearadox-2022.appspot.com/images/" + Pearadox.FRC_Event).child("robot_" + team.trim() + ".png");
        e(TAG, "images/" + Pearadox.FRC_Event + "/robot_" + team.trim() + ".png" + "\n \n");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                e(TAG,  FB_num + "  uri: " + uri.toString());
                ImageView tbl_robotB3 = (ImageView) findViewById(R.id.tbl_robotB3);
                btn_PitB3 = (Button) findViewById(R.id.btn_PitB3);
                URL = uri.toString();
                FB_url[5] = URL;
                if (URL.length() > 0) {
                    Picasso.with(Visualizer_Activity.this).load(URL).into(tbl_robotB3);
                    btn_PitB3.setEnabled(true);
                } else {
                }
            }
        });
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void getTeams() {
        i(TAG, "$$$$$  getTeams");
        w(TAG, ">>>>>  Match = '" + matchID + "'");
        final int[] rank = {0};
        txt_teamR1 = (TextView) findViewById(R.id.txt_teamR1);
        txt_teamR2 = (TextView) findViewById(R.id.txt_teamR2);
        txt_teamR3 = (TextView) findViewById(R.id.txt_teamR3);
        txt_teamB1 = (TextView) findViewById(R.id.txt_teamB1);
        txt_teamB2 = (TextView) findViewById(R.id.txt_teamB2);
        txt_teamB3 = (TextView) findViewById(R.id.txt_teamB3);
        txt_teamR1_Name = (TextView) findViewById(R.id.txt_teamR1_Name);
        txt_teamR2_Name = (TextView) findViewById(R.id.txt_teamR2_Name);
        txt_teamR3_Name = (TextView) findViewById(R.id.txt_teamR3_Name);
        txt_teamB1_Name = (TextView) findViewById(R.id.txt_teamB1_Name);
        txt_teamB2_Name = (TextView) findViewById(R.id.txt_teamB2_Name);
        txt_teamB3_Name = (TextView) findViewById(R.id.txt_teamB3_Name);

//        int z = matchID.length();
        if (matchID.length() >= 3) {        // Worlds = 103 matches!!  GLF 4/17
            i(TAG, "\n   Q U E R Y  ");
            String child = "match";
            String key = matchID;
            Query query = pfMatch_DBReference.orderByChild(child).equalTo(key);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    w(TAG, "%%%  ChildAdded");
//                    System.out.println(dataSnapshot.getValue());
                    p_Firebase.matchObj mobj = dataSnapshot.getValue(p_Firebase.matchObj.class);
//                    System.out.println("Match: " + mobj.getMatch());
//                    System.out.println("Type: " + mobj.getMtype());
//                    System.out.println("Date: " + mobj.getDate());
//                    System.out.println("R1: " + mobj.getR1());
//                    System.out.println("B3: " + mobj.getB3());
                    Scout_teams.clear();          // empty the list
                    String tn = mobj.getR1();
                    findTeam(tn);
                    load_team = tn;
                    launchViz =false;   // Don't launch VisMatch
                    addMD_VE_Listener(pfMatchData_DBReference.orderByChild("match"));        // Load ALL Match data into Vis_MD
                    tn = mobj.getR2();
                    findTeam(tn);
                    tn = mobj.getR3();
                    findTeam(tn);
                    tn = mobj.getB1();
                    findTeam(tn);
                    tn = mobj.getB2();
                    findTeam(tn);
                    tn = mobj.getB3();
                    findTeam(tn);
                    load_team = tn;

                    txt_teamR1 = (TextView) findViewById(R.id.txt_teamR1);
                    txt_teamR2 = (TextView) findViewById(R.id.txt_teamR2);
                    txt_teamR3 = (TextView) findViewById(R.id.txt_teamR3);
                    txt_teamB1 = (TextView) findViewById(R.id.txt_teamB1);
                    txt_teamB2 = (TextView) findViewById(R.id.txt_teamB2);
                    txt_teamB3 = (TextView) findViewById(R.id.txt_teamB3);
                    txt_teamR1_Name = (TextView) findViewById(R.id.txt_teamR1_Name);
                    txt_teamR2_Name = (TextView) findViewById(R.id.txt_teamR2_Name);
                    txt_teamR3_Name = (TextView) findViewById(R.id.txt_teamR3_Name);
                    txt_teamB1_Name = (TextView) findViewById(R.id.txt_teamB1_Name);
                    txt_teamB2_Name = (TextView) findViewById(R.id.txt_teamB2_Name);
                    txt_teamB3_Name = (TextView) findViewById(R.id.txt_teamB3_Name);
                    tbl_teamR1 = (TextView) findViewById(R.id.tbl_teamR1);
                    tbl_teamR2 = (TextView) findViewById(R.id.tbl_teamR2);
                    tbl_teamR3 = (TextView) findViewById(R.id.tbl_teamR3);
                    tbl_teamB1 = (TextView) findViewById(R.id.tbl_teamB1);
                    tbl_teamB2 = (TextView) findViewById(R.id.tbl_teamB2);
                    tbl_teamB3 = (TextView) findViewById(R.id.tbl_teamB3);

                    w(TAG, "@@@@@@@@@@@@@@@@@@@ GET TEAM DATA  @@@@@@@@@@@@@@@@@@@");  //** DEBUG

                    String x = "";
                    team_inst = Scout_teams.get(0);       // Team#R1
                    txt_teamR1.setText(team_inst.getTeam_num());
                    txt_teamR1_Name.setText(team_inst.getTeam_name());
                    x = "Rank="+ team_inst.getTeam_rank() + "   Score=" + team_inst.getTeam_rScore() + "\nWLT=" + team_inst.getTeam_WLT() + "   OPR=" + team_inst.getTeam_OPR();
                    txt_baR1.setText(x);
                    tbl_teamR1.setText(team_inst.getTeam_num());
                    team_inst = Scout_teams.get(1);       // Team#R2
                    txt_teamR2.setText(team_inst.getTeam_num());
                    txt_teamR2_Name.setText(team_inst.getTeam_name());
                    x = "Rank="+ team_inst.getTeam_rank() + "   Score=" + team_inst.getTeam_rScore() + "\nWLT=" + team_inst.getTeam_WLT() + "   OPR=" + team_inst.getTeam_OPR();
                    txt_baR2.setText(x);
                    tbl_teamR2.setText(team_inst.getTeam_num());
                    team_inst = Scout_teams.get(2);       // Team#R3
                    txt_teamR3.setText(team_inst.getTeam_num());
                    txt_teamR3_Name.setText(team_inst.getTeam_name());
                    x = "Rank="+ team_inst.getTeam_rank() + "   Score=" + team_inst.getTeam_rScore() + "\nWLT=" + team_inst.getTeam_WLT() + "   OPR=" + team_inst.getTeam_OPR();
                    txt_baR3.setText(x);
                    tbl_teamR3.setText(team_inst.getTeam_num());
                    team_inst = Scout_teams.get(3);       // Team#B1
                    txt_teamB1.setText(team_inst.getTeam_num());
                    txt_teamB1_Name.setText(team_inst.getTeam_name());
                    x = "Rank="+ team_inst.getTeam_rank() + "   Score=" + team_inst.getTeam_rScore() + "\nWLT=" + team_inst.getTeam_WLT() + "   OPR=" + team_inst.getTeam_OPR();
                    txt_baB1.setText(x);
                    tbl_teamB1.setText(team_inst.getTeam_num());
                    team_inst = Scout_teams.get(4);       // Team#B2
                    txt_teamB2.setText(team_inst.getTeam_num());
                    txt_teamB2_Name.setText(team_inst.getTeam_name());
                    x = "Rank="+ team_inst.getTeam_rank() + "   Score=" + team_inst.getTeam_rScore() + "\nWLT=" + team_inst.getTeam_WLT() + "   OPR=" + team_inst.getTeam_OPR();
                    txt_baB2.setText(x);
                    tbl_teamB2.setText(team_inst.getTeam_num());
                    team_inst = Scout_teams.get(5);       // Team#B3
                    txt_teamB3.setText(team_inst.getTeam_num());
                    txt_teamB3_Name.setText(team_inst.getTeam_name());
                    x = "Rank="+ team_inst.getTeam_rank() + "   Score=" + team_inst.getTeam_rScore() + "\nWLT=" + team_inst.getTeam_WLT() + "   OPR=" + team_inst.getTeam_OPR();
                    txt_baB3.setText(x);
                    tbl_teamB3.setText(team_inst.getTeam_num());
                    Log.w(TAG, "+++++++++; size = " + Scout_teams.size());

//                    w(TAG, "***  Calling Async class  ***");  //** DEBUG
//                    new Load_MatchData_Task().execute();     // Load match data Asyncronously
                    loadTblData();      // Load the images (if any)

                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    w(TAG, "%%%  ChildChanged");
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    w(TAG, "%%%  ChildRemoved");
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    w(TAG, "%%%  ChildMoved");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(getBaseContext(), "** Select both Match TYPE & NUMBER ** ", Toast.LENGTH_LONG).show();
            // ToDo - turn toggle back to logon
        }
        Log.e(TAG, "<<<<<< End of getteams; size = " + Scout_teams.size());
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void getMatchForTeams() {
        Log.w(TAG, "$$$$$  getMatchForTeams " + Pearadox.Matches_Data.size());
        String team="";
        int md = Vis_MD.size();
        if (md > 0) {
            int ndx=0;
            for(int x = 0; x < 6; x++) {
                team_inst = Scout_teams.get(x);
                team = team_inst.getTeam_num();        // Team#
                Log.w(TAG, "TEAM  " + team);
                ndx = x;
                Log.w(TAG, "NDX  " + ndx);
                int numMDs = 0; int AcellNumUnder = 0; int AcellNumLine = 0; int AcellNumCPFront = 0; int cgGt1 = 0; int base = 0;
                int A_lowNum = 0; int T_lowNum = 0; int A_highNum = 0; int T_highNum = 0; String A_LowPercent = ""; String A_HiPercent = ""; String T_LowPercent = ""; String T_HiPercent = "";
                int A_misslowNum = 0; int T_misslowNum = 0; int A_misshighNum = 0; int T_misshighNum = 0;
                int LostComs = 0; int LostParts = 0; int Tipped = 0;
                int Def1 = 0; int Def2 = 0; int Def3 = 0; int Def4 = 0;
                int Hang0=0; int Hang1=0; int Hang2=0; int Hang3=0; int Hang4=0; int climbed=0; int pen=0; int floor=0; int tfloor=0;

                for (int i = 0; i < md; i++) {
                match_inst = Vis_MD.get(i);      // Get instance of Match Data
                String mdt = match_inst.getTeam_num();
                if (mdt.matches(team)) {        // is this match data for the team we are working on?
                Log.w(TAG, "GMFT TEAM  " + mdt);
                    numMDs++;       // increment # of MDs
                    if (match_inst.isAuto_leftTarmac()) {      // Left Tarmac
                        base++;
                    }
                    A_lowNum = A_lowNum + match_inst.getAuto_Low();
                    A_highNum = A_highNum + match_inst.getAuto_High();
                    A_misslowNum = A_misslowNum + match_inst.getAuto_MissedLow();
                    A_misshighNum = A_misshighNum + match_inst.getAuto_MissedHigh();
                    Float BatAvg;
                    Log.e(TAG, "AutoLow   Score" + A_lowNum  +"  Miss" +A_misslowNum);
                    if ((A_lowNum + A_misslowNum) > 0) {
                        BatAvg = (float)A_lowNum / (A_lowNum + A_misslowNum);  // Made ÷ Attempts
//                        Log.e(TAG, "Low%= " + BatAvg);
                        if (BatAvg == 1.0f) {
                            A_LowPercent = "1.0";
                        } else {
                            A_LowPercent = String.format("%.2f", BatAvg);
                        }
                    } else {
                        A_LowPercent = ".00";
                    }
                    if ((A_highNum + A_misshighNum) > 0) {
                        BatAvg = (float)A_highNum / (A_highNum + A_misshighNum);  // Made ÷ Attempts
                        if (BatAvg == 1.0f) {
                            A_HiPercent = "1.0";
                        } else {
                            A_HiPercent =String.format("%.2f", BatAvg);
                        }
                    } else {
                        A_HiPercent = ".00";
                    }

                    // ********************  Tele  ********************
                    T_lowNum = T_lowNum + match_inst.getTele_Low();
                    T_highNum = T_highNum + match_inst.getTele_High();
                    T_misslowNum = T_misslowNum + match_inst.getTele_MissedLow();
                    T_misshighNum = T_misshighNum + match_inst.getTele_MissedHigh();
                    Log.e(TAG, "TeleLow   Score" + T_lowNum  +"  Miss" +T_misslowNum);
                    if ((T_lowNum + T_misslowNum) > 0) {        // avoid divide by 0
                        BatAvg = (float)T_lowNum / (T_lowNum + T_misslowNum);  // Made ÷ Attempts
                        if (BatAvg == 1.0f) {       // all this to get 3 digits!!
                            T_LowPercent = "1.0";
                        } else {
//                            Log.e(TAG, "TeleLow%= " + BatAvg +" L" + T_lowNum  +"  M" +T_misslowNum);
                            T_LowPercent = String.format("%.2f", BatAvg);
                        }
                    } else {
//                        Log.e(TAG, "Default TeleLow = .000");
                        T_LowPercent = ".00";
                    }
                    if ((T_highNum + T_misshighNum) > 0) {
                        BatAvg = (float)T_highNum / (T_highNum + T_misshighNum);    // Made ÷ Attempts
                        if (BatAvg == 1.0f) {
                            T_HiPercent = "1.0";
                        } else {
//                            Log.e(TAG, "TeleHigh%= " + BatAvg+" L" + T_highNum  +"  M" +T_misshighNum);
                            T_HiPercent = String.format("%.2f", BatAvg);
                        }
                    } else {
                        T_HiPercent = ".00";
                    }

                    if (match_inst.isTele_Climbed()) {
                        climbed++;
                    }
                    switch (match_inst.getTele_HangarLevel()) {
                        case "None":
                            Hang0++;
                            break;
                        case "Low":
                            Hang1++;
                            break;
                        case "Mid":
                            Hang2++;
                            break;
                        case "High":
                            Hang3++;
                            break;
                        case "Traversal":
                            Hang4++;
                            break;
                        default:                // ????
                            Log.e(TAG, "*** Error - bad Hangar Level  ***");
                    } // end Switch

                    pen = pen + match_inst.getTele_num_Penalties(); ;

                    // *************************************************
                    // ********************  Final  ********************
                    // *************************************************
                    if (match_inst.isFinal_lostComms()) {
                        LostComs++;
                    }
                    if (match_inst.isFinal_lostParts()) {
                        LostParts++;
                    }
                    if (match_inst.isFinal_tipped()) {
                        Tipped++;
                    }
                    switch (match_inst.getFinal_defense()) {
                        case "Bad":
                            Def1++;
                            break;
                        case "Avg":
                            Def2++;
                            break;
                        case "Good":
                            Def3++;
                            break;
                        case "Not Observed (None)":
                            Def4++;
                            break;
                        default:                // ????
                            Log.e(TAG, "*** Error - bad Hangar Level  ***");
                    } // end Switch

                } // EndIf teams match
            } // End for #teams
            Log.e(TAG, team + " ==== Match Data " +  base + "  " +  AcellNumUnder );
            tbl_event1R1 = (TextView) findViewById(R.id.tbl_event1R1);
            tbl_event1R2 = (TextView) findViewById(R.id.tbl_event1R2);
            tbl_event1R3 = (TextView) findViewById(R.id.tbl_event1R3);
            tbl_event1B1 = (TextView) findViewById(R.id.tbl_event1B1);
            tbl_event1B2 = (TextView) findViewById(R.id.tbl_event1B2);
            tbl_event1B3 = (TextView) findViewById(R.id.tbl_event1B3);
            tbl_rate1R1 = (TextView) findViewById(R.id.tbl_rate1R1);
            tbl_rate1R2 = (TextView) findViewById(R.id.tbl_rate1R2);
            tbl_rate1R3 = (TextView) findViewById(R.id.tbl_rate1R3);
            tbl_rate1B1 = (TextView) findViewById(R.id.tbl_rate1B1);
            tbl_rate1B2 = (TextView) findViewById(R.id.tbl_rate1B2);
            tbl_rate1B3 = (TextView) findViewById(R.id.tbl_rate1B3);
            tbl_event2R1 = (TextView) findViewById(R.id.tbl_event2R1);
            tbl_event2R2 = (TextView) findViewById(R.id.tbl_event2R2);
            tbl_event2R3 = (TextView) findViewById(R.id.tbl_event2R3);
            tbl_event2B1 = (TextView) findViewById(R.id.tbl_event2B1);
            tbl_event2B2 = (TextView) findViewById(R.id.tbl_event2B2);
            tbl_event2B3 = (TextView) findViewById(R.id.tbl_event2B3);
            tbl_rate2R1 = (TextView) findViewById(R.id.tbl_rate2R1);
            tbl_rate2R2 = (TextView) findViewById(R.id.tbl_rate2R2);
            tbl_rate2R3 = (TextView) findViewById(R.id.tbl_rate2R3);
            tbl_rate2B1 = (TextView) findViewById(R.id.tbl_rate2B1);
            tbl_rate2B2 = (TextView) findViewById(R.id.tbl_rate2B2);
            tbl_rate2B3 = (TextView) findViewById(R.id.tbl_rate2B3);
            txt_MatchesR1 = (TextView) findViewById(R.id.txt_MatchesR1);
            txt_MatchesR2 = (TextView) findViewById(R.id.txt_MatchesR2);
            txt_MatchesR3 = (TextView) findViewById(R.id.txt_MatchesR3);
            txt_MatchesB1 = (TextView) findViewById(R.id.txt_MatchesB1);
            txt_MatchesB2 = (TextView) findViewById(R.id.txt_MatchesB2);
            txt_MatchesB3 = (TextView) findViewById(R.id.txt_MatchesB3);

            switch (ndx) {
            case 0:
                txt_MatchesR1.setText(String.valueOf(numMDs));
                tbl_event1R1.setText("Auto" + " \n" + "Tele");
                tbl_rate1R1.setText( "◯L" + A_lowNum + " U" + A_highNum + "\uD83D\uDEAB" + "L" + A_misslowNum + " U" + A_misshighNum + " L%" + A_LowPercent  + " U%"+ A_HiPercent + "\n" + "◯L" + T_lowNum + " U" + T_highNum  + "\uD83D\uDEAB" + "L"+ T_misslowNum + " U" + T_misshighNum + " L%" + T_LowPercent  + " U%"+ T_HiPercent);
                tbl_event2R1.setText("Hang" + "\n" + "Final");
                tbl_rate2R1.setText(" ╪" + climbed + "   ⁰" + Hang0 + " ¹" + Hang1  + " ²" +Hang2 + " ³" + Hang3 + " ⁴" + Hang4 +  "\n" +  "  ⚑" + pen + "⚡" + LostComs+ "  Def" + " b" + Def1  + " a" +Def2 + " g" + Def3 + " N" + Def4);
                break;
            case 1:
                txt_MatchesR2.setText(String.valueOf(numMDs));
                tbl_event1R2.setText("Auto" + " \n" + "Tele");
                tbl_rate1R2.setText( "◯L" + A_lowNum + " U" + A_highNum + "\uD83D\uDEAB" + "L" + A_misslowNum + " U" + A_misshighNum + " L%" + A_LowPercent  + " U%"+ A_HiPercent + "\n" + "◯L" + T_lowNum + " U" + T_highNum  + "\uD83D\uDEAB" + "L"+ T_misslowNum + " U" + T_misshighNum + " L%" + T_LowPercent  + " U%"+ T_HiPercent);
                tbl_event2R2.setText("Hang" + "\n" + "Final");
                tbl_rate2R2.setText( " ╪" + climbed + "   ⁰" + Hang0 + " ¹" + Hang1  + " ²" +Hang2 + " ³" + Hang3 + " ⁴" + Hang4 +  "\n" +  "  ⚑" + pen + "⚡" + LostComs+ "  Def" + " b" + Def1  + " a" +Def2 + " g" + Def3 + " N" + Def4);
                break;
            case 2:
                txt_MatchesR3.setText(String.valueOf(numMDs));
                tbl_event1R3.setText("Auto" + " \n" + "Tele");
                tbl_rate1R3.setText( "◯L" + A_lowNum + " U" + A_highNum + "\uD83D\uDEAB" + "L" + A_misslowNum + " U" + A_misshighNum + " L%" + A_LowPercent  + " U%"+ A_HiPercent + "\n" + "◯L" + T_lowNum + " U" + T_highNum  + "\uD83D\uDEAB" + "L"+ T_misslowNum + " U" + T_misshighNum + " L%" + T_LowPercent  + " U%"+ T_HiPercent);
                tbl_event2R3.setText("Hang" + "\n" + "Final");
                tbl_rate2R3.setText( " ╪" + climbed + "   ⁰" + Hang0 + " ¹" + Hang1  + " ²" +Hang2 + " ³" + Hang3 + " ⁴" + Hang4 +  "\n" +  "  ⚑" + pen + "⚡" + LostComs+ "  Def" + " b" + Def1  + " a" +Def2 + " g" + Def3 + " N" + Def4);
                break;
            case 3:
                txt_MatchesB1.setText(String.valueOf(numMDs));
                tbl_event1B1.setText("Auto" + " \n" + "Tele");
                tbl_rate1B1.setText( "◯L" + A_lowNum + " U" + A_highNum + "\uD83D\uDEAB" + "L" + A_misslowNum + " U" + A_misshighNum + " L%" + A_LowPercent  + " U%"+ A_HiPercent + "\n" + "◯L" + T_lowNum + " U" + T_highNum  + "\uD83D\uDEAB" + "L"+ T_misslowNum + " U" + T_misshighNum + " L%" + T_LowPercent  + " U%"+ T_HiPercent);
                tbl_event2B1.setText("Hang" + "\n" + "Final");
                tbl_rate2B1.setText( " ╪" + climbed + "   ⁰" + Hang0 + " ¹" + Hang1  + " ²" +Hang2 + " ³" + Hang3 + " ⁴" + Hang4 +  "\n" +  "  ⚑" + pen + "⚡" + LostComs+ "  Def" + " b" + Def1  + " a" +Def2 + " g" + Def3 + " N" + Def4);
                break;
            case 4:
                txt_MatchesB2.setText(String.valueOf(numMDs));
                tbl_event1B2.setText("Auto" + " \n" + "Tele");
                tbl_rate1B2.setText( "◯L" + A_lowNum + " U" + A_highNum + "\uD83D\uDEAB" + "L" + A_misslowNum + " U" + A_misshighNum + " L%" + A_LowPercent  + " U%"+ A_HiPercent + "\n" + "◯L" + T_lowNum + " U" + T_highNum  + "\uD83D\uDEAB" + "L"+ T_misslowNum + " U" + T_misshighNum + " L%" + T_LowPercent  + " U%"+ T_HiPercent);
                tbl_event2B2.setText("Hang" + "\n" + "Final");
                tbl_rate2B2.setText( " ╪" + climbed + "   ⁰" + Hang0 + " ¹" + Hang1  + " ²" +Hang2 + " ³" + Hang3 + " ⁴" + Hang4 +  "\n" +  "  ⚑" + pen + "⚡" + LostComs+ "  Def" + " b" + Def1  + " a" +Def2 + " g" + Def3 + " N" + Def4);
                break;
            case 5:
                txt_MatchesB3.setText(String.valueOf(numMDs));
                tbl_event1B3.setText("Auto" + " \n" + "Tele");
                tbl_rate1B3.setText( "◯L" + A_lowNum + " U" + A_highNum + "\uD83D\uDEAB" + "L" + A_misslowNum + " U" + A_misshighNum + " L%" + A_LowPercent  + " U%"+ A_HiPercent + "\n" + "◯L" + T_lowNum + " U" + T_highNum  + "\uD83D\uDEAB" + "L"+ T_misslowNum + " U" + T_misshighNum + " L%" + T_LowPercent  + " U%"+ T_HiPercent);
                tbl_event2B3.setText("Hang" + "\n" + "Final");
                tbl_rate2B3.setText(" ╪" + climbed + "   ⁰" + Hang0 + " ¹" + Hang1  + " ²" +Hang2 + " ³" + Hang3 + " ⁴" + Hang4 +  "\n" +  "  ⚑" + pen + "⚡" + LostComs+ "  Def" + " b" + Def1  + " a" +Def2 + " g" + Def3 + " N" + Def4);
                break;
            default:                // ????
                Log.e(TAG, "*** Error - bad NDX  ***");
            } // end Switch

        } //end If md>0
        } // End for #teams
     }


    private void findTeam(String tnum) {
        Log.w(TAG, "$$$$$  findTeam " + tnum);
        boolean found = false;
        for (int i = 0; i < Pearadox.numTeams; i++) {        // check each team entry
            if (Pearadox.team_List.get(i).getTeam_num().equals(tnum)) {
                team_inst = Pearadox.team_List.get(i);
                Scout_teams.add(team_inst);
//                Log.w(TAG, "===  Team " + team_inst.getTeam_num() + " " + team_inst.getTeam_name() + " " + team_inst.getTeam_loc());
                found = true;
                break;  // found it!
            }
        }  // end For
        Log.w("TAG", ">>>>>>>  findTeam: " + Scout_teams.size());
        if (!found) {
            Toast.makeText(getBaseContext(),"** Team '" + tnum + "' from Matches table _NOT_ found in Team list  ** ", Toast.LENGTH_LONG).show();
            p_Firebase.teamsObj team_dummy = new p_Firebase.teamsObj();
            Scout_teams.add(team_dummy);
        }
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void loadMatches() {
        i(TAG, "###  loadMatches  ###");

        addMatchSched_VE_Listener(pfMatch_DBReference.orderByChild("match"));

    }

        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void addMatchSched_VE_Listener(final Query pfMatch_DBReference) {
        pfMatch_DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                i(TAG, "******* Firebase retrieve Match Schedule  *******");
                txt_NextMatch = (TextView) findViewById(R.id.txt_NextMatch);
                matchList.clear();
                next_Match = "";
                p_Firebase.matchObj match_inst = new p_Firebase.matchObj();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();   /*get the data children*/
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    match_inst = iterator.next().getValue(p_Firebase.matchObj.class);
//                    Log.w(TAG,"      " + match_inst.getMatch());
//                    matchList.add(match_inst.getMatch() + "  Time: " + match_inst.getTime() + "  " + match_inst.getMtype());
                    matchList.add(match_inst.getMatch() +  "  " + match_inst.getMtype());
                    // Create the list of _OUR_ matches across the top
                    if (match_inst.getR1().matches("5414")) {
                        next_Match =  next_Match + match_inst.getMatch() + " ";
                    }
                    if (match_inst.getR2().matches("5414")) {
                        next_Match =  next_Match + match_inst.getMatch() + " ";
                    }
                    if (match_inst.getR3().matches("5414")) {
                        next_Match =  next_Match + match_inst.getMatch() + " ";
                    }
                    if (match_inst.getB1().matches("5414")) {
                        next_Match =  next_Match + match_inst.getMatch() + " ";
                    }
                    if (match_inst.getB2().matches("5414")) {
                        next_Match =  next_Match + match_inst.getMatch() + " ";
                    }
                    if (match_inst.getB3().matches("5414")) {
                        next_Match =  next_Match + match_inst.getMatch() + " ";
                    }
                }
                w(TAG,"### Matches ###  : " + matchList.size());
                if (next_Match.length() > 60) {             // 12 matches fit in 60 Char.
                    txt_NextMatch.setTextSize((float) 12.0);
                }
                txt_NextMatch.setText(next_Match);
                listView_Matches = (ListView) findViewById(R.id.listView_Matches);
                adaptMatch = new ArrayAdapter<String>(Visualizer_Activity.this, R.layout.match_list_layout, matchList);
                listView_Matches.setAdapter(adaptMatch);
                adaptMatch.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
                throw databaseError.toException();
            }
        });
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

//###################################################################
//###################################################################
//###################################################################
@Override
public void onStart() {
    super.onStart();
    v(TAG, "onStart");

    FirebaseUser user = mAuth.getCurrentUser();
    if (user != null) {
        // do your stuff
    } else {
        signInAnonymously();
    }
    loadMatches();  // Find all matches for this event
}
    @Override
    public void onResume() {
        super.onResume();
        v(TAG, "onResume");
    }
    @Override
    public void onStop() {
        super.onStop();
        v(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        v(TAG, "OnDestroy");
    }

}

