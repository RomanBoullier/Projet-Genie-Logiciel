package com.example.projetglcellule.model;

public enum Directions {
    NORTH(0, -1),
    SOUTH(0, 1),
    EAST(-1, 0),
    WEST(1, 0);

    public final int dx;
    public final int dy;

    Directions(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }

}