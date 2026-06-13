package com.example.projetglcellule.model;


/**
 * Defines how the simulation grid behaves at its borders.
 * A bounded grid stops at the edges, while a toroidal grid wraps around.
 */
public enum Topology {
    /** The simulation stops at the edge of the grid. */
    BOUNDED,

    /** The simulation wraps cells across the grid boundaries. */
    TOROIDAL
}