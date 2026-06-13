package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;


/**
 * Base class for every living cell in the simulation.
 * It stores the shared state used by all species, such as position, age, energy,
 * size and activity status.
 */
public abstract class Cell implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /** The horizontal position of the cell on the grid. */
    protected int x;

    /** The vertical position of the cell on the grid. */
    protected int y;

    /** The number of simulation steps this cell has survived. */
    protected int age;

    /** The current energy level of the cell. */
    protected int energy;

    /** The visual radius used for the simulation display. */
    protected int radius;

    /** Indicates whether the cell is still alive and active in the current step. */
    protected boolean isActive;

    /** Tracks whether the cell has already acted during the current turn. */
    private boolean hasPlayed = false;

    /** Random generator used for age-based death probability. */
    private final java.util.Random cellRandom = new java.util.Random();

    /**
     * Creates a new cell at the given coordinates.
     *
     * @param x the initial horizontal position
     * @param y the initial vertical position
     * @param energy the starting energy of the cell
     * @param radius the display radius of the cell
     */
    public Cell(int x, int y, int energy, int radius) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.radius = radius;
        this.age = 0;
        this.isActive = true;
    }

    /**
     * Updates the cell during one simulation step.
     *
     * @param currentGrid the simulation grid currently being processed
     */
    public abstract void update(Grid currentGrid);

    /**
     * Advances the cell age by one step and may mark it inactive if it becomes too old.
     */
    public void ageOneStep() {
        this.age++;

        if (this.age > 15) {
            double deathProbability = (this.age - 15) * 0.05;

            if (cellRandom.nextDouble() < deathProbability) {
                this.isActive = false;
            }
        }
    }

    /**
     * Moves this cell to a new position.
     *
     * @param newX the new horizontal coordinate
     * @param newY the new vertical coordinate
     */
    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    /**
     * Returns the current x coordinate.
     *
     * @return the horizontal position of the cell
     */
    public int getX() { return x; }

    /**
     * Sets the x coordinate of the cell.
     *
     * @param x the new horizontal position
     */
    public void setX(int x) { this.x = x; }

    /**
     * Returns the current y coordinate.
     *
     * @return the vertical position of the cell
     */
    public int getY() { return y; }

    /**
     * Sets the y coordinate of the cell.
     *
     * @param y the new vertical position
     */
    public void setY(int y) { this.y = y; }

    /**
     * Returns the age of the cell.
     *
     * @return the number of elapsed simulation steps
     */
    public int getAge() { return this.age; }

    /**
     * Sets the age of the cell.
     *
     * @param age the new age value
     */
    public void setAge(int age) { this.age = age; }

    /**
     * Returns the current amount of energy.
     *
     * @return the energy value
     */
    public int getEnergy() { return energy; }

    /**
     * Sets the current amount of energy.
     *
     * @param energy the new energy value
     */
    public void setEnergy(int energy) { this.energy = energy; }

    /**
     * Returns the radius of the cell.
     *
     * @return the display radius
     */
    public int getRadius() { return radius; }

    /**
     * Sets the radius of the cell.
     *
     * @param radius the new radius value
     */
    public void setRadius(int radius) { this.radius = radius; }

    /**
     * Indicates whether the cell is currently active.
     *
     * @return true if the cell is alive and usable, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Changes the active state of the cell.
     *
     * @param active true to activate the cell, false to deactivate it
     */
    public void setActive(boolean active) { isActive = active; }

    /**
     * Indicates whether this cell has already acted this turn.
     *
     * @return true if the cell has played in the current simulation step
     */
    public boolean hasPlayed() { return hasPlayed; }

    /**
     * Marks whether this cell has already acted during the current turn.
     *
     * @param hasPlayed true when the cell has completed its move for this step
     */
    public void setHasPlayed(boolean hasPlayed) { this.hasPlayed = hasPlayed; }

    /**
     * Returns a readable summary of the cell state.
     *
     * @return a string representation of the cell
     */
    @Override
    public String toString() {
        return "Cell{" +
                "type=" + this.getClass().getSimpleName() +
                ", x=" + x +
                ", y=" + y +
                ", age=" + age +
                ", energy=" + energy +
                ", active=" + isActive +
                '}';
    }
}