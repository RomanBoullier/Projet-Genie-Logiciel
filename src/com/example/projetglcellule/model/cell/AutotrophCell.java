package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Position;
import com.example.projetglcellule.model.Directions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A plant-like cell that gains energy and may create offspring.
 */
public class AutotrophCell extends Cell implements Consumable {

    private static final int ENERGY_GAIN_PER_STEP = 2;
    private static final int REPRODUCTION_ENERGY_THRESHOLD = 15;
    private static final double REPRODUCTION_PROBABILITY = 0.4;
    /** Random generator used to decide whether the cell reproduces. */
    private final Random random = new Random();

    /**
     * Creates a plant cell with the given position and energy.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param energy the starting energy
     * @param radius the cell radius
     */
    public AutotrophCell(int x, int y, int energy, int radius) {
        super(x, y, energy, radius);
    }

    /**
     * Updates the plant cell during one simulation step.
     *
     * @param currentGrid the current simulation grid
     */
    @Override
    public void update(Grid currentGrid) {
        ageOneStep();
        if (!isActive()) return;

        setEnergy(getEnergy() + ENERGY_GAIN_PER_STEP);

        if (getEnergy() >= REPRODUCTION_ENERGY_THRESHOLD && random.nextDouble() < REPRODUCTION_PROBABILITY) {
            reproduce(currentGrid);
        }
    }

    /**
     * Returns the energy value given to a consumer.
     *
     * @return the energy value
     */
    @Override
    public int getNutritionalValue() {
        return getEnergy();
    }

    /**
     * Creates a new plant child in an empty nearby place.
     *
     * @param currentGrid the simulation grid
     */
    private void reproduce(Grid currentGrid) {
        List<Position> emptyNeighbors = getEmptyNeighbors(currentGrid);
        if (!emptyNeighbors.isEmpty()) {
            Position targetPos = emptyNeighbors.get(random.nextInt(emptyNeighbors.size()));
            int childEnergy = getEnergy() / 2;
            AutotrophCell child = new AutotrophCell(targetPos.x(), targetPos.y(), childEnergy, getRadius());
            currentGrid.setCell(targetPos.x(), targetPos.y(), child);
            setEnergy(getEnergy() - childEnergy);
        }
    }

    /**
     * Finds all empty neighboring cells around this plant.
     *
     * @param currentGrid the simulation grid
     * @return a list of available neighbor positions
     */
    private List<Position> getEmptyNeighbors(Grid currentGrid) {
        List<Position> empty = new ArrayList<>();
        for (Directions dir : Directions.values()) {
            Position adjPos = currentGrid.getAdjustedPosition(getX() + dir.dx, getY() + dir.dy);
            if (adjPos != null && currentGrid.isEmpty(adjPos.x(), adjPos.y())) {
                empty.add(adjPos);
            }
        }
        return empty;
    }
}