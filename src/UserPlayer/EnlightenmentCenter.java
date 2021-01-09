package UserPlayer;
import battlecode.common.*;
import java.lang.String;
import java.util.HashMap;
import java.lang.Integer;

public class EnlightenmentCenter extends Robot{
    Communications comms = new Communications();
    private boolean closeNeutralEC;

    // maps buildThreshholds maps round numbers to influence threshholds for building a new bot
    private HashMap<Integer, Integer> buildThreshholds = new HashMap<Integer, Integer>(); 

    public EnlightenmentCenter(RobotController r){
        super(r);
        closeNeutralEC = false;

        for(int i = 0; i < 3000; i++){
            if(i < 150){
                buildThreshholds.put(i, 150);
            }else if (i < 500){
                buildThreshholds.put(i, 500);
            } else if (i < 1000){
                buildThreshholds.put(i, 500);
            } else if (i < 2000){
                buildThreshholds.put(i, 1000);
            }else{
                buildThreshholds.put(i, 1000);
            }
        }
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        int bidAmount = setBidAmount();
        int leftoverInfluence = rc.getInfluence() - bidAmount;
        comms.useComms(rc);
        /*RobotType toBuild = Utils.randomSpawnableRobotType();
        int influence = 50;
        for (Direction dir : Utils.directions) {
            if (rc.canBuildRobot(toBuild, dir, influence)) {
                rc.buildRobot(toBuild, dir, influence);
            } else {
                break;
            }
        }*/
        if (buildThreshholds.get(rc.getRoundNum()) < rc.getInfluence()){
            buildRobots(leftoverInfluence);
        }
        rc.bid(bidAmount);
        System.out.println("just bid: " +  bidAmount);
    }

    private int setBidAmount(){
        int totalInfluence = rc.getInfluence();
        int roundNum = rc.getRoundNum();
        int curVotes = rc.getTeamVotes();

        if (curVotes > 1500){
            return 0;
        }

        if(roundNum < 150){
            if ((int) roundNum/2 > curVotes){
                return 1;
            }else {
                return 1;
            }
        }
        return 1;
    }

    private void buildRobots(int leftInf) throws GameActionException{
        int roundNum = rc.getRoundNum();
        int curVotes = rc.getTeamVotes();
        int epsilon = (int) (Math.random() * 100);
        System.out.println("epsilon:" + epsilon);

        if(curVotes > 1500){
            //survival stages
            if(epsilon < 50){
                constructRobot((int) leftInf/10, RobotType.POLITICIAN);
            }else{
                constructRobot((int) leftInf/10, RobotType.MUCKRAKER);
            }
        }else {
            //growth stages 
            if(roundNum < 150){
                //early game builds
                if(epsilon < 30){
                    constructRobot((int) leftInf/10, RobotType.SLANDERER);
                }else if (epsilon < 75){
                    constructRobot((int) leftInf/10, RobotType.POLITICIAN);
                } else{
                    constructRobot((int) leftInf/10, RobotType.MUCKRAKER);
                }
            }else if (roundNum < 1750){
                //mid game builds
                if(epsilon < 50){
                    constructRobot((int) leftInf/10, RobotType.POLITICIAN);
                }else if (epsilon < 70){
                    constructRobot((int) leftInf/10, RobotType.SLANDERER);
                } else{
                    constructRobot((int) leftInf/10, RobotType.MUCKRAKER);
                }
            }else{
                //late game builds
                if(epsilon < 50){
                    constructRobot((int) leftInf/10, RobotType.MUCKRAKER);
                }else if (epsilon < 80){
                    constructRobot((int) leftInf/10, RobotType.POLITICIAN);
                } else{
                    constructRobot((int) leftInf/10, RobotType.SLANDERER);
                }
            }
        }
    }

    private void constructRobot(int inf, RobotType type) throws GameActionException{
        for (Direction dir : Utils.directions){
            if(rc.canBuildRobot(type, dir, inf)){
                rc.buildRobot(type, dir, inf);
                break;
            }
        }
    }
}
