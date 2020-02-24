package com.pearadox.yg_alliance;

import java.io.Serializable;

public class matchData implements Serializable {
    private static final long serialVersionUID = -54145414541400L;
    // ============= AUTO ================
    private String match;                   // Match ID (e.g., Qualifying) and '00' - match #)
    private String team_num;                // Team Number (e.g., '5414')
                                            // *** Pre-Game **
    private int     pre_cells_carried;      // Carry how many PowerCell(s)
    private String  pre_startPos;           // Start Position
    private int     pre_PlayerSta;          // Player Station (1-3)

                                            // ---- AFTER Start ----
    private boolean auto_mode;              // Do they have Autonomous mode?
    private boolean auto_leftSectorLine;    // Did they leave Sector Line
    private boolean auto_Dump;              // Did they Dump balls to partner?
    private boolean auto_CollectFloor;      // Collect from Floor?
    private boolean auto_CollectCP;         // Collect from Control Panel?
    private boolean auto_CollectTrench;     // Collect from Trench?
    private boolean auto_CollectSGboundary; // Collect from SG boundary?
    private boolean auto_CollectRobot;      // Collect from a Robot?
    private int     auto_Low;               // # Low Goal balls
    private int     auto_HighClose;         // # High Goal balls - Close
    private int     auto_HighLine;          // # High Goal balls - Line
    private int     auto_HighFrontCP;       // # High Goal balls - Front CP
    private boolean auto_conInnerClose;     // Consistent Inner Goal scored Close?
    private boolean auto_conInnerLine;      // Consistent Inner Goal scored on Line?
    private boolean auto_conInnerFrontCP;   // Consistent Inner Goal scored in Front of CP?

    private String  auto_comment;           // Auto comment

    // ============== TELE =================
    private boolean tele_PowerCell_floor;   // Did they pick up PowerCell from floor
    private boolean tele_PowerCell_LoadSta; // Did they get PowerCell from Loading Station
    private boolean tele_PowerCell_CP;      // Did they pick up PowerCell from Control Panel
    private boolean tele_PowerCell_Trench;  // Did they get PowerCell from Loading Station
    private boolean tele_PowerCell_Boundary; // Did they get PowerCell from SG boundary?
    private boolean tele_PowerCell_Robot;   // Get from a Robot?
    private int     tele_Low;               // # Low Goal balls
    private int     tele_HighClose;         // # High Goal balls - Close
    private int     tele_HighLine;          // # High Goal balls - Line
    private int     tele_HighFrontCP;       // # High Goal balls - Front CP
    private int     tele_HighBackCP;        // # High Goal balls - Back CP
    private boolean tele_conInnerClose;     // Consistent Inner Goal scored Close?
    private boolean tele_conInnerLine;      // Consistent Inner Goal scored Con Line?
    private boolean tele_conInnerFrontCP;   // Consistent Inner Goal scored in Front of CP?
    private boolean tele_conInnerBackCP;    // Consistent Inner Goal scored in Back of CP?
    private boolean tele_CPspin;            // Control Panel Spin
    private boolean tele_CPcolor;           // Control Panel Color

    private boolean tele_Climbed;           // Did they Climb?
    private boolean tele_UnderSG;           // Parked under Shield Generator
    private boolean tele_got_lift;          // Did they get lifted
    private boolean tele_lifted;            // Did they lift a robot
    private int     tele_liftedNum;         // How many lifted?
    private int     tele_Hang_num;          // End - How many on Bar (0-3)
    private boolean tele_Balanced;          // SG is Balanced

    private int     tele_num_Penalties;     // How many penalties received?
    private String  tele_comment;           // Tele comment

    // ============= Final  ================
    private boolean final_lostParts;         // Did they lose parts
    private boolean final_lostComms;         // Did they lose communication
    private boolean final_defense_good;      // Was their overall Defense Good (bad=false)
    private boolean final_def_Block;         // Did they use Blocking Defense on SG
    private boolean final_def_TrenchInt;     // Did they block the Trench
    /*=============================================================================*/
    private String  final_comment;           // Final comment
    private String  final_studID;            // Student doing the scouting
    private String  final_dateTime;          // Date & Time data was saved

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//  Constructor


    public matchData(String match, String team_num, int pre_cells_carried, String pre_startPos, int pre_PlayerSta, boolean auto_mode, boolean auto_leftSectorLine, boolean auto_Dump, boolean auto_CollectFloor, boolean auto_CollectCP, boolean auto_CollectTrench, boolean auto_CollectSGboundary, boolean auto_CollectRobot, int auto_Low, int auto_HighClose, int auto_HighLine, int auto_HighFrontCP, boolean auto_conInnerClose, boolean auto_conInnerLine, boolean auto_conInnerFrontCP, String auto_comment, boolean tele_PowerCell_floor, boolean tele_PowerCell_LoadSta, boolean tele_PowerCell_CP, boolean tele_PowerCell_Trench, boolean tele_PowerCell_Boundary, boolean tele_PowerCell_Robot, int tele_Low, int tele_HighClose, int tele_HighLine, int tele_HighFrontCP, int tele_HighBackCP, boolean tele_conInnerClose, boolean tele_conInnerLine, boolean tele_conInnerFrontCP, boolean tele_conInnerBackCP, boolean tele_CPspin, boolean tele_CPcolor, boolean tele_Climbed, boolean tele_UnderSG, boolean tele_got_lift, boolean tele_lifted, int tele_liftedNum, int tele_Hang_num, boolean tele_Balanced, int tele_num_Penalties, String tele_comment, boolean final_lostParts, boolean final_lostComms, boolean final_defense_good, boolean final_def_Block, boolean final_def_TrenchInt, String final_comment, String final_studID, String final_dateTime) {
        this.match = match;
        this.team_num = team_num;
        this.pre_cells_carried = pre_cells_carried;
        this.pre_startPos = pre_startPos;
        this.pre_PlayerSta = pre_PlayerSta;
        this.auto_mode = auto_mode;
        this.auto_leftSectorLine = auto_leftSectorLine;
        this.auto_Dump = auto_Dump;
        this.auto_CollectFloor = auto_CollectFloor;
        this.auto_CollectCP = auto_CollectCP;
        this.auto_CollectTrench = auto_CollectTrench;
        this.auto_CollectSGboundary = auto_CollectSGboundary;
        this.auto_CollectRobot = auto_CollectRobot;
        this.auto_Low = auto_Low;
        this.auto_HighClose = auto_HighClose;
        this.auto_HighLine = auto_HighLine;
        this.auto_HighFrontCP = auto_HighFrontCP;
        this.auto_conInnerClose = auto_conInnerClose;
        this.auto_conInnerLine = auto_conInnerLine;
        this.auto_conInnerFrontCP = auto_conInnerFrontCP;
        this.auto_comment = auto_comment;
        this.tele_PowerCell_floor = tele_PowerCell_floor;
        this.tele_PowerCell_LoadSta = tele_PowerCell_LoadSta;
        this.tele_PowerCell_CP = tele_PowerCell_CP;
        this.tele_PowerCell_Trench = tele_PowerCell_Trench;
        this.tele_PowerCell_Boundary = tele_PowerCell_Boundary;
        this.tele_PowerCell_Robot = tele_PowerCell_Robot;
        this.tele_Low = tele_Low;
        this.tele_HighClose = tele_HighClose;
        this.tele_HighLine = tele_HighLine;
        this.tele_HighFrontCP = tele_HighFrontCP;
        this.tele_HighBackCP = tele_HighBackCP;
        this.tele_conInnerClose = tele_conInnerClose;
        this.tele_conInnerLine = tele_conInnerLine;
        this.tele_conInnerFrontCP = tele_conInnerFrontCP;
        this.tele_conInnerBackCP = tele_conInnerBackCP;
        this.tele_CPspin = tele_CPspin;
        this.tele_CPcolor = tele_CPcolor;
        this.tele_Climbed = tele_Climbed;
        this.tele_UnderSG = tele_UnderSG;
        this.tele_got_lift = tele_got_lift;
        this.tele_lifted = tele_lifted;
        this.tele_liftedNum = tele_liftedNum;
        this.tele_Hang_num = tele_Hang_num;
        this.tele_Balanced = tele_Balanced;
        this.tele_num_Penalties = tele_num_Penalties;
        this.tele_comment = tele_comment;
        this.final_lostParts = final_lostParts;
        this.final_lostComms = final_lostComms;
        this.final_defense_good = final_defense_good;
        this.final_def_Block = final_def_Block;
        this.final_def_TrenchInt = final_def_TrenchInt;
        this.final_comment = final_comment;
        this.final_studID = final_studID;
        this.final_dateTime = final_dateTime;
    }

    //
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// Default constructor required for calls to
// DataSnapshot.getValue(matchData.class)
public matchData() {

}

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// Getters & Setters

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getTeam_num() {
        return team_num;
    }

    public void setTeam_num(String team_num) {
        this.team_num = team_num;
    }

    public int getPre_cells_carried() {
        return pre_cells_carried;
    }

    public void setPre_cells_carried(int pre_cells_carried) {
        this.pre_cells_carried = pre_cells_carried;
    }

    public String getPre_startPos() {
        return pre_startPos;
    }

    public void setPre_startPos(String pre_startPos) {
        this.pre_startPos = pre_startPos;
    }

    public int getPre_PlayerSta() {
        return pre_PlayerSta;
    }

    public void setPre_PlayerSta(int pre_PlayerSta) {
        this.pre_PlayerSta = pre_PlayerSta;
    }

    public boolean isAuto_mode() {
        return auto_mode;
    }

    public void setAuto_mode(boolean auto_mode) {
        this.auto_mode = auto_mode;
    }

    public boolean isAuto_leftSectorLine() {
        return auto_leftSectorLine;
    }

    public void setAuto_leftSectorLine(boolean auto_leftSectorLine) {
        this.auto_leftSectorLine = auto_leftSectorLine;
    }

    public boolean isAuto_Dump() {
        return auto_Dump;
    }

    public void setAuto_Dump(boolean auto_Dump) {
        this.auto_Dump = auto_Dump;
    }

    public boolean isAuto_CollectFloor() {
        return auto_CollectFloor;
    }

    public void setAuto_CollectFloor(boolean auto_CollectFloor) {
        this.auto_CollectFloor = auto_CollectFloor;
    }

    public boolean isAuto_CollectCP() {
        return auto_CollectCP;
    }

    public void setAuto_CollectCP(boolean auto_CollectCP) {
        this.auto_CollectCP = auto_CollectCP;
    }

    public boolean isAuto_CollectTrench() {
        return auto_CollectTrench;
    }

    public void setAuto_CollectTrench(boolean auto_CollectTrench) {
        this.auto_CollectTrench = auto_CollectTrench;
    }

    public boolean isAuto_CollectSGboundary() {
        return auto_CollectSGboundary;
    }

    public void setAuto_CollectSGboundary(boolean auto_CollectSGboundary) {
        this.auto_CollectSGboundary = auto_CollectSGboundary;
    }

    public boolean isAuto_CollectRobot() {
        return auto_CollectRobot;
    }

    public void setAuto_CollectRobot(boolean auto_CollectRobot) {
        this.auto_CollectRobot = auto_CollectRobot;
    }

    public int getAuto_Low() {
        return auto_Low;
    }

    public void setAuto_Low(int auto_Low) {
        this.auto_Low = auto_Low;
    }

    public int getAuto_HighClose() {
        return auto_HighClose;
    }

    public void setAuto_HighClose(int auto_HighClose) {
        this.auto_HighClose = auto_HighClose;
    }

    public int getAuto_HighLine() {
        return auto_HighLine;
    }

    public void setAuto_HighLine(int auto_HighLine) {
        this.auto_HighLine = auto_HighLine;
    }

    public int getAuto_HighFrontCP() {
        return auto_HighFrontCP;
    }

    public void setAuto_HighFrontCP(int auto_HighFrontCP) {
        this.auto_HighFrontCP = auto_HighFrontCP;
    }

    public boolean isAuto_conInnerClose() {
        return auto_conInnerClose;
    }

    public void setAuto_conInnerClose(boolean auto_conInnerClose) {
        this.auto_conInnerClose = auto_conInnerClose;
    }

    public boolean isAuto_conInnerLine() {
        return auto_conInnerLine;
    }

    public void setAuto_conInnerLine(boolean auto_conInnerLine) {
        this.auto_conInnerLine = auto_conInnerLine;
    }

    public boolean isAuto_conInnerFrontCP() {
        return auto_conInnerFrontCP;
    }

    public void setAuto_conInnerFrontCP(boolean auto_conInnerFrontCP) {
        this.auto_conInnerFrontCP = auto_conInnerFrontCP;
    }

    public String getAuto_comment() {
        return auto_comment;
    }

    public void setAuto_comment(String auto_comment) {
        this.auto_comment = auto_comment;
    }

    public boolean isTele_PowerCell_floor() {
        return tele_PowerCell_floor;
    }

    public void setTele_PowerCell_floor(boolean tele_PowerCell_floor) {
        this.tele_PowerCell_floor = tele_PowerCell_floor;
    }

    public boolean isTele_PowerCell_LoadSta() {
        return tele_PowerCell_LoadSta;
    }

    public void setTele_PowerCell_LoadSta(boolean tele_PowerCell_LoadSta) {
        this.tele_PowerCell_LoadSta = tele_PowerCell_LoadSta;
    }

    public boolean isTele_PowerCell_CP() {
        return tele_PowerCell_CP;
    }

    public void setTele_PowerCell_CP(boolean tele_PowerCell_CP) {
        this.tele_PowerCell_CP = tele_PowerCell_CP;
    }

    public boolean isTele_PowerCell_Trench() {
        return tele_PowerCell_Trench;
    }

    public void setTele_PowerCell_Trench(boolean tele_PowerCell_Trench) {
        this.tele_PowerCell_Trench = tele_PowerCell_Trench;
    }

    public boolean isTele_PowerCell_Boundary() {
        return tele_PowerCell_Boundary;
    }

    public void setTele_PowerCell_Boundary(boolean tele_PowerCell_Boundary) {
        this.tele_PowerCell_Boundary = tele_PowerCell_Boundary;
    }

    public boolean isTele_PowerCell_Robot() {
        return tele_PowerCell_Robot;
    }

    public void setTele_PowerCell_Robot(boolean tele_PowerCell_Robot) {
        this.tele_PowerCell_Robot = tele_PowerCell_Robot;
    }

    public int getTele_Low() {
        return tele_Low;
    }

    public void setTele_Low(int tele_Low) {
        this.tele_Low = tele_Low;
    }

    public int getTele_HighClose() {
        return tele_HighClose;
    }

    public void setTele_HighClose(int tele_HighClose) {
        this.tele_HighClose = tele_HighClose;
    }

    public int getTele_HighLine() {
        return tele_HighLine;
    }

    public void setTele_HighLine(int tele_HighLine) {
        this.tele_HighLine = tele_HighLine;
    }

    public int getTele_HighFrontCP() {
        return tele_HighFrontCP;
    }

    public void setTele_HighFrontCP(int tele_HighFrontCP) {
        this.tele_HighFrontCP = tele_HighFrontCP;
    }

    public int getTele_HighBackCP() {
        return tele_HighBackCP;
    }

    public void setTele_HighBackCP(int tele_HighBackCP) {
        this.tele_HighBackCP = tele_HighBackCP;
    }

    public boolean isTele_conInnerClose() {
        return tele_conInnerClose;
    }

    public void setTele_conInnerClose(boolean tele_conInnerClose) {
        this.tele_conInnerClose = tele_conInnerClose;
    }

    public boolean isTele_conInnerLine() {
        return tele_conInnerLine;
    }

    public void setTele_conInnerLine(boolean tele_conInnerLine) {
        this.tele_conInnerLine = tele_conInnerLine;
    }

    public boolean isTele_conInnerFrontCP() {
        return tele_conInnerFrontCP;
    }

    public void setTele_conInnerFrontCP(boolean tele_conInnerFrontCP) {
        this.tele_conInnerFrontCP = tele_conInnerFrontCP;
    }

    public boolean isTele_conInnerBackCP() {
        return tele_conInnerBackCP;
    }

    public void setTele_conInnerBackCP(boolean tele_conInnerBackCP) {
        this.tele_conInnerBackCP = tele_conInnerBackCP;
    }

    public boolean isTele_CPspin() {
        return tele_CPspin;
    }

    public void setTele_CPspin(boolean tele_CPspin) {
        this.tele_CPspin = tele_CPspin;
    }

    public boolean isTele_CPcolor() {
        return tele_CPcolor;
    }

    public void setTele_CPcolor(boolean tele_CPcolor) {
        this.tele_CPcolor = tele_CPcolor;
    }

    public boolean isTele_Climbed() {
        return tele_Climbed;
    }

    public void setTele_Climbed(boolean tele_Climbed) {
        this.tele_Climbed = tele_Climbed;
    }

    public boolean isTele_UnderSG() {
        return tele_UnderSG;
    }

    public void setTele_UnderSG(boolean tele_UnderSG) {
        this.tele_UnderSG = tele_UnderSG;
    }

    public boolean isTele_got_lift() {
        return tele_got_lift;
    }

    public void setTele_got_lift(boolean tele_got_lift) {
        this.tele_got_lift = tele_got_lift;
    }

    public boolean isTele_lifted() {
        return tele_lifted;
    }

    public void setTele_lifted(boolean tele_lifted) {
        this.tele_lifted = tele_lifted;
    }

    public int getTele_liftedNum() {
        return tele_liftedNum;
    }

    public void setTele_liftedNum(int tele_liftedNum) {
        this.tele_liftedNum = tele_liftedNum;
    }

    public int getTele_Hang_num() {
        return tele_Hang_num;
    }

    public void setTele_Hang_num(int tele_Hang_num) {
        this.tele_Hang_num = tele_Hang_num;
    }

    public boolean isTele_Balanced() {
        return tele_Balanced;
    }

    public void setTele_Balanced(boolean tele_Balanced) {
        this.tele_Balanced = tele_Balanced;
    }

    public int getTele_num_Penalties() {
        return tele_num_Penalties;
    }

    public void setTele_num_Penalties(int tele_num_Penalties) {
        this.tele_num_Penalties = tele_num_Penalties;
    }

    public String getTele_comment() {
        return tele_comment;
    }

    public void setTele_comment(String tele_comment) {
        this.tele_comment = tele_comment;
    }

    public boolean isFinal_lostParts() {
        return final_lostParts;
    }

    public void setFinal_lostParts(boolean final_lostParts) {
        this.final_lostParts = final_lostParts;
    }

    public boolean isFinal_lostComms() {
        return final_lostComms;
    }

    public void setFinal_lostComms(boolean final_lostComms) {
        this.final_lostComms = final_lostComms;
    }

    public boolean isFinal_defense_good() {
        return final_defense_good;
    }

    public void setFinal_defense_good(boolean final_defense_good) {
        this.final_defense_good = final_defense_good;
    }

    public boolean isFinal_def_Block() {
        return final_def_Block;
    }

    public void setFinal_def_Block(boolean final_def_Block) {
        this.final_def_Block = final_def_Block;
    }

    public boolean isFinal_def_TrenchInt() {
        return final_def_TrenchInt;
    }

    public void setFinal_def_TrenchInt(boolean final_def_TrenchInt) {
        this.final_def_TrenchInt = final_def_TrenchInt;
    }

    public String getFinal_comment() {
        return final_comment;
    }

    public void setFinal_comment(String final_comment) {
        this.final_comment = final_comment;
    }

    public String getFinal_studID() {
        return final_studID;
    }

    public void setFinal_studID(String final_studID) {
        this.final_studID = final_studID;
    }

    public String getFinal_dateTime() {
        return final_dateTime;
    }

    public void setFinal_dateTime(String final_dateTime) {
        this.final_dateTime = final_dateTime;
    }


//   GLF 1/27/20
// End of Getters/Setters

}