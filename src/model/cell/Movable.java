package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;

/**
 * Interface defining the capability of a cell to move across the grid.
 */
public interface Movable {
    /**
     * Executes the movement and hunting/scavenging logic of the cell.
     * @param grid The simulation grid
     */
    void moveWithStrategy(Grid grid);
}