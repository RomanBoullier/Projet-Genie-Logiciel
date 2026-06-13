package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Position;
import com.example.projetglcellule.model.Directions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HerbivoreCell extends Cell implements Movable, Consumable {

    private static final int ENERGY_LOSS_PER_STEP = 1;
    private static final int REPRODUCTION_THRESHOLD = 25;
    private final Random random = new Random();

    /**
     * Creates a herbivore cell with the given position and energy.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param energy the starting energy
     * @param radius the cell radius
     */
    public HerbivoreCell(int x, int y, int energy, int radius) {
        super(x, y, energy, radius);
    }

    /**
     * Updates the herbivore during one simulation step.
     *
     * @param currentGrid the current simulation grid
     */
    @Override
    public void update(Grid currentGrid) {
        if (hasPlayed()) return;

        ageOneStep();
        setEnergy(getEnergy() - ENERGY_LOSS_PER_STEP);

        if (!isActive()) return;

        moveWithStrategy(currentGrid);

        if (getEnergy() >= REPRODUCTION_THRESHOLD) {
            reproduce(currentGrid);
        }

        setHasPlayed(true);
    }

    /**
     * Moves the herbivore and eats a plant if one is nearby.
     *
     * @param currentGrid the simulation grid
     */
    @Override
    public void moveWithStrategy(Grid currentGrid) {
        List<Position> plantsNearby = getPlantsNearby(currentGrid);

        if (!plantsNearby.isEmpty()) {
            Position targetPos = plantsNearby.get(random.nextInt(plantsNearby.size()));
            Cell plant = currentGrid.getCell(targetPos.x(), targetPos.y());

            if (plant instanceof Consumable) {
                setEnergy(getEnergy() + ((Consumable) plant).getNutritionalValue());
                plant.setActive(false);
            }

            currentGrid.clearCell(getX(), getY());
            move(targetPos.x(), targetPos.y());
            currentGrid.setCell(targetPos.x(), targetPos.y(), this);

        } else {
            List<Position> emptyNeighbors = getEmptyNeighbors(currentGrid);
            if (!emptyNeighbors.isEmpty()) {
                Position targetPos = emptyNeighbors.get(random.nextInt(emptyNeighbors.size()));
                currentGrid.clearCell(getX(), getY());
                move(targetPos.x(), targetPos.y());
                currentGrid.setCell(targetPos.x(), targetPos.y(), this);
            }
        }
    }

    /**
     * Returns the energy value given to a predator.
     *
     * @return the energy value
     */
    @Override
    public int getNutritionalValue() {
        return getEnergy();
    }

    /**
     * Creates a new herbivore child in an empty nearby place.
     *
     * @param currentGrid the simulation grid
     */
    private void reproduce(Grid currentGrid) {
        List<Position> emptyNeighbors = getEmptyNeighbors(currentGrid);
        if (!emptyNeighbors.isEmpty()) {
            Position spawnPos = emptyNeighbors.get(random.nextInt(emptyNeighbors.size()));
            int childEnergy = getEnergy() / 2;
            HerbivoreCell child = new HerbivoreCell(spawnPos.x(), spawnPos.y(), childEnergy, getRadius());
            currentGrid.setCell(spawnPos.x(), spawnPos.y(), child);
            setEnergy(getEnergy() - childEnergy);
        }
    }

    /**
     * Finds nearby plant cells that can be eaten.
     *
     * @param currentGrid the simulation grid
     * @return a list of plant positions near this cell
     */
    private List<Position> getPlantsNearby(Grid currentGrid) {
        List<Position> plants = new ArrayList<>();
        for (Directions dir : Directions.values()) {
            Position adjPos = currentGrid.getAdjustedPosition(getX() + dir.dx, getY() + dir.dy);
            if (adjPos != null) {
                Cell targetCell = currentGrid.getCell(adjPos.x(), adjPos.y());
                if (targetCell instanceof AutotrophCell && targetCell.isActive()) {
                    plants.add(adjPos);
                }
            }
        }
        return plants;
    }

    /**
     * Finds empty neighboring cells for movement.
     *
     * @param currentGrid the simulation grid
     * @return a list of free neighbor positions
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