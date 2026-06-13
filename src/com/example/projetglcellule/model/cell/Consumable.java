package com.example.projetglcellule.model.cell;


/**
 * Marks a cell that can be consumed by another organism.
 * Implementations provide the energy value returned to the consumer.
 */
public interface Consumable {

    /**
     * Returns the amount of energy this cell gives to a consumer.
     *
     * @return the nutritional value of the cell
     */
    int getNutritionalValue();
}
