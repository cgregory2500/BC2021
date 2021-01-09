package UserPlayer;
import battlecode.common.*;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

public class Politician extends Robot{
    Navigation nav = new Navigation();
    Communications comms = new Communications();
    public MapLocation enlightenmentCenterLoc;
    private HashSet<MapLocation> defensePerimeter;
    private boolean defender;
    public MapLocation ec;

    public Politician(RobotController rc) {
        super(rc);
        if((int) (Math.random() * 10) < 4){
            this.defender = false;
        }else{
            this.defender = true;
        }
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if(turnCount <= 1)
            storeCenterLoc();

        if (enlightenmentCenterLoc != null)
            defensePerimeter = calculateDefensePerimeter(enlightenmentCenterLoc);

        comms.useComms(rc);

        if(comms.discoveredEC){
            comms.reportBack(rc, nav, enlightenmentCenterLoc);
            return;
        }

        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] captureable = rc.senseNearbyRobots(actionRadius, Team.NEUTRAL);

        if(captureable.length != 0 && rc.canEmpower(actionRadius)){
            rc.empower(actionRadius);
            return;
        }

        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            rc.empower(actionRadius);
            return;
        }

        if (comms.rushing){
            nav.searchForEC(rc, comms);
        }else if(defender){
            if(defensePerimeter.contains(rc.getLocation())){
            }else if(defensePerimeter != null){
                goToDefensePerimeter();
            }else{
                System.out.println("I moved!");
            }
        }else {
            if(comms.discoveredEC && enlightenmentCenterLoc != null){
                comms.reportBack(rc, nav, ec);
            } else if (comms.curClosestEC != 0){
                nav.searchForEC(rc, comms);
            }else{
                nav.scout(rc);
            }
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

    private HashSet<MapLocation> calculateDefensePerimeter(MapLocation center){
        HashSet<MapLocation> perimeter = new HashSet<MapLocation>();
        ArrayList<ArrayList<MapLocation>> wedges = new ArrayList<ArrayList<MapLocation>>();

        MapLocation topRight = center.add(Direction.NORTHEAST).add(Direction.NORTHEAST);
        MapLocation bottomRight = center.add(Direction.SOUTHEAST).add(Direction.SOUTHEAST);
        MapLocation topLeft = center.add(Direction.NORTHWEST).add(Direction.NORTHWEST);
        MapLocation bottomLeft = center.add(Direction.SOUTHWEST).add(Direction.SOUTHWEST);

        wedges.add(locsAroundWedge(Direction.SOUTHWEST, topRight));
        wedges.add(locsAroundWedge(Direction.NORTHWEST, bottomRight));
        wedges.add(locsAroundWedge(Direction.SOUTHEAST, topLeft));
        wedges.add(locsAroundWedge(Direction.NORTHEAST, bottomLeft));

        for(ArrayList<MapLocation> w: wedges){
            for(MapLocation l : w ){
                perimeter.add(l);
            }
        }

        return perimeter;
    }

    private ArrayList<MapLocation> locsAroundWedge(Direction missingDir, MapLocation center){
        ArrayList<MapLocation> toReturn = new ArrayList<MapLocation>();
        toReturn.add(center);
        
        for (Direction dir: Utils.directions){
            if(dir != missingDir){
                toReturn.add(center.add(dir));
            }
        }

        return toReturn;
    }

    private void goToDefensePerimeter() throws GameActionException{
        //Right now just goes to a random position in the defense perimeter
        MapLocation[] perimeterLocs = defensePerimeter.toArray(new MapLocation[defensePerimeter.size()]);
        
        MapLocation randomDest = perimeterLocs[(int) Math.random() * perimeterLocs.length];
        nav.simpleNav(rc, randomDest);
    }

}
