package com.pearadox.yg_alliance;

import java.io.Serializable;

public class pitData implements Serializable {
    private static final long serialVersionUID = -54145414541400L;
    // ============= Pit Scout Data ================
    public String pit_team = " ";                   // Team #
    public int pit_weight = 0;                      // Height (inches)
    public int pit_totWheels = 0;                   // Total # of wheels
    public int pit_numTrac = 0;                     // Num. of Traction wheels
    public int pit_numOmni = 0;                     // Num. of Omni wheels
    public int pit_numMecanum = 0;                  // Num. of Mecanum wheels
    public int pit_numPneumatic = 0;                // Num. of Pneumatic wheels
    public boolean pit_vision = false;              // presence of Vision Camera
    public boolean pit_pneumatics = false;          // presence of Pneumatics
    public boolean pit_climber = false;             // presence of a Climbing mechanism
    public boolean pit_spin = false;                // Ability to Spin # turns on Control Panel
    public boolean pit_color = false;               // Ability to Stop Wheel on color
    public boolean pit_PowerCellFloor = false;      // presence of a way to pick up PowerCell from floor
    public boolean pit_PowerCellLoad = false;       // presence of a way to pick up PowerCell from Loading Sta.
    public boolean pit_undTrench = false;           // Ability to Drive under Control Panel (in Trench)
    public boolean pit_canLift = false;             // Ability to lift other robots
    public int pit_numLifted = 0;                   // Num. of robots can lift (1-2)
    public boolean pit_liftRamp = false;            // lift type Ramp
    public boolean pit_liftHook = false;            // lift type Hook
    public String pit_motor;                        // Type of Motor
    public String pit_lang;                         // Programming  Language
    public String pit_autoMode;                     // Autonomous Operatong Mode
    public boolean pit_dump = false;                // Can dump cells to partner
    //                                              // Grid
    public boolean pit_climberL1 = false;           //   L1--M1--R1
    public boolean pit_climberL2 = false;           //   |    |   |
    public boolean pit_climberL3 = false;           //   |    |   |
    public boolean pit_climberM1 = false;           //   L2--M2--R2
    public boolean pit_climberM2 = false;           //   |    |   |
    public boolean pit_climberM3 = false;           //   |    |   |
    public boolean pit_climberR1 = false;           //   L3--M3--R3
    public boolean pit_climberR2 = false;           //
    public boolean pit_climberR3 = false;           //
    public boolean pit_shootLow = false;            // Can Shoot into Bottom Port
    public boolean pit_shootUnder = false;          // Can Shoot into Port from Under
    public boolean pit_shootLine = false;           // Can Shoot into Port from Sector Line
    public boolean pit_shootFront= false;           // Can Shoot into IPort from Control Panel Front
    public boolean pit_shootBack= false;            // Can Shoot into IPort from Control Panel Back

    /* */
    public String pit_comment;                      // Comment(s)
    public String pit_scout = " ";                  // Student who collected the data
    public String  pit_dateTime;                    // Date & Time data was saved
    public String pit_photoURL;                     // URL of the robot photo in Firebase

    // ===========================================================================
    //  Constructor


    public pitData(String pit_team, int pit_weight, int pit_totWheels, int pit_numTrac, int pit_numOmni, int pit_numMecanum, int pit_numPneumatic, boolean pit_vision, boolean pit_pneumatics, boolean pit_climber, boolean pit_spin, boolean pit_color, boolean pit_PowerCellFloor, boolean pit_PowerCellLoad, boolean pit_undTrench, boolean pit_canLift, int pit_numLifted, boolean pit_liftRamp, boolean pit_liftHook, String pit_motor, String pit_lang, String pit_autoMode, boolean pit_climberL1, boolean pit_climberL2, boolean pit_climberL3, boolean pit_climberM1, boolean pit_climberM2, boolean pit_climberM3, boolean pit_climberR1, boolean pit_climberR2, boolean pit_climberR3, boolean pit_dump, boolean pit_shootLow, boolean pit_shootUnder, boolean pit_shootLine, boolean pit_shootFront, boolean pit_shootBack, String pit_comment, String pit_scout, String pit_dateTime, String pit_photoURL) {
        this.pit_team = pit_team;
        this.pit_weight = pit_weight;
        this.pit_totWheels = pit_totWheels;
        this.pit_numTrac = pit_numTrac;
        this.pit_numOmni = pit_numOmni;
        this.pit_numMecanum = pit_numMecanum;
        this.pit_numPneumatic = pit_numPneumatic;
        this.pit_vision = pit_vision;
        this.pit_pneumatics = pit_pneumatics;
        this.pit_climber = pit_climber;
        this.pit_spin = pit_spin;
        this.pit_color = pit_color;
        this.pit_PowerCellFloor = pit_PowerCellFloor;
        this.pit_PowerCellLoad = pit_PowerCellLoad;
        this.pit_undTrench = pit_undTrench;
        this.pit_canLift = pit_canLift;
        this.pit_numLifted = pit_numLifted;
        this.pit_liftRamp = pit_liftRamp;
        this.pit_liftHook = pit_liftHook;
        this.pit_motor = pit_motor;
        this.pit_lang = pit_lang;
        this.pit_autoMode = pit_autoMode;
        this.pit_climberL1 = pit_climberL1;
        this.pit_climberL2 = pit_climberL2;
        this.pit_climberL3 = pit_climberL3;
        this.pit_climberM1 = pit_climberM1;
        this.pit_climberM2 = pit_climberM2;
        this.pit_climberM3 = pit_climberM3;
        this.pit_climberR1 = pit_climberR1;
        this.pit_climberR2 = pit_climberR2;
        this.pit_climberR3 = pit_climberR3;
        this.pit_dump = pit_dump;
        this.pit_shootLow = pit_shootLow;
        this.pit_shootUnder = pit_shootUnder;
        this.pit_shootLine = pit_shootLine;
        this.pit_shootFront = pit_shootFront;
        this.pit_shootBack = pit_shootBack;
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

    public int getPit_weight() {
        return pit_weight;
    }

    public void setPit_weight(int pit_weight) {
        this.pit_weight = pit_weight;
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

    public boolean isPit_spin() {
        return pit_spin;
    }

    public void setPit_spin(boolean pit_spin) {
        this.pit_spin = pit_spin;
    }

    public boolean isPit_color() {
        return pit_color;
    }

    public void setPit_color(boolean pit_color) {
        this.pit_color = pit_color;
    }

    public boolean isPit_PowerCellFloor() {
        return pit_PowerCellFloor;
    }

    public void setPit_PowerCellFloor(boolean pit_PowerCellFloor) {
        this.pit_PowerCellFloor = pit_PowerCellFloor;
    }

    public boolean isPit_PowerCellLoad() {
        return pit_PowerCellLoad;
    }

    public void setPit_PowerCellLoad(boolean pit_PowerCellLoad) {
        this.pit_PowerCellLoad = pit_PowerCellLoad;
    }

    public boolean isPit_undTrench() {
        return pit_undTrench;
    }

    public void setPit_undTrench(boolean pit_undTrench) {
        this.pit_undTrench = pit_undTrench;
    }

    public boolean isPit_canLift() {
        return pit_canLift;
    }

    public void setPit_canLift(boolean pit_canLift) {
        this.pit_canLift = pit_canLift;
    }

    public int getPit_numLifted() {
        return pit_numLifted;
    }

    public void setPit_numLifted(int pit_numLifted) {
        this.pit_numLifted = pit_numLifted;
    }

    public boolean isPit_liftRamp() {
        return pit_liftRamp;
    }

    public void setPit_liftRamp(boolean pit_liftRamp) {
        this.pit_liftRamp = pit_liftRamp;
    }

    public boolean isPit_liftHook() {
        return pit_liftHook;
    }

    public void setPit_liftHook(boolean pit_liftHook) {
        this.pit_liftHook = pit_liftHook;
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

    public boolean isPit_climberL1() {
        return pit_climberL1;
    }

    public void setPit_climberL1(boolean pit_climberL1) {
        this.pit_climberL1 = pit_climberL1;
    }

    public boolean isPit_climberL2() {
        return pit_climberL2;
    }

    public void setPit_climberL2(boolean pit_climberL2) {
        this.pit_climberL2 = pit_climberL2;
    }

    public boolean isPit_climberL3() {
        return pit_climberL3;
    }

    public void setPit_climberL3(boolean pit_climberL3) {
        this.pit_climberL3 = pit_climberL3;
    }

    public boolean isPit_climberM1() {
        return pit_climberM1;
    }

    public void setPit_climberM1(boolean pit_climberM1) {
        this.pit_climberM1 = pit_climberM1;
    }

    public boolean isPit_climberM2() {
        return pit_climberM2;
    }

    public void setPit_climberM2(boolean pit_climberM2) {
        this.pit_climberM2 = pit_climberM2;
    }

    public boolean isPit_climberM3() {
        return pit_climberM3;
    }

    public void setPit_climberM3(boolean pit_climberM3) {
        this.pit_climberM3 = pit_climberM3;
    }

    public boolean isPit_climberR1() {
        return pit_climberR1;
    }

    public void setPit_climberR1(boolean pit_climberR1) {
        this.pit_climberR1 = pit_climberR1;
    }

    public boolean isPit_climberR2() {
        return pit_climberR2;
    }

    public void setPit_climberR2(boolean pit_climberR2) {
        this.pit_climberR2 = pit_climberR2;
    }

    public boolean isPit_climberR3() {
        return pit_climberR3;
    }

    public void setPit_climberR3(boolean pit_climberR3) {
        this.pit_climberR3 = pit_climberR3;
    }

    public boolean isPit_dump() {
        return pit_dump;
    }

    public void setPit_dump(boolean pit_dump) {
        this.pit_dump = pit_dump;
    }

    public boolean isPit_shootLow() {
        return pit_shootLow;
    }

    public void setPit_shootLow(boolean pit_shootLow) {
        this.pit_shootLow = pit_shootLow;
    }

    public boolean isPit_shootUnder() {
        return pit_shootUnder;
    }

    public void setPit_shootUnder(boolean pit_shootUnder) {
        this.pit_shootUnder = pit_shootUnder;
    }

    public boolean isPit_shootLine() {
        return pit_shootLine;
    }

    public void setPit_shootLine(boolean pit_shootLine) {
        this.pit_shootLine = pit_shootLine;
    }

    public boolean isPit_shootFront() {
        return pit_shootFront;
    }

    public void setPit_shootFront(boolean pit_shootFront) {
        this.pit_shootFront = pit_shootFront;
    }

    public boolean isPit_shootBack() {
        return pit_shootBack;
    }

    public void setPit_shootBack(boolean pit_shootBack) {
        this.pit_shootBack = pit_shootBack;
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
