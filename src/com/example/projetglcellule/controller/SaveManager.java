package com.example.projetglcellule.controller;

import com.example.projetglcellule.model.Grid;
import java.io.*;


/**
 * Saves and loads the current simulation state from a file.
 */
public class SaveManager {

    /**
     * Creates a save manager instance.
     */
    public SaveManager() {
    }

    private static final String SAVE_FILE_NAME = "simulation_save.ser";

    /**
     * Saves the current grid to a file on disk.
     *
     * @param grid the grid to save
     */
    public static void saveGame(Grid grid) {
        try (FileOutputStream fileOut = new FileOutputStream(SAVE_FILE_NAME);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(grid);
            System.out.println("💾 Simulation successfully saved to '" + SAVE_FILE_NAME + "'!");

        } catch (IOException e) {
            System.err.println("❌ Error while saving the simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a saved grid from the simulation file.
     *
     * @return the loaded grid, or null if no file is found
     */
    public static Grid loadGame() {
        File saveFile = new File(SAVE_FILE_NAME);
        if (!saveFile.exists()) {
            System.out.println("⚠️ No save file found named '" + SAVE_FILE_NAME + "'.");
            return null;
        }

        try (FileInputStream fileIn = new FileInputStream(SAVE_FILE_NAME);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            Grid restoredGrid = (Grid) objectIn.readObject();
            System.out.println("📂 Simulation successfully restored from '" + SAVE_FILE_NAME + "'!");
            return restoredGrid;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error while loading the simulation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}