package UserPlayer;
import battlecode.common.*;
import java.util.HashMap;
import java.lang.Float;
import java.util.ArrayList;

public class Navigation {
    HashMap<int[], Float> locPassabilities = new HashMap<int[], Float>();
    Direction prevScoutDir;
    Direction lastLineScan;

    public Navigation(){
        this.lastLineScan = Direction.NORTH;
    }

    public void navigate(RobotController r) throws GameActionException {
        
    }

    public void explore(){

    }

    public void dijkstra(){
        
    }

    public void storeNewPassabilities(){

    }

    public int manhattanDistance(MapLocation origin, MapLocation dest){
        return Math.abs(origin.x - dest.x) + Math.abs(origin.y - dest.y);
    }

    public void simpleNav(RobotController r, MapLocation dest) throws GameActionException {
        MapLocation origin = r.getLocation();
        int minDist = 100000000;
        Direction bestDir = Direction.CENTER;
        for(Direction d: Utils.directions){
            if(manhattanDistance(origin.add(d), dest) < minDist){
                bestDir = d;
                minDist = manhattanDistance(origin.add(d), dest);
            }
        }

        tryMoveDirection(r, bestDir);
    }
    
    public void scout(RobotController r) throws GameActionException{
        if(prevScoutDir == null){
            prevScoutDir = Utils.randomDirection();
        }

        int epsilon = (int) (Math.random() * 10);

        if(epsilon < 1)
            prevScoutDir = Utils.randomDirection();

        if(Utils.tryMove(prevScoutDir, r)){
            System.out.println("Moved in Scouting Direction");
            return;
        }

        if(Utils.tryMove(Utils.randomDirection(), r))
            System.out.println("Moved in random Direction");
            
    }

    public void searchForEC(RobotController rc, Communications comms) throws GameActionException{
        MapLocation curLoc = rc.getLocation();

        if(curLoc.x < comms.curClosestEC){
            if(tryMoveDirection(rc, Direction.EAST)){
                return ;
            } else if (tryMoveDirection(rc, Direction.NORTHEAST)){
                return;
            } else{
                tryMoveDirection(rc, Direction.SOUTHEAST);
                return;
            }
        } else if (curLoc.x > comms.curClosestEC){
            if(tryMoveDirection(rc, Direction.WEST)){
                return ;
            } else if (tryMoveDirection(rc, Direction.NORTHWEST)){
                return;
            } else{
                tryMoveDirection(rc, Direction.SOUTHWEST);
                return;
            }
        } else {
            if(tryMoveDirection(rc, lastLineScan)){
                return;
            } else {
                lastLineScan = lastLineScan.opposite();
                tryMoveDirection(rc, lastLineScan);
                return;
            }
        }

    }

    public boolean tryMoveDirection(RobotController r, Direction dir) throws GameActionException{
        //tries to move in the shortest manhattan distance of the system
        if (Utils.tryMove(dir, r)){
            System.out.println("Moved in optimal direction according to Simple Nav!");
            return true;
        }


        //moves in random direction if cannot move in optimal direction
        if (Utils.tryMove(Utils.randomDirection(), r)){
            System.out.println("Moved in random Direction");
            return false;
        }
        return false;
    }

}
