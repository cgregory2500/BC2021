package UserPlayer;
import battlecode.common.*;
import java.util.ArrayList;
import java.lang.Integer;
import java.lang.String;
import java.util.HashSet;

public class Communications{
    public int curClosestEC = 0;
    public boolean discoveredEC;
    public boolean closestECNeutral;
    public HashSet<Integer> knownECs = new HashSet<Integer>();
    public boolean rushing;

    public Communications(){
        this.discoveredEC = false;
        this.rushing = false;
    }

    public void useComms(RobotController rc) throws GameActionException{
        closerEnemyEC(rc);
        System.out.println("Reading messages works");
        if (curClosestEC != 0){
            sendCoordsOfEnemyEC(rc);
        }
    }

    public ArrayList<Integer> readFlagsAndScanForEnemyEC(RobotController rc) throws GameActionException{
        RobotInfo[] robots= rc.senseNearbyRobots();
        int[] robotIDs = new int[robots.length];
        for (int i = 0; i < robots.length; i++){
            robotIDs[i] = robots[i].ID;
            if(robots[i].type == RobotType.ENLIGHTENMENT_CENTER && robots[i].team != rc.getTeam() && curClosestEC != robots[i].location.x){
                curClosestEC = robots[i].location.x;
                if(knownECs.contains(robots[i].location.x)){
                    discoveredEC = false;
                } else {
                    discoveredEC = true;
                    knownECs.add(robots[i].location.x);
                    if (robots[i].team == Team.NEUTRAL){
                        closestECNeutral = true;
                    }else{
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
        int distToCurEC;
        int curX = rc.getLocation().x;
        int minDist;
        if(curClosestEC > 0){
            minDist = Math.abs(curX - curClosestEC);
        }else{
            minDist = 100;
        }
        String tag = "10101";
        for(Integer m: messages){
            String binString = Integer.toBinaryString(m);
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
            }
        }
    }

    public void sendCoordsOfEnemyEC(RobotController rc) throws GameActionException{
        int message = encodeEnemyEC(rc);
        if(rc.canSetFlag(message)){
            rc.setFlag(message);
        }
    }

    public int encodeEnemyEC(RobotController r) {
        if(r.getType() ==  RobotType.ENLIGHTENMENT_CENTER && r.getInfluence() > 250){
            return Integer.parseUnsignedInt("10101111" + Integer.toBinaryString(curClosestEC), 2);
        }else if (closestECNeutral){
            return Integer.parseUnsignedInt("10101010" + Integer.toBinaryString(curClosestEC), 2);
        } else {
            return Integer.parseUnsignedInt("10101000" + Integer.toBinaryString(curClosestEC), 2);
        }
    }

    public void reportBack(RobotController rc, Navigation nav, MapLocation EC) throws GameActionException{
        if (!rc.canSenseLocation(EC)){
            nav.simpleNav(rc, EC);
        } else {
            discoveredEC = false;
            nav.scout(rc);
        }
    }

}