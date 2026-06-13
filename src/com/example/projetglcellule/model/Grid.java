package com.example.projetglcellule.model;

import com.example.projetglcellule.model.cell.AutotrophCell;
import com.example.projetglcellule.model.cell.CarnivoreCell;
import com.example.projetglcellule.model.cell.Cell;
import com.example.projetglcellule.model.cell.HerbivoreCell;

import java.util.Scanner;

/**
 * Represents the world of the simulation and stores all cells.
 */
public class Grid implements java.io.Serializable {
    /**
     * Serial version ID used for saving and loading the grid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Number of columns in the grid.
     */
    private final int width;

    /**
     * Number of rows in the grid.
     */
    private final int height;

    /**
     * Total number of cells in the grid.
     */
    private final int size;

    /**
     * Current border rule used by the simulation.
     */
    private final Topology topology;

    /**
     * Internal array that stores all cells of the grid.
     */
    private final Cell[] cells;

    /**
     * Creates a new grid with the given size and topology.
     *
     * @param width the number of columns
     * @param height the number of rows
     * @param topology the border behavior of the grid
     */
    public Grid(int width, int height, Topology topology) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        this.topology = topology;
        this.cells = new Cell[size];
    }

    /**
     * Converts a position to the internal array index.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the index in the cell array
     */
    private int index(int x, int y) {
        return x + y * width;
    }

    /**
     * Wraps an x position when the topology is toroidal.
     *
     * @param x the x coordinate to wrap
     * @return the wrapped x position
     */
    private int wrapX(int x) {
        if (topology == Topology.BOUNDED) {
            return x;
        }
        return (x + width) % width;
    }

    /**
     * Wraps a y position when the topology is toroidal.
     *
     * @param y the y coordinate to wrap
     * @return the wrapped y position
     */
    private int wrapY(int y) {
        if (topology == Topology.BOUNDED) {
            return y;
        }
        return (y + height) % height;
    }

    
    /**
     * Returns the cell stored at the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the cell at that position, or null if the position is empty or out of range
     */
    public Cell getCell(int x, int y) {
        if (topology == Topology.BOUNDED && (x < 0 || x >= width || y < 0 || y >= height)) {
            return null;
        }

        x = wrapX(x);
        y = wrapY(y);
        return cells[index(x, y)];
    }

    
    /**
     * Places a cell at the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param cell the cell to place on the grid
     */
    public void setCell(int x, int y, Cell cell) {
        if (topology == Topology.BOUNDED && (x < 0 || x >= width || y < 0 || y >= height)) {
            return;
        }

        x = wrapX(x);
        y = wrapY(y);

        cells[index(x, y)] = cell;

        if (cell != null) {
            cell.setX(x);
            cell.setY(y);
        }
    }

    
    /**
     * Removes the cell at the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void clearCell(int x, int y) {
        if (topology == Topology.BOUNDED && (x < 0 || x >= width || y < 0 || y >= height)) {
            return;
        }

        x = wrapX(x);
        y = wrapY(y);
        cells[index(x, y)] = null;
    }

    
    /**
     * Checks if a position is free.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the position does not contain a cell
     */
    public boolean isEmpty(int x, int y) {
        return getCell(x, y) == null;
    }

    
    /**
     * Returns the width of the grid.
     *
     * @return the number of columns
     */
    public int getWidth() {
        return width;
    }

    
    /**
     * Returns the height of the grid.
     *
     * @return the number of rows
     */
    public int getHeight() {
        return height;
    }

    
    /**
     * Prints the current simulation grid to the console.
     */
    public void display() {
        System.out.println();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = getCell(x, y);

                if (cell == null) {
                    System.out.print(". ");
                } else if (cell instanceof AutotrophCell) {
                    System.out.print("A ");
                } else if (cell instanceof HerbivoreCell) {
                    System.out.print("H ");
                } else if (cell instanceof CarnivoreCell) {
                    System.out.print("C ");
                } else {
                    System.out.print("? ");
                }
            }
            System.out.println();
        }

        System.out.println();
    }

    
    /**
     * Returns a valid position using the active topology.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the adjusted position, or null in bounded mode when out of range
     */
    public Position getAdjustedPosition(int x, int y) {
        if (topology == Topology.BOUNDED) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return null;
            }
            return new Position(x, y);
        }

        int nx = (x + width) % width;
        int ny = (y + height) % height;
        return new Position(nx, ny);
    }

    
    /**
     * Removes every cell from the grid.
     */
    public void clearAll() {
        java.util.Arrays.fill(this.cells, null);
    }

    
    /**
     * Creates a new grid from values entered in the console.
     *
     * @return the created grid
     */
    public static Grid createFromUserInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Grid setup ===");

        int width = 0;
        while (true) {
            try {
                System.out.print("Width (3 to 50): ");
                width = Integer.parseInt(scanner.nextLine().trim());

                if (width >= 3 && width <= 50) {
                    break;
                }

                System.out.println("Invalid value. Enter an integer between 3 and 50.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }

        int height = 0;
        while (true) {
            try {
                System.out.print("Height (3 to 50): ");
                height = Integer.parseInt(scanner.nextLine().trim());

                if (height >= 3 && height <= 50) {
                    break;
                }

                System.out.println("Invalid value. Enter an integer between 3 and 50.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }

        Topology topology;
        while (true) {
            System.out.print("Topology (BOUNDED / TOROIDAL): ");

            String choice = scanner.nextLine().trim().toUpperCase();

            if (choice.equals("BOUNDED")) {
                topology = Topology.BOUNDED;
                break;
            } else if (choice.equals("TOROIDAL")) {
                topology = Topology.TOROIDAL;
                break;
            } else {
                System.out.println("Invalid topology. Enter BOUNDED or TOROIDAL.");
            }
        }

        System.out.println("Grid created: " + width + " x " + height + " with topology " + topology + ".");
        return new Grid(width, height, topology);
    }
}
