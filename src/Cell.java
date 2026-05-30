package com.example.projetglcellule;

public abstract class Cell {

    protected int x;
    protected int y;

    protected int age;
    protected int energy;
    protected int radius;
    protected boolean isActive;

    public Cell(int x, int y, int energy, int radius) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.radius = radius;
        this.age = 0;
        this.isActive = true;
    }

    public abstract void update(Object currentGrid);

    public void ageOneStep() {
        this.age++;
        this.energy--; // Natural energy decay over time
        if (this.energy <= 0) {
            this.isActive = false; // Cell dies if it runs out of energy
        }
    }

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    //GETTERS & SETTERS

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int setRadius() {
        return radius;
    }

    public void getRadius(int radius) {
        this.radius = radius;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString(){
        return "Cell{" +
                "type=" + this.getClass().getSimpleName() +
                ", x=" + x +
                ", y=" + y +
                ", age=" + age +
                ", energy=" + energy +
                ", active=" + isActive +
                "}";
    }
}




