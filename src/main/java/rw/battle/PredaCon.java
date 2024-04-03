package rw.battle;

import rw.enums.Direction;
import rw.enums.WeaponType;

/**
 * A PredaCon is a Robot with a set ARMOR STRENGTH and a user provided WEAPON TYPE
 *
 * @author Jonathan Hudson
 * @version 1.0
 */
public final class PredaCon extends Robot {

    /**
     * The set armor strength of a PredaCon
     */
    private static final int PREDACON_ARMOR_STRENGTH = 2;

    /**
     * The user provided weapon type
     */
    private final WeaponType weaponType;

    /**
     * A PredaCon has regular health and symbol as well as a weapon type
     *
     * @param symbol     The symbol of predacon
     * @param name       The name of the predacon
     * @param health     The health of the predacon
     * @param weaponType The weapon type of the predacon
     */
    public PredaCon(char symbol, String name, int health, WeaponType weaponType) {
        super(symbol, name, health);
        this.weaponType = weaponType;
    }

    /**
     * Gets PredaCon's weapon type
     *
     * @return The PredaCon's weapon type
     */
    public WeaponType getWeaponType() {
        return this.weaponType;
    }

    /**
     * The weapon strength of PredaCon is from their weapon type
     *
     * @return The weapon strength of PredaCon is from their weapon type
     */
    @Override
    public int weaponStrength() {
        return weaponType.getWeaponStrength();
    }

    /**
     * The armor strength of PredaCon is from the stored constant
     *
     * @return The armor strength of PredaCon is from the stored constant
     */
    @Override
    public int armorStrength() {
        return PREDACON_ARMOR_STRENGTH;
    }


    @Override
    public String toString() {
        return super.toString() + "\t" + weaponType;
    }

    @Override
    public Direction chooseMove(Battle local) {
        Direction dir = Direction.WEST;
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
        for(Direction dir: Direction.values()){
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
