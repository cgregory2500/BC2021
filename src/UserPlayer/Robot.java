package UserPlayer;
import battlecode.common.*;
import java.util.HashSet;

public class Robot {
    RobotController rc;
    int turnCount = 0;

    public Robot(RobotController r){
        this.rc = r;
    }

    public void takeTurn() throws GameActionException {
        turnCount += 1;
    }
}
