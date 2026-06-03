package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Directions;
import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutotrophCell extends Cell {

    private static final int ENERGY_GAIN_PER_STEP = 2;
    private static final int REPRODUCTION_ENERGY_THRESHOLD = 15;
    private static final double REPRODUCTION_PROBABILITY = 0.4; // 40% de chance
    private final Random random = new Random();

    public AutotrophCell(int x, int y, int energy, int radius) {
        super(x, y, energy, radius);
    }

    @Override
    public void update(Grid currentGrid) {
        // 1. Vieillir et consommer l'énergie de base
        ageOneStep();
        if (!isActive()) {
            return; // La cellule est morte à ce tour, on s'arrête là
        }

        // 2. Photosynthèse : gain passif d'énergie
        setEnergy(getEnergy() + ENERGY_GAIN_PER_STEP);

        // 3. Tentative de reproduction si assez d'énergie
        if (getEnergy() >= REPRODUCTION_ENERGY_THRESHOLD && random.nextDouble() < REPRODUCTION_PROBABILITY) {
            reproduce(currentGrid);
        }
    }

    private void reproduce(Grid currentGrid) {
        List<Position> emptyNeighbors = getEmptyNeighbors(currentGrid);

        if (!emptyNeighbors.isEmpty()) {
            // Choisir une case vide au hasard parmi les voisins disponibles
            Position targetPos = emptyNeighbors.get(random.nextInt(emptyNeighbors.size()));

            // Créer la nouvelle cellule végétale (moitié de l'énergie de la mère)
            int childEnergy = getEnergy() / 2;
            AutotrophCell child = new AutotrophCell(targetPos.x(), targetPos.y(), childEnergy, getRadius());

            // Placer l'enfant sur la grille
            currentGrid.setCell(targetPos.x(), targetPos.y(), child);

            // La mère perd l'énergie donnée à son enfant
            setEnergy(getEnergy() - childEnergy);
        }
    }

    // Récupère la liste des positions adjacentes (Nord, Sud, Est, Ouest) qui sont vides
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