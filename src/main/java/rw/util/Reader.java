package rw.util;

import rw.battle.*;
import rw.enums.WeaponType;

import java.io.*;
import java.util.ArrayList;

/** Syed Omar
 *  March 20, 2024
 *  T12
 */

/**
 * Class to assist reading in battle file
 * @author Jonathan Hudson
 * @author Syed Omar
 * UCID: 30206798
 * @version 1.0
 */

public final class Reader {
    /**
     * This method is used to read from a file and extract information from it such as the details of the entities and floors. The
     * entities and floors are then added using addEntity.
     * @param file
     * @author Syed Omar
     * @return battleMap
     */
    public static Battle loadBattle(File file) {
        int rowNum = 0;
        int columnNum = 0;
        int lineWithCoords = 0;
        Battle battleMap = null;
        try {
            FileReader battleFile = new FileReader(file);
            BufferedReader bufferedFile = new BufferedReader(battleFile);
            try {
                String line = bufferedFile.readLine();
                while (line != null) {
                    if (line.length() == 1 && lineWithCoords == 1) {
                        columnNum = Integer.parseInt(line);
                        battleMap = new Battle(rowNum, columnNum);
                        line = bufferedFile.readLine();
                    } else if (line.length() == 1) {
                        lineWithCoords++;
                        rowNum = Integer.parseInt(line);
                        line = bufferedFile.readLine();
                    } else {
                        line = line.replace("\n", "");
                        String spaceLessLine = line.replace(" ", "");
                        String[] lineDetails = spaceLessLine.split(",");
                        if (lineDetails.length == 2) {
                            //floor
                            int floorXCoordinate = Integer.parseInt(lineDetails[0]);
                            int floorYCoordinate = Integer.parseInt(lineDetails[1]);
                            battleMap.addEntity(floorXCoordinate, floorYCoordinate, null);
                        } else if (lineDetails.length == 3) {
                            //wall
                            int wallXCoordinate = Integer.parseInt(lineDetails[0]);
                            int wallYCoordinate = Integer.parseInt(lineDetails[1]);
                            Wall wallType = Wall.getWall();
                            battleMap.addEntity(wallXCoordinate, wallYCoordinate, wallType);
                        } else if (lineDetails.length > 6 && lineDetails.length < 9) {
                            //robot
                            int robotXCoordinate = Integer.parseInt(lineDetails[0]);
                            int robotYCoordinate = Integer.parseInt(lineDetails[1]);
                            String robotType = lineDetails[2];
                            String robotSymbol = lineDetails[3];
                            String robotName = lineDetails[4];
                            String robotHealth = lineDetails[5];
                            if (robotType.equals("PREDACON")) {
                                String robotWeapon = lineDetails[6];
                                WeaponType weaponType = null;
                                if (robotWeapon.equals("C")) {
                                    weaponType = WeaponType.CLAWS;
                                } else if (robotWeapon.equals("T")) {
                                    weaponType = WeaponType.TEETH;
                                } else if (robotWeapon.equals("L")) {
                                    weaponType = WeaponType.LASER;
                                }
                                PredaCon predaConInstance = new PredaCon(robotSymbol.charAt(0), robotName, Integer.parseInt(robotHealth), weaponType);
                                battleMap.addEntity(robotXCoordinate, robotYCoordinate, predaConInstance);
                            } else if (robotType.equals("MAXIMAL")) {
                                int robotAttackStrength = Integer.parseInt(lineDetails[6]);
                                int robotArmourStrength = Integer.parseInt(lineDetails[7]);
                                Maximal maximalInstance = new Maximal(robotSymbol.charAt(0), robotName, Integer.parseInt(robotHealth), robotAttackStrength, robotArmourStrength);
                                battleMap.addEntity(robotXCoordinate, robotYCoordinate, maximalInstance);
                            }
                        }line = bufferedFile.readLine();
                    }
                }bufferedFile.close();
            } catch (IOException e) {
                throw new UncheckedIOException(new IOException("Line cannot be read in this file: " + file));
            }
        }catch (FileNotFoundException e) {
            throw new UncheckedIOException(new FileNotFoundException("File not found: " + file));
        }
        return battleMap;
    }
}

