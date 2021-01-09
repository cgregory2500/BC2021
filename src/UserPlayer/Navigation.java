package UserPlayer;
import battlecode.common.*;
import java.util.HashMap;

import javax.management.ServiceNotFoundException;

import java.lang.Float;
import java.lang.Double;
import java.util.ArrayList;

public class Navigation {
    HashMap<MapLocation, Double> savedLocPassabilities = new HashMap<MapLocation, Double>();
    Direction prevScoutDir;
    Direction lastLineScan;


    public Navigation(){
        this.lastLineScan = Direction.NORTH;
    }

    public HashMap<MapLocation[], Double> buildEdgeMap(RobotController rc) throws GameActionException{
        //hash map the maps an edge to the edge weight that is defined as (1/passability of the origin)
        HashMap<MapLocation[], Double> edgeMap = new HashMap<MapLocation[], Double>();

        MapLocation curLoc = rc.getLocation();
        int actionRadius = rc.getType().actionRadiusSquared;
        MapLocation newNode;
        Double edgeWeight;
        MapLocation dest;


        for(int dx = -5; dx < 5; dx++){
            for(int dy = -5; dy < 5; dy++){
                newNode = curLoc.translate(dx, dy);
                if(rc.onTheMap(newNode) && rc.canSenseLocation(newNode)){
                    edgeWeight = (1/rc.sensePassability(newNode));
                    
                    for(Direction d: Utils.directions){
                        dest = newNode.add(d);
                        if(rc.onTheMap(dest)){
                            MapLocation[] edge = {newNode, dest};
                            edgeMap.put(edge, edgeWeight);
                        }
                    }
                }
            }
        }

        return edgeMap;
    }

    public HashMap<MapLocation, ArrayList<MapLocation>> buildAdjacencyMatrix(RobotController rc) throws GameActionException{
        HashMap<MapLocation, ArrayList<MapLocation>> adjacencyMatrix = new HashMap<MapLocation, ArrayList<MapLocation>>();

        MapLocation curLoc = rc.getLocation();
        MapLocation newNode;
        MapLocation possDest;
        ArrayList<MapLocation> adjacent = new ArrayList<MapLocation>();

        for(int dx = -5; dx < 5; dx++){
            for(int dy= -5; dy < 5; dy++){
                adjacent.clear();
                newNode = curLoc.translate(dx, dy);
                if(rc.onTheMap(newNode) && rc.canSenseLocation(newNode)){
                    for(Direction d: Utils.directions){
                        possDest = newNode.add(d);
                        if(rc.onTheMap(possDest) && rc.canSenseLocation(possDest) && !rc.isLocationOccupied(possDest)){
                            adjacent.add(possDest);
                        }
                    }
                }
                adjacencyMatrix.put(newNode, adjacent);
            }
        }

        return adjacencyMatrix;
    }

    public DijkPair extractMin(HashMap<MapLocation, Double> q){
        MapLocation nextEdge; 
        Double minVal = Double.NEGATIVE_INFINITY;

        for(MapLocation k: q.keySet()){
            if(minVal > q.get(k)){
                nextEdge = k;
                minVal = q.get(k);
            }
        }

        return new DijkPair(nextEdge, minVal);
    }

    public Direction findOptimalDirection(MapLocation dest, HashMap<MapLocation, MapLocation> parents){
        MapLocation lastPlace = dest;
        MapLocation nextPlace = dest;

        while(parents.nextPlace != null){
            lastPlace = nextPlace;
            nextPlace = parents.get(nextPlace);
        }

        return nextPlace.directionTo(lastPlace);
    }

    public MapLocation findClosestSensable(RobotController rc, MapLocation ultDest){
        MapLocation curLoc = rc.getLocation();
        Direction bestDir = curLoc.directionTo(ultDest);
        MapLocation closest = curLoc;

        while(rc.canSenseLocation(closest)){
            closest = closest.add(bestDir);
        }

        return closest;
    }

    /*public void dijkstra(RobotController rc, MapLocation dest) throws GameActionException{
        HashMap<MapLocation[], Double> edgeMap = buildEdgeMap(rc);
        HashMap<MapLocation, ArrayList<MapLocation>> adjacencyMatrix = buildAdjacencyMatrix(rc);
        HashMap<MapLocation, Double> distances = new HashMap<MapLocation, Double>();
        HashSet<MapLocation> exploredLocations = new HashSet<MapLocation>();
        HashSet<MapLocation> seen = new HashSet<MapLocation>();
        HashMap<MapLocation, MapLocation> parents = new HashMap<MapLocation, MapLocation>();
        HashMap<MapLocation, Double> queue = new HashMap<MapLocation[], Double>();
        dest = findClosestSensable(rc, dest);

        
        // sets all locations to positive infinity
        for(MapLocation k: adjacencyMatrix.keySet()){
            distances.put(k, Double.POSITIVE_INFINITY);
        }

        MapLocation origin = rc.getLocation();
        DijkPair toExpand;
        Double newDist;

        exploredLocations.add(origin);
        distances.put(origin, 0);
        parents.put(origin, null);

        while(!queue.isEmpty()){
            toExpand = extractMin(queue);
            seen.add(toExpand.loc);

            for (MapLocation l: adjacencyMatrix.get(toExpand.loc)){
                if(!seen.contains(l)){
                    new_dist = toExpand.dist + (edgeMap.get({toExpand.loc, l}));
                    if(new_dist <= distances.get(l)){
                        distances.put(l, new_dist);
                        exploredLocations.add(l);
                        parents.put(l, toExpand.loc);
                    }
                    queue.add(l, new_dist);
                }
            }
        }

        if(parents.contains(dest)){
            return findOptimalDirection(dest, parents);
        }else{
            return Utils.randomDirection();
        }

    }*/

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
