package rw.battle;

import rw.shell.Main;
import rw.shell.Menu;
import rw.enums.Direction;
import rw.enums.Symbol;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Battle is a 2D grid of entities, null Spots are floor spots
 *
 * @author Jonathan Hudson
 * @version 1.0
 */
public class Battle {

    /**
     * The local view of battle for attacking will be 3x3 grid
     */
    private static final int ATTACK_BATTLE_SIZE = 3;
    /**
     * The local view of battle for moving will be 5x5 grid
     */
    private static final int MOVE_BATTLE_SIZE = 5;
    /**
     * The storage of entities in Battle, floor is null, Dead robots can be moved on top of (deleting them from the map)
     */
    private final Entity[][] battle;
    /**
     * We track the order that robots were added (this is used to determine order of actions each turn)
     * Robots remain in this list (Even if DEAD) ,unlike the battle Robot[][] where they can be moved on top of causing deletion.
     */
    private final ArrayList<Robot> robots;
    /**
     * We use a HashMap to track robot location in battle {row, column}
     * We will update this every time a Robot is shifted in the battle Robot[][]
     */
    private final HashMap<Entity, Integer[]> locations;
    /**
     * The Battle starts ACTIVE
     */
    private State state;

    /**
     * A new battle of ROWSxCOLUMNS in size
     *
     * @param rows    The 1D of the 2D battle (rows)
     * @param columns The 2D of the 2D battle (columns)
     */
    public Battle(int rows, int columns) {
        this.battle = new Entity[rows][columns];
        this.robots = new ArrayList<>();
        this.locations = new HashMap<>();
        //Starts active
        this.state = State.ACTIVE;
    }

    /**
     * Is this simulation still considered ACTIVE
     *
     * @return True if the simulation still active, otherwise False
     */
    public boolean isActive() {
        return state == State.ACTIVE;
    }

    /**
     * End the simulation, (Set in INACTIVE)
     */
    public void endSimulation() {
        this.state = State.INACTIVE;
    }

    /**
     * Return a sub-battle Entity[][] grid focused around given row,col of odd grid size 3,5,7,9...
     *
     * @param size         The size of grid
     * @param centreRow    The row to generate grid around
     * @param centreColumn The column to generate grid around
     * @return Battle of given size centred around given location
     */
    public Battle getLocal(int size, int centreRow, int centreColumn) {
        final int ADJ = (size - 1) / 2;
        //Check that size is large enough, and is an odd number
        if (size < 3 || size % 2 != 1) {
            throw new IllegalArgumentException("Local size must be 3,5,7,...!");
        }
        //Make the empty battle
        Battle local = new Battle(size, size);
        //Now loop through indices in create local view of battle
        for (int localRow = 0; localRow < size; localRow++) {
            for (int localColumn = 0; localColumn < size; localColumn++) {
                //Get the index in the greater battle that matches
                int battleRow = centreRow + localRow - (ADJ);
                int battleColumn = centreColumn + localColumn - (ADJ);
                //
                //If the lookup is validly in the Battle we get that robot (be it null or not and store it)
                if (battleRow >= 0 && battleRow < battle.length && battleColumn >= 0 && battleColumn < battle[0].length) {
                    local.addEntity(localRow, localColumn, battle[battleRow][battleColumn]);
                }
                //If the lookup is for something outside battle, then we fill with a Wall
                else {
                    local.addEntity(localRow, localColumn, Wall.getWall());
                }
            }
        }
        return local;
    }

    /**
     * Advance the simulation one step
     */
    public void advanceSimulation() {
        //Do not advance if simulation is done
        if (state == State.INACTIVE) {
            return;
        }
        //If not done go through all robots (this will be in order read and added from file)
        for (Robot robot : robots) {
            //If robot is something that is ALIVE, we want to give it a turn to ATTACK or MOVE
            if (robot.isAlive()) {
                //Get location of robot (only the battle knows this, the robot does not itself)
                Integer[] location = locations.get(robot);
                //Pull out row,column
                int row = location[0];
                int column = location[1];
                //Determine if/where a robot wants to attack
                Battle attackBattle3X3 = getLocal(ATTACK_BATTLE_SIZE, row, column);
                Direction attackWhere = robot.attackWhere(attackBattle3X3);
                //If I don't attack, then I must be moving
                if (attackWhere == null) {
                    //Figure out where robot wants to move
                    Battle moveBattle5x5 = getLocal(MOVE_BATTLE_SIZE, row, column);
                    Direction moveWhere = robot.chooseMove(moveBattle5x5);
                    //Log moving
                    Menu.println(String.format("%s moving %s", robot.shortString(), moveWhere));
                    //If this move is valid, then move it
                    if (canMoveOnTopOf(row, column, moveWhere)) {
                        moveEntity(row, column, moveWhere);
                    } else {
                        //Otherwise, indicate an invalid attempt to move
                        Menu.println(String.format("%s  tried to move somewhere it could not!", robot.shortString()));
                    }
                } else {
                    //If we are here our earlier attack question was not null, and we are attacking a nearby robot
                    //Get the robot we are attacking
                    Entity entity = getEntity(row, column, attackWhere);
                    Menu.println(String.format("%s attacking %s in direction %s", robot.shortString(), entity.getClass(), attackWhere));
                    //Can we attack this entity
                    if (canBeAttacked(row, column, attackWhere)) {
                        if (entity instanceof Robot) {
                            Robot attacked = (Robot) entity;
                            //Determine damage using RNG
                            int damage = 1 + Main.random.nextInt(robot.weaponStrength());
                            int true_damage = Math.max(0, damage - attacked.armorStrength());
                            Menu.println(String.format("%s attacked %s for %d damage against %d defense for %d", robot.shortString(), attacked.shortString(), damage, attacked.armorStrength(), true_damage));
                            attacked.damage(true_damage);
                            if (!attacked.isAlive()) {
                                locations.remove(attacked);
                                Menu.println(String.format("%s died!", attacked.shortString()));
                            }
                        } else {
                            Menu.println(String.format("%s  tried to attack somewhere it could not!", robot.shortString()));
                        }
                    }
                }
            }
        }
        checkActive();
    }

    /**
     * Check if simulation has now ended (only one of two versus Robot types is alive)
     */
    private void checkActive() {
        boolean maximal_alive = false;
        boolean predacon_alive = false;
        for (Robot robot : robots) {
            if (robot.isAlive()) {
                if (robot instanceof PredaCon) {
                    predacon_alive = true;
                } else if (robot instanceof Maximal) {
                    maximal_alive = true;
                }
            }
        }
        if (!(maximal_alive && predacon_alive)) {
            state = State.INACTIVE;
        }
    }

    /**
     * Move an existing entity
     *
     * @param row    The  row location of existing entity
     * @param column The  column location of existing entity
     * @param d      The direction to move the entity in
     */
    public void moveEntity(int row, int column, Direction d) {
        Entity entity = this.getEntity(row, column);
        int moveRow = row + d.getRowChange();
        int moveColumn = column + d.getColumnChange();
        this.battle[moveRow][moveColumn] = entity;
        this.battle[row][column] = null;
        this.locations.put(entity, new Integer[]{moveRow, moveColumn});
    }

    /**
     * Add a new entity
     *
     * @param row    The  row location of new entity
     * @param column The  column location of new entity
     * @param entity The entity to add
     */
    public void addEntity(int row, int column, Entity entity) {
        this.battle[row][column] = entity;
        if (entity instanceof Robot) {
            this.robots.add((Robot) entity);
            this.locations.put(entity, new Integer[]{row, column});
        }
    }

    /**
     * Get entity at a location
     *
     * @param row    The row of the entity
     * @param column The column of the entity
     * @return The Entity at the given row, column
     */
    public Entity getEntity(int row, int column) {
        return this.battle[row][column];
    }

    /**
     * Get entity at a location
     *
     * @param row    The row of the entity
     * @param column The column of the entity
     * @param d      The direction adjust look up towards
     * @return The Entity at the given row, column
     */
    public Entity getEntity(int row, int column, Direction d) {
        return this.getEntity(row + d.getRowChange(), column + d.getColumnChange());
    }

    /**
     * See if we can move to location
     *
     * @param row    The row to check
     * @param column The column to check
     * @return True if we can move to that location
     */
    public boolean canMoveOnTopOf(int row, int column) {
        if(!valid(row,column)){
            return false;
        }
        Entity entity = this.getEntity(row, column);
        if (entity == null) {
            return true;
        }
        return entity.canMoveOnTopOf();
    }

    /**
     * Is an index within the battle
     * Assume rectangular map
     * @param row    The row to check
     * @param column The column to check
     * @return True if yes, false otherwise
     */
    public boolean valid(int row, int column) {
        if(row >=0 && row < battle.length && column >=0 && column < battle[0].length){
            return true;
        }
        return false;
    }

    /**
     * See if we can move to location
     *
     * @param row    The row to check
     * @param column The column to check
     * @param d      The direction adjust look up towards
     * @return True if we can move to that location
     */
    public boolean canMoveOnTopOf(int row, int column, Direction d) {
        return canMoveOnTopOf(row + d.getRowChange(), column + d.getColumnChange());
    }

    /**
     * See if we can attack entity at a location
     *
     * @param row    The row to check
     * @param column The column to check
     * @return True if we can attack entity at that location
     */
    public boolean canBeAttacked(int row, int column) {
        if(!valid(row,column)){
            return false;
        }
        Entity entity = this.getEntity(row, column);
        if (entity == null) {
            return false;
        }
        return entity.canBeAttacked();

    }

    /**
     * See if we can attack entity at a location
     *
     * @param row    The row to check
     * @param column The column to check
     * @param d      The direction adjust look up towards
     * @return True if we can attack robot at that location
     */
    public boolean canBeAttacked(int row, int column, Direction d) {
        return canBeAttacked(row + d.getRowChange(), column + d.getColumnChange());

    }

    /**
     * See if entity is maximal at this location
     *
     * @param row    The row to check
     * @param column The column to check
     * @return True if entity is a maximal at that location
     */
    public boolean isMaximal(int row, int column) {
        Entity entity = this.getEntity(row, column);
        if (entity == null) {
            return false;
        }
        return entity instanceof Maximal;
    }

    /**
     * See if entity is predacon at this location
     *
     * @param row    The row to check
     * @param column The column to check
     * @return True if entity is a predacon at that location
     */
    public boolean isPredaCon(int row, int column) {
        Entity entity = this.getEntity(row, column);
        if (entity == null) {
            return false;
        }
        return entity instanceof PredaCon;
    }



    /**
     * A String version of the battle
     *
     * @return String version of the battle, '#' for walls, '.' for empty floor, '$' for dead entities
     */
    public String battleString() {
        StringBuilder sb = new StringBuilder();
        //Top wall
        sb.append(String.valueOf(Wall.getWall().getSymbol()).repeat(battle[0].length + 2));
        sb.append("\n");
        //Each row
        for (Entity[] row : battle) {
            //Start with wall
            sb.append(Wall.getWall().getSymbol());
            for (Entity entity : row) {
                if (entity != null) {
                    if(entity instanceof Robot) {
                        if (((Robot)entity).isAlive()) {
                            //Entity symbol
                            sb.append(entity.getSymbol());
                        } else {
                            //Dead
                            sb.append(Symbol.DEAD.getSymbol());
                        }
                    } else if(entity instanceof Wall) {
                        sb.append(entity.getSymbol());
                    }
                } else {
                    //Floor
                    sb.append(Symbol.FLOOR.getSymbol());
                }
            }
            //End with wall
            sb.append(Wall.getWall().getSymbol());
            sb.append("\n");
        }
        //Bottom wall
        sb.append(String.valueOf(Wall.getWall().getSymbol()).repeat(battle[0].length + 2));
        sb.append("\n");
        return sb.toString();
    }

    /**
     * A String version of the battle
     * Consists of the game map followed by information about the entities in the battle
     *
     * @return String version of the game
     */
    public String gameString() {
        StringBuilder sb = new StringBuilder();
        sb.append(battleString());
        //Header for table
        sb.append("NAME   \tS\tH\tSTATE\tINFO\n");
        for (Entity e : robots) {
            sb.append(e.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return gameString();
    }

    /**
     * The rows of the battle
     *
     * @return The rows of the battle
     */
    public int getRows() {
        return battle.length;
    }

    /**
     * The columns of the battle
     *
     * @return The columns of the battle
     */
    public int getColumns() {
        return battle[0].length;
    }

    /**
     * Battle starts ACTIVE, but will turn INACTIVE after a simulation ends with only one type of Robot still ALIVE
     */
    private enum State {
        ACTIVE, INACTIVE
    }

}



