package life.model;

public interface CellRule {
    CellType getCellType();

    boolean survives(NeighborhoodSnapshot snapshot);

    boolean isBorn(NeighborhoodSnapshot snapshot);
}
