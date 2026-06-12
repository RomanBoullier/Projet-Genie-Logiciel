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

    public HerbivoreCell(int x, int y, int energy, int radius) {
        super(x, y, energy, radius);
    }

    @Override
    public void update(Grid currentGrid) {
        ageOneStep();
        setEnergy(getEnergy() - ENERGY_LOSS_PER_STEP);
        if (!isActive()) return;

        // Délégation du déplacement à l'interface Movable
        moveWithStrategy(currentGrid);

        // Reproduction
        if (getEnergy() >= REPRODUCTION_THRESHOLD) {
            reproduce(currentGrid);
        }
    }

    @Override
    public void moveWithStrategy(Grid currentGrid) {
        List<Position> plantsNearby = getPlantsNearby(currentGrid);

        if (!plantsNearby.isEmpty()) {
            Position targetPos = plantsNearby.get(random.nextInt(plantsNearby.size()));
            Cell plant = currentGrid.getCell(targetPos.x(), targetPos.y());

            // Polymorphisme : on traite la cible comme un objet "Consumable" !
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

    @Override
    public int getNutritionalValue() {
        return getEnergy(); // Le carnivore absorbe l'énergie restante de l'herbivore
    }

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