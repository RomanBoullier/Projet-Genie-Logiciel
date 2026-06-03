package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Directions;
import com.example.projetglcellule.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarnivoreCell extends Cell {

    private static final int ENERGY_LOSS_PER_STEP = 3; // Jauge descend très vite !
    private static final int REPRODUCTION_THRESHOLD = 40;
    private final Random random = new Random();

    public CarnivoreCell(int x, int y, int energy, int radius) {
        super(x, y, energy, radius);
    }

    @Override
    public void update(Grid currentGrid) {
        ageOneStep();
        setEnergy(getEnergy() - ENERGY_LOSS_PER_STEP);
        if (!isActive()) return;

        // Traque des herbivores aux alentours
        List<Position> preyNearby = getPreyNearby(currentGrid);

        if (!preyNearby.isEmpty()) {
            // Attaque !
            Position targetPos = preyNearby.get(random.nextInt(preyNearby.size()));
            Cell prey = currentGrid.getCell(targetPos.x(), targetPos.y());

            if (prey != null) {
                setEnergy(getEnergy() + prey.getEnergy()); // Assimile l'énergie restante de la proie
                prey.setActive(false);
            }

            currentGrid.clearCell(getX(), getY());
            move(targetPos.x(), targetPos.y());
            currentGrid.setCell(targetPos.x(), targetPos.y(), this);

        } else {
            // Recherche de cases vides
            List<Position> emptyNeighbors = getEmptyNeighbors(currentGrid);
            if (!emptyNeighbors.isEmpty()) {
                Position targetPos = emptyNeighbors.get(random.nextInt(emptyNeighbors.size()));
                currentGrid.clearCell(getX(), getY());
                move(targetPos.x(), targetPos.y());
                currentGrid.setCell(targetPos.x(), targetPos.y(), this);
            }
        }

        if (getEnergy() >= REPRODUCTION_THRESHOLD) {
            reproduce(currentGrid);
        }
    }

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

    private List<Position> getPreyNearby(Grid currentGrid) {
        List<Position> preys = new ArrayList<>();

        for (Directions dir : Directions.values()) {
            int targetX = getX() + dir.dx;
            int targetY = getY() + dir.dy;

            // Calcul de la position selon la topologie (BOUNDED ou TOROIDAL)
            Position adjPos = currentGrid.getAdjustedPosition(targetX, targetY);

            // Si la case existe bien
            if (adjPos != null) {
                Cell targetCell = currentGrid.getCell(adjPos.x(), adjPos.y());
                // On vérifie si c'est un herbivore (notre proie) et qu'il est vivant
                if (targetCell instanceof HerbivoreCell && targetCell.isActive()) {
                    preys.add(adjPos);
                }
            }
        }
        return preys;
    }

    private List<Position> getEmptyNeighbors(Grid currentGrid) {
        List<Position> empty = new ArrayList<>();

        for (Directions dir : Directions.values()) {
            int nextX = getX() + dir.dx;
            int nextY = getY() + dir.dy;

            // On demande à la map de nous donner la position ajustée selon la topologie
            Position adjPos = currentGrid.getAdjustedPosition(nextX, nextY);

            // Si la position est valide (pas hors-bornes en mode BOUNDED)
            if (adjPos != null) {
                if (currentGrid.isEmpty(adjPos.x(), adjPos.y())) {
                    empty.add(adjPos);
                }
            }
        }
        return empty;
    }
}