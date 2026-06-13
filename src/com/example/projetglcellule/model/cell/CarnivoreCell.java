package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Position;
import com.example.projetglcellule.model.Directions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CarnivoreCell extends Cell implements Movable {

    private static final int ENERGY_LOSS_PER_STEP = 3;
    private static final int REPRODUCTION_THRESHOLD = 40;
    private static final double ATTACK_SUCCESS_PROBABILITY = 0.75;
    private final Random random = new Random();

    /**
     * Creates a carnivore cell with the given position and energy.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param energy the starting energy
     * @param radius the cell radius
     */
    public CarnivoreCell(int x, int y, int energy, int radius) {
        super(x, y, energy, radius);
    }

    /**
     * Updates the carnivore during one simulation step.
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
     * Moves the carnivore and tries to hunt nearby prey.
     *
     * @param currentGrid the simulation grid
     */
    @Override
    public void moveWithStrategy(Grid currentGrid) {
        List<Position> preyNearby = getPreyNearby(currentGrid);

        if (!preyNearby.isEmpty()) {
            Position targetPos = preyNearby.get(random.nextInt(preyNearby.size()));
            Cell prey = currentGrid.getCell(targetPos.x(), targetPos.y());

            if (prey instanceof Consumable) {
                if (random.nextDouble() < ATTACK_SUCCESS_PROBABILITY) {
                    System.out.println("💥 A carnivore successfully hunted an herbivore at (" + targetPos.x() + "," + targetPos.y() + ")");
                    setEnergy(getEnergy() + ((Consumable) prey).getNutritionalValue());
                    prey.setActive(false);
                    currentGrid.clearCell(getX(), getY());
                    move(targetPos.x(), targetPos.y());
                    currentGrid.setCell(targetPos.x(), targetPos.y(), this);
                } else {
                    System.out.println("🏃 An herbivore managed to escape a carnivore's attack at (" + targetPos.x() + "," + targetPos.y() + ")");
                }
            }
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
     * Creates a new carnivore child in an empty nearby place.
     *
     * @param currentGrid the simulation grid
     */
    private void reproduce(Grid currentGrid) {
        List<Position> emptyNeighbors = getEmptyNeighbors(currentGrid);
        if (!emptyNeighbors.isEmpty()) {
            Position spawnPos = emptyNeighbors.get(random.nextInt(emptyNeighbors.size()));
            int childEnergy = getEnergy() / 2;
            CarnivoreCell child = new CarnivoreCell(spawnPos.x(), spawnPos.y(), childEnergy, getRadius());
            currentGrid.setCell(spawnPos.x(), spawnPos.y(), child);
            setEnergy(getEnergy() - childEnergy);
        }
    }

    /**
     * Finds nearby herbivore cells that can be hunted.
     *
     * @param currentGrid the simulation grid
     * @return a list of prey positions near this cell
     */
    private List<Position> getPreyNearby(Grid currentGrid) {
        List<Position> preys = new ArrayList<>();
        for (Directions dir : Directions.values()) {
            Position adjPos = currentGrid.getAdjustedPosition(getX() + dir.dx, getY() + dir.dy);
            if (adjPos != null) {
                Cell targetCell = currentGrid.getCell(adjPos.x(), adjPos.y());
                if (targetCell instanceof HerbivoreCell && targetCell.isActive()) {
                    preys.add(adjPos);
                }
            }
        }
        return preys;
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