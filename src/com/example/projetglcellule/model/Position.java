package com.example.projetglcellule.model;


/**
 * Represents one coordinate on the simulation grid.
 * The record stores the horizontal and vertical position of a cell.
 *
 * @param x the horizontal coordinate
 * @param y the vertical coordinate
 */
public record Position(int x, int y) {
}