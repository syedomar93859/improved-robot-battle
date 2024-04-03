package rw.shell;

import rw.battle.Battle;
import rw.util.Reader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * PredaCons vs Maximals
 * A simple 2D grid simulation. Load a defined battle file and step by step resolve movement in grid to determine if
 * the Maximals win, or the PredaCons win. Simulation step order is in order that Robots are added to the simulation and resolved one robot at a time.
 *
 * @author Jonathan Hudson
 * @version 1.0
 */
public final class Main {


    /**
     * A program-wide random number generator
     */
    public static Random random = new Random(12345);

    /**
     * Check arguments, read battle, setup logger, and start simulation
     *
     * @param args Program arguments, Usage: Main &lt;battle&gt; &lt;log&gt; &lt;seed&gt;
     */
    public static void main(String[] args) {
        //Check for 3 arguments
        checkArgument(args);

        //Get the two filenames
        File fileBattle = new File(args[0]);
        File fileLog = new File(args[1]);

        //Create the random number generator
        setupRNG(args[2]);

        //Check if files are accessible
        checkFiles(fileBattle, fileLog);

        //Set up Menu with logger and read battle from file
        Menu.setup(fileLog);
        Menu.println("Arguments: " + Arrays.toString(args));
        Battle battle = Reader.loadBattle(fileBattle);
        if (battle == null) {
            System.err.println("The Reader class is returning a null file still!");
            System.exit(1);
        }
        //Run simulation
        runSimulation(battle);
    }

    /**
     * Verify that program has the 3 command line arguments necessary
     *
     * @param args The program arguments
     */
    private static void checkArgument(String[] args) {
        if (args.length != 3) {
            System.err.println("Program requires 3 arguments!");
            System.err.println("Usage: Main <battle> <log> <seed>");
            System.exit(1);
        }
    }

    /**
     * Setup random number generator
     *
     * @param seed The integer seed for the RNG
     */
    private static void setupRNG(String seed) {
        try {
            random = new Random(Integer.parseInt(seed));
        } catch (Exception e) {
            System.err.printf("Third argument %s should be integer seed!%n", seed);
            System.exit(1);
        }
    }

    /**
     * Verify if files exists and can be accessed properly
     * Create log file if it doesn't exist and require confirmation to overwrite past log files
     *
     * @param fileBattle The battle file storing the game setup
     * @param fileLog    The log file for program output
     */
    private static void checkFiles(File fileBattle, File fileLog) {
        //Check battle file
        if (!fileBattle.exists() || !fileBattle.isFile() || !fileBattle.canRead()) {
            System.err.printf("The battle file %s does not exist!%n", fileBattle.getAbsoluteFile());
            System.exit(1);
        }
        //Check log file overwrite?
        if (fileLog.exists() && fileLog.isFile() && fileLog.canWrite()) {
            System.out.println("Overwrite log file?");
            if (Menu.checkYes()) {
                if (!fileLog.delete()) {
                    System.err.println("Failed to delete log file!");
                }
            } else {
                System.err.println("Ending program instead of overwriting log file!");
                System.exit(0);
            }
        }
        //Create log file if the log file doesn't exist
        if (!fileLog.exists()) {
            try {
                if (!fileLog.createNewFile()) {
                    System.err.println("Failed to create log file!");
                }
            } catch (IOException e) {
                System.err.printf("Cannot create new log file %s%n!", fileLog.getAbsoluteFile());
                System.exit(0);
            }
        }
        //Check log file
        if (!fileLog.exists() || !fileLog.isFile() || !fileLog.canWrite()) {
            System.err.printf("The log file %s cannot be accessed!%n", fileLog.getAbsoluteFile());
            System.exit(1);
        }
        //We should trust both of our input files at this point
    }

    /**
     * Run the PredaCon versus Maximal simulation
     *
     * @param battle The loaded battle state to simulate
     */
    private static void runSimulation(Battle battle) {
        while (battle.isActive()) {
            String message = battle.gameString();
            Menu.println(message);
            if (Menu.continueSimulation()) {
                battle.advanceSimulation();
            } else {
                battle.endSimulation();
            }
        }
        String message = battle.gameString();
        Menu.println(message);
    }
}
