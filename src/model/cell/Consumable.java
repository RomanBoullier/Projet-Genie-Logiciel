package com.example.projetglcellule.model.cell;

/**
 * Interface defining that a cell can be eaten by another entity.
 */
public interface Consumable {
    /**
     * Returns the energy yield given to the predator/consumer.
     * @return energy value
     */
    int getNutritionalValue();
}
