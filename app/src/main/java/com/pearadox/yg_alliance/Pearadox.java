package com.pearadox.yg_alliance;

//******************************************************
// GLOBAL Variables

import java.util.ArrayList;

public class Pearadox {

    public static String FRC_Event;                                 // FIRST Event Code (e.g., txwa)
    public static String FRC_EventName;                             // FIRST Event Code (e.g., 'Hub City')
    public static String FRC_ChampDiv;                              // FIRST Championshio Division (e.g., 'Hub City')
    public static ArrayList<p_Firebase.eventObj> eventList = new ArrayList<p_Firebase.eventObj>();      // Event objects
    public static String[] comp_List = new String[8];               // Events list (array of just Names)
    public static int num_Events = 0; 						        // Actual # of Events/Competitions


// -----  Array of Match Data Objects
public static ArrayList<matchData> Matches_Data = new ArrayList<matchData>();

}
