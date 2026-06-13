package com.example.projetglcellule.model.cell;

import com.example.projetglcellule.model.Grid;


/**
 * Marks a cell that can apply its own movement logic during a simulation step.
 */
public interface Movable {

    /**
     * Executes the movement behavior for this cell on the given grid.
     *
     * @param grid the current simulation grid
     */
    void moveWithStrategy(Grid grid);
}