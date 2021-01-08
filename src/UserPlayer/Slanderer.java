package UserPlayer;
import battlecode.common.*;
import java.util.HashSet;
import java.util.ArrayList;

public class Slanderer extends Robot{
    Navigation nav = new Navigation();
    Communications comms = new Communications();
    private MapLocation enlightenmentCenterLoc;
    private HashSet<MapLocation> defensePerimeter;

    public Slanderer(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        if(turnCount <= 1)
            storeCenterLoc();

        if (enlightenmentCenterLoc != null)
            defensePerimeter = calculateDefensePerimeter(enlightenmentCenterLoc);
        
        if(turnCount >= 300){
            Team enemy = rc.getTeam().opponent();
            int actionRadius = rc.getType().actionRadiusSquared;
            RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
            if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
                System.out.println("empowering...");
                rc.empower(actionRadius);
                System.out.println("empowered");
                return;
            }
            if(defensePerimeter.contains(rc.getLocation())){
                return;
            }
            if(defensePerimeter != null)
                goToDefensePerimeter();
            else
                System.out.println("Moved in random Direction");
        }else{
            nav.scout(rc);
        }
    }

    private void storeCenterLoc(){
        for(RobotInfo r : rc.senseNearbyRobots()){
            if (r.type == RobotType.ENLIGHTENMENT_CENTER && r.team == rc.getTeam()){
                enlightenmentCenterLoc = r.location;
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
        MapLocation[] perimeterLocs = defensePerimeter.toArray( new MapLocation[defensePerimeter.size()]);
        
        MapLocation randomDest = perimeterLocs[(int) Math.random() * perimeterLocs.length];
        nav.simpleNav(rc, randomDest);
    }
}
