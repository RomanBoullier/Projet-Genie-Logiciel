package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;
import com.example.projetglcellule.model.Organism;

public abstract class Cell implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    protected int x;
    protected int y;
    protected int age;
    protected int energy;
    protected int radius;
    protected boolean isActive;
    protected Organism organism;
    private boolean hasPlayed = false;

    private final java.util.Random cellRandom = new java.util.Random();

    public Cell(int x, int y, int energy, int radius) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.radius = radius;
        this.age = 0;
        this.isActive = true;
        this.organism = null;
    }


    public abstract void update(Grid currentGrid);


    public void ageOneStep() {
        this.age++;

        // À partir d'un certain âge (ex: 15 tours), la cellule a une chance de mourir de vieillesse
        if (this.age > 15) {
            // La probabilité augmente avec l'âge (ex: (âge - 15) * 5%)
            double deathProbability = (this.age - 15) * 0.05;

            if (cellRandom.nextDouble() < deathProbability) {
                this.isActive = false; // Mort de vieillesse
            }
        }
    }

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    public Organism getOrganism() {
        return organism;
    }

    // GETTERS & SETTERS
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getAge() { return this.age; }
    public void setAge(int age) { this.age = age; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }
    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean hasPlayed() {
        return hasPlayed;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "type=" + this.getClass().getSimpleName() +
                ", x=" + x +
                ", y=" + y +
                ", age=" + age +
                ", energy=" + energy +
                ", active=" + isActive +
                ", organismId=" + (organism != null ? organism.getId() : "null") +
                "}";
    }
}