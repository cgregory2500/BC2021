package UserPlayer;
import battlecode.common.*;

public class Communications{

    public Communications(){

    }

    public int[] readFlags(RobotController rc) throws GameActionException{
        RobotInfo[] robots= rc.senseNearbyRobots();
        int[] robotIDs = new int[robots.length];
        for (int i = 0; i < robots.length; i++){
            robotIDs[i] = robots[i].ID;
        }

        int[] flagMessages = new int[robotIDs.length];
        for (int ind = 0; i < robotIDs.length; ind++){
            if(rc.canGetFlag(robotIDs[i])){

            }else{
                flagMessages[i] = null;
            }
        }
        
        return flagMessages; 
    }

    public boolean closerEnemyEC(){


        return false;
    }

    public void sendCoordsOfEnemyEC(){
        
    }

}