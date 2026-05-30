/**
 * Represents the 2D simulation grid.
 * Each cell of the grid can contain a cell name (String) or be empty (null).
 */
public class Map {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Minimum allowed dimension for the grid (width or height). */
    private static final int MIN_SIZE = 2;

    /** Maximum allowed dimension for the grid (width or height). */
    private static final int MAX_SIZE = 500;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** Number of columns in the grid. */
    private int width;

    /** Number of rows in the grid. */
    private int height;

    /**
     * The grid itself.
     * {@code grid[row][col]} holds the name of the cell occupying that position,
     * or {@code null} if the position is empty.
     */
    private String[][] grid;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a grid with the given dimensions.
     * All positions are initialised to {@code null} (empty).
     *
     * @param width  number of columns (must be between {@value #MIN_SIZE} and {@value #MAX_SIZE})
     * @param height number of rows    (must be between {@value #MIN_SIZE} and {@value #MAX_SIZE})
     * @throws IllegalArgumentException if either dimension is out of the allowed range
     */
    public Map(int width, int height) {
        validateDimension(width,  "width");
        validateDimension(height, "height");

        this.width  = width;
        this.height = height;
        this.grid   = new String[height][width];
    }

    // -------------------------------------------------------------------------
    // Factory – interactive setup
    // -------------------------------------------------------------------------

    /**
     * Interactively asks the user to choose the grid dimensions via the console,
     * then returns a fully initialised {@code Map}.
     *
     * <p>The user is prompted separately for the width and the height.
     * Invalid inputs (non-integer, out-of-range) are rejected with an
     * explanatory message and the question is repeated until a valid value
     * is entered.</p>
     *
     * @return a new {@code Map} configured with the dimensions chosen by the user
     */
    public static Map createFromUserInput() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        System.out.println("=== Grid configuration ===");
        int width  = askDimension(scanner, "width",  MIN_SIZE, MAX_SIZE);
        int height = askDimension(scanner, "height", MIN_SIZE, MAX_SIZE);

        Map map = new Map(width, height);
        System.out.println("Grid created: " + width + " x " + height);
        return map;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the number of columns in the grid.
     *
     * @return grid width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of rows in the grid.
     *
     * @return grid height
     */
    public int getHeight() {
        return height;
    }

    // -------------------------------------------------------------------------
    // Grid access
    // -------------------------------------------------------------------------

    /**
     * Returns the name of the cell at position {@code (col, row)},
     * or {@code null} if the position is empty.
     *
     * @param col column index (0-based)
     * @param row row    index (0-based)
     * @return the cell name, or {@code null}
     * @throws IndexOutOfBoundsException if the coordinates are outside the grid
     */
    public String getCell(int col, int row) {
        checkBounds(col, row);
        return grid[row][col];
    }

    /**
     * Places a cell name at position {@code (col, row)}.
     * Pass {@code null} to clear the position.
     *
     * @param col      column index (0-based)
     * @param row      row    index (0-based)
     * @param cellName the name to store, or {@code null} to empty the tile
     * @throws IndexOutOfBoundsException if the coordinates are outside the grid
     */
    public void setCell(int col, int row, String cellName) {
        checkBounds(col, row);
        grid[row][col] = cellName;
    }

    /**
     * Checks whether position {@code (col, row)} is empty (contains {@code null}).
     *
     * @param col column index (0-based)
     * @param row row    index (0-based)
     * @return {@code true} if the position is empty
     * @throws IndexOutOfBoundsException if the coordinates are outside the grid
     */
    public boolean isEmpty(int col, int row) {
        checkBounds(col, row);
        return grid[row][col] == null;
    }

    /**
     * Removes the cell at position {@code (col, row)}, leaving the tile empty.
     *
     * @param col column index (0-based)
     * @param row row    index (0-based)
     * @throws IndexOutOfBoundsException if the coordinates are outside the grid
     */
    public void clearCell(int col, int row) {
        setCell(col, row, null);
    }

    /**
     * Empties every tile of the grid.
     */
    public void clearAll() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c] = null;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Display
    // -------------------------------------------------------------------------

    /**
     * Prints the grid to {@link System#out}.
     * Empty tiles are shown as {@code "."} and occupied tiles show the first
     * character of the cell name surrounded by brackets, e.g. {@code "[A]"}.
     */
    public void display() {
        for (int r = 0; r < height; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < width; c++) {
                if (grid[r][c] == null) {
                    line.append(" . ");
                } else {
                    line.append("[").append(grid[r][c].charAt(0)).append("]");
                }
            }
            System.out.println(line);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Prompts the user repeatedly until a valid integer dimension is entered.
     *
     * @param scanner   the input scanner to read from
     * @param dimension human-readable name of the dimension ("width" or "height")
     * @param min       minimum accepted value (inclusive)
     * @param max       maximum accepted value (inclusive)
     * @return the validated dimension chosen by the user
     */
    private static int askDimension(java.util.Scanner scanner,
                                    String dimension, int min, int max) {
        while (true) {
            System.out.print("Enter the grid " + dimension
                    + " (" + min + "-" + max + "): ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Value must be between " + min
                            + " and " + max + ". Please try again.");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: \"" + input
                        + "\" is not an integer. Please try again.");
            }
        }
    }

    /**
     * Validates that a single dimension value is within the allowed range.
     *
     * @param value     the dimension value to check
     * @param dimension human-readable name used in the error message
     * @throws IllegalArgumentException if the value is out of range
     */
    private static void validateDimension(int value, String dimension) {
        if (value < MIN_SIZE || value > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid " + dimension + ": " + value
                    + ". Must be between " + MIN_SIZE + " and " + MAX_SIZE + ".");
        }
    }

    /**
     * Checks that {@code (col, row)} lies within the grid.
     *
     * @param col column index
     * @param row row    index
     * @throws IndexOutOfBoundsException if either index is out of range
     */
    private void checkBounds(int col, int row) {
        if (col < 0 || col >= width || row < 0 || row >= height) {
            throw new IndexOutOfBoundsException(
                    "Coordinates (" + col + ", " + row + ") are out of bounds "
                    + "for a " + width + "x" + height + " grid.");
        }
    }
}