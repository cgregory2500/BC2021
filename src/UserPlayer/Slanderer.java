package UserPlayer;
import battlecode.common.*;


public class Slanderer extends Robot{

    public Slanderer(RobotController rc) {
        super(rc);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        if (Utils.tryMove(Utils.randomDirection(), rc))
            System.out.println("I moved!");
    }
}
