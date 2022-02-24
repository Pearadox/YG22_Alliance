package com.pearadox.yg_alliance;

import java.io.Serializable;

public class pitData implements Serializable {
    private static final long serialVersionUID = -54145414541400L;
    // ============= Pit Scout Data ================
    public String pit_team = " ";                   // Team #
    public boolean pit_everyBot = false;            // EveryBot
    public int pit_weight = 0;                      // Weight (lbs)
    public int pit_height = 0;                      // Height (inches)
    public int pit_totWheels = 0;                   // Total # of wheels
    public int pit_numTrac = 0;                     // Num. of Traction wheels
    public int pit_numOmni = 0;                     // Num. of Omni wheels
    public int pit_numMecanum = 0;                  // Num. of Mecanum wheels
    public int pit_numPneumatic = 0;                // Num. of Pneumatic wheels
    public boolean pit_vision = false;              // presence of Vision Camera
    public boolean pit_pneumatics = false;          // presence of Pneumatics
    public boolean pit_climber = false;             // presence of a Climbing mechanism
    public boolean pit_cargoFloor = false;          // presence of a way to pick up Cargo from floor
    public boolean pit_cargoTerm = false;           // presence of a way to pick up Cargo from Terminal
    public String pit_hangarLevel;                  // Highest Hangar Climb
    public String pit_motor;                        // Type of Motor
    public String pit_lang;                         // Programming  Language
    public String pit_autoMode;                     // Autonomous Operating Mode
    public boolean pit_canshoot = false;            // Has Shooter for Upper Hub
    public boolean pit_shootLow = false;            // Can Shoot into Lower Hub
    public boolean pit_shootLP = false;             // Can Shoot from Launch Pad
    public boolean pit_shootTarmac = false;         // Can Shoot from Tarmac
    public boolean pit_shootRing= false;            // Can Shoot from Cargo Ring
    public boolean pit_shootAnywhere= false;        // Can Shoot from Anywhere
    /* */
    public String pit_comment;                      // Comment(s)
    public String pit_scout = " ";                  // Student who collected the data
    public String  pit_dateTime;                    // Date & Time data was saved
    public String pit_photoURL;                     // URL of the robot photo in Firebase

    // ===========================================================================
    //  Constructor


    public pitData(String pit_team, boolean pit_everyBot, int pit_weight, int pit_height, int pit_totWheels, int pit_numTrac, int pit_numOmni, int pit_numMecanum, int pit_numPneumatic, boolean pit_vision, boolean pit_pneumatics, boolean pit_climber, boolean pit_cargoFloor, boolean pit_cargoTerm, String pit_hangarLevel, String pit_motor, String pit_lang, String pit_autoMode, boolean pit_canshoot, boolean pit_shootLow, boolean pit_shootLP, boolean pit_shootTarmac, boolean pit_shootRing, boolean pit_shootAnywhere, String pit_comment, String pit_scout, String pit_dateTime, String pit_photoURL) {
        this.pit_team = pit_team;
        this.pit_everyBot = pit_everyBot;
        this.pit_weight = pit_weight;
        this.pit_height = pit_height;
        this.pit_totWheels = pit_totWheels;
        this.pit_numTrac = pit_numTrac;
        this.pit_numOmni = pit_numOmni;
        this.pit_numMecanum = pit_numMecanum;
        this.pit_numPneumatic = pit_numPneumatic;
        this.pit_vision = pit_vision;
        this.pit_pneumatics = pit_pneumatics;
        this.pit_climber = pit_climber;
        this.pit_cargoFloor = pit_cargoFloor;
        this.pit_cargoTerm = pit_cargoTerm;
        this.pit_hangarLevel = pit_hangarLevel;
        this.pit_motor = pit_motor;
        this.pit_lang = pit_lang;
        this.pit_autoMode = pit_autoMode;
        this.pit_canshoot = pit_canshoot;
        this.pit_shootLow = pit_shootLow;
        this.pit_shootLP = pit_shootLP;
        this.pit_shootTarmac = pit_shootTarmac;
        this.pit_shootRing = pit_shootRing;
        this.pit_shootAnywhere = pit_shootAnywhere;
        this.pit_comment = pit_comment;
        this.pit_scout = pit_scout;
        this.pit_dateTime = pit_dateTime;
        this.pit_photoURL = pit_photoURL;
    }

    // ===========================================================================
// Default constructor required for calls to
// DataSnapshot.getValue(teams.class)
public pitData() {
    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// Getters & Setters

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPit_team() {
        return pit_team;
    }

    public void setPit_team(String pit_team) {
        this.pit_team = pit_team;
    }

    public boolean isPit_everyBot() {
        return pit_everyBot;
    }

    public void setPit_everyBot(boolean pit_everyBot) {
        this.pit_everyBot = pit_everyBot;
    }

    public int getPit_weight() {
        return pit_weight;
    }

    public void setPit_weight(int pit_weight) {
        this.pit_weight = pit_weight;
    }

    public int getPit_height() {
        return pit_height;
    }

    public void setPit_height(int pit_height) {
        this.pit_height = pit_height;
    }

    public int getPit_totWheels() {
        return pit_totWheels;
    }

    public void setPit_totWheels(int pit_totWheels) {
        this.pit_totWheels = pit_totWheels;
    }

    public int getPit_numTrac() {
        return pit_numTrac;
    }

    public void setPit_numTrac(int pit_numTrac) {
        this.pit_numTrac = pit_numTrac;
    }

    public int getPit_numOmni() {
        return pit_numOmni;
    }

    public void setPit_numOmni(int pit_numOmni) {
        this.pit_numOmni = pit_numOmni;
    }

    public int getPit_numMecanum() {
        return pit_numMecanum;
    }

    public void setPit_numMecanum(int pit_numMecanum) {
        this.pit_numMecanum = pit_numMecanum;
    }

    public int getPit_numPneumatic() {
        return pit_numPneumatic;
    }

    public void setPit_numPneumatic(int pit_numPneumatic) {
        this.pit_numPneumatic = pit_numPneumatic;
    }

    public boolean isPit_vision() {
        return pit_vision;
    }

    public void setPit_vision(boolean pit_vision) {
        this.pit_vision = pit_vision;
    }

    public boolean isPit_pneumatics() {
        return pit_pneumatics;
    }

    public void setPit_pneumatics(boolean pit_pneumatics) {
        this.pit_pneumatics = pit_pneumatics;
    }

    public boolean isPit_climber() {
        return pit_climber;
    }

    public void setPit_climber(boolean pit_climber) {
        this.pit_climber = pit_climber;
    }

    public boolean isPit_cargoFloor() {
        return pit_cargoFloor;
    }

    public void setPit_cargoFloor(boolean pit_cargoFloor) {
        this.pit_cargoFloor = pit_cargoFloor;
    }

    public boolean isPit_cargoTerm() {
        return pit_cargoTerm;
    }

    public void setPit_cargoTerm(boolean pit_cargoTerm) {
        this.pit_cargoTerm = pit_cargoTerm;
    }

    public String getPit_hangarLevel() {
        return pit_hangarLevel;
    }

    public void setPit_hangarLevel(String pit_hangarLevel) {
        this.pit_hangarLevel = pit_hangarLevel;
    }

    public String getPit_motor() {
        return pit_motor;
    }

    public void setPit_motor(String pit_motor) {
        this.pit_motor = pit_motor;
    }

    public String getPit_lang() {
        return pit_lang;
    }

    public void setPit_lang(String pit_lang) {
        this.pit_lang = pit_lang;
    }

    public String getPit_autoMode() {
        return pit_autoMode;
    }

    public void setPit_autoMode(String pit_autoMode) {
        this.pit_autoMode = pit_autoMode;
    }

    public boolean isPit_canshoot() {
        return pit_canshoot;
    }

    public void setPit_canshoot(boolean pit_canshoot) {
        this.pit_canshoot = pit_canshoot;
    }

    public boolean isPit_shootLow() {
        return pit_shootLow;
    }

    public void setPit_shootLow(boolean pit_shootLow) {
        this.pit_shootLow = pit_shootLow;
    }

    public boolean isPit_shootLP() {
        return pit_shootLP;
    }

    public void setPit_shootLP(boolean pit_shootLP) {
        this.pit_shootLP = pit_shootLP;
    }

    public boolean isPit_shootTarmac() {
        return pit_shootTarmac;
    }

    public void setPit_shootTarmac(boolean pit_shootTarmac) {
        this.pit_shootTarmac = pit_shootTarmac;
    }

    public boolean isPit_shootRing() {
        return pit_shootRing;
    }

    public void setPit_shootRing(boolean pit_shootRing) {
        this.pit_shootRing = pit_shootRing;
    }

    public boolean isPit_shootAnywhere() {
        return pit_shootAnywhere;
    }

    public void setPit_shootAnywhere(boolean pit_shootAnywhere) {
        this.pit_shootAnywhere = pit_shootAnywhere;
    }

    public String getPit_comment() {
        return pit_comment;
    }

    public void setPit_comment(String pit_comment) {
        this.pit_comment = pit_comment;
    }

    public String getPit_scout() {
        return pit_scout;
    }

    public void setPit_scout(String pit_scout) {
        this.pit_scout = pit_scout;
    }

    public String getPit_dateTime() {
        return pit_dateTime;
    }

    public void setPit_dateTime(String pit_dateTime) {
        this.pit_dateTime = pit_dateTime;
    }

    public String getPit_photoURL() {
        return pit_photoURL;
    }

    public void setPit_photoURL(String pit_photoURL) {
        this.pit_photoURL = pit_photoURL;
    }
// End of Getters & Setters
}
