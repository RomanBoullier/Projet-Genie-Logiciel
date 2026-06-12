package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Position;
import com.example.projetglcellule.model.Directions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutotrophCell extends Cell implements Consumable {

    private static final int ENERGY_GAIN_PER_STEP = 2;
    private static final int REPRODUCTION_ENERGY_THRESHOLD = 15;
    private static final double REPRODUCTION_PROBABILITY = 0.4;
    private final Random random = new Random();

    public AutotrophCell(int x, int y, int energy, int radius) {
        super(x, y, energy, radius);
    }

    @Override
    public void update(Grid currentGrid) {
        ageOneStep();
        if (!isActive()) return;

        // Photosynthèse
        setEnergy(getEnergy() + ENERGY_GAIN_PER_STEP);

        // Reproduction
        if (getEnergy() >= REPRODUCTION_ENERGY_THRESHOLD && random.nextDouble() < REPRODUCTION_PROBABILITY) {
            reproduce(currentGrid);
        }
    }

    @Override
    public int getNutritionalValue() {
        return getEnergy(); // L'herbivore gagne toute l'énergie de la plante
    }

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