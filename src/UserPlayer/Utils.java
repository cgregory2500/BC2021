package UserPlayer;
import battlecode.common.*;
import java.lang.Double;
import java.util.HashMap;

public class Utils {
    public static HashMap<RobotType, Double> baseCooldown = new HashMap<RobotType, Double>();

    public static void buildHashMapDependencies(){
        baseCooldown.put(RobotType.POLITICIAN, 1.0);
        baseCooldown.put(RobotType.ENLIGHTENMENT_CENTER, 2.0);
        baseCooldown.put(RobotType.SLANDERER, 1.5);
        baseCooldown.put(RobotType.MUCKRAKER, 2.0);
    }

    public static final RobotType[] spawnableRobot = {
            RobotType.POLITICIAN,
            RobotType.SLANDERER,
            RobotType.MUCKRAKER,
    };

    public static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    public static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random spawnable RobotType
     *
     * @return a random RobotType
     */
    public static RobotType randomSpawnableRobotType() {
        return spawnableRobot[(int) (Math.random() * spawnableRobot.length)];
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    public static boolean tryMove(Direction dir, RobotController rc) throws GameActionException {
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }
}
