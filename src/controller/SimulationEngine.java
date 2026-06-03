package com.example.projetglcellule.controller;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.cell.AutotrophCell; // Ajuste le package selon ton arborescence
import com.example.projetglcellule.model.cell.CarnivoreCell;
import com.example.projetglcellule.model.cell.Cell;
import com.example.projetglcellule.model.cell.HerbivoreCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The main controller of the simulation.
 * Manages the time steps, cell updates, and the console-based user interaction.
 */
public class SimulationEngine {

    public static void main(String[] args) {
        // 1. Initialisation de la grille via la console (Exigence du sujet)
        Grid map = Grid.createFromUserInput();
        Scanner scanner = new Scanner(System.in);

        // 2. Placer aléatoirement des cellules de différents stypes au départ
        java.util.Random rand = new java.util.Random();
        int totalCellsToSpawn = (map.getWidth() * map.getHeight()) / 3; // On remplit 30% de la grille

        for (int i = 0; i < totalCellsToSpawn; i++) {
            int rx = rand.nextInt(map.getWidth());
            int ry = rand.nextInt(map.getHeight());

            if (map.isEmpty(rx, ry)) {
                double choice = rand.nextDouble();
                if (choice < 0.75) {
                    // 70% de chance de spawn une plante (il faut beaucoup de végétation)
                    map.setCell(rx, ry, new AutotrophCell(rx, ry, 10, 1));
                } else if (choice < 0.95) {
                    // 25% de chance de spawn un herbivore
                    map.setCell(rx, ry, new HerbivoreCell(rx, ry, 15, 1));
                } else {
                    // 5% de chance de spawn un carnivore (les prédateurs doivent être rares)
                    map.setCell(rx, ry, new CarnivoreCell(rx, ry, 25, 1));
                }
            }
        }

        System.out.println("\n--- Simulation Ready ---");
        System.out.println("Press ENTER to go to the next step. Type 'exit' to stop.");
        map.display();

        int step = 0;

        // 3. Boucle principale de simulation (CLI)
        while (true) {
            String input = scanner.nextLine();

            // Permet de quitter proprement la simulation
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Simulation stopped.");
                break;
            }

            step++;
            System.out.println("\n================ STEP " + step + " ================");

            // Étape A : Collecter toutes les cellules vivantes AVANT les mises à jour.
            // On fait une liste séparée pour éviter que les nouveaux "bébés" créés durant ce tour
            // ne s'activent immédiatement au cours du même tour (ce qui fausserait la simulation).
            List<Cell> cellsToUpdate = new ArrayList<>();
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    Cell cell = map.getCell(x, y);
                    if (cell != null && cell.isActive()) {
                        cellsToUpdate.add(cell);
                    }
                }
            }

            // Étape B : Mettre à jour chaque cellule collectée
            for (Cell cell : cellsToUpdate) {
                // On revérifie isActive() au cas où la cellule se serait fait manger/détruire entre-temps
                if (cell.isActive()) {
                    cell.update(map);
                }
            }

            // Étape C : Nettoyer la grille des cellules mortes (énergie <= 0)
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    Cell cell = map.getCell(x, y);
                    if (cell != null && !cell.isActive()) {
                        map.clearCell(x, y);
                        System.out.println("🌱 A cell died of exhaustion at (" + x + "," + y + ")");
                    }
                }
            }

            // Étape D : Afficher le nouvel état de la grille
            map.display();
        }

        scanner.close();
    }
}