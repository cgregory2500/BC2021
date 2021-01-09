package UserPlayer;
import java.lang.Double;
import battlecode.common.*;

public class DijkPair {
    MapLocation loc;
    Double dist;

    public DijkPair(MapLocation location, Double distance){
        this.loc = location;
        this.dist = distance;
    }

}