package UserPlayer;
import battlecode.common.*;
import java.util.ArrayList;
import java.lang.Integer;
import java.lang.String;
import java.util.HashSet;

public class Communications{
    //Old Communication Dependencies
    public int curClosestEC = 0;
    public boolean discoveredEC;
    public boolean closestECNeutral;
    public HashSet<Integer> knownECs = new HashSet<Integer>();
    public boolean rushing;

    //new Communication Dependencies
    public MapLocation curEC;
    public HashSet<Integer> possXs = new HashSet<Integer>();
    public HashSet<Integer> possYs = new HashSet<Integer>();
    public HashSet<MapLocation> neutrals = new HashSet<MapLocation>();
    public ArrayList<MapLocation> possECs = new ArrayList<MapLocation>();

    public Communications(){
        this.discoveredEC = false;
        this.rushing = false;
    }

    public void useComms(RobotController rc) throws GameActionException{
        closerEnemyEC(rc);
        System.out.println("Reading messages works");
        evaluatePossLocs(rc);
        if(possECs.size() > 0){
            curEC = closestPossEC(rc);
        }

        //old code
        if (curEC != null){
            sendCoordsOfEnemyEC(rc);
        }
    }

    public ArrayList<Integer> readFlagsAndScanForEnemyEC(RobotController rc) throws GameActionException{
        RobotInfo[] robots= rc.senseNearbyRobots();
        int[] robotIDs = new int[robots.length];
        for (int i = 0; i < robots.length; i++){
            robotIDs[i] = robots[i].ID;
            if(robots[i].type == RobotType.ENLIGHTENMENT_CENTER && robots[i].team != rc.getTeam()){
                if(possECs.contains(robots[i].location)){
                    discoveredEC = false;
                    if (robots[i].team == Team.NEUTRAL){
                        neutrals.add(robots[i].location);
                        closestECNeutral = true;
                    }else {
                        closestECNeutral = false;
                    }
                } else {
                    discoveredEC = true;
                    possECs.add(robots[i].location);
                    curEC = robots[i].location;
                    if (robots[i].team == Team.NEUTRAL){
                        neutrals.add(robots[i].location);
                        closestECNeutral = true;
                    } else {
                        closestECNeutral = false;
                    }
                }
            }
        }

        ArrayList<Integer> flagMessages = new ArrayList<Integer>();
        for (int ind = 0; ind < robotIDs.length; ind++){
            if(rc.canGetFlag(robotIDs[ind])){
                flagMessages.add(rc.getFlag(robotIDs[ind]));
            }
        }
        
        return flagMessages; 
    }

    public void closerEnemyEC(RobotController rc) throws GameActionException{
        ArrayList<Integer> messages = readFlagsAndScanForEnemyEC(rc);

        //old code
        /*int distToCurEC;
        int curX = rc.getLocation().x;
        int minDist;
        if(curClosestEC > 0){
            minDist = Math.abs(curX - curClosestEC);
        }else{
            minDist = 100;
        }
        String tag = "10101";*/

        for(Integer m: messages){
            decodeMessage(m, rc);

            // old code 
            /*String binString = Integer.toBinaryString(m);
            if(binString.length() > 5){
                if(binString.substring(0, 4) == tag){
                    int xFromMessage = Integer.parseUnsignedInt(binString.substring(8,23), 2);
                    knownECs.add(xFromMessage);

                    if(rc.getType() == RobotType.POLITICIAN){
                        if(binString.substring(5, 7) == "111"){
                            rushing = true;
                        }
                    }

                    if (minDist > Math.abs(curX - xFromMessage)){
                        minDist = Math.abs(curX - xFromMessage);
                        curClosestEC = xFromMessage;

                        if(binString.substring(5, 7) == "010"){
                            closestECNeutral = true;
                        } else {
                            closestECNeutral = false;
                        }
                    }
                } 
            }*/
        }
    }

    public void decodeMessage(int message, RobotController rc){
        String binString = Integer.toBinaryString(message);

        if(binString.length() > 5  && binString.substring(0, 4) == "10101"){
            switch(binString.substring(5, 7)){
                case "010": if (rc.getType() != RobotType.MUCKRAKER) possXs.add(Integer.parseUnsignedInt(binString.substring(8, 23)));
                case "000": possXs.add(Integer.parseUnsignedInt(binString.substring(8, 23)));
                case "110": possXs.add(Integer.parseUnsignedInt(binString.substring(8, 23))); discoveredEC = true;
                case "011": if (rc.getType() != RobotType.MUCKRAKER) possYs.add(Integer.parseUnsignedInt(binString.substring(8, 23)));
                case "001": possYs.add(Integer.parseUnsignedInt(binString.substring(8, 23)));
                case "101": possYs.add(Integer.parseUnsignedInt(binString.substring(8, 23))); discoveredEC = true;
            }
        }
    }

    public void sendCoordsOfEnemyEC(RobotController rc) throws GameActionException{
        int message = encodeEnemyEC(rc);
        if(rc.canSetFlag(message)){
            rc.setFlag(message);
        }
    }

    public int encodeEnemyEC(RobotController rc) {

        String middleMessage = middleString(rc);
        String endMessage = endMessage(rc);
        String message = "10101" + middleMessage + endMessage;

        return Integer.parseUnsignedInt(message, 2);
    }

    public String middleString(RobotController rc){
        int round = rc.getRoundNum();

        if(round % 2 == 1){
            if(closestECNeutral){
                return "010";
            }else{
                return "000";
            }
        } else {
            if(closestECNeutral){
                return "011";
            }else{
                return "001";
            }
        }
    }

    public String endMessage(RobotController rc){
        int round = rc.getRoundNum();
        int coord;

        if (round % 2 == 1){
            coord = curEC.x;
        } else {
            coord = curEC.y;
        }

        return Integer.toBinaryString(coord);
    }

    public void reportBack(RobotController rc, Navigation nav, MapLocation EC) throws GameActionException{
        if (!rc.canSenseLocation(EC)){
            nav.simpleNav(rc, EC);
        } else {
            discoveredEC = false;
            nav.scout(rc);
        }
    }

    public void evaluatePossLocs(RobotController rc){
        for(int x: possXs){
            for(int y: possYs){
                MapLocation newLoc = new MapLocation(x, y);
                if(!inPossECs(newLoc)){
                    if(rc.getType() == RobotType.MUCKRAKER && !neutrals.contains(newLoc)){
                        continue;
                    } else {
                        possECs.add(newLoc);
                    }
                }
            }
        }
    }

    public boolean inPossECs(MapLocation loc){
        for(MapLocation pec: possECs){
            if (loc.equals(pec)){
                return true;
            }
        }
        return false;
    }

    public MapLocation closestPossEC(RobotController rc){
        MapLocation curLoc = rc.getLocation();
        int minDist = 10000;
        MapLocation bestLoc = rc.getLocation();

        for(MapLocation pec: possECs){
            if(minDist > curLoc.distanceSquaredTo(pec)){
                bestLoc = pec;
                minDist = curLoc.distanceSquaredTo(pec);
            }
        }

        return bestLoc;
    }

    public void goToClosestEC(RobotController rc, Navigation nav) throws GameActionException{
        nav.simpleNav(rc, curEC);
    }

}