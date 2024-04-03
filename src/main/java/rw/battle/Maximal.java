package rw.battle;

import rw.enums.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A Maximal is a Robot with a user provided WEAPON STRENGTH and ARMOR STRENGTH
 *
 * @author Jonathan Hudson
 * @version 1.0
 */
public final class Maximal extends Robot {

    /**
     * The user provided weapon strength
     */
    private final int weaponStrength;

    /**
     * The user provided armor strength
     */
    private final int armorStrength;

    /**
     * A Maximal has regular health and symbol as well as a weapon strength and armor strength
     *
     * @param symbol         The symbol of maximal
     * @param name           The name of the maximal
     * @param health         The health of the maximal
     * @param weaponStrength The weapon strength of the maximal
     * @param armorStrength  The armor strength of the maximal
     */
    public Maximal(char symbol, String name, int health, int weaponStrength, int armorStrength) {
        super(symbol, name, health);
        this.weaponStrength = weaponStrength;
        this.armorStrength = armorStrength;
    }

    /**
     * The weapon strength of Maximal is from user value
     *
     * @return The weapon strength of maximal is from user value
     */
    @Override
    public int weaponStrength() {
        return weaponStrength;
    }

    /**
     * The armor strength of Maximal is from user value
     *
     * @return The armor strength of maximal is from user value
     */
    @Override
    public int armorStrength() {
        return armorStrength;
    }

    @Override
    public String toString() {
        return super.toString() + "\t" + weaponStrength + "\t" + armorStrength;
    }

    @Override
    public Direction chooseMove(Battle local) {
        Direction dir = Direction.EAST;
        if (local.canMoveOnTopOf(2, 2, dir)) {
            return dir;
        }
        dir = Direction.getRandomDirection();
        if (local.canMoveOnTopOf(2, 2, dir)) {
            return dir;
        }
        return Direction.STAY;
    }


    @Override
    public Direction attackWhere(Battle local) {
        List<Direction> directions = Arrays.asList(Direction.values());
        Collections.reverse(directions);
        for(Direction dir: directions){
            if(dir == Direction.STAY){
                continue;
            }
            if (local.canBeAttacked(1, 1, dir)) {
                return dir;
            }
        }
        return null;
    }
}
