package rw.util;

import rw.battle.Battle;
import rw.battle.Entity;
import rw.battle.Maximal;
import rw.battle.PredaCon;
import rw.battle.Wall;
import rw.enums.WeaponType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class to assist writing battle state to a file
 *
 * @author Jonathan Hudson
 * @author Syed Omar
 * UCID: 30206798
 * @version 1.0
 */
public final class Writer {
    /**
     * This method is used to extract information from the battle, and add them to a new file called
     * saveFile.
     * @param file,battle
     * @author Syed Omar
     */
    public static void saveBattle(Battle battle, File file) {
        BufferedWriter saveFile = null;
        try {
            saveFile = new BufferedWriter(new FileWriter(file));
            //Adding the rows and columns to saveFile
            saveFile.write(String.valueOf(battle.getRows()));
            saveFile.newLine();
            saveFile.write(String.valueOf(battle.getColumns()));
            saveFile.newLine();
            //Adding entities to saveFile
            for (int rowIndex = 0; rowIndex < battle.getRows(); rowIndex++) {
                for (int columnIndex = 0; columnIndex < battle.getColumns(); columnIndex++) {
                    Entity entity = battle.getEntity(rowIndex, columnIndex);
                    if (entity == null) {
                        saveFile.write(String.format("%d,%d", rowIndex, columnIndex));
                    } else if (entity instanceof Wall) {
                        saveFile.write(String.format("%d,%d,WALL", rowIndex, columnIndex));
                    } else if (entity instanceof PredaCon) {
                        PredaCon predaConEntity = (PredaCon) entity;
                        String entityWeapon = predaConEntity.getWeaponType().name();
                        char firstLetterOfWeapon = entityWeapon.charAt(0);
                        saveFile.write(String.format("%d,%d,PREDACON,%c,%s,%d,", rowIndex, columnIndex, predaConEntity.getSymbol(), predaConEntity.getName(), predaConEntity.getHealth()) + firstLetterOfWeapon);
                    } else if (entity instanceof Maximal) {
                        Maximal maximalEntity = (Maximal) entity;
                        saveFile.write(String.format("%d,%d,MAXIMAL,%c,%s,%d,%d,%d", rowIndex, columnIndex, maximalEntity.getSymbol(), maximalEntity.getName(), maximalEntity.getHealth(), maximalEntity.weaponStrength(), maximalEntity.armorStrength()));
                    }
                    saveFile.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (saveFile != null) {
                    saveFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}