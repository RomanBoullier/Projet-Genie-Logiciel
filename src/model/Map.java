package com.example.projetglcellule.model;

import com.example.projetglcellule.model.cell.Cell;
import com.example.projetglcellule.model.Topology;
import com.example.projetglcellule.model.Position;

public class Map {
    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 500;

    private int width;
    private int height;
    private Cell[][] grid;
    private Topology topology;

    public Map(int width, int height) {
        validateDimension(width,  "width");
        validateDimension(height, "height");
        this.width  = width;
        this.height = height;
        this.grid   = new Cell[height][width];
    }

    public static Map createFromUserInput() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        System.out.println("=== Grid configuration ===");
        int width  = askDimension(scanner, "width",  MIN_SIZE, MAX_SIZE);
        int height = askDimension(scanner, "height", MIN_SIZE, MAX_SIZE);
        Topology topo = askTopology(scanner);

        Map map = new Map(width, height);
        map.setTopology(topo); // On applique le choix de l'utilisateur

        System.out.println("Grid created: " + width + " x " + height + " (" + topo + " mode)");
        return map;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Cell getCell(int col, int row) {
        checkBounds(col, row);
        return grid[row][col];
    }

    public void setCell(int col, int row, Cell cell) {
        checkBounds(col, row);
        grid[row][col] = cell;
        if (cell != null) {
            cell.setX(col);
            cell.setY(row);
        }
    }

    public Topology getTopology() {
        return topology;
    }

    public void setTopology(Topology topology) {
        this.topology = topology;
    }

    public boolean isEmpty(int col, int row) {
        checkBounds(col, row);
        return grid[row][col] == null;
    }

    public void clearCell(int col, int row) {
        setCell(col, row, null);
    }

    public void clearAll() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c] = null;
            }
        }
    }

    // Affichage adapté pour lire la première lettre de la classe de la cellule (ex: O pour OrganismCell)
    public void display() {
        for (int r = 0; r < height; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < width; c++) {
                if (grid[r][c] == null) {
                    line.append(" . ");
                } else {
                    char firstLetter = grid[r][c].getClass().getSimpleName().charAt(0);
                    line.append("[").append(firstLetter).append("]");
                }
            }
            System.out.println(line);
        }
    }

    private static int askDimension(java.util.Scanner scanner, String dimension, int min, int max) {
        while (true) {
            System.out.print("Enter the grid " + dimension + " (" + min + "-" + max + "): ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Value must be between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: \"" + input + "\" is not an integer.");
            }
        }
    }

    private static Topology askTopology(java.util.Scanner scanner) {
        while (true) {
            System.out.print("Choose grid topology (1 for BOUNDED, 2 for TOROIDAL): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) {
                return Topology.BOUNDED;
            } else if (input.equals("2")) {
                return Topology.TOROIDAL;
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }

    private static void validateDimension(int value, String dimension) {
        if (value < MIN_SIZE || value > MAX_SIZE) {
            throw new IllegalArgumentException("Invalid " + dimension + ": " + value + ".");
        }
    }

    private void checkBounds(int col, int row) {
        if (col < 0 || col >= width || row < 0 || row >= height) {
            throw new IndexOutOfBoundsException("Coordinates (" + col + ", " + row + ") out of bounds.");
        }
    }

    /**
     * Calculates the real position on the map based on its topology.
     * If the coordinates are out of bounds, it returns null in BOUNDED mode,
     * or wraps around to the opposite side in TOROIDAL mode.
     */
    public Position getAdjustedPosition(int targetX, int targetY) {
        if (topology == Topology.TOROIDAL) {
            // L'opérateur modulo (%) gère le dépassement.
            // On ajoute +width ou +height avant le modulo pour gérer proprement les nombres négatifs (ex: x = -1)
            int wrappedX = (targetX % width + width) % width;
            int wrappedY = (targetY % height + height) % height;
            return new Position(wrappedX, wrappedY);
        } else {
            // Mode BOUNDED : on vérifie simplement si on est dans les limites
            if (targetX >= 0 && targetX < width && targetY >= 0 && targetY < height) {
                return new Position(targetX, targetY);
            }
            return null; // En dehors des limites, la case n'existe pas
        }
    }
}