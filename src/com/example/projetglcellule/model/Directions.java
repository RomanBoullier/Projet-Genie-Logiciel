package com.example.projetglcellule.model;


/**
 * Lists the four basic directions used to inspect neighboring cells.
 */
public enum Directions {
    /** Moves one cell upward. */
    NORTH(0, -1),

    /** Moves one cell downward. */
    SOUTH(0, 1),

    /** Moves one cell to the left. */
    EAST(-1, 0),

    /** Moves one cell to the right. */
    WEST(1, 0);

    /** Horizontal offset applied by this direction. */
    public final int dx;

    /** Vertical offset applied by this direction. */
    public final int dy;

    /**
     * Creates a direction with the given coordinate offsets.
     *
     * @param dx the horizontal shift for this direction
     * @param dy the vertical shift for this direction
     */
    Directions(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}