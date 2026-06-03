package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Directions;
import com.example.projetglcellule.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HerbivoreCell extends Cell {

    private static final int ENERGY_LOSS_PER_STEP = 1;
    private static final int REPRODUCTION_THRESHOLD = 25;
    private final Random random = new Random();

    public HerbivoreCell(int x, int y, int energy, int radius) {
        super(x, y, energy, radius);
    }

    @Override
    public void update(Grid currentGrid) {
        // 1. Perte d'énergie de base liée au temps qui passe
        ageOneStep();
        setEnergy(getEnergy() - ENERGY_LOSS_PER_STEP);
        if (!isActive()) return;

        // 2. Recherche de nourriture (Plantes adjacentes)
        List<Position> plantsNearby = getPlantsNearby(currentGrid);

        if (!plantsNearby.isEmpty()) {
            // Choix d'une plante au hasard et déplacement (Repas !)
            Position targetPos = plantsNearby.get(random.nextInt(plantsNearby.size()));
            Cell plant = currentGrid.getCell(targetPos.x(), targetPos.y());

            // On absorbe l'énergie de la plante
            if (plant != null) {
                setEnergy(getEnergy() + plant.getEnergy());
                plant.setActive(false); // La plante est marquée comme morte
            }

            // Déplacement physique de l'herbivore sur la grille
            currentGrid.clearCell(getX(), getY());
            move(targetPos.x(), targetPos.y());
            currentGrid.setCell(targetPos.x(), targetPos.y(), this);

        } else {
            // Pas de nourriture -> Déplacement aléatoire sur une case vide
            List<Position> emptyNeighbors = getEmptyNeighbors(currentGrid);
            if (!emptyNeighbors.isEmpty()) {
                Position targetPos = emptyNeighbors.get(random.nextInt(emptyNeighbors.size()));
                currentGrid.clearCell(getX(), getY());
                move(targetPos.x(), targetPos.y());
                currentGrid.setCell(targetPos.x(), targetPos.y(), this);
            }
        }

        // 3. Division cellulaire si l'énergie est au maximum
        if (getEnergy() >= REPRODUCTION_THRESHOLD) {
            reproduce(currentGrid);
        }
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
            int targetX = getX() + dir.dx;
            int targetY = getY() + dir.dy;

            // Calcul de la position selon la topologie (BOUNDED ou TOROIDAL)
            Position adjPos = currentGrid.getAdjustedPosition(targetX, targetY);

            // Si la case existe bien
            if (adjPos != null) {
                Cell targetCell = currentGrid.getCell(adjPos.x(), adjPos.y());
                // On vérifie si c'est une plante et qu'elle est encore en vie
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