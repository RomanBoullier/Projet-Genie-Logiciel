package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Map;
import com.example.projetglcellule.model.Organism;

public abstract class Cell {
    protected int x;
    protected int y;
    protected int age;
    protected int energy;
    protected int radius;
    protected boolean isActive;
    protected Organism organism;

    public Cell(int x, int y, int energy, int radius) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.radius = radius;
        this.age = 0;
        this.isActive = true;
        this.organism = null;
    }


    public abstract void update(Map currentGrid);

    public void ageOneStep() {
        this.age++;
        this.energy--;
        if (this.energy <= 0) {
            this.isActive = false;
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