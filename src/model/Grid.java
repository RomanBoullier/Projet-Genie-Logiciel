package com.example.projetglcellule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grid {

    // ---------------------------
    // CONFIG
    // ---------------------------

    private final int width;
    private final int height;
    private final int size;

    private final Topology topology;

    // ---------------------------
    // GRILLES (double buffer)
    // ---------------------------

    /**
     * Etat courant : id organisme par cellule
     */
    private int[] currentGrid;

    /**
     * Etat suivant
     */
    private int[] nextGrid;

    // ---------------------------
    // DONNEES METIER
    // ---------------------------

    private final Map<Integer, Organism> organisms;

    /**
     * Cellules frontières (coordonnées packées en int)
     */
    private final Set<Integer> activeCells;

    // ---------------------------
    // INIT
    // ---------------------------

    public Grid(int width, int height, Topology topology) {

        this.width = width;
        this.height = height;
        this.size = width * height;
        this.topology = topology;

        this.currentGrid = new int[size];
        this.nextGrid = new int[size];

        this.organisms = new HashMap<>();
        this.activeCells = new HashSet<>();
    }

    // ---------------------------
    // INDEXING
    // ---------------------------

    private int index(int x, int y) {
        return x + y * width;
    }

    private int xOf(int index) {
        return index % width;
    }

    private int yOf(int index) {
        return index / width;
    }

    // ---------------------------
    // WRAPPING
    // ---------------------------

    private int wrapX(int x) {
        if (topology == Topology.BOUNDED) return x;
        return (x + width) % width;
    }

    private int wrapY(int y) {
        if (topology == Topology.BOUNDED) return y;
        return (y + height) % height;
    }

    // ---------------------------
    // ACCESS
    // ---------------------------

    public int getCell(int x, int y) {

        if (topology == Topology.BOUNDED) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return -1;
            }
        }

        x = wrapX(x);
        y = wrapY(y);

        return currentGrid[index(x, y)];
    }

    public void setCell(int x, int y, int organismId) {
        currentGrid[index(x, y)] = organismId;
        updateBoundaryAround(x, y);
    }

    // ---------------------------
    // ORGANISMES
    // ---------------------------

    public void addOrganism(Organism organism) {

        organisms.put(organism.getId(), organism);

        Cell mother = organism.getMotherCell();

        updateBoundaryAround(mother.getX(), mother.getY());
    }

    // ---------------------------
    // FRONTIERES
    // ---------------------------

    public boolean isBoundary(int x, int y) {

        int id = getCell(x, y);

        if (id <= 0) return false;

        for (Direction d : Direction.values()) {

            int nx = wrapX(x + d.dx);
            int ny = wrapY(y + d.dy);

            int nid = getCell(nx, ny);

            if (nid != id) {
                return true;
            }
        }

        return false;
    }

    private int pack(int x, int y) {
        return x << 16 | (y & 0xFFFF);
    }

    private int unpackX(int p) {
        return p >>> 16;
    }

    private int unpackY(int p) {
        return p & 0xFFFF;
    }

    private void refreshBoundary(int x, int y) {

        int packed = pack(x, y);

        if (isBoundary(x, y)) {
            activeCells.add(packed);
        } else {
            activeCells.remove(packed);
        }
    }

    private void updateBoundaryAround(int x, int y) {

        refreshBoundary(x, y);

        for (Direction d : Direction.values()) {
            refreshBoundary(x + d.dx, y + d.dy);
        }
    }

    // ---------------------------
    // SIMULATION CORE
    // ---------------------------

    public void computeNextState() {

        // On ne recopie PAS toute la grille :
        // on travaille uniquement sur activeCells

        for (int packed : activeCells) {

            int x = unpackX(packed);
            int y = unpackY(packed);

            int id = getCell(x, y);

            if (id <= 0) continue;

            Organism org = organisms.get(id);

            if (org != null) {

                // logique biologique déléguée
                org.updateCell(this, x, y, nextGrid);
            }
        }
    }

    // ---------------------------
    // SWAP BUFFERS
    // ---------------------------

    public void swapBuffers() {

        int[] tmp = currentGrid;
        currentGrid = nextGrid;
        nextGrid = tmp;

        // reset partiel seulement (pas de réalloc obligatoire si tu veux optimiser encore)
        for (int i = 0; i < size; i++) {
            nextGrid[i] = 0;
        }
    }

    // ---------------------------
    // UTILITIES
    // ---------------------------

    public int width() { return width; }
    public int height() { return height; }

    public Map<Integer, Organism> getOrganisms() {
        return organisms;
    }
}