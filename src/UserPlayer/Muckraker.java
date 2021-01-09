package UserPlayer;
import battlecode.common.*;

public class Muckraker extends Robot{
    Navigation nav = new Navigation();
    Communications comms = new Communications();
    public MapLocation enlightenmentCenterLoc;
    public MapLocation ec;

    public Muckraker(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if(turnCount <= 1)
            storeCenterLoc();

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
        if(comms.discoveredEC && enlightenmentCenterLoc != null){
            comms.reportBack(rc, nav, ec);
        }else if (comms.curClosestEC != 0 && !comms.closestECNeutral){
            nav.searchForEC(rc, comms);
        }else{
            nav.scout(rc);
        }
    }

    public void storeCenterLoc(){
        for(RobotInfo r : rc.senseNearbyRobots()){
            if (r.type == RobotType.ENLIGHTENMENT_CENTER && r.team == rc.getTeam()){
                enlightenmentCenterLoc = r.location;
                ec = r.location;
                break;
            }
        }
    }
}
