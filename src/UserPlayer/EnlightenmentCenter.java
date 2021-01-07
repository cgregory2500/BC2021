package UserPlayer;
import battlecode.common.*;

public class EnlightenmentCenter extends Robot{

    public EnlightenmentCenter(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        RobotType toBuild = Utils.randomSpawnableRobotType();
        System.out.println("here  2222222222222222222222");
        int influence = 50;
        System.out.println(rc);
        for (Direction dir : Utils.directions) {
            System.out.println("here  3333333333333333333333333333");
            if (rc.canBuildRobot(toBuild, dir, influence)) {
                rc.buildRobot(toBuild, dir, influence);
                System.out.println("here 111111111111");
            } else {
                break;
            }
        }
    }
}
