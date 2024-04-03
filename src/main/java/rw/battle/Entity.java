package rw.battle;

/**
 * A Battle map is made up of entities (this class itself cannot be constructed, but extensions of it (child classes) will).
 *
 * @author Jonathan Hudson
 * @version 1.0
 */
public abstract class Entity {

    /**
     * Entities all have a symbol for printing a battle map
     */
    private final char symbol;

    /**
     * Create entity with given symbol (since class is abstract, only children can be made)
     *
     * @param symbol The symbol of entity
     */
    protected Entity(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Get symbol
     *
     * @return The symbol for map
     */
    public final char getSymbol() {
        return symbol;
    }

    /**
     * Can this entity be moved on top of
     *
     * @return Boolean if true
     */
    public abstract boolean canMoveOnTopOf();

    /**
     * Can this entity be attacked
     *
     * @return Boolean if true
     */
    public abstract boolean canBeAttacked();

}