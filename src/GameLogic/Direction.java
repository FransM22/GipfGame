package GameLogic;

/**
 * Created by frans on 29-9-2015.
 */
public enum Direction {
    NORTH,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH,
    SOUTH_EAST,
    SOUTH_WEST;

    public int getDeltaPos() {
        // Determine the deltaPos value based on the direction
        switch (this) {
            case NORTH:
                return 1;
            case NORTH_EAST:
                return 11;
            case SOUTH_EAST:
                return 10;
            case SOUTH:
                return -1;
            case SOUTH_WEST:
                return -11;
            case NORTH_WEST:
                return -10;
        }

        return -1;
    }

    public static Direction getDirectionFromDeltaPos(int deltaPos) {
        switch (deltaPos) {
            case 1:
                return NORTH;
            case 11:
                return NORTH_EAST;
            case 10:
                return SOUTH_EAST;
            case -1:
                return SOUTH;
            case -11:
                return SOUTH_WEST;
            case -10:
                return NORTH_WEST;

            default:
                return null;
        }
    }
}