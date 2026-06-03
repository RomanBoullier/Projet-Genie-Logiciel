package com.example.projetglcellule.model; // Correction package proget -> projet

import com.example.projetglcellule.model.cell.Cell;

import java.util.HashSet;
import java.util.Set;

public class Organism {
    private final int id;
    private final Cell motherCell;
    private final Set<Cell> cells;

    public Organism(int id, Cell motherCell) {
        this.id = id;
        this.motherCell = motherCell;

        this.cells = new HashSet<>();
        addCell(motherCell);
    }

    public void addCell(Cell cell) {
        if (cell == null) return;
        cells.add(cell);
        cell.setOrganism(this);
    }

    public void removeCell(Cell cell) {
        if (cell == null) return;
        cells.remove(cell);
        if (cell.getOrganism() == this) {
            cell.setOrganism(null);
        }
    }

    public boolean contains(Cell cell) { return cells.contains(cell); }
    public int size() { return cells.size(); }
    public int getId() { return id; }
    public Cell getMotherCell() { return motherCell; }
    public Set<Cell> getCells() { return cells; }

    @Override
    public String toString() {
        return "Organism{" +
                "id=" + id +
                ", size=" + cells.size() +
                ", motherCell=(" + motherCell.getX() + "," + motherCell.getY() + ")" +
                "}";
    }
}