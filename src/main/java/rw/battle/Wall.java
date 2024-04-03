package rw.battle;

import rw.enums.Symbol;

/**
 * A singleton class for Walls (As we only ever need one)
 * Currently only used to indicate parts of battle that Robots can not move to or attack
 * in the local view of the Battle given to robots
 *
 * @author Jonathan Hudson
 * @version 1.0
 */
public final class Wall extends Entity {
    /**
     * We only need one wall, so we'll re-use this one (a singleton) over and over
     */
    private static final Wall wall = new Wall();

    /**
     * We will hide constructor so that Walls can never get made and only the single wall is accessible and re-usable through getWall()
     */
    private Wall() {
        //A wall is always a #
        super(Symbol.WALL.getSymbol());
    }

    /**
     * If we want a wall we'll use the get Wall to re-use the same wall over and over
     *
     * @return The one single Wall that will ever exist can be accessed this way
     */
    public static final Wall getWall() {
        return wall;
    }

    /**
     * Can't be moved on top of
     *
     * @return false
     */
    @Override
    public final boolean canMoveOnTopOf() {
        return false;
    }

    /**
     * Can't be attacked
     *
     * @return false
     */
    @Override
    public final boolean canBeAttacked() {
        return false;
    }
}
