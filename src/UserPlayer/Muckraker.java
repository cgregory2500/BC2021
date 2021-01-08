package UserPlayer;
import battlecode.common.*;

public class Muckraker extends Robot{
    Navigation nav = new Navigation();
    Communications comms = new Communications();

    public Muckraker(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
            if (robot.type.canBeExposed()) {
                // It's a slanderer... go get them!
                if (rc.canExpose(robot.location)) {
                    rc.expose(robot.location);
                    return;
                }
            }
        }
        comms.useComms(rc);
        if (comms.curClosestEC != 0){
            nav.searchForEC(rc, comms);
        }else{
            nav.scout(rc);
        }
    }
}
