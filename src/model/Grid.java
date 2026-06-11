package com.example.projetglcellule.model;

import com.example.projetglcellule.model.cell.AutotrophCell;
import com.example.projetglcellule.model.cell.CarnivoreCell;
import com.example.projetglcellule.model.cell.Cell;
import com.example.projetglcellule.model.cell.HerbivoreCell;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Grid {

    private final int width;
    private final int height;
    private final int size;

    private final Topology topology;

    private final Cell[] cells;

    private final Map<Integer, Organism> organisms;

    public Grid(int width, int height, Topology topology) {

        this.width = width;
        this.height = height;
        this.size = width * height;
        this.topology = topology;

        this.cells = new Cell[size];

        this.organisms = new HashMap<>();
    }

    private int index(int x, int y) {
        return x + y * width;
    }

    private int wrapX(int x) {
        if (topology == Topology.BOUNDED) {
            return x;
        }
        return (x + width) % width;
    }

    private int wrapY(int y) {
        if (topology == Topology.BOUNDED) {
            return y;
        }
        return (y + height) % height;
    }

    public Cell getCell(int x, int y) {

        if (topology == Topology.BOUNDED) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return null;
            }
        }

        x = wrapX(x);
        y = wrapY(y);

        return cells[index(x, y)];
    }

    public void setCell(int x, int y, Cell cell) {

        if (topology == Topology.BOUNDED) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return;
            }
        }

        x = wrapX(x);
        y = wrapY(y);

        cells[index(x, y)] = cell;

        if (cell != null) {
            cell.setX(x);
            cell.setY(y);
        }
    }

    public void clearCell(int x, int y) {

        if (topology == Topology.BOUNDED) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return;
            }
        }

        x = wrapX(x);
        y = wrapY(y);

        cells[index(x, y)] = null;
    }

    public boolean isEmpty(int x, int y) {
        return getCell(x, y) == null;
    }

    public void addOrganism(Organism organism) {
        if (organism != null) {
            organisms.put(organism.getId(), organism);
        }
    }

    public Map<Integer, Organism> getOrganisms() {
        return organisms;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void display() {

        System.out.println();

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                Cell cell = getCell(x, y);

                if (cell == null) {
                    System.out.print(". ");
                }
                else if (cell instanceof AutotrophCell) {
                    System.out.print("A ");
                }
                else if (cell instanceof HerbivoreCell) {
                    System.out.print("H ");
                }
                else if (cell instanceof CarnivoreCell) {
                    System.out.print("C ");
                }
                else {
                    System.out.print("? ");
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    public static Grid createFromUserInput() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Configuration de la grille ===");

        int width = 0;
        while (true) {
            try {
                System.out.print("Largeur (entre 3 et 50) : ");
                width = Integer.parseInt(scanner.nextLine().trim());

                if (width >= 3 && width <= 50) {
                    break;
                }

                System.out.println("Valeur invalide. Veuillez saisir un entier entre 3 et 50.");

            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide. Veuillez entrer un entier.");
            }
        }

        int height = 0;
        while (true) {
            try {
                System.out.print("Hauteur (entre 3 et 50) : ");
                height = Integer.parseInt(scanner.nextLine().trim());

                if (height >= 3 && height <= 50) {
                    break;
                }

                System.out.println("Valeur invalide. Veuillez saisir un entier entre 3 et 50.");

            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide. Veuillez entrer un entier.");
            }
        }

        Topology topology;

        while (true) {

            System.out.print("Topologie (BOUNDED / TOROIDAL) : ");

            String choice = scanner.nextLine().trim().toUpperCase();

            if (choice.equals("BOUNDED")) {
                topology = Topology.BOUNDED;
                break;
            }
            else if (choice.equals("TOROIDAL")) {
                topology = Topology.TOROIDAL;
                break;
            }
            else {
                System.out.println("Topologie invalide. Entrez BOUNDED ou TOROIDAL.");
            }
        }

        System.out.println(
                "Grille créée : "
                        + width
                        + " x "
                        + height
                        + " avec topologie "
                        + topology
                        + "."
        );

        return new Grid(width, height, topology);
    }
}