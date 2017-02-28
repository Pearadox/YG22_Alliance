package com.pearadox.yg_alliance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";        // This CLASS name
    Spinner spinner_Device, spinner_Event;
    ArrayAdapter<String> adapter_Event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Log.i(TAG, "******* Starting Yellow-Green Alliance  *******");

        Spinner spinner_Event = (Spinner) findViewById(R.id.spinner_Event);
        String[] events = getResources().getStringArray(R.array.event_array);
        adapter_Event = new ArrayAdapter<String>(this, R.layout.list_layout, events);
        adapter_Event.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Event.setAdapter(adapter_Event);
        spinner_Event.setSelection(0, false);
        spinner_Event.setOnItemSelectedListener(new event_OnItemSelectedListener());


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
        Log.v(TAG, "OnDestroy key-> ");
    }

}
