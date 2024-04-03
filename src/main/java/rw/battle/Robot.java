package rw.battle;

import rw.enums.Direction;

public abstract class Robot extends Entity {
    /**
     * Allows us to generate unique IDs for each robot crated.
     */
    private static int counter = 1;
    /**
     * Robots all have a name that is more detailed than the map symbol
     */
    private final String name;
    /**
     * ID to help tell each robot apart so shared symbols don't become confusing
     */
    private final int id;
    /**
     * Robots have a state
     */
    private State state;
    /**
     * Robots have health and at <=0 they will go from ALIVE to DEAD
     */
    private int health;

    /**
     * Create robot with given symbol and health (since class is abstract, only children can be made)
     *
     * @param symbol The symbol of robot
     * @param name   The name of the robot
     * @param health The health of the robot
     */
    protected Robot(char symbol, String name, int health) {
        super(symbol);
        this.name = name;
        if (health < 0) {
            throw new IllegalArgumentException("Health for robot must be >= 0. Value given was " + health + "!");
        }
        this.health = health;
        this.state = State.ALIVE;
        //If health was too low (<=0) then update this robot to dead
        checkDead();
        this.id = counter;
        counter++;
    }

    /**
     * Reset the ID counter (for testing purposes only)
     */
    public static void resetIDCounter() {
        counter = 1;
    }

    /**
     * Get robots health
     *
     * @return The health of robot, should be >= 0 (<=0 is DEAD)
     */
    public final int getHealth() {
        return health;
    }

    /**
     * Is robot ALIVE?
     *
     * @return True if state is alive (health should be > 0)
     */
    public final boolean isAlive() {
        return state == State.ALIVE;
    }

    /**
     * Is robot DEAD?
     *
     * @return True if state is dead (health should be == 0)
     */
    public final boolean isDead() {
        return !isAlive();
    }

    /**
     * Damage the health of robot for the given positive (or 0) amount
     * We will update state of ALIVE/DEAD if health falls to <= 0
     *
     * @param damage The damage to subtract from the robot
     */
    public final void damage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must be reported as positive (or zero) value (which is subtracted from current health)!");
        }
        this.health = Math.max(0, this.health - damage);
        checkDead();
    }

    /**
     * Internal function to check if robot has moved from alive to dead
     */
    private void checkDead() {
        if (this.health <= 0) {
            this.state = State.DEAD;
        }
    }

    /**
     * Returns the weapon strength of the robot based on subtype
     *
     * @return Weapon strength of robot
     */
    public abstract int weaponStrength();

    /**
     * Returns the armor strength of the robot based on subtype
     *
     * @return Armor strength of robot
     */
    public abstract int armorStrength();

    /**
     * Where does this robot want to move based on local view of battle (5x5)
     *
     * @param local The local view of the robot
     * @return The Direction the robot wants to move (presumption that robot already decided to not attack)
     */
    public abstract Direction chooseMove(Battle local);

    /**
     * Where does this robot want to attack based on local view of battle (immediate neighbors 3x3)
     *
     * @param local The local view of the robot (immediate neighbors 3x3)
     * @return The Direction the robot wants to attack (null if not attacking)
     */
    public abstract Direction attackWhere(Battle local);

    /**
     * Can only be moved on top of if dead
     *
     * @return isDead()
     */
    @Override
    public final boolean canMoveOnTopOf() {
        return isDead();
    }

    /**
     * Can only be attacked if alive
     *
     * @return isAlive()
     */
    @Override
    public final boolean canBeAttacked() {
        return isAlive();
    }

    /**
     * String version of robot that contains nothing but the CLASS and ID
     *
     * @return "CLASS(ID)" form of robot
     */
    public final String shortString() {
        return getClass().getSimpleName().substring(0, 4) + "(" + id + ")";
    }

    /**
     * String version of robot that contains CLASS(ID) SYMBOL NAME HEALTH STATE
     *
     * @return "CLASS(ID) SYMBOL NAME HEALTH STATE" form of robot
     */
    @Override
    public String toString() {
        return shortString() + "\t" + getSymbol() + "\t" + name + "\t" + health + "\t" + state;
    }

    public String getName() {
        return name;
    }

    /**
     * Robots are either ALIVE or DEAD
     */
    private enum State {
        ALIVE, DEAD
    }
}
