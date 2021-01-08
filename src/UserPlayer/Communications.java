package UserPlayer;
import battlecode.common.*;
import java.util.ArrayList;
import java.lang.Integer;
import java.lang.String;

public class Communications{
    public int curClosestEC = 0;

    public Communications(){

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
            if(robots[i].type == RobotType.ENLIGHTENMENT_CENTER && robots[i].team != rc.getTeam()){
                curClosestEC = robots[i].location.x;
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
                    if (minDist > Math.abs(curX - xFromMessage)){
                        minDist = Math.abs(curX - xFromMessage);
                        curClosestEC = xFromMessage;
                    }
                } 
            }
        }
    }

    public void sendCoordsOfEnemyEC(RobotController rc) throws GameActionException{
        int message = encodeEnemyEC();
        if(rc.canSetFlag(message)){
            rc.setFlag(message);
        }
    }

    public int encodeEnemyEC() {
        return Integer.parseUnsignedInt("10101000" + Integer.toBinaryString(curClosestEC), 2);
    }

}